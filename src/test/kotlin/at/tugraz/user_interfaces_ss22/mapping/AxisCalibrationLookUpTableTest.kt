package at.tugraz.user_interfaces_ss22.mapping

import org.junit.jupiter.api.*

class AxisAccCalibrationLookUpTableTest : CalibrationLookUpTableTest({ AxisAccCalibrationLookUpTable() }) {

    @Test
    fun testNormalSequence() = createAndAssertLUT(
        generateRandomData(
            listOf(
                // TODO: Find good ranges for gyro, the DataSpec class supports wrapping
                //  e.g. 100 downTo 4000 will go from 100 to 0 and from 4095 to 4000
                //  or 4000 to 100 will go from 4000 to 4095 and from 0 to 100
            )
        )
    )

    override fun CalibrationLookUpTable.assertLUT(sampleData: List<Int>) {
        // TODO: Find good tests for gyro
    }
}
