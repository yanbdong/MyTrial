package com.lombok;

import com.google.common.base.Strings;

import java.io.File;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * @author yanbdong@cienet.com.cn
 * @since Oct 22, 2020
 */
@Getter
@Builder(setterPrefix = "set")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ToString
class TryMyBest {

    // @NonNull
    File audioFile;
    long recordTime;
    @Builder.Default
    String errorMessage = "";

    ParsedTwoParts parse(String toBeParsedContent) throws ActionException {
        if (Strings.isNullOrEmpty(toBeParsedContent)) {
            return new ParsedTwoParts(ParsedTwoParts.EMPTY_PART, ParsedTwoParts.EMPTY_PART);
        }
        int index = toBeParsedContent.indexOf(Const.ACTION_ARRAY_PARAMETER_DELIMITER);
        if (index == -1) {
            throw new ActionException(3185, "id", toBeParsedContent, getName(), "limited to 2");
        }
        String firstPart = toBeParsedContent.substring(0, index);
        String remainingPart = toBeParsedContent.substring(index + 2);
        if (remainingPart.contains(Const.ACTION_ARRAY_PARAMETER_DELIMITER)) {
            // Only one "::" is allowed
            throw new ActionException(3185, "id", toBeParsedContent, getName(), "limited to 2");
        }
        if (firstPart.length() == 0 && remainingPart.length() == 0) {
            return new ParsedTwoParts(ParsedTwoParts.EMPTY_PART, ParsedTwoParts.EMPTY_PART);
        }
        if (firstPart.length() == 0) {
            return new ParsedTwoParts(ParsedTwoParts.EMPTY_PART, remainingPart);
        }
        if (remainingPart.length() == 0) {
            return new ParsedTwoParts(firstPart, ParsedTwoParts.EMPTY_PART);
        }
        return new ParsedTwoParts(firstPart, remainingPart);
    }

    private String getName() {
        return "";
    }

    static class ParsedTwoParts {
        static final String EMPTY_PART = "";
        final String mFirstPart;
        final String mSecondPart;

        ParsedTwoParts(String firstPart, String secondPart) {
            mFirstPart = firstPart;
            mSecondPart = secondPart;
        }
    }

    class ActionException extends Exception {

        public ActionException(int i, String id, String toBeParsedContent, String name, String s) {
        }
    }
}
