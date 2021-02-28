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
package org.openhab.binding.mysensors.internal.protocol.message;

/**
 * A MySensors Message consists of 6 parts splitted by semicolon
 *
 * @author Tim Oberföll - Initial contribution
 *
 */
public enum MySensorsMessagePart {
    NODE(0),
    CHILD(1),
    TYPE(2),
    ACK(3),
    SUBTYPE(4),
    PAYLOAD(5);

    private final int id;

    private MySensorsMessagePart(int id) {
        this.id = id;
    }

    public final int getId() {
        return id;
    }
}
