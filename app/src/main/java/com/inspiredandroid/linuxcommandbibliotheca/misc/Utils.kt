package com.inspiredandroid.linuxcommandbibliotheca.misc

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.inputmethod.InputMethodManager

import com.inspiredandroid.linuxcommandbibliotheca.R

import java.text.Normalizer

/**
 * Created by Simon Schubert
 */
object Utils {

    val PACKAGE_BURGER = "com.inspiredandroid.stopandroll"
    val PACKAGE_LINUXREMOTE = "com.inspiredandroid.linuxcontrolcenter"
    val PACKAGE_LINUXREMOTE_PRO = "com.inspiredandroid.linuxcontrolcenterpro"
    val PACKAGE_ORCGENOCIDE = "com.inspiredandroid.orcgenocide"
    val PACKAGE_BIMO = "com.inspiredandroid.bimo"
    val PACKAGE_QUIZ = "com.inspiredandroid.twoplayerquiz"
    val PACKAGE_COMMANDLIBRARY = "com.inspiredandroid.linuxcommandbibliotheca"

    /**
     * Check if app is installed
     *
     * @param packageName
     * @return
     */
    fun isAppInstalled(context: Context, packageName: String): Boolean {
        val pm = context.packageManager
        var installed: Boolean
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            installed = true
        } catch (e: PackageManager.NameNotFoundException) {
            installed = false
        }
        return installed
    }
}

/**
 * Highlight the the appearance of search query inside originalText
 */
fun String.highlightQueryInsideText(context: Context?, query: String): SearchResult {
    if (query.isEmpty() || this.isEmpty() || context == null) {
        return SearchResult(this, arrayListOf())
    }

    val indexes = arrayListOf<Int>()
    val normalizedText = Normalizer.normalize(this, Normalizer.Form.NFD).replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "").toLowerCase()
    val highlighted = SpannableString(this)

    var start = normalizedText.indexOf(query)
    while (start >= 0) {
        val spanStart = Math.min(start, this.length)
        val spanEnd = Math.min(start + query.length, this.length)

        if (spanStart == -1 || spanEnd == -1) {
            break
        }

        highlighted.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.ab_primary)), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        start = normalizedText.indexOf(query, spanEnd)
        indexes.add(spanStart)
    }

    return SearchResult(highlighted, indexes)
}

fun Activity.hideKeyboard() {
    val view = this.currentFocus
    view?.let {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

class SearchResult(var result: CharSequence, var indexes: ArrayList<Int>)

class SearchIndex(var pos: Int, var index: Int)
