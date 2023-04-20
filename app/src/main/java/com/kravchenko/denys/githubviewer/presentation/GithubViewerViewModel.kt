package com.kravchenko.denys.githubviewer.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kravchenko.denys.githubviewer.BuildConfig
import com.kravchenko.denys.githubviewer.data.github.GithubRepository
import com.kravchenko.denys.githubviewer.model.UserRepositoriesResponseItem
import com.kravchenko.denys.githubviewer.model.UserResponse
import com.kravchenko.denys.githubviewer.network.NetworkResult
import kotlinx.coroutines.launch

class GithubViewerViewModel(private val repository: GithubRepository) : ViewModel() {

    private val _userRepos: MutableLiveData<NetworkResult<List<UserRepositoriesResponseItem>>> =
        MutableLiveData()
    val userRepos: LiveData<NetworkResult<List<UserRepositoriesResponseItem>>> = _userRepos

    private val _userResponse: MutableLiveData<NetworkResult<UserResponse>> =
        MutableLiveData()
    val userResponse: MutableLiveData<NetworkResult<UserResponse>> = _userResponse

    lateinit var userInfo: UserResponse

    fun fetchUserRepos(userName: String) = viewModelScope.launch {
        if (userName.length > 2)//At least 2 letters should be typed for starting search
            repository.getUserRepos(userName).collect { values ->
                _userRepos.value = values
            }
    }

    fun signIn(token: String) = viewModelScope.launch {
        with(repository) {
            saveAuthToken(token)
            repository.getAuthenticatedUser().collect { user ->
                user?.let { userResponse ->
                    _userResponse.value = userResponse
                    userResponse.data?.let { user -> userInfo = user }
                }
            }
        }
    }
}