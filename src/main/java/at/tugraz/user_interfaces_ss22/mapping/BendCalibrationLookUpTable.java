package at.tugraz.user_interfaces_ss22.mapping;

import at.tugraz.user_interfaces_ss22.glove.GlovePacket;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Implements calibration for bend sensor inputs.
 *
 * @see CalibrationLookUpTable
 */
public class BendCalibrationLookUpTable implements CalibrationLookUpTable {

    private final int[] lut = new int[4096];
    private int maxInput;
    private int minInput;

    @Override
    public void updateTable(@NotNull List<Integer> calibrationInputDataPoints) {

        this.maxInput = 0;
        this.minInput = 4095;

        for (Integer currentInput : calibrationInputDataPoints) {
            this.maxInput = Math.max(currentInput, this.maxInput);
            this.minInput = Math.min(currentInput, this.minInput);
        }
        //TODO: maybe add a buffer-zone here (at least raise minInput a little) ?

        double slope = 4095.0 / (this.maxInput - this.minInput);

        for (int i = 0; i < lut.length; i++){
            if(i <= this.minInput){
                this.lut[i] = 0;
            }
            else if(i >= this.maxInput){
                this.lut[i] = 4095;
            }
            else{
                this.lut[i] = (int) (slope * (i - this.minInput));
            }
        }
    }


    public int mapInput(@NotNull GlovePacket packet, short input) throws IndexOutOfBoundsException {

        if (input < 0) throw new IndexOutOfBoundsException();
        if (input > 4095) throw new IndexOutOfBoundsException();
        if (input > this.maxInput) return 4095;
        if (input < this.minInput) return 0;

        return this.lut[input];
    }
}
