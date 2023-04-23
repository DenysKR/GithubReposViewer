package com.kravchenko.denys.githubviewer.data.github

import com.kravchenko.denys.githubviewer.model.UserResponse
import com.kravchenko.denys.githubviewer.network.GithubAPI
import com.kravchenko.denys.githubviewer.network.SessionHolder
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions

class GithubRepositoryTest {

    private lateinit var githubAPI: GithubAPI
    private lateinit var sessionHolder: SessionHolder
    private lateinit var repository: GithubRepository

    @Before
    fun setup() {
        githubAPI = mockk()
        sessionHolder = spyk()
        repository = GithubRepository(githubAPI, sessionHolder)
    }

    @Test
    fun saveAuthToken() {
        val testToken = "Token"
        repository.saveAuthToken(testToken)
        Assertions.assertEquals(testToken, sessionHolder.githubUserToken)
    }

    @Test
    fun fetchUserRepos() = runTest {
        val userMock = mockk<UserResponse>()

        val userName = "TestUser"

        coEvery { githubAPI.fetchUser(userName) } returns userMock

        val userInfo = repository.fetchUserInfo(userName)

        Assertions.assertEquals(userInfo, userMock)
    }

    @Test
    fun fetchRepoStargazers() {
    }

    @Test
    fun fetchRepoContributors() {
    }

    @Test
    fun fetchAuthenticatedUserInfo() {
    }

    @Test
    fun fetchFollowing() {
    }

    @Test
    fun fetchFollowers() {
    }

    @Test
    fun fetchUserInfo() {
    }

    @Test
    fun starRepo() {
    }

    @Test
    fun unStarRepo() {
    }
}