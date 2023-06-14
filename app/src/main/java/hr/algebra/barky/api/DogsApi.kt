package hr.algebra.barky.api

import hr.algebra.barky.BuildConfig
import hr.algebra.barky.model.PageResult
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface DogsApi {
    @GET("/paginated/")
    suspend fun getDogs(
        @Header("X-RapidAPI-Key") apiKey: String = BuildConfig.DOGS_KEY,
        @Header("X-RapidAPI-Host") host: String = BuildConfig.DOGS_HOST,
        @Query("page") page: Int=1,
        @Query("search") search : String=""
    ) : PageResult
}