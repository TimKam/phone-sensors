package se.umu.timotheuskampik.tremormeasuring

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.apache.commons.math3.stat.StatUtils


class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager : SensorManager

    private var windowStartTime : Long? = null
    private var measurementsX = arrayListOf<Double>()
    private var measurementsY = arrayListOf<Double>()
    private var measurementsZ = arrayListOf<Double>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(savedInstanceState == null) {
            setContentView(R.layout.activity_main)
            container.setBackgroundColor(Color.rgb(0,255,0))
            container.invalidate()
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
        if(windowStartTime == null) {
            windowStartTime = event!!.timestamp
        } else if(event!!.timestamp - windowStartTime!! > 1000000000) {
            windowStartTime = event!!.timestamp
            val standardDeviationX = Math.ceil(
                    StatUtils.populationVariance(measurementsX.toDoubleArray()))
            val standardDeviationY = Math.ceil(
                    StatUtils.populationVariance(measurementsY.toDoubleArray()))
            val standardDeviationZ = Math.ceil(
                    StatUtils.populationVariance(measurementsZ.toDoubleArray()))
            var impactFactor = (
                    standardDeviationX +
                    standardDeviationY +
                    standardDeviationZ) / 3.0
            if(impactFactor < 1) {
                impactFactor = 1.0
            }
            val green = 255/impactFactor
            val red = 255/(255/impactFactor)
            container.setBackgroundColor(
                    Color.rgb(red.toInt(), green.toInt(),0))
            measurementsX.clear()
            measurementsY.clear()
            measurementsZ.clear()

        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // Do nothing
    }

}
