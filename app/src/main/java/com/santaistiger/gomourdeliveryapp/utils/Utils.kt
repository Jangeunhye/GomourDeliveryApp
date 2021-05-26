/**
 * created by Kang Gumsil
 */
package com.santaistiger.gomourdeliveryapp.utils

import android.util.DisplayMetrics
import android.util.TypedValue

fun toDp(metrics: DisplayMetrics, px: Int): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px.toFloat(), metrics).toInt()
}