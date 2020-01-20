package ir.logicbase.jalalicalendar.uti

import kotlin.math.*

/**
 * Utility functions to calculate leap year with high precision
 */
internal object CalendarLeapUtils {

    fun isJalaliLeapYear(year: Int): Boolean {
        return persianaToJd(year + 1.toDouble(), 1.0, 1.0) -
                persianaToJd(year.toDouble(), 1.0, 1.0) > 365
    }

    private fun isGregorianLeapYear(year: Double): Boolean = year % 4 == 0.0 &&
            !(year % 100 == 0.0 && year % 400 != 0.0)

    private fun mod(a: Double, b: Double): Double = a - b * floor(a / b)

    private const val GREGORIAN_EPOCH = 1721425.5

    private fun gregorianToJd(
        year: Double,
        month: Double,
        day: Double
    ): Double {
        return GREGORIAN_EPOCH - 1 +
                365 * (year - 1) +
                floor((year - 1) / 4) +
                -floor((year - 1) / 100) +
                floor((year - 1) / 400) +
                floor(
                    (367 * month - 362) / 12 +
                            (if (month <= 2) 0 else if (isGregorianLeapYear(year)) -1 else -2) + day
                )
    }

    private fun jdToGregorian(jd: Double): DoubleArray {
        val wjd: Double = floor(jd - 0.5) + 0.5
        val depoch = wjd - GREGORIAN_EPOCH
        val quadricent = floor(depoch / 146097)
        val dqc = mod(depoch, 146097.0)
        val cent = floor(dqc / 36524)
        val dcent = mod(dqc, 36524.0)
        val quad = floor(dcent / 1461)
        val dquad = mod(dcent, 1461.0)
        val yindex = floor(dquad / 365)
        var year = quadricent * 400 + cent * 100 + quad * 4 + yindex
        if (!(cent == 4.0 || yindex == 4.0)) {
            year++
        }
        val yearday = wjd - gregorianToJd(year, 1.0, 1.0)
        val leapadj = (when {
            wjd < gregorianToJd(year, 3.0, 1.0) -> 0
            isGregorianLeapYear(year) -> 1
            else -> 2
        }).toDouble()
        val month = floor(((yearday + leapadj) * 12 + 373) / 367)
        val day = wjd - gregorianToJd(year, month, 1.0) + 1
        return doubleArrayOf(year, month, day)
    }

    private var JDE0tab1000 = arrayOf(
        doubleArrayOf(1721139.29189, 365242.13740, 0.06134, 0.00111, -0.00071),
        doubleArrayOf(1721233.25401, 365241.72562, -0.05323, 0.00907, 0.00025),
        doubleArrayOf(1721325.70455, 365242.49558, -0.11677, -0.00297, 0.00074),
        doubleArrayOf(1721414.39987, 365242.88257, -0.00769, -0.00933, -0.00006)
    )
    private var JDE0tab2000 = arrayOf(
        doubleArrayOf(2451623.80984, 365242.37404, 0.05169, -0.00411, -0.00057),
        doubleArrayOf(2451716.56767, 365241.62603, 0.00325, 0.00888, -0.00030),
        doubleArrayOf(2451810.21715, 365242.01767, -0.11575, 0.00337, 0.00078),
        doubleArrayOf(2451900.05952, 365242.74049, -0.06223, -0.00823, 0.00032)
    )

    private fun equinox(year: Double, which: Int): Double {
        val y: Double
        var j: Int
        val jde0tab: Array<DoubleArray>
        if (year < 1000) {
            jde0tab = JDE0tab1000
            y = year / 1000
        } else {
            jde0tab = JDE0tab2000
            y = (year - 2000) / 1000
        }
        val jde0 = jde0tab[which][0] +
                jde0tab[which][1] * y +
                jde0tab[which][2] * y * y +
                jde0tab[which][3] * y * y * y +
                jde0tab[which][4] * y * y * y * y
        val t = (jde0 - 2451545.0) / 36525
        val w = 35999.373 * t - 2.47
        val deltaL = 1 + 0.0334 * dCos(w) + 0.0007 * dCos(2 * w)
        var s = 0.0
        var i = 0.also { j = it }
        while (i < 24) {
            s += equinoxpTerms[j] * dCos(equinoxpTerms[j + 1] + equinoxpTerms[j + 2] * t)
            j += 3
            i++
        }
        return jde0 + s * 0.00001 / deltaL
    }

