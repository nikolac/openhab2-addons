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
package org.openhab.binding.mysensors.internal.sensors.child;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.mysensors.internal.exception.NoContentException;
import org.openhab.binding.mysensors.internal.protocol.message.MySensorsMessageSubType;
import org.openhab.binding.mysensors.internal.sensors.MySensorsChild;
import org.openhab.binding.mysensors.internal.sensors.variable.MySensorsVariableVKwh;
import org.openhab.binding.mysensors.internal.sensors.variable.MySensorsVariableVPowerFactor;
import org.openhab.binding.mysensors.internal.sensors.variable.MySensorsVariableVVa;
import org.openhab.binding.mysensors.internal.sensors.variable.MySensorsVariableVVar;
import org.openhab.binding.mysensors.internal.sensors.variable.MySensorsVariableVVar1;
import org.openhab.binding.mysensors.internal.sensors.variable.MySensorsVariableVVar2;
import org.openhab.binding.mysensors.internal.sensors.variable.MySensorsVariableVVar3;
import org.openhab.binding.mysensors.internal.sensors.variable.MySensorsVariableVVar4;
import org.openhab.binding.mysensors.internal.sensors.variable.MySensorsVariableVVar5;
import org.openhab.binding.mysensors.internal.sensors.variable.MySensorsVariableVWatt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MySensors Child definition according to MySensors serial API
 * https://www.mysensors.org/download/serial_api_20
 *
 * @author Andrea Cioni - Initial contribution
 * @author Tim Oberföll - Redesign
 *
 */
@NonNullByDefault
public class MySensorsChildSPower extends MySensorsChild {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public MySensorsChildSPower(int childId) {
        super(childId);
        setPresentationCode(MySensorsMessageSubType.S_POWER);
        try {
            addVariable(new MySensorsVariableVWatt());
            addVariable(new MySensorsVariableVKwh());
            addVariable(new MySensorsVariableVVar());
            addVariable(new MySensorsVariableVVa());
            addVariable(new MySensorsVariableVPowerFactor());
            addVariable(new MySensorsVariableVVar1());
            addVariable(new MySensorsVariableVVar2());
            addVariable(new MySensorsVariableVVar3());
            addVariable(new MySensorsVariableVVar4());
            addVariable(new MySensorsVariableVVar5());
        } catch (NoContentException e) {
            logger.debug("No content to add: ", e);
        }
    }
}
