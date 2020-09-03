package com.lambda;

import java.util.Objects;
import java.util.regex.Pattern;

import io.vavr.Function1;
import io.vavr.control.Either;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

/**
 * @author yanbdong@cienet.com.cn
 * @since Jun 18, 2020
 */
class Chapter2 {


    static Pattern emailPattern =
            Pattern.compile("^[a-z0-9._+-]+@[a-z0-9.-]+$");
    static Function1<String, Either<String, String>> emailChecker = s -> Match(s).of(
            Case($(Objects::isNull), Either.left("email must not be null")),
            Case($(String::isEmpty), Either.left("email must not be empty")),
            Case($(it -> emailPattern.matcher(it).matches()), Either::right),
            Case($(), it -> Either.left("email " + it + " is invalid."))
    );

    private static void logError(String s) { System.err.println("Error message logged: " + s); }

    private static void sendVerificationMail(String s) {
        System.out.println("Mail sent to " + s);
    }

    static Function1<Function1<String, Either<String, String>>, Function1<String, Runnable>> validate = f -> s ->
            f.apply(s).<Runnable, Runnable>bimap(it -> () -> logError(it), it -> () -> sendVerificationMail(it))
                    .getOrElseGet(Function1.identity());

    static Function1<Either<String, String>, Runnable> execute = e ->
            e.<Runnable, Runnable>bimap(it -> () -> logError(it), it -> () -> sendVerificationMail(it))
                    .getOrElseGet(Function1.identity());

    static Function1<String, Runnable> total = emailChecker.andThen(execute);

    {

    }

    public static void main(String... args) {
        validate.apply(emailChecker).apply("this.is@my.email").run();
        validate.apply(emailChecker).apply(null).run();
        validate.apply(emailChecker).apply("").run();
        validate.apply(emailChecker).apply("john.doe@acme.com").run();
    }
}