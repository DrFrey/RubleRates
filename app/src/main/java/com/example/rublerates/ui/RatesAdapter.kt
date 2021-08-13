package com.example.rublerates.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rublerates.data.Rates
import com.example.rublerates.data.RatesRepository
import com.example.rublerates.databinding.RatesListItemBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RatesAdapter(private val repo: RatesRepository) :
    ListAdapter<Rates, RatesAdapter.RatesViewHolder>(RatesDiffUtil()) {
    class RatesViewHolder(
        private val binding: RatesListItemBinding,
        private val repo: RatesRepository
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(rates: Rates) {
            repo.getBankById(rates.bankId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    binding.rates = rates
                    binding.bank = it
                }, {
                    binding.rates = rates
                    Log.d("___", it.message.toString())
                })
            binding.executePendingBindings()
        }
    }

    class RatesDiffUtil : DiffUtil.ItemCallback<Rates>() {
        override fun areItemsTheSame(oldItem: Rates, newItem: Rates): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Rates, newItem: Rates): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatesViewHolder {
        return RatesViewHolder(
            RatesListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            repo
        )
    }

    override fun onBindViewHolder(holder: RatesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}