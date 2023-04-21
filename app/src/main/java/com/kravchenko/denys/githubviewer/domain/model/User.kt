package com.kravchenko.denys.githubviewer.domain.model

data class User(
    val name: String,
    val avatarURL: String? = null,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    var repos: List<Repository> = emptyList()
)


data class Repository(
    val name: String,
    val contributorsUrl: String? = null,
    val ownerName: String
)
