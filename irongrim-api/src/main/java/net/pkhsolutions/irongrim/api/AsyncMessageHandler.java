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

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import rx.Observable;

/**
 * An extended version of the {@link MessageHandler} interface that also supports asynchronous message handling
 * by using {@link Observable}s from RxJava. Implementations can create the Observables themselves or use e.g. Hystrix
 * commands. Implementations should remember to transfer the {@link Context} to any new threads they create.
 * 
 * @see <a href="https://github.com/ReactiveX/RxJava">RxJava</a>
 * @see AsyncMessageBroker
 */
public interface AsyncMessageHandler<MESSAGE extends Message<REPLY>, REPLY> extends MessageHandler<MESSAGE, REPLY> {

    /**
     * {@inheritDoc}
     * <p>
     * This default implementation will delegate to the {@link #handleMessageAsync(Message) asynchronous version} and
     * block until a single reply has been received. If more than one reply is received, the
     * {@link Message#getFallbackReply() fallback reply} will be returned if available, otherwise an exception will be
     * thrown.
     * </p>
     */
    default REPLY handleMessage(@NotNull MESSAGE message) {
        Optional<REPLY> fallbackReply = message.getFallbackReply();
        if (fallbackReply.isPresent()) {
            return handleMessageAsync(message).toBlocking().singleOrDefault(fallbackReply.get());
        } else {
            return handleMessageAsync(message).toBlocking().single();
        }
    }

    /**
     * Handles the specified message asynchronously using the observer pattern.
     * 
     * @param message the message to handle (never {@code null}).
     * @return an observable that will handle the message once an observer subscribes to it (never {@code null}).
     */
    @NotNull
    Observable<REPLY> handleMessageAsync(@NotNull MESSAGE message);
}
