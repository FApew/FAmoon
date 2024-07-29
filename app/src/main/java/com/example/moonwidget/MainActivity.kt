package com.example.moonwidget

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.preference.PreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.tan

class MainActivity : AppCompatActivity() {
    private lateinit var imageMoon: ImageView
    private lateinit var line: ImageView
    private lateinit var altImg: ImageView
    private lateinit var aziImg: ImageView
    private lateinit var phaseText: TextView
    private lateinit var illPerc: TextView
    private lateinit var ageTxt: TextView
    private lateinit var fullTtxt: TextView
    private lateinit var newTtxt: TextView
    private lateinit var altTxt: TextView
    private lateinit var aziTxt: TextView
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var clock: ImageButton
    private lateinit var yup: ImageButton
    private lateinit var mup: ImageButton
    private lateinit var dup: ImageButton
    private lateinit var hup: ImageButton
    private lateinit var mmup: ImageButton
    private lateinit var ydw: ImageButton
    private lateinit var mdw: ImageButton
    private lateinit var ddw: ImageButton
    private lateinit var hdw: ImageButton
    private lateinit var mmdw: ImageButton
    private lateinit var optBtn: ImageButton
    private lateinit var ytxt: TextView
    private lateinit var mtxt: TextView
    private lateinit var dtxt: TextView
    private lateinit var htxt: TextView
    private lateinit var mmtxt: TextView
    private lateinit var newBit: Bitmap
    private lateinit var fullBit: Bitmap
    private lateinit var barBit: Bitmap
    private lateinit var options: LinearLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageMoon = findViewById(R.id.moonIcon)
        line = findViewById(R.id.line)
        altImg = findViewById(R.id.altImage)
        aziImg = findViewById(R.id.aziImage)
        phaseText = findViewById(R.id.phaseText)
        illPerc = findViewById(R.id.illPerc)
        ageTxt = findViewById(R.id.ageTxt)
        fullTtxt = findViewById(R.id.fullTtxt)
        newTtxt = findViewById(R.id.newTtxt)
        altTxt = findViewById(R.id.altTxt)
        aziTxt = findViewById(R.id.aziTxt)
        clock = findViewById(R.id.resetClock)
        yup = findViewById(R.id.yU)
        mup = findViewById(R.id.mU)
        dup = findViewById(R.id.dU)
        hup = findViewById(R.id.hU)
        mmup = findViewById(R.id.mmU)
        ydw = findViewById(R.id.yD)
        mdw = findViewById(R.id.mD)
        ddw = findViewById(R.id.dD)
        hdw = findViewById(R.id.hD)
        mmdw = findViewById(R.id.mmD)
        optBtn = findViewById(R.id.optBtn)
        ytxt = findViewById(R.id.yTxt)
        mtxt = findViewById(R.id.mTxt)
        dtxt = findViewById(R.id.dTxt)
        htxt = findViewById(R.id.hTxt)
        mmtxt = findViewById(R.id.mmTxt)
        options = findViewById(R.id.options)

        clock.setOnClickListener{sync()}
        yup.setOnClickListener{arrow(0)}

        mup.setOnClickListener{arrow(1)}
        dup.setOnClickListener{arrow(2)}
        hup.setOnClickListener{arrow(3)}
        mmup.setOnClickListener{arrow(4)}
        ydw.setOnClickListener{arrow(5)}
        mdw.setOnClickListener{arrow(6)}
        ddw.setOnClickListener{arrow(7)}
        hdw.setOnClickListener{arrow(8)}
        mmdw.setOnClickListener{arrow(9)}

        optBtn.setOnClickListener{toggleOpt()}

