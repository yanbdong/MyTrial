/*************************************************************************
 *
 * CIeNET CONFIDENTIAL
 * __________________
 *
 *  CIeNET Technologies
 *  All Rights Reserved.
 *
 * NOTICE:  All source codes contained herein are, and remain
 * the property of CIeNET Technologies. The intellectual and technical concepts contained
 * herein are proprietary to CIeNET Technologies
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from CIeNET Technologies.
 *************************************************************************/

package com.bytecode;

import com.google.common.collect.Sets;

import java.util.Set;
import java.util.function.Consumer;

public class ExtraFuncEventPublisher {

    private static final ExtraFuncEventPublisher SINGLETON = new ExtraFuncEventPublisher();

    private Set<Consumer<String>> listenerSet = Sets.newCopyOnWriteArraySet();

    public static ExtraFuncEventPublisher getInstance() {
        return SINGLETON;
    }

    public void addListener(Consumer<String> listener) {
        this.listenerSet.add(listener);
    }

    public void removeListener(Consumer<String> listener) {
        this.listenerSet.remove(listener);
    }

    public void updateExtraFunc(String event) {
        this.listenerSet.forEach(it -> it.accept(event));
    }
}
