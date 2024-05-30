package com.example.simongame

class ConstantVal {
    companion object {
        const val NUMBER_OF_LEVELS: Int = 5
        val LEVEL_NAME = listOf("beginner", "easy", "intermediate", "difficult", "expert")
        val LEVEL_LEN_INITIAL_SEQUENCE_STEPS = intArrayOf(2, 4, 6, 8, 10)
        val LEVEL_VELOCITY_SEC = floatArrayOf(1f, 0.8f, 0.75f, 0.5f, 0.25f)
        val LEVEL_MAX_RESPONSE_TIME_SEC = floatArrayOf(Float.POSITIVE_INFINITY, 5f, 4f, 3f, 2f)
    }
}