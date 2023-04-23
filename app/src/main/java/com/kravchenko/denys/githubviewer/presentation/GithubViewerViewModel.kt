package com.kravchenko.denys.githubviewer.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kravchenko.denys.githubviewer.R
import com.kravchenko.denys.githubviewer.domain.GetRepositoriesUseCase
import com.kravchenko.denys.githubviewer.domain.AuthUseCase
import com.kravchenko.denys.githubviewer.domain.model.Repository
import com.kravchenko.denys.githubviewer.domain.model.User
import com.kravchenko.denys.githubviewer.network.NetworkResult
import kotlinx.coroutines.launch

enum class FFUSERS {
    FOLLOWERS, FOLLOWINGS
}

class GithubViewerViewModel(
    private val authUseCase: AuthUseCase,
    private val repositoriesUseCase: GetRepositoriesUseCase
) : ViewModel() {
    var selectedRepository: Repository? = null

    private val _userRepos: MutableLiveData<NetworkResult<List<Repository>>> = MutableLiveData()
    val userRepos: LiveData<NetworkResult<List<Repository>>> = _userRepos

    private val _userResponse: MutableLiveData<NetworkResult<User>> = MutableLiveData()
    val userResponse: LiveData<NetworkResult<User>> = _userResponse

    private val _repoStarringResponse: MutableLiveData<Boolean> = MutableLiveData()
    val repoStarringResponse: LiveData<Boolean> = _repoStarringResponse

    private var _followersFollowings: MutableLiveData<Pair<Int, List<User>>> = MutableLiveData()
    var followersFollowings: LiveData<Pair<Int, List<User>>> = _followersFollowings

    fun logout() {
        authUseCase.logout()
    }

    fun updateFollowersFollowings(users: FFUSERS) {
        when (users) {
            FFUSERS.FOLLOWERS -> {
                val ffUsers = userResponse.value?.data?.followers ?: emptyList()
                _followersFollowings.value = Pair(R.string.followers, ffUsers)
            }

            FFUSERS.FOLLOWINGS -> {
                val ffUsers = userResponse.value?.data?.following ?: emptyList()
                _followersFollowings.value = Pair(R.string.following, ffUsers)
            }
        }
    }

    fun fetchUserRepos(userName: String) = viewModelScope.launch {
        if (userName.length > 2)//At least 2 letters should be typed for starting search
            repositoriesUseCase.fetchUserRepositories(userName).collect { repositories ->
                _userRepos.value = repositories
            }
    }

    fun fetchUserInfo(userName: String) = viewModelScope.launch {
        authUseCase.fetchUserInfo(userName).collect { user ->
            _userResponse.value = user
        }
    }

    fun signIn(token: String) = viewModelScope.launch {
        with(authUseCase) {
            saveAuthToken(token)
            fetchCurrentUserInfo().collect { user ->
                _userResponse.value = user
            }
        }
    }

    fun starRepo() = viewModelScope.launch {
        selectedRepository?.let { repo ->
            val ownerUsername = repo.ownerName
            repositoriesUseCase.starRepo(ownerUsername, ownerUsername, repo).collect { status ->
                _repoStarringResponse.value = when (status) {
                    is NetworkResult.Success -> true
                    is NetworkResult.Error -> false
                    else -> false
                }
            }
        }
    }
}