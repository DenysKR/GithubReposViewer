package com.kravchenko.denys.githubviewer

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kravchenko.denys.githubviewer.domain.model.Repository
import com.kravchenko.denys.githubviewer.domain.model.User
import com.kravchenko.denys.githubviewer.network.NetworkResult
import com.kravchenko.denys.githubviewer.presentation.GithubViewerViewModel
import com.kravchenko.denys.githubviewer.ui.PROFILE_TAG
import com.kravchenko.denys.githubviewer.ui.ProfileScreen
import com.kravchenko.denys.githubviewer.ui.REPOSITORY_TAG
import com.kravchenko.denys.githubviewer.ui.RepositoryScreen
import com.kravchenko.denys.githubviewer.ui.SEARCH_TAG
import com.kravchenko.denys.githubviewer.ui.SIGN_IN_TAG
import com.kravchenko.denys.githubviewer.ui.SearchScreen
import com.kravchenko.denys.githubviewer.ui.SignInScreen
import com.kravchenko.denys.githubviewer.ui.theme.GithubViewerTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GithubViewerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {
                    GithubViewerNavHost()
                }
            }
        }
    }

    @Composable
    fun GithubViewerNavHost(
        viewModel: GithubViewerViewModel = koinViewModel(),
        modifier: Modifier = Modifier.fillMaxSize(),
        navController: NavHostController = rememberNavController(),
        startDestination: String = SIGN_IN_TAG
    ) {
        NavHost(
            modifier = modifier, navController = navController, startDestination = startDestination
        ) {
            val goToReposScreen: (repo: Repository) -> Unit =
                { repo ->
                    viewModel.selectedRepository = repo
                    navController.navigate(REPOSITORY_TAG)
                }

            buildSignInScreen(modifier, viewModel)
            buildProfileScreen(viewModel, goToReposScreen)
            buildRepositoryScreen(navController, viewModel)
            buildSearchScreen(goToReposScreen)
        }

        observeUserInfo(viewModel, navController)
    }

    @Composable
    private fun observeUserInfo(
        viewModel: GithubViewerViewModel,
        navController: NavHostController
    ) {
        when (val user = viewModel.userResponse.observeAsState()?.value) {
            is NetworkResult.Error -> {
                showProgress(false)
                user.message?.let { showToast(it) }
            }

            is NetworkResult.Success<User> -> {
                showProgress(false)
                navController.navigate(PROFILE_TAG) {
                    popUpTo(SIGN_IN_TAG) { inclusive = true }
                }
            }

            is NetworkResult.Loading -> {
                showProgress(true)
            }

            else -> {}
        }
    }

    @Composable
    private fun showProgress(show: Boolean) {
        if (show)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
    }

    @Composable
    private fun showToast(message: String) {
        Toast.makeText(LocalContext.current, message, Toast.LENGTH_LONG).show()
    }

    private fun NavGraphBuilder.buildSignInScreen(
        modifier: Modifier,
        viewModel: GithubViewerViewModel
    ) = composable(SIGN_IN_TAG) {
        SignInScreen(modifier, onClick = { token ->
            viewModel.signIn(token)
        })
    }

    private fun NavGraphBuilder.buildProfileScreen(
        viewModel: GithubViewerViewModel,
        onNavigateToReposScreen: (item: Repository) -> Unit
    ) =
        composable(PROFILE_TAG) {
            ProfileScreen(viewModel, onNavigateToReposScreen)
        }

    private fun NavGraphBuilder.buildRepositoryScreen(
        navController: NavHostController,
        viewModel: GithubViewerViewModel,
    ) =
        composable(REPOSITORY_TAG) {
            RepositoryScreen(onContributorsClick = { navController.navigate(PROFILE_TAG) },
                onOwnerClick = { navController.navigate(PROFILE_TAG) },
                onStarUnStarClick = {
                    viewModel.starRepo()
                })
        }

    private fun NavGraphBuilder.buildSearchScreen(onNavigateToReposScreen: (item: Repository) -> Unit) =
        composable(SEARCH_TAG) {
            SearchScreen(
                onNavigateToReposScreen = onNavigateToReposScreen,
            )
        }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        GithubViewerTheme {
            GithubViewerNavHost()
        }
    }
}