package com.kravchenko.denys.githubviewer.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kravchenko.denys.githubviewer.domain.GetRepositoriesUseCase
import com.kravchenko.denys.githubviewer.domain.SignInUseCase
import com.kravchenko.denys.githubviewer.domain.model.Repository
import com.kravchenko.denys.githubviewer.domain.model.User
import com.kravchenko.denys.githubviewer.network.NetworkResult
import kotlinx.coroutines.launch

class GithubViewerViewModel(
    private val signInUseCase: SignInUseCase,
    private val repositoriesUseCase: GetRepositoriesUseCase
) : ViewModel() {

    private val _userRepos: MutableLiveData<NetworkResult<List<Repository>>> =
        MutableLiveData()
    val userRepos: LiveData<NetworkResult<List<Repository>>> = _userRepos

    private val _userResponse: MutableLiveData<NetworkResult<User>> =
        MutableLiveData()
    val userResponse: MutableLiveData<NetworkResult<User>> = _userResponse

    fun fetchUserRepos(userName: String) = viewModelScope.launch {
        if (userName.length > 2)//At least 2 letters should be typed for starting search
            repositoriesUseCase.fetchUserRepositories(userName).collect { repositories ->
                _userRepos.value = repositories
            }
    }

    fun signIn(token: String) = viewModelScope.launch {
        with(signInUseCase) {
            saveAuthToken(token)
            fetchUserInfo().collect { user ->
                _userResponse.value = user
            }
        }
    }
}