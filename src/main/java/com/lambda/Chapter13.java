package com.lambda;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.function.Function;

import io.vavr.API;
import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Stream;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;

/**
 *
 * @author yanbdong@cienet.com.cn
 * @since Jun 18, 2020
 */
class Chapter13 {

    public interface State<V, S> {

        <A> A applyValue(Function<V, A> f);

        <A> A applyState(Function<S, A> f);
    }

    public interface Input
            extends Function1<BufferedReader, Tuple2<Either<Throwable, Option<String>>, BufferedReader>> {
    }

    private Input mReadString = bufferedReader -> Tuple
            .of(Try.of(() -> Option.of(bufferedReader.readLine())).fold(Either::left, Either::right), bufferedReader);

    public static void main(String... args) {
        Chapter13 instance = new Chapter13();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Function<Tuple2<Either<Throwable, Option<String>>, BufferedReader>, Try<Option<String>>> closeStream = it -> Try
                .of(() -> {
                    it._2.close();
                    return it._1;
                }).flatMapTry(either -> either.fold(Try::failure, Try::success));
        Function<? super BufferedReader, Option<Tuple2<? extends String, ? extends BufferedReader>>> next = it -> {
            Tuple2<Either<Throwable, Option<String>>, BufferedReader> r = instance.mReadString.apply(it);
            Function<Option<String>, Option<String>> figureOutEmptyString = op -> op.filterNot(String::isEmpty);
            Option<String> ss = r._1.map(figureOutEmptyString).getOrElse(Option::none);
            Function<String, Option<Tuple2<String, BufferedReader>>> ff = s -> Option.some(Tuple.of(s, r._2));
            return ss.flatMap(ff);
        };
        API.println(Stream.unfoldRight(reader, next).toList());
        // instance.mReadString.andThen(closeStream).apply(reader);
    }
}