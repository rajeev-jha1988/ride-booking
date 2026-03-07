package org.example.bookride.constant

import com.uber.h3core.util.LatLng

object BookRideConstant {
    const val RIDE_REQUEST_OFFER_KEY = "offer_timer:{ride_id}:{driver_id}:{latitude}:{longitude}"
    const val DRIVER_STATUS_KEY = "driver:{driver_id}:status"

    val DELHI_BOUNDRIES =
        listOf(
            LatLng(28.88, 77.10), // North
            LatLng(28.60, 77.35), // East (Yamuna side)
            LatLng(28.40, 77.20), // South
            LatLng(28.50, 76.85), // West
            LatLng(28.88, 77.10), // Close the polygon
        )
    val MUMBAI_BOUNDARIES =
        listOf(
            LatLng(19.27, 72.84), // North-West (Dahisar Check Naka)
            LatLng(19.17, 72.96), // North-East (Mulund Check Naka)
            LatLng(19.03, 72.94), // East (Trombay/Creek area)
            LatLng(18.89, 72.81), // South (Colaba Point / Gateway area)
            LatLng(19.04, 72.81), // West Coast (Bandra / Juhu)
            LatLng(19.20, 72.79), // West Coast (Borivali / Gorai)
            LatLng(19.27, 72.84), // Close the polygon
        )
}
