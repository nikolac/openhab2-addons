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
package org.openhab.binding.mysensors.internal.protocol.mqtt;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.text.ParseException;

import org.openhab.binding.mysensors.internal.event.MySensorsEventRegister;
import org.openhab.binding.mysensors.internal.gateway.MySensorsGatewayConfig;
import org.openhab.binding.mysensors.internal.protocol.MySensorsAbstractConnection;
import org.openhab.binding.mysensors.internal.protocol.message.MySensorsMessage;
import org.openhab.core.io.transport.mqtt.MqttActionCallback;
import org.openhab.core.io.transport.mqtt.MqttBrokerConnection;
import org.openhab.core.io.transport.mqtt.MqttConnectionObserver;
import org.openhab.core.io.transport.mqtt.MqttConnectionState;
import org.openhab.core.io.transport.mqtt.MqttMessageSubscriber;
import org.openhab.core.io.transport.mqtt.MqttService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * Implements the MQTT connection to a gateway of the MySensors network.
 *
 * @author Tim Oberföll
 * @author Sean McGuire
 *
 */

public class MySensorsMqttConnection extends MySensorsAbstractConnection implements MqttConnectionObserver {

    private MySensorsMqttSubscriber myMqttSub;
    private MqttBrokerConnection connection;

    private PipedOutputStream out;
    private PipedInputStream in;

    private MySensorsMqttPublishCallback myMqttPublishCallback = new MySensorsMqttPublishCallback();

    public MySensorsMqttConnection(MySensorsGatewayConfig myGatewayConfig, MySensorsEventRegister myEventRegister) {
        super(myGatewayConfig, myEventRegister);
        myMqttSub = new MySensorsMqttSubscriber(myGatewayConfig.getTopicSubscribe());
    }

    /**
     * Establishes a link to the broker connection
     */
    @Override
    protected boolean establishConnection() {
        boolean connectionEstablished = false;

        // ## Start workaround
        if (MySensorsMqttService.getMqttService() == null) {
            BundleContext bc = FrameworkUtil.getBundle(getClass()).getBundleContext();
            final ServiceReference sr = bc.getServiceReference(MqttService.class.getName());
            if (sr != null) {
                final MqttService mqtt = (MqttService) bc.getService(sr);
                if (mqtt != null) {
                    new MySensorsMqttService().setMqttService(mqtt);
                }
            }
        }
        // ## End workaround

        if (MySensorsMqttService.getMqttService() == null) {
            logger.error("MqttService is null!");
            return false;
        }

        out = new PipedOutputStream();
        in = new PipedInputStream();

        try {
            in.connect(out);
        } catch (IOException e1) {
            logger.error("Exception thrown while trying to connect input stream for MQTT messages! {}", e1.toString());
        }
        mysConReader = new MySensorsReader(in);
        mysConWriter = new MySensorsMqttWriter(new OutputStream() {

            @Override
            public void write(int b) throws IOException {
            }
        });
        connection = MySensorsMqttService.getMqttService().getBrokerConnection(myGatewayConfig.getBrokerName());

        if (connection == null) {
            logger.error("No connection to broker: {}", myGatewayConfig.getBrokerName());
            return false;
        }

        connection.addConnectionObserver(this);

        connectionStateChanged(connection.connectionState(), null);

        connection.subscribe(myMqttSub.getTopic(), myMqttSub);
        logger.debug("Adding consumer for topic: {}", myMqttSub.getTopic());

        connectionEstablished = startReaderWriterThread(mysConReader, mysConWriter);

        return connectionEstablished;
    }

    /**
     * Removes the consumer from the broker connection
     */
    @Override
    protected void stopConnection() {
        in = null;
        out = null;
        connection.unsubscribe(myMqttSub.getTopic(), myMqttSub);
    }

    /**
     * Receives messages from MQTT transport, translates them and passes them on to
     * the MySensors abstract connection
     *
     * @author Sean McGuire
     * @author Tim Oberföll
     */
    public class MySensorsMqttSubscriber implements MqttMessageSubscriber {
        private String topicSubscribe;

