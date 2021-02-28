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
 *
 * @author Tim Oberföll - Initial contribution
 *
 */
@NonNullByDefault
public class MySensorsRGBTypeConverter implements MySensorsTypeConverter {

    @Override
    public State fromString(String s) {
        int r = Integer.parseInt(s.substring(0, 2), 16);
        int g = Integer.parseInt(s.substring(2, 4), 16);
        int b = Integer.parseInt(s.substring(4, 6), 16);

        HSBType hsbValue = HSBType.fromRGB(r, g, b);

        return hsbValue;
    }

    @Override
    public String fromCommand(Command value) {
        String r = "", g = "", b = "";
        if (value instanceof HSBType) {
            HSBType hsbValue = (HSBType) value;

            Color color = Color.getHSBColor(hsbValue.getHue().floatValue() / 360,
                    hsbValue.getSaturation().floatValue() / 100, hsbValue.getBrightness().floatValue() / 100);

            int redValue = color.getRed();
            int greenValue = color.getGreen();
            int blueValue = color.getBlue();

            r = Integer.toHexString(redValue);
            g = Integer.toHexString(greenValue);
            b = Integer.toHexString(blueValue);

            if (r.length() == 1) {
                r = "0" + r;
            }
            if (g.length() == 1) {
                g = "0" + g;
            }
            if (b.length() == 1) {
                b = "0" + b;
            }
        }

        String hexString = r.concat(g).concat(b);

        return hexString;
    }
}
