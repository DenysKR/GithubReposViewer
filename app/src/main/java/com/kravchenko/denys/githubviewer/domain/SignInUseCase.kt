package com.kravchenko.denys.githubviewer.domain

import com.kravchenko.denys.githubviewer.data.github.GithubRepository
import com.kravchenko.denys.githubviewer.domain.model.Repository
import com.kravchenko.denys.githubviewer.domain.model.User
import com.kravchenko.denys.githubviewer.network.BaseUseCase

class SignInUseCase(repository: GithubRepository) : BaseUseCase(repository) {

    fun saveAuthToken(token: String) = repository.saveAuthToken(token)

    suspend fun fetchUserInfo() =
        safeApiCall {
            with(repository) {
                val userData = fetchAuthenticatedUserInfo()
                val userRepositories = fetchUserRepos(userData.login)
                User(name = userData.name,
                    avatarURL = userData.avatarUrl,
                    followersCount = userData.followers,
                    followingCount = userData.following,
                    repos = userRepositories.map { repository ->
                        Repository(repository.fullName)
                    })
            }
        }
}