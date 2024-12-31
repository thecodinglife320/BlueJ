@file:Suppress("PropertyName")

package com.project.ad.bluej.gamedetail

data class GameDetail(
    val achievements_count: Int? = 0,
    val added: Int? = 0,
    val added_by_status: AddedByStatus? = AddedByStatus(),
    val additions_count: Int? = 0,
    val alternative_names: List<Any?>? = listOf(),
    val background_image: String? = "",
    val background_image_additional: String? = "",
    val clip: Any? = Any(),
    val creators_count: Int? = 0,
    val description: String? = "",
    val description_raw: String? = "",
    val developers: List<Developer>? = listOf(),
    val dominant_color: String? = "",
    val esrb_rating: EsrbRating? = EsrbRating(),
    val game_series_count: Int? = 0,
    val genres: List<Genre>? = listOf(),
    val id: Int? = 0,
    val metacritic: Int? = 0,
    val metacritic_platforms: List<MetacriticPlatform>? = listOf(),
    val metacritic_url: String? = "",
    val movies_count: Int? = 0,
    val name: String? = "",
    val name_original: String? = "",
    val parent_achievements_count: Int? = 0,
    val parent_platforms: List<ParentPlatform>? = listOf(),
    val parents_count: Int? = 0,
    val platforms: List<PlatformXX>? = listOf(),
    val playtime: Int? = 0,
    val publishers: List<Publisher>? = listOf(),
    val rating: Double? = 0.0,
    val rating_top: Int? = 0,
    val ratings: List<Rating>? = listOf(),
    val ratings_count: Int? = 0,
    val reactions: Reactions? = Reactions(),
    val reddit_count: Int? = 0,
    val reddit_description: String? = "",
    val reddit_logo: String? = "",
    val reddit_name: String? = "",
    val reddit_url: String? = "",
    val released: String? = "",
    val reviews_count: Int? = 0,
    val reviews_text_count: Int? = 0,
    val saturated_color: String? = "",
    val screenshots_count: Int? = 0,
    val slug: String? = "",
    val stores: List<Store>? = listOf(),
    val suggestions_count: Int? = 0,
    val tags: List<Tag>? = listOf(),
    val tba: Boolean? = false,
    val twitch_count: Int? = 0,
    val updated: String? = "",
    val user_game: Any? = Any(),
    val website: String? = "",
    val youtube_count: Int? = 0
)

data class AddedByStatus(
    val beaten: Int? = 0,
    val dropped: Int? = 0,
    val owned: Int? = 0,
    val playing: Int? = 0,
    val toplay: Int? = 0,
    val yet: Int? = 0
)

data class Developer(
    val games_count: Int? = 0,
    val id: Int? = 0,
    val image_background: String? = "",
    val name: String? = "",
    val slug: String? = ""
)

data class EsrbRating(
    val id: Int? = 0,
    val name: String? = "",
    val slug: String? = ""
)

data class Genre(
    val games_count: Int? = 0,
    val id: Int? = 0,
    val image_background: String? = "",
    val name: String? = "",
    val slug: String? = ""
)

data class MetacriticPlatform(
    val metascore: Int? = 0,
    val platform: Platform? = Platform(),
    val url: String? = ""
)

data class ParentPlatform(
    val platform: PlatformX? = PlatformX()
)

data class PlatformXX(
    val platform: PlatformXXX? = PlatformXXX(),
    val released_at: String? = "",
    val requirements: Requirements? = Requirements()
)

data class Publisher(
    val games_count: Int? = 0,
    val id: Int? = 0,
    val image_background: String? = "",
    val name: String? = "",
    val slug: String? = ""
)

data class Rating(
    val count: Int? = 0,
    val id: Int? = 0,
    val percent: Double? = 0.0,
    val title: String? = ""
)

data class Reactions(
    val `1`: Int? = 0,
    val `10`: Int? = 0,
    val `11`: Int? = 0,
    val `12`: Int? = 0,
    val `13`: Int? = 0,
    val `14`: Int? = 0,
    val `15`: Int? = 0,
    val `16`: Int? = 0,
    val `17`: Int? = 0,
    val `18`: Int? = 0,
    val `19`: Int? = 0,
    val `2`: Int? = 0,
    val `20`: Int? = 0,
    val `21`: Int? = 0,
    val `3`: Int? = 0,
    val `4`: Int? = 0,
    val `5`: Int? = 0,
    val `6`: Int? = 0,
    val `7`: Int? = 0,
    val `8`: Int? = 0,
    val `9`: Int? = 0
)

data class Store(
    val id: Int? = 0,
    val store: StoreX? = StoreX(),
    val url: String? = ""
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
    val name: String? = "",
    val platform: Int? = 0,
    val slug: String? = ""
)

data class PlatformX(
    val id: Int? = 0,
    val name: String? = "",
    val slug: String? = ""
)

data class PlatformXXX(
    val games_count: Int? = 0,
    val id: Int? = 0,
    val image: Any? = Any(),
    val image_background: String? = "",
    val name: String? = "",
    val slug: String? = "",
    val year_end: Any? = Any(),
    val year_start: Int? = 0
)

data class Requirements(
    val minimum: String? = "",
    val recommended: String? = ""
)

data class StoreX(
    val domain: String? = "",
    val games_count: Int? = 0,
    val id: Int? = 0,
    val image_background: String? = "",
    val name: String? = "",
    val slug: String? = ""
)