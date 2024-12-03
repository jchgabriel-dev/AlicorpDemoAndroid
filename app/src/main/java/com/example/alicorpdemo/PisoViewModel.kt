import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.alicorpdemo.database.Camara
import com.example.alicorpdemo.database.Piso
import kotlinx.coroutines.launch
import com.example.alicorpdemo.database.PisoDao

class PisoViewModel(private val pisoDao: PisoDao) : ViewModel() {
    val pisos: LiveData<List<Piso>> = pisoDao.obtenerTodosLosPisos()

    fun agregarPiso(nombre: String, descripcion: String, imagen: String) {
        viewModelScope.launch {
            pisoDao.insertarPiso(Piso(nombre = nombre, descripcion = descripcion, imagen = imagen))
        }
    }

    fun actualizarPiso(piso: Piso) {
        viewModelScope.launch {
            pisoDao.actualizarPiso(piso)
        }
    }

    fun eliminarPiso(piso: Piso) {
        viewModelScope.launch {
            pisoDao.eliminarPiso(piso)
        }
    }

    fun obtenerPisoPorId(pisoId: Int): LiveData<Piso> {
        return pisoDao.obtenerPisoPorId(pisoId)
    }
}

class PisoViewModelFactory(private val pisoDao: PisoDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PisoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PisoViewModel(pisoDao) as T
        }
        throw IllegalArgumentException("Clase desconocida para ViewModel")
    }
}
