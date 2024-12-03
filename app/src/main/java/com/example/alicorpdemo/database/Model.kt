package com.example.alicorpdemo.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity

data class Piso(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val imagen: String
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = Piso::class,
        parentColumns = ["id"],
        childColumns = ["pisoId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Camara(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val pisoId: Int,
    val latitud: Float,
    val longitud: Float,
    val codigo: String,
    val descripcion: String

)


@Entity(
    foreignKeys = [ForeignKey(
        entity = Camara::class,
        parentColumns = ["id"],
        childColumns = ["camaraId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Informe(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val descripcion: String,
    val fecha: String,
    val observacion: String,
    val autor: String,
    val camaraId: Int
)