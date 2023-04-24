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
    fun manageAuthToken() {
        val testToken = "Token"
        repository.saveAuthToken(testToken)
        Assertions.assertEquals(testToken, sessionHolder.githubUserToken)
        repository.clearAuthToken()
        Assertions.assertNull(sessionHolder.githubUserToken)
    }

    @Test
    fun fetchUserRepos() = runTest {
        val userResponseMock = mockk<UserResponse>()

        val userName = "TestUser"

        coEvery { githubAPI.fetchUser(userName) } returns userResponseMock

        val userInfo = repository.fetchUserInfo(userName)

        Assertions.assertEquals(userInfo, userResponseMock)
    }

    @Test
    fun fetchRepoStargazers() = runTest {
        val repoStargazersMock = listOf(mockk<UserResponse>())

        val userName = "TestUser"
        val repoName = "RepoName"

        coEvery { githubAPI.fetchStargazers(userName, repoName) } returns repoStargazersMock

        val repoStargazers = repository.fetchRepoStargazers(userName, repoName)

        Assertions.assertEquals(repoStargazers, repoStargazersMock)
    }

    @Test
    fun fetchRepoContributors() = runTest {
        val contributorsMock = listOf(mockk<UserResponse>())

        val userName = "TestUser"
        val repoName = "RepoName"

        coEvery { githubAPI.fetchContributors(userName, repoName) } returns contributorsMock

        val repoRepoContributors = repository.fetchRepoContributors(userName, repoName)

        Assertions.assertEquals(repoRepoContributors, contributorsMock)
    }

    @Test
    fun fetchAuthenticatedUserInfo() = runTest {
        val currentUserMockResponse = mockk<UserResponse>()

        coEvery { githubAPI.fetchCurrentUser() } returns currentUserMockResponse

        val currentUserInfo = repository.fetchAuthenticatedUserInfo()

        Assertions.assertEquals(currentUserInfo, currentUserMockResponse)
    }

    @Test
    fun fetchFollowing() = runTest {
        val followingsMock = listOf(mockk<UserResponse>())

        val userName = "TestUser"

        coEvery { githubAPI.fetchFollowing(userName) } returns followingsMock

        val followings = repository.fetchFollowing(userName)

        Assertions.assertEquals(followings, followingsMock)
    }

    @Test
    fun fetchFollowers() = runTest {
        val followersMock = listOf(mockk<UserResponse>())

        val userName = "TestUser"

        coEvery { githubAPI.fetchFollowers(userName) } returns followersMock

        val followers = repository.fetchFollowers(userName)

        Assertions.assertEquals(followers, followersMock)
    }

    @Test
    fun fetchUserInfo() = runTest {
        val userResponseMock = mockk<UserResponse>()

        val userName = "TestUser"

        coEvery { githubAPI.fetchUser(userName) } returns userResponseMock

        val user = repository.fetchUserInfo(userName)

        Assertions.assertEquals(user, userResponseMock)
    }
}