package com.example.esgrimaapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform