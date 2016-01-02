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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.pkhsolutions.irongrim.api.AsyncMessageHandler;
import net.pkhsolutions.irongrim.api.MessageHandler;
import net.pkhsolutions.irongrim.api.NoSuchMessageHandlerException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import rx.Observable;

/**
 * Unit test for {@link LocalAsyncMessageBroker}.
 */
@SuppressWarnings("unused")
public class LocalAsyncMessageBrokerTest {

    private ApplicationContext applicationContext;
    private LocalAsyncMessageBroker localAsyncMessageBroker;
    private MessageHandler<TestMessage, String> messageHandler;
    private AsyncMessageHandler<TestMessage, String> asyncMessageHandler;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        applicationContext = mock(ApplicationContext.class);
        messageHandler = mock(MessageHandler.class);
        asyncMessageHandler = mock(AsyncMessageHandler.class);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        localAsyncMessageBroker = new LocalAsyncMessageBroker(applicationContext, executorService);
    }

    @Test(expected = NoSuchMessageHandlerException.class)
    public void sendAsync_noHandlersInApplicationContext_exceptionThrown() {
        when(applicationContext.getBeansOfType(MessageHandler.class)).thenReturn(Collections.emptyMap());
        localAsyncMessageBroker.sendAsync(new TestMessage());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void sendAsync_asyncHandlerFound_observableIsReturned() {
        final TestMessage message = new TestMessage();
        final Observable<String> result = mock(Observable.class);
        when(asyncMessageHandler.supports(TestMessage.class)).thenReturn(true);
        when(asyncMessageHandler.handleMessageAsync(message)).thenReturn(result);
        when(applicationContext.getBeansOfType(MessageHandler.class))
            .thenReturn(Collections.singletonMap("mockHandler", asyncMessageHandler));

        assertSame(result, localAsyncMessageBroker.sendAsync(message));
    }

    @Test
    public void sendAsync_normalHandlerFound_observableUsingExecutorServiceIsReturned() {
        final TestMessage message = new TestMessage();
        when(messageHandler.supports(TestMessage.class)).thenReturn(true);
        when(messageHandler.handleMessage(message)).thenReturn("hello");
        when(applicationContext.getBeansOfType(MessageHandler.class))
            .thenReturn(Collections.singletonMap("mockHandler", messageHandler));

        assertEquals("hello", localAsyncMessageBroker.sendAsync(message).toBlocking().single());
    }
}
