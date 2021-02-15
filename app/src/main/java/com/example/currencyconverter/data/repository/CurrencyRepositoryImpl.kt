package com.example.currencyconverter.data.repository

import com.example.currencyconverter.data.models.CurrencyResponse
import com.example.currencyconverter.data.network.CurrencyAPI
import com.example.currencyconverter.util.Resource
import okhttp3.internal.notify
import java.lang.Exception
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    private val api: CurrencyAPI
): CurrencyRepository {
    override suspend fun getRates(base: String): Resource<CurrencyResponse> {
        return try {
            val response = api.getRates(base)
            val result = response.body()
            if (response.isSuccessful && result != null){
                Resource.Success(result)
            }else{
                Resource.Error(response.message())
            }
        }catch (e: Exception){
            Resource.Error(e.message ?: "An error ocurred")
        }
    }
}