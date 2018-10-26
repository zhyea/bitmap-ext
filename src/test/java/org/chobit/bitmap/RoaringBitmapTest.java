package org.chobit.bitmap;

import org.junit.Test;
import org.roaringbitmap.RoaringBitmap;

/**
 * @author rui.zhang
 */
public class RoaringBitmapTest {


    @Test
    public void andNot() {
        RoaringBitmap a = new RoaringBitmap();
        a.add(1, 2, 3, 4, 5, 6);
        RoaringBitmap b = new RoaringBitmap();
        b.add(3, 4, 8);

        a.andNot(b);

        System.out.println(a);
    }


    @Test
    public void not() {
        RoaringBitmap a = new RoaringBitmap();
        a.add(1, 4, 5, 6);
        a.flip(0, 100);
        System.out.println(a);
    }


    @Test
    public void add() {
        RoaringBitmap a = new RoaringBitmap();
        a.add(1L, 4L);
        System.out.println(a);
    }

}
