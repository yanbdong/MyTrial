package com.cglib;

/**
 * @author yanbodong
 * @date 2021/05/23 21:29
 **/
public interface IMapper {

    Integer doMapper();

    class SpiritMapper implements IMapper {

        @Override
        public Integer doMapper() {
            return 1;
        }
    }
}
