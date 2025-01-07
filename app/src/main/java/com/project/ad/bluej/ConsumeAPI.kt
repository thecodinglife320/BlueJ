package com.project.ad.bluej

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.NavHostFragment
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
import com.project.ad.bluej.databinding.ActivityGameInfoBinding
import com.project.ad.bluej.databinding.ActivityMainBinding
import com.project.ad.bluej.databinding.FragmentGameDetailBinding
import com.project.ad.bluej.databinding.FragmentGamesBinding
import com.project.ad.bluej.databinding.GameCardBinding
import com.project.ad.bluej.gamedetail.GameDetail
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
   private lateinit var binding: ActivityMainBinding

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      binding = ActivityMainBinding.inflate(layoutInflater)
      setSupportActionBar(binding.searchBar)
      setContentView(binding.root)
      binding.searchView.setupWithSearchBar(binding.searchBar)

      val navHostFragment = supportFragmentManager.findFragmentById(R.id.host_nav_games) as NavHostFragment
      val navController = navHostFragment.navController
      val appBarConfiguration = AppBarConfiguration(navController.graph)
      NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
      NavigationUI.setupWithNavController(binding.searchBar, navController)
   }
}

class GameInfoActivity:AppCompatActivity(){

   private lateinit var viewModel: GameInfoViewModel

   private lateinit var binding: ActivityGameInfoBinding

   private val args: GameInfoActivityArgs by navArgs()

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      binding = DataBindingUtil.setContentView(this, R.layout.activity_game_info)
      setSupportActionBar(binding.toolbar)
      supportActionBar?.setDisplayHomeAsUpEnabled(true)
      supportActionBar?.title = ""
      binding.toolbar.setNavigationOnClickListener {
         finish()
      }
      viewModel = ViewModelProvider(this)[GameInfoViewModel::class.java]
      binding.viewmodel = viewModel
      binding.lifecycleOwner = this
      viewModel.getGameDetail(args.idGame)
   }
}

class GameViewModel : ViewModel() {

   private val service = RawgServices.service

   private val myPagingSource: MyPagingSource = MyPagingSource(service)

   val myPagingData = Pager(
      config = PagingConfig(
         pageSize = 4, // Number of items per page
         enablePlaceholders = false, // Whether to show placeholders
         initialLoadSize = 4 // Number of items to load initially
      ),
      pagingSourceFactory = { myPagingSource }
   ).flow.cachedIn(viewModelScope)
}

class GameInfoViewModel:ViewModel(){

   private val service = RawgServices.service

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

class MyPagingSource(private val apiService: RawgApi) : PagingSource<Int, Result>() {
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
      binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
      return binding.root
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      val adapter = PagingAdapter { idGame -> NavHostFragment.findNavController(this).navigate(GamesFragmentDirections.actionGamesFragmentToGameInfoActivity(idGame.toString()))
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

   private val viewModel: GameInfoViewModel by activityViewModels()

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
      _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game_detail, container, false)
      binding.lifecycleOwner = viewLifecycleOwner
      return binding.root
   }

   @SuppressLint("SetTextI18n")
   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      viewModel.gameDetail.observe(viewLifecycleOwner) {
         binding.gameDetail = it
         (requireActivity() as AppCompatActivity ).supportActionBar?.title = it.name_original
         it.released?.let { released -> binding.releaseDateTv.text = formatString(released) }

         it.playtime?.let { playtime -> binding.averagePlayTimeTv.text = requireContext().getString(R.string.average_time,playtime) }

         it.parent_platforms?.forEach {platform->
            run {
               val imageView = ImageView(requireContext()).apply {
                  val layoutParams = LinearLayout.LayoutParams(
                     LinearLayout.LayoutParams.WRAP_CONTENT,
                     LinearLayout.LayoutParams.WRAP_CONTENT
                  )
                  layoutParams.marginEnd = 20
                  setLayoutParams(layoutParams)
               }
               when(platform.platform?.id){
                  1->imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.pc))
                  2->imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ps))
                  3->imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.xbox))
                  4->imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ios))
                  8->imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.android))
                  5->imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.apple))
                  6->imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.linux))
                  7->imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.nintendo))
                  9->imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.atari))
                  10->imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.comodo))
                  11->imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.sega))
                  12->imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable._do))
                  13->imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.neo_geo_cd_seeklogo))
                  14->imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.web))
               }
               binding.icons.addView(imageView)
            }
         }

         var textView: TextView
         for (i in 0..<it.ratings?.size!!) {
            textView = (binding.ratings[i] as TextView)
            textView.text = it.ratings[i].title?.replaceFirstChar {first->
               if (first.isLowerCase()) first.titlecase(Locale.getDefault()) else it.toString()
            }
            textView.text = "${textView.text} ${it.ratings[i].count}"
         }
      }
   }

   override fun onDestroyView() {
      super.onDestroyView()
      _binding = null
   }
   private fun formatString(dateString: String): String {
      val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
      val outputFormat = SimpleDateFormat("MMM dd,yyyy", Locale.getDefault())
      val date: Date
      try {
         date = inputFormat.parse(dateString)!!
      } catch (e: Exception) {
         return "unknown date"
      }
      return outputFormat.format(date)
   }
}

