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

/**
 * Marker interface for messages that are commands. A command typically instructs the system to perform some operation
 * that <em>changes the state of the system</em>. There is no technical difference between implementing this interface
 * and the generic {@link Message} interface. This interface is only intended to make the code easier to read.
 */
@SuppressWarnings("unused")
public interface Command<RESULT> extends Message<RESULT> {
}
