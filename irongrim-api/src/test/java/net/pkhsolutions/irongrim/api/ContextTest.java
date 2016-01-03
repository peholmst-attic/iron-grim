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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Unit test for {@link Context}.
 */
public class ContextTest {

    @Test
    public void setAttribute_usingAttributeClass_attributeStoredUnderClassName() {
        Context context = new Context();
        context.setAttribute(String.class, "hello");
        assertEquals("hello", context.getAttribute("java.lang.String"));
        assertEquals("hello", context.getAttribute(String.class));
    }

    @Test
    public void setAttribute_setValueThenSetToNull_noAttributeStored() {
        Context context = new Context();
        context.setAttribute("hello", "world");
        context.setAttribute("hello", null);
        assertNull(context.getAttribute("hello"));
    }

    @Test
    public void copyConstructor_changeAttributeInCopy_attributeInOriginalRemainsTheSame() {
        Context context = new Context();
        context.setAttribute(String.class, "hello");
        Context copy = new Context(context);
        assertEquals("hello", copy.getAttribute(String.class));
        copy.setAttribute(String.class, "world");
        assertEquals("hello", context.getAttribute(String.class));
        assertEquals("world", copy.getAttribute(String.class));
    }
}
