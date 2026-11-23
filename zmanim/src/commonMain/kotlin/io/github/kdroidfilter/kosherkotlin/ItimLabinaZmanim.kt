package io.github.kdroidfilter.kosherkotlin

import io.github.kdroidfilter.kosherkotlin.metadata.UsesElevation
import io.github.kdroidfilter.kosherkotlin.metadata.ZmanAuthority
import io.github.kdroidfilter.kosherkotlin.metadata.ZmanCalculationMethod
import io.github.kdroidfilter.kosherkotlin.metadata.ZmanDefinition
import io.github.kdroidfilter.kosherkotlin.metadata.ZmanType
import kotlin.time.Instant

/**
 * The עתים לבינה zmanim of [ComplexZmanimCalendar]. Upstream removed them in 4.0.0; they live in a file of their own
 * so that merging upstream touches them as little as possible.
 */
interface ItimLabinaZmanim {
    /** Implemented by [AstronomicalCalendar]. */
    fun getSunriseOffsetByDegreesBelowHorizon(degrees: Double): Instant?

    /** Implemented by [AstronomicalCalendar]. */
    fun getSunsetOffsetByDegreesBelowHorizon(degrees: Double): Instant?

    // עתים לבינה: degree-based zmanim measured below the apparent horizon of the day rather than the geometric one
    // (see getSunriseOffsetByDegreesBelowHorizon). They are meant for the ItimLabinaCalculator, whose horizon follows
    // the luach's refraction, and stay meaningful with any calculator.

    private fun itimLabina(type: ZmanType, degrees: Float, isSunrise: Boolean, vararg authorities: ZmanAuthority) =
        Zman.DateBased(
            ZmanDefinition(
                type,
                ZmanCalculationMethod.DegreesBelowHorizon(degrees),
                UsesElevation.NEVER,
                listOf(ZmanAuthority.ITIM_LABINA, *authorities),
            )
        ) {
            if (isSunrise) getSunriseOffsetByDegreesBelowHorizon(degrees.toDouble())
            else getSunsetOffsetByDegreesBelowHorizon(degrees.toDouble())
        }

    /** *Alos* "72 במעלות" of עתים לבינה: 15.2193˚ below the apparent horizon, 72 minutes before sunrise at the equinox. */
    val alos72ItimLabina: Zman.DateBased get() = itimLabina(ZmanType.ALOS, 15.2193F, true)

    /** *Alos* "90 במעלות" of עתים לבינה: 18.9712˚ below the apparent horizon. */
    val alos90ItimLabina: Zman.DateBased get() = itimLabina(ZmanType.ALOS, 18.9712F, true)

    /** *Alos* "120 במעלות" of עתים לבינה: 25.1347˚ below the apparent horizon. */
    val alos120ItimLabina: Zman.DateBased get() = itimLabina(ZmanType.ALOS, 25.1347F, true)

    /** *Tzais* "72 במעלות" of עתים לבינה (Rabeinu Tam): 15.2193˚ below the apparent horizon. */
    val tzais72ItimLabina: Zman.DateBased get() = itimLabina(ZmanType.TZAIS, 15.2193F, false, ZmanAuthority.RABEINU_TAM)

    /** *Tzais* "90 במעלות" of עתים לבינה: 18.9712˚ below the apparent horizon. */
    val tzais90ItimLabina: Zman.DateBased get() = itimLabina(ZmanType.TZAIS, 18.9712F, false)

    /** *Tzais* "120 במעלות" of עתים לבינה: 25.1347˚ below the apparent horizon. */
    val tzais120ItimLabina: Zman.DateBased get() = itimLabina(ZmanType.TZAIS, 25.1347F, false)

    /** *Tzais* of the *Geonim*, "18 דק' במעלות" of עתים לבינה: 3.8217˚ below the apparent horizon. */
    val tzaisGeonim18MinutesItimLabina: Zman.DateBased get() = itimLabina(ZmanType.TZAIS, 3.8217F, false, ZmanAuthority.GEONIM)

    /** *Tzais* "13.5 דק' במעלות" of עתים לבינה: 2.86674˚ below the apparent horizon. */
    val tzais13Point5MinutesItimLabina: Zman.DateBased get() = itimLabina(ZmanType.TZAIS, 2.86674F, false)

    /** *Tzais* of the Rambam per עתים לבינה: 4.24604˚ below the apparent horizon. */
    val tzaisRambamItimLabina: Zman.DateBased get() = itimLabina(ZmanType.TZAIS, 4.24604F, false, ZmanAuthority.RAMBAM)

    /** *Bain hashmashos* of the Yereim per עתים לבינה: 2.86674˚ above the apparent horizon, 13.5 minutes before sunset. */
    val bainHashmashosYereimItimLabina: Zman.DateBased
        get() = itimLabina(ZmanType.BAIN_HASHMASHOS, -2.86674F, false, ZmanAuthority.YEREIM)
}

/** All of [ItimLabinaZmanim], for [ComplexZmanimCalendar.allZmanim]. */
internal fun ItimLabinaZmanim.itimLabinaZmanim(): Array<Zman.DateBased> = arrayOf(
    alos72ItimLabina,
    alos90ItimLabina,
    alos120ItimLabina,
    tzais72ItimLabina,
    tzais90ItimLabina,
    tzais120ItimLabina,
    tzaisGeonim18MinutesItimLabina,
    tzais13Point5MinutesItimLabina,
    tzaisRambamItimLabina,
    bainHashmashosYereimItimLabina,
)
