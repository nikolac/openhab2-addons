/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
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

import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.types.State;

/**
 * Used to convert a String from an incoming MySensors message to a PercentType
 *
 * @author Andrea Cioni
 *
 */
public class MySensorsPercentTypeConverter implements MySensorsTypeConverter {

    @Override
    public State fromString(String string) {
        return new PercentType(string);
    }
}
