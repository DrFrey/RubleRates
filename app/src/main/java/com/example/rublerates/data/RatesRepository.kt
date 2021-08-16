package com.example.rublerates.data

import android.util.Log
import com.example.rublerates.utils.*
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
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

    private fun insertRates(rates: Rates) {
        ratesDao.insertRates(rates)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun deleteAll(): Completable {
        return ratesDao.deleteAll()
    }

    // Retrieving data from the Internet

    private fun getCurrentDateTime(): Long {
        return ZonedDateTime.of(
            LocalDateTime.now(),
            ZoneId.systemDefault()
        ).toInstant().toEpochMilli()
    }

    val compositeDisposable = CompositeDisposable()

    fun refresh() {
        compositeDisposable.addAll(

            SberApi.retrofitSberService.getSberRates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("___", "processSberRates activated: $it")
                    var bankId: Int?
                    compositeDisposable.add(
                        bankDao.getBankByName("Sberbank")
                            .subscribeOn(Schedulers.io())
                            .subscribe({ t ->
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
                            }, { t ->
                                Log.d("___", t?.message.toString())
                            })
                    )
                }, {
                    handleError(it)
                }),


            AlfaApi.retrofitAlfaService.getAlfaRates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("___", "processAlfaRates activated: $it")

                    var bankId: Int?
                    compositeDisposable.add(
                        bankDao.getBankByName("Alfabank")
                            .subscribeOn(Schedulers.io())
                            .subscribe({ t ->
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
                            }, { t ->
                                Log.d("___", t?.message.toString())
                            })
                    )
                }, {
                    handleError(it)
                }),


            VtbApi.retrofitVtbService.getVtbRates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("___", "processVtbRates activated: $it")

                    var bankId: Int?
                    compositeDisposable.add(
                        bankDao.getBankByName("VTB")
                            .subscribeOn(Schedulers.io())
                            .subscribe({ t ->
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
                            }, { t ->
                                Log.d("___", t?.message.toString())
                            })
                    )
                }, {
                    handleError(it)
                }),


            GazApi.retrofitGazService.getGazRates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("___", "processGazRates activated: $it")

                    var bankId: Int?
                    compositeDisposable.add(
                        bankDao.getBankByName("Gazprombank")
                            .subscribeOn(Schedulers.io())
                            .subscribe({ t ->
                                bankId = t?.id
                                val stringDateTime = it[1].content?.get(0)?.updated
                                val formatter =
                                    DateTimeFormatter.ofPattern("'Курс актуален на 'HH:mm',' dd.MM.yyyy")
                                val rates = Rates(
                                    id = 0,
                                    date = if (!stringDateTime.isNullOrEmpty()) ZonedDateTime.of(
                                        LocalDateTime.parse(stringDateTime, formatter),
                                        ZoneId.systemDefault()
                                    ).toInstant().toEpochMilli() else getCurrentDateTime(),
                                    bankId = bankId ?: -1,
                                    bankEurBuy = it[1].content?.get(0)?.items?.get(1)?.buy?.toDouble()
                                        ?: 0.0,
                                    bankEurSell = it[1].content?.get(0)?.items?.get(1)?.sell?.toDouble()
                                        ?: 0.0,
                                    bankUsdBuy = it[1].content?.get(0)?.items?.get(0)?.buy?.toDouble()
                                        ?: 0.0,
                                    bankUsdSell = it[1].content?.get(0)?.items?.get(0)?.sell?.toDouble()
                                        ?: 0.0
                                )
                                Log.d("___", rates.toString())
                                insertRates(rates)
                            }, { t ->
                                Log.d("___", t?.message.toString())
                            })
                    )
                }, {
                    handleError(it)
                }),


            OpenApi.retrofitOpenService.getOpenRates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("___", "processOpenRates activated: ${it.substring(0, 25)}")

                    var bankId: Int?
                    compositeDisposable.add(
                        bankDao.getBankByName("Otkritie")
                            .subscribeOn(Schedulers.io())
                            .subscribe({ t ->
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
                                        val openRates =
                                            gson.fromJson(jsonString, OpenRates::class.java)

                                        val stringTime =
                                            openRates.currencyRates?.online?.value?.get(1)?.createdAt
                                        var longTime: Long = 0
                                        stringTime?.let { s ->
                                            val hours = s.substring(0, s.indexOf(':')).toLong()
                                            val mins =
                                                s.substring(s.indexOf(':') + 1, s.length)
                                                    .toLong()
                                            longTime = hours * 60 * 60 * 1000 + mins * 60 * 1000
                                        }

                                        val currentDate =
                                            LocalDate.now().atStartOfDay(ZoneId.systemDefault())
                                                .toInstant()
                                                .toEpochMilli()

                                        val rates = Rates(
                                            id = 0,
                                            date = if (longTime > 0) longTime + currentDate else getCurrentDateTime(),
                                            bankId = bankId ?: -1,
                                            bankEurBuy = openRates.currencyRates?.online?.value?.get(
                                                3
                                            )?.purchasePrice
                                                ?: 0.0,
                                            bankEurSell = openRates.currencyRates?.online?.value?.get(
                                                3
                                            )?.salePrice
                                                ?: 0.0,
                                            bankUsdBuy = openRates.currencyRates?.online?.value?.get(
                                                1
                                            )?.purchasePrice
                                                ?: 0.0,
                                            bankUsdSell = openRates.currencyRates?.online?.value?.get(
                                                1
                                            )?.salePrice
                                                ?: 0.0
                                        )
                                        Log.d("___", rates.toString())
                                        insertRates(rates)
                                    } catch (e: Exception) {
                                        Log.d(
                                            "___",
                                            "processOpenRates error: ${e.message.toString()}"
                                        )
                                    }
                                } else Log.d("___", "otkritie json string empty")
                            }, { t ->
                                Log.d("___", t?.message.toString())
                            })
                    )
                }, {
                    handleError(it)
                }),


            RaifApi.retrofitRaifService.getRaifRates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("___", "processRaifRates activated: ${it.substring(0, 25)}")

                    var bankId: Int?
                    compositeDisposable.add(
                        bankDao.getBankByName("Raiffeisen")
                            .subscribeOn(Schedulers.io())
                            .subscribe({ t ->
                                bankId = t?.id

                                val doc = Jsoup.parse(it)
                                val ibank = doc.select("div.currency-ibank")
                                val raifRates = ibank[0].child(0).select(".cn-data")

                                val stringDateTime =
                                    ibank[0].select("div.b-block-row__item--error-list")[0]
                                        .text()
                                val formatter =
                                    DateTimeFormatter.ofPattern("'Действуют с' HH:mm 'МСК' dd.MM.yyyy")
                                val rates = Rates(
                                    id = 0,
                                    date = if (!stringDateTime.isNullOrEmpty()) ZonedDateTime.of(
                                        LocalDateTime.parse(stringDateTime, formatter),
                                        ZoneId.systemDefault()
                                    ).toInstant().toEpochMilli() else getCurrentDateTime(),
                                    bankId = bankId ?: -1,
                                    bankEurBuy = raifRates[1].text().toDouble(),
                                    bankEurSell = raifRates[3].text().toDouble(),
                                    bankUsdBuy = raifRates[0].text().toDouble(),
                                    bankUsdSell = raifRates[2].text().toDouble()
                                )
                                Log.d("___", rates.toString())
                                insertRates(rates)
                            }, { t ->
                                Log.d("___", t?.message.toString())
                            })
                    )
                }, {
                    handleError(it)
                }),


            RshbApi.retrofitRshbService.getRshbRates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("___", "processRshbRates activated: ${it.substring(0, 25)}")

                    var bankId: Int?
                    compositeDisposable.add(
                        bankDao.getBankByName("Rosselkhozbank")
                            .subscribeOn(Schedulers.io())
                            .subscribe({ t ->
                                bankId = t?.id

                                val doc = Jsoup.parse(it)
                                val buyRates =
                                    doc.getElementsByClass("full").select("tr")[1].select("td")[1]
                                val sellRates =
                                    doc.getElementsByClass("full").select("tr")[1].select("td")[2]

                                val stringDateTime =
                                    doc.getElementsByClass("b-rates-title-date")[0]
                                        .text() + ' ' + doc.getElementsByClass("b-rates-title-time")[0]
                                        .text()

                                val formatter =
                                    DateTimeFormatter.ofPattern("dd.MM.yyyy 'с' HH:mm")
                                val rates = Rates(
                                    id = 0,
                                    date = if (stringDateTime.isNotEmpty()) ZonedDateTime.of(
                                        LocalDateTime.parse(stringDateTime, formatter),
                                        ZoneId.systemDefault()
                                    ).toInstant().toEpochMilli() else getCurrentDateTime(),
                                    bankId = bankId ?: -1,
                                    bankEurBuy = buyRates.select("p")[2].text().toDouble(),
                                    bankEurSell = sellRates.select("p")[2].text().toDouble(),
                                    bankUsdBuy = buyRates.select("p")[0].text().toDouble(),
                                    bankUsdSell = sellRates.select("p")[0].text().toDouble()
                                )
                                Log.d("___", rates.toString())
                                insertRates(rates)
                            }, { t ->
                                Log.d("___", t?.message.toString())
                            })
                    )
                }, {
                    handleError(it)
                }),


            MkbApi.retrofitMkbService.getMkbRates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("___", "processMkbRates activated: ${it.substring(0, 25)}")

                    var bankId: Int?
                    compositeDisposable.add(
                        bankDao.getBankByName("MKB")
                            .subscribeOn(Schedulers.io())
                            .subscribe({ t ->
                                bankId = t?.id

                                val doc = Jsoup.parse(it)
                                val mkbRates =
                                    doc.getElementsByClass("currency-exchange__table-value")

                                val rates = Rates(
                                    id = 0,
                                    date = getCurrentDateTime(),
                                    bankId = bankId ?: -1,
                                    bankEurBuy = mkbRates[3].text().toDouble(),
                                    bankEurSell = mkbRates[4].text().toDouble(),
                                    bankUsdBuy = mkbRates[0].text().toDouble(),
                                    bankUsdSell = mkbRates[1].text().toDouble()
                                )
                                Log.d("___", rates.toString())
                                insertRates(rates)
                            }, { t ->
                                Log.d("___", t?.message.toString())
                            })
                    )
                }, {
                    handleError(it)
                })
        )
    }

    private fun handleError(t: Throwable) {
        Log.d("___", "handle error activated: ${t.message.toString()}")
    }

}


