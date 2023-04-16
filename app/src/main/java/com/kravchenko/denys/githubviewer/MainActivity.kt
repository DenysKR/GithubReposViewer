package com.kravchenko.denys.githubviewer

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kravchenko.denys.githubviewer.network.NetworkResult
import com.kravchenko.denys.githubviewer.presentation.GithubViewerViewModel
import com.kravchenko.denys.githubviewer.ui.theme.GithubViewerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : ComponentActivity() {

    private val model: GithubViewerViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GithubViewerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    GithubViewerNavHost()
                }
            }
        }
    }

    @Composable
    fun GithubViewerNavHost(
        modifier: Modifier = Modifier.fillMaxSize(),
        navController: NavHostController = rememberNavController(),
        startDestination: String = "search"
    ) {
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = startDestination
        ) {
            composable("profile") {
                ProfileScreen()
            }
            composable("repository") { RepositoryScreen(/*...*/) }
            composable("search") {
                SearchScreen(
                    onNavigateToRepos = { navController.navigate("repository") },
                )
            }
        }
    }

    @Composable
    fun ProfileScreen() {
        Text(text = "Profile")
    }

    @Composable
    fun RepositoryScreen() {
        Text(text = "Repository")
    }

    @Composable
    fun SearchScreen(onNavigateToRepos: () -> Unit) {
        Text(text = "Search")
        Button(onClick = onNavigateToRepos) {
//            fetchUserRepos("DenysKR")
            Text(text = "Go to repos")
        }
    }

    private fun fetchUserRepos(userName: String) {
        with(model) {
            fetchUserRepos(userName)
            response.observe(this@MainActivity) { response ->
                when (response) {
                    is NetworkResult.Success -> {
                        Log.d("UserRepos Success: ", "${response.data}")
                    }

                    is NetworkResult.Error -> {
                        Log.d("UserRepos Error: ", "${response.message}")
                    }

                    is NetworkResult.Loading -> {
                        Log.d("UserRepos Loading: ", "Loading")
                    }
                }
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