package com.kravchenko.denys.githubviewer.di

import com.kravchenko.denys.githubviewer.presentation.GithubViewerViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    viewModelOf(::GithubViewerViewModel)

}