        public MySensorsMqttSubscriber(String topicSubscribe) {
            setTopic(topicSubscribe);
        }

        @Override
        public void processMessage(String topic, byte[] payload) {
            if (!isConnected() || out == null) {
                logger.error("Not connected - Messages can not be processed");
                return;
            }

            String payloadString = new String(payload);
            logger.debug("MQTT message received. Topic: {}, Message: {}", topic, payloadString);
            if (topic.indexOf(myGatewayConfig.getTopicSubscribe()) == 0) {
                String messageTopicPart = topic.replace(myGatewayConfig.getTopicSubscribe() + "/", "");
                logger.debug("Message topic part: {}", messageTopicPart);
                MySensorsMessage incomingMessage = new MySensorsMessage();
                try {
                    incomingMessage = MySensorsMessage.parseMQTT(messageTopicPart, payloadString);
                    logger.debug("Converted MQTT message to MySensors Serial format. Sending on to bridge: {}",
                            MySensorsMessage.generateAPIString(incomingMessage).trim());
                    try {
                        out.write(MySensorsMessage.generateAPIString(incomingMessage).getBytes());
                    } catch (IOException ioe) {
                        ioe.toString();
                    }
                } catch (ParseException pe) {
                    logger.debug("Unable to send message to bridge: {}", pe.toString());
                }
            }
        }

        /**
         * Get the topic that should be listened to
         */
        public String getTopic() {
            return topicSubscribe;
        }

        /**
         * Set the topic that should be listen to
         *
         * @param topicSubscribe topic that should be listened to
         */
        public void setTopic(String topicSubscribe) {
            if (topicSubscribe.substring(topicSubscribe.length() - 1) != "/") {
                topicSubscribe += "/";
            }
            this.topicSubscribe = topicSubscribe + "+/+/+/+/+";
        }
    }

    /**
     *
     * @author Sean McGuire
     * @author Tim Oberföll
     *
     */
    protected class MySensorsMqttWriter extends MySensorsWriter {
        MqttBrokerConnection conn = MySensorsMqttService.getMqttService()
                .getBrokerConnection(myGatewayConfig.getBrokerName());

        public MySensorsMqttWriter(OutputStream outStream) {
            super(outStream);
        }

        @Override
        protected void sendMessage(String msg) {
            logger.debug("Sending MQTT Message: Topic: {}, Message: {}", myGatewayConfig.getTopicPublish(), msg.trim());

            try {
                MySensorsMessage msgOut = MySensorsMessage.parse(msg);
                String newTopic = myGatewayConfig.getTopicPublish() + "/" + MySensorsMessage.generateMQTTString(msgOut);
                conn.publish(newTopic, msgOut.getMsg().getBytes(), 0, false).whenComplete((m, t) -> {
                    if (t == null) {
                        myMqttPublishCallback.onSuccess(newTopic);
                    } else {
                        myMqttPublishCallback.onFailure(newTopic, t);
                    }
                });
            } catch (ParseException e) {
                logger.error("Unable to convert String to MySensorsMessage!", e);
            }
        }
    }

    @Override
    public void connectionStateChanged(MqttConnectionState state, Throwable error) {
        if (state == MqttConnectionState.CONNECTED) {
            logger.debug("Connected to MQTT broker!");
        } else {
            if (error == null) {
                logger.error("MQTT connection offline - Reason unknown");
            } else {
                logger.error("MQTT connection offline - ", error);
            }
        }
    }

    /**
     *
     * Callback for published MQTT messages
     * We're not using the callbacks yet.
     *
     * @author Tim Oberföll
     *
     */
    public class MySensorsMqttPublishCallback implements MqttActionCallback {

        @Override
        public void onSuccess(String topic) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onFailure(String topic, Throwable error) {
            logger.error("Error sending MQTT message to broker: {}.", myGatewayConfig.getBrokerName(), error);
        }
    }
}
