package at.tugraz.user_interfaces_ss22;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Implements calibration for gyroskope axis inputs.
 *
 * @see CalibrationLookUpTable
 */
public class AxisCalibrationLookUpTable implements CalibrationLookUpTable {

    @Override
    public void updateTable(@NotNull List<Integer> calibrationInputDataPoints) {
        // TODO: Update the internal function/LUT with the new calibration data points
        // Step1 Find beginning and end of curve (when data goes significantly above zero, and returns back to zero)
        // Step2 Use the real data-points (sub-list with start/stop sliced) to calibrate function
        // Step3 Store function as an efficient list, so it can be used later every few milliseconds
    }

    @Override
    public int mapInput(int input) {
        // TODO: retrieve mapping/function and return calibrated value
        // Is the function linear? If so, maybe store an array with 4096 values and input==index to look it up?
        return input;
    }
}
