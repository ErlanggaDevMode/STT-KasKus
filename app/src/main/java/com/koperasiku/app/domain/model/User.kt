package com.koperasiku.app.domain.model

import com.koperasiku.app.domain.model.enums.UserRole

data class User(
    val id: String,
    val nama: String,
    val noHp: String?,
    val fotoUrl: String?,
    val role: UserRole
)
