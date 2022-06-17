package at.tugraz.user_interfaces_ss22.mapping

import kotlin.random.Random

abstract class CalibrationLookUpTableTest(private val factory: () -> CalibrationLookUpTable) {

    /** Highest number the noise shall reach */
    protected open val maxNoise: Int = 15

    /** Highest value outputted by the sensors */
    protected open val maxData: Int = 4095

    /** Num of samples per seconds that should be generated */
    protected open val readingsPerSecond: Int = 120

    /** Duration of the sequence in seconds */
    protected open val durationInSeconds: Double = 2.5

    protected val numReadingsSequence: Int
        get() = (readingsPerSecond * durationInSeconds).toInt()

    protected fun generateRandomData(
        dataSpecs: List<DataSpec>,
        maxNoise: Int = this.maxNoise,
    ): List<Int> {
        return mutableListOf<Int>().also { results ->
            // pre noise
            repeat(Random.nextInt(this.numReadingsSequence / 8, this.numReadingsSequence / 4)) {
                results += if (maxNoise <= 0) 0 else Random.nextInt(0, maxNoise)
            }

            // actual data
            dataSpecs.forEach { it.generateData(this.maxData, results) }

            // post noise
            repeat(Random.nextInt(this.numReadingsSequence / 8, this.numReadingsSequence / 4)) {
                results += if (maxNoise <= 0) 0 else Random.nextInt(0, maxNoise)
            }
        }
    }

    protected fun createAndAssertLUT(sampleData: List<Int>, otherAssertions: CalibrationLookUpTable.() -> Unit = {}) {
        this.factory().apply {
            updateTable(sampleData)
            assertLUT(sampleData)
            otherAssertions()
        }
    }

    protected abstract fun CalibrationLookUpTable.assertLUT(sampleData: List<Int>)

    protected class DataSpec(
        private val first: Int,
        private val last: Int,
        private val numReadings: Int,
        private val downTo: Boolean = false,
    ) {
        fun generateData(maxData: Int, results2: MutableList<Int>) {
            if (this.numReadings <= 0) throw IllegalArgumentException("numReadings(=$numReadings) must be > 0")
            if (this.first < 0) throw IllegalArgumentException("first(=$first) must be >= 0")
            if (this.last < 0) throw IllegalArgumentException("last(=$last) must be >= 0")
            if (this.first > maxData) throw IllegalArgumentException("first(=$first) must be <= maxData(=${maxData})")
            if (this.last > maxData) throw IllegalArgumentException("last(=$last) must be <= maxData(=${maxData})")

            val results = mutableListOf<Int>()

            if (this.downTo) {
                when {
                    this.first < this.last -> {
                        val step = (this.first + maxData - this.last) / this.numReadings
                        results.generate(this.first, 0, step)
                        results.generate(maxData, this.last, step)
                    }
                    else -> results.generate(this.first, this.last, (this.first - this.last) / this.numReadings)
                }
            } else {
                when {
                    this.first > this.last -> {
                        val step = (this.last + maxData - this.first) / this.numReadings
                        results.generate(this.first, maxData, step)
                        results.generate(0, this.last, step)
                    }
                    else -> results.generate(this.first, this.last, (this.last - this.first) / this.numReadings)
                }
            }

            println("Generated\n$results\nfor first=$first,last=$last,num=$numReadings,downTo=$downTo,size=${results.size}")

            results2 += results
        }

        private fun MutableList<Int>.generate(first: Int, last: Int, step: Int) {
            val actualStep = step.coerceAtLeast(5)
            var currentValue = first

            if (first < last) {
                while (currentValue < last) {
                    currentValue += Random.nextInt(actualStep - 5, actualStep + 5)
                    this += currentValue.coerceAtMost(last)
                }
            } else if (first > last) {
                while (currentValue > last) {
                    currentValue -= Random.nextInt(actualStep - 5, actualStep + 5)
                    this += currentValue.coerceAtLeast(last)
                }
            } else {
                this += first
            }
        }
    }
}
