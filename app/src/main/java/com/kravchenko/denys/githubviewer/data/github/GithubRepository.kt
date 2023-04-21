package com.kravchenko.denys.githubviewer.data.github

import com.kravchenko.denys.githubviewer.network.GithubAPI
import com.kravchenko.denys.githubviewer.network.SessionHolder

class GithubRepository(private val api: GithubAPI, private val sessionManager: SessionHolder) {
    fun saveAuthToken(token: String) {
        sessionManager.githubUserToken = token
    }

    suspend fun fetchUserRepos(userName: String) = api.getUserRepos(userName)
    suspend fun fetchRepoStargazers(ownerName: String, repoName: String) =
        api.fetchStargazers(ownerName, repoName)

    suspend fun fetchAuthenticatedUserInfo() = api.getUser()
    suspend fun starRepo(ownerName: String, repoName: String) = api.starRepo(ownerName, repoName)
    suspend fun unStarRepo(ownerName: String, repoName: String) =
        api.unStarRepo(ownerName, repoName)
}

