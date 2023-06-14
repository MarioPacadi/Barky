package hr.algebra.barky.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class PageResult(
    @SerialName("results")
    val dogs: List<Dog>
)
