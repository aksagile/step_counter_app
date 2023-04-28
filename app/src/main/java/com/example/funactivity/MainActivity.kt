package com.example.funactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast
import android.util.Log
import android.media.MediaPlayer
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest

class MainActivity : AppCompatActivity(), SensorEventListener {
    private var sensorManager: SensorManager? = null
    //private var mp: MediaPlayer
    private var running = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f
    private var prevSteps :String = "0"
    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: LinearLayoutManager
    lateinit var recyclerAdapter: RvAdapter
    lateinit var updateButton: Button
    lateinit var refreshButton: Button
    private var ranking : String = "18"


    fun onClick(view: View) {
        val appName = "Arista Stride"
        println("Welcome to $appName")
        resetSteps()
    }

    private fun resetSteps() {
        var stepsTaken = findViewById<TextView>(R.id.steps)
        previousTotalSteps = totalSteps
        stepsTaken.text = prevSteps
        // This will save the data
        saveData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mp:MediaPlayer = MediaPlayer.create( this, R.raw.walk)
        mp.start()
        getDataFromAPI()
        //loadData()
        //resetSteps()
        var rankingText = findViewById<TextView>(R.id.ranking)
        rankingText.text = "Your Current Rank is $ranking"

        //to make the user send his current steps to the spreadsheet
        updateButton = findViewById<Button?>(R.id.updateSteps)
        updateButton.setOnClickListener {
            // Send POST request here
            val steps = findViewById<TextView>(R.id.steps).toString()
            print("the value of steps we are getting ")
            print(steps)
            val name = "Ankesh"
            val teamName = "Strata"
            sendRequest(name, teamName, steps);
        }

        //refresh ranking on app start and also on button click
        refreshButton = findViewById<Button?>(R.id.refreshRankings)
        refreshButton.setOnClickListener{
            getDataFromAPI()
        }
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onResume() {
        super.onResume()
        running = true
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor == null) {
            Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        var stepsTaken = findViewById<TextView>(R.id.steps)
        if (running) {
            totalSteps = event!!.values[0]
            val currentSteps = totalSteps.toInt() + "$stepsTaken".toInt()
            stepsTaken.text = ("$totalSteps")
        }
    }

    private fun saveData() {
        // TODO - Save to spreadsheet

        // Shared Preferences will allow us to save
        // and retrieve data in the form of key,value pair.
        // In this function we will save data
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.apply()

    }

    private fun loadData() {
        // TODO - Get from spreadsheet

        // In this function we will retrieve data
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)
        // Log.d is used for debugging purposes
        Log.d("MainActivity", "$savedNumber")
        previousTotalSteps = savedNumber
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // We do not have to write anything in this function for this app
    }

    private fun getDataFromAPI(){
        recyclerView= findViewById(R.id.idRVUsers)
        layoutManager= LinearLayoutManager(this)
        var userModalArrayList = arrayListOf<User>()
        var url = "https://script.google.com/macros/s/AKfycbyGVJB0oSn8UogxjHIQAsO-ldPmqBhiVdoDhKzf4GVQlEZS0F0oS_bqKehoeFdd7fGTtw/exec?action=get"
        val queue= Volley.newRequestQueue(this)
        val jsonObjectRequest=object : JsonObjectRequest(
            Request.Method.GET,url,null,
            Response.Listener {
                //readProgressLayout.visibility=View.GONE
                //readProgressBar.visibility=View.GONE
                val data=it.getJSONArray("items")
                for(i in 0 until data.length()){
                    val userJsonObject=data.getJSONObject(i)
                    val name = userJsonObject.getString("name")
                    if(name == "Ankesh"){
                        prevSteps = userJsonObject.getString("steps")
                        ranking = userJsonObject.getString("id")
                        var rankingText = findViewById<TextView>(R.id.ranking)
                        var rank = i+1
                        rankingText.text = "Your Current Ranks is $rank"
                        var stepsTaken = findViewById<TextView>(R.id.steps)
                        stepsTaken.text = prevSteps
                    }
                    val userObject=User(
                        //userJsonObject.getString("id"),
                        userJsonObject.getString("name"),
                        userJsonObject.getString("teamName"),
                        userJsonObject.getString("steps")
                    )
                    if (i < 5) {
                        userModalArrayList.add(userObject)
                    }
                }
                recyclerAdapter= RvAdapter(this,userModalArrayList)
                recyclerView.adapter=recyclerAdapter
                recyclerView.layoutManager=layoutManager
            },Response.ErrorListener {
                //readProgressLayout.visibility=View.GONE
                //readProgressBar.visibility=View.GONE
                Toast.makeText(this@MainActivity, it.toString(), Toast.LENGTH_SHORT).show()
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                return super.getHeaders()
            }
        }
        queue.add(jsonObjectRequest)
    }

    private fun sendRequest(name : String, teamName: String, steps: String) {
        val step = findViewById<TextView>(R.id.steps).text.toString()
        Log.d("issues in send request", step)
        //val newSteps = Integer.parseInt(step)
        val url = "https://script.google.com/macros/s/AKfycbyGVJB0oSn8UogxjHIQAsO-ldPmqBhiVdoDhKzf4GVQlEZS0F0oS_bqKehoeFdd7fGTtw/exec?action=update&id=4&name=Ankesh&teamName=sand&steps="+step
        val stringRequest =  StringRequest(Request.Method.GET, url,
            Response.Listener {
                Toast.makeText(this@MainActivity, it.toString(), Toast.LENGTH_SHORT).show()
                //writeProgressLayout.visibility = View.GONE
                //writeProgressBar.visibility = View.GONE
            },
            Response.ErrorListener {
                Toast.makeText(this@MainActivity, it.toString(), Toast.LENGTH_SHORT).show()
                //writeProgressLayout.visibility = View.GONE
                //writeProgressBar.visibility = View.GONE
                Log.d("API", "that didn't work")
            })
        val queue = Volley.newRequestQueue(this@MainActivity)
        queue.add(stringRequest)
    }
}

