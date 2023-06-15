package hr.algebra.barky.repository.firebase

interface IFirestoreRepo {
    fun <T>getAllValuesFromCollection(collectionName : String, clazz: Class<*>, callback:(HashMap<String, T>)->Unit)
    fun addClassToCollection(collectionName : String, value : Any,onSuccess:(String)->Unit={})
    fun update(collectionName : String, fieldName: String, searchParam : Any, updatedValue: Any, callback:(Boolean)->Unit={})
    fun delete(collectionName : String, fieldName: String, searchParam : Any, callback:(Boolean)->Unit={})
}
