package at.tugraz.user_interfaces_ss22;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Implements calibration for bend sensor inputs.
 *
 * @see CalibrationLookUpTable
 */
public class BendCalibrationLookUpTable implements CalibrationLookUpTable {

    private int min;
    private int max;

    @Override
    public void updateTable(@NotNull List<Integer> calibrationInputDataPoints) {
        // TODO: Update the internal function/LUT with the new calibration data points
        // Step1 Find beginning and end of curve (when data goes significantly above zero, and returns back to zero)
        // Step2 Use the real data-points (sub-list with start/stop sliced) to calibrate function
        // Step3 Store function as an efficient list, so it can be used later every few milliseconds

        this.min = calibrationInputDataPoints.stream().reduce((o1, o2) -> o1 < o2 ? o1 : o2).orElseThrow();
        this.max = calibrationInputDataPoints.stream().reduce((o1, o2) -> o1 > o2 ? o1 : o2).orElseThrow();
    }

    @Override
    public int mapInput(int input) {
        // TODO: retrieve mapping/function and return calibrated value
        // Is the function linear? If so, maybe store an array with 4096 values and input==index to look it up?

        if (input < 0) throw new IndexOutOfBoundsException();
        if (input > 4095) throw new IndexOutOfBoundsException();
        if (input > this.max) return 4095;

        double slope = 4095.0 / (this.max - this.min);
        return (int) (slope * (input - this.min));
    }
}
