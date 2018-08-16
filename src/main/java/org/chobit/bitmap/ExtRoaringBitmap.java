package org.chobit.bitmap;

import java.io.IOException;

/**
 * @author rui.zhang
 */
public class ExtRoaringBitmap extends AbstractExtBitmap<ExtRoaringBitmap, RoaringBitmap> {


    @Override
    protected long maxUnitSize() {
        return 0;
    }

    @Override
    public ExtRoaringBitmap and(ExtRoaringBitmap other) {
        return null;
    }

    @Override
    public ExtRoaringBitmap or(ExtRoaringBitmap other) {
        return null;
    }

    @Override
    public ExtRoaringBitmap xor(ExtRoaringBitmap other) {
        return null;
    }

    @Override
    public ExtRoaringBitmap andNot(ExtRoaringBitmap other) {
        return null;
    }

    @Override
    public ExtRoaringBitmap not() {
        return null;
    }

    @Override
    public ExtRoaringBitmap clone() {
        return null;
    }

    @Override
    public ExtRoaringBitmap fromBytes(byte[] bytes) throws IOException {
        return null;
    }

    @Override
    protected ExtRoaringBitmap newUnit() {
        return null;
    }
}
