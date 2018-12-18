/*
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

/*
 *
 *
 *
 *
 *
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

package java.util.concurrent.atomic;
import java.util.function.IntUnaryOperator;
import java.util.function.IntBinaryOperator;
import sun.misc.Unsafe;

/**
 * An {@code int} value that may be updated atomically.  See the
 * {@link java.util.concurrent.atomic} package specification for
 * description of the properties of atomic variables. An
 * {@code AtomicInteger} is used in applications such as atomically
 * incremented counters, and cannot be used as a replacement for an
 * {@link java.lang.Integer}. However, this class does extend
 * {@code Number} to allow uniform access by tools and utilities that
 * deal with numerically-based classes.
 * Integer原子操作类：
 * 在进行并发编程的时候我们需要确保程序在被多个线程并发访问时可以得到正确的结果，也就是实现线程安全。线程安全的定义如下：
 *          当多个线程访问某个类时，不管运行时环境采用何种调度方式或者这些线程将如何交替执行，
 *          并且在主调代码中不需要任何额外的同步或协同，这个类都能表现出正确的行为，那么这个类就是线程安全的。
 *     举个线程不安全的例子。假如我们想实现一个功能来统计网页访问量，你可能想到用count++ 来统计访问量，但是这个自增操作不是线程安全的。
 *     count++ 可以分成三个操作：
 *        获取变量当前值
 *        给获取的当前变量值+1
 *        写回新的值到变量
 *     假设count的初始值为10，当进行并发操作的时候，可能出现线程A和线程B都进行到了1操作，之后又同时进行2操作。A先进行到3操作+1，
 * 现在值为11；注意刚才AB获取到的当前值都是10，所以B执行3操作后，count的值依然是11。这个结果显然不符合我们的要求。
 *
 *   AtomicInteger 来保证线程安全。
 *
 *   独占锁就是线程获取锁后其他的线程都需要挂起，直到持有独占锁的线程释放锁；
 *   乐观锁是先假定没有冲突直接进行操作，如果因为有冲突而失败就重试，直到操作成功。
 *   其中乐观锁用到的机制就是CAS，Compare and Swap。
 * @since 1.5
 * @author Doug Lea
*/
public class AtomicInteger extends Number implements java.io.Serializable {
    private static final long serialVersionUID = 6214790243416807050L;

    // setup to use Unsafe.compareAndSwapInt for updates
//    Unsafe是JDK内部的工具类，主要实现了平台相关的操作。
//   它通过暴露一些Java意义上说“不安全”的功能给Java层代码，
//   来让JDK能够更多的使用Java代码来实现一些原本是平台相关的、需要使用native语言（例如C或C++）才可以实现的功能。
//   该类不应该在JDK核心类库之外使用。
    private static final Unsafe unsafe = Unsafe.getUnsafe();
    private static final long valueOffset;

    static {
        try {
            valueOffset = unsafe.objectFieldOffset
                (AtomicInteger.class.getDeclaredField("value"));
        } catch (Exception ex) { throw new Error(ex); }
    }

    //volatile相当于synchronized的弱实现，也就是说volatile实现了类似synchronized的语义，却又没有锁机制。
//  1、Java 存储模型不会对valatile指令的操作进行重排序：这个保证对volatile变量的操作时按照指令的出现顺序执行的。
//  2、volatile变量不会被缓存在寄存器中（只有拥有线程可见）或者其他对CPU不可见的地方，每次总是从主存中读取volatile变量的结果。
//  也就是说对于volatile变量的修改，其它线程总是可见的，并且不是使用自己线程栈内部的变量。
//    也就是在happens-before法则中，对一个valatile变量的写操作后，其后的任何读操作理解可见此写操作的结果。

//    volatile 的作用是当一个线程修改了共享变量时，另一个线程可以读取到这个修改后的值。
    private volatile int value;

