package com.popalay.barnee.shared

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platformName}!"
    }
}
