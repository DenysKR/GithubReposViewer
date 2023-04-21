package com.kravchenko.denys.githubviewer.domain

import com.kravchenko.denys.githubviewer.data.github.GithubRepository
import com.kravchenko.denys.githubviewer.domain.model.Repository
import com.kravchenko.denys.githubviewer.network.BaseUseCase

class GetRepositoriesUseCase(private val githubRepository: GithubRepository) :
    BaseUseCase(githubRepository) {

    suspend fun fetchUserRepositories(username: String) =
        safeApiCall {
            repository.fetchUserRepos(username).map { repository ->
                val stargazers =
                    githubRepository.fetchRepoStargazers(username, repository.name).map {
                        it.toUser()
                    }
                Repository(
                    repository.name,
                    stargazers = stargazers,
                    ownerName = username
                )
            }
        }

    suspend fun starRepo(stargazerUsername: String, ownerUsername: String, repo: Repository) =
        safeApiCall {
            val isRepoStarredByUser =
                repo.stargazers?.findLast { it.name == stargazerUsername } != null
            if (isRepoStarredByUser)
                repository.unStarRepo(ownerUsername, repo.name)
            else
                repository.starRepo(ownerUsername, repo.name)
        }
}