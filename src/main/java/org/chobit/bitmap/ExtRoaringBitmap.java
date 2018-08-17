package org.chobit.bitmap;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author rui.zhang
 */
public class ExtRoaringBitmap extends AbstractExtBitmap<ExtRoaringBitmap, RoaringUnit> {

    public ExtRoaringBitmap() {
        super();
    }

    private ExtRoaringBitmap(List<RoaringUnit> units) {
        super(units);
    }


    @Override
    protected long maxUnitSize() {
        return RoaringUnit.maxSize();
    }

    @Override
    protected ExtRoaringBitmap combine(List<RoaringUnit> units) {
        return new ExtRoaringBitmap(units);
    }

    @Override
    protected RoaringUnit newUnit() {
        return new RoaringUnit();
    }

    @Override
    public ExtRoaringBitmap fromBytes(byte[] bytes) throws IOException {
        super.deserialize(new DataInputStream(new ByteArrayInputStream(bytes)));
        return this;
    }


    /**
     * 将一个bitmap子单元强行添加到bitmap子单元中
     * <p>
     * 慎用该方法
     *
     * @param index 子单元要添加到的位置索引
     * @param unit  bitmap子单元
     * @return bitmap子单元
     */
    public ExtRoaringBitmap appendForce(int index, RoaringUnit unit) {
        if (index < 0) {
            throw new IllegalArgumentException("Index cannot be less than zero.");
        }
        if (null == unit) {
            throw new IllegalArgumentException("Bitmap to be appended cannot be null.");
        }
        super.appendWithIndex(index, unit);
        return this;
    }
}
