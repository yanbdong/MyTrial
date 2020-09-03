package com.lambda;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import io.vavr.API;
import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;

/**
 * @author yanbdong@cienet.com.cn
 * @since Jun 18, 2020
 */
class Chapter8<T> {

    Function<Option<T>, Function<T, Option<T>>> f;

    public static <A> List<A> flattenListT(List<Try<A>> list, A tmp, Option<A> op) {
        return null;
    }

    {
        Function1<List<Try<String>>, Function1<String, Function1<Option<String>, List<String>>>> token = API
                .<List<Try<String>>, String, Option<String>, List<String>> Function(Chapter8::flattenListT).curried();

    }

    /**
     * 以 List<Result<A>> 为参数井返 回 一 个 List<A>，其中包含原始列表中所有值为 success 的元素 ， 并忽略 failure 和 empty值
     * 
     * @param list
     * @param <A>
     * @return
     */
    public static <A> List<A> flattenResult(List<Try<A>> list) {
        BiFunction<Try<A>, List<A>, List<A>> appendTry = (_item, _list) -> _item.fold(ignore -> _list, _list::prepend);
        return list.foldRight(List.empty(), appendTry);
    }

    /**
     * 如 果原始列表中的所有值均为 Success 实例，则为 Success<List<T>>，否则就是 Failure<List<T>
     *
     * @param list
     * @param <A>
     * @return
     */
    public static <A> Try<List<A>> sequence(List<Try<A>> list) {
        BiFunction<A, List<A>, List<A>> appendElement = (_item, _list) -> _list.prepend(_item);
        return Try.of(() -> list.map(Try::get).foldRight(List.empty(), appendElement));
    }

    static <A, B, C> List<C> zip(List<A> aList, List<B> bList, Function2<A, B, C> func) {
        class Tmp {
            Chapter4.TailCall<List<C>> zip0(List<A> aList, List<B> bList, Function2<A, B, C> func, List<C> acc) {
                return (aList.isEmpty() || bList.isEmpty()) ? Chapter4.TailCall.ret(acc)
                        : Chapter4.TailCall.sus(() -> this.zip0(aList.tail(), bList.tail(), func,
                                acc.prepend(func.apply(aList.head(), bList.head()))));
            }
        }
        return new Tmp().zip0(aList, bList, func, List.empty()).eval().reverse();
    }

    public static <A> Boolean startsWith(List<A> list, List<A> sub) {
        class Tmp {
            Chapter4.TailCall<Boolean> startsWith0(List<A> list, List<A> sub) {
                if (sub.isEmpty()) {
                    return Chapter4.TailCall.ret(true);
                }
                if (list.isEmpty()) {
                    return Chapter4.TailCall.ret(false);
                }
                if (!Objects.equals(list.head(), sub.head())) {
                    return Chapter4.TailCall.ret(false);
                }
                return Chapter4.TailCall.sus(() -> startsWith0(list.tail(), sub.tail()));
            }
        }
        return new Tmp().startsWith0(list, sub).eval();
    }

    public static <A> Boolean hasSubSequence(List<A> list, List<A> sub) {
        class Tmp {
            Chapter4.TailCall<Boolean> hasSubSequence0(List<A> list, List<A> sub) {
                if (sub.isEmpty()) {
                    return Chapter4.TailCall.ret(true);
                }
                if (list.isEmpty()) {
                    return Chapter4.TailCall.ret(false);
                }
                if (startsWith(list, sub)) {
                    return Chapter4.TailCall.ret(true);
                }
                return Chapter4.TailCall.sus(() -> hasSubSequence0(list.tail(), sub.tail()));
            }
        }
        return new Tmp().hasSubSequence0(list, sub).eval();
    }

    /**
     * 解折叠操作。给一个初始值，和一个映射函数。当映射函数返回none时，列表生成。 至于映射函数对应的tuple，哪一个是列表项，列表的生成顺序如何，不清楚应该是怎么定义的。
     * 
     * @see List#unfoldRight(Object, Function)
     * 
     * @param seed
     * @param f
     * @param <T>
     * @param <A>
     * @return
     */
    public static <T, A> List<A> unfold(T seed, Function1<? super T, Option<Tuple2<? extends T, ? extends A>>> f) {
        class Tmp {
            Chapter4.TailCall<List<A>> unfold0(T seed, Function1<? super T, Option<Tuple2<? extends T, ? extends A>>> f,
                    List<A> acc) {
                Option<Tuple2<? extends T, ? extends A>> option = f.apply(seed);
                return option.fold(() -> Chapter4.TailCall.ret(acc),
                        it -> Chapter4.TailCall.sus(() -> unfold0(it._1, f, acc.prepend(it._2))));
            }
        }
        return new Tmp().unfold0(seed, f, List.empty()).eval().reverse();
    }

    public static void main(String... args) {
        List<Try<Integer>> s = List.tabulate(10, it -> it != 7 ? Try.success(it) : Try.failure(new RuntimeException()));
        List<Integer> s1 = List.tabulate(17, Function1.identity());
        API.println(flattenResult(s));
        API.println(sequence(s));
        API.println(zip(s, s1, (n1, n2) -> n1 + "," + n2));
        API.println(unfold(0, it -> it == 10 ? Option.none() : Option.of(API.Tuple(it + 1, it))));
        API.println(List.unfoldRight(0, it -> it == 10 ? Option.none() : Option.of(API.Tuple(it, it + 1))));
    }
}