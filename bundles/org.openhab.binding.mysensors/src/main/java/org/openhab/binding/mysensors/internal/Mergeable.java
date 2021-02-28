/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.mysensors.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.mysensors.internal.exception.MergeException;

/**
 * Indicates that a class could be merged to another one of the same type
 *
 * @author Andrea Cioni - Initial contribution
 *
 */
@NonNullByDefault
public interface Mergeable {

    /**
     * Merge an object to another one.
     *
     * @param o
     * @throws MergeException
     */
    public void merge(Object o) throws MergeException;
}
