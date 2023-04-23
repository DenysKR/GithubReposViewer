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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.kravchenko.denys.githubviewer.domain.model.Repository
import com.kravchenko.denys.githubviewer.domain.model.User
import com.kravchenko.denys.githubviewer.network.NetworkResult
import com.kravchenko.denys.githubviewer.presentation.FFUSERS
import com.kravchenko.denys.githubviewer.presentation.GithubViewerViewModel
import com.kravchenko.denys.githubviewer.ui.PROFILE_TAG
import com.kravchenko.denys.githubviewer.ui.REPOSITORY_TAG
import com.kravchenko.denys.githubviewer.ui.SEARCH_TAG
import com.kravchenko.denys.githubviewer.ui.SIGN_IN_TAG
import com.kravchenko.denys.githubviewer.ui.USERS_TAG
import com.kravchenko.denys.githubviewer.ui.showProgress
import com.kravchenko.denys.githubviewer.ui.showToast
import com.kravchenko.denys.githubviewer.ui.theme.GithubViewerTheme
import com.kravchenko.denys.githubviewer.utils.buildContributorsListScreen
import com.kravchenko.denys.githubviewer.utils.buildProfileScreen
import com.kravchenko.denys.githubviewer.utils.buildRepositoryScreen
import com.kravchenko.denys.githubviewer.utils.buildSearchScreen
import com.kravchenko.denys.githubviewer.utils.buildSignInScreen
import com.kravchenko.denys.githubviewer.utils.buildUsersListScreen
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

    //TODO Handle back stack navigation
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

            val goToFollowersFollowingScreen: (ffusersType: FFUSERS) -> Unit =
                { ffusersType -> viewModel.updateFollowersFollowings(ffusersType) }

            buildSignInScreen(modifier, viewModel)
            buildProfileScreen(
                viewModel = viewModel, onNavigateToReposScreen = goToReposScreen,
                onSearchClicked =
                {
                    navController.navigate(SEARCH_TAG)
                },
                onFollowersClicked = {
                    goToFollowersFollowingScreen(FFUSERS.FOLLOWERS)
                },
                onFollowingClicked = {
                    goToFollowersFollowingScreen(FFUSERS.FOLLOWINGS)
                }
            )
            buildRepositoryScreen(navController, viewModel)
            buildSearchScreen(goToReposScreen, viewModel)
            buildUsersListScreen(viewModel)
            buildContributorsListScreen(viewModel)
        }

        observeUserInfo(viewModel, navController)
        observeFollowersFollowingsInfo(viewModel, navController)
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
    private fun observeFollowersFollowingsInfo(
        viewModel: GithubViewerViewModel,
        navController: NavHostController
    ) {
        viewModel.followersFollowings.observeAsState().value?.let { _ ->
            navController.navigate(USERS_TAG)
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