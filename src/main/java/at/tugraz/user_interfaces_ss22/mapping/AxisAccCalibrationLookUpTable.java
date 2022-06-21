package at.tugraz.user_interfaces_ss22.mapping;

import at.tugraz.user_interfaces_ss22.glove.GlovePacket;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AxisAccCalibrationLookUpTable implements CalibrationLookUpTable {

    private final float accConstant = 4096.0F;
    private final float gyroConstant = 65.5F;
    private final float gyroCoefficientConstant = 0.98F;

    private float accXoffset = 0;
    private float accYoffset = 0;
    private float accZoffset = 0;
    private float gyroXoffset = 0;
    private float gyroYoffset = 0;
    private float gyroZoffset = 0;

    private float lastTimeConstant = 0;
    float angleAccX = 0;
    float angleAccY = 0;
    float angleX = 0;
    float angleY = 0;
    float angleZ = 0;


    @Override
    public void updateTable(@NotNull List<Integer> calibrationInputDataPoints) {

        //TODO: calculate offset
    }
    @Override
    public int mapInput(@NotNull GlovePacket packet, short input) throws IndexOutOfBoundsException {
        //TODO: not needed anymore
        return input;
    }

    public void updateData(@NotNull GlovePacket packet) throws IndexOutOfBoundsException {

        float accX = packet.getAccX();
        float accY = packet.getAccY();
        float accZ = packet.getAccZ();
        float gyroX = packet.getGyroX();
        float gyroY = packet.getGyroY();
        float gyroZ = packet.getGyroZ();
        float timeConstant = packet.getTimeConstant();

        float accX_processed = (accX / accConstant) - accXoffset;
        float accY_processed = (accY / accConstant) - accYoffset;
        float accZ_processed = (accZ / accConstant) - accZoffset;

        float gyroX_processed = (gyroX / gyroConstant) - gyroXoffset;
        float gyroY_processed = (gyroY / gyroConstant) - gyroYoffset;
        float gyroZ_processed = (gyroZ / gyroConstant) - gyroZoffset;

        int signZ = (int) Math.signum(accZ_processed);

        angleAccX = (float) Math.atan2(accY_processed, signZ * Math.sqrt(accZ_processed * accZ_processed + accX_processed * accX_processed) * 57.29578F);
        angleAccY = (float) Math.atan2(accX_processed, signZ * Math.sqrt(accZ_processed * accZ_processed + accY_processed * accY_processed) * 57.29578F);

        float dt = (timeConstant - lastTimeConstant) * 0.001F;
        lastTimeConstant = timeConstant;

        angleX = (gyroCoefficientConstant*(angleX + gyroX_processed * dt)) + ((1.0F - gyroCoefficientConstant) * angleAccX);
        angleY = (gyroCoefficientConstant*(angleY + gyroY_processed * dt)) + ((1.0F - gyroCoefficientConstant) * angleAccY);
        angleZ += gyroZ_processed * dt;
    }
}
