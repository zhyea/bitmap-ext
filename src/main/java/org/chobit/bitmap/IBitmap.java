package org.chobit.bitmap;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.Externalizable;
import java.io.IOException;

/**
 * Bitmap接口
 *
 * @param <T> IBitmap的实现
 * @author robin
 */
public interface IBitmap<T extends IBitmap<T>> extends Externalizable {

    /**
     * 添加长整型值到当前bitmap中
     *
     * @param offset 长整型值，对应bitmap上的位置
     */
    void add(final long offset);


    /**
     * 如指定位置上已经有值，将其移除
     *
     * @param offset 长整型值，对应bitmap上的位置
     */
    void remove(final long offset);


    /**
     * 将指定区间 [rangeStart, rangeEndExclusive]内的全部值添加到bitmap中。区间两端的值均包含在内。
     *
     * @param rangeStart        区间开始位置
     * @param rangeEndExclusive 区间结束位置
     */
    void add(final long rangeStart, final long rangeEndExclusive);


    /**
     * 将指定区间 [rangeStart, rangeEndExclusive]内的全部值从当前bitmap中移除。区间两端的值均包含在内。
     *
     * @param rangeStart        区间开始位置
     * @param rangeEndExclusive 区间结束位置
     */
    void remove(final long rangeStart, final long rangeEndExclusive);


    /**
     * 检查bitmap指定位置是否为true
     *
     * @param offset bitmap上的指定位置
     * @return 如果bitmap中的该bit位已经设置有值则为true，反之为false
     */
    boolean check(final long offset);


    /**
     * 当前bitmap与其他bitmap做and运算后的结果
     *
     * @param other 其他bitmap
     * @return and运算结果
     */
    T and(T other);


    /**
     * 当前bitmap与其他bitmap做or运算后的结果
     *
     * @param other 其他bitmap
     * @return and运算结果
     */
    T or(T other);


    /**
     * 当前bitmap与其他bitmap做xor运算后的结果
     *
     * @param other 其他bitmap
     * @return xor运算结果
     */
    T xor(T other);


    /**
     * 当前bitmap与其他bitmap做andNot运算(即差集计算)后的结果
     *
     * @param other 其他bitmap
     * @return and运算结果
     */
    T andNot(T other);


    /**
     * 当前bitmap做not运算后的结果。谨慎使用。
     *
     * @return not运算后的结果
     */
    T not();


    /**
     * 获取bitmap中第一个(最小的)值
     *
     * @return bitmap中第一个(最小的)值，bitmap为空则返回-1
     */
    long first();


    /**
     * 获取bitmap中最后一个(最大的)值
     *
     * @return bitmap中最后一个(最大的)值，bitmap为空则返回-1
     */
    long last();

    /**
     * 评估当前数据结构的内存使用量
     *
     * @return 估计的内存使用量
     */
    long size();


    /**
     * 添加到bitmap的长整型值的总数
     *
     * @return bitmap中长整型值的总数
     */
    long cardinality();


    /**
     * 扩展当前bitmap的size
     *
     * @param newSize 扩展后的size
     * @return 扩展成功则返回true，扩展失败或不需扩展则返回false
     */
    boolean extend(long newSize);


    /**
     * 返回当前bitmap的完全拷贝
     *
     * @return 当前bitmap的完全拷贝
     */
    T copy();


    /**
     * 序列化当前bitmap到输出流
     *
     * @param out 输出流
     * @throws IOException
     */
    void serialize(DataOutput out) throws IOException;


    /**
     * 从输入流中反序列化到当前bitmap
     *
     * @param in 输入流
     * @throws IOException
     */
    void deserialize(DataInput in) throws IOException;


    /**
     * 将当前bitmap序列化为字节数组
     *
     * @return 序列化后的字节数组
     * @throws IOException
     */
    byte[] toBytes() throws IOException;


    /**
     * 将字节数组反序列化为bitmap
     *
     * @param bytes 字节数组
     * @return 字节数组反序列化生成的bitmap
     * @throws IOException
     */
    T fromBytes(byte[] bytes) throws IOException;


    /**
     * bitmap元素iterator
     *
     * @return bitmap元素iterator
     */
    LongIterator longIterator();
}
