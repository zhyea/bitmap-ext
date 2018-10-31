package org.chobit.bitmap;

import java.util.List;

/**
 * @author robin
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


}
