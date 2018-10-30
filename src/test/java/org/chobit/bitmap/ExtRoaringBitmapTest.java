package org.chobit.bitmap;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;

/**
 * @author robin
 */
public class ExtRoaringBitmapTest {


    @Test
    public void constructor() {
        ExtRoaringBitmap bitmap = new ExtRoaringBitmap();
        Assert.assertNotNull(bitmap);
    }


    @Test
    public void add() {
        ExtRoaringBitmap bitmap = new ExtRoaringBitmap();
        bitmap.add(20L);
        System.out.println(bitmap);
        Assert.assertTrue(1 == bitmap.cardinality());
    }


    @Test
    public void remove() {
        long value = 1024001002302L;
        ExtRoaringBitmap bitmap = new ExtRoaringBitmap();
        bitmap.add(value);
        System.out.println(bitmap);
        Assert.assertEquals(1, bitmap.cardinality());
        bitmap.remove(value);
        System.out.println(bitmap);
        Assert.assertEquals(0, bitmap.cardinality());
    }


    @Test
    public void addRange() {
        long value = 1024001002302L;
        ExtRoaringBitmap bitmap = new ExtRoaringBitmap();
        bitmap.add(value, value + 10);
        System.out.println(bitmap);
        Assert.assertEquals(10, bitmap.cardinality());
    }


    @Test
    public void removeRange() {
        long value = 1024001002302L;
        ExtRoaringBitmap bitmap = new ExtRoaringBitmap();
        bitmap.add(value, value + 10);
        System.out.println(bitmap);
        bitmap.remove(value + 5, value + 15);
        System.out.println(bitmap);
        Assert.assertEquals(5, bitmap.cardinality());
    }


    @Test
    public void check() {
        long value = 1024001002302L;
        ExtRoaringBitmap bitmap = new ExtRoaringBitmap();
        bitmap.add(value, value + 10);
        Assert.assertTrue(bitmap.check(value + 2));
        Assert.assertFalse(bitmap.check(value - 1000000002));
    }


    @Test
    public void and() {
        long value = 1024001002302L;
        ExtRoaringBitmap b1 = new ExtRoaringBitmap();
        ExtRoaringBitmap b2 = new ExtRoaringBitmap();

        b1.add(value, value + 10);
        System.out.println(b1);
        b2.add(value - 2, value + 2);
        System.out.println(b2);
        ExtRoaringBitmap b3 = b1.and(b2);
        System.out.println(b3);

        Assert.assertEquals(2, b3.cardinality());
    }


    @Test
    public void or() {
        long value = 1024001002302L;
        ExtRoaringBitmap b1 = new ExtRoaringBitmap();
        ExtRoaringBitmap b2 = new ExtRoaringBitmap();

        b1.add(value, value + 10);
        System.out.println(b1);
        b2.add(value - 2, value + 2);
        System.out.println(b2);
        ExtRoaringBitmap b3 = b1.or(b2);
        System.out.println(b3);

        Assert.assertEquals(12, b3.cardinality());
    }


    @Test
    public void xor() {
        long value = 1024001002302L;
        ExtRoaringBitmap b1 = new ExtRoaringBitmap();
        ExtRoaringBitmap b2 = new ExtRoaringBitmap();

        b1.add(value, value + 10);
        System.out.println(b1);
        b2.add(value - 2, value + 2);
        System.out.println(b2);
        ExtRoaringBitmap b3 = b1.xor(b2);
        System.out.println(b3);

        Assert.assertEquals(10, b3.cardinality());
    }


    @Test
    public void andNot() {
        long value = 1024001002302L;
        ExtRoaringBitmap b1 = new ExtRoaringBitmap();
        ExtRoaringBitmap b2 = new ExtRoaringBitmap();

        b1.add(value, value + 10);
        System.out.println(b1);
        b2.add(value - 2, value + 2);
        System.out.println(b2);
        ExtRoaringBitmap b3 = b1.andNot(b2);
        System.out.println(b3);
        System.out.println(b1);

        Assert.assertEquals(8, b3.cardinality());
    }


    @Test
    public void first() {
        long value = 1024001002302L;
        ExtRoaringBitmap b1 = new ExtRoaringBitmap();
        b1.add(value, value + 10);
        Assert.assertEquals(value, b1.first());
    }


    @Test
    public void last() {
        long value = 1024001002302L;
        ExtRoaringBitmap b1 = new ExtRoaringBitmap();
        b1.add(value, value + 10);
        Assert.assertEquals(value + 10 - 1, b1.last());
    }


    @Test
    public void size() {
        long value = 0;//1024001002302L;
        ExtRoaringBitmap bitmap = new ExtRoaringBitmap();
        bitmap.add(value, value + 10);
        Assert.assertEquals(value + 10, bitmap.size());
    }


    @Test
    public void cardinality() {
        long value = 1024001002302L;
        ExtRoaringBitmap bitmap = new ExtRoaringBitmap();
        bitmap.add(value, value + 10);
        Assert.assertEquals(10, bitmap.cardinality());
    }


    @Test
    public void extend() {
        ExtRoaringBitmap bitmap = new ExtRoaringBitmap();
        bitmap.extend(100L);
        Assert.assertEquals(100L, bitmap.size());
        bitmap.extend(1024001002302L);
        Assert.assertEquals(1024001002302L, bitmap.size());
    }


    @Test
    public void copy() {
        ExtRoaringBitmap bitmap = new ExtRoaringBitmap();
        bitmap.add(1L, 6L);
        ExtRoaringBitmap bitmap2 = bitmap.copy();
        bitmap2.add(12L);
        Assert.assertEquals(5, bitmap.cardinality());
        Assert.assertEquals(6, bitmap2.cardinality());
    }


    @Test
    public void serializeAndDeserialize() throws IOException {
        ExtRoaringBitmap b1 = new ExtRoaringBitmap();
        b1.add(1L, 6L);

        Assert.assertEquals(5, b1.cardinality());

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        b1.serialize(new DataOutputStream(bos));

        byte[] bytes = bos.toByteArray();

        DataInputStream input = new DataInputStream(new ByteArrayInputStream(bytes));
        ExtRoaringBitmap b2 = new ExtRoaringBitmap();
        b2.deserialize(input);
        Assert.assertEquals(5, b2.cardinality());
    }


    @Test
    public void toAndFromBytes() throws IOException {
        ExtRoaringBitmap b1 = new ExtRoaringBitmap();
        b1.add(1L, 6L);

        Assert.assertEquals(5, b1.cardinality());

        byte[] bytes = b1.toBytes();
        ExtRoaringBitmap b2 = new ExtRoaringBitmap();
        b2.fromBytes(bytes);

        Assert.assertEquals(5, b2.cardinality());
    }



    @Test
    public void test() {
        long v2 = 1024001002312L % Integer.MAX_VALUE;
        System.out.println(v2);
        long v3 = 1022202215972L % Integer.MAX_VALUE;
        System.out.println(v3);
    }
}
