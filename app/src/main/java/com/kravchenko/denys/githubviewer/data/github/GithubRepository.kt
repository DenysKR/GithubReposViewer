package com.kravchenko.denys.githubviewer.data.github

import com.kravchenko.denys.githubviewer.network.GithubAPI
import com.kravchenko.denys.githubviewer.network.SessionHolder

class GithubRepository(private val api: GithubAPI, private val sessionHolder: SessionHolder) {
    fun saveAuthToken(token: String) {
        sessionHolder.githubUserToken = token
    }

    suspend fun fetchUserRepos(userName: String) = api.getUserRepos(userName)
    suspend fun fetchRepoStargazers(ownerName: String, repoName: String) =
        api.fetchStargazers(ownerName, repoName)

    suspend fun fetchRepoContributors(ownerName: String, repoName: String) =
        api.fetchContributors(ownerName, repoName)

    suspend fun fetchAuthenticatedUserInfo() = api.fetchCurrentUser()
    suspend fun fetchFollowing(name: String) = api.fetchFollowing(name)
    suspend fun fetchFollowers(name: String) = api.fetchFollowers(name)
    suspend fun fetchUserInfo(name: String) = api.fetchUser(name)
    suspend fun starRepo(ownerName: String, repoName: String) = api.starRepo(ownerName, repoName)
    suspend fun unStarRepo(ownerName: String, repoName: String) =
        api.unStarRepo(ownerName, repoName)
}

