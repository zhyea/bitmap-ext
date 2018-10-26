package org.chobit.bitmap;

import org.junit.Assert;
import org.junit.Test;

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


    public void size() {

    }


    public void cardinality() {

    }


    public void extend() {

    }


    public void copy() {
    }


    public void serializeAndDeserialize() {
    }


    public void toANdFromBytes() {
    }
}
