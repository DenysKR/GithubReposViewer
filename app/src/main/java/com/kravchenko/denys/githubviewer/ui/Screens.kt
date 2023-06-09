package com.kravchenko.denys.githubviewer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kravchenko.denys.githubviewer.R
import com.kravchenko.denys.githubviewer.domain.model.Repository
import com.kravchenko.denys.githubviewer.domain.model.User
import com.kravchenko.denys.githubviewer.network.NetworkResult
import com.kravchenko.denys.githubviewer.presentation.GithubViewerViewModel

const val PROFILE_TAG = "ProfileScreen"
const val SIGN_IN_TAG = "SignInScreen"
const val REPOSITORY_TAG = "RepositoryScreen"
const val SEARCH_TAG = "SearchScreen"
const val USERS_TAG = "UsersScreen"
const val CONTRIBUTORS_TAG = "ContributorsScreen"

private const val enableSignInThreshold = 2

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignInScreen(
    screenModifier: Modifier,
    onClick: (enteredToken: String) -> Unit,
    githubToken: String = ""
) {
    var githubToken by remember { mutableStateOf(githubToken) }
    var clickButtonState by remember { mutableStateOf(githubToken.isNotBlank()) }
    val keyboardController = LocalSoftwareKeyboardController.current

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
                    keyboardController?.hide()
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
    onSearchClicked: () -> Unit,
    onFollowersClicked: () -> Unit,
    onFollowingsClicked: () -> Unit,
    onLogout: () -> Unit,
) {
    viewModel.userResponse.value?.data?.let { user ->

        val user by remember {
            mutableStateOf(user)
        }

        val padding = 10.dp

        Column {
            Icon(
                Icons.Default.Search,
                contentDescription = "",
                modifier = Modifier
                    .padding(15.dp)
                    .size(24.dp)
                    .clickable { onSearchClicked.invoke() }
            )
            Row(modifier = Modifier.padding(top = padding, start = padding)) {
                AsyncImage(
                    model = user.avatarURL,
                    contentDescription = stringResource(R.string.user_avatar),
                    modifier = Modifier.padding(start = padding, end = padding)
                )
                Column(verticalArrangement = Arrangement.Top) {
                    Text("${user.name}")
                    Text(
                        stringResource(R.string.followers_count, user.followersCount),
                        Modifier.clickable {
                            onFollowersClicked()
                        })
                    Text(stringResource(R.string.following_count, user.followingCount),
                        Modifier.clickable {
                            onFollowingsClicked()
                        })
                }
            }
            Text(
                text = stringResource(R.string.repositories, user.name), fontStyle = Italic,
                modifier = Modifier.padding(padding)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.purple_700))
            ) {
                ItemList(
                    Modifier.padding(horizontal = padding),
                    user.repos,
                    onNavigateToReposScreen
                )
            }
            Row(
                Modifier.fillMaxSize(), verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    onClick = onLogout
                ) {
                    Text(text = stringResource(R.string.logout))
                }
            }
        }
    }
}

@Composable
fun FollowersFollowingsScreen(
    viewModel: GithubViewerViewModel
) {
    val users = viewModel.followersFollowings.observeAsState().value!!
    buildUsersScreen(users)
}
@Composable
fun ContributorsScreen(
    viewModel: GithubViewerViewModel
) {
    val contributorsState by remember {
        mutableStateOf(viewModel.selectedRepository?.contributors)
    }
    contributorsState?.let {
        val screenData = Pair(R.string.contributors, it)
        buildUsersScreen(screenData)
    }
}

@Composable
private fun buildUsersScreen(users: Pair<Int, List<User>>) {
    val title = users.first
    val usersState = users.second

    val padding = 10.dp

    Column {
        Text(
            text = stringResource(id = title), fontStyle = Italic,
            modifier = Modifier.padding(padding)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.purple_700))
        ) {
            LazyColumn {
                itemsIndexed(usersState) { index, _ ->
                    ItemListItem(modifier = Modifier.fillMaxWidth(), text = usersState[index].name,
                        onItemClick = {

                        })
                }
            }
        }
    }
}

@Composable
fun RepositoryScreen(
    viewModel: GithubViewerViewModel,
    onContributorsClick: () -> Unit,
    onOwnerClick: () -> Unit,
    onStarUnStarClick: () -> Unit,
    screenModifier: Modifier = Modifier.fillMaxSize()
) {
    val isRepoStarredByCurrentUser = viewModel.repoStarringResponse.observeAsState().value ?: false

    Column(
        modifier = screenModifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Button(onClick = onContributorsClick) {
            Text(text = stringResource(R.string.contributors))
        }

        Button(onClick = onOwnerClick) {
            Text(text = stringResource(R.string.owner))
        }

        Button(onClick = onStarUnStarClick) {
            Text(
                text = if (isRepoStarredByCurrentUser) stringResource(R.string.unstar) else
                    stringResource(R.string.unstar)
            )
        }
    }
}

@Composable
fun SearchScreen(
    onNavigateToReposScreen: (item: Repository) -> Unit,
    viewModel: GithubViewerViewModel
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colorResource(id = R.color.purple_700))
                    ) {
                        ItemList(Modifier.padding(horizontal = 10.dp), it, onNavigateToReposScreen)
                    }
                }
            }

            is NetworkResult.Loading -> {}

            else -> {}
        }
    }
}