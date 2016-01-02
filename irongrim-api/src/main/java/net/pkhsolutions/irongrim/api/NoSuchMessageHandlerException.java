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
 * Exception thrown when a {@link MessageHandler} can not be found for a specific {@link Message}.
 */
@SuppressWarnings("unused")
public class NoSuchMessageHandlerException extends RuntimeException {

    private final Class<? extends Message> messageClass;

    /**
     * Creates a new {@code NoSuchMessageHandlerException}.
     * 
     * @param messageClass the class of the message (never {@code null}).
     */
    public NoSuchMessageHandlerException(@NotNull Class<? extends Message> messageClass) {
        super("No handler found for message class " + messageClass.getName());
        this.messageClass = messageClass;
    }

    /**
     * Gets the class of the message for which no handler could be found.
     * 
     * @return the message class (never {@code null}).
     */
    @NotNull
    public Class<? extends Message> getMessageClass() {
        return messageClass;
    }
}
