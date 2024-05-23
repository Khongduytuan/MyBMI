package com.eagletech.kt.mybmi.data

import android.content.Context
import android.content.SharedPreferences


class MyDataApp constructor(context: Context) {
    private val sharedPreferences: SharedPreferences


    init {
        sharedPreferences = context.getSharedPreferences("DataPref", Context.MODE_PRIVATE)
    }

    companion object {
        @Volatile
        private var instance: MyDataApp? = null

        fun getInstance(context: Context): MyDataApp {
            return instance ?: synchronized(this) {
                instance ?: MyDataApp(context).also { instance = it }
            }
        }
    }

    // Lấy ra thông tin mua theo lượt
    fun getBMI(): Int {
        return sharedPreferences.getInt("bmi", 0)
    }

    fun setBMI(bmis: Int) {
        sharedPreferences.edit().putInt("bmi", bmis).apply()
    }

    fun addBMI(amount: Int) {
        val current = getBMI()
        setBMI(current + amount)
    }

    fun removeBMI() {
        val current = getBMI()
        if (current > 0) {
            setBMI(current - 1)
        }
    }


    // Lấy thông tin mua premium
    var isPremium: Boolean?
        get() {
            val userId = sharedPreferences.getString("UserId", "")
            return sharedPreferences.getBoolean("PremiumPlan_\$userId$userId", false)
        }
        set(state) {
            val userId = sharedPreferences.getString("UserId", "")
            sharedPreferences.edit().putBoolean("PremiumPlan_\$userId$userId", state!!).apply()
        }

    // Lưu thông tin người dùng
    fun userId(id: String?) {
        sharedPreferences.edit().putString("UserId", id).apply()
    }

    // Lấy ra thông tin id người dùng
    fun getUserId(): String? {
        return sharedPreferences.getString("UserId", null)
    }

}