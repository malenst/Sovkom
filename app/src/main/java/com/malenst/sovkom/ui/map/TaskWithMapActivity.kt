package com.malenst.sovkom.ui.map

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.malenst.sovkom.R
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Locale


class TaskWithMapActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private lateinit var pdfContainer: LinearLayout
    private lateinit var btnOpenNavigator: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        setContentView(R.layout.activity_task_with_map)

        map = findViewById(R.id.map)
        map.setBuiltInZoomControls(true)
        map.setMultiTouchControls(true)
        val mapController = map.controller
        mapController.setZoom(13.0)

        val taskDeparture = intent.getStringExtra("TASK_DEPARTURE") ?: ""
        val taskDestination = intent.getStringExtra("TASK_DESTINATION") ?: ""

        val startCoords = taskDeparture.split(",").map { it.trim().toDouble() }
        val endCoords = taskDestination.split(",").map { it.trim().toDouble() }

        val startPoint = GeoPoint(startCoords[0], startCoords[1])
        val endPoint = GeoPoint(endCoords[0], endCoords[1])

        mapController.setCenter(startPoint)

        val startMarker = Marker(map)
        startMarker.position = startPoint
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        startMarker.title = "Начало пути"
        startMarker.setIcon(resources.getDrawable(R.drawable.ic_start_marker, null))
        map.overlays.add(startMarker)

        val endMarker = Marker(map)
        endMarker.position = endPoint
        endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        endMarker.title = "Конец пути"
        endMarker.setIcon(resources.getDrawable(R.drawable.ic_end_marker, null))
        map.overlays.add(endMarker)

        GetRouteTask(map).execute(startPoint, endPoint)

        val bottomSheet: FrameLayout = findViewById(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight = resources.displayMetrics.heightPixels / 3  // Начальная высота
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        pdfContainer = findViewById(R.id.pdfContainer)
        btnOpenNavigator = findViewById(R.id.btn_open_navigator)

        val taskTitle: TextView = findViewById(R.id.taskTitle)
        val taskDescription: TextView = findViewById(R.id.taskDescription)
        val taskTime: TextView = findViewById(R.id.taskTime)
        val taskDate: TextView = findViewById(R.id.taskDate)
        val taskAddress: TextView = findViewById(R.id.taskAddress)
        val pdfText: TextView = findViewById(R.id.pdfText)

        val taskId = intent.getLongExtra("TASK_ID", -1L)
        val taskDesc = intent.getStringExtra("TASK_DESCRIPTION") ?: ""
        val taskStartTime = intent.getStringExtra("TASK_START_TIME") ?: ""
        val taskEndTime = intent.getStringExtra("TASK_END_TIME") ?: ""
        val taskDest = intent.getStringExtra("TASK_DESTINATION") ?: ""
        val taskDateStr = intent.getStringExtra("TASK_DATE") ?: ""
        val taskCategory = intent.getStringExtra("TASK_CATEGORY") ?: ""
        val taskName = intent.getStringExtra("TASK_NAME") ?: ""

        taskTitle.text = taskName
        taskDescription.text = taskDesc

        val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputTimeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val outputDateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

        try {
            val parsedStartTime = inputFormatter.parse(taskStartTime) ?: throw IllegalArgumentException("Invalid start time")
            val parsedEndTime = inputFormatter.parse(taskEndTime) ?: throw IllegalArgumentException("Invalid end time")
            val parsedDate = inputFormatter.parse(taskDateStr) ?: throw IllegalArgumentException("Invalid date")

            val formattedStartTime = outputTimeFormatter.format(parsedStartTime)
            val formattedEndTime = outputTimeFormatter.format(parsedEndTime)
            val formattedDate = outputDateFormatter.format(parsedDate)

            taskTime.text = "Время выполнения: $formattedStartTime - $formattedEndTime"
            taskDate.text = "Дата: $formattedDate"
        } catch (e: Exception) {
            taskTime.text = "Time format error"
            taskDate.text = "Date format error"
            Log.e("TaskWithMapActivity", "Error formatting time or date", e)
        }

        if (taskCategory == "Техническое обслуживание") {
            pdfContainer.visibility = View.GONE
        } else {
            pdfText.text = taskCategory
            pdfContainer.visibility = View.VISIBLE
            pdfContainer.setOnClickListener {
                val pdfUri = Uri.parse("http://example.com/sample.pdf")
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(pdfUri, "application/pdf")
                    flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                }
                startActivity(intent)
            }
        }

        // Получение адреса по координатам
        Thread {
            val address = convertCoordinatesToAddress(taskDest)
            runOnUiThread {
                taskAddress.text = "Адрес: $address"
            }
        }.start()

        btnOpenNavigator.setOnClickListener {
            val gmmIntentUri = Uri.parse("http://maps.google.com/maps?saddr=${startPoint.latitude},${startPoint.longitude}&daddr=${endPoint.latitude},${endPoint.longitude}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            } else {
                Toast.makeText(this, "Google Maps не установлено", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    private fun convertCoordinatesToAddress(coordinates: String): String {
        val parts = coordinates.split(",")
        if (parts.size != 2) return "Invalid coordinates"

        val latitude = parts[0].trim()
        val longitude = parts[1].trim()
        return requestAddressFromAPI(latitude, longitude)
    }

    private fun requestAddressFromAPI(latitude: String, longitude: String): String {
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

    private fun parseAddressFromJson(jsonData: String): String {
        val jsonObject = JSONObject(jsonData)
        val address = jsonObject.getJSONObject("address")
        val houseNumber = address.optString("house_number", "")
        val road = address.optString("road", "Unknown road")
        val city = address.optString("city", "Unknown city")
        val country = address.optString("country", "Unknown country")
        return "$road $houseNumber, $city, $country"
    }

    private class GetRouteTask(val map: MapView) : AsyncTask<GeoPoint, Void, List<GeoPoint>>() {
        override fun doInBackground(vararg points: GeoPoint): List<GeoPoint>? {
            val start = points[0]
            val end = points[1]
            val urlString = "http://router.project-osrm.org/route/v1/driving/${start.longitude},${start.latitude};${end.longitude},${end.latitude}?overview=full&geometries=geojson"
            val url = URL(urlString)
            val urlConnection = url.openConnection() as HttpURLConnection

            return try {
                val inputStream = urlConnection.inputStream.bufferedReader().use { it.readText() }
                val jsonResponse = JSONObject(inputStream)
                val routes = jsonResponse.getJSONArray("routes")
                if (routes.length() > 0) {
                    val geometry = routes.getJSONObject(0).getJSONObject("geometry")
                    val coordinates = geometry.getJSONArray("coordinates")
                    val geoPoints = mutableListOf<GeoPoint>()
                    for (i in 0 until coordinates.length()) {
                        val coord = coordinates.getJSONArray(i)
                        val lon = coord.getDouble(0)
                        val lat = coord.getDouble(1)
                        geoPoints.add(GeoPoint(lat, lon))
                    }
                    geoPoints
                } else {
                    null
                }
            } finally {
                urlConnection.disconnect()
            }
        }

        override fun onPostExecute(result: List<GeoPoint>?) {
            if (result != null && result.isNotEmpty()) {
                val polyline = Polyline()
                polyline.setPoints(result)
                polyline.outlinePaint.color = Color.RED
                map.overlays.add(polyline)
                map.invalidate()
            }
        }
    }
}