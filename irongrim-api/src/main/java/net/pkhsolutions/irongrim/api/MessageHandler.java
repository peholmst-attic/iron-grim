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

import org.jetbrains.annotations.NotNull;

/**
 * A message handler receives a {@link Message}, does something with it and returns a reply. For {@link Command
 * commands}, the handler is the object that implements the business logic of the command and returns any results as a
 * reply. For {@link Query queries}, the handler would be the object that fetches the data from the database and returns
 * it as a reply.
 * <p>
 * Clients should typically never invoke a message handler directly. Instead, they should use the {@link MessageBroker}.
 * </p>
 * 
 * @see AsyncMessageHandler
 * @see MessageBroker
 */
public interface MessageHandler<MESSAGE extends Message<REPLY>, REPLY> {

    /**
     * Checks if this handler can handle messages of the specified class.
     * 
     * @param messageClass the message class to check (never {@code null}).
     * @return true if the handler supports this message, false otherwise.
     */
    boolean supports(@NotNull Class<? extends Message> messageClass);

    /**
     * Handles the specified message and returns a reply or throws an exception.
     * 
     * @param message the message to handle (never {@code null}).
     * @return the reply to the message.
     */
    REPLY handleMessage(@NotNull MESSAGE message);
}
