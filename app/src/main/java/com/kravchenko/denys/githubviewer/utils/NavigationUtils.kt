package com.kravchenko.denys.githubviewer.utils

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.kravchenko.denys.githubviewer.domain.model.Repository
import com.kravchenko.denys.githubviewer.presentation.GithubViewerViewModel
import com.kravchenko.denys.githubviewer.ui.CONTRIBUTORS_TAG
import com.kravchenko.denys.githubviewer.ui.ContributorsScreen
import com.kravchenko.denys.githubviewer.ui.FollowersFollowingsScreen
import com.kravchenko.denys.githubviewer.ui.PROFILE_TAG
import com.kravchenko.denys.githubviewer.ui.ProfileScreen
import com.kravchenko.denys.githubviewer.ui.REPOSITORY_TAG
import com.kravchenko.denys.githubviewer.ui.RepositoryScreen
import com.kravchenko.denys.githubviewer.ui.SEARCH_TAG
import com.kravchenko.denys.githubviewer.ui.SIGN_IN_TAG
import com.kravchenko.denys.githubviewer.ui.SearchScreen
import com.kravchenko.denys.githubviewer.ui.SignInScreen
import com.kravchenko.denys.githubviewer.ui.USERS_TAG

fun NavGraphBuilder.buildSignInScreen(
    modifier: Modifier, viewModel: GithubViewerViewModel
) = composable(SIGN_IN_TAG) {
    SignInScreen(modifier, onClick = { token ->
        viewModel.signIn(token)
    })
}
fun NavGraphBuilder.buildProfileScreen(
    viewModel: GithubViewerViewModel,
    onNavigateToReposScreen: (item: Repository) -> Unit,
    onSearchClicked: () -> Unit,
    onFollowersClicked: () -> Unit,
    onFollowingClicked: () -> Unit,
    onLogout: () -> Unit
) = composable(PROFILE_TAG) {
    ProfileScreen(
        viewModel,
        onNavigateToReposScreen,
        onSearchClicked = onSearchClicked,
        onFollowersClicked = onFollowersClicked,
        onFollowingsClicked = onFollowingClicked,
        onLogout = onLogout
    )
}
fun NavGraphBuilder.buildRepositoryScreen(
    navController: NavHostController,
    viewModel: GithubViewerViewModel,
) = composable(REPOSITORY_TAG) {
    RepositoryScreen(onContributorsClick = {
        navController.navigate(CONTRIBUTORS_TAG)
    }, onOwnerClick = {
        viewModel.fetchUserInfo(viewModel.selectedRepository!!.ownerName)
    }, onStarUnStarClick = {
        viewModel.starRepo()
    }, viewModel = viewModel
    )
}
fun NavGraphBuilder.buildSearchScreen(
    onNavigateToReposScreen: (item: Repository) -> Unit, viewModel: GithubViewerViewModel
) = composable(SEARCH_TAG) {
    SearchScreen(
        onNavigateToReposScreen = onNavigateToReposScreen, viewModel
    )
}
fun NavGraphBuilder.buildUsersListScreen(
    viewModel: GithubViewerViewModel
) = composable(USERS_TAG) {
    FollowersFollowingsScreen(viewModel)
}
fun NavGraphBuilder.buildContributorsListScreen(viewModel: GithubViewerViewModel) = composable(
    CONTRIBUTORS_TAG
) {
    ContributorsScreen(viewModel)
}