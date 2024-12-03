package com.example.alicorpdemo
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.alicorpdemo.database.Camara
import com.example.alicorpdemo.database.CamaraDao
import com.example.alicorpdemo.database.Piso
import kotlinx.coroutines.launch
import com.example.alicorpdemo.database.PisoDao


class CamaraViewModel(private val camaraDao: CamaraDao) : ViewModel() {

    fun obtenerCamarasPorPiso(pisoId: Int): LiveData<List<Camara>> {
        return camaraDao.obtenerCamarasPorPiso(pisoId)
    }

    fun agregarCamara(camara: Camara) {
        viewModelScope.launch {
            camaraDao.insertarCamara(camara)
        }
    }

    fun actualizarCamara(camara: Camara) {
        viewModelScope.launch {
            camaraDao.actualizarCamara(camara)
        }
    }

    fun eliminarCamara(camara: Camara) {
        viewModelScope.launch {
            camaraDao.eliminarCamara(camara)
        }
    }

    fun obtenerCamaraPorId(camaraId: Int): LiveData<Camara> {
        return camaraDao.obtenerCamaraPorId(camaraId)
    }

    fun obtenerCamaraPorCodigo(camaraCodigo: String, pisoId: Int): LiveData<Camara> {
        return camaraDao.obtenerCamaraPorCodigo(camaraCodigo, pisoId)
    }
}

class CamaraViewModelFactory(private val camaraDao: CamaraDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CamaraViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CamaraViewModel(camaraDao) as T
        }
        throw IllegalArgumentException("Clase desconocida para ViewModel")
    }
}
