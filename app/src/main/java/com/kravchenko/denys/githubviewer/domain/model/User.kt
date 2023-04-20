package com.kravchenko.denys.githubviewer.domain.model

data class User(
    val name: String,
    val avatarURL: String? = null,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val repos: List<Repository>
)

data class Repository(val name: String)
