package com.project.ad.bluej

import com.project.ad.bluej.gamedetail.GameDetail
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RawgApiService {
   @GET("games?")
   suspend fun getGames(
      @Query(KEY) key: String = APIKEY,
      @Query(DATES) dates: String,
      @Query(ORDERING) ordering: String,
      @Query(PAGESIZE) pageSize:Int,
      @Query(PAGE) page: Int
   ): GamesResponse

   companion object{
      private const val APIKEY = "cd06a40993784b06a46713bcafb92fa5"
      const val BASE_URL = "https://api.rawg.io/api/"
      private const val KEY = "key"
      private const val DATES = "dates"
      private const val ORDERING = "ordering"
      private const val PAGESIZE = "page_size"
      private const val PAGE = "page"
   }

   @GET("games/{id}")
   suspend fun getGameDetails(
      @Path("id") gameId: String,
      @Query("key") key: String = APIKEY
   ): GameDetail
}
