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

import rx.Observable;

/**
 * An extended version of the {@link MessageBroker} interface that also supports asynchronous message handling
 * by using {@link Observable}s from RxJava.
 *
 * @see <a href="https://github.com/ReactiveX/RxJava">RxJava</a>
 * @see AsyncMessageHandler
 */
@SuppressWarnings("unused")
public interface AsyncMessageBroker extends MessageBroker {

    /**
     * Sends the specified message to its message handler using the observer pattern. If the message handler is
     * an instance of {@link AsyncMessageHandler}, {@link AsyncMessageHandler#handleMessageAsync(Message)} will be used.
     * Otherwise, the broker will take care of creating the observable and invoke
     * {@link MessageHandler#handleMessage(Message)}. In that case, the broker will transfer the {@link Context} to any
     * new threads it creates.
     *
     * @param message the message to send (never {@code null}).
     * @return an observable that will handle the message once an observer subscribes to it (never {@code null}).
     * @throws NoSuchMessageHandlerException if no message handler could be found.
     */
    @NotNull
    <MESSAGE extends Message<REPLY>, REPLY> Observable<REPLY> sendAsync(@NotNull MESSAGE message)
        throws NoSuchMessageHandlerException;
}
