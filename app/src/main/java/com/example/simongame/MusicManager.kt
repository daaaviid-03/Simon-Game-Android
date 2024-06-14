package com.example.simongame

import android.content.Context
import android.media.MediaPlayer

/**
 * Class in charge of playing background music
 */
class MusicManager {
    companion object {
        /**
         * MediaPlayer object
         */
        private var mediaPlayer: MediaPlayer? = null

        /**
         * Starts the music
         */
        fun startMusic(context: Context, initialVolume: Float) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(context, R.raw.background_music).apply {
                    isLooping = true
                }
                setVolume(initialVolume)
            }
            resumeMusic()
        }

        /**
         * Stops the music
         */
        fun stopMusic() {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }

        /**
         * Pauses the music
         */
        fun pauseMusic() {
            mediaPlayer?.pause()
        }

        /**
         * Resumes the music
         */
        fun resumeMusic() {
            mediaPlayer?.start()
        }

        /**
         * Sets the volume
         */
        fun setVolume(volume: Float) {
            mediaPlayer?.setVolume(volume, volume)
        }
    }

}