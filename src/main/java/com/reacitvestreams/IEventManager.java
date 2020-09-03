package com.reacitvestreams;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.reactivestreams.Publisher;

/**
 * @author yanbdong@cienet.com.cn
 * @since Aug 17, 2020
 */
interface IEventManager {

    Topic REPORT = new TopicBuilder().setEventType(Integer.class).build();

    IEventManager DEFAULT = new EventManagerImpl();

    <T> void registerPublisher(Topic topic, Publisher<T> publisher);

    <T> void unregisterPublisher(Publisher<T> publisher);

    <T> Publisher<T> fetchPublisher(Topic topic);

    class Topic {

        private final Class<?> mEventType;
        private final String mTopic;
        private final int mHashCode;

        Topic(Class<?> eventType, String topic) {
            mEventType = eventType;
            mTopic = topic;
            mHashCode = Objects.hash(eventType, topic);
        }

        @Override
        public boolean equals(Object obj) {
            return (this == obj) || (obj != null && obj.hashCode() == this.hashCode());
        }

        @Override
        public int hashCode() {
            return mHashCode;
        }
    }

    class TopicBuilder implements Cloneable {

        private Class<?> mEventType;
        private String mTopic;

        public TopicBuilder setEventType(Class<?> eventType) {
            mEventType = eventType;
            return this;
        }

        public TopicBuilder setTopic(String topic) {
            mTopic = topic;
            return this;
        }

        public Topic build() {
            return new Topic(mEventType, mTopic);
        }
    }

    class EventManagerImpl implements IEventManager {

        private final Map<Topic, Publisher<?>> mRegisteredPublisher = new ConcurrentHashMap<>(16);

        @Override
        public <T> void registerPublisher(Topic topic, Publisher<T> publisher) {
            mRegisteredPublisher.put(topic, publisher);
        }

        @Override
        public <T> void unregisterPublisher(Publisher<T> publisher) {
            mRegisteredPublisher.entrySet().removeIf(it -> it.getValue().equals(publisher));
        }

        @Override
        public <T> Publisher<T> fetchPublisher(Topic topic) {
            Publisher<T> publisher = (Publisher<T>) mRegisteredPublisher.get(topic);
            if (null != publisher) {
                return publisher;
            }
            Set<Topic> topicSet = mRegisteredPublisher.keySet();
            return null;
        }
    }

}