    private var equinoxpTerms = doubleArrayOf(
        485.0, 324.96, 1934.136, 203.0, 337.23, 32964.467, 199.0, 342.08, 20.186, 182.0, 27.85, 445267.112,
        156.0, 73.14, 45036.886, 136.0, 171.52, 22518.443, 77.0, 222.54, 65928.934, 74.0, 296.72, 3034.906,
        70.0, 243.58, 9037.513, 58.0, 119.81, 33718.147, 52.0, 297.17, 150.678, 50.0, 21.02, 2281.226,
        45.0, 247.54, 29929.562, 44.0, 325.15, 31555.956, 29.0, 60.93, 4443.417, 18.0, 155.12, 67555.328,
        17.0, 288.79, 4562.452, 16.0, 198.04, 62894.029, 14.0, 199.76, 31436.921, 12.0, 95.39, 14577.848,
        12.0, 287.11, 31931.756, 12.0, 320.81, 34777.259, 9.0, 227.73, 1222.114, 8.0, 15.45, 16859.074
    )

    private fun tehranEquinox(year: Double): Double {
        val equJED: Double = equinox(year, 0)
        val equJD = equJED - deltat(year) / (24 * 60 * 60)
        val equAPP = equJD + equationOfTime(equJED)
        val dtTehran: Double = (52 + 30 / 60.0 + 0 / (60.0 * 60.0)) / 360
        return equAPP + dtTehran
    }

    private fun deltat(year: Double): Double {
        var dt: Double
        if (year in 1620.0..2000.0) {
            val i = floor((year - 1620) / 2).toInt()
            val f = (year - 1620) / 2 - i
            dt = deltaTtab[i] + (deltaTtab[i + 1] - deltaTtab[i]) * f
        } else {
            val t = (year - 2000) / 100
            if (year < 948) {
                dt = 2177 + 497 * t + 44.1 * t * t
            } else {
                dt = 102 + 102 * t + 25.3 * t * t
                if (year > 2000 && year < 2100) {
                    dt += 0.37 * (year - 2100)
                }
            }
        }
        return dt
    }

    private val deltaTtab = doubleArrayOf(
        121.0, 112.0, 103.0, 95.0,
        88.0, 82.0, 77.0, 72.0, 68.0, 63.0, 60.0, 56.0, 53.0, 51.0, 48.0, 46.0, 44.0, 42.0, 40.0, 38.0, 35.0, 33.0,
        31.0, 29.0, 26.0, 24.0, 22.0, 20.0, 18.0, 16.0, 14.0, 12.0, 11.0, 10.0, 9.0, 8.0, 7.0, 7.0, 7.0, 7.0, 7.0,
        7.0, 8.0, 8.0, 9.0, 9.0, 9.0, 9.0, 9.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 11.0, 11.0, 11.0,
        11.0, 11.0, 12.0, 12.0, 12.0, 12.0, 13.0, 13.0, 13.0, 14.0, 14.0, 14.0, 14.0, 15.0, 15.0, 15.0, 15.0, 15.0,
        16.0, 16.0, 16.0, 16.0, 16.0, 16.0, 16.0, 16.0, 15.0, 15.0, 14.0, 13.0, 13.1, 12.5, 12.2, 12.0, 12.0, 12.0,
        12.0, 12.0, 12.0, 11.9, 11.6, 11.0, 10.2, 9.2, 8.2, 7.1, 6.2, 5.6, 5.4, 5.3, 5.4, 5.6, 5.9, 6.2, 6.5, 6.8, 7.1,
        7.3, 7.5, 7.6, 7.7, 7.3, 6.2, 5.2, 2.7, 1.4, -1.2, -2.8, -3.8, -4.8, -5.5, -5.3, -5.6, -5.7, -5.9, -6.0, -6.3,
        -6.5, -6.2, -4.7, -2.8, -0.1, 2.6, 5.3, 7.7, 10.4, 13.3, 16.0, 18.2, 20.2, 21.1, 22.4, 23.5, 23.8, 24.3, 24.0,
        23.9, 23.9, 23.7, 24.0, 24.3, 25.3, 26.2, 27.3, 28.2, 29.1, 30.0, 30.7, 31.4, 32.2, 33.1, 34.0, 35.0, 36.5,
        38.3, 40.2, 42.2, 44.5, 46.5, 48.5, 50.5, 52.2, 53.8, 54.9, 55.8, 56.9, 58.3, 60.0, 61.6, 63.0, 65.0, 66.6
    )

    private const val J2000 = 2451545.0
    private const val JulianCentury = 36525.0
    private const val JulianMillennium = JulianCentury * 10