    /**
     * Creates a new AtomicInteger with the given initial value.
     *
     * @param initialValue the initial value
     */
    public AtomicInteger(int initialValue) {
        value = initialValue;
    }

    /**
     * Creates a new AtomicInteger with initial value {@code 0}.
     */
    public AtomicInteger() {
    }

    /**
     * Gets the current value.
     *
     * @return the current value
     */
    public final int get() {
        return value;
    }

    /**
     * Sets to the given value.
     *
     * @param newValue the new value
     */
    public final void set(int newValue) {
        value = newValue;
    }

    /**
     * Eventually sets to the given value.
     *
     * @param newValue the new value
     * @since 1.6
     */
    public final void lazySet(int newValue) {
        unsafe.putOrderedInt(this, valueOffset, newValue);//JDK会在执行这个方法时插入StoreStore内存屏障，避免发生写操作重排序
    }

    /**
     * Atomically sets to the given value and returns the old value.
     * 原子更新为新值，并返回旧值
     * @param newValue the new value
     * @return the previous value
     */
    /**进行循环CAS操作，确保成功的功能函数
      @HotSpotIntrinsicCandidate
        public final int getAndSetInt(Object o, long offset, int newValue) {
        int v;
        do {
        v = getIntVolatile(o, offset);
        } while (!weakCompareAndSetInt(o, offset, v, newValue));
        return v;
        }
     *
     */
    public final int getAndSet(int newValue) {
        return unsafe.getAndSetInt(this, valueOffset, newValue);

    }

    /**
     * Atomically sets the value to the given updated value
     * if the current value {@code ==} the expected value.
     *  比较并更新对象.从内存中根据内存偏移量（valueOffset）取出数据，将取出的值跟expect 比较，
     *  如果数据一致就把内存中的值改为update。
     * @param expect the expected value
     * @param update the new value
     * @return {@code true} if successful. False return indicates that
     * the actual value was not equal to the expected value.
     */
    public final boolean compareAndSet(int expect, int update) {
        return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
    }

    /**
     * Atomically sets the value to the given updated value
     * if the current value {@code ==} the expected value.
     *
     * <p><a href="package-summary.html#weakCompareAndSet">May fail
     * spuriously and does not provide ordering guarantees</a>, so is
     * only rarely an appropriate alternative to {@code compareAndSet}.
     *
     * @param expect the expected value
     * @param update the new value
     * @return {@code true} if successful
     */
    public final boolean weakCompareAndSet(int expect, int update) {
        return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
    }

    /**
     * Atomically increments by one the current value.
     * 原子性在当前值基础上加一，并返回前值
     * @return the previous value
     */
    public final int getAndIncrement() {
        return unsafe.getAndAddInt(this, valueOffset, 1);
    }

    /**
     * Atomically decrements by one the current value.
     * 原子性在当前值基础上减一，返回前值
     * @return the previous value
     */
    public final int getAndDecrement() {
        return unsafe.getAndAddInt(this, valueOffset, -1);
    }

    /**
     * Atomically adds the given value to the current value.
     * 求和并保持原子性
     * @param delta the value to add
     * @return the previous value
     */
    public final int getAndAdd(int delta) {
        return unsafe.getAndAddInt(this, valueOffset, delta);
    }

    /**
     * Atomically increments by one the current value.
     * 原子加一，并返回当前值
     *
     * 注意与getAndIncrement的区别。从字面理解一个是先获取再加，一个是先加在获取
     *
     * @return the updated value
     */
    public final int incrementAndGet() {
        return unsafe.getAndAddInt(this, valueOffset, 1) + 1;
    }

    /**
     * Atomically decrements by one the current value.
     *注意与getAndDecrement的区别。从字面理解一个是先获取再减，一个是先减在获取
     * @return the updated value
     */
    public final int decrementAndGet() {
        return unsafe.getAndAddInt(this, valueOffset, -1) - 1;
    }

