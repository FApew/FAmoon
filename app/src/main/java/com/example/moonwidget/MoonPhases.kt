package com.example.moonwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.text.style.TextAppearanceSpan
import android.widget.RemoteViews
import androidx.preference.PreferenceManager
import androidx.core.content.res.ResourcesCompat
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.Date
import kotlin.math.roundToInt

class MoonPhases : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            clock(context, appWidgetManager, appWidgetId)
        }
    }
}

private val handler = Handler(Looper.getMainLooper())
private fun clock(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    handler.postDelayed(object : Runnable {
        override fun run() {
            updateAppWidget(context, appWidgetManager, appWidgetId)
            handler.postDelayed(this, 30000L)
        }
    }, 0L)
}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    val views = RemoteViews(context.packageName, R.layout.moon_phases)
    var moonAge = (ChronoUnit.MINUTES.between(LocalDateTime.of(2024, 8, 4, 13, 13, 0), LocalDateTime.now()).toDouble()/(60*24))%29.530588
    if (moonAge < 0) {
        moonAge += 29.530588
    }
    val percentage = MainActivity().getFraction(MainActivity().jDate(Date().time.toDouble())) * 100
    val phase = MainActivity().getPhase(context, moonAge)
    val dir = MainActivity().getDir(phase[1] as Int, moonAge, percentage)
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val latitude = sharedPreferences.getFloat("latitude", 0.0f)
    val longitude = sharedPreferences.getFloat("longitude", 0.0f)
    val date = LocalDateTime.now()
    val sunPosition = MainActivity().calcSunPos(LocalDateTime.now(), latitude.toDouble(), longitude.toDouble())
    val moonPosition = MainActivity().calcMoonPos(date, latitude.toDouble(), longitude.toDouble())
    val clipMoon = MainActivity().clipMoon(context, R.drawable.newm, (percentage*100).roundToInt().toFloat()/100, dir, MainActivity().getAngle(moonPosition[0], moonPosition[1], sunPosition[0], sunPosition[1]).toFloat() - 90, true)
    val moonIcon = MainActivity().overlapBitmapsAndRotate(BitmapFactory.decodeResource(context.resources, R.drawable.full), clipMoon)
    views.setTextViewText(R.id.illPerc, getFont(context, R.font.sambold,"${percentage.roundToInt()}%", R.style.illPerc))
    views.setTextViewText(R.id.phaseText, getFont(context, R.font.samsans, phase[0].toString(), R.style.phaseText))
    views.setImageViewBitmap(R.id.moonLilIcon, moonIcon)
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

private fun buildTextBitmap(context: Context, text: String, typeface: Typeface?): Bitmap {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val baseline = -paint.ascent().toInt()
    val width = (paint.measureText(text) + 0.5f).toInt()
    val height = (baseline + paint.descent() + 0.5f).toInt()
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    paint.textSize = context.resources.getDimensionPixelSize(R.dimen.font_size).toFloat()
    paint.color = Color.BLACK
    paint.typeface = typeface
    canvas.drawText(text, 0f, baseline.toFloat(), paint)

    return bitmap
}

private fun getFont(context: Context, font: Int, string: String, style: Int): SpannableString {
    val typeface = ResourcesCompat.getFont(context, font)
    val bitmap = buildTextBitmap(context, string, typeface)
    val spannableString = SpannableString(string)
    val drawable = BitmapDrawable(context.resources, bitmap)
    val imageSpan = ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM)
    val illPercTextAppearanceSpan = TextAppearanceSpan(context, style)

    drawable.setBounds(0, 0, bitmap.width, bitmap.height)
    spannableString.setSpan(imageSpan, 0, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    spannableString.setSpan(illPercTextAppearanceSpan, 0, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

    return spannableString
}