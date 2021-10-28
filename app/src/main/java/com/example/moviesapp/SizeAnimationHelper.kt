package com.example.moviesapp

import android.view.View
import android.view.animation.Interpolator
import androidx.core.view.ViewCompat

class ReverseInterpolator : Interpolator {
    override fun getInterpolation(paramFloat: Float): Float {
        return Math.abs(paramFloat - 1f)
    }
}

class SizeAnimationHelper(){
    companion object{
        fun animateTap(view: View){
            val duration = 75L

            ViewCompat.animate(view)
                .scaleX(0.8f)
                .scaleY(0.8f)
                .setDuration(duration)
                .setInterpolator(ReverseInterpolator())
        }
    }
}