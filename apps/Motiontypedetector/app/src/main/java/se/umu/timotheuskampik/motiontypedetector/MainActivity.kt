package se.umu.timotheuskampik.motiontypedetector

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.apache.commons.math3.stat.StatUtils

class MainActivity : AppCompatActivity(), SensorEventListener {

    private var windowStartTime : Long? = null
    private var measurementsX = arrayListOf<Double>()
    private var measurementsY = arrayListOf<Double>()
    private var measurementsZ = arrayListOf<Double>()

    private lateinit var sensorManager : SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(savedInstanceState == null) {
            setContentView(R.layout.activity_main)
            // set up sensor listener for accelerometer
            sensorManager = getSystemService(Context.SENSOR_SERVICE)
                    as SensorManager
            sensorManager.registerListener(
                    this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        measurementsX.add(event!!.values[0].toDouble())
        measurementsY.add(event!!.values[1].toDouble())
        measurementsZ.add(event!!.values[2].toDouble())
        if (windowStartTime == null) {
            windowStartTime = event!!.timestamp
        } else if (event!!.timestamp - windowStartTime!! > 1000000000) {
            windowStartTime = event!!.timestamp
            status.text = determineMotionType()
            measurementsX.clear()
            measurementsY.clear()
            measurementsZ.clear()

        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // Do nothing
    }

    private fun determineMotionType(): String {
        val standardDeviationX = Math.sqrt(
                StatUtils.populationVariance(measurementsX.toDoubleArray()))
        val standardDeviationY = Math.sqrt(
                StatUtils.populationVariance(measurementsY.toDoubleArray()))
        val standardDeviationZ = Math.sqrt(
                StatUtils.populationVariance(measurementsZ.toDoubleArray()))

        if(standardDeviationX > 0.5 && standardDeviationY > 0.5) {
            if(standardDeviationZ > 7) {
                return "Jumping"
            } else if(standardDeviationZ > 4) {
                return "Running"
            } else if(standardDeviationZ > 0.35) {
                return "Walking"
            }
            return "Standing"
        }
        return "Standing"

    }
}