    private fun equationOfTime(jd: Double): Double {
        val tau: Double = (jd - J2000) / JulianMillennium
        var l0 = 280.4664567 + 360007.6982779 * tau +
                0.03032028 * tau * tau +
                tau * tau * tau / 49931 +
                -(tau * tau * tau * tau / 15300) +
                -(tau * tau * tau * tau * tau / 2000000)
        l0 = fixAngle(l0)
        val alpha: Double = sunPos(jd)[10]
        val deltaPsi: Double = nutation(jd)[0]
        val epsilon: Double = obliqeq(jd) + nutation(jd)[1]
        var e = l0 + -0.0057183 + -alpha + deltaPsi * dCos(epsilon)
        e -= 20.0 * floor(e / 20.0)
        e /= (24 * 60)
        return e
    }

    private fun nutation(jd: Double): DoubleArray {
        var t2: Double
        val t3: Double
        var dp = 0.0
        var de = 0.0
        val t = (jd - 2451545.0) / 36525.0
        val ta = DoubleArray(5)
        t3 = t * (t * t.also { t2 = it })
        ta[0] = dtr(297.850363 + 445267.11148 * t - 0.0019142 * t2 + t3 / 189474.0)
        ta[1] = dtr(357.52772 + 35999.05034 * t - 0.0001603 * t2 - t3 / 300000.0)
        ta[2] = dtr(134.96298 + 477198.867398 * t + 0.0086972 * t2 + t3 / 56250.0)
        ta[3] = dtr(93.27191 + 483202.017538 * t - 0.0036825 * t2 + t3 / 327270)
        ta[4] = dtr(125.04452 - 1934.136261 * t + 0.0020708 * t2 + t3 / 450000.0)
        var i = 0
        while (i < 5) {
            ta[i] = fixAngr(ta[i])
            i++
        }
        val to10 = t / 10.0
        i = 0
        while (i < 63) {
            var ang = 0.0
            var j = 0
            while (j < 5) {
                if (nutArgMult[i * 5 + j] != 0.0) {
                    ang += nutArgMult[i * 5 + j] * ta[j]
                }
                j++
            }
            dp += (nutArgCoeff[i * 4 + 0] + nutArgCoeff[i * 4 + 1] * to10) * sin(ang)
            de += (nutArgCoeff[i * 4 + 2] + nutArgCoeff[i * 4 + 3] * to10) * cos(ang)
            i++
        }
        val deltaPsi = dp / (3600.0 * 10000.0)
        val deltaEpsilon = de / (3600.0 * 10000.0)
        return doubleArrayOf(deltaPsi, deltaEpsilon)
    }

    private val nutArgCoeff = doubleArrayOf(
        -171996.0,
        -1742.0, 92095.0, 89.0, -13187.0, -16.0, 5736.0, -31.0, -2274.0, -2.0, 977.0, -5.0, 2062.0, 2.0, -895.0,
        5.0, 1426.0, -34.0, 54.0, -1.0, 712.0, 1.0, -7.0, 0.0, -517.0, 12.0, 224.0, -6.0, -386.0, -4.0, 200.0, 0.0,
        -301.0, 0.0, 129.0, -1.0, 217.0, -5.0, -95.0, 3.0, -158.0, 0.0, 0.0, 0.0, 129.0, 1.0, -70.0, 0.0, 123.0,
        0.0, -53.0, 0.0, 63.0, 0.0, 0.0, 0.0, 63.0, 1.0, -33.0, 0.0, -59.0, 0.0, 26.0, 0.0, -58.0, -1.0, 32.0, 0.0,
        -51.0, 0.0, 27.0, 0.0, 48.0, 0.0, 0.0, 0.0, 46.0, 0.0, -24.0, 0.0, -38.0, 0.0, 16.0, 0.0, -31.0, 0.0, 13.0,
        0.0, 29.0, 0.0, 0.0, 0.0, 29.0, 0.0, -12.0, 0.0, 26.0, 0.0, 0.0, 0.0, -22.0, 0.0, 0.0, 0.0, 21.0, 0.0, -10.0,
        0.0, 17.0, -1.0, 0.0, 0.0, 16.0, 0.0, -8.0, 0.0, -16.0, 1.0, 7.0, 0.0, -15.0, 0.0, 9.0, 0.0, -13.0, 0.0, 7.0,
        0.0, -12.0, 0.0, 6.0, 0.0, 11.0, 0.0, 0.0, 0.0, -10.0, 0.0, 5.0, 0.0, -8.0, 0.0, 3.0, 0.0, 7.0, 0.0, -3.0, 0.0,
        -7.0, 0.0, 0.0, 0.0, -7.0, 0.0, 3.0, 0.0, -7.0, 0.0, 3.0, 0.0, 6.0, 0.0, 0.0, 0.0, 6.0, 0.0, -3.0, 0.0, 6.0,
        0.0, -3.0, 0.0, -6.0, 0.0, 3.0, 0.0, -6.0, 0.0, 3.0, 0.0, 5.0, 0.0, 0.0, 0.0, -5.0, 0.0, 3.0, 0.0, -5.0, 0.0,
        3.0, 0.0, -5.0, 0.0, 3.0, 0.0, 4.0, 0.0, 0.0, 0.0, 4.0, 0.0, 0.0, 0.0, 4.0, 0.0, 0.0, 0.0, -4.0, 0.0, 0.0,
        0.0, -4.0, 0.0, 0.0, 0.0, -4.0, 0.0, 0.0, 0.0, 3.0, 0.0, 0.0, 0.0, -3.0, 0.0, 0.0, 0.0, -3.0, 0.0, 0.0, 0.0,
        -3.0, 0.0, 0.0, 0.0, -3.0, 0.0, 0.0, 0.0, -3.0, 0.0, 0.0, 0.0, -3.0, 0.0, 0.0, 0.0, -3.0, 0.0, 0.0, 0.0
    )

