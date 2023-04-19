package com.kravchenko.denys.githubviewer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kravchenko.denys.githubviewer.R
import com.kravchenko.denys.githubviewer.model.UserRepositoriesResponseItem
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
    onClick: () -> Unit,
    githubToken: String
) {
    var githubToken by remember { mutableStateOf(githubToken) }
    var clickButtonState by remember { mutableStateOf(githubToken.isNotBlank()) }

    Row(
        modifier = screenModifier.padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
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
            onClick =
            onClick, enabled = clickButtonState
        ) {
            Text(text = stringResource(R.string.sign_in))
        }
    }
}

@Composable
fun ProfileScreen() {
    Text(text = "Profile")
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

            is NetworkResult.Success<List<UserRepositoriesResponseItem>> -> {
                repos.data?.let {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(it[0].owner?.avatarUrl)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(R.drawable.ic_launcher_background),
                        contentDescription = stringResource(R.string.app_name),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.clip(CircleShape)
                    )
                }
            }

            is NetworkResult.Loading -> {}

            else -> {}
        }
    }
}

@Composable
fun SearchScreen(
    onNavigateToReposScreen: (item: UserRepositoriesResponseItem) -> Unit,
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

            is NetworkResult.Success<List<UserRepositoriesResponseItem>> -> {
                repos.data?.let {
                    ItemList(it, onNavigateToReposScreen)
                }
            }

            is NetworkResult.Loading -> {}

            else -> {}
        }
    }
}