package com.kravchenko.denys.githubviewer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kravchenko.denys.githubviewer.R
import com.kravchenko.denys.githubviewer.domain.model.Repository
import com.kravchenko.denys.githubviewer.network.NetworkResult
import com.kravchenko.denys.githubviewer.presentation.GithubViewerViewModel
import com.kravchenko.denys.githubviewer.ui.components.ItemList
import com.kravchenko.denys.githubviewer.ui.components.SearchView
import org.koin.androidx.compose.koinViewModel

const val PROFILE_TAG = "ProfileScreen"
const val SIGN_IN_TAG = "SignInScreen"
const val REPOSITORY_TAG = "RepositoryScreen"
const val SEARCH_TAG = "SearchScreen"

private const val enableSignInThreshold = 2

@Composable
fun SignInScreen(
    screenModifier: Modifier,
    onClick: (enteredToken: String) -> Unit,
    githubToken: String = ""
) {
    var githubToken by remember { mutableStateOf(githubToken) }
    var clickButtonState by remember { mutableStateOf(githubToken.isNotBlank()) }

    Column(
        modifier = screenModifier.padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(R.string.github_token), fontStyle = Italic,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Row {
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 10.dp),
                value = githubToken,
                onValueChange = {
                    clickButtonState = it.length > enableSignInThreshold
                    githubToken = it
                },
                placeholder = { Text(stringResource(R.string.type_github_token)) },
                singleLine = true
            )

            Button(
                onClick = {
                    onClick(githubToken)
                }, enabled = clickButtonState
            ) {
                Text(text = stringResource(R.string.sign_in))
            }
        }
    }
}

@Composable
fun ProfileScreen(
    viewModel: GithubViewerViewModel,
    onNavigateToReposScreen: (item: Repository) -> Unit,
) {
    val user = viewModel.userResponse.value!!.data!!
    Column {
        Row(modifier = Modifier.padding(top = 10.dp, start = 10.dp)) {
            AsyncImage(
                model = user.avatarURL,
                contentDescription = stringResource(R.string.user_avatar),
                modifier = Modifier.padding(start = 10.dp, end = 10.dp)
            )
            Column(verticalArrangement = Arrangement.Top) {
                Text("${user.name}")
                Text(stringResource(R.string.followers_count, user.followersCount))
                Text(stringResource(R.string.following_count, user.followingCount))
            }
        }

        when (val repos = viewModel.userRepos.observeAsState()?.value) {
            is NetworkResult.Error -> {
                repos.message
            }

            is NetworkResult.Success<List<Repository>> -> {
                repos.data?.let {
                    ItemList(it, onNavigateToReposScreen)
                }
            }

            is NetworkResult.Loading -> {}

            else -> {}
        }
    }
}

@Composable
fun RepositoryScreen(
    viewModel: GithubViewerViewModel = koinViewModel(),
    screenModifier: Modifier = Modifier.fillMaxSize()
) {
    Column(
        modifier = screenModifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        when (val repos = viewModel.userRepos.observeAsState()?.value) {
            is NetworkResult.Error -> {
                repos.message
            }

            is NetworkResult.Success<List<Repository>> -> {
               //TODO Implement repos list
            }

            is NetworkResult.Loading -> {}

            else -> {}
        }
    }
}

@Composable
fun SearchScreen(
    onNavigateToReposScreen: (item: Repository) -> Unit,
    viewModel: GithubViewerViewModel = koinViewModel()
) {
    val state = remember {
        mutableStateOf(TextFieldValue(""))
    }

    Column {
        SearchView(state) {
            viewModel.fetchUserRepos(it)
        }

        when (val repos = viewModel.userRepos.observeAsState()?.value) {
            is NetworkResult.Error -> {
                repos.message
            }

            is NetworkResult.Success<List<Repository>> -> {
                repos.data?.let {
                    ItemList(it, onNavigateToReposScreen)
                }
            }

            is NetworkResult.Loading -> {}

            else -> {}
        }
    }
}