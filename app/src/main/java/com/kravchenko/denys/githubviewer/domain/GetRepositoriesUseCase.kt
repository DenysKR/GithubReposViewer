package com.kravchenko.denys.githubviewer.domain

import com.kravchenko.denys.githubviewer.data.github.GithubRepository
import com.kravchenko.denys.githubviewer.domain.model.Repository
import com.kravchenko.denys.githubviewer.network.BaseUseCase

class GetRepositoriesUseCase(val repository: GithubRepository) :
    BaseUseCase(repository) {

    suspend fun fetchUserRepositories(username: String) =
        safeApiCall {
            repository.fetchUserRepos(username).map { repository ->
                val stargazers =
                    this.repository.fetchRepoStargazers(username, repository.name).map {
                        it.toUser()
                    }

                val contributors =
                    this.repository.fetchRepoContributors(username, repository.name).map {
                        it.toUser()
                    }
                Repository(
                    repository.name,
                    stargazers = stargazers,
                    contributors = contributors,
                    ownerName = username
                )
            }
        }

    suspend fun starRepo(stargazerUsername: String, ownerUsername: String, repo: Repository) =
        safeApiCall {
            val isRepoStarredByUser =
                repo.stargazers?.findLast { it?.name == stargazerUsername } != null
            if (isRepoStarredByUser)
                repository.unStarRepo(ownerUsername, repo.name)
            else
                repository.starRepo(ownerUsername, repo.name)
        }
}