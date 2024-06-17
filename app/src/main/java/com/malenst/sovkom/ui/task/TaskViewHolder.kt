package com.malenst.sovkom.ui.task

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.malenst.sovkom.R
import com.malenst.sovkom.model.Task
import com.malenst.sovkom.ui.map.TaskWithMapActivity
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvTaskDescription: TextView = itemView.findViewById(R.id.tvTaskDescription)
    private val tvTaskTime: TextView = itemView.findViewById(R.id.tvTaskTime)
    private val tvTaskLocation: TextView = itemView.findViewById(R.id.tvTaskLocation)
    private val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    private val outputFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun bind(task: Task) {
        tvTaskDescription.text = task.status

        try {
            val parsedStartTime = inputFormatter.parse(task.startTime) ?: throw IllegalArgumentException("Invalid start time")
            val parsedEndTime = inputFormatter.parse(task.endTime) ?: throw IllegalArgumentException("Invalid end time")

            val formattedStartTime = outputFormatter.format(parsedStartTime)
            val formattedEndTime = outputFormatter.format(parsedEndTime)

            tvTaskTime.text = "$formattedStartTime - $formattedEndTime"
        } catch (e: Exception) {
            tvTaskTime.text = "Time format error"
            Log.e("TaskViewHolder", "Error formatting time", e)
        }

        Thread {
            val address = convertCoordinatesToAddress(task.destinationCoordinates)
            itemView.post {
                tvTaskLocation.text = address.replace(", Россия", "")
            }
        }.start()

        itemView.setOnClickListener {
            val context = itemView.context
            val intent = Intent(context, TaskWithMapActivity::class.java).apply {
                putExtra("TASK_ID", task.id)
                putExtra("TASK_DESCRIPTION", task.description)
                putExtra("TASK_START_TIME", task.startTime)
                putExtra("TASK_END_TIME", task.endTime)
                putExtra("TASK_NAME", task.status)
                putExtra("TASK_DESTINATION", task.destinationCoordinates)
                putExtra("TASK_DEPARTURE", task.departureCoordinates)
                putExtra("TASK_DATE", task.assignmentDate)
                putExtra("TASK_CATEGORY", task.category)
            }
            context.startActivity(intent)
        }

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
            urlConnection.setRequestProperty("User-Agent", "MyApp")
            urlConnection.connect()

            val statusCode = urlConnection.responseCode
            if (statusCode == HttpURLConnection.HTTP_OK) {
                val inputStream = BufferedReader(InputStreamReader(urlConnection.inputStream))
                val response = inputStream.readText()
                inputStream.close()

                return parseAddressFromJson(response)
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