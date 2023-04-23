package com.kravchenko.denys.githubviewer.domain

import com.kravchenko.denys.githubviewer.data.github.GithubRepository
import com.kravchenko.denys.githubviewer.domain.model.Repository
import com.kravchenko.denys.githubviewer.network.BaseUseCase

//TODO Implement autologin
class SignInUseCase(repository: GithubRepository) : BaseUseCase(repository) {

    fun saveAuthToken(token: String) = githubRepository.saveAuthToken(token)

    suspend fun fetchCurrentUserInfo() =
        safeApiCall {
            with(githubRepository) {
                val userData = fetchAuthenticatedUserInfo()
                val userName = userData.login
                val fllowers = fetchFollowers(userName)
                val fllowing = fetchFollowing(userName)
                val userRepositories = fetchUserRepos(userName)
                userData.toUser().apply {
                    repos = userRepositories.map { repository ->
                        val contributors = githubRepository.fetchRepoContributors(
                            repository.owner.login,
                            repository.name
                        )
                        val stargazers = githubRepository.fetchRepoStargazers(
                            repository.owner.login,
                            repository.name
                        )
                        following = fllowing.map { it.toUser() }
                        followers = fllowers.map { it.toUser() }
                        Repository(
                            name = repository.name,
                            ownerName = userName,
                            contributors = contributors.map { it.toUser() },
                            stargazers = stargazers.map { it.toUser() }
                        )
                    }
                }
            }
        }

    suspend fun fetchUserInfo(name: String) =
        safeApiCall {
            with(githubRepository) {
                val userData = fetchUserInfo(name)
                val fllowers = fetchFollowers(name)
                val fllowing = fetchFollowing(name)
                val userName = userData.login
                val userRepositories = fetchUserRepos(userName)
                userData.toUser().apply {
                    following = fllowing.map { it.toUser() }
                    followers = fllowers.map { it.toUser() }
                    repos = userRepositories.map { repository ->
                        Repository(
                            name = repository.name,
                            ownerName = userName
                        )
                    }
                }
            }
        }
}