package com.kravchenko.denys.githubviewer.network
//TODO Encrypt the token and save it in preferences datastore
data class SessionHolder(var githubUserToken: String? = null)