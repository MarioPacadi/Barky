package hr.algebra.barky.state

import hr.algebra.barky.model.Point

data class MapState(
    val points: List<Point> = emptyList(),
    val loading: Boolean = true,

)
