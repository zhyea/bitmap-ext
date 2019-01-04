# 说明  
bitmap-ext是一种使bitmap支持Long值的方案  

思路比较简单：将多个普通bitmap串联起来一起使用，那么最后能支持的值的范围就是（0， Integer.Integer.MAX_VALUE \* Integer.MAX_VALUE）。串联后的Bitmap即为扩展bitmap，在扩展bitmap中的每个子bitmap元素即为一个Unit。    

# 使用

扩展普通bitmap只需要提供两个类：继承接口IBitmap实现的Bitmap Unit，继承抽象类AbstractExtBitmap的具体扩展类实现。二者都比较简单，可以在几分钟内完成。

在这里已经默认提供了RoaringBitmap的扩展类实现，可以用来参考一下。

# 其他

挺简单的功能，没啥好说的。
