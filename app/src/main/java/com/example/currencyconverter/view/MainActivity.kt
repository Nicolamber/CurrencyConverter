package com.example.currencyconverter.view

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.currencyconverter.R
import com.example.currencyconverter.databinding.ActivityMainBinding
import com.example.currencyconverter.view.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initTexts()

        binding.convertMoneyButton. setOnClickListener {
            viewModel.convert(binding.moneyToConvert.text.toString(),
                binding.fromCurrencySpinner.selectedItem.toString(),
                binding.toCurrencySpinner.selectedItem.toString()
            )
        }

        lifecycleScope.launchWhenStarted {
            viewModel.liveConversion.collect { event ->
                when(event) {
                    is MainViewModel.CurrencyEvent.Success -> {
                        binding.currencyProgressBar.isVisible = false
                        binding.conversionResult.setTextColor(Color.BLACK)
                        binding.conversionResult.text = event.resultText
                    }

                    is MainViewModel.CurrencyEvent.Failure -> {
                        binding.currencyProgressBar.isVisible = false
                        binding.conversionResult.setTextColor(Color.RED)
                        binding.conversionResult.text = event.errorText
                    }

                    is MainViewModel.CurrencyEvent.Loading -> {
                        binding.currencyProgressBar.isVisible = true
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun initTexts() {
        binding.converterTitle.text = getString(R.string.converter_title)
        binding.fromMoney.text = getString(R.string.from_text)
        binding.toMoney.text = getString(R.string.to_text)
        binding.moneyToConvert.hint = getString(R.string.amount_text)
        binding.convertMoneyButton.text = getString(R.string.convert_button_text)

    }
}