package com.lambda;

import io.vavr.API;
import io.vavr.Function1;
import io.vavr.collection.List;

/**
 * @author yanbdong@cienet.com.cn
 * @since Jun 18, 2020
 */
class Chapter5 {

    static <T> String listToString(List<T> list) {
        return listToString_(new StringBuilder(), list).eval();
    }

    static <T> Chapter4.TailCall<String> listToString_(StringBuilder sb, List<T> list) {
        return list.isEmpty() ? Chapter4.TailCall.ret(sb.delete(sb.length() - 2, sb.length()).toString())
                : Chapter4.TailCall.sus(() -> listToString_(sb.append(list.head()).append(", "), list.tail()));
    }

    /**
     * Convert a method to a function object
     */
    static Function1<StringBuilder, Function1<List<Integer>, Chapter4.TailCall<String>>> ObjectToken1 = API.<StringBuilder, List<Integer>, Chapter4.TailCall<String>> Function(
            Chapter5::listToString_).curried();
    {
    }

    static <T> String listToStringSelfContained(List<T> _list) {
        class SelfContained {
            Function1<StringBuilder, Function1<List<T>, Chapter4.TailCall<String>>> ObjectToken = sb -> list -> list
                    .isEmpty() ? Chapter4.TailCall.ret(sb.delete(sb.length() - 2, sb.length()).toString())
                            : Chapter4.TailCall.sus(() -> this.ObjectToken.apply(sb.append(list.head()).append(", "))
                                    .apply(list.tail()));
        }
        return new SelfContained().ObjectToken.apply(new StringBuilder()).apply(_list).eval();
    }

    static <T> List<T> reverse(List<T> _list) {
        class SelfContained {
            Function1<List<T>, Function1<List<T>, Chapter4.TailCall<List<T>>>> ObjectToken = acc -> list -> list
                    .isEmpty() ? Chapter4.TailCall.ret(acc)
                            : Chapter4.TailCall
                                    .sus(() -> this.ObjectToken.apply(acc.prepend(list.head())).apply(list.tail()));
        }
        return new SelfContained().ObjectToken.apply(List.empty()).apply(_list).eval();
    }

    public static void main(String... args) {
        API.println(listToString(List.of('y', 'b', 'd', 'o', 'm', 'g')));
        API.println(reverse(List.of('y', 'b', 'd', 'o', 'm', 'g')));
        API.println(List.of('y', 'b', 'd', 'o', 'm', 'g').foldLeft(new StringBuilder(),
                (sb, ch) -> sb.append(ch).append(", ")));
    }

}