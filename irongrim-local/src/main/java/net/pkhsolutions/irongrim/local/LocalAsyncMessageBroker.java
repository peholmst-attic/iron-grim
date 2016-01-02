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

import java.util.Objects;
import java.util.concurrent.ExecutorService;

import net.pkhsolutions.irongrim.api.*;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;

import rx.Observable;

/**
 * Extended version of {@link LocalMessageBroker} that also adds support for asynchronous message handling. If a
 * message handler implements {@link AsyncMessageHandler}, the broker will delegate to the handler directly. Otherwise,
 * the broker will invoke {@link MessageHandler#handleMessage(Message)} within a different thread by using an
 * {@link ExecutorService}.
 */
@SuppressWarnings("unused")
public class LocalAsyncMessageBroker extends LocalMessageBroker implements AsyncMessageBroker {

    private final ExecutorService executorService;

    /**
     * Creates a new {@code LocalMessageBroker}.
     *
     * @param applicationContext the Spring application context from which message handlers should be fetched (never
     *        {@code null}).
     * @param executorService the executor service to use for handling messages asynchronously (never {@code null}).
     */
    public LocalAsyncMessageBroker(@NotNull ApplicationContext applicationContext,
        @NotNull ExecutorService executorService) {
        super(applicationContext);
        this.executorService = Objects.requireNonNull(executorService);
    }

    @Override
    public @NotNull <MESSAGE extends Message<REPLY>, REPLY> Observable<REPLY> sendAsync(@NotNull MESSAGE message) {
        Objects.requireNonNull(message);
        return getHandler(message).map(handler -> handleAsync(message, handler))
            .orElseThrow(() -> new NoSuchMessageHandlerException(message.getClass()));
    }

    @SuppressWarnings("unchecked")
    private <MESSAGE extends Message<REPLY>, REPLY> Observable<REPLY> handleAsync(MESSAGE message,
        MessageHandler<MESSAGE, REPLY> handler) {
        if (handler instanceof AsyncMessageHandler) {
            return ((AsyncMessageHandler) handler).handleMessageAsync(message);
        } else {
            return Observable.create(subscriber -> executorService.submit(() -> {
                subscriber.onNext(handler.handleMessage(message));
                subscriber.onCompleted();
            }));
        }
    }
}
