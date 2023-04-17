package com.kravchenko.denys.githubviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kravchenko.denys.githubviewer.model.UserRepositoriesResponseItem
import com.kravchenko.denys.githubviewer.network.NetworkResult
import com.kravchenko.denys.githubviewer.presentation.GithubViewerViewModel
import com.kravchenko.denys.githubviewer.ui.components.SearchView
import com.kravchenko.denys.githubviewer.ui.theme.GithubViewerTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : ComponentActivity() {

    private val model: GithubViewerViewModel by viewModel()

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
    fun SearchScreen(
        onNavigateToRepos: (item: UserRepositoriesResponseItem) -> Unit,
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
                        ItemList(it, onNavigateToRepos)
                    }
                }

                is NetworkResult.Loading -> {
                    "Loading"
                }

                else -> {}
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

    @Composable
    fun ItemList(
        repos: List<UserRepositoriesResponseItem>,
        onClick: (item: UserRepositoriesResponseItem) -> Unit
    ) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            itemsIndexed(repos) { index, item ->
                ItemListItem(
                    text = repos[index].fullName ?: "",
                    onItemClick = {
                        onClick(item)
                    }
                )
            }
        }
    }

    @Composable
    fun ItemListItem(text: String, onItemClick: (String) -> Unit) {
        Row(
            modifier = Modifier
                .clickable(onClick = { onItemClick(text) })
                .background(colorResource(id = R.color.purple_700))
                .height(57.dp)
                .fillMaxWidth()
                .padding(PaddingValues(8.dp, 16.dp))
        ) {
            Text(text = text, fontSize = 18.sp, color = Color.White)
        }
    }
}