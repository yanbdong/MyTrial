package com.lambda;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yanbdong@cienet.com.cn
 * @since Jun 01, 2020
 */
public class Inherit {

    public static void main(String... args) throws IOException {

        B b = new B();
        b.m1 = "ba";
        b.m2 = "bb";
        C c = new C();
        c.m1 = "ca";
        c.m3 = "cc";

        CC cc = new CC();
        cc.mList.add(b);
        cc.mList.add(c);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        String s = mapper.writeValueAsString(cc);
        mapper.writeValue(System.out, cc);
        CC ccc = mapper.readValue(s, CC.class);

    }

    public static class CC {

        public List<A> mList = new ArrayList<>();
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "m1", visible = true)
    @JsonSubTypes({@JsonSubTypes.Type(B.class), @JsonSubTypes.Type(C.class)})
    private abstract static class A {

        public String m1;
    }

    @JsonTypeName("ba")
    private static class B extends A {

        public String m2;
    }

    @JsonTypeName("ca")
    private static class C extends A {

        public String m3;
    }
}
