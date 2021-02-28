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

import java.awt.Color;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.library.types.HSBType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;

/**
 * Used to convert a String from an incoming MySensors message to a HSBType
 * If the color value exceeds a certain limit a pure white color is generated
 *
 * @author Oliver Hilsky - Initial contribution
 * @author Tim Oberföll - Redesign
 *
 */
@NonNullByDefault
public class MySensorsRGBWPureTypeConverter implements MySensorsTypeConverter {

    private static final int WHITE_LIMIT = 245;
    private final MySensorsRGBWTypeConverter standardRGBWConverter = new MySensorsRGBWTypeConverter();

    @Override
    public State fromString(String s) {
        return standardRGBWConverter.fromString(s);
    }

    @Override
    public String fromCommand(Command value) {
        if (value instanceof HSBType) {
            HSBType hsbValue = (HSBType) value;

            Color color = Color.getHSBColor(hsbValue.getHue().floatValue() / 360,
                    hsbValue.getSaturation().floatValue() / 100, hsbValue.getBrightness().floatValue() / 100);

            int redValue = color.getRed();
            int greenValue = color.getGreen();
            int blueValue = color.getBlue();
            int whiteValue = Math.min(redValue, Math.min(greenValue, blueValue));

            // if all color values are over the WHITE_LIMIT it is assumed that this should be pure white and only the
            // white channel is used
            if (redValue > WHITE_LIMIT && greenValue > WHITE_LIMIT && blueValue > WHITE_LIMIT) {
                String rgbDisabled = "000000";
                String w = Integer.toHexString(whiteValue);
                return rgbDisabled.concat(w);
            } else {
                return standardRGBWConverter.fromCommand(value);
            }
        }

        return "";
    }
}
