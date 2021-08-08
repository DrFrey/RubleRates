package com.example.rublerates.utils

import com.example.rublerates.data.AlfaRates
import com.example.rublerates.data.GazRates
import com.example.rublerates.data.SberRates
import com.example.rublerates.data.VtbRates
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

private const val BASE_SBER = "https://www.sberbank.ru/"
private const val BASE_ALFA = "https://alfabank.ru/"
private const val BASE_OPEN = "https://www.open.ru/"
private const val BASE_RAIF = "http://www.raiffeisen.ru/"
private const val BASE_VTB = "https://www.vtb.ru/"
private const val BASE_GZPB = "https://www.gazprombank.ru/"
private const val BASE_RSHB = "https://www.rshb.ru/"
private const val BASE_MKB = "https://mkb.ru/"
//private const val USER_AGENT = "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:89.0) Gecko/20100101 Firefox/89.0"

val interceptor = HttpLoggingInterceptor()
val client =
    OkHttpClient.Builder()
        .addInterceptor(interceptor.setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

private fun getUnsafeOkHttpClient(): OkHttpClient? {
    return try {
        // Create a trust manager that does not validate certificate chains
        val trustAllCerts = arrayOf<TrustManager>(
            object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate?>? {
                    return arrayOf()
                }
            }
        )

        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory = sslContext.socketFactory
        val trustManagerFactory: TrustManagerFactory =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(null as KeyStore?)
        val trustManagers: Array<TrustManager> =
            trustManagerFactory.trustManagers
        check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
            "Unexpected default trust managers:" + trustManagers.contentToString()
        }

        val trustManager =
            trustManagers[0] as X509TrustManager

        val builder = OkHttpClient.Builder()
        builder.sslSocketFactory(sslSocketFactory, trustManager)
        builder.hostnameVerifier { _, _ -> true }
        builder.build()
    } catch (e: Exception) {
        throw RuntimeException(e)
    }
}

// Banks that return rates info in JSON
// Сбер
val retrofitSber = Retrofit.Builder()
    .baseUrl(BASE_SBER)
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .build()

interface SberRatesService {
    @GET("proxy/services/rates/public/actual/")
    fun getSberRates(
        @Query("rateType")
        rateType: String = "ERNP-2",
        @Query("isoCodes[]")
        isoCodes1: String = "EUR",
        @Query("isoCodes[]")
        isoCodes2: String = "USD",
        @Query("regionId")
        regionId: String = "038"
    ): Observable<SberRates>
}

object SberApi {
    val retrofitSberService: SberRatesService by lazy {
        retrofitSber.create(SberRatesService::class.java)
    }
}

// Альфа
val retrofitAlfa = Retrofit.Builder()
    .baseUrl(BASE_ALFA)
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .build()

interface AlfaRatesService {
    @GET("ext-json/0.2/exchange/cash/")
    fun getAlfaRates(): Single<AlfaRates>
}

object AlfaApi {
    val retrofitAlfaService: AlfaRatesService by lazy {
        retrofitAlfa.create(AlfaRatesService::class.java)
    }
}

// ВТБ
val retrofitVtb = Retrofit.Builder()
    .baseUrl(BASE_VTB)
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .build()

interface VtbRatesService {
    @GET("api/currency-exchange/table-info/")
    fun getVtbRates(
        @Query("contextItemId")
        contextItemId: String = "{C5471052-2291-4AFD-9C2D-1DBC40A4769D}",
        @Query("conversionPlace")
        conversionPlace: Int = 1,
        @Query("conversionType")
        conversionType: Int = 1,
        @Query("renderingId")
        renderingId: String = "ede2e4d0-eb6b-4730-857b-06fd4975c06b",
        @Query("renderingParams")
        renderingParams: String = "LegalStatus__{F2A32685-E909-44E8-A954-1E206D92FFF8};IsFromRuble__1;CardMaxPeriodDays__5;CardRecordsOnPage__5;ConditionsUrl__%2Fpersonal%2Fplatezhi-i-perevody%2Fobmen-valjuty%2Fspezkassy%2F;Multiply100JPYand10SEK__1"
    ): Single<VtbRates>
}

object VtbApi {
    val retrofitVtbService: VtbRatesService by lazy {
        retrofitVtb.create(VtbRatesService::class.java)
    }
}

// Газпромбанк
val retrofitGaz = Retrofit.Builder()
    .baseUrl(BASE_GZPB)
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .build()

interface GazRatesService {
    @GET("rest/exchange/rate/")
    fun getGazRates(
        @Query("cityId")
        cityId: Int = 617
    ): Single<GazRates>
}

object GazApi {
    val retrofitGazService: GazRatesService by lazy {
        retrofitGaz.create(GazRatesService::class.java)
    }
}

// Here and below we need to parse HTML to get the rates
// Открытие
val retrofitOpen = Retrofit.Builder()
    .baseUrl(BASE_OPEN)
    .client(client)
    .addConverterFactory(ScalarsConverterFactory.create())
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .build()

interface OpenRatesService {
    @GET("exchange-person/")
    fun getOpenRates(): Single<String>
}

object OpenApi {
    val retrofitOpenService: OpenRatesService by lazy {
        retrofitOpen.create(OpenRatesService::class.java)
    }
}

// Райффайзен
val retrofitRaif = Retrofit.Builder()
    .baseUrl(BASE_RAIF)
    .client(getUnsafeOkHttpClient() ?: client)
    .addConverterFactory(ScalarsConverterFactory.create())
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .build()

interface RaifRatesService {
    @GET("http://www.raiffeisen.ru/")
    fun getRaifRates(): Single<String>
}

object RaifApi {
    val retrofitRaifService: RaifRatesService by lazy {
        retrofitRaif.create(RaifRatesService::class.java)
    }
}

// Россельхоз
val retrofitRshb = Retrofit.Builder()
    .baseUrl(BASE_RSHB)
    .client(client)
    .addConverterFactory(ScalarsConverterFactory.create())
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .build()

interface RshbRatesService {
    @GET("branches/moscow/currency-rates/")
    fun getRshbRates(): Single<String>
}

object RshbApi {
    val retrofitRshbService: RshbRatesService by lazy {
        retrofitRshb.create(RshbRatesService::class.java)
    }
}

// МКБ
val retrofitMkb = Retrofit.Builder()
    .baseUrl(BASE_MKB)
    .client(client)
    .addConverterFactory(ScalarsConverterFactory.create())
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .build()

interface MkbRatesService {
    @GET("https://mkb.ru/")
    fun getMkbRates(): Single<String>
}

object MkbApi {
    val retrofitMkbService: MkbRatesService by lazy {
        retrofitMkb.create(MkbRatesService::class.java)
    }
}
