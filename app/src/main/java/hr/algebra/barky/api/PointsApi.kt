package hr.algebra.barky.api

import hr.algebra.barky.model.Point
import retrofit2.http.GET
import retrofit2.http.Query

interface PointsApi {
    @GET("Points.json")
    suspend fun getPoints(
        @Query("alt") alt: String ="media",
        @Query("token") token: String ="50bc2c6c-61e8-4db4-8552-b2d95f8794fa"
    ) : List<Point>
}