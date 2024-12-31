package com.project.ad.bluej

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingDataAdapter
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.project.ad.bluej.RawgApiService.Companion.BASE_URL
import com.project.ad.bluej.databinding.ActivityGameDetailBinding
import com.project.ad.bluej.databinding.ActivityMainBinding
import com.project.ad.bluej.databinding.FragmentGameDetailBinding
import com.project.ad.bluej.databinding.FragmentGamesBinding
import com.project.ad.bluej.databinding.GameCardBinding
import com.project.ad.bluej.gamedetail.GameDetail
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException

class MainActivity : AppCompatActivity() {
   private lateinit var binding: ActivityMainBinding

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      binding = ActivityMainBinding.inflate(layoutInflater)
      setSupportActionBar(binding.searchBar)
      setContentView(binding.root)
      binding.searchView.setupWithSearchBar(binding.searchBar)

      val navHostFragment =
         supportFragmentManager.findFragmentById(R.id.host_nav_games) as NavHostFragment
      val navController = navHostFragment.navController
      val appBarConfiguration = AppBarConfiguration(navController.graph)
      NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
      NavigationUI.setupWithNavController(binding.searchBar, navController)
   }
}

class GameDetailActivity:AppCompatActivity(){

   private lateinit var binding:ActivityGameDetailBinding

   private val viewModel: GameViewModel = GameViewModel()

   override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
      super.onCreate(savedInstanceState, persistentState)
      println("a")
      binding=DataBindingUtil.setContentView(this,R.layout.activity_game_detail)
      setSupportActionBar(binding.toolbar)
      supportActionBar?.setDisplayHomeAsUpEnabled(true)
      viewModel.getGameDetail("41494")
      binding.viewmodel=viewModel
      binding.lifecycleOwner = this
   }
}

class GameViewModel : ViewModel() {

   private val service: RawgApiService

   private val myPagingSource: MyPagingSource

   private val moshi = Moshi.Builder()
      .add(KotlinJsonAdapterFactory())
      .build()

   private val retrofit = Retrofit.Builder()
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .baseUrl(BASE_URL)
      .build()

   init {
      service = retrofit.create(RawgApiService::class.java)
      myPagingSource = MyPagingSource(service)
   }

   val myPagingData = Pager(
      config = PagingConfig(
         pageSize = 4, // Number of items per page
         enablePlaceholders = false, // Whether to show placeholders
         initialLoadSize = 4 // Number of items to load initially
      ),
      pagingSourceFactory = { myPagingSource }
   ).flow.cachedIn(viewModelScope)

   val gameDetail = MutableLiveData<GameDetail>()

   fun getGameDetail(idGame: String) {
      viewModelScope.launch {
         gameDetail.value = service.getGameDetails(gameId = idGame)
      }
   }
}

class PagingAdapter(private val goToDetail: (idGame: Int) -> Unit) :
   PagingDataAdapter<Result, PagingAdapter.ViewHolder>(DiffCallback()) {

   class ViewHolder(private val binding: GameCardBinding) : RecyclerView.ViewHolder(binding.root) {
      fun bind(result: Result, goToDetail: (idGame: Int) -> Unit) {
         binding.game = result
         Glide.with(binding.root)
            .load(result.background_image)
            .apply(
               RequestOptions()
                  .placeholder(R.drawable.game_placeholder) // Optional: Placeholder image
                  .error(R.drawable.game_error) // Optional: Error image
                  .diskCacheStrategy(DiskCacheStrategy.ALL) // Optional: Caching strategy
            )
            .transition(DrawableTransitionOptions.withCrossFade()) // Optional: Crossfade animation
            .into(binding.gameImg)
         binding.root.setOnClickListener { goToDetail(result.id!!) }
      }
   }

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val postCardBinding =
         GameCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
      return ViewHolder(postCardBinding)
   }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) =
      holder.bind(getItem(position)!!, goToDetail)

   class DiffCallback : DiffUtil.ItemCallback<Result>() {
      override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
         return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
         return oldItem == newItem
      }
   }
}

class MyPagingSource(private val apiService: RawgApiService) : PagingSource<Int, Result>() {
   override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Result> {
      val page = params.key ?: 1 // Start with page 1 if null
      val perPage = params.loadSize // Use the loadSize from params

      return try {
         val response = apiService
            .getGames(
               dates = "2020-01-01,2020-12-31",
               ordering = "-added",
               pageSize = perPage,
               page = page
            )
         val items = response.results
         val prevKey = if (page == 1) null else page - 1
         val nextKey = if (items?.isEmpty() == true) null else page + 1

         LoadResult.Page(
            data = items!!,
            prevKey = prevKey,
            nextKey = nextKey
         )
      } catch (e: IOException) {
         LoadResult.Error(e)
      } catch (e: HttpException) {
         LoadResult.Error(e)
      }
   }

   override fun getRefreshKey(state: PagingState<Int, Result>): Int? {
      return state.anchorPosition?.let { anchorPosition ->
         val anchorPage = state.closestPageToPosition(anchorPosition)
         anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
      }
   }
}

class GamesFragment : Fragment() {

   private var _binding: FragmentGamesBinding? = null

   private val binding get() = _binding!!

   private val viewModel: GameViewModel by activityViewModels()

   override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_games, container, false)
      binding.viewmodel = viewModel
      binding.lifecycleOwner = viewLifecycleOwner
      binding.recyclerView.setHasFixedSize(true)
      binding.recyclerView.layoutManager =
         StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
      return binding.root
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      val adapter = PagingAdapter { idGame ->
         run {
            startActivity(Intent(requireContext(), GameDetailActivity::class.java))
            println(idGame)
         }
      }
      binding.recyclerView.adapter = adapter
      lifecycleScope.launch {
         viewModel.myPagingData.collectLatest { pagingData ->
            adapter.submitData(pagingData)
         }
      }
   }

   override fun onDestroyView() {
      super.onDestroyView()
      _binding = null
   }
}

class GameDetailFragment : Fragment() {
   private var _binding: FragmentGameDetailBinding? = null

   private val binding get() = _binding!!

   private val viewModel: GameViewModel by activityViewModels()

   override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game_detail, container, false)
      binding.lifecycleOwner = viewLifecycleOwner
      return binding.root
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      viewModel.gameDetail.observe(viewLifecycleOwner) {
         //binding.gameDetail = it
         println(it)
      }
   }

   override fun onDestroyView() {
      super.onDestroyView()
      _binding = null
   }
}

