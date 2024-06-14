package com.example.simongame

import android.content.Context
import android.media.MediaPlayer

class MusicManager {
    companion object {
        private var mediaPlayer: MediaPlayer? = null
        fun startMusic(context: Context) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(context, R.raw.background_music).apply {
                    isLooping = true
                }
            }
            resumeMusic()
        }
        fun stopMusic() {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }
        fun pauseMusic() {
            mediaPlayer?.pause()
        }
        fun resumeMusic() {
            mediaPlayer?.start()
        }
        fun setVolume(volume: Float) {
            mediaPlayer?.setVolume(volume, volume)
        }
    }

}