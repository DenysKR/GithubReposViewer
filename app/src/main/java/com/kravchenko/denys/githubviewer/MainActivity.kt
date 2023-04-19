package com.kravchenko.denys.githubviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kravchenko.denys.githubviewer.model.UserResponse
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
            buildSignInScreen(modifier, viewModel)
            buildProfileScreen()
            buildRepositoryScreen()
            buildSearchScreen(navController)
        }
        when (viewModel.user.observeAsState()?.value) {
            is NetworkResult.Error -> {

            }

            is NetworkResult.Success<UserResponse> -> {
                navController.navigate(PROFILE_TAG) {
                    popUpTo(SIGN_IN_TAG) { inclusive = true }
                }
            }

            is NetworkResult.Loading -> {

            }

            else -> {}
        }
    }

    private fun NavGraphBuilder.buildSignInScreen(
        modifier: Modifier,
        viewModel: GithubViewerViewModel
    ) = composable(SIGN_IN_TAG) {
        SignInScreen(modifier, githubToken = viewModel.githubToken, onClick = {
            viewModel.signIn()
        })
    }

    private fun NavGraphBuilder.buildProfileScreen() = composable(PROFILE_TAG) {
        ProfileScreen()
    }

    private fun NavGraphBuilder.buildRepositoryScreen() = composable(REPOSITORY_TAG) {
        RepositoryScreen()
    }

    private fun NavGraphBuilder.buildSearchScreen(navController: NavHostController) =
        composable(SEARCH_TAG) {
            SearchScreen(
                onNavigateToReposScreen = { navController.navigate(SEARCH_TAG) },
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