package com.kravchenko.denys.githubviewer.domain

import com.kravchenko.denys.githubviewer.domain.model.User
import com.kravchenko.denys.githubviewer.model.UserResponse

fun UserResponse.toUser(): User = User(
    name = name ?: "",
    avatarURL = avatarUrl,
    followersCount = followers,
    followingCount = following,
)