    private val nutArgMult = doubleArrayOf(
        0.0, 0.0, 0.0, 0.0, 1.0, -2.0, 0.0, 0.0, 2.0, 2.0, 0.0, 0.0, 0.0,
        2.0, 2.0, 0.0, 0.0, 0.0, 0.0, 2.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, -2.0, 1.0, 0.0, 2.0, 2.0,
        0.0, 0.0, 0.0, 2.0, 1.0, 0.0, 0.0, 1.0, 2.0, 2.0, -2.0, -1.0, 0.0, 2.0, 2.0, -2.0, 0.0, 1.0, 0.0, 0.0, -2.0,
        0.0, 0.0, 2.0, 1.0, 0.0, 0.0, -1.0, 2.0, 2.0, 2.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 2.0, 0.0, -1.0,
        2.0, 2.0, 0.0, 0.0, -1.0, 0.0, 1.0, 0.0, 0.0, 1.0, 2.0, 1.0, -2.0, 0.0, 2.0, 0.0, 0.0, 0.0, 0.0, -2.0, 2.0, 1.0,
        2.0, 0.0, 0.0, 2.0, 2.0, 0.0, 0.0, 2.0, 2.0, 2.0, 0.0, 0.0, 2.0, 0.0, 0.0, -2.0, 0.0, 1.0, 2.0, 2.0, 0.0, 0.0,
        0.0, 2.0, 0.0, -2.0, 0.0, 0.0, 2.0, 0.0, 0.0, 0.0, -1.0, 2.0, 1.0, 0.0, 2.0, 0.0, 0.0, 0.0, 2.0, 0.0, -1.0, 0.0,
        1.0, -2.0, 2.0, 0.0, 2.0, 2.0, 0.0, 1.0, 0.0, 0.0, 1.0, -2.0, 0.0, 1.0, 0.0, 1.0, 0.0, -1.0, 0.0, 0.0, 1.0, 0.0,
        0.0, 2.0, -2.0, 0.0, 2.0, 0.0, -1.0, 2.0, 1.0, 2.0, 0.0, 1.0, 2.0, 2.0, 0.0, 1.0, 0.0, 2.0, 2.0, -2.0, 1.0, 1.0,
        0.0, 0.0, 0.0, -1.0, 0.0, 2.0, 2.0, 2.0, 0.0, 0.0, 2.0, 1.0, 2.0, 0.0, 1.0, 0.0, 0.0, -2.0, 0.0, 2.0, 2.0, 2.0,
        -2.0, 0.0, 1.0, 2.0, 1.0, 2.0, 0.0, -2.0, 0.0, 1.0, 2.0, 0.0, 0.0, 0.0, 1.0, 0.0, -1.0, 1.0, 0.0, 0.0, -2.0,
        -1.0, 0.0, 2.0, 1.0, -2.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 2.0, 2.0, 1.0, -2.0, 0.0, 2.0, 0.0, 1.0, -2.0, 1.0,
        0.0, 2.0, 1.0, 0.0, 0.0, 1.0, -2.0, 0.0, -1.0, 0.0, 1.0, 0.0, 0.0, -2.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 1.0, 2.0, 0.0, -1.0, -1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, -1.0, 1.0, 2.0,
        2.0, 2.0, -1.0, -1.0, 2.0, 2.0, 0.0, 0.0, -2.0, 2.0, 2.0, 0.0, 0.0, 3.0, 2.0, 2.0, 2.0, -1.0, 0.0, 2.0, 2.0
    )

