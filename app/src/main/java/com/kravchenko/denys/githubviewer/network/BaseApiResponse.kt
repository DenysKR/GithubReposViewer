package com.kravchenko.denys.githubviewer.network

import com.kravchenko.denys.githubviewer.data.github.GithubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

abstract class BaseUseCase(protected val repository: GithubRepository) {
    suspend fun <T> safeApiCall(apiCall: suspend () -> T) = flow {
        emit(NetworkResult.Loading())
        try {
            val result = apiCall()
            emit(NetworkResult.Success(result))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error("Api call failed ${e.message ?: "Something went wrong"}"))
        }
    }.flowOn(Dispatchers.IO)
}