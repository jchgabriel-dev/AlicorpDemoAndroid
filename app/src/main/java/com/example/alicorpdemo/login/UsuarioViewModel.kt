package com.example.alicorpdemo.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.alicorpdemo.database.Camara
import com.example.alicorpdemo.database.CamaraDao
import com.example.alicorpdemo.database.Usuario
import com.example.alicorpdemo.database.UsuarioDao
import kotlinx.coroutines.launch

class UsuarioViewModel(private val usuarioDao: UsuarioDao) : ViewModel() {

    fun registrarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            usuarioDao.registrarUsuario(usuario)
        }
    }

    fun login(username: String, contraseña: String): LiveData<Usuario> {
        return usuarioDao.login(username, contraseña)
    }

    fun obtenerUsuarioPorUsername(username: String): LiveData<Usuario> {
        return usuarioDao.obtenerUsuarioPorUsername(username)
    }

}



class UsuarioViewModelFactory(private val UsuarioDao: UsuarioDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsuarioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UsuarioViewModel(UsuarioDao) as T
        }
        throw IllegalArgumentException("Clase desconocida para ViewModel")
    }
}
