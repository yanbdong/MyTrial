package com.lambda;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.MoreExecutors;

import io.vavr.API;
import io.vavr.collection.Stream;

/**
 * @author yanbdong@cienet.com.cn
 * @since Apr 11, 2019
 */
public class Hash {
    

    public static void main(String... args) {

        HashFunction hashFunction = Hashing.goodFastHash(128);
        // List.range(0, 10).forEach(ignore -> API.println(
        // hashFunction.hashString("23232dfsldjflskjdflskjdfalkjdflkajdflkajfafadf",
        // Charset.defaultCharset()).toString()));
        //
        Hasher hasher = hashFunction.newHasher();
        // hasher.putString("23232dfsldjflskjdflskjdfalkjdflkajdflkajfafadf", Charset.defaultCharset());
        // List.range(0, 10).forEach(ignore -> API.println(
        // hasher.hash()));
        ExecutorService service = Executors.newCachedThreadPool();

        Stream.range(1, 100).forEach(it -> service.execute(() -> Hash.doFuture(it)));
        MoreExecutors.shutdownAndAwaitTermination(service, 10, TimeUnit.SECONDS);
        
        
    }

    static Integer mInt = 0;

    static void doFuture(int number) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int i = mInt;
        mInt++;
        if (mInt != ++i) {
            API.println(number);
        }
    }
}
