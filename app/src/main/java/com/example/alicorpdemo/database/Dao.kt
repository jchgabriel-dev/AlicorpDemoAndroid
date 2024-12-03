package com.example.alicorpdemo.database
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PisoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPiso(piso: Piso)

    @Update
    suspend fun actualizarPiso(piso: Piso)

    @Delete
    suspend fun eliminarPiso(piso: Piso)

    @Query("SELECT * FROM Piso")
    fun obtenerTodosLosPisos(): LiveData<List<Piso>>

    @Query("SELECT * FROM Piso WHERE id = :pisoId LIMIT 1")
    fun obtenerPisoPorId(pisoId: Int): LiveData<Piso>

}


@Dao
interface CamaraDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCamara(camara: Camara)

    @Update
    suspend fun actualizarCamara(camara: Camara)

    @Delete
    suspend fun eliminarCamara(camara: Camara)

    @Query("SELECT * FROM Camara WHERE pisoId = :pisoId")
    fun obtenerCamarasPorPiso(pisoId: Int): LiveData<List<Camara>>

    @Query("SELECT * FROM Camara WHERE id = :camaraId LIMIT 1")
    fun obtenerCamaraPorId(camaraId: Int): LiveData<Camara>

    @Query("SELECT * FROM Camara WHERE codigo = :camaraCodigo AND pisoId = :pisoId LIMIT 1")
    fun obtenerCamaraPorCodigo(camaraCodigo: String, pisoId: Int): LiveData<Camara>

}


@Dao
interface InformeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarInforme(informe: Informe)

    @Update
    suspend fun actualizarInforme(informe: Informe)

    @Delete
    suspend fun eliminarInforme(informe: Informe)

    @Query("SELECT * FROM Informe WHERE camaraId = :camaraId")
    fun obtenerInformesPorCamara(camaraId: Int): LiveData<List<Informe>>

    @Query("SELECT * FROM Informe WHERE id = :informeId")
    fun obtenerInformePorId(informeId: Int): LiveData<Informe>
}