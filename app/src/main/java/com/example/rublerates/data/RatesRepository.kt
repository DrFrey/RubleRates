package com.example.rublerates.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.rublerates.utils.*
import com.google.gson.Gson
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.collect
import org.jsoup.Jsoup
import java.lang.Exception
import java.time.*
import java.time.format.DateTimeFormatter

class RatesRepository(
    private val ratesDao: RatesDao,
    private val bankDao: BankDao,
) {

    // Working with Room
    val allRates = ratesDao.getAllRates()

    val allBanks = bankDao.getAllBanks()

    fun getBankById(id: Int): Single<Bank> {
        return bankDao.getBankById(id)
    }

    fun insertRates(rates: Rates) {
        ratesDao.insertRates(rates)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun deleteAll() {
        ratesDao.deleteAll()
    }

    // Retrieving data from the Internet

    fun getCurrentDateTime(): Long {
        return ZonedDateTime.of(
            LocalDateTime.now(),
            ZoneId.systemDefault()
        ).toInstant().toEpochMilli()
    }

    val sberRates = SberApi.retrofitSberService.getSberRates()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            Log.d("___", "processSberRates activated: ${it}")
            var bankId: Int?
            bankDao.getBankByName("Sberbank")
                .subscribeOn(Schedulers.io())
                .subscribe(object : Consumer<Bank> {
                    override fun accept(t: Bank?) {
                        bankId = t?.id
                        val rates = Rates(
                            id = 0,
                            date = it.uSD?.startDateTime ?: getCurrentDateTime(),
                            bankId = bankId ?: -1,
                            bankEurBuy = it.eUR?.rateList?.get(0)?.rateBuy ?: 0.0,
                            bankEurSell = it.eUR?.rateList?.get(0)?.rateSell ?: 0.0,
                            bankUsdBuy = it.uSD?.rateList?.get(0)?.rateBuy ?: 0.0,
                            bankUsdSell = it.uSD?.rateList?.get(0)?.rateSell ?: 0.0
                        )
                        Log.d("___", rates.toString())
                        insertRates(rates)
                    }
                }, object : Consumer<Throwable> {
                    override fun accept(t: Throwable?) {
                        Log.d("___", t?.message.toString())
                    }
                })
        }, {
            handleError(it)
        })

    val alfaRates = AlfaApi.retrofitAlfaService.getAlfaRates()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            Log.d("___", "processAlfaRates activated: ${it}")

            var bankId: Int?
            bankDao.getBankByName("Alfabank")
                .subscribeOn(Schedulers.io())
                .subscribe(object : Consumer<Bank> {
                    override fun accept(t: Bank?) {
                        bankId = t?.id
                        val stringDateTime = it.response?.data?.usd?.get(0)?.date
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val rates = Rates(
                            id = 0,
                            date = if (!stringDateTime.isNullOrEmpty()) ZonedDateTime.of(
                                LocalDateTime.parse(stringDateTime, formatter),
                                ZoneId.systemDefault()
                            ).toInstant().toEpochMilli() else getCurrentDateTime(),
                            bankId = bankId ?: -1,
                            bankEurBuy = it.response?.data?.eur?.get(0)?.value ?: 0.0,
                            bankEurSell = it.response?.data?.eur?.get(1)?.value ?: 0.0,
                            bankUsdBuy = it.response?.data?.usd?.get(0)?.value ?: 0.0,
                            bankUsdSell = it.response?.data?.usd?.get(1)?.value ?: 0.0
                        )
                        Log.d("___", rates.toString())
                        insertRates(rates)
                    }
                }, object : Consumer<Throwable> {
                    override fun accept(t: Throwable?) {
                        Log.d("___", t?.message.toString())
                    }
                })
        }, {
            handleError(it)
        })

    val vtbRates = VtbApi.retrofitVtbService.getVtbRates()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            Log.d("___", "processVtbRates activated: ${it}")

            var bankId: Int?
            bankDao.getBankByName("VTB")
                .subscribeOn(Schedulers.io())
                .subscribe(object : Consumer<Bank> {
                    override fun accept(t: Bank?) {
                        bankId = t?.id
                        val stringDateTime = it.dateFrom
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                        val rates = Rates(
                            id = 0,
                            date = if (!stringDateTime.isNullOrEmpty()) ZonedDateTime.of(
                                LocalDateTime.parse(stringDateTime, formatter),
                                ZoneId.systemDefault()
                            ).toInstant().toEpochMilli() else getCurrentDateTime(),
                            bankId = bankId ?: -1,
                            bankEurBuy = it.groupedRates?.get(1)?.moneyRates?.get(0)?.bankBuyAt
                                ?: 0.0,
                            bankEurSell = it.groupedRates?.get(1)?.moneyRates?.get(0)?.bankSellAt
                                ?: 0.0,
                            bankUsdBuy = it.groupedRates?.get(0)?.moneyRates?.get(0)?.bankBuyAt
                                ?: 0.0,
                            bankUsdSell = it.groupedRates?.get(0)?.moneyRates?.get(0)?.bankSellAt
                                ?: 0.0
                        )
                        Log.d("___", rates.toString())
                        insertRates(rates)
                    }
                }, object : Consumer<Throwable> {
                    override fun accept(t: Throwable?) {
                        Log.d("___", t?.message.toString())
                    }
                })
        }, {
            handleError(it)
        })

    val gazRates = GazApi.retrofitGazService.getGazRates()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            Log.d("___", "processGazRates activated: ${it}")

            var bankId: Int?
            bankDao.getBankByName("Gazprombank")
                .subscribeOn(Schedulers.io())
                .subscribe(object : Consumer<Bank> {
                    override fun accept(t: Bank?) {
                        bankId = t?.id
                        val stringDateTime = it.get(1).content?.get(0)?.updated
                        val formatter =
                            DateTimeFormatter.ofPattern("'Курс актуален на 'HH:mm',' dd.MM.yyyy")
                        val rates = Rates(
                            id = 0,
                            date = if (!stringDateTime.isNullOrEmpty()) ZonedDateTime.of(
                                LocalDateTime.parse(stringDateTime, formatter),
                                ZoneId.systemDefault()
                            ).toInstant().toEpochMilli() else getCurrentDateTime(),
                            bankId = bankId ?: -1,
                            bankEurBuy = it.get(1).content?.get(0)?.items?.get(1)?.buy?.toDouble()
                                ?: 0.0,
                            bankEurSell = it.get(1).content?.get(0)?.items?.get(1)?.sell?.toDouble()
                                ?: 0.0,
                            bankUsdBuy = it.get(1).content?.get(0)?.items?.get(0)?.buy?.toDouble()
                                ?: 0.0,
                            bankUsdSell = it.get(1).content?.get(0)?.items?.get(0)?.sell?.toDouble()
                                ?: 0.0
                        )
                        Log.d("___", rates.toString())
                        insertRates(rates)
                    }
                }, object : Consumer<Throwable> {
                    override fun accept(t: Throwable?) {
                        Log.d("___", t?.message.toString())
                    }
                })
        }, {
            handleError(it)
        })

    val openRates = OpenApi.retrofitOpenService.getOpenRates()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            Log.d("___", "processOpenRates activated: ${it.substring(0, 25)}")

            var bankId: Int?
            bankDao.getBankByName("Otkritie")
                .subscribeOn(Schedulers.io())
                .subscribe(object : Consumer<Bank> {
                    override fun accept(t: Bank?) {
                        bankId = t?.id

                        var jsonString = ""
                        val doc = Jsoup.parse(it)
                        val elements = doc.getElementsByAttribute("data-react-props")
                        for (el in elements) {
                            if (el.attr("data-react-props")
                                    .contains("\"currencyRates\":{\"online\"")
                            ) {
                                jsonString = el.attr("data-react-props")
                            }
                        }

                        if (jsonString.isNotEmpty()) {
                            try {
                                val gson = Gson()
                                val openRates = gson.fromJson(jsonString, OpenRates::class.java)

                                val stringTime =
                                    openRates.currencyRates?.online?.value?.get(1)?.createdAt
                                var longTime: Long = 0
                                stringTime?.let {
                                    val hours = it.substring(0, it.indexOf(':')).toLong()
                                    val mins = it.substring(it.indexOf(':') + 1, it.length).toLong()
                                    longTime = hours * 60 * 60 * 1000 + mins * 60 * 1000
                                }

                                val currentDate =
                                    LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()
                                        .toEpochMilli()

                                val rates = Rates(
                                    id = 0,
                                    date = if (longTime > 0) longTime + currentDate else getCurrentDateTime(),
                                    bankId = bankId ?: -1,
                                    bankEurBuy = openRates.currencyRates?.online?.value?.get(3)?.purchasePrice
                                        ?: 0.0,
                                    bankEurSell = openRates.currencyRates?.online?.value?.get(3)?.salePrice
                                        ?: 0.0,
                                    bankUsdBuy = openRates.currencyRates?.online?.value?.get(1)?.purchasePrice
                                        ?: 0.0,
                                    bankUsdSell = openRates.currencyRates?.online?.value?.get(1)?.salePrice
                                        ?: 0.0
                                )
                                Log.d("___", rates.toString())
                                insertRates(rates)
                            } catch (e: Exception) {
                                Log.d("___", "processOpenRates error: ${e.message.toString()}")
                            }
                        } else Log.d("___", "otkritie json string empty")
                    }
                }, object : Consumer<Throwable> {
                    override fun accept(t: Throwable?) {
                        Log.d("___", t?.message.toString())
                    }
                })
        }, {
            handleError(it)
        })

    val raifRates = RaifApi.retrofitRaifService.getRaifRates()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            Log.d("___", "processRaifRates activated: ${it.substring(0, 25)}")

            var bankId: Int?
            bankDao.getBankByName("Raiffeisen")
                .subscribeOn(Schedulers.io())
                .subscribe(object : Consumer<Bank> {
                    override fun accept(t: Bank?) {
                        bankId = t?.id

                        val doc = Jsoup.parse(it)
                        val ibank = doc.select("div.currency-ibank")
                        val raifRates = ibank.get(0).child(0).select(".cn-data")

                        val stringDateTime =
                            ibank.get(0).select("div.b-block-row__item--error-list").get(0).text()
                        val formatter =
                            DateTimeFormatter.ofPattern("'Действуют с' HH:mm 'МСК' dd.MM.yyyy")
                        val rates = Rates(
                            id = 0,
                            date = if (!stringDateTime.isNullOrEmpty()) ZonedDateTime.of(
                                LocalDateTime.parse(stringDateTime, formatter),
                                ZoneId.systemDefault()
                            ).toInstant().toEpochMilli() else getCurrentDateTime(),
                            bankId = bankId ?: -1,
                            bankEurBuy = raifRates.get(1).text().toDouble()
                                ?: 0.0,
                            bankEurSell = raifRates.get(3).text().toDouble()
                                ?: 0.0,
                            bankUsdBuy = raifRates.get(0).text().toDouble()
                                ?: 0.0,
                            bankUsdSell = raifRates.get(2).text().toDouble()
                                ?: 0.0
                        )
                        Log.d("___", rates.toString())
                        insertRates(rates)
                    }
                }, object : Consumer<Throwable> {
                    override fun accept(t: Throwable?) {
                        Log.d("___", t?.message.toString())
                    }
                })
        }, {
            handleError(it)
        })

    val rshbRates = RshbApi.retrofitRshbService.getRshbRates()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            Log.d("___", "processRshbRates activated: ${it.substring(0, 25)}")

            var bankId: Int?
            bankDao.getBankByName("Rosselkhozbank")
                .subscribeOn(Schedulers.io())
                .subscribe(object : Consumer<Bank> {
                    override fun accept(t: Bank?) {
                        bankId = t?.id

                        val doc = Jsoup.parse(it)
                        val buyRates =
                            doc.getElementsByClass("full").select("tr").get(1).select("td").get(1)
                        val sellRates =
                            doc.getElementsByClass("full").select("tr").get(1).select("td").get(2)

                        val stringDateTime = doc.getElementsByClass("b-rates-title-date").get(0)
                            .text() + ' ' + doc.getElementsByClass("b-rates-title-time").get(0)
                            .text()

                        val formatter =
                            DateTimeFormatter.ofPattern("dd.MM.yyyy 'с' HH:mm")
                        val rates = Rates(
                            id = 0,
                            date = if (!stringDateTime.isNullOrEmpty()) ZonedDateTime.of(
                                LocalDateTime.parse(stringDateTime, formatter),
                                ZoneId.systemDefault()
                            ).toInstant().toEpochMilli() else getCurrentDateTime(),
                            bankId = bankId ?: -1,
                            bankEurBuy = buyRates.select("p").get(2).text().toDouble()
                                ?: 0.0,
                            bankEurSell = buyRates.select("p").get(0).text().toDouble()
                                ?: 0.0,
                            bankUsdBuy = sellRates.select("p").get(2).text().toDouble()
                                ?: 0.0,
                            bankUsdSell = sellRates.select("p").get(0).text().toDouble()
                                ?: 0.0
                        )
                        Log.d("___", rates.toString())
                        insertRates(rates)
                    }
                }, object : Consumer<Throwable> {
                    override fun accept(t: Throwable?) {
                        Log.d("___", t?.message.toString())
                    }
                })
        }, {
            handleError(it)
        })

    val mkbRates = MkbApi.retrofitMkbService.getMkbRates()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            Log.d("___", "processMkbRates activated: ${it.substring(0, 25)}")

            var bankId: Int?
            bankDao.getBankByName("MKB")
                .subscribeOn(Schedulers.io())
                .subscribe(object : Consumer<Bank> {
                    override fun accept(t: Bank?) {
                        bankId = t?.id

                        val doc = Jsoup.parse(it)
                        val mkbRates = doc.getElementsByClass("currency-exchange__table-value")

                        val rates = Rates(
                            id = 0,
                            date = getCurrentDateTime(),
                            bankId = bankId ?: -1,
                            bankEurBuy = mkbRates.get(3).text().toDouble()
                                ?: 0.0,
                            bankEurSell = mkbRates.get(4).text().toDouble()
                                ?: 0.0,
                            bankUsdBuy = mkbRates.get(0).text().toDouble()
                                ?: 0.0,
                            bankUsdSell = mkbRates.get(1).text().toDouble()
                                ?: 0.0
                        )
                        Log.d("___", rates.toString())
                        insertRates(rates)
                    }
                }, object : Consumer<Throwable> {
                    override fun accept(t: Throwable?) {
                        Log.d("___", t?.message.toString())
                    }
                })
        }, {
            handleError(it)
        })

    private fun handleError(t: Throwable) {
        Log.d("___", "handle error activated: ${t.message.toString()}")
    }

}


