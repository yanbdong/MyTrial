package com.lombok;

import java.util.BitSet;

/**
 * @author yanbdong@cienet.com.cn
 * @since Oct 22, 2020
 */
class Test {

    public static void main(String[] args) throws TryMyBest.ActionException {
        BitSet set = new BitSet(2);
        TryMyBest instance = TryMyBest.builder().build();
        TryMyBest.ParsedTwoParts p1 = instance.parse("");
        TryMyBest.ParsedTwoParts p2 = instance.parse("::");
        TryMyBest.ParsedTwoParts p3 = instance.parse("::sb");
        TryMyBest.ParsedTwoParts p4 = instance.parse("sb::");
        TryMyBest.ParsedTwoParts p5 = instance.parse("sb::sb");
        TryMyBest.ParsedTwoParts p6 = instance.parse("sb");
        TryMyBest.ParsedTwoParts p7 = instance.parse("sb::sb,sb::sb");
    }

}
