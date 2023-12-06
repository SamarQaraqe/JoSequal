package com.josequal.maps.utils

import android.util.Xml
import com.google.android.gms.maps.model.LatLng
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

class KMLParser {

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): List<LatLng> {
        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(inputStream, null)

        val coordinates = mutableListOf<LatLng>()

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    if (parser.name == "coordinates") {
                        val coordinatesString = parser.nextText()
                        parseCoordinates(coordinatesString, coordinates)
                    }
                }
            }
            eventType = parser.next()
        }

        return coordinates
    }

    private fun parseCoordinates(coordinatesString: String, coordinates: MutableList<LatLng>) {
        val parts = coordinatesString.trim().split("\\s+".toRegex())
        for (part in parts) {
            val latLngParts = part.split(",")
            if (latLngParts.size == 2) {
                val latitude = latLngParts[1].toDouble()
                val longitude = latLngParts[0].toDouble()
                coordinates.add(LatLng(latitude, longitude))
            }
        }
    }
}
