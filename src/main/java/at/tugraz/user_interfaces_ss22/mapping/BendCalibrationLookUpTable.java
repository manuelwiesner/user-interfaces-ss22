package at.tugraz.user_interfaces_ss22.mapping;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Implements calibration for bend sensor inputs.
 *
 * @see CalibrationLookUpTable
 */
public class BendCalibrationLookUpTable implements CalibrationLookUpTable {

    private final int[] lut = new int[4096];

    @Override
    public void updateTable(@NotNull List<Integer> calibrationInputDataPoints) {
        // TODO: Update the internal function/LUT with the new calibration data points
        //  Step1 Find beginning and end of curve (when data goes significantly above zero, and returns back to zero)
        //  Step2 Use the real data-points (sub-list with start/stop sliced) to calibrate function
        //  Step3 Store function as an efficient list, so it can be used later every few milliseconds
    }

    @Override
    public int mapInput(short input) throws IndexOutOfBoundsException {
        // TODO: retrieve mapping/function and return calibrated value
        //  Is the function linear? If so, maybe store an array with 4096 values and input==index to look it up?
        //  if (input < 0) throw new IndexOutOfBoundsException();
        //  if (input > 4095) throw new IndexOutOfBoundsException();
        //  if (input > this.max) return 4095;
        return input;
    }
}
