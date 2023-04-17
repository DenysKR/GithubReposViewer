package com.kravchenko.denys.githubviewer.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kravchenko.denys.githubviewer.data.github.GithubRepository
import com.kravchenko.denys.githubviewer.model.UserRepositoriesResponseItem
import com.kravchenko.denys.githubviewer.network.NetworkResult
import kotlinx.coroutines.launch

class GithubViewerViewModel(private val repository: GithubRepository) : ViewModel() {

    private val _response: MutableLiveData<NetworkResult<List<UserRepositoriesResponseItem>>> =
        MutableLiveData()
    val response: LiveData<NetworkResult<List<UserRepositoriesResponseItem>>> = _response
    fun fetchUserRepos(userName: String) = viewModelScope.launch {
        if (userName.length > 2)//At least 2 letters should be typed for starting search
            repository.getUserRepos(userName).collect { values ->
                _response.value = values
            }
    }
}