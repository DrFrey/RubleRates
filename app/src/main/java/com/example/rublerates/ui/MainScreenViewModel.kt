package com.example.rublerates.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rublerates.R
import com.example.rublerates.RatesApplication
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

    fun clearError() {
        _error.value = ""
    }


    init {
        repo.deleteAll()
            .subscribeOn(Schedulers.io())
            .subscribe()
        fetchFreshRates()
    }

    fun fetchFreshRates() {
        if (!isInternetAvailable()) {
            _error.value =
                RatesApplication.instance.applicationContext.getString(R.string.no_internet_error)
        } else {
            repo.refresh()
        }
        getLatestRates()
    }

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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _allRates.value = it
                }, {
                    _error.value = it.message
                })
        )
    }

    override fun onCleared() {
        Log.d("___", "oncleared called")

        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
            Log.d("___", "disposable disposed")
        }
        if (!repo.compositeDisposable.isDisposed) {
            repo.compositeDisposable.clear()
            Log.d("___", "repo disposable disposed")
        }
    }

    private fun isInternetAvailable(): Boolean {
        var result = false
        val context = RatesApplication.instance.applicationContext
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }
                }
            }
        }
        return result
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