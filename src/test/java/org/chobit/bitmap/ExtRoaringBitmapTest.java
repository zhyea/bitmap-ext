package org.chobit.bitmap;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author rui.zhang
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
        b2.add(value - 2, value + 2);
        ExtRoaringBitmap b3 = b1.and(b2);
        System.out.println(b3);

        Assert.assertEquals(3, b3.cardinality());
    }


}
