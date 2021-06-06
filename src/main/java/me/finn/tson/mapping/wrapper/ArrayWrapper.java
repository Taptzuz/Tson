package me.finn.tson.mapping.wrapper;

import java.util.ArrayList;

/**
 * @author Finn on 22.05.2021
 */
public class ArrayWrapper {

    private final ArrayList<?> arrayList;

    /*I don't know if that can be called hardcoded, but i have no idea how to cast primitives/object-arrays via. reflection without creating an own method for every single type*/

    public ArrayWrapper(Object object) {
        this.arrayList = (ArrayList<?>) object;
    }

    public char[] asCharArray() {
        char[] charArray = new char[arrayList.size()];
        for (int i = 0; i < charArray.length; i++)
            charArray[i] = ((String) arrayList.get(i)).charAt(0);
        //Casting directly to (Character) does not work, so we will grab the first char of String
        return charArray;
    }

    public boolean[] asBooleanArray() {
        boolean[] booleanArray = new boolean[arrayList.size()];
        for (int i = 0; i < booleanArray.length; i++)
            booleanArray[i] = (Boolean) arrayList.get(i);
        return booleanArray;
    }

    public byte[] asByteArray() {
        byte[] byteArray = new byte[arrayList.size()];
        for (int i = 0; i < byteArray.length; i++)
            byteArray[i] = getNumber(arrayList.get(i)).byteValue();
        return byteArray;
    }

    public short[] asShortArray() {
        short[] shortArray = new short[arrayList.size()];
        for (int i = 0; i < shortArray.length; i++)
            shortArray[i] = getNumber(arrayList.get(i)).shortValue();
        return shortArray;
    }

    public int[] asIntArray() {
        int[] intArray = new int[arrayList.size()];
        for (int i = 0; i < intArray.length; i++)
            intArray[i] = getNumber(arrayList.get(i)).intValue();
        return intArray;
    }

    public long[] asLongArray() {
        long[] longArray = new long[arrayList.size()];
        for (int i = 0; i < longArray.length; i++)
            longArray[i] = getNumber(arrayList.get(i)).longValue();
        return longArray;
    }

    public float[] asFloatArray() {
        float[] floatArray = new float[arrayList.size()];
        for (int i = 0; i < floatArray.length; i++)
            floatArray[i] = getNumber(arrayList.get(i)).floatValue();
        return floatArray;
    }

    public double[] asDoubleArray() {
        double[] doubleArray = new double[arrayList.size()];
        for (int i = 0; i < doubleArray.length; i++)
            doubleArray[i] = getNumber(arrayList.get(i)).doubleValue();
        return doubleArray;
    }

    public Object[] asObjectArray() {
        Object[] objectArray = new Object[arrayList.size()];
        for (int i = 0; i < objectArray.length; i++)
            objectArray[i] = arrayList.get(i);
        return objectArray;
    }

    private Number getNumber(Object object) {
        return (Number) object;
    }
}
