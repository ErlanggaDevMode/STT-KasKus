package com.koperasiku.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    val id: String,
    val nama: String,
    @SerialName("no_hp") val noHp: String?,
    @SerialName("foto_url") val fotoUrl: String?,
    val role: String
)
