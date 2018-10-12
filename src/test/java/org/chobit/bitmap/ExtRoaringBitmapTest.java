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

}
