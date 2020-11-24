package com.example.demoapp.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.demoapp.R
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class DummyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy)


        // Thread for processing GET requests
        val threadGet = Thread {
            val apiUrl = "https://api.mocki.io/v1/2dbcf888"
            val url = URL(apiUrl)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            try {
                httpURLConnection.requestMethod = "GET"
                httpURLConnection.setRequestProperty("Content-Type", "application/json")
                httpURLConnection.doOutput = true
                httpURLConnection.connect()

                val br =
                    BufferedReader(InputStreamReader(httpURLConnection.content as InputStream?))
                val sb = StringBuilder()
                var line: String
                br.readLine().also { line = it }
                sb.append(line.trimIndent())
                br.close()
                runOnUiThread { findViewById<TextView>(R.id.textView_out).text = sb.toString() }


            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                httpURLConnection.disconnect()
            }
        }

        val getButton: Button = findViewById(R.id.button_get)
        getButton.setOnClickListener {
            threadGet.start()
        }

        // Thread for processing POST requests
        val threadPost = Thread {
            val apiUrl = "https://api.mocki.io/v1/d712fc44"
            val url = URL(apiUrl)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            try {

                httpURLConnection.requestMethod = "POST"
                httpURLConnection.setRequestProperty("Content-Type", "application/json")
                httpURLConnection.doOutput = true
                httpURLConnection.doInput = true
                httpURLConnection.connect()

                val outputStream = DataOutputStream(httpURLConnection.outputStream)
                val jsonParam = JSONObject()
                jsonParam.put("ID", "25")
                jsonParam.put("description", "Real")
                jsonParam.put("enable", "true")
                outputStream.writeBytes(jsonParam.toString())
                outputStream.flush()
                outputStream.close()

                val br =
                    BufferedReader(InputStreamReader(httpURLConnection.content as InputStream?))
                val sb = StringBuilder()
                var line: String
                br.readLine().also { line = it }
                sb.append(line.trimIndent())
                br.close()

                val message = "${httpURLConnection.responseMessage} \n $sb"
                runOnUiThread { findViewById<TextView>(R.id.textView_out).text = message }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                httpURLConnection.disconnect()
            }
        }

        val postButton: Button = findViewById(R.id.button_post)
        postButton.setOnClickListener {
            threadPost.start()
        }


    }


}
