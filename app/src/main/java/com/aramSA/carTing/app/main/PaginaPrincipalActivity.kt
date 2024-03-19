package com.aramSA.carTing.app.main

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.ui.NavigationUI
import com.aramSA.carTing.R
import com.google.android.material.bottomnavigation.BottomNavigationView


class PaginaPrincipalActivity : AppCompatActivity() {


    private lateinit var navController: NavController

    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagina_principal)

        sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val bundle = intent.extras
        val email = bundle?.getString("email")

        with (sharedPref.edit()) {
            putString("dataKey", email)
            apply()
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainContainer) as NavHost
        navController = navHostFragment.navController
        val bottomNavegationView = findViewById<BottomNavigationView>(R.id.bottomNav)
        NavigationUI.setupWithNavController(bottomNavegationView, navController)

        navController.navigate(R.id.homeFragment)
    }

}