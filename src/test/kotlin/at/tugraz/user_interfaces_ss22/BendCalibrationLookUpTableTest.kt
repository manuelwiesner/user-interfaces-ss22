package at.tugraz.user_interfaces_ss22

import org.junit.jupiter.api.*
import kotlin.test.assertEquals

class BendCalibrationLookUpTableTest : CalibrationLookUpTableTest({ BendCalibrationLookUpTable() }) {

    @Test
    fun testNormalSequence() = createAndAssertLUT(
        generateRandomData(
            listOf(
                DataSpec(0, this.maxData, this.numReadingsSequence.div(2)),
                DataSpec(this.maxData, 0, this.numReadingsSequence.div(2), true),
            )
        )
    )

    @Test
    fun testNoiseFreeSequence() = createAndAssertLUT(
        generateRandomData(
            listOf(
                DataSpec(0, this.maxData, this.numReadingsSequence.div(2)),
                DataSpec(this.maxData, 0, this.numReadingsSequence.div(2), true),
            ),
            maxNoise = 0
        )
    )

    @Test
    fun testLimitedRangeSequence() = createAndAssertLUT(
        generateRandomData(
            listOf(
                DataSpec(0, this.maxData / 2, this.numReadingsSequence.div(2)),
                DataSpec(this.maxData / 2, 0, this.numReadingsSequence.div(2), true),
            )
        )
    )

    @Test
    fun testUnevenSequence() = createAndAssertLUT(
        generateRandomData(
            listOf(
                DataSpec(0, this.maxData / 2, this.numReadingsSequence.div(5)),
                DataSpec(this.maxData / 2, 0, this.numReadingsSequence),
            )
        )
    )

    override fun CalibrationLookUpTable.assertLUT(sampleData: List<Int>) {
        // input can't be negative
        assertThrows<IndexOutOfBoundsException> { mapInput(-1) }

        // max read/generated sample
        val peakSample = sampleData.maxOrNull() ?: 0
        var lastValue = 0

        for (i in 0..maxData) {
            // get next mapping
            val nextValue = mapInput(i)
            // if peak is reached we should only get the max value above that
            if (i >= peakSample) assertEquals(this@BendCalibrationLookUpTableTest.maxData, nextValue)
            // next value should probably be >= than the last?
            assert(nextValue >= lastValue)
            lastValue = nextValue
        }

        // input can't be greater than max
        assertThrows<IndexOutOfBoundsException> { mapInput(maxData + 1) }
    }
}
