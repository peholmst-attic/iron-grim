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

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class that provides static access to the current {@code Context}. The strategy pattern is used for actually storing
 * and retrieving the context, which makes it possible to use different implementations for e.g. testing.
 * 
 * @see #setContext(Context)
 * @see #getContext()
 * @see #resetContext()
 */
public final class ContextHolder {

    private static ContextStorageStrategy STRATEGY = new ThreadLocalContextStorageStrategy();

    private ContextHolder() {
    }

    /**
     * Gets the current context.
     * 
     * @return the context (never {@code null}).
     * @throws IllegalStateException if no context is available.
     */
    @NotNull
    public static Context getContext() {
        Context context = STRATEGY.getContext();
        if (context == null) {
            throw new IllegalStateException("No Context available");
        }
        return context;
    }

    /**
     * Sets the current context. This method is intended to be used to transfer contextual data between threads. You
     * would get the context in the parent thread and set it in the child thread before invoking any contextual
     * operations.
     * 
     * @see #resetContext()
     * @param context the context to set.
     */
    public static void setContext(@Nullable Context context) {
        STRATEGY.setContext(context);
    }

    /**
     * Resets the current context. It is recommended to always call this method before leaving a thread to prevent
     * contextual data from leaking between e.g. user sessions.
     */
    public static void resetContext() {
        STRATEGY.resetContext();
    }

    /**
     * Gets the strategy that is used to retrieve and store contexts. By default, this is a
     * {@link ThreadLocalContextStorageStrategy}.
     * 
     * @return the strategy (never {@code null}).
     */
    @NotNull
    public static ContextStorageStrategy getContextStorageStrategy() {
        return STRATEGY;
    }

    /**
     * Sets the strategy to use to retrieve and store contexts.
     * 
     * @param contextStorageStrategy the strategy to set (never {@code null}).
     */
    public static void setContextStorageStrategy(@NotNull ContextStorageStrategy contextStorageStrategy) {
        ContextHolder.STRATEGY = Objects.requireNonNull(contextStorageStrategy);
    }

    /**
     * A context storage strategy is used by the {@link ContextHolder} to actually store and retrieve contexts.
     * 
     * @see ThreadLocalContextStorageStrategy
     */
    interface ContextStorageStrategy {

        /**
         * Gets the current context, or {@code null} if not available.
         */
        @Nullable
        Context getContext();

        /**
         * @see ContextHolder#setContext(Context)
         */
        void setContext(@Nullable Context context);

        /**
         * @see ContextHolder#resetContext()
         */
        void resetContext();
    }

    /**
     * An implementation of {@link ContextStorageStrategy} that stores the context in a {@link ThreadLocal}.
     */
    public static class ThreadLocalContextStorageStrategy implements ContextStorageStrategy {

        private final ThreadLocal<Context> contextThreadLocal = new ThreadLocal<>();

        @Override
        @Nullable
        public Context getContext() {
            return contextThreadLocal.get();
        }

        @Override
        public void setContext(@Nullable Context context) {
            contextThreadLocal.set(context);
        }

        @Override
        public void resetContext() {
            contextThreadLocal.remove();
        }
    }
}
