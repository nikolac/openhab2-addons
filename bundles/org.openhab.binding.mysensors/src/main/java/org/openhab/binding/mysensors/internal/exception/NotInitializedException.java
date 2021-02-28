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
package org.openhab.binding.mysensors.internal.exception;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Exception occurs if the state of a thing is not yet initialized.
 *
 * @author Tim Oberföll - Initial contribution
 *
 */
@NonNullByDefault
public class NotInitializedException extends Exception {
    private static final long serialVersionUID = -1441354134423131361L;

    public NotInitializedException(String message) {
        super(message);
    }
}
