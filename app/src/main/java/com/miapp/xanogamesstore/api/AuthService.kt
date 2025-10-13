// app/src/main/java/com/miapp/xanogamesstore/api/AuthService.kt
package com.miapp.xanogamesstore.api

import com.miapp.xanogamesstore.model.AuthResponse
import com.miapp.xanogamesstore.model.LoginBody
import com.miapp.xanogamesstore.model.LoginResponse
import com.miapp.xanogamesstore.model.SignupBody
import com.miapp.xanogamesstore.model.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body body: LoginBody): LoginResponse

    @POST("auth/signup")
    suspend fun signup(@Body body: SignupBody): AuthResponse
    @GET("auth/me")
    suspend fun me(): UserDto
}
