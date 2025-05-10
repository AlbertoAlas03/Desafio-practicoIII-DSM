package com.example.desafio_practicoiii_dsm.data

import com.example.desafio_practicoiii_dsm.data.model.ApiResponse
import retrofit2.Response

class Repository {
    private val apiService = ApiService.create()

    suspend fun getRecursos(): Response<ApiResponse> {
        return apiService.getRecursos()
    }
}