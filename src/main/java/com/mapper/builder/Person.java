package com.mapper.builder;

/**
 * @author yanbdong@cienet.com.cn
 * @since Mar 04, 2021
 */
class Person {

    private final String name;

    protected Person(Person.Builder builder) {
        this.name = builder.name;
    }

    public static Person.Builder builder() {
        return new Person.Builder();
    }

    public static class Builder {

        private String name;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Person create() {
            return new Person(this);
        }
    }
}
