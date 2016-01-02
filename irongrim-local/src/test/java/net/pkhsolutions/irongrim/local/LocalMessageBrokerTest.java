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
import static org.mockito.Mockito.*;

import java.util.Collections;

import net.pkhsolutions.irongrim.api.Message;
import net.pkhsolutions.irongrim.api.MessageHandler;
import net.pkhsolutions.irongrim.api.NoSuchMessageHandlerException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

/**
 * Unit test for {@link LocalMessageBroker}.
 */
public class LocalMessageBrokerTest {

    ApplicationContext applicationContext;
    LocalMessageBroker localMessageBroker;
    MessageHandler<TestMessage, String> messageHandler;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        applicationContext = mock(ApplicationContext.class);
        messageHandler = mock(MessageHandler.class);
        localMessageBroker = new LocalMessageBroker(applicationContext);
    }

    @Test(expected = NoSuchMessageHandlerException.class)
    public void send_noHandlersInApplicationContext_exceptionThrown() {
        when(applicationContext.getBeansOfType(MessageHandler.class)).thenReturn(Collections.emptyMap());
        localMessageBroker.send(new TestMessage());
    }

    @Test
    public void send_handlerFoundAndMessageSentOnlyOnce_replyIsReturned() {
        final TestMessage message = new TestMessage();
        when(messageHandler.supports(TestMessage.class)).thenReturn(true);
        when(messageHandler.handleMessage(message)).thenReturn("hello");
        when(applicationContext.getBeansOfType(MessageHandler.class))
            .thenReturn(Collections.singletonMap("mockHandler", messageHandler));

        assertEquals("hello", localMessageBroker.send(message));
    }

    @Test
    public void send_handlerFoundAndMessageSentTwice_replyIsReturnedBothTimesAndHandlerIsFetchedFromCacheTheSecondTime() {
        final TestMessage message = new TestMessage();
        when(messageHandler.supports(TestMessage.class)).thenReturn(true);
        when(messageHandler.handleMessage(message)).thenReturn("hello");
        when(applicationContext.getBeansOfType(MessageHandler.class))
            .thenReturn(Collections.singletonMap("mockHandler", messageHandler));

        assertEquals("hello", localMessageBroker.send(message));
        assertEquals("hello", localMessageBroker.send(message));

        verify(messageHandler).supports(TestMessage.class);
        verify(applicationContext).getBeansOfType(MessageHandler.class);
    }

    static class TestMessage implements Message<String> {
    }
}
