package dev.mayankmkh.kmpsample

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform