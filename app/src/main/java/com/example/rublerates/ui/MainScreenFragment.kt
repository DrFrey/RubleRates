package com.example.rublerates.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.rublerates.RatesApplication
import com.example.rublerates.databinding.MainScreenFragmentBinding
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class MainScreenFragment : Fragment() {

    private lateinit var binding: MainScreenFragmentBinding

    private val viewModel: MainScreenViewModel by viewModels {
        MainScreenViewModelFactory(RatesApplication.repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainScreenFragmentBinding.inflate(inflater)

        val adapter = RatesAdapter(viewModel.repo)
        binding.ratesListRecyclerview.adapter = adapter

        viewModel.allRates.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

        viewModel.error.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                Snackbar.make(binding.baseLayout, it, Snackbar.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        })

        return binding.root
    }
}