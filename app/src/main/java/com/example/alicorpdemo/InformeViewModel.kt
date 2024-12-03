package com.example.alicorpdemo

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.alicorpdemo.database.Camara
import com.example.alicorpdemo.database.CamaraDao
import com.example.alicorpdemo.database.Informe
import com.example.alicorpdemo.database.InformeDao
import kotlinx.coroutines.launch

class InformeViewModel(private val informeDao: InformeDao) : ViewModel() {

    fun obtenerInformesPorCamara(pisoId: Int): LiveData<List<Informe>> {
        return informeDao.obtenerInformesPorCamara(pisoId)
    }

    fun agregarInforme(informe: Informe) {
        viewModelScope.launch {
            informeDao.insertarInforme(informe)
        }
    }

    fun actualizarInforme(informe: Informe) {
        viewModelScope.launch {
            informeDao.actualizarInforme(informe)
        }
    }

    fun eliminarInforme(informe: Informe) {
        viewModelScope.launch {
            informeDao.eliminarInforme(informe)
        }
    }

    fun obtenerInformePorId(informeId: Int): LiveData<Informe> {
        return informeDao.obtenerInformePorId(informeId)
    }
}

class InformeViewModelFactory(private val informeDao: InformeDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InformeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InformeViewModel(informeDao) as T
        }
        throw IllegalArgumentException("Clase desconocida para ViewModel")
    }
}
