package com.example.currencyconverter.data.repository

import com.example.currencyconverter.data.models.CurrencyResponse
import com.example.currencyconverter.util.Resource

interface CurrencyRepository {
    suspend fun getRates(base: String): Resource<CurrencyResponse>
}