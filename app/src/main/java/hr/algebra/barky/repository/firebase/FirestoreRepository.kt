@file:Suppress("UNCHECKED_CAST")

package hr.algebra.barky.repository.firebase

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirestoreRepository : IFirestoreRepo {

    val db by lazy {
        Firebase.firestore
    }

    override fun <T>getAllValuesFromCollection(collectionName : String, clazz: Class<*>, callback:(HashMap<String, T>)->Unit) {
        val collectionRef = db.collection(collectionName)
        collectionRef.get()
            .addOnSuccessListener { documents ->
                val result= HashMap<String, T>()
                for (document in documents) {
                    val initialized : T = document.toObject(clazz) as T
                    Log.d("GotData of ${clazz.simpleName}", "Data: $initialized")
                    result[document.id]=initialized
                }
                callback(result)
            }
            .addOnFailureListener { exception ->
                Log.w("Failed to get ${clazz.simpleName}", "Error getting data", exception)
                callback(hashMapOf())
            }
    }

    fun <T>getAllOfClass(collectionName : String, fieldName: String, value : Any, clazz: Class<*>, callback:(HashMap<String, T>)->Unit) {
        val collectionRef = db.collection(collectionName)
        collectionRef.whereEqualTo(fieldName, value)
            .get()
            .addOnSuccessListener { documents ->
                val result= HashMap<String, T>()
                for (document in documents) {
                    val initialized : T = initializeData(clazz,document)
                    //val initialized : T = document.toObject(clazz) as T
                    Log.d("GotData of ${clazz.name}", "Data: $initialized")
                    result[document.id]=initialized
                }
                callback(result)
            }
            .addOnFailureListener { exception ->
                Log.w("Failed to get ${clazz.name}", "Error getting data by $fieldName", exception)
                callback(hashMapOf())
            }
    }

    fun <T>getClassFromCollection(collectionName : String, fieldName: String, value : Any, clazz: Class<*>, callback:(HashMap<String, T>)->Unit) {
        val collectionRef = db.collection(collectionName)
        collectionRef.whereEqualTo(fieldName, value)
            .get()
            .addOnSuccessListener { documents ->
                val result= HashMap<String, T>()
                for (document in documents) {
                    //val initialized : T = document.toObject(clazz) as T
                    val initialized : T = initializeData(clazz,document)
                    Log.d("GotData of ${clazz.name}", "Data: $initialized")
                    result[document.id]=initialized
                }
                callback(result)
            }
            .addOnFailureListener { exception ->
                Log.w("Failed to get ${clazz.name}", "Error getting data by $fieldName", exception)
                callback(hashMapOf())
            }
    }

    override fun addClassToCollection(collectionName : String, value : Any, onSuccess:(String)->Unit){
        val variableMap = getVariableMap(value)
        Log.w("Variable name:", "$variableMap")
        // Add a new document with a generated ID
        db.collection(collectionName)
            .add(variableMap)
            .addOnSuccessListener { documentReference ->
                Log.d("DocumentSnapshot", "added with ID: ${documentReference.id}")
                onSuccess(documentReference.id)
            }
            .addOnFailureListener { e ->
                Log.w("Error", "adding document", e)
            }
    }

    override fun update(collectionName : String, fieldName: String, searchParam : Any, updatedValue: Any, callback:(Boolean)->Unit) {
        val collectionRef = db.collection(collectionName)
        collectionRef.whereEqualTo(fieldName, searchParam)
            .get()
            .addOnSuccessListener { documents ->
                val variableMap = getVariableMap(updatedValue)
                for (document in documents) {
                    db.collection(collectionName).document(document.id).update(variableMap)
                }
                callback(true)
            }
            .addOnFailureListener { exception ->
                Log.w("Failed to update", "Error getting data by $fieldName", exception)
                callback(false)
            }
    }

    override fun delete(collectionName : String, fieldName: String, searchParam : Any, callback:(Boolean)->Unit) {
        val collectionRef = db.collection(collectionName)
        collectionRef.whereEqualTo(fieldName, searchParam)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    db.collection(collectionName).document(document.id).delete()
                }
                callback(true)
            }
            .addOnFailureListener { exception ->
                Log.w("Failed to delete", "Error getting data by $fieldName", exception)
                callback(false)
            }
    }

    fun checkIfValueExists(collectionName : String, fieldName: String, value : Any, callback:(Boolean)->Unit={}){
        db.collection(collectionName)
            .whereEqualTo(fieldName, value)
            .get()
            .addOnSuccessListener { documentReference ->
                if (documentReference.isEmpty){
                    callback(false)
                }
                else{
                    callback(true)
                }
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun getCountOfDocuments(collectionName: String,callback: (Int) -> Unit){
        val collectionReference= db.collection(collectionName)
        getDocumentCount(collectionReference).addOnSuccessListener { count ->
            Log.d("GetCount", "Document count: $count")
            callback(count)
        }.addOnFailureListener { exception ->
            Log.e("GetCount", "Error getting document count", exception)
            callback(0)
        }
    }

    fun getCountOfDocuments(collectionName: String,fieldName: String,value:Any,callback: (Int) -> Unit){
        val collectionRef = db.collection(collectionName)
        val query = collectionRef.whereEqualTo(fieldName, value)

        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val documents = task.result?.documents
                val count = documents?.size ?: 0
                callback(count)
            } else {
                // Handle the error
            }
        }
    }

    fun <T>initializeData(clazz: Class<*>, document: DocumentSnapshot): T {
        /*
        IMPORTANT NOTES:
            if working with data classes you must create a zero argument constructor
        * */
        try {
            //val constructor = clazz.getDeclaredConstructor()
            val instance = clazz.newInstance()

            clazz.declaredFields.forEach { field ->
                field.isAccessible = true
                val value = document.get(field.name)

                if(value!=null){
                    when (field.type) {
                        String::class.java -> field.set(instance, document.getString(field.name))
                        Boolean::class.java -> field.set(instance, document.getBoolean(field.name))
                        Int::class.java -> field.set(instance, document.getLong(field.name)!!.toInt())
                        Double::class.java -> field.set(instance, document.getLong(field.name)!!.toDouble())
                        Long::class.java -> field.set(instance, document.getLong(field.name))
                    }
                }
            }

            return instance as T
        } catch (e: Exception) {
            Log.e("Error",e.toString())
            e.printStackTrace()
        }
        return Any() as T
    }

    fun getVariableMap(obj: Any): HashMap<String, Any?> {
        val result = HashMap<String, Any?>()
        val fields = obj.javaClass.declaredFields
        for (field in fields) {
            field.isAccessible = true
            if (!field.name.contains("$") && !field.name.equals("Companion"))
                result[field.name] = field.get(obj)
        }
        return result
    }

    fun getDocumentCount(collectionReference: CollectionReference): Task<Int> {
        return collectionReference.get().continueWith { task ->
            if (!task.isSuccessful) {
                throw task.exception!!
            }
            task.result!!.documents.size
        }
    }

}