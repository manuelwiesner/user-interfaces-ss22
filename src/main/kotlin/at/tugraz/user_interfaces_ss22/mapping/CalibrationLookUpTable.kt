package at.tugraz.user_interfaces_ss22.mapping

import at.tugraz.user_interfaces_ss22.glove.GlovePacket

/**
 * Used to build a function or LUT from sample data, so it can be used to map/calibrate input later.
 * @see AxisCalibrationLookUpTable
 * @see BendCalibrationLookUpTable
 */
interface CalibrationLookUpTable {

    /** Updates the internal function/LUT with new sample data. */
    fun updateTable(calibrationInputDataPoints: List<Int>)

    /** Applies the stored function/LUT to the input. */
    @Throws(IndexOutOfBoundsException::class)
    fun mapInput(packet: GlovePacket, input: Short): Int
}