    /**
     * Atomically adds the given value to the current value.
     * 原子加一个值，再将更新后的值返回。有别与getAndAdd。
     * @param delta the value to add
     * @return the updated value
     */
    public final int addAndGet(int delta) {
        return unsafe.getAndAddInt(this, valueOffset, delta) + delta;
    }

    /**
     * Atomically updates the current value with the results of
     * applying the given function, returning the previous value. The
     * function should be side-effect-free, since it may be re-applied
     * when attempted updates fail due to contention among threads.
     *
     * @param updateFunction a side-effect-free function
     * @return the previous value
     * @since 1.8
     */
    public final int getAndUpdate(IntUnaryOperator updateFunction) {
        int prev, next;
        do {
            prev = get();
            next = updateFunction.applyAsInt(prev);//自定义处理逻辑
        } while (!compareAndSet(prev, next));
        return prev;//返回前值
    }

    /**
     * Atomically updates the current value with the results of
     * applying the given function, returning the updated value. The
     * function should be side-effect-free, since it may be re-applied
     * when attempted updates fail due to contention among threads.
     *
     * @param updateFunction a side-effect-free function
     * @return the updated value
     * @since 1.8
     */
    public final int updateAndGet(IntUnaryOperator updateFunction) {
        int prev, next;
        do {
            prev = get();
            next = updateFunction.applyAsInt(prev);
        } while (!compareAndSet(prev, next));
        return next;//返回更新值
    }

    /**
     * Atomically updates the current value with the results of
     * applying the given function to the current and given values,
     * returning the previous value. The function should be
     * side-effect-free, since it may be re-applied when attempted
     * updates fail due to contention among threads.  The function
     * is applied with the current value as its first argument,
     * and the given update as the second argument.
     * 双目运算
     * @param x the update value
     * @param accumulatorFunction a side-effect-free function of two arguments
     * @return the previous value
     * @since 1.8
     */
    public final int getAndAccumulate(int x,
                                      IntBinaryOperator accumulatorFunction) {
        int prev, next;
        do {
            prev = get();
            next = accumulatorFunction.applyAsInt(prev, x);
        } while (!compareAndSet(prev, next));
        return prev;
    }

    /**
     * Atomically updates the current value with the results of
     * applying the given function to the current and given values,
     * returning the updated value. The function should be
     * side-effect-free, since it may be re-applied when attempted
     * updates fail due to contention among threads.  The function
     * is applied with the current value as its first argument,
     * and the given update as the second argument.
     *
     * @param x the update value
     * @param accumulatorFunction a side-effect-free function of two arguments
     * @return the updated value
     * @since 1.8
     */
    public final int accumulateAndGet(int x,
                                      IntBinaryOperator accumulatorFunction) {
        int prev, next;
        do {
            prev = get();
            next = accumulatorFunction.applyAsInt(prev, x);
        } while (!compareAndSet(prev, next));
        return next;
    }

    /**
     * Returns the String representation of the current value.
     * @return the String representation of the current value
     */
    public String toString() {
        return Integer.toString(get());
    }

    /**
     * Returns the value of this {@code AtomicInteger} as an {@code int}.
     */
    public int intValue() {
        return get();
    }

    /**
     * Returns the value of this {@code AtomicInteger} as a {@code long}
     * after a widening primitive conversion.
     * @jls 5.1.2 Widening Primitive Conversions
     */
    public long longValue() {
        return (long)get();
    }

    /**
     * Returns the value of this {@code AtomicInteger} as a {@code float}
     * after a widening primitive conversion.
     * @jls 5.1.2 Widening Primitive Conversions
     */
    public float floatValue() {
        return (float)get();
    }

    /**
     * Returns the value of this {@code AtomicInteger} as a {@code double}
     * after a widening primitive conversion.
     * @jls 5.1.2 Widening Primitive Conversions
     */
    public double doubleValue() {
        return (double)get();
    }

}
