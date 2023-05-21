package me.liwk.karhu.util;

import lombok.val;
import lombok.var;
import me.liwk.karhu.Karhu;
import me.liwk.karhu.data.PlayerData;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MathUtil {

    public static final double HITBOX_NORMAL = 0.4;
    public static final double HITBOX_DIAGONAL = Math.sqrt(Math.pow(HITBOX_NORMAL, 2) + Math.pow(HITBOX_NORMAL, 2));

    public static double getRandomDouble(double maximum, double minimum) {
        Random r = new Random();
        return minimum + (maximum - minimum) * r.nextDouble();
    }

    public static double average(final Iterable<? extends Number> iterable) {
        double n = 0.0;
        int n2 = 0;
        for (Number number : iterable) {
            n += number.doubleValue();
            ++n2;
        }
        return n / n2;
    }

    public static <T> Stream<T> stream(T... array) {
        return Arrays.stream(array);
    }

    public static <T> T firstNonNull(@Nullable T t, @Nullable T t2) {
        return t != null ? t : t2;
    }

    public static <T> Queue<T> trim(Queue<T> queue, int n) {
        for(int i = queue.size(); i > n; --i) {
            queue.poll();
        }

        return queue;
    }

    /*public static double getStandardDeviation(Collection<? extends Number> values) {
        double average = getAverage(values);

        AtomicDouble variance = new AtomicDouble(0D);

        values.forEach(delay -> variance.getAndAdd(Math.pow(delay.doubleValue() - average, 2D)));

        return Math.sqrt(variance.get() / values.size());
    }*/

    public static double getStandardDeviation(final Collection<? extends Number> data) {
        final double variance = getVariance(data);

        // The standard deviation is the square root of variance. (sqrt(s^2))
        return Math.sqrt(variance);
    }

    public static double getVariance(final Collection<? extends Number> data) {
        int count = 0;

        double sum = 0.0;
        double variance = 0.0;

        double average;

        // Increase the sum and the count to find the average and the standard deviation
        for (final Number number : data) {
            sum += number.doubleValue();
            ++count;
        }

        average = sum / count;

        // Run the standard deviation formula
        for (final Number number : data) {
            variance += Math.pow(number.doubleValue() - average, 2.0);
        }

        return variance;
    }

    public static double getAverage(Collection<? extends Number> values) {
        return values.stream()
                .mapToDouble(Number::doubleValue)
                .average()
                .orElse(0D);
    }

    public static double getCPS(Collection<? extends Number> values) {
        return 20.0D / getAverage(values);
    }

    public static double getCPS1000(Collection<? extends Number> values) {
        return 1000D / getAverage(values);
    }

    public static double getMoveAngle(Location from, Location to) {
        double dx = to.getX() - from.getX();
        double dz = to.getZ() - from.getZ();

        double moveAngle = Math.toDegrees(Math.atan2(dz, dx)) - 90D; // have to subtract by 90 because minecraft does it

        return Math.abs(wrapAngleTo180_double(moveAngle - to.getYaw()));
    }

    public static Vector getDirection(Player player) {
        return new Vector(-Math.sin(player.getEyeLocation().getYaw() * Math.PI / 180.0D) * 1.0D * 0.5D, 0.0D, Math.cos(player.getEyeLocation().getYaw() * Math.PI / 180.0D) * 1.0D * 0.5D);
        //return new Vector(-Math.sin(player.getEyeLocation().getYaw() * 3.1415927F / 180.0F) * (float) 1 * 0.5F, 0, Math.cos(player.getEyeLocation().getYaw() * 3.1415927F / 180.0F) * (float) 1 * 0.5F);
    }

    public static double getDirectionShit(Location from, Location to) {
        if (from == null || to == null) {
            return 0.0D;
        }
        double difX = to.getX() - from.getX();
        double difZ = to.getZ() - from.getZ();

        return (float) ((Math.atan2(difZ, difX) * 180.0D / Math.PI) - 90.0F);
    }

    public static Vector getVectorSpeed(Location from, Location to) {
        return new Vector(to.getX() - from.getX(), 0, to.getZ() - from.getZ());
    }

    public static double lowestAbs(Iterable<? extends Number> iterable) {
        Double value = null;
        Iterator var2 = iterable.iterator();

        while(true) {
            Number n;
            do {
                if (!var2.hasNext()) {
                    return (Double)firstNonNull(value, 0.0D);
                }

                n = (Number)var2.next();
            } while(value != null && Math.abs(n.doubleValue()) >= Math.abs(value));

            value = n.doubleValue();
        }
    }

    public static Double getMinimumAngle(LocationData packetLocation, Location... array) {
        Double value = null;
        Location[] var3 = array;
        int var4 = array.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Location playerLocation = var3[var5];
            double distanceBetweenAngles360 = getDistanceBetweenAngles360((double)playerLocation.getYaw(), (double)((float)(Math.atan2(packetLocation.getZ() - playerLocation.getZ(), packetLocation.getX() - playerLocation.getX()) * 180.0D / 3.141592653589793D) - 90.0F));
            if (value == null || value > distanceBetweenAngles360) {
                value = distanceBetweenAngles360;
            }
        }

        return value;
    }

    public static float averageFloat(List<Float> list) {
        float avg = 0.0f;
        for (float value : list) {
            avg += value;
        }
        if (list.size() > 0) {
            return avg / (float)list.size();
        }
        return 0.0f;
    }

    public static float averageLong(Deque<Long> list) {
        float avg = 0;
        for (float value : list) {
            avg += value;
        }
        if (list.size() > 0) {
            return avg / list.size();
        }
        return 0;
    }

    public static int getPingInTicks(long ping) {
        return (int) Math.floor(ping / 50.);
    }

    public static int getPingToTimer(long ping) {
        return (int) ping / 10000;
    }


    public static double deviation(Iterable<? extends Number> iterable) {
        return Math.sqrt(deviationSquared(iterable));
    }

    public static Vector getHorizontalVector(Vector v) {
        v.setY(0);
        return v;
    }

    public static boolean onGround(double n) {
        return (n % 0.015625D == 0.0D);
    }

    public static double deviationSquared(Iterable<? extends Number> iterable) {
        double n = 0.0;
        int n2 = 0;
        for (Number number : iterable) {
            n += number.doubleValue();
            ++n2;
        }
        final double n3 = n / n2;
        double n4 = 0.0;
        for (Number number : iterable) {
            n4 += Math.pow(number.doubleValue() - n3, 2.0);
        }
        return (n4 == 0.0) ? 0.0 : (n4 / (n2 - 1));
    }

    public static float sqrt(double var0) {
        return (float)Math.sqrt(var0);
    }

    public static float f(List<Float> list) {
        float n = 0.0f;
        Iterator<Float> iterator = list.iterator();
        while (iterator.hasNext()) {
            n += iterator.next();
            try {
                if (iterator.toString() == null) {
                    return 0.0f;
                }
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
            break;
        }
        try {
            if (list.size() > 0) {
                return n / list.size();
            }
        } catch (IllegalArgumentException ex2) {
            ex2.printStackTrace();
        }
        return 0.0f;
    }

    public static double getHitboxSize(float yaw) {
        var clamped = (int) Math.abs(MathUtil.clamp180(yaw)) % 90;

        if (clamped > 45) {
            clamped = 90 - clamped;
        }

        clamped /= 0.45;

        val opposite = 100 - clamped;

        val diagonal = HITBOX_DIAGONAL * (clamped / 100.0);
        val normal = HITBOX_NORMAL * (opposite / 100.0);

        return diagonal + normal;
    }

    public static double clamp180(double theta) {
        theta %= 360.0;

        if (theta >= 180.0) {
            theta -= 360.0;
        }

        if (theta < -180.0) {
            theta += 360.0;
        }
        return theta;
    }

    public static double getDirection(Location from, Location to) {
        if (from == null || to == null) {
            return 0.0D;
        }
        double difX = to.getX() - from.getX();
        double difZ = to.getZ() - from.getZ();

        return (float) ((Math.atan2(difZ, difX) * 180.0D / Math.PI) - 90.0F);
    }

    public static Vec3 getPositionEyes(double x, double y, double z, float eyeHeight) {
        return new Vec3(x, y + (double)eyeHeight, z);
    }

    public Vec3 getLook(float partialTicks, PlayerData playerData) {
        if (partialTicks == 1.0F) {
            return getVectorForRotation(playerData.getLocation().getPitch(), playerData.getLocation().getYaw());
        } else {
            float f = playerData.getLastLocation().getPitch() + (playerData.getLocation().getPitch() - playerData.getLastLocation().getPitch()) * partialTicks;
            float f1 = playerData.getLastLocation().getYaw() + (playerData.getLocation().getYaw() - playerData.getLastLocation().getYaw()) * partialTicks;
            return getVectorForRotation(f, f1);
        }
    }

    public static final Vec3 getVectorForRotation(float pitch, float yaw) {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3(f1 * f2, f3, f * f2);
    }

    public static AxisAlignedBB getEntityBoundingBox(double x, double y, double z) {
        float f = 0.6F / 2.0F;
        float f1 = 1.8F;
        return (new AxisAlignedBB(x - (double)f, y, z - (double)f, x + (double)f, y + (double)f1, z + (double)f));
    }

    public static long gcd(final long limit, final long a, final long b) {
        try {
            if (b <= limit) {
                return a;
            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return gcd(limit, b, a % b);
    }

    public static long gcd (long a, long b){
        if (b <= 16384) {
            return a;
        }
        return gcd(b, a % b);
    }

    public static double gcd(final double limit, final double a, final double b) {
        try {
            if (b <= limit) {
                return a;
            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return gcd(limit, b, a % b);
    }

    public static long getGcd(long current, long previous) {
        return previous <= 0b100000000000000 ? current : MathUtil.getGcd(previous, MathUtil.getDelta(current, previous));
    }

    public static <T extends Number> T getMode(Collection<T> collect) {
        Map<T, Integer> repeated = new HashMap<>();

        //Sorting each value by how to repeat into a map.
        collect.forEach(val -> {
            int number = repeated.getOrDefault(val, 0);

            repeated.put(val, number + 1);
        });

        //Calculating the largest value to the key, which would be the mode.
        return (T) repeated.keySet().stream()
                .map(key -> new Tuple<>(key, repeated.get(key))) //We map it into a Tuple for easier sorting.
                .max(Comparator.comparing(tup -> tup.b(), Comparator.naturalOrder()))
                .orElseThrow(NullPointerException::new).a();
    }

    private static String getBooleanValue3(boolean value) {
        return value ? "%%__TIMESTAMP__%%" : "%%__USER__%%";
    }


    private static long getDelta(long alpha, long beta) {
        return alpha % beta;
    }

    public static float[] getRotationFromPosition(CustomLocation playerLocation, CustomLocation targetLocation) {
        double xDiff = targetLocation.getX() - playerLocation.getX();
        double zDiff = targetLocation.getZ() - playerLocation.getZ();
        double yDiff = targetLocation.getY() - (playerLocation.getY() + 0.12);
        double dist = sqrt(xDiff * xDiff + zDiff * zDiff);
        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float) (-(Math.atan2(yDiff, dist) * 180.0 / 3.141592653589793));
        return new float[]{yaw, pitch};
    }

    public static double pingFormula(long ping) {
        return Math.ceil((ping + 5) / 50.0D);
    }

    public static double invSqrt(double x) {
        double xhalf = 0.5d * x;
        long i = Double.doubleToLongBits(x);
        i = 0x5fe6ec85e7de30daL - (i >> 1);
        x = Double.longBitsToDouble(i);
        x *= (1.5d - xhalf * x * x);
        return x;
    }

    public static double hypotNEW(double... value) {
        double total = 0.0D;
        double[] var3 = value;
        int var4 = value.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            double val = var3[var5];
            total += val * val;
        }

        return Math.sqrt(total);
    }

    public static double hypot(double... value) {
        double total = 0;

        for (double val : value) {
            total += (val * val);
        }

        return Math.sqrt(total);
    }

    public static float hypot(float... value) {
        float total = 0;

        for (float val : value) {
            total += (val * val);
        }

        return (float) Math.sqrt(total);
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static float round(float value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    public static float round(float value, int places, RoundingMode mode) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, mode);
        return bd.floatValue();
    }

    public static float round(float value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(0, RoundingMode.UP);
        return bd.floatValue();
    }


    /*public static double hypot(double... array) {
        return Math.sqrt(hypotSquared(array));
    }

    public static double hypotSquared(double... array) {
        double n = 0.0D;
        int length = array.length;

        for(int i = 0; i < length; ++i) {
            n += Math.pow(array[i], 2.0D);
        }

        return n;
    }*/

    private static String getBooleanValue4(boolean value) {
        return value ? "%%__TIMESTAMP__%%" : "%%__USER__%%";
    }


    public static float getDistanceBetweenAngles(float angle1, float angle2) {
        float distance = Math.abs(angle1 - angle2) % 360.0f;
        if (distance > 180.0f) {
            distance = 360.0f - distance;
        }
        return distance;
    }


    public static double getDistanceBetweenAngles360(double angle1, double angle2) {
        double distance = Math.abs(angle1 % 360.0 - angle2 % 360.0);
        distance = Math.min(360.0 - distance, distance);
        return Math.abs(distance);
    }

    @SuppressWarnings("unused")
    public static double[] extractFeatures(List<Float> angleSequence) {
        List<Double> anglesDouble = toDoubleList(angleSequence);
        List<Double> anglesDoubleDelta = calculateDelta(anglesDouble);

        double featureA = stddev(anglesDouble);
        double featureB = mean(anglesDouble);
        double featureC = stddev(anglesDoubleDelta);
        double featureD = mean(anglesDoubleDelta);

        return new double[]{featureA, featureB, featureC, featureD};
    }

    public static double getAngleDistance(double alpha, double beta) {
        double abs = Math.abs(alpha % 360.0D - beta % 360.0D);
        return Math.abs(Math.min(360.0D - abs, abs));
    }

    // Get delta of a double list
    public static List<Double> calculateDelta(List<Double> doubleList) {
        if (doubleList.size() <= 1)
            throw new IllegalArgumentException("The list must contain 2 or more elements in order to calculate delta");

        List<Double> out = new ArrayList<>();
        for (int i = 1; i <= doubleList.size() - 1; i++)
            out.add(doubleList.get(i) - doubleList.get(i - 1));
        return out;
    }

    // Convert a float list to a double list
    public static List<Double> toDoubleList(List<Float> floatList) {
        return floatList.stream().map(e -> (double) e).collect(Collectors.toList());
    }

    // Get mean average of a double sequence
    public static double mean(List<Double> angles) {
        return angles.stream().mapToDouble(e -> e).sum() / angles.size();
    }

    // Get standard deviation of a double sequence
    public static double stddev(List<Double> angles) {
        double mean = mean(angles);
        double output = 0;
        for (double angle : angles)
            output += Math.pow(angle - mean, 2);
        return output / angles.size();
    }

    private static String getBooleanValue1(boolean value) {
        return value ? "%%__TIMESTAMP__%%" : "%%__USER__%%";
    }

    // Get euclidean distance of two vector
    public static double euclideanDistance(double[] vectorA, double[] vectorB) {
        validateDimension("Two vectors need to have exact the same dimension", vectorA, vectorB);

        double dist = 0;
        for (int i = 0; i <= vectorA.length - 1; i++)
            dist += Math.pow(vectorA[i] - vectorB[i], 2);
        return Math.sqrt(dist);
    }

    // Convert a double array to a double list
    public static List<Double> toList(double[] doubleArray) {
        return Arrays.asList(ArrayUtils.toObject(doubleArray));
    }

    // Convert a double list to a double array
    public static double[] toArray(List<Double> doubleList) {
        return doubleList.stream().mapToDouble(e -> e).toArray();
    }

    // generate a double array filled with random values from 0 to 1
    public static double[] randomArray(int length) {
        double[] randomArray = new double[length];
        applyFunc(randomArray, e -> e = ThreadLocalRandom.current().nextDouble());
        return randomArray;
    }

    // apply function on a array
    public static void applyFunc(double[] doubleArray, Function<Double, Double> func) {
        for (int i = 0; i <= doubleArray.length - 1; i++)
            doubleArray[i] = func.apply(doubleArray[i]);
    }

    // add two vector together
    public static double[] add(double[] vectorA, double[] vectorB) {
        validateDimension("Two vectors need to have exact the same dimension", vectorA, vectorB);

        double[] output = new double[vectorA.length];
        for (int i = 0; i <= vectorA.length - 1; i++)
            output[i] = vectorA[i] + vectorB[i];
        return output;
    }

    // Get diff of two different vectors (subtract)
    public static double[] subtract(double[] vectorA, double[] vectorB) {
        validateDimension("Two vectors need to have exact the same dimension", vectorA, vectorB);

        return add(vectorA, opposite(vectorB));
    }

    // get opposite numbers of elements in the vector
    public static double[] opposite(double[] vector) {
        return multiply(vector, -1);
    }

    // multiply all elements in the vector with a value
    public static double[] multiply(double[] vector, double factor) {
        double[] output = vector.clone();
        applyFunc(output, e -> e * factor);
        return output;
    }

    // normalize a value with feature scaling according to the given min and max
    public static double normalize(double value, double min, double max) {
        return (value - min) / (max - min);
    }

    // round a value with given arguments
    public static double round(double value, int precision, RoundingMode mode) {
        return BigDecimal.valueOf(value).round(new MathContext(precision, mode)).doubleValue();
    }

    public static double round(double value) {
         return value - value % 1000;
    }

    @SuppressWarnings("SameParameterValue")
    private static void validateDimension(String message, double[]... vectors) {
        for (int i = 0; i <= vectors.length - 1; i++)
            if (vectors[0].length != vectors[i].length)
                throw new IllegalArgumentException(message);
    }

    public static float wrapAngleTo180_float(float value)
    {
        value = value % 360.0F;

        if (value >= 180.0F)
        {
            value -= 360.0F;
        }

        if (value < -180.0F)
        {
            value += 360.0F;
        }

        return value;
    }

    public static double wrapAngleTo180_double(double value) {
        value %= 360D;

        if (value >= 180D)
            value -= 360D;

        if (value < -180D)
            value += 360D;

        return value;
    }

    public static boolean isUsingOptifine(Location current, Location previous) {
        val yawChange = Math.abs(current.getYaw() - previous.getYaw());
        val pitchChange = Math.abs(current.getPitch() - previous.getPitch());

        val yawWrapped = MathUtil.wrapAngleTo180_float(yawChange);
        val pitchWrapped = MathUtil.wrapAngleTo180_float(pitchChange);

        return pitchWrapped < 0.01 || yawWrapped < 0.015;
    }

    public static double positiveSmaller(Number n, Number n2) {
        return Math.abs(n.doubleValue()) < Math.abs(n2.doubleValue()) ? n.doubleValue() : n2.doubleValue();
    }

    public static double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    public static final Random RANDOM = new Random();

    public static double getDistanceToGround(Player p) {
        Location loc = p.getLocation().clone();
        double y = loc.getBlockY();
        double distance = 0.0;
        for (double i = y; i >= 0.0; --i) {
            loc.setY(i);
            if (loc.getBlock().getType().isSolid()) {
                break;
            }
            ++distance;
        }
        return distance;
    }

}
