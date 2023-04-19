package com.kravchenko.denys.githubviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LiveData
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kravchenko.denys.githubviewer.model.UserRepositoriesResponseItem
import com.kravchenko.denys.githubviewer.network.NetworkResult
import com.kravchenko.denys.githubviewer.presentation.GithubViewerViewModel
import com.kravchenko.denys.githubviewer.ui.auth.SignInScreen
import com.kravchenko.denys.githubviewer.ui.components.ItemList
import com.kravchenko.denys.githubviewer.ui.components.SearchView
import com.kravchenko.denys.githubviewer.ui.theme.GithubViewerTheme
import org.koin.androidx.compose.koinViewModel


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GithubViewerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    GithubViewerNavHost()
                }
            }
        }
    }

    //TODO Refactor navigation
    @Composable
    fun GithubViewerNavHost(
        viewModel: GithubViewerViewModel = koinViewModel(),
        modifier: Modifier = Modifier.fillMaxSize(),
        navController: NavHostController = rememberNavController(),
        startDestination: String = "sign_in"
    ) {
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = startDestination
        ) {
            composable("sign_in") {
                SignInScreen(modifier) {
                    navController.navigate("profile") {
                        popUpTo("sign_in") { inclusive = true }
                    }
                    viewModel.signIn()
                }
            }
            composable("profile") {
                ProfileScreen()
            }
            composable("repository") { RepositoryScreen(/*...*/) }
            composable("search") {
                SearchScreen(
                    onNavigateToReposScreen = { navController.navigate("repository") },
                )
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

            when (val repos = viewModel.response.observeAsState()?.value) {
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

            when (val repos = viewModel.response.observeAsState()?.value) {
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

    @Composable
    private fun <T> handleData(
        state: LiveData<T>, onError: (() -> Unit)? = null,
        onSuccess: @Composable (data: T) -> Unit,
        onLoading: (@Composable () -> Unit)? = null,
    ) {
        when (val result = state.observeAsState()?.value) {
            is NetworkResult.Error<*> -> {
                onError?.invoke()
            }

            is NetworkResult.Success<*> -> {
                onSuccess.invoke(result.data as T)
            }

            is NetworkResult.Loading<*> -> {}

            else -> {
                onLoading?.invoke()
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        GithubViewerTheme {
            GithubViewerNavHost()
        }
    }
}