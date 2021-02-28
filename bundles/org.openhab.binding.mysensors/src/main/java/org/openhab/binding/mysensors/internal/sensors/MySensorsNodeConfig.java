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
package org.openhab.binding.mysensors.internal.sensors;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.mysensors.internal.Mergeable;
import org.openhab.binding.mysensors.internal.exception.MergeException;

/**
 * Configuration and parameters of a MySensors node.
 *
 * @author Tim Oberföll - Initial contribution
 * @author Andrea Cioni - Redesign
 *
 */
@NonNullByDefault
public class MySensorsNodeConfig implements Mergeable {

    private boolean requestHeartbeatResponse;
    private int expectUpdateTimeout;

    public MySensorsNodeConfig() {
        requestHeartbeatResponse = false;
        expectUpdateTimeout = -1;
    }

    public boolean getRequestHeartbeatResponse() {
        return requestHeartbeatResponse;
    }

    public void setRequestHeartbeatResponse(boolean requestHeartbeatResponse) {
        this.requestHeartbeatResponse = requestHeartbeatResponse;
    }

    public int getExpectUpdateTimeout() {
        return expectUpdateTimeout;
    }

    public void setExpectUpdateTimeout(int expectUpdateTimeout) {
        this.expectUpdateTimeout = expectUpdateTimeout;
    }

    @Override
    public void merge(Object o) throws MergeException {
        if (o == null || !(o instanceof MySensorsNodeConfig)) {
            throw new MergeException("Invalid object to merge");
        }

        MySensorsNodeConfig nodeConfig = (MySensorsNodeConfig) o;

        requestHeartbeatResponse |= nodeConfig.requestHeartbeatResponse;

        if (expectUpdateTimeout <= 0) {
            expectUpdateTimeout = nodeConfig.expectUpdateTimeout;
        }
    }

    @Override
    public String toString() {
        return "MySensorsNodeConfig [requestHeartbeatResponse=" + requestHeartbeatResponse + ", expectUpdateTimeout="
                + expectUpdateTimeout + "]";
    }
}
