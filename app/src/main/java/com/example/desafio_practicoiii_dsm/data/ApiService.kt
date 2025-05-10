package com.example.desafio_practicoiii_dsm.data

import com.example.desafio_practicoiii_dsm.data.model.ApiResponse
import com.example.desafio_practicoiii_dsm.data.model.Recurso
import com.example.desafio_practicoiii_dsm.data.model.SingleResourceResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("list")
    suspend fun getRecursos(): Response<ApiResponse>

    @POST("buscar/{id}")
    suspend fun getRecursoById(@Path("id") id: Int): Response<SingleResourceResponse>

    @POST("agregar")
    suspend fun addRecurso(@Body recurso: Recurso): Response<Recurso>

    @PUT("actualizar/{id}")
    suspend fun updateRecurso(@Path("id") id: Int, @Body recurso: Recurso): Response<Recurso>

    @DELETE("eliminar/{id}")
    suspend fun deleteRecurso(@Path("id") id: Int): Response<Void>

    companion object {
        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.1.8:3001/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}