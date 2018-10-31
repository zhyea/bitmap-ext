package org.chobit.bitmap;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * 抽象bitmap扩展类。
 * <p>
 * 封装了一系列bitmap扩展类的通用方法
 *
 * @param <T> bitmap扩展类的类型，主要用于计算
 * @param <U> bitmap扩展类子bitmap单元的类型，主要用于增删元素
 * @author robin
 */
public abstract class AbstractExtBitmap<T extends AbstractExtBitmap<T, U>, U extends IBitmap<U>>
        implements IBitmap<T> {


    private final List<U> units;


    protected AbstractExtBitmap() {
        this(new ArrayList<>());
    }


    protected AbstractExtBitmap(List<U> units) {
        this.units = units;
    }

    /**
     * 每个bitmap单元的最大容量
     *
     * @return 每个bitmap单元的最大容量
     */
    protected abstract long maxUnitSize();


    /**
     * 根据子单元集合创建新的bitmap实例
     *
     * @param units bitmap子单元集合
     * @return 新建的bitmap
     */
    protected abstract T combine(List<U> units);


    /**
     * 创建新的bitmap单元
     *
     * @return 新的bitmap单元
     */
    protected abstract U newUnit();


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
            throw new IllegalArgumentException("Range start:[" + rangeStart + "] is greater than end:[" + rangeEnd + "].");
        }
        checkOffset(rangeEnd);
        long maxIndex = rangeEnd / maxUnitSize();
        while (unitsLength() <= maxIndex) {
            appendNewUnit();
        }

        long tmpIndex = rangeStart / maxUnitSize();
        while (tmpIndex <= maxIndex) {
            long start = Math.max(0, rangeStart - tmpIndex * maxUnitSize());
            long end = Math.min(maxUnitSize(), rangeEnd - tmpIndex * maxUnitSize());
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
        while (tmpIndex < unitsLength()) {
            long start = Math.max(0, rangeStart - tmpIndex * maxUnitSize());
            long end = Math.min(maxUnitSize(), rangeEnd - tmpIndex * maxUnitSize());
            getUnit((int) tmpIndex).remove(start, end);
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
    public T and(T other) {
        int length = Math.min(this.unitsLength(), other.unitsLength());
        return compute(length, other, IBitmap::and);
    }


    @Override
    public T or(T other) {
        int length = Math.max(this.unitsLength(), other.unitsLength());
        return compute(length, other, IBitmap::or);
    }


    @Override
    public T xor(T other) {
        int length = Math.max(this.unitsLength(), other.unitsLength());
        return compute(length, other, IBitmap::xor);
    }


    @Override
    public T andNot(T another) {
        return andNot0(another);
    }


    @Override
    public T not() {
        List<U> notUnits = new ArrayList<>(this.unitsLength());
        for (U unit : units) {
            append(notUnits, unit.not());
        }
        return combine(notUnits);
    }


    @Override
    public T copy() {
        return combine(copy0());
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
        if (units.isEmpty()) {
            return 0;
        }
        return (unitsLength() - 1) * maxUnitSize() + getUnit(unitsLength() - 1).size();
    }


    @Override
    public long cardinality() {
        long c = 0L;
        for (U u : units) {
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
            U u = getUnit(i);
            u.serialize(out);
            out.writeInt(i);
        }
    }


    @Override
    public void deserialize(DataInput in) throws IOException {
        boolean hasNext = true;
        while (hasNext) {
            U u = newUnit();
            u.deserialize(in);
            append(u);
            int index = in.readInt();
            hasNext = index > 0;
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
    @SuppressWarnings("unchecked")
    public T fromBytes(byte[] bytes) throws IOException {
        deserialize(new DataInputStream(new ByteArrayInputStream(bytes)));
        return (T) this;
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
        return Objects.equals(units, that.units);
    }


    @Override
    public int hashCode() {
        return Objects.hash(units);
    }


    @Override
    public LongIterator longIterator() {
        return new LongIterator() {

            private int index = 0;
            private LongIterator itr;

            @Override
            public boolean hasNext() {
                while (true) {
                    if (index >= units.size()) {
                        return false;
                    }
                    if (itr == null) {
                        itr = units.get(index).longIterator();
                    }
                    if (itr.hasNext()) {
                        return true;
                    } else {
                        itr = null;
                        index++;
                    }
                }
            }

            @Override
            public long next() {
                return itr.next() + index * maxUnitSize();
            }
        };
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        LongIterator itr = longIterator();
        while (itr.hasNext()) {
            if (builder.length() > 2) {
                builder.append(",");
            }
            builder.append(itr.next());
            if (builder.length() > 300 && itr.hasNext()) {
                return builder.append("...]").toString();
            }
        }
        builder.append("]");
        builder.insert(0, getClass().getSimpleName());
        return builder.toString();
    }


    public T andAtIndex(int index, U unit) {
        checkIndex(index);
        if (this.unitsLength() < index + 1) {
            return copy();
        }
        List<U> list = copy0();
        U childUnit = list.get(index);
        list.set(index, childUnit.and(unit));
        return combine(list);
    }


    public T orAtIndex(int index, U unit) {
        checkIndex(index);
        List<U> list = copy0();
        if (list.size() < index + 1) {
            append(list, newUnit());
        }
        U childUnit = list.get(index);
        list.set(index, childUnit.or(unit));
        return combine(list);
    }


    public T xorAtIndex(int index, U unit) {
        checkIndex(index);
        List<U> list = copy0();
        if (list.size() < index + 1) {
            append(list, newUnit());
        }
        U childUnit = list.get(index);
        list.set(index, childUnit.xor(unit));
        return combine(list);
    }


    public T andNotAtIndex(int index, U unit) {
        checkIndex(index);
        if (this.unitsLength() < index + 1) {
            return copy();
        }
        List<U> list = copy0();
        U childUnit = list.get(index);
        list.set(index, childUnit.andNot(unit));
        return combine(list);
    }

    private T compute(int unitsLength, AbstractExtBitmap<T, U> o, BiFunction<U, U, U> func) {
        List<U> resultUnits = new ArrayList<>(unitsLength);
        for (int i = 0; i < unitsLength; i++) {
            U b1 = i < this.unitsLength() ? this.getUnit(i).copy() : newUnit();
            U b2 = i < o.unitsLength() ? o.getUnit(i).copy() : newUnit();
            append(resultUnits, func.apply(b1, b2));
        }
        return combine(resultUnits);
    }


    private T andNot0(AbstractExtBitmap<T, U> other) {
        int length = this.unitsLength();
        List<U> andNotUnits = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            if (i < other.unitsLength()) {
                append(andNotUnits, this.getUnit(i).andNot(other.getUnit(i)));
            } else {
                append(andNotUnits, this.getUnit(i).copy());
            }
        }
        return combine(andNotUnits);
    }


    private List<U> copy0() {
        List<U> copyUnits = new ArrayList<>(this.unitsLength());
        for (U unit : units) {
            append(copyUnits, unit.copy());
        }
        return units;
    }

    private U getUnit(int index) {
        return units.get(index);
    }


    private void checkOffset(long offset) {
        if (offset >= maxUnitSize() * Integer.MAX_VALUE) {
            throw new IllegalArgumentException(String.format("Offset must be less than (%d * Integer.MAX_VALUE). Your offset is %d.", maxUnitSize(), offset));
        }
    }


    private void checkIndex(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Index of unit bitmap cannot be less than zero. Your index is " + index);
        }
    }


    private void appendNewUnit() {
        append(newUnit());
    }


    private void append(U bitmap) {
        append(units, bitmap);
    }


    private void append(List<U> bitmaps, U bitmap) {
        int i = bitmaps.size() - 1;
        while (i >= 0) {
            bitmaps.get(i).extend(maxUnitSize());
            i--;
        }
        bitmaps.add(bitmap);
    }


    protected int unitsLength() {
        return units.size();
    }

}
