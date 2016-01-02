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
 * Clients use the message broker for sending messages and receiving replies. The message broker takes care of routing
 * the message to the correct {@link MessageHandler}, which might not even be in the same VM.
 *
 * @see AsyncMessageBroker
 */
@SuppressWarnings("unused")
public interface MessageBroker {

    /**
     * Sends the specified message to its message handler and returns the reply immediately.
     * 
     * @param message the message to send (never {@code null}).
     * @return the reply from the message handler.
     * @throws NoSuchMessageHandlerException if no message handler could be found.
     */
    <MESSAGE extends Message<REPLY>, REPLY> REPLY send(@NotNull MESSAGE message) throws NoSuchMessageHandlerException;
}
