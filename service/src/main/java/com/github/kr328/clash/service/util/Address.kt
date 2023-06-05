package com.github.kr328.clash.service.util

import com.github.kr328.clash.common.log.Log
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.net.NetworkInterface

fun InetAddress.asSocketAddressText(port: Int): String {
    return when (this) {
        is Inet6Address ->
            "[${numericToTextFormat(this.address)}]:$port"
        is Inet4Address ->
            "${this.hostAddress}:$port"
        else -> throw IllegalArgumentException("Unsupported Inet type ${this.javaClass}")
    }
}

fun getHostIp(): String {
    val networkInterfaces = NetworkInterface.getNetworkInterfaces()
    val ips = ArrayList<String>()

    while (networkInterfaces?.hasMoreElements() == true) {
        val net = networkInterfaces.nextElement()
        if (net.isLoopback) continue

        val addresses = net.inetAddresses
        while (addresses.hasMoreElements()) {
            val address = addresses.nextElement()
            if (address is Inet6Address || address?.hostAddress == null) continue

            val ip = address.hostAddress as String
            // 找到, 全部打印
            Log.i("find IP: $ip")
            ips.add(ip)
        }
    }

    return ips.first()
}

private const val INT16SZ = 2
private const val INADDRSZ = 16
private fun numericToTextFormat(src: ByteArray): String {
    val sb = StringBuilder(39)
    for (i in 0 until INADDRSZ / INT16SZ) {
        sb.append(
            Integer.toHexString(
                src[i shl 1].toInt() shl 8 and 0xff00
                        or (src[(i shl 1) + 1].toInt() and 0xff)
            )
        )
        if (i < INADDRSZ / INT16SZ - 1) {
            sb.append(":")
        }
    }
    return sb.toString()
}

