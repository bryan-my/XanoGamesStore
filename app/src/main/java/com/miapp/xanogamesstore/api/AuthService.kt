package com.miapp.xanogamesstore.api

import com.miapp.xanogamesstore.model.AuthResponse
import com.miapp.xanogamesstore.model.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class LoginBody(
    val email: String,
    val password: String
)

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body body: LoginBody): AuthResponse

    @GET("auth/me")
    suspend fun me(): UserDto
}