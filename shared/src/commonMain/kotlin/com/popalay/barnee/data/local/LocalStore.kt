package com.popalay.barnee.data.local

import kotlinx.coroutines.flow.Flow

expect object LocalStore {
    fun getFavoriteDrinks(): Flow<Set<String>>
    suspend fun saveFavorite(alias: String)
    suspend fun removeFavorite(alias: String)
}
