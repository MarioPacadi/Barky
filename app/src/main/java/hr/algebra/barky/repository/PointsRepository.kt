package hr.algebra.barky.repository

import hr.algebra.barky.api.PointsApi
import hr.algebra.barky.model.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PointsRepository @Inject constructor(
    private val pointsApi: PointsApi
) {
    suspend fun getPoints() : List<Point> {
        return withContext(Dispatchers.IO){
            pointsApi.getPoints().toList()
        }
    }
}