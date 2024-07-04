
package com.example.wheat

import android.annotation.SuppressLint
import com.example.wheat.ApiinterFace
import com.example.wheat.Wheat
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView // Ensure this import is correct
import com.example.wheat.databinding.ActivityMainBinding
import com.google.android.material.color.utilities.ViewingConditions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Kolkata")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }


    private fun fetchWeatherData(location: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiinterFace::class.java)

        val response = retrofit.getweatherData(location, "620e26b0bb9b5cdd3a6b1f67400a8933", "metric")

        response.enqueue(object : Callback<Wheat> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<Wheat>, response: Response<Wheat>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val conditions = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    val maxTemperature = responseBody.main.temp_max
                    val minTemperature = responseBody.main.temp_min

                    binding.Temp.text = "$temperature °C"
                    binding.CondDay.text = conditions
                    binding.Max.text = "Max Temp: $maxTemperature °C"
                    binding.Min.text = "Min Temp: $minTemperature °C"
                    binding.humid.text = "$humidity %"
                    binding.speed.text = "$windSpeed m/s"
                    binding.level.text = "$seaLevel hPa"
                    binding.con.text = conditions
                    binding.Day.text = dayName(System.currentTimeMillis())
                    binding.Date.text = date()
                    binding.Location.text = location
                    binding.rise.text = "${formatTime(sunRise)}"
                    binding.set.text = "${formatTime(sunSet)}"
                    imagesConditions(conditions)
                } else {
                    Log.d("MainActivity", "Response failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Wheat>, t: Throwable) {
                Log.d("MainActivity", "Request failed: ${t.message}")
            }
        })
    }

    private fun imagesConditions(conditions: String) {
        when (conditions){
            "Partly Clouds","Clouds","Overcast","Mist","Foggy","Haze"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Clear Sky","Sunny","Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Light Rain","Drizzle","Moderate Rain","Shower","Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }

    private fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }
}

private fun SearchView.setOnQueryTextListener(onQueryTextListener: SearchView.OnQueryTextListener) {

}
