package com.jeevansatya.githubsearcher.Interface

import com.jeevansatya.githubsearcher.Model.RepoResponse
import com.jeevansatya.githubsearcher.Model.UserDetailsResponse
import com.jeevansatya.githubsearcher.Model.UserListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {
    @GET("users")
    fun getUsersList(@Header("Authorization") token:String): Call<List<UserListResponse>>

    @GET("users/{name}/repos")
    fun getRepoList(@Path("name") name:String, @Header("Authorization") token:String ): Call<List<RepoResponse>>

    @GET("users/{name}")
    fun getUserDetails(@Path("name") name:String, @Header("Authorization") token:String ): Call<UserDetailsResponse>
}