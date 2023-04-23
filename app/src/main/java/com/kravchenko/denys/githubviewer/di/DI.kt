package com.kravchenko.denys.githubviewer.di

import android.content.Context
import com.kravchenko.denys.githubviewer.BuildConfig
import com.kravchenko.denys.githubviewer.data.github.GithubRepository
import com.kravchenko.denys.githubviewer.domain.GetRepositoriesUseCase
import com.kravchenko.denys.githubviewer.domain.SignInUseCase
import com.kravchenko.denys.githubviewer.network.GithubAPI
import com.kravchenko.denys.githubviewer.network.SessionHolder
import com.kravchenko.denys.githubviewer.presentation.GithubViewerViewModel
import com.kravchenko.denys.githubviewer.utils.hasNetwork
import okhttp3.Cache
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single { GithubRepository(get(), get()) }
    single { GithubViewerViewModel(get(), get()) }
    factory { GetRepositoriesUseCase(get()) }
}

val signInModule = module {
    factory { SignInUseCase(get()) }
}

val networkModule = module {
    single { provideRetrofit(androidContext(), get()) }
    single { provideGithubApi(get()) }
    single { SessionHolder() }
}

private fun provideGithubApi(retrofit: Retrofit) = retrofit.create(GithubAPI::class.java)
private fun provideRetrofit(context: Context, sessionHolder: SessionHolder): Retrofit {

    val baseUrl = "https://api.github.com/"

    val headerAuthorizationInterceptor = Interceptor { chain ->
        var request = chain.request()
        val headers: Headers =
            request.headers.newBuilder().add(
                "Authorization",
                "Bearer ${sessionHolder.githubUserToken}"
            ).build()
        request = request.newBuilder().headers(headers).build()
        chain.proceed(request)
    }
    var httpLoggingInterceptor = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BASIC)

    var cacheInterceptor = Interceptor { chain ->
        var request = chain.request()
        request = if (!hasNetwork(context))
            request.newBuilder().header(
                "Cache-Control",
                "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7
            ).build()
        else
            request
        chain.proceed(request)
    }

    val cacheSize = (5 * 1024 * 1024).toLong()
    val myCache = Cache(context.cacheDir, cacheSize)

    val okHttpClient = OkHttpClient.Builder()
        .cache(myCache)
        .addInterceptor(cacheInterceptor)
        .addInterceptor(httpLoggingInterceptor)
        .addInterceptor(headerAuthorizationInterceptor)
        .build()

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()
}