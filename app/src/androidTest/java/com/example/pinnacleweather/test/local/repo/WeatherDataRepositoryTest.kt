package com.example.pinnacleweather.test.local.repo

import com.example.pinnacleweather.data.local.LocalWeatherData
import com.example.pinnacleweather.data.network.Main
import com.example.pinnacleweather.data.network.OpenWeatherDataResponse
import com.example.pinnacleweather.data.network.Weather
import com.example.pinnacleweather.data.network.WeatherNetworkService
import com.example.pinnacleweather.data.repo.WeatherDataRepositoryImpl
import com.example.pinnacleweather.mock.data.local.MockWeatherDataDao
import com.example.pinnacleweather.mock.data.network.MockOpenWeatherMapAPI
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.util.UUID

class WeatherDataRepositoryTest {

    private val localWeatherData = LocalWeatherData(
        id = UUID.randomUUID().toString(),
        city = "New York",
        lat = 70.3,
        lon = 40.7,
        temperature = 60.1,
        weatherIcon = "",
        weatherMain = "Rain",
        weatherDescription = "Mostly Cloudy",
        lastUpdated = 0
    )

    private lateinit var mockLocalWeatherDao: MockWeatherDataDao
    private lateinit var mockOpenWeatherMapAPI: MockOpenWeatherMapAPI
    private lateinit var weatherNetworkService: WeatherNetworkService

    @OptIn(ExperimentalCoroutinesApi::class)
    private var testDispatcher = UnconfinedTestDispatcher()

    private lateinit var weatherDataRepository: WeatherDataRepositoryImpl

    @Before
    fun setup() {
        mockLocalWeatherDao = MockWeatherDataDao()
        mockOpenWeatherMapAPI = MockOpenWeatherMapAPI()
        weatherNetworkService = WeatherNetworkService(mockOpenWeatherMapAPI)

        weatherDataRepository = WeatherDataRepositoryImpl(
            localWeatherDataDao = mockLocalWeatherDao,
            weatherNetworkService = weatherNetworkService,
            dispatcher = testDispatcher
        )
    }

    @Test
    fun test_fetchMostRecentWeatherDataIfExists_successful_request_and_persistence() = runTest{
        val testCity = ""
        val testLat = 50.0
        val testLon = 30.0
        val testTemp = 100.0
        val testIcon = "testIcon"
        val testWeatherMain = "testWeatherMain"
        val testWeatherDisc = "testWeatherDisc"

        mockOneEntryInLocalWeatherDataDatabase(
            id = UUID.randomUUID().toString(),
            city = "",
            lat = testLat,
            lon = testLon,
            temperature = 0.0,
            weatherIcon = "",
            weatherMain = "",
            weatherDescription = "",
            lastUpdated = 0
        )

        mockOpenWeatherMapAPI.openWeatherDataResponse = Response.success(
            OpenWeatherDataResponse(
                lat = testLat,
                lon = testLon,
                main = Main(
                    temp = testTemp
                ),
                mutableListOf(
                    Weather(
                        id = 0,
                        main = testWeatherMain,
                        description = testWeatherDisc,
                        icon = testIcon
                    )
                )
            )
        )
        weatherDataRepository.fetchMostRecentWeatherDataIfExists()

        val fetchedWeatherData = mockLocalWeatherDao.getFirst()
        assertEquals(1,mockLocalWeatherDao.getAll().size)
        assertNotNull(fetchedWeatherData)
        assertEquals(testCity,fetchedWeatherData.city)
        assertEquals(testLat,fetchedWeatherData.lat)
        assertEquals(testLon,fetchedWeatherData.lon)
        assertEquals(testTemp,fetchedWeatherData.temperature,)
        assertEquals(weatherNetworkService.iconUrl(testIcon),fetchedWeatherData.weatherIcon)
        assertEquals(testWeatherDisc,fetchedWeatherData.weatherDescription)
    }

    @Test
    fun test_fetchMostRecentWeatherDataIfExists_api_error() = runTest {
        mockOneEntryInLocalWeatherDataDatabase(
            id = UUID.randomUUID().toString(),
            city = "",
            lat = 0.0,
            lon = 0.0,
            temperature = 0.0,
            weatherIcon = "",
            weatherMain = "",
            weatherDescription = "",
            lastUpdated = 0
        )

        val testErrorBody = "testErrorBody"
        mockOpenWeatherMapAPI.openWeatherDataResponse = Response.error(400, ResponseBody.create(
            "application/json".toMediaTypeOrNull(),
            testErrorBody
        ))

        var errorMessage = ""
        try {
            weatherDataRepository.fetchMostRecentWeatherDataIfExists()
        }
        catch (e: Exception) {
            errorMessage = e.message.toString()
        }
        assert(errorMessage != "") // This is a bit of a hack and should be refactored to catch the specific error
                                    //  not any Exception
    }

    private suspend fun mockOneEntryInLocalWeatherDataDatabase(id: String, city: String, lat: Double, lon: Double, temperature: Double, weatherIcon: String, weatherMain: String, weatherDescription: String, lastUpdated: Long) {
        mockLocalWeatherDao.deleteAll()
        mockLocalWeatherDao.upsert(LocalWeatherData(
            id = id,
            city = city,
            lat = lat,
            lon = lon,
            temperature = temperature,
            weatherIcon = weatherIcon,
            weatherMain = weatherMain,
            weatherDescription = weatherDescription,
            lastUpdated = lastUpdated
        ))
    }
}