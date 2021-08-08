package com.example.rublerates.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rublerates.data.Bank
import com.example.rublerates.data.Rates
import com.example.rublerates.data.RatesRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.IllegalArgumentException

class MainScreenViewModel(val repo: RatesRepository) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _allRates = MutableLiveData<List<Rates>>()
    val allRates: LiveData<List<Rates>>
        get() = _allRates

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    fun getLatestRates() {
        compositeDisposable.add(
            repo.allRates
                .subscribeOn(Schedulers.io())
                .map { r ->
                    val newList = mutableMapOf<Int, Rates>()
                    for (el in r) {
                        if (el.bankId in newList.keys) {
                            if (newList[el.bankId]!!.date < el.date) {
                                newList[el.bankId] = el
                            }
                        } else {
                            newList[el.bankId] = el
                        }
                    }
                    newList.values.toList()
                }
                .map { l ->
                    var banks = listOf<Bank>()
                    repo.allBanks
                        .subscribeOn(Schedulers.io())
                        .subscribe {
                            banks = it
                        }
                    for (el in l) {

                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _allRates.value = it
                }, {
                    _error.value = it.message
                })
        )
    }


    init {
        fetchFreshRates()
    }

    fun fetchFreshRates() {
        compositeDisposable.addAll(
            repo.sberRates,
            repo.alfaRates,
            repo.gazRates,
            repo.openRates,
            repo.mkbRates,
            repo.rshbRates,
            repo.vtbRates,
            repo.raifRates
        )
        getLatestRates()
    }

    override fun onCleared() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
            Log.d("___", "disposable disposed")
        }
    }

}

class MainScreenViewModelFactory(private val repo: RatesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainScreenViewModel::class.java)) {
            return MainScreenViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown model class")
    }

}