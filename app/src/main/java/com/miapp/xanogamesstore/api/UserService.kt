// app/src/main/java/com/miapp/xanogamesstore/api/UserService.kt
package com.miapp.xanogamesstore.api

import com.miapp.xanogamesstore.model.UserDto
import com.miapp.xanogamesstore.model.UpdateUserBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.DELETE

interface UserService {
    @GET("user/{id}")
    suspend fun getUser(@Path("id") id: Int): UserDto

    @PUT("user/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body body: UpdateUserBody
    ): UserDto


    @GET("user")
    suspend fun getUsers(): List<UserDto>


    @POST("user")
    suspend fun createUser(@Body body: com.miapp.xanogamesstore.model.SignupBody): UserDto
    @DELETE("user/{id}")
    suspend fun deleteUser(@Path("id") id: Int)
}
