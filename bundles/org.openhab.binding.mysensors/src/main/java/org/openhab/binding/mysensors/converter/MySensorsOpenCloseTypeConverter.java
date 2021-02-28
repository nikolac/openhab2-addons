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
package org.openhab.binding.mysensors.converter;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.OpenClosedType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;

/**
 * Used to convert a String from an incoming MySensors message to an OpenCloseType
 *
 * @author Tim Oberföll - Initial contribution
 * @author Andrea Cioni - Redesign
 *
 */
@NonNullByDefault
public class MySensorsOpenCloseTypeConverter implements MySensorsTypeConverter {
    @Override
    public State fromString(String string) {
        if ("0".equals(string)) {
            return OpenClosedType.CLOSED;
        } else if ("1".equals(string)) {
            return OpenClosedType.OPEN;
        } else {
            throw new IllegalArgumentException("String: " + string + ", could not be used as OpenClose state");
        }
    }

    @Override
    public String fromCommand(Command value) {
        if (value instanceof OnOffType) {
            if (value == OpenClosedType.CLOSED) {
                return "0";
            } else if (value == OpenClosedType.OPEN) {
                return "1";
            }
        }
        throw new IllegalArgumentException("Passed command: " + value + " is not an OpenClose command");
    }
}
