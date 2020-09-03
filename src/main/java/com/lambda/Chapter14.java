package com.lambda;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.vavr.API;
import io.vavr.control.Try;

/**
 *
 * @author yanbdong@cienet.com.cn
 * @since Jun 18, 2020
 */
class Chapter14 {

    interface Actor<T> {

        void tell(T message, Try<Actor<T>> sender);

    }

    interface MessageProcessor<T> {

        void process(T t, Try<Actor<T>> sender);
    }

    static class ActorContext<T> {

        public MessageProcessor<T> getBehavior() {
            return mBehavior;
        }

        private MessageProcessor<T> mBehavior;

        /**
         * 通过注册新的behavior，来改变actor的行为
         * 
         * @param behavior
         */
        public void become(MessageProcessor<T> behavior) {
            mBehavior = behavior;
        }
    }

    static abstract class AbsActor<T> implements Actor<T> {

        protected final String mId;
        private ActorContext<T> mContext = new ActorContext<>();
        private ExecutorService mExecutor = Executors.newSingleThreadExecutor();

        protected AbsActor(String id) {
            mId = id;
            mContext.become(this::onReceive);
        }

        public abstract void onReceive(T t, Try<Actor<T>> sender);

        @Override
        public void tell(T message, Try<Actor<T>> sender) {
            mExecutor.execute(() -> Try.runRunnable(() -> mContext.getBehavior().process(message, sender)));
        }
    }

    static Actor<Integer> mReferee = new AbsActor<Integer>("Referee") {

        @Override
        public void onReceive(Integer integer, Try<Actor<Integer>> sender) {
            API.println("Referee: Game ended after " + integer + " shots");
        }
    };

    static class Player extends AbsActor<Integer> {

        public Player setReferee(Actor<Integer> referee) {
            mReferee = referee;
            return this;
        }

        private Actor<Integer> mReferee;

        protected Player(String id) {
            super("Player " + id);
        }

        @Override
        public void onReceive(Integer integer, Try<Actor<Integer>> sender) {
            API.println(mId + ": " + integer + " shots");
            if (integer >= 10) {
                mReferee.tell(integer, sender);
            } else {
                sender.andThen(it -> it.tell(integer + 1, Try.success(Player.this)));
            }
        }
    }

}