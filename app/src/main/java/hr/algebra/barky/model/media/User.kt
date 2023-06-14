package hr.algebra.barky.model.media

import java.util.*

data class User(
    val userId : String="",
    val mail: String="",
    val isAdmin:Boolean=false,
    val packageID : String="",
    val consumption : Long=0,
    val package_change:Boolean=false,
    var consumption_timer:Long=0,
    var package_changed_timer:Long=0,
){
    fun setConsumptionTime() {
        consumption_timer= Calendar.getInstance().timeInMillis
    }

    fun setPackageChangeTime() {
        package_changed_timer=Calendar.getInstance().timeInMillis
    }
}