        newBit = BitmapFactory.decodeResource(resources, R.drawable.dark)
        fullBit = BitmapFactory.decodeResource(resources, R.drawable.light)
        barBit = BitmapFactory.decodeResource(resources, R.drawable.barra)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        clock()
    }

    private val mLength = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31, 29)
    private var time = true
    private var editDate = intArrayOf()
    private val newMoon = R.drawable.dark
    private val fullMoon = R.drawable.light
    private val timer = Timer()
    private var day = LocalDateTime.now().withHour(0).withMinute(0)
    private var altArr = mutableListOf<Double>()
    private var altDates = mutableListOf(LocalDateTime.now())
    private var optBool = false

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    private fun clock() {
        timer.schedule(object : TimerTask() {
            override fun run() {
                main()
            }
        }, 0L, 10000L)
    }

    @SuppressLint("SetTextI18n")
    fun main() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sharedPreferences.edit()
        var date = LocalDateTime.now()
        if (time) {
            val curDate = LocalDateTime.now()
            editDate = intArrayOf(curDate.year, curDate.monthValue, curDate.dayOfMonth, curDate.hour, curDate.minute)
            runOnUiThread {
                ytxt.text = "000${editDate[0]}".takeLast(4)
                mtxt.text = "0${editDate[1]}".takeLast(2)
                dtxt.text = "0${editDate[2]}".takeLast(2)
                htxt.text = "0${editDate[3]}".takeLast(2)
                mmtxt.text = "0${editDate[4]}".takeLast(2)
            }
        } else {
            date = LocalDateTime.of(editDate[0], editDate[1], editDate[2], editDate[3], editDate[4], 0)
        }

        getLocation(this, fusedLocationProviderClient) { latitude, longitude ->

            if (latitude != 0.0 && longitude != 0.0) {
                editor.putFloat("latitude", latitude.toFloat())
                editor.putFloat("longitude", longitude.toFloat())
                editor.apply()
            }

            val lat = sharedPreferences.getFloat("latitude", 0.0f).toDouble()
            val long = sharedPreferences.getFloat("longitude", 0.0f).toDouble()

            var moonAge = (ChronoUnit.MINUTES.between(LocalDateTime.of(2024, 8, 4, 13, 13, 0), date).toDouble()/(60*24))%29.530588
            if (moonAge < 0) {
                moonAge += 29.530588
            }
            val percentage = (getFraction(jDate(Date.from(date.toInstant(getTimeZoneOffset(long))).time.toDouble())) * 100)
            val phase = getPhase(this, moonAge)
            val dir = getDir(phase[1] as Int, moonAge, percentage)
            val sunPosition = calcSunPos(date, lat, long)
            val moonPosition = calcMoonPos(date, lat, long)
            val clipMoon = clipMoon(this, newMoon, (percentage*100).roundToInt().toFloat()/100, dir, getAngle(moonPosition.first, moonPosition.second, sunPosition.first, sunPosition.second).toFloat() - 90, false)
            val moonIcon = overlapBitmapsAndRotate(fullBit, clipMoon)
            val dates = getMoons(date, long)
            val lineMap = drawDaysLine((moonAge) / 29.530588, moonIcon)
            if (LocalDateTime.now().withHour(0).withMinute(0) != day) {
                altArr = getAltArr(date, lat, long)
                altDates = getAltDates(date, altArr, lat, long)
                day = LocalDateTime.now().withHour(0).withMinute(0)
            }
            val altSine = drawAltGraph(this, date, moonPosition.first, altArr, altDates, moonIcon)

            try {
                //Update Phase
                imageMoon.setImageBitmap(moonIcon)
                phaseText.text = phase[0] as CharSequence
                if (percentage.roundToInt().toDouble() != ((percentage*100).roundToInt().toDouble()/100)) {
                    illPerc.text = "${((percentage*100).roundToInt().toDouble()/100)}%"
                } else {
                    illPerc.text = "${percentage.roundToInt()}%"
                }

                //Update Age
                ageTxt.text = "${((moonAge)*100).roundToInt().toDouble()/100}d ${this.getString(R.string.outOf)} 29.53"
                newTtxt.text = "0${dates.first.dayOfMonth}".takeLast(2) + "/" + "0${dates.first.monthValue}".takeLast(2) + " - ${"0${dates.first.hour}".takeLast(2)}:${"0${dates.first.minute}".takeLast(2)}"
                fullTtxt.text = "0${dates.second.dayOfMonth}".takeLast(2) + "/" + "0${dates.second.monthValue}".takeLast(2) +  " - ${"0${dates.second.hour}".takeLast(2)}:${"0${dates.second.minute}".takeLast(2)}"
                line.setImageBitmap(lineMap)

                //Update Altitude
                var alt = (moonPosition.first*100).roundToInt().toDouble()/100
                if (alt > 0) {
                    altTxt.text = "↑ ${alt}°"
                } else {
                    altTxt.text = "↓ ${-alt}°"
                }
                altImg.setImageBitmap(altSine)

                //Update azimuth
                var azi = (moonPosition.second*100).roundToInt().toDouble()/100
                when (azi) {
                    in 0f .. 22.5f -> aziTxt.text = "${azi}° N"
                    in 22.6f .. 67.5f -> aziTxt.text = "${azi}° NE"
                    in 67.6f .. 112.5f -> aziTxt.text = "${azi}° E"
                    in 112.6f .. 157.5f -> aziTxt.text = "${azi}° SE"
                    in 157.6f .. 202.5f -> aziTxt.text = "${azi}° S"
                    in 202.6f .. 247.5f -> aziTxt.text = "${azi}° SW"
                    in 247.6f .. 292.5f -> aziTxt.text = "${azi}° W"
                    in 292.6f .. 337.5f -> aziTxt.text = "${azi}° NW"
                    in 337.6f .. 360f -> aziTxt.text = "${azi}° N"
                }
            } catch (_: Exception) {}
        }
    }

    private fun getMoons(date: LocalDateTime, long: Double): Pair<LocalDateTime, LocalDateTime> {
        var max = 0.0
        var newDate = date
        var min = 1.0
        var fullDate = date

        for (i in 0 .. 31) {
            val tempDate = date.plusDays(i.toLong())
            var p = getFraction(jDate(Date.from(tempDate.toInstant(getTimeZoneOffset(long))).time.toDouble()))

            if (p < min) {
                min = p
                newDate = tempDate
            }
            if (p > max) {
                max = p
                fullDate = tempDate
            }
        }

        for (i in -24 .. 24) {
            val newTemp = newDate.plusHours(i.toLong())
            val fullTemp = fullDate.plusHours(i.toLong())
            var newP = getFraction(jDate(Date.from(newTemp.toInstant(getTimeZoneOffset(long))).time.toDouble()))
            var fullP = getFraction(jDate(Date.from(fullTemp.toInstant(getTimeZoneOffset(long))).time.toDouble()))

            if (newP < min) {
                min = newP
                newDate = newTemp
            }
            if (fullP > max) {
                max = fullP
                fullDate = fullTemp
            }
        }
        newDate = newDate.withMinute(0)
        fullDate = fullDate.withMinute(0)

        for (i in -60 .. 60) {
            val newTemp = newDate.plusMinutes(i.toLong())
            val fullTemp = fullDate.plusMinutes(i.toLong())
            var newP = getFraction(jDate(Date.from(newTemp.toInstant(getTimeZoneOffset(long))).time.toDouble()))
            var fullP = getFraction(jDate(Date.from(fullTemp.toInstant(getTimeZoneOffset(long))).time.toDouble()))

            if (newP < min) {
                min = newP
                newDate = newTemp
            }
            if (fullP > max) {
                max = fullP
                fullDate = fullTemp
            }
        }

        return Pair(newDate, fullDate)
    }

    private fun getAltArr(date: LocalDateTime, lat: Double, long: Double): MutableList<Double> {
        val arr = mutableListOf<Double>()
        var tempDate = date.withHour(0).withMinute(0)
        while (!tempDate.isAfter(date.withHour(0).withMinute(0).plusDays(1.toLong()))) {
            arr.add((calcMoonPos(tempDate, lat, long).first*100).roundToInt().toDouble()/100)
            tempDate = tempDate.plusMinutes(30)
        }

        return arr
    }

    private fun getAltDates(date: LocalDateTime, altArr: MutableList<Double>, lat: Double, long: Double): MutableList<LocalDateTime> {
        var temp = altArr[0] < 0
        var idx1 = -1
        var idx2 = -1
        for (i in altArr.indices) {
            if (temp == altArr[i] > 0) {
                if (idx1 < 0) {
                    idx1 = i
                } else {
                    idx2 = i
                }
            }
            temp = altArr[i] < 0
        }
        val Mm1 = (idx1/altArr.size.toFloat())*1440f
        val Mm2 = (idx2/altArr.size.toFloat())*1440f
        val Mm3 = (altArr.indexOf(altArr.minOrNull())/altArr.size.toFloat())*1440f
        val Mm4 = (altArr.indexOf(altArr.maxOrNull())/altArr.size.toFloat())*1440f
        var tempDate1 = date.withHour(0).withMinute(0).plusMinutes(Mm1.toLong())
        var tempDate2 = date.withHour(0).withMinute(0).plusMinutes(Mm2.toLong())
        var tempDate3 = date.withHour(0).withMinute(0).plusMinutes(Mm3.toLong())
        var tempDate4 = date.withHour(0).withMinute(0).plusMinutes(Mm4.toLong())

        var min1 = 128f
        var min2 = 128f
        var min3 = 128f
        var max4 = -128f
        var date1 = date
        var date2 = date
        var date3 = date
        var date4 = date

        for (i in -30 .. 30) {
            val tempDate = tempDate1.plusMinutes(i.toLong())
            val alt = abs(calcMoonPos(tempDate, lat, long).first)
            if (alt < min1) {
                min1 = alt.toFloat()
                date1 = tempDate
            }
        }

        for (i in -30 .. 30) {
            val tempDate = tempDate2.plusMinutes(i.toLong())
            val alt = abs(calcMoonPos(tempDate, lat, long).first)
            if (alt < min2) {
                min2 = alt.toFloat()
                date2 = tempDate
            }
        }

        for (i in -30 .. 30) {
            val tempDate = tempDate3.plusMinutes(i.toLong())
            val alt = calcMoonPos(tempDate, lat, long).first
            if (alt < min3) {
                min3 = alt.toFloat()
                date3 = tempDate
            }
        }

        for (i in -30 .. 30) {
            val tempDate = tempDate4.plusMinutes(i.toLong())
            val alt = calcMoonPos(tempDate, lat, long).first
            if (alt > max4) {
                max4 = alt.toFloat()
                date4 = tempDate
            }
        }

        return mutableListOf(date1, date2, date3, date4)
    }

    private fun drawDaysLine(p: Double, icon: Bitmap): Bitmap {
        val width = barBit.width
        val height = barBit.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawBitmap(barBit, 0f, 0f, null)

        val paint = Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }

        val iconHeight = height/6
        val offset = iconHeight*5*p.toFloat()

        canvas.drawOval(RectF(-20f, offset-20f, width+20f,offset+iconHeight+20f), paint)
        canvas.drawBitmap(Bitmap.createScaledBitmap(icon, width, iconHeight, false), 0f, offset, null)

        return bitmap
    }

    private fun drawAltGraph(ctx: Context, date: LocalDateTime, alt: Double, altArr: MutableList<Double>, altDates: MutableList<LocalDateTime>, icon: Bitmap): Bitmap {
        val width = 1920
        val height = 1080
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val off = 128f
        val txtOff = 64f
        val offY = height/2f

        var paint = Paint().apply {
            color = ContextCompat.getColor(ctx, R.color.cl3)
            style = Paint.Style.STROKE
            strokeWidth = 16f
            isAntiAlias = true
        }

        canvas.drawLine(off, offY, width-off, offY, paint)

        paint = Paint().apply {
            color = ContextCompat.getColor(ctx, R.color.cl3)
            style = Paint.Style.STROKE
            strokeWidth = 16f
            isAntiAlias = true
        }
        val dashPath = DashPathEffect(floatArrayOf(60f, 30f), 30f)
        paint.pathEffect = dashPath

        val font = ResourcesCompat.getFont(ctx, R.font.samsans)
        val textPaint = Paint().apply {
            color = ContextCompat.getColor(ctx, R.color.cl4)
            textSize = 150f
            isAntiAlias = true
            typeface = font
        }

        fun drawTxt(time: LocalDateTime, x: Float, y: Float) {
            val txt = "${"0${time.hour}".takeLast(2)}:${"0${time.minute}".takeLast(2)}"
            val parts = txt.split(":")
            val h = textPaint.measureText(parts[0])
            val sep = textPaint.measureText(":")
            val mm = textPaint.measureText(parts[1])

            var posX = x
            if (x+sep/2+mm > width) {
                posX += width-(x+sep/2+mm)
            }

            if (x-sep/2-h < 0) {
                posX -= (x-sep/2-h)
            }

            val startX = posX - (h + sep / 2)

            canvas.drawText(parts[0], startX, y, textPaint)
            canvas.drawText(":", startX + h, y, textPaint)
            canvas.drawText(parts[1], startX + h + sep, y, textPaint)
        }

        var lineX = (width - 2 * off) * (ChronoUnit.MINUTES.between(date.withHour(0).withMinute(0), altDates[0]).toFloat() / 1440f)
        canvas.drawLine(off + lineX, 3*txtOff, off + lineX, height - txtOff, paint)
        drawTxt(altDates[0], off + lineX, txtOff + textPaint.textSize/2)

        lineX = (width - 2 * off) * (ChronoUnit.MINUTES.between(date.withHour(0).withMinute(0), altDates[1]).toFloat() / 1440f)
        canvas.drawLine(off + lineX, 3*txtOff, off + lineX, height - txtOff, paint)
        drawTxt(altDates[1], off + lineX, txtOff + textPaint.textSize/2)

        lineX = (width - 2 * off) * (ChronoUnit.MINUTES.between(date.withHour(0).withMinute(0), altDates[2]).toFloat() / 1440f)
        canvas.drawLine(off + lineX, txtOff, off + lineX, height - 3*txtOff, paint)
        drawTxt(altDates[2], off + lineX, height - txtOff)

        lineX = (width - 2 * off) * (ChronoUnit.MINUTES.between(date.withHour(0).withMinute(0), altDates[3]).toFloat() / 1440f)
        canvas.drawLine(off + lineX, txtOff, off + lineX, height - 3*txtOff, paint)
        drawTxt(altDates[3], off + lineX, height - txtOff)

        val path = Path()
        val scX = (width - 2*off)/(altArr.size-1)
        val scY = (height - 2*off)/200f
        path.moveTo(off, (-altArr[0] * scY + offY).toFloat())
        for (i in altArr.indices) {
            val x = i * scX + off
            val y = -altArr[i] * scY + offY
            path.lineTo(x, y.toFloat())
        }

        paint = Paint().apply {
            color = ContextCompat.getColor(ctx, R.color.cl4)
            style = Paint.Style.STROKE
            strokeWidth = 16f
            isAntiAlias = true
        }
        canvas.drawPath(path, paint)

        paint = Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }

        val x = (width - 2*off)*(ChronoUnit.MINUTES.between(date.withHour(0).withMinute(0), date).toFloat()/1440f)
        val y = -alt * scY + offY
        val size = 30f
        canvas.drawOval(RectF(x-size, (y-off-size).toFloat(), x+2*off+size, (y-off+2*off+size).toFloat()), paint)
        canvas.drawBitmap(Bitmap.createScaledBitmap(icon, (off*2).toInt(), (off*2).toInt(), false), x, (y-off).toFloat(), null)

        return bitmap
    }

    fun jDate(t: Double): Double {
        return (t / 86400000) + 2440587.5
    }

    private fun cnt(d: Double): Double {
        var t = d%360
        if (t<0) { t+=360 }
        return t
    }

    fun getFraction(jDate: Double): Double {
        val toRad = PI / 180
        val t = (jDate - 2451545) / 36525
        val d = cnt(297.8501921 + 445267.1114034 * t - 0.0018819 * t * t + 1.0 / 545868.0 * t * t * t - 1.0 / 113065000.0 * t * t * t * t) * toRad
        val m = cnt(357.5291092 + 35999.0502909 * t - 0.0001536 * t * t + 1.0 / 24490000.0 * t * t * t) * toRad
        val mP = cnt(134.9633964 + 477198.8675055 * t + 0.0087414 * t * t + 1.0 / 69699.0 * t * t * t - 1.0 / 14712000.0 * t * t * t * t) * toRad
        val i = cnt(180 - d * 180 / Math.PI - 6.289 * sin(mP) + 2.1 * sin(m) - 1.274 * sin(2 * d - mP) - 0.658 * sin(2 * d) - 0.214 * sin(2 * mP) - 0.11 * sin(d)) * toRad

        return (1 + cos(i)) / 2
    }

    fun clipMoon(context: Context, drwID: Int, p: Float, dir: Int, dgr: Float, r:Boolean): Bitmap {
        val bmap: Bitmap = BitmapFactory.decodeResource(context.resources, drwID)
        val path = Path()
        val invPath = Path()
        val rectPath = Path()
        val matrix = Matrix()
        val ovalRect = RectF( bmap.width * (1 - (p / 100f)), 0f, bmap.width * (p / 100f), bmap.height.toFloat())

        invPath.addRect(0f, 0f, bmap.width.toFloat(), bmap.height.toFloat(), Path.Direction.CW)
        path.addOval(ovalRect, Path.Direction.CW)
        if (dir == 1) {
            rectPath.addRect(bmap.width / 2f, bmap.height.toFloat(), bmap.width.toFloat(), 0f, Path.Direction.CW)
        } else {
            rectPath.addRect(0f , bmap.height.toFloat(), bmap.width / 2f, 0f, Path.Direction.CW)
        }
        rectPath.close()
        path.close()
        path.op(rectPath, Path.Op.UNION)
        invPath.op(path, Path.Op.DIFFERENCE)

        matrix.setRotate(dgr, bmap.width / 2f, bmap.height / 2f)
        path.transform(matrix)
        invPath.transform(matrix)

        return if (p > 50) {
            applyPathToBitmap(bmap, invPath, r)
        } else {
            applyPathToBitmap(bmap, path, r)
        }
    }

    private fun applyPathToBitmap(bmap: Bitmap, path: Path, r: Boolean): Bitmap {
        val outputBitmap = Bitmap.createBitmap(bmap.width, bmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val paint1 = Paint(Paint.ANTI_ALIAS_FLAG)
        var rad: Float
        if (r) {
            rad = 20f
        } else {
            rad = 1f
        }
        paint1.maskFilter = BlurMaskFilter(rad, BlurMaskFilter.Blur.NORMAL)

        canvas.drawPath(path, paint1)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bmap, 0f, 0f, paint)

        return outputBitmap
    }

    fun overlapBitmapsAndRotate(bmap1: Bitmap, bmap2: Bitmap): Bitmap {
        val maxWidth = maxOf(bmap1.width, bmap2.width)
        val maxHeight = maxOf(bmap1.height, bmap2.height)
        val resultBitmap = Bitmap.createBitmap(maxWidth, maxHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)
        val matrix = Matrix()

        canvas.drawBitmap(bmap1, matrix, null)
        canvas.drawBitmap(bmap2, matrix, null)

        return resultBitmap
    }

    fun calcSunPos(date: LocalDateTime, latitude: Double, longitude: Double): Pair<Double, Double> {
        val timeZoneOffsetHours = ZoneId.systemDefault().rules.getOffset(date).totalSeconds / 3600.0
        val timeDate = date.minusHours(timeZoneOffsetHours.toLong())
        val julianDay = calcJday(timeDate)
        val julianCentury = (julianDay - 2451545.0) / 36525.0
        val geomMeanLongSun = (280.46646 + julianCentury * (36000.76983 + julianCentury * 0.0003032)) % 360
        val geomMeanAnomalySun = 357.52911 + julianCentury * (35999.05029 - 0.0001537 * julianCentury)
        val eccentEarthOrbit = 0.016708634 - julianCentury * (0.000042037 + 0.0000001267 * julianCentury)
        val sunEqOfCtr = sin(Math.toRadians(geomMeanAnomalySun)) * (1.914602 - julianCentury * (0.004817 + 0.000014 * julianCentury)) + sin(Math.toRadians(2 * geomMeanAnomalySun)) * (0.019993 - 0.000101 * julianCentury) + sin(Math.toRadians(3 * geomMeanAnomalySun)) * 0.000289
        val sunTrueLong = geomMeanLongSun + sunEqOfCtr
        val sunAppLong = sunTrueLong - 0.00569 - 0.00478 * sin(Math.toRadians(125.04 - 1934.136 * julianCentury))
        val meanObliqEcliptic = 23 + (26 + ((21.448 - julianCentury * (46.815 + julianCentury * (0.00059 - julianCentury * 0.001813)))) / 60) / 60
        val obliqCorr = meanObliqEcliptic + 0.00256 * cos(Math.toRadians(125.04 - 1934.136 * julianCentury))
        val sunDeclin = asin(sin(Math.toRadians(obliqCorr)) * sin(Math.toRadians(sunAppLong)))
        val varY = tan(Math.toRadians(obliqCorr / 2)) * tan(Math.toRadians(obliqCorr / 2))
        val eqOfTime = 4 * Math.toDegrees(varY * sin(2 * Math.toRadians(geomMeanLongSun)) - 2 * eccentEarthOrbit * sin(Math.toRadians(geomMeanAnomalySun)) + 4 * eccentEarthOrbit * varY * sin(Math.toRadians(geomMeanAnomalySun)) * cos(2 * Math.toRadians(geomMeanLongSun)) - 0.5 * varY * varY * sin(4 * Math.toRadians(geomMeanLongSun)) - 1.25 * eccentEarthOrbit * eccentEarthOrbit * sin(2 * Math.toRadians(geomMeanAnomalySun)))
        val trueSolarTime = (timeDate.hour + (timeDate.minute + timeDate.second / 60.0) / 60.0) * 15 + eqOfTime
        val hourAngle = trueSolarTime - 180 + longitude
        val solarZenith = acos(sin(Math.toRadians(latitude)) * sin(sunDeclin) + cos(Math.toRadians(latitude)) * cos(sunDeclin) * cos(Math.toRadians(hourAngle)))
        val solarAltitude = 90 - Math.toDegrees(solarZenith)
        val solarAzimuth = (Math.toDegrees(atan2(sin(Math.toRadians(hourAngle)), cos(Math.toRadians(hourAngle)) * sin(Math.toRadians(latitude)) - tan(sunDeclin) * cos(Math.toRadians(latitude)))) + 180) % 360

        return Pair(solarAltitude, solarAzimuth)
    }

    fun calcMoonPos(date: LocalDateTime, latitude: Double, longitude: Double): Pair<Double, Double> {
        val timeZoneOffsetHours = ZoneId.systemDefault().rules.getOffset(date).totalSeconds / 3600.0
        val timeDate = date.minusHours(timeZoneOffsetHours.toLong())
        val jd = calcJday(timeDate)
        val t = (jd - 2451545.0) / 36525.0
        val lPrime = (218.316 + 481267.8813 * t) % 360
        val d = (297.85 + 445267.1115 * t) % 360
        val m = (357.53 + 35999.0503 * t) % 360
        val mPrime = (134.96 + 477198.8673 * t) % 360
        val f = (93.272 + 483202.0175 * t) % 360
        val moonLongitude = lPrime + 6.29 * sin(Math.toRadians(mPrime)) + 1.27 * sin(Math.toRadians(2 * d - mPrime)) + 0.66 * sin(Math.toRadians(2 * d)) + 0.21 * sin(Math.toRadians(2 * mPrime - m))
        val moonLatitude = 5.13 * sin(Math.toRadians(f)) + 0.28 * sin(Math.toRadians(mPrime + f)) + 0.24 * sin(Math.toRadians(2 * d - mPrime + f)) + 0.23 * sin(Math.toRadians(2 * d + f)) + 0.21 * sin(Math.toRadians(mPrime - f)) + 0.17 * sin(Math.toRadians(d))
        val moonRightAscension = atan2(sin(Math.toRadians(moonLongitude)) * cos(Math.toRadians(23.44)), cos(Math.toRadians(moonLongitude))) * 180 / Math.PI
        val moonDeclination = asin(sin(Math.toRadians(moonLatitude)) * cos(Math.toRadians(23.44)) + cos(Math.toRadians(moonLatitude)) * sin(Math.toRadians(23.44)) * sin(Math.toRadians(moonLongitude))) * 180 / Math.PI
        val hourAngle = lst(timeDate, longitude) - moonRightAscension
        val moonAltitude = asin(sin(Math.toRadians(latitude)) * sin(Math.toRadians(moonDeclination)) + cos(Math.toRadians(latitude)) * cos(Math.toRadians(moonDeclination)) * cos(Math.toRadians(hourAngle))) * 180 / Math.PI
        val moonAzimuth = atan2(-sin(Math.toRadians(hourAngle)), tan(Math.toRadians(moonDeclination)) * cos(Math.toRadians(latitude)) - sin(Math.toRadians(latitude)) * cos(Math.toRadians(hourAngle))) * 180 / Math.PI

        return Pair(moonAltitude, (moonAzimuth + 360) % 360)
    }

    private fun lst(dateTime: LocalDateTime, longitude: Double): Double {
        val jd = calcJday(dateTime)
        val t = (jd - 2451545.0) / 36525.0
        val gmst = 280.46061837 + 360.98564736629 * (jd - 2451545.0) + 0.000387933 * t * t - t * t * t / 38710000.0
        var lst = gmst + longitude

        lst %= 360.0
        if (lst < 0) {
            lst += 360.0
        }

        return lst
    }

    private fun calcJday(date: LocalDateTime): Double {
        val year = date.year
        val month = date.monthValue
        val day = date.dayOfMonth
        val hour = date.hour
        val minute = date.minute
        val second = date.second
        val a = (14 - month) / 12
        val y = year + 4800 - a
        val m = month + 12 * a - 3
        val julianDay = day + ((153 * m + 2) / 5) + 365 * y + (y / 4) - (y / 100) + (y / 400) - 32045

        return julianDay + (hour - 12) / 24.0 + minute / 1440.0 + second / 86400
    }

    fun getAngle(sunalt: Double, sunaz: Double, moonalt: Double, moonaz: Double): Double {
        val dLon = Math.toRadians(moonaz - sunaz)
        val y = sin(dLon) * cos(Math.toRadians(moonalt))
        val x = cos(Math.toRadians(sunalt)) * sin(Math.toRadians(moonalt)) - sin(Math.toRadians(sunalt)) * cos(Math.toRadians(moonalt)) * cos(dLon)
        var brng = atan2(y, x)

        brng = Math.toDegrees(brng)
        brng = (brng + 360) % 360

        return brng
    }

    private fun getLocation(context: Context, position: FusedLocationProviderClient, callback: (latitude: Double, longitude: Double) -> Unit) {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 100)
            callback(0.0, 0.0)
            return
        }

        val locationTask = position.lastLocation

        locationTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val resultLocation = task.result
                if (resultLocation != null) {
                    val latitude = resultLocation.latitude
                    val longitude = resultLocation.longitude
                    callback(latitude, longitude)
                } else {
                    callback(0.0, 0.0)
                }
            } else {
                callback(0.0, 0.0)
            }
        }
    }

    fun getDir(phase: Int, moonAge: Double, percentage: Double): Int {
        return when (phase) {
            0 -> if (moonAge in 0f..14.765294f) { 0 } else { 1 }
            1 -> 0
            2 -> if (percentage > 50f) { 1 } else { 0 }
            3 -> 1
            4 -> if (moonAge > 14.765294f) { 1 } else { 0 }
            5 -> 1
            6 -> if (percentage > 50f) { 1 } else { 0 }
            7 -> 0
            else -> 0
        }
    }

    fun getPhase(context: Context, moonAge: Double): Array<Any> {
        return when (moonAge) {
            in 0f..1f -> arrayOf(context.getString(R.string.new_moon), 0)
            in 1f..6.382647f -> arrayOf(context.getString(R.string.waxing_crescent), 1)
            in 6.382647f..8.382647f -> arrayOf(context.getString(R.string.first_quarter), 2)
            in 8.382647f..13.765294f -> arrayOf(context.getString(R.string.waxing_gibbous), 3)
            in 13.765294f..15.765294f -> arrayOf(context.getString(R.string.full_moon), 4)
            in 15.765294f..21.147942f -> arrayOf(context.getString(R.string.waning_gibbous), 5)
            in 21.147942f..23.147942f -> arrayOf(context.getString(R.string.third_quarter), 6)
            in 23.147942f..28.530588f -> arrayOf(context.getString(R.string.waning_crescent), 7)
            else -> arrayOf(context.getString(R.string.new_moon), 0)
        }
    }

    private fun arrow(id: Int) {
        time = false

        runOnUiThread {
            when (id) {
                0 -> {editDate[0]+=1}
                1 -> {editDate[1]+=1}
                2 -> {editDate[2]+=1}
                3 -> {editDate[3]+=1}
                4 -> {editDate[4]+=1}
                5 -> {editDate[0]-=1}
                6 -> {editDate[1]-=1}
                7 -> {editDate[2]-=1}
                8 -> {editDate[3]-=1}
                9 -> {editDate[4]-=1}
            }
        }

        val length = if (editDate[0] % 4 == 0 && editDate[1] == 2) {
            mLength[12]
        } else {
            try {
                mLength[editDate[1] - 1]
            } catch (_:Exception) {
                mLength[0]
            }
        }
        println(length)
        val pLength = try {
            mLength[editDate[1]-2]
        } catch (_:Exception) {
            mLength[0]
        }
        // 4-> Minuto 3-> Ora 2-> Giorno 1-> Mese 0-> Anno
        if (editDate[4] < 0) {
            editDate[4] = 59
            editDate[3] -= 1
        } else if (editDate[4] > 59) {
            editDate[4] = 0
            editDate[3] += 1
        }
        if (editDate[3] < 0) {
            editDate[3] = 23
            editDate[2] -= 1
        } else if (editDate[3] > 23) {
            editDate[3] = 0
            editDate[2] += 1
        }
        if (editDate[2] < 1) {
            editDate[2] = pLength
            editDate[1] -= 1
        } else if (editDate[2] > length) {
            editDate[2] = 1
            editDate[1] += 1
        }
        if (editDate[1] < 1) {
            editDate[1] = 12
            editDate[0] -= 1
            if (editDate[2] > length) {
                editDate[2] = length-1
            }
        } else if (editDate[1] > 12) {
            editDate[1] = 1
            editDate[0] += 1
        }
        if (editDate[0] < 0) {
            editDate[0] = 1
        } else if (editDate[1] > 5000) {
            editDate[0] = 4999
        }

        runOnUiThread {
            ytxt.text = "000${editDate[0]}".takeLast(4)
            mtxt.text = "0${editDate[1]}".takeLast(2)
            dtxt.text = "0${editDate[2]}".takeLast(2)
            htxt.text = "0${editDate[3]}".takeLast(2)
            mmtxt.text = "0${editDate[4]}".takeLast(2)
        }
        main()
    }

    private fun sync() {
        time = true
        main()
    }

    private fun getTimeZoneOffset(longitude: Double): ZoneOffset {
        val offsetHours = (longitude / 15).toInt()
        val offsetMinutes = ((longitude / 15 - offsetHours) * 60).toInt()
        val sign = if (offsetHours >= 0) 1 else -1

        return ZoneOffset.ofHoursMinutes(sign * offsetHours, sign * offsetMinutes)
    }

    private fun toggleOpt() {
        optBool = !optBool
        val params = options.layoutParams as LinearLayout.LayoutParams

        if (optBool) {
            params.weight = .3f
        } else {
            params.weight = 0f
        }

        options.layoutParams = params
    }
}