package com.cglib;

/**
 * @author yanbodong
 * @date 2021/05/23 21:29
 **/
public interface IWorker {

    String doWorker();

    class Worker implements IWorker {

        @Override
        public String doWorker() {
            return "I'm working";
        }
    }

}
