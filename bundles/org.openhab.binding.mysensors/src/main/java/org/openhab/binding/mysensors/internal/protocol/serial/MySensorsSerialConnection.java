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
package org.openhab.binding.mysensors.internal.protocol.serial;

import java.io.IOException;
import java.util.TooManyListenersException;

import org.openhab.binding.mysensors.internal.event.MySensorsEventRegister;
import org.openhab.binding.mysensors.internal.gateway.MySensorsGatewayConfig;
import org.openhab.binding.mysensors.internal.protocol.MySensorsAbstractConnection;
import org.openhab.core.io.transport.serial.PortInUseException;
import org.openhab.core.io.transport.serial.SerialPort;
import org.openhab.core.io.transport.serial.SerialPortEvent;
import org.openhab.core.io.transport.serial.SerialPortEventListener;
import org.openhab.core.io.transport.serial.SerialPortIdentifier;
import org.openhab.core.io.transport.serial.SerialPortManager;
import org.openhab.core.io.transport.serial.UnsupportedCommOperationException;

/**
 * Connection to the serial interface where the MySensors Gateway is connected.
 *
 * @author Tim Oberföll - Initial contribution
 * @author Andrea Cioni - Redesign
 *
 */
public class MySensorsSerialConnection extends MySensorsAbstractConnection implements SerialPortEventListener {

    private SerialPort serialConnection = null;
    private SerialPortManager serialPortManager;

    public MySensorsSerialConnection(MySensorsGatewayConfig myConf, MySensorsEventRegister myEventRegister,
            SerialPortManager serialPortManager) {
        super(myConf, myEventRegister);
        this.serialPortManager = serialPortManager;
    }

    /**
     * Tries to accomplish a connection via a serial port to the serial gateway.
     */
    @Override
    public boolean establishConnection() {
        logger.debug("Connecting to {} [baudRate:{}]", this.myGatewayConfig.getSerialPort(),
                this.myGatewayConfig.getBaudRate());

        try {
            SerialPortIdentifier portIdentifier = this.serialPortManager
                    .getIdentifier(this.myGatewayConfig.getSerialPort());
            SerialPort commPort = portIdentifier.open(getClass().getName(), 2000);

            this.serialConnection = commPort;
            this.serialConnection.setSerialPortParams(this.myGatewayConfig.getBaudRate().intValue(),
                    SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            this.serialConnection.enableReceiveThreshold(1);
            this.serialConnection.enableReceiveTimeout(100); // In ms. Small values mean faster shutdown but more cpu
                                                             // usage.

            // RXTX serial port library causes high CPU load
            // Start event listener, which will just sleep and slow down event loop
            try {
                this.serialConnection.addEventListener(this);
                this.serialConnection.notifyOnDataAvailable(true);
                logger.debug("Serial port event listener started");
            } catch (TooManyListenersException e) {
            }

            logger.debug("Successfully connected to serial port.");

            mysConReader = new MySensorsReader(serialConnection.getInputStream());
            mysConWriter = new MySensorsWriter(serialConnection.getOutputStream());

            return startReaderWriterThread(mysConReader, mysConWriter);
        } catch (PortInUseException e) {
            logger.error("Port: {} is already in use", myGatewayConfig.getSerialPort(), e);
        } catch (UnsupportedCommOperationException e) {
            logger.error("Comm operation on port: {} not supported", myGatewayConfig.getSerialPort(), e);
        } catch (IOException e) {
            logger.error("IOException on port: {}", myGatewayConfig.getSerialPort(), e);
        }
        return false;
    }

    /**
     * Initiates a clean disconnect from the serial gateway.
     */
    @Override
    public void stopConnection() {
        logger.debug("Shutting down serial connection!");

        if (mysConWriter != null) {
            mysConWriter.stopWriting();
            mysConWriter = null;
        }

        if (mysConReader != null) {
            mysConReader.stopReader();
            mysConReader = null;
        }

        if (myGatewayConfig.isHardReset()) {
            resetAttachedGateway();
        }

        if (serialConnection != null) {
            try {
                serialConnection.removeEventListener();
                serialConnection.close();
            } catch (Exception e) {
            }
            serialConnection = null;
        }
    }

    @Override
    public void serialEvent(SerialPortEvent arg0) {
        try {
            /*
             * See more details from
             * https://github.com/NeuronRobotics/nrjavaserial/issues/22
             */
            logger.trace("RXTX library CPU load workaround, sleep forever");
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            logger.warn("RXTX library CPU load workaround, sleep forever", e);
        }
    }

    /**
     * Try to reset the attached gateway by using DTR
     *
     */
    public void resetAttachedGateway() {
        logger.debug("Trying to reset of attached gateway with DTR");
        serialConnection.setDTR(true);
        try {
            Thread.sleep(RESET_TIME_IN_MILLISECONDS);
        } catch (InterruptedException e) {
            logger.warn("Wait for reset of attached gateway interrupted!", e);
        }
        serialConnection.setDTR(false);
        logger.debug("Finished reset of attached gateway with DTR");
    }

    @Override
    public String toString() {
        return "MySensorsSerialConnection [serialPort=" + myGatewayConfig.getSerialPort() + ", baudRate="
                + myGatewayConfig.getBaudRate() + "]";
    }
}
