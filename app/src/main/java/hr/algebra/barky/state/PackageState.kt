package hr.algebra.barky.state

import hr.algebra.barky.model.media.Package

data class PackageState(
    val packages: List<Package> = emptyList(),
    var selected: Package = Package()
)