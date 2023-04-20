package com.kravchenko.denys.githubviewer.domain

import com.kravchenko.denys.githubviewer.data.github.GithubRepository
import com.kravchenko.denys.githubviewer.domain.model.Repository
import com.kravchenko.denys.githubviewer.network.BaseUseCase

class GetRepositoriesUseCase(repository: GithubRepository) : BaseUseCase(repository) {

    suspend fun fetchUserRepositories(username: String) =
        safeApiCall {
            repository.fetchUserRepos(username).map { repository ->
                Repository(repository.fullName)
            }
        }

}