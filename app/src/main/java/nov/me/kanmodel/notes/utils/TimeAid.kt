package nov.me.kanmodel.notes.utils

import android.annotation.SuppressLint

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by KanModel on 2017/12/29.
 * 时间计算相关方法
 */

object TimeAid {
    val nowTime: Long
        get() = System.currentTimeMillis()

    val backupDateFormat: SimpleDateFormat
        get() = fromSkeleton("yyyy-MM-dd_HHmmss", Locale.US)

    val csvDateFormat: SimpleDateFormat
        get() = fromSkeleton("yyyy-MM-dd", Locale.US)

    private fun fromSkeleton(skeleton: String, locale: Locale): SimpleDateFormat {
        val df = SimpleDateFormat(skeleton, locale)
        df.timeZone = TimeZone.getTimeZone("UTC")
        return df
    }

    /**
     * @param time 字符串类型的时间戳
     * @return 时间字符串
     */
    private fun stampToDate(time: String): String {
        val res: String
        @SuppressLint("SimpleDateFormat") val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val lt = java.lang.Long.valueOf(time)
        val date = Date(lt)
        res = simpleDateFormat.format(date)
        return res
    }

    /**
     * @param time long类型的时间戳
     * @return 时间字符串
     */
    fun stampToDate(time: Long): String {
        return stampToDate(time.toString())
    }

    /**
     * 将时间转换为时间戳
     */
    fun dateToStamp(s: String): Long {
        @SuppressLint("SimpleDateFormat") val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var date: Date? = null
        var ts: Long = 0
        try {
            date = simpleDateFormat.parse(s)
            ts = date!!.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return ts
    }

    fun getTimeStamp(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long {
        val cal = Calendar.getInstance()
        cal.set(year, month, day, hour, minute)
        return cal.timeInMillis
    }

    fun getDiffDay(dstTime: Long, nowTime: Long): Long {
        return getDiff(dstTime, nowTime) / (1000 * 60 * 60 * 24)
    }

    fun getDiffDay(dstTime: Long): Long {
        return getDiff(dstTime, nowTime) / (1000 * 60 * 60 * 24)
    }

    fun getDiffHour(dstTime: Long, nowTime: Long): Long {
        return (getDiff(dstTime, nowTime) - getDiffDay(dstTime, nowTime) * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
    }

    fun getDiffHour(dstTime: Long): Long {
        val nowTime = nowTime
        return (getDiff(dstTime, nowTime) - getDiffDay(dstTime, nowTime) * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
    }

    fun getDiffMinutes(dstTime: Long, nowTime: Long): Long {
        return (getDiff(dstTime, nowTime) - getDiffDay(dstTime, nowTime) * (1000 * 60 * 60 * 24)
                - getDiffHour(dstTime, nowTime) * (1000 * 60 * 60)) / (1000 * 60)
    }

    fun getDiffMinutes(dstTime: Long): Long {
        val nowTime = nowTime
        return (getDiff(dstTime, nowTime) - getDiffDay(dstTime, nowTime) * (1000 * 60 * 60 * 24)
                - getDiffHour(dstTime, nowTime) * (1000 * 60 * 60)) / (1000 * 60)
    }

    fun getDiff(dstTime: Long, nowTime: Long): Long {
        return dstTime - nowTime
    }
}
