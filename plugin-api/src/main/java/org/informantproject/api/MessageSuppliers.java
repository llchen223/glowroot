/**
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.informantproject.api;

import java.util.Map;

/**
 * @author Trask Stalnaker
 * @since 0.5
 */
public final class MessageSuppliers {

    public static Supplier<Message> of(String message) {
        return Supplier.ofInstance(Message.of(message));
    }

    public static Supplier<Message> of(String template, Object... args) {
        return Supplier.ofInstance(Message.of(template, args));
    }

    public static Supplier<Message> of(String message, Map<String, ?> contextMap) {
        return Supplier.ofInstance(Message.of(message, contextMap));
    }

    public static Supplier<Message> of(String template, Object[] args,
            Map<String, ?> contextMap) {

        return Supplier.ofInstance(Message.of(template, args, contextMap));
    }

    private MessageSuppliers() {}
}