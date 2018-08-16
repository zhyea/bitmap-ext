package org.chobit.bitmap;

import java.io.*;

/**
 * 对{@link org.roaringbitmap.RoaringBitmap}的封装。封装后的bitmap仍然只能设置整型值。
 *
 * @author rui.zhang
 */
public class RoaringBitmap implements IBitmap<RoaringBitmap> {


    private final org.roaringbitmap.RoaringBitmap bitmap;
    private long size = -1;


    public RoaringBitmap() {
        this(new org.roaringbitmap.RoaringBitmap(), 0);
    }

    public RoaringBitmap(org.roaringbitmap.RoaringBitmap bitmap) {
        this(bitmap, bitmap.isEmpty() ? 0 : bitmap.last() + 1);
    }

    public RoaringBitmap(org.roaringbitmap.RoaringBitmap bitmap, long size) {
        this.bitmap = bitmap;
        this.size = size;
    }


    @Override
    public void add(long offset) {
        int x = castToInteger(offset);
        bitmap.add(x);
        extend(x + 1);
    }

    @Override
    public void remove(long offset) {
        bitmap.remove(castToInteger(offset));
    }

    @Override
    public void add(long rangeStart, long rangeEndExclusive) {
        int start = castToInteger(rangeStart);
        int end = castToInteger(rangeEndExclusive - 1);
        if (end > start) {
            bitmap.add(rangeStart, rangeEndExclusive);
            extend(rangeEndExclusive);
        }
    }

    @Override
    public void remove(long rangeStart, long rangeEndExclusive) {
        bitmap.remove(rangeStart, rangeEndExclusive);
    }

    @Override
    public boolean check(long offset) {
        return bitmap.contains(castToInteger(offset));
    }

    @Override
    public RoaringBitmap and(RoaringBitmap other) {
        return new RoaringBitmap(org.roaringbitmap.RoaringBitmap.and(this.bitmap, other.bitmap),
                Math.min(this.size, other.size));
    }

    @Override
    public RoaringBitmap or(RoaringBitmap other) {
        return new RoaringBitmap(org.roaringbitmap.RoaringBitmap.or(this.bitmap, other.bitmap),
                Math.max(this.size, other.size));
    }

    @Override
    public RoaringBitmap xor(RoaringBitmap other) {
        return new RoaringBitmap(org.roaringbitmap.RoaringBitmap.xor(this.bitmap, other.bitmap),
                Math.max(this.size, other.size));
    }

    @Override
    public RoaringBitmap andNot(RoaringBitmap other) {
        return new RoaringBitmap(org.roaringbitmap.RoaringBitmap.andNot(this.bitmap, other.bitmap),
                this.size);
    }

    @Override
    public RoaringBitmap not() {
        RoaringBitmap x = clone();
        x.bitmap.flip(0, size);
        return x;
    }

    @Override
    public long first() {
        if (bitmap.isEmpty()) {
            return -1;
        }
        return bitmap.first();
    }

    @Override
    public long last() {
        if (bitmap.isEmpty()) {
            return -1;
        }
        return bitmap.last();
    }

    @Override
    public long size() {
        if (this.size < 0) {
            this.size = this.bitmap.last() + 1;
        }
        return 0;
    }

    @Override
    public long cardinality() {
        return this.bitmap.getLongCardinality();
    }


    /**
     * 当前bitmap的最大size
     *
     * @return 当前bitmap的最大size
     */
    public int maxSize() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean extend(long newSize) {
        newSize = newSize > maxSize() ? maxSize() : newSize;

        if (newSize > size) {
            size = newSize;
            return true;
        }
        return false;
    }

    @Override
    public RoaringBitmap clone() {
        return new RoaringBitmap(this.bitmap.clone(), this.size);
    }


    @Override
    public void serialize(DataOutput out) throws IOException {
        this.bitmap.runOptimize();
        this.bitmap.serialize(out);
    }

    @Override
    public void deserialize(DataInput in) throws IOException {
        this.bitmap.deserialize(in);
        this.size = last() + 1;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        this.bitmap.runOptimize();
        this.bitmap.writeExternal(out);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.bitmap.readExternal(in);
        this.size = last() + 1;
    }

    @Override
    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        serialize(new DataOutputStream(bos));
        return bos.toByteArray();
    }

    @Override
    public RoaringBitmap fromBytes(byte[] bytes) throws IOException {
        deserialize(new DataInputStream(new ByteArrayInputStream(bytes)));
        return this;
    }


    private int castToInteger(Long x) {
        checkOffset(x);
        return x.intValue();
    }


    private void checkOffset(long offset) {
        if (offset > maxSize()) {
            throw new IllegalArgumentException(String.format("Offset must be less than %s.%d. Your offset is: %d.",
                    this.getClass().getSimpleName(), maxSize(), offset));
        }
    }
}
