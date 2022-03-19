package com.lugares.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase
import com.lugares.model.Lugar

class LugarDao {

    private val coleccion1 = "lugaresAPP"
    private val coleccion2 = "misLugares"
    private var codigoUsuario:String
    private var firestore: FirebaseFirestore

    init {
        val usuario = Firebase.auth.currentUser?.email
        //val usuario = Firebase.auth.currentUser?.uid
        codigoUsuario = "$usuario"
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }

    //Obtener toda la lista de los documentos tipo lugar...
    fun getLugares():MutableLiveData<List<Lugar>> {
        val listaLugares = MutableLiveData<List<Lugar>>()

        //obtener los documentos
        firestore
            .collection(coleccion1)     //lugaresApp
            .document(codigoUsuario)    //el usuario (correo o uid)
            .collection(coleccion2)     //misLugares
            .addSnapshotListener{snapshot, e->
                if (e != null){  //Si se da alguna excepcion al tomar la instantanea...entra aca
                    return@addSnapshotListener
                }
                if (snapshot != null) {  //Recupero los documentos lugar de la instantanea
                    val lista = ArrayList<Lugar>()
                    val lugares = snapshot.documents //Recupera la lista de documentos
                    lugares.forEach{
                        val lugar = it.toObject(Lugar::class.java)  //Transformando un documento a un objeto Lugar
                        if (lugar != null){
                            lista.add(lugar) //Si se pudo transformar el lugar ... se pasa a la lista
                        }
                    }
                listaLugares.value = lista
                }
            }
        return listaLugares
    }

    suspend fun saveLugar(lugar: Lugar){
    val document : DocumentReference //El documento a actualizar o crear
    if (lugar.id.isEmpty()){  //Si el id del lugar no está definido ... entonces es un documento nuevo
        document = firestore.collection(coleccion1).document(codigoUsuario).collection(coleccion2).document()
            lugar.id = document.id
    }else{  //Si el lugar tiene un id... entonces es actualizar un lugar...
        document = firestore.collection(coleccion1).document(codigoUsuario).collection(coleccion2).document(lugar.id)
    }
        val set = document.set(lugar)   // Efectivamente se actualiza firebase con un nuevo documento o modifica el que existe
        set.addOnSuccessListener { Log.d("saveLugar", "Lugar agregado / modificado")}
            .addOnCanceledListener { Log.d("saveLugar", "ERROR: Lugar no agregado ni modificado") }
    }
    suspend fun deleteLugar(lugar: Lugar){
        if (lugar.id.isNotEmpty()){ //Si tiene un valor... en teoria existe en la coleccion y lo puedo borrar
            firestore
                .collection(coleccion1)
                .document(codigoUsuario)
                .collection(coleccion2)
                .document(lugar.id)
                .delete()
                .addOnSuccessListener { Log.d("deleteLugar","Lugar eliminado") }
                .addOnCanceledListener { Log.d("deleteLugar","ERROR: Lugar no eliminado") }
        }
    }
}