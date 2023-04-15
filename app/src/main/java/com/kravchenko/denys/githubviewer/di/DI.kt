package com.kravchenko.denys.githubviewer.di

import com.kravchenko.denys.githubviewer.data.github.GithubRepository
import com.kravchenko.denys.githubviewer.network.GithubHttpClient
import com.kravchenko.denys.githubviewer.presentation.GithubViewerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { GithubHttpClient.apiService }
    single { GithubRepository(get()) }
    viewModel<GithubViewerViewModel>()

}