    private fun fixAngr(a: Double): Double = a - 2 * Math.PI * floor(a / (2 * Math.PI))

    private fun sunPos(jd: Double): DoubleArray {
        val t: Double = (jd - J2000) / JulianCentury
        val t2 = t * t
        var l0 = 280.46646 + 36000.76983 * t + 0.0003032 * t2
        l0 = fixAngle(l0)
        var m = 357.52911 + 35999.05029 * t + -0.0001537 * t2
        m = fixAngle(m)
        val e = 0.016708634 + -0.000042037 * t + -0.0000001267 * t2
        val c = (1.914602 + -0.004817 * t + -0.000014 * t2) * dSin(m) +
                (0.019993 - 0.000101 * t) * dSin(2 * m) + 0.000289 * dSin(3 * m)
        val sunLong = l0 + c
        val sunAnomaly = m + c
        val sunR = 1.000001018 * (1 - e * e) / (1 + e * dCos(sunAnomaly))
        val omega = 125.04 - 1934.136 * t
        val lambda = sunLong + -0.00569 + -0.00478 * dSin(omega)
        val epsilon0: Double = obliqeq(jd)
        val epsilon = epsilon0 + 0.00256 * dCos(omega)
        var alpha = rtd(atan2(dCos(epsilon0) * dSin(sunLong), dCos(sunLong)))
        alpha = fixAngle(alpha)
        val delta = rtd(asin(dSin(epsilon0) * dSin(sunLong)))
        var alphaApp = rtd(atan2(dCos(epsilon) * dSin(lambda), dCos(lambda)))
        alphaApp = fixAngle(alphaApp)
        val deltaApp = rtd(asin(dSin(epsilon) * dSin(lambda)))
        return doubleArrayOf(l0, m, e, c, sunLong, sunAnomaly, sunR, lambda, alpha, delta, alphaApp, deltaApp)
    }

    private fun dCos(d: Double): Double = cos(dtr(d))

    private fun dtr(d: Double): Double = d * Math.PI / 180.0

    private fun rtd(r: Double): Double = r * 180.0 / Math.PI

    private fun dSin(d: Double): Double = sin(dtr(d))

    private fun obliqeq(jd: Double): Double {
        val u: Double = (jd - J2000) / (JulianCentury * 100)
        var v = u
        var eps: Double = 23 + 26 / 60.0 + 21.448 / 3600.0
        if (abs(u) < 1.0) {
            var i = 0
            while (i < 10) {
                eps += oTerms[i] / 3600.0 * v
                v *= u
                i++
            }
        }
        return eps
    }

    private val oTerms = doubleArrayOf(
        -4680.93, -1.55, 1999.25, -51.38, -249.67, -39.05, 7.12, 27.87, 5.79, 2.45
    )

    private fun fixAngle(a: Double): Double = a - 360.0 * floor(a / 360.0)

    private fun tehranEquinoxJd(year: Double): Double {
        val ep = tehranEquinox(year)
        return floor(ep)
    }

    private const val PERSIAN_EPOCH = 1948320.5
    private const val TropicalYear = 365.24219878
    private fun persianaYear(jd: Double): DoubleArray {
        var guess = jdToGregorian(jd)[0] - 2
        var lasteq = tehranEquinoxJd(guess)
        while (lasteq > jd) {
            guess--
            lasteq = tehranEquinoxJd(guess)
        }
        var nexteq = lasteq - 1
        while (!(lasteq <= jd && jd < nexteq)) {
            lasteq = nexteq
            guess++
            nexteq = tehranEquinoxJd(guess)
        }
        val adr = ((lasteq - PERSIAN_EPOCH) / TropicalYear).roundToInt() + 1.toDouble()
        return doubleArrayOf(adr, lasteq)
    }

    private fun persianaToJd(year: Double, month: Double, day: Double): Double {
        var adr = doubleArrayOf(year - 1, 0.0)
        var guess = PERSIAN_EPOCH - 1 + TropicalYear * (year - 1 - 1)
        while (adr[0] < year) {
            adr = persianaYear(guess)
            guess = adr[1] + (TropicalYear + 2)
        }
        val equinox = adr[1]
        return equinox + (if (month <= 7) (month - 1) * 31 else (month - 1) * 30 + 6) + (day - 1)
    }
}