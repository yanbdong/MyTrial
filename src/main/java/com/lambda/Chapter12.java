package com.lambda;

import java.util.function.Function;

import io.vavr.API;
import io.vavr.Function1;
import io.vavr.Tuple2;
import io.vavr.collection.List;

/**
 * 定义一种运算，输入一个state，输出目标值和下一个state。
 *
 * @author yanbdong@cienet.com.cn
 * @since Jun 18, 2020
 */
class Chapter12 {

    /**
     * 给定一个特定的目标值，得到这种运算的一个实例。让任何状态输入，都得到这个目标值和这个状态输入本身。即状态不改变，目标值也永远唯一
     * 
     * @param t
     * @param <State>
     * @param <Type>
     * @return
     */
    <State, Type> Function1<State, Tuple2<Type, State>> unit(Type t) {
        return it -> API.Tuple(t, it);
    }

    /**
     * 如果已有一个运算，用于某一个特定类型A。给定映射A->（作用于B的一个运算），得到了运算B
     *
     * @param func
     * @param mapFunc
     * @param <State>
     * @param <TypeA>
     * @param <TypeB>
     * @return
     */
    <State, TypeA, TypeB> Function1<State, Tuple2<TypeB, State>> flatMap(Function1<State, Tuple2<TypeA, State>> func,
            Function1<TypeA, Function1<State, Tuple2<TypeB, State>>> mapFunc) {
        return it -> func.apply(it).map((t1, t2) -> mapFunc.apply(t1).apply(t2));
    }

    /**
     * 如果已有一个运算，用于某一个特定类型A。给定映射A->B，可以得到另一个运算，用于类型B
     *
     * @param func
     * @param mapFunc
     * @param <State>
     * @param <TypeA>
     * @param <TypeB>
     * @return
     */
    <State, TypeA, TypeB> Function1<State, Tuple2<TypeB, State>> map(Function1<State, Tuple2<TypeA, State>> func,
            Function1<TypeA, TypeB> mapFunc) {
        return it -> func.apply(it).map(mapFunc, Function1.identity());
    }

    <State, TypeA, TypeB> Function1<State, Tuple2<TypeB, State>> map0(Function1<State, Tuple2<TypeA, State>> func,
            Function1<TypeA, TypeB> mapFunc) {
        return flatMap(func, it -> unit(mapFunc.apply(it)));
    }

    /**
     * 如果已有两个运算，用于某一个特定类型A和特定类型B。给定映射A->B->C，可以得到另一个运算，用于类型C
     */
    <State, TypeA, TypeB, TypeC> Function1<State, Tuple2<TypeC, State>> map2(
            Function1<State, Tuple2<TypeA, State>> funcA, Function1<State, Tuple2<TypeB, State>> funcB,
            Function1<TypeA, Function1<TypeB, TypeC>> mapFunc) {
        return it -> funcA.apply(it).map(mapFunc, funcB)
                .map((funcBC, tupleBState) -> new Tuple2<>(funcBC.apply(tupleBState._1), tupleBState._2));
    }

    <State, TypeA, TypeB, TypeC> Function1<State, Tuple2<TypeC, State>> map20(
            Function1<State, Tuple2<TypeA, State>> funcA, Function1<State, Tuple2<TypeB, State>> funcB,
            Function1<TypeA, Function1<TypeB, TypeC>> mapFunc) {
        return flatMap(funcA, a -> map(funcB, b -> mapFunc.apply(a).apply(b)));
    }

    /**
     * 如果已有多个运算，都用于某一个特定类型A。返回一个运算，用于类型List<A>
     */
    <State, Type> Function1<State, Tuple2<List<Type>, State>> sequence(
            List<Function1<State, Tuple2<Type, State>>> funcList) {
        return funcList.foldRight(unit(List.empty()), (item, acc) -> map2(item, acc, x -> y -> y.prepend(x)));
    }

    public interface State<S, A> extends Function1<S, Tuple2<A, S>> {

        static <S, A> State<S, A> unit(A a) {
            return s -> API.Tuple(a, s);
        }

        default <B> State<S, B> flatMap(Function1<A, State<S, B>> f) {
            return s -> apply(s).map((t1, t2) -> f.apply(t1).apply(t2));
        }

        default <B> State<S, B> map(Function1<A, B> f) {
            return flatMap(a -> State.unit(f.apply(a)));
        }

        default <B, C> State<S, C> map2(State<S, B> bs, Function1<A, Function1<B, C>> f) {
            return flatMap(a -> bs.map(b -> f.apply(a).apply(b)));
        }

        default <B, C, D> State<S, D> map3(State<S, B> bs, State<S, C> cs,
                Function1<A, Function1<B, Function1<C, D>>> f) {
            return flatMap(a -> bs.map2(cs, b -> f.apply(a).apply(b)));
        }

        static <S, A> State<S, List<A>> sequence(List<State<S, A>> list) {
            return list.foldRight(State.unit(List.empty()), (item, acc) -> item.map2(acc, x -> y -> y.prepend(x)));
        }
    }

    interface Condition<V, S> extends Function<Tuple2<V, S>, Boolean> {
    }

    interface Transition<V, S> extends Function<Tuple2<V, S>, S> {
    }

    public static class StateMachine<V, S> {

        Function<V, State<S, Void>> mFunction;

        public StateMachine(List<Tuple2<Condition<V, S>, Transition<V, S>>> transitions) {

        }
    }
}