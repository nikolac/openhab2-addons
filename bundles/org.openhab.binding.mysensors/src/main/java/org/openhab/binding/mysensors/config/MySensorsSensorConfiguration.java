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
package org.openhab.binding.mysensors.config;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Parameters used for node / thing configuration.
 *
 * @author Tim Oberföll - Initial contribution
 * @author Andrea Cioni - Redesign of the binding
 *
 */
@NonNullByDefault
public class MySensorsSensorConfiguration {
    /**
     * Node ID in the MySensors network
     */
    public int nodeId;

    /**
     * Child ID in the MySensors network
     */
    public int childId;

    /**
     * Should a message request an ACK?
     */
    public boolean requestAck;

    /**
     * If no ACK was received after the defined retries, should the state of the item get reverted?
     */
    public boolean revertState;

    /**
     * Does this node support Smartsleep? A message to the node is only send in response to a heartbeat!
     */
    public boolean smartSleep;

    /**
     * Minutes after, if no message received, thing will be set to OFFLINE
     */
    public int childUpdateTimeout;

    /**
     * Minutes after, if no message received, ALL things with same node ID will be set to OFFLINE
     */
    public int nodeUpdateTimeout;

    /**
     * If no heartbeat received and no attempts left (see: sanCheckSendHeartbeatFailAttempts) ALL thing with same node
     * ID will be set OFFLINE.
     */
    public boolean requestHeartbeatResponse;

    /**
     * If a pure white rgbw value is selected in openhab the binding will only switch on the white led not the rgb part
     */
    public boolean usePureWhiteLightInRGBW;

    @Override
    public String toString() {
        return "MySensorsSensorConfiguration{" + "nodeId=" + nodeId + ", childId=" + childId + ", requestAck="
                + requestAck + ", revertState=" + revertState + ", smartSleep=" + smartSleep + ", childUpdateTimeout="
                + childUpdateTimeout + ", nodeUpdateTimeout=" + nodeUpdateTimeout + ", requestHeartbeatResponse="
                + requestHeartbeatResponse + ", usePureWhiteLightInRGBW=" + usePureWhiteLightInRGBW + '}';
    }
}
