package com.eagletech.kt.mybmi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.eagletech.kt.mybmi.data.MyDataApp
import com.eagletech.kt.mybmi.databinding.ActivityMainBinding
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var myDataApp: MyDataApp
    private var choose = true
    private var age: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myDataApp = MyDataApp.getInstance(this)
        setDefaultBackground()

        binding.male.setOnClickListener {
            choose = true
            binding.male.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.selected_background
                )
            )
            binding.woman.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.default_background
                )
            )
        }

        binding.woman.setOnClickListener {
            choose = false
            binding.woman.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.selected_background
                )
            )
            binding.male.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.default_background
                )
            )
        }

        binding.topBar.dowloadIcon.setOnClickListener {
            showInfoBuy()
        }
        binding.topBar.menuIcon.setOnClickListener {
            val intent = Intent(this, PayScreenActivity::class.java)
            startActivity(intent)
        }
        binding.cardBtn.setOnClickListener {
            val m = binding.edtMet.text.toString()
            val cm = binding.edtCm.text.toString()
            val weight = binding.edtWeight.text.toString()
            Log.d("data", "$m $cm $weight")
            if (age != 0) {
                Toast.makeText(this, "Enter your age", Toast.LENGTH_LONG).show()
            } else if (m.isEmpty()) {
                Toast.makeText(this, "Enter your height", Toast.LENGTH_LONG).show()
            } else if (cm.isEmpty()) {
                Toast.makeText(this, "Enter your height", Toast.LENGTH_LONG).show()
            } else if (weight.isEmpty()) {
                Toast.makeText(this, "Enter your weight", Toast.LENGTH_LONG).show()
            } else {
                if (choose) {
                    val height = convertHeightToMeters(m, cm)
                    val resulBMI = calculateBMI(weight, height)
                    checkUI(resulBMI)

                } else {
                    val height = convertHeightToMeters(m, cm)
                    val resulBMI = calculateBMI(weight, height)
                    checkUI(resulBMI)
                }
                myDataApp.removeBMI()
            }
        }


    }

    private fun checkUI(result: Double) {
        binding.tvBmi.text = String.format("%.1f", result)

        val (comment, color) = when {
            result < 18.5 -> "Underweight" to R.color.level1
            result in 18.5..24.9 -> "Normal weight" to R.color.level3
            result in 25.0..29.9 -> "Overweight" to R.color.level2
            result in 30.0..34.9 -> "Obesity class I" to R.color.level1
            result in 35.0..39.9 -> "Obesity class II" to R.color.level1
            result >= 40.0 -> "Obesity class III" to R.color.level1
            else -> "No data" to R.color.black
        }

        binding.tvCmt.text = comment
        binding.tvCmt.setTextColor(ContextCompat.getColor(this, color))
    }

    private fun setDefaultBackground() {
        binding.male.setBackgroundColor(ContextCompat.getColor(this, R.color.default_background))
        binding.woman.setBackgroundColor(ContextCompat.getColor(this, R.color.default_background))
    }

    private fun convertHeightToMeters(meters: String, centimeters: String): Double {
        val metersValue = meters.toDoubleOrNull() ?: 0.0
        val centimetersValue = centimeters.toDoubleOrNull() ?: 0.0
        val heightInMeters = metersValue + centimetersValue / 100.0
        val roundedHeight = BigDecimal(heightInMeters).setScale(1, RoundingMode.HALF_UP)
        return roundedHeight.toDouble()
    }
    fun calculateBMI(weight: String, height: Double): Double {
        val weightInKg = weight.toDoubleOrNull() ?: 0.0
        return weightInKg / (height * height)
    }


    private fun showInfoBuy() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Your usage BMI")
            .setPositiveButton("Confirm") { dialog, _ -> dialog.dismiss() }
            .create()
        if (myDataApp.isPremium == true) {
            dialog.setMessage("Successfully registered")
        } else {
            dialog.setMessage("You have ${myDataApp.getBMI()} turns")
        }
        dialog.show()
    }

}