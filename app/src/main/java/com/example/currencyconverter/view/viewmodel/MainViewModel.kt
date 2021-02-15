package com.example.currencyconverter.view.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.data.models.Rates
import com.example.currencyconverter.data.repository.CurrencyRepository
import com.example.currencyconverter.util.DispatcherProvider
import com.example.currencyconverter.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.round

class MainViewModel @ViewModelInject constructor(
    private val repository: CurrencyRepository,
    private val dispatcher: DispatcherProvider
) : ViewModel() {

    sealed class CurrencyEvent{
        class Success(val resultText: String): CurrencyEvent()
        class Failure(val errorText: String): CurrencyEvent()
        object Loading: CurrencyEvent()
        object Empty: CurrencyEvent()
    }

    private val conversion = MutableStateFlow<CurrencyEvent>(CurrencyEvent.Empty)
    val liveConversion: StateFlow<CurrencyEvent> = conversion

    fun convert(
        amountStr: String,
        fromCurrency: String,
        toCurrency: String
    ) {
        val fromAmmount = amountStr.toFloatOrNull()
        if(fromAmmount == null){
            conversion.value = CurrencyEvent.Failure("Ammount is null or empty")
            return
        }

        viewModelScope.launch(dispatcher.io){
            conversion.value = CurrencyEvent.Loading
            when( val ratesResponse = repository.getRates(fromCurrency)){
                is Resource.Error -> conversion.value = CurrencyEvent.Failure(ratesResponse.message ?: "an error has occurred, please try again later")
                is Resource.Success ->{
                    val rates = ratesResponse.data?.rates
                    val rate = rates?.let { getRateForCurrency(toCurrency, it) }
                    if (rate == null){
                        conversion.value = CurrencyEvent.Failure("Unexpected error")
                    }else{
                        val convertedCurrency = convertMoney(fromAmmount, rate)
                        conversion.value = CurrencyEvent.Success(
                            "$fromAmmount $fromCurrency = $convertedCurrency $toCurrency"
                        )
                    }
                }
            }
        }
    }

    private fun convertMoney(fromAmount: Float, rate: Double): Double {
        return round(fromAmount * rate * 100) / 100
    }

    private fun getRateForCurrency(currency: String, rates: Rates) = when (currency) {
        "CAD" -> rates.cAD
        "HKD" -> rates.hKD
        "ISK" -> rates.iSK
        "EUR" -> rates.eUR
        "PHP" -> rates.pHP
        "DKK" -> rates.dKK
        "HUF" -> rates.hUF
        "CZK" -> rates.cZK
        "AUD" -> rates.aUD
        "RON" -> rates.rON
        "SEK" -> rates.sEK
        "IDR" -> rates.iDR
        "INR" -> rates.iNR
        "BRL" -> rates.bRL
        "RUB" -> rates.rUB
        "HRK" -> rates.hRK
        "JPY" -> rates.jPY
        "THB" -> rates.tHB
        "CHF" -> rates.cHF
        "SGD" -> rates.sGD
        "PLN" -> rates.pLN
        "BGN" -> rates.bGN
        "CNY" -> rates.cNY
        "NOK" -> rates.nOK
        "NZD" -> rates.nZD
        "ZAR" -> rates.zAR
        "USD" -> rates.uSD
        "MXN" -> rates.mXN
        "ILS" -> rates.iLS
        "GBP" -> rates.gBP
        "KRW" -> rates.kRW
        "MYR" -> rates.mYR
        else -> null
    }
}