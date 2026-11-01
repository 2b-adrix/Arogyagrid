package com.example.arogyagrid.core

import android.content.Context
import android.content.SharedPreferences

object UserSession {

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences("arogyagrid_session", Context.MODE_PRIVATE)
    }

    var token: String?
        get() = prefs.getString("token", null)
        set(value) = prefs.edit().putString("token", value).apply()

    var userId: String?
        get() = prefs.getString("userId", null)
        set(value) = prefs.edit().putString("userId", value).apply()

    var userName: String?
        get() = prefs.getString("userName", null)
        set(value) = prefs.edit().putString("userName", value).apply()

    var userEmail: String?
        get() = prefs.getString("userEmail", null)
        set(value) = prefs.edit().putString("userEmail", value).apply()

    var userRole: String?
        get() = prefs.getString("userRole", null)
        set(value) = prefs.edit().putString("userRole", value).apply()

    var userDistrict: String?
        get() = prefs.getString("userDistrict", null)
        set(value) = prefs.edit().putString("userDistrict", value).apply()

    var userFacilityId: String?
        get() = prefs.getString("userFacilityId", null)
        set(value) = prefs.edit().putString("userFacilityId", value).apply()

    val isLoggedIn: Boolean
        get() = !token.isNullOrEmpty()

    fun saveLogin(
        token: String,
        userId: String,
        name: String,
        email: String,
        role: String,
        district: String?,
        facilityId: String?
    ) {
        this.token = token
        this.userId = userId
        this.userName = name
        this.userEmail = email
        this.userRole = role
        this.userDistrict = district
        this.userFacilityId = facilityId
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}
