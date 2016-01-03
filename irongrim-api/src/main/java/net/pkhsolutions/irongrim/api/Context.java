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

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thread-safe collection of context attributes that will be sent with every {@link Message} crossing a thread or VM
 * boundary (e.g. when doing asynchronous message processing or remote calls). If the context is going to be serialized
 * (e.g. when doring remote calls), all context attributes should be serializable as well.
 */
public class Context implements Serializable {

    // Remember to change this every time the class is changed.
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(Context.class);

    private Map<String, Object> attributeMap;

    /**
     * Creates a new, empty {@code Context}.
     */
    public Context() {
        attributeMap = new ConcurrentHashMap<>();
    }

    /**
     * Creates a new {@code Context} that is a copy of the {@code original}.
     * 
     * @param original the context to copy (never {@code null}).
     */
    public Context(@NotNull Context original) {
        this.attributeMap = new ConcurrentHashMap<>(Objects.requireNonNull(original).attributeMap);
    }

    /**
     * Gets the context attribute with the name of the specified class.
     * 
     * @param attributeClass the class (and name) of the attribute (never {@code null}).
     * @return the attribute value, or {@code null} if not set.
     */
    public <T> T getAttribute(@NotNull Class<T> attributeClass) {
        Objects.requireNonNull(attributeClass);
        return getAttribute(attributeClass, attributeClass.getName());
    }

    /**
     * Gets the context attribute with the specified name, cast to the specified class.
     * 
     * @param attributeClass the class of the attribute (never {@code null}).
     * @param attributeName the name of the attribute (never {@code null}).
     * @return the attribute value, or {@code null} if not set.
     */
    public <T> T getAttribute(@NotNull Class<T> attributeClass, @NotNull String attributeName) {
        Objects.requireNonNull(attributeClass);
        Objects.requireNonNull(attributeName);
        return attributeClass.cast(attributeMap.get(attributeName));
    }

    /**
     * Gets the context attribute with the specified name.
     * 
     * @param attributeName the name of the attribute (never {@code null}).
     * @return the attribute value, or {@code null} if not set.
     */
    public Object getAttribute(@NotNull String attributeName) {
        Objects.requireNonNull(attributeName);
        return attributeMap.get(attributeName);
    }

    /**
     * Sets the specified attribute to the specified value.
     * 
     * @param attributeName the name of the attribute (never {@code null}).
     * @param attributeValue the value of the attribute.
     * @return this {@code Context} object, to allow method call chaining.
     */
    @NotNull
    public Context setAttribute(@NotNull String attributeName, @Nullable Object attributeValue) {
        Objects.requireNonNull(attributeName);
        if (attributeValue == null) {
            attributeMap.remove(attributeName);
        } else {
            if (!(attributeValue instanceof Serializable)) {
                LOGGER.warn("The value of attribute {} is not serializable");
            }
            attributeMap.put(attributeName, attributeValue);
        }
        return this;
    }

    /**
     * Sets the specified attribute to the specified value. The full name of the {@code attributeClass} is used as the
     * attribute name.
     *
     * @param attributeClass the class (and name) of the attribute (never {@code null}).
     * @param attributeValue the value of the attribute.
     * @return this {@code Context} object, to allow method call chaining.
     */
    @NotNull
    public <T> Context setAttribute(@NotNull Class<T> attributeClass, @Nullable T attributeValue) {
        Objects.requireNonNull(attributeClass);
        return setAttribute(attributeClass.getName(), attributeValue);
    }
}
