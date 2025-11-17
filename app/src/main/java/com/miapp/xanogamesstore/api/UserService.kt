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

    /**
     * Devuelve la lista completa de usuarios. Este endpoint existe en Xano con
     * permisos de administrador. En un entorno real, debe protegerse mediante
     * autenticación y roles.
     */
    @GET("user")
    suspend fun getUsers(): List<UserDto>

    /**
     * Crea un nuevo usuario. Este método requiere un cuerpo con los campos
     * necesarios para la creación (generalmente email, password y opcionalmente
     * nombre). Si necesitas establecer el rol, Xano puede permitir un
     * parámetro adicional como parte del cuerpo.
     */
    @POST("user")
    suspend fun createUser(@Body body: com.miapp.xanogamesstore.model.SignupBody): UserDto

    /**
     * Elimina un usuario específico por su ID. Usa con precaución.
     */
    @DELETE("user/{id}")
    suspend fun deleteUser(@Path("id") id: Int)
}
