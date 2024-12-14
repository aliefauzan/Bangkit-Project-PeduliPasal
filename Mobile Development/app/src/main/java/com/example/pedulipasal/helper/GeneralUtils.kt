package com.example.pedulipasal.helper

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getTimeFormat(date: Date?): String {
    val dateFormat = SimpleDateFormat("HH.mm", Locale.getDefault())
    return dateFormat.format(date ?: dateFormat.parse("00.00"))
}

fun showLocalTime(date: Date?): Date {
    return SimpleDateFormat("HH.mm", Locale.getDefault()).parse(getTimeFormat(date)) ?: Date()
}

fun getDateFormat(date: Date?): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return dateFormat.format(date ?: dateFormat.parse("01/01/1970"))
}

fun getDayOfWeek(date: Date?): String? {
    val dayOfWeekFormatter = SimpleDateFormat("EEE", Locale.US)
    val dayOfWeek = date?.let { dayOfWeekFormatter.format(it) }
    return dayOfWeek?.uppercase(Locale.US)
}

fun filteredString(input: String): String {
    val listToBeRemoved = listOf("*", "\"[object Object]\"")
    var filteredInput = input
    listToBeRemoved.forEach { char ->
        filteredInput = input.replace(char, "")
    }
    return filteredInput
}

fun removeNewLine(input: String): String {
    return input.replace("\n", " ")
}

