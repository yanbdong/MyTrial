package com.mapper;

import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-10-30T16:20:37+0800",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 1.8.0_152 (Oracle Corporation)"
)
public class MImpl implements M {

    @Override
    public B mm(A a) {
        if ( a == null ) {
            return null;
        }

        B b = new B();

        b.setYou( String.valueOf( a.getHeart() ) );
        b.setSb( a.getName() );

        return b;
    }
}
