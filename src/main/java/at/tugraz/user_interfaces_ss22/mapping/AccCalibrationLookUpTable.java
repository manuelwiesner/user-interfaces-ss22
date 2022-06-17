package at.tugraz.user_interfaces_ss22.mapping;

import at.tugraz.user_interfaces_ss22.glove.GlovePacket;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AccCalibrationLookUpTable implements CalibrationLookUpTable {

    @Override
    public void updateTable(@NotNull List<Integer> calibrationInputDataPoints) {

    }

    @Override
    public int mapInput(@NotNull GlovePacket packet, short input) throws IndexOutOfBoundsException {
        return input;
    }
}
