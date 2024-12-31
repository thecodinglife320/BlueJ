@file:Suppress("PropertyName")

package com.project.ad.bluej

import com.project.ad.bluej.gamedetail.AddedByStatus
import com.project.ad.bluej.gamedetail.EsrbRating
import com.project.ad.bluej.gamedetail.Genre
import com.project.ad.bluej.gamedetail.ParentPlatform
import com.project.ad.bluej.gamedetail.Platform
import com.project.ad.bluej.gamedetail.PlatformX
import com.project.ad.bluej.gamedetail.Rating
import com.project.ad.bluej.gamedetail.Store
import com.project.ad.bluej.gamedetail.StoreX
import com.project.ad.bluej.gamedetail.Tag

data class GamesResponse(
    val count: Int? = 0,
    val next: String? = "",
    val previous: Any? = Any(),
    val results: List<Result>? = listOf(),
    val user_platforms: Boolean? = false
)

data class Result(
    val added: Int? = 0,
    val added_by_status: AddedByStatus? = AddedByStatus(),
    val background_image: String? = "",
    val clip: Any? = Any(),
    val dominant_color: String? = "",
    val esrb_rating: EsrbRating? = EsrbRating(),
    val genres: List<Genre>? = listOf(),
    val id: Int? = 0,
    val metacritic: Int? = 0,
    val name: String? = "",
    val parent_platforms: List<ParentPlatform>? = listOf(),
    val platforms: List<PlatformX>? = listOf(),
    val playtime: Int? = 0,
    val rating: Double? = 0.0,
    val rating_top: Int? = 0,
    val ratings: List<Rating>? = listOf(),
    val ratings_count: Int? = 0,
    val released: String? = "",
    val reviews_count: Int? = 0,
    val reviews_text_count: Int? = 0,
    val saturated_color: String? = "",
    val score: Any? = Any(),
    val short_screenshots: List<ShortScreenshot>? = listOf(),
    val slug: String? = "",
    val stores: List<Store>? = listOf(),
    val suggestions_count: Int? = 0,
    val tags: List<Tag>? = listOf(),
    val tba: Boolean? = false,
    val updated: String? = "",
    val user_game: Any? = Any()
)

data class AddedByStatus(
    val beaten: Int? = 0,
    val dropped: Int? = 0,
    val owned: Int? = 0,
    val playing: Int? = 0,
    val toplay: Int? = 0,
    val yet: Int? = 0
)

data class EsrbRating(
    val id: Int? = 0,
    val name: String? = "",
    val name_en: String? = "",
    val name_ru: String? = "",
    val slug: String? = ""
)

data class Genre(
    val id: Int? = 0,
    val name: String? = "",
    val slug: String? = ""
)

data class ParentPlatform(
    val platform: Platform? = Platform()
)

data class PlatformX(
    val platform: Platform? = Platform()
)

data class Rating(
    val count: Int? = 0,
    val id: Int? = 0,
    val percent: Double? = 0.0,
    val title: String? = ""
)

data class ShortScreenshot(
    val id: Int? = 0,
    val image: String? = ""
)

data class Store(
    val store: StoreX? = StoreX()
)

data class Tag(
    val games_count: Int? = 0,
    val id: Int? = 0,
    val image_background: String? = "",
    val language: String? = "",
    val name: String? = "",
    val slug: String? = ""
)

data class Platform(
    val id: Int? = 0,
    val name: String? = "",
    val slug: String? = ""
)

data class StoreX(
    val id: Int? = 0,
    val name: String? = "",
    val slug: String? = ""
)