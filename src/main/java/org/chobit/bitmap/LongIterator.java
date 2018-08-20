package org.chobit.bitmap;

/**
 * 用来遍历bitmap数值的iterator
 *
 * @author rui.zhang
 */
public interface LongIterator {

    /**
     * 判断有无下一个值
     *
     * @return true 有值, false 无值
     */
    boolean hasNext();


    /**
     * 下一个长整型值
     *
     * @return 长整型值
     */
    long next();

}
