package se.umu.timotheuskampik.zerogravity

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SensorEventListener {

    private var longestPeriodLength: Long = 0
    private var startTimeCurrentPeriod: Long? = null

    private lateinit var sensorManager : SensorManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(savedInstanceState == null){
            setContentView(R.layout.activity_main)
            // set up sensor listener
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            sensorManager.registerListener(
                    this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val isZeroGravity = checkZeroGravity(event!!)
        if(isZeroGravity && startTimeCurrentPeriod == null) {
            // start new zero gravity period
            startTimeCurrentPeriod = event!!.timestamp

        } else if(!isZeroGravity && startTimeCurrentPeriod != null) {
            // end current zero gravity period
            /*Log.d("T", event.timestamp.toString())
            Log.d("T", startTimeCurrentPeriod!!.toString())*/
            val currentPeriodLength = // calculate difference and convert nano to milliseconds
                    (event.timestamp - startTimeCurrentPeriod!!) / 1000000
            startTimeCurrentPeriod = null
            last.text = "Last zero gravity period: ${currentPeriodLength.toString()}ms"
            if(currentPeriodLength > longestPeriodLength) {
                longestPeriodLength = currentPeriodLength
                longest.text =
                        "Longest zero gravity period: ${longestPeriodLength.toString()}ms"
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // Do nothing
    }

    private fun checkZeroGravity(event: SensorEvent): Boolean {
        val x = event!!.values[0].toDouble()
        val y = event!!.values[1].toDouble()
        val z = event!!.values[2].toDouble()
        if(Math.sqrt(Math.pow(x, 2.0) + Math.pow(y, 2.0) + Math.pow(z, 2.0)) < 0.1) {
            return true
        }
        return false
    }
}
