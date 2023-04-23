package com.kravchenko.denys.githubviewer.domain.model

data class User(
    val name: String,
    val avatarURL: String? = null,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    var repos: List<Repository> = emptyList(),
    var followers: List<User> = emptyList(),
    var following: List<User> = emptyList(),
)


data class Repository(
    val name: String,
    val stargazers: List<User?>? = null,
    val contributors: List<User>? = null,
    val ownerName: String
)
