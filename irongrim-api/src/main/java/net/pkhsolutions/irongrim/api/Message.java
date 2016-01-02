/*
 * Copyright 2016 Petter Holmström
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.pkhsolutions.irongrim.api;

import java.io.Serializable;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

/**
 * Generic interface for any message that can be sent through a {@link MessageBroker} and handled by a
 * {@link MessageHandler}. Messages can be bidirectional (where the sender expects a reply) or unidirectional (the
 * sender sends the message and only needs to know whether the message was successfully handled or not).
 * 
 * @see Command
 * @see Query
 * 
 * @param <REPLY> the type of the reply to this message. If no reply is expected, {@link Void} can be used.
 */
public interface Message<REPLY> extends Serializable {

    /**
     * Gets an optional fallback reply that message handlers can return if they for some reason cannot return the
     * "real" reply. This, however, means that the default value cannot be {@code null} since handlers would interpret
     * the empty {@code Optional} as if there was no fallback reply at all.
     * <p>
     * This is an optional feature, meaning that message handlers are not required to use it.
     * </p>
     * 
     * @return the default reply value or an empty {@code Optional}.
     */
    @NotNull
    default Optional<REPLY> getFallbackReply() {
        return Optional.empty();
    }
}
