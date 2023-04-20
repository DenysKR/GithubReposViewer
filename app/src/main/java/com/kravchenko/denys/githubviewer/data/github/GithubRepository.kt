package com.kravchenko.denys.githubviewer.data.github

import com.kravchenko.denys.githubviewer.network.GithubAPI
import com.kravchenko.denys.githubviewer.network.SessionHolder

class GithubRepository(private val api: GithubAPI, private val sessionManager: SessionHolder){

    fun saveAuthToken(token: String) {
        sessionManager.githubUserToken = token
    }

    suspend fun fetchUserRepos(userName: String) = api.getUserRepos(userName)

    suspend fun fetchAuthenticatedUserInfo() = api.getUser()
}

