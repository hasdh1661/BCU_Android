package com.mandarin.bcu.androidutil.battle.asynchs

import android.media.MediaPlayer
import android.os.AsyncTask
import com.mandarin.bcu.androidutil.adapters.MediaPrepare
import com.mandarin.bcu.androidutil.battle.sound.SoundHandler
import com.mandarin.bcu.androidutil.battle.sound.SoundHandler.available
import com.mandarin.bcu.androidutil.battle.sound.SoundHandler.getMP
import com.mandarin.bcu.androidutil.battle.sound.SoundHandler.inBattle
import com.mandarin.bcu.androidutil.battle.sound.SoundHandler.releaseAll
import com.mandarin.bcu.androidutil.battle.sound.SoundHandler.returnBack
import common.util.pack.Pack
import java.io.IOException

class SoundAsync(private val ind: Int) : AsyncTask<Void?, Void?, Void?>() {
    override fun doInBackground(vararg voids: Void?): Void? {
        if (!inBattle) {
            releaseAll()
            return null
        }
        if (available == 0) return null
        val mp = getMP(ind)
        try {
            if (!mp.isInitialized) {
                if (ind == 45 && SoundHandler.twoMusic && SoundHandler.haveToChange) {
                    if (!SoundHandler.Changed) {
                        returnBack(mp, ind)
                        if (SoundHandler.MUSIC.isRunning) SoundHandler.MUSIC.pause()
                        SoundHandler.MUSIC.reset()
                        SoundHandler.MUSIC.isLooping = false
                        val g = Pack.def.ms[ind]
                        if (g != null) {
                            try {
                                SoundHandler.MUSIC.setDataSource(g.absolutePath)
                                SoundHandler.MUSIC.setVolume(SoundHandler.se_vol, SoundHandler.se_vol)
                                SoundHandler.MUSIC.prepareAsync()
                                SoundHandler.MUSIC.setOnPreparedListener(object : MediaPrepare() {
                                    override fun prepare(mp: MediaPlayer?) {
                                        SoundHandler.MUSIC.start()
                                    }
                                })
                                SoundHandler.MUSIC.setOnCompletionListener {
                                    SoundHandler.MUSIC.reset()
                                    val h = Pack.def.ms[SoundHandler.mu1]
                                    if (h != null) {
                                        if (h.exists()) {
                                            SoundHandler.MUSIC.isLooping = true
                                            try {
                                                SoundHandler.MUSIC.setDataSource(h.absolutePath)
                                                SoundHandler.MUSIC.setVolume(SoundHandler.mu_vol, SoundHandler.mu_vol)
                                                SoundHandler.MUSIC.prepareAsync()
                                                SoundHandler.MUSIC.setOnPreparedListener(object : MediaPrepare() {
                                                    override fun prepare(mp: MediaPlayer?) {
                                                        if (SoundHandler.musicPlay) {
                                                            SoundHandler.MUSIC.start()
                                                        }
                                                        SoundHandler.Changed = true
                                                        SoundHandler.haveToChange = false
                                                    }
                                                })
                                            } catch (e: IOException) {
                                                e.printStackTrace()
                                            }
                                        }
                                    }
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                        return null
                    }
                }
                if (SoundHandler.se_vol == 0f) return null
                val f = Pack.def.ms[ind] ?: return null
                if (!f.exists()) return null
                val path = f.absolutePath
                mp.setDataSource(path)
                mp.isLooping = false
                mp.prepareAsync()
                mp.setOnPreparedListener(object : MediaPrepare() {
                    override fun prepare(mmp : MediaPlayer?) {
                        mp.start(true)
                    }
                })
                mp.setOnCompletionListener {
                    mp.isRunning = false
                    returnBack(mp, ind)
                }
            } else {
                if (!mp.isRunning && mp.isInitialized) {
                    mp.start(true)
                } else {
                    mp.reset()
                    returnBack(mp, ind)
                }
            }
        } catch (e: IOException) {
            mp.release()
        } catch (e: IllegalStateException) {
            mp.release()
        }
        return null
    }

}