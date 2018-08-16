package org.chobit.bitmap;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author rui.zhang
 */
public abstract class AbstractExtBitmap<T extends AbstractExtBitmap<T, U>, U extends IBitmap<U>> implements IBitmap<T> {


    private final List<T> allUnits;


    protected AbstractExtBitmap() {
        this(new ArrayList<>());
    }


    protected AbstractExtBitmap(List<T> units) {
        this.allUnits = units;
    }

    /**
     * 每个bitmap单元的最大容量
     *
     * @return 每个bitmap单元的最大容量
     */
    protected abstract long maxUnitSize();


    @Override
    public abstract T clone();


    /**
     * 创建新的bitmap单元
     *
     * @return 新的bitmap单元
     */
    protected abstract T newUnit();


    @Override
    public void add(final long offset) {
        checkOffset(offset);
        long index = offset / maxUnitSize();
        long unitOffset = offset % maxUnitSize();
        while (unitsLength() <= index) {
            appendNewUnit();
        }
        getUnit((int) index).add(unitOffset);
    }


    @Override
    public void remove(final long offset) {
        long index = offset / maxUnitSize();
        if (index < unitsLength()) {
            long unitOffset = offset % maxUnitSize();
            getUnit((int) index).remove(unitOffset);
        }
    }


    @Override
    public void add(final long rangeStart, final long rangeEnd) {
        if (rangeStart >= rangeEnd) {
            return;
        }
        checkOffset(rangeEnd);
        long maxIndex = rangeEnd / maxUnitSize();
        while (unitsLength() < maxIndex) {
            appendNewUnit();
        }

        long tmpIndex = rangeStart / maxUnitSize();
        while (tmpIndex <= maxIndex) {
            long start = Math.max(tmpIndex * maxUnitSize(), rangeStart);
            long end = Math.min((tmpIndex + 1) * maxUnitSize(), rangeEnd);
            getUnit((int) tmpIndex).add(start, end);
            tmpIndex++;
        }
    }


    @Override
    public void remove(final long rangeStart, final long rangeEnd) {
        if (rangeStart >= rangeEnd) {
            return;
        }
        checkOffset(rangeEnd);

        long tmpIndex = rangeStart / maxUnitSize();
        while (tmpIndex <= unitsLength()) {
            long start = Math.max(tmpIndex * maxUnitSize(), rangeStart);
            long end = Math.min((tmpIndex + 1) * maxUnitSize(), rangeEnd);
            getUnit((int) tmpIndex).add(start, end);
            tmpIndex++;
        }
    }


    @Override
    public boolean check(long offset) {
        long index = offset / maxUnitSize();
        long unitOffset = offset % maxUnitSize();
        return index < unitsLength() && getUnit((int) index).check(unitOffset);
    }


    @Override
    public long first() {
        for (int i = 0; i < unitsLength(); i++) {
            long firstInUnit = getUnit(i).first();
            if (firstInUnit != -1) {
                return i * maxUnitSize() + firstInUnit;
            }
        }
        return -1;
    }


    @Override
    public long last() {
        for (int i = unitsLength() - 1; i >= 0; i--) {
            long lastInUnit = getUnit(i).last();
            if (lastInUnit != -1) {
                return i * maxUnitSize() + lastInUnit;
            }
        }
        return -1;
    }


    @Override
    public long size() {
        if (allUnits.isEmpty()) {
            return 0;
        }
        return (unitsLength() - 1) * maxUnitSize() + getUnit(unitsLength() - 1).size();
    }


    @Override
    public long cardinality() {
        long c = 0L;
        for (T u : allUnits) {
            c += u.cardinality();
        }
        return c;
    }


    @Override
    public boolean extend(long newSize) {
        long index = (newSize - 1) / maxUnitSize();
        while (unitsLength() <= index) {
            appendNewUnit();
        }
        boolean extended = false;
        for (int i = 0; i < unitsLength(); i++) {
            if (getUnit(i).extend(newSize - i * maxUnitSize())) {
                extended = true;
            }
        }
        return extended;
    }


    @Override
    public void serialize(DataOutput out) throws IOException {
        for (int i = 0; i < unitsLength(); i++) {
            T u = getUnit(i);
            u.serialize(out);
            if (i < unitsLength() - 1) {
                out.writeByte(i);
            }
        }
    }


    @Override
    public void deserialize(DataInput in) throws IOException {
        boolean hasNext = true;
        while (hasNext) {
            T u = newUnit();
            u.deserialize(in);
            append(u);
            hasNext = in.readByte() != -1;
        }
    }


    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        serialize(out);
    }


    @Override
    public void readExternal(ObjectInput in) throws IOException {
        deserialize(in);
    }


    @Override
    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        serialize(new DataOutputStream(bos));
        return bos.toByteArray();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractExtBitmap<T, U> that = (AbstractExtBitmap<T, U>) o;
        return Objects.equals(allUnits, that.allUnits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allUnits);
    }


    protected List<T> and0(AbstractExtBitmap<T, U> o) {
        int count = Math.min(this.unitsLength(), o.unitsLength());
        List<T> andUnits = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            T b1 = this.getUnit(i).clone();
            T b2 = o.getUnit(i).clone();
            append(andUnits, b1.and(b2));
        }
        return andUnits;
    }


    protected List<T> or0(AbstractExtBitmap<T, U> o) {
        int count = Math.max(this.unitsLength(), o.unitsLength());
        List<T> orUnits = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            T b1 = i < this.unitsLength() ? this.getUnit(i).clone() : newUnit();
            T b2 = i < o.unitsLength() ? o.getUnit(i).clone() : newUnit();
            append(orUnits, b1.or(b2));
        }
        return orUnits;
    }


    protected List<T> xor0(AbstractExtBitmap<T, U> o) {
        int count = Math.max(this.unitsLength(), o.unitsLength());
        List<T> xorUnits = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            T b1 = i < this.unitsLength() ? this.getUnit(i).clone() : newUnit();
            T b2 = i < o.unitsLength() ? o.getUnit(i).clone() : newUnit();
            append(xorUnits, b1.xor(b2));
        }
        return xorUnits;
    }


    protected List<T> andNot0(AbstractExtBitmap<T, U> o) {
        List<T> andNotUnits = new ArrayList<>(this.unitsLength());
        for (int i = 0; i < andNotUnits.size(); i++) {
            if (i < o.unitsLength()) {
                append(andNotUnits, this.getUnit(i).andNot(o.getUnit(i)));
            } else {
                append(andNotUnits, this.getUnit(i).clone());
            }
        }
        return andNotUnits;
    }


    protected List<T> not0() {
        List<T> notUnits = new ArrayList<>(this.unitsLength());
        for (T unit : allUnits) {
            append(notUnits, unit.not());
        }
        return notUnits;
    }


    protected List<T> clone0() {
        List<T> cloneUnits = new ArrayList<>(this.unitsLength());
        for (T unit : allUnits) {
            append(cloneUnits, unit.clone());
        }
        return cloneUnits;
    }


    private T getUnit(int index) {
        return allUnits.get(index);
    }


    private void checkOffset(long offset) {
        if (offset >= maxUnitSize() * Integer.MAX_VALUE) {
            throw new IllegalArgumentException(String.format("Offset must be less than (%d * Integer.MAX_VALUE). Your offset is %d.", maxUnitSize(), offset));
        }
    }


    private void appendNewUnit() {
        append(newUnit());
    }


    private void append(T bitmap) {
        append(allUnits, bitmap);
    }


    private void append(List<T> bitmaps, T bitmap) {
        int i = unitsLength() - 1;
        while (i >= 0) {
            bitmaps.get(i).extend(maxUnitSize());
            i--;
        }
        bitmaps.add(bitmap);
    }


    protected int unitsLength() {
        return allUnits.size();
    }


    protected void appendWithIndex(int index, T bitmap) {
        if (index > unitsLength()) {
            while (unitsLength() < index) {
                append(newUnit());
            }
            allUnits.add(bitmap);
        } else if (0 == bitmap.size()) {
            allUnits.add(bitmap);
        } else {
            allUnits.set(index, bitmap);
        }
    }

}
