package com.malenst.sovkom.ui.task

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.malenst.sovkom.R
import com.malenst.sovkom.model.Task
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Locale

class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvTaskDescription: TextView = itemView.findViewById(R.id.tvTaskDescription)
    private val tvTaskTime: TextView = itemView.findViewById(R.id.tvTaskTime)
    private val tvTaskLocation: TextView = itemView.findViewById(R.id.tvTaskLocation)
    private val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    private val outputFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun bind(task: Task) {
        tvTaskDescription.text = task.description
        tvTaskTime.text = "${task.startTime} - ${task.endTime}"

        // Асинхронный запуск для получения адреса
        Thread {
            val address = convertCoordinatesToAddress(task.destinationCoordinates)
            itemView.post {
                tvTaskLocation.text = address
            }
        }.start()
    }


    fun convertCoordinatesToAddress(coordinates: String): String {
        val parts = coordinates.split(",")
        if (parts.size != 2) return "Invalid coordinates"

        val latitude = parts[0].trim()
        val longitude = parts[1].trim()
        return requestAddressFromAPI(latitude, longitude)
    }

    fun requestAddressFromAPI(latitude: String, longitude: String): String {
        val apiUrl = "https://nominatim.openstreetmap.org/reverse?format=json&lat=$latitude&lon=$longitude"

        try {
            val url = URL(apiUrl)
            val urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.setRequestProperty("User-Agent", "MyApp") // Замените "MyApp" на название вашего приложения
            urlConnection.connect()

            val statusCode = urlConnection.responseCode
            if (statusCode == HttpURLConnection.HTTP_OK) {
                val inputStream = BufferedReader(InputStreamReader(urlConnection.inputStream))
                val response = inputStream.readText()
                inputStream.close()

                return parseAddressFromJson(response) // Реализуйте parseAddressFromJson
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "Address not found"
    }

    fun parseAddressFromJson(jsonData: String): String {
        val jsonObject = JSONObject(jsonData)
        val address = jsonObject.getJSONObject("address")
        return "${address.optString("road", "Unknown road")}, ${address.optString("city", "Unknown city")}, ${address.optString("country", "Unknown country")}"
    }

}