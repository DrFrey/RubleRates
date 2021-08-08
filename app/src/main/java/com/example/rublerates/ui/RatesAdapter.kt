package com.example.rublerates.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rublerates.data.Rates
import com.example.rublerates.databinding.RatesListItemBinding

class RatesAdapter : ListAdapter<Rates, RatesAdapter.RatesViewHolder>(RatesDiffUtil()) {
    class RatesViewHolder(private val binding: RatesListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(rates: Rates) {
            binding.rates = rates
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
            RatesListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RatesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}