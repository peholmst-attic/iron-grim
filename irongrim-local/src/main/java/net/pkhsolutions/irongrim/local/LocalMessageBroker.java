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
package net.pkhsolutions.irongrim.local;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import net.pkhsolutions.irongrim.api.Message;
import net.pkhsolutions.irongrim.api.MessageBroker;
import net.pkhsolutions.irongrim.api.MessageHandler;
import net.pkhsolutions.irongrim.api.NoSuchMessageHandlerException;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StopWatch;

/**
 * Thread-safe implementation of {@link MessageBroker} that looks up the {@link MessageHandler}s from the Spring
 * application context. The handlers are cached, so the lookup is only done once for each message type.
 */
public class LocalMessageBroker implements MessageBroker {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalMessageBroker.class);

    private final ApplicationContext applicationContext;
    private Map<Class<?>, MessageHandler> messageHandlerCache = new ConcurrentHashMap<>();

    /**
     * Creates a new {@code LocalMessageBroker}.
     * 
     * @param applicationContext the Spring application context from which message handlers should be fetched.
     */
    public LocalMessageBroker(@NotNull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public <MESSAGE extends Message<REPLY>, REPLY> REPLY send(@NotNull MESSAGE message) {
        Objects.requireNonNull(message);
        LOGGER.trace("Sending message synchronously {}", message);
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            return getHandler(message).map(handler -> handler.handleMessage(message))
                .orElseThrow(() -> new NoSuchMessageHandlerException(message.getClass()));
        } finally {
            stopWatch.stop();
            LOGGER.trace("Message {} handled in {} ms", message, stopWatch.getLastTaskTimeMillis());
        }
    }

    /**
     * Tries to find a message handler for the specified message, either from the cache or from the application context.
     * 
     * @param message the message that needs to be handled (never {@code null}).
     * @return the message handler if found, an empty {@code Optional} otherwise.
     */
    @SuppressWarnings("unchecked")
    @NotNull
    protected <MESSAGE extends Message<REPLY>, REPLY> Optional<MessageHandler<MESSAGE, REPLY>> getHandler(
        @NotNull MESSAGE message) {
        final Class<? extends Message> messageClass = message.getClass();
        MessageHandler messageHandler = messageHandlerCache.get(messageClass);
        if (messageHandler != null) {
            LOGGER.trace("Found handler {} for message class {} in cache", messageHandler, messageClass.getName());
        } else {
            LOGGER.debug("Looking for a handler for message class {}", messageClass.getName());
            messageHandler = findHandler(messageClass);
            if (messageHandler != null) {
                LOGGER.info("Found handler {} for message class {}", messageHandler, messageClass.getName());
                messageHandlerCache.put(messageClass, messageHandler);
            } else {
                LOGGER.warn("Found no handler for message class {}", messageClass);
            }
        }
        return Optional.ofNullable(messageHandler);
    }

    private MessageHandler findHandler(@NotNull Class<? extends Message> messageClass) {
        return applicationContext.getBeansOfType(MessageHandler.class).values().stream()
            .filter(handler -> handler.supports(messageClass)).findFirst().orElse(null);
    }
}
