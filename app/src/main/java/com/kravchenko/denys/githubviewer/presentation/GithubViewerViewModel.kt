package com.kravchenko.denys.githubviewer.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kravchenko.denys.githubviewer.data.github.GithubRepository
import com.kravchenko.denys.githubviewer.model.UserRepositoriesResponse
import com.kravchenko.denys.githubviewer.network.NetworkResult
import kotlinx.coroutines.launch

class GithubViewerViewModel(private val repository: GithubRepository) : ViewModel() {

    private val _response: MutableLiveData<NetworkResult<UserRepositoriesResponse>> =
        MutableLiveData()
    val response: LiveData<NetworkResult<UserRepositoriesResponse>> = _response
    fun fetchUserRepos(userName: String) = viewModelScope.launch {
        repository.getUserRepos(userName).collect { values ->
            _response.value = values
        }
    }
}