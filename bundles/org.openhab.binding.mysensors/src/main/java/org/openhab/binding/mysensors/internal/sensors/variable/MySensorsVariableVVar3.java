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
package org.openhab.binding.mysensors.internal.sensors.variable;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.mysensors.internal.protocol.message.MySensorsMessageSubType;
import org.openhab.binding.mysensors.internal.sensors.MySensorsVariable;

/**
 * MySensors variable definition according to MySensors serial API
 * https://www.mysensors.org/download/serial_api_20
 *
 * @author Andrea Cioni - Initial contribution
 * @author Tim Oberföll - Redesign
 *
 */
@NonNullByDefault
public class MySensorsVariableVVar3 extends MySensorsVariable {

    public MySensorsVariableVVar3() {
        super(MySensorsMessageSubType.V_VAR3);
    }
}
