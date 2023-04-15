package com.kravchenko.denys.githubviewer.data.github

import com.kravchenko.denys.githubviewer.network.BaseApiResponse
import com.kravchenko.denys.githubviewer.network.GithubAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GithubRepository(private val api: GithubAPI) : BaseApiResponse() {
    fun getUserRepos(userName: String) =
        flow {
            val apiCall = safeApiCall { api.getUserRepos(userName) }
            emit(apiCall)
        }.flowOn(Dispatchers.IO)
}

