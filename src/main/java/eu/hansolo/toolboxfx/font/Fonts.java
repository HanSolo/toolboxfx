/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2021 Gerrit Grunwald.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.toolboxfx.font;

import javafx.scene.text.Font;


public class Fonts {
    private static final String LATO_LIGHT_NAME;
    private static final String LATO_REGULAR_NAME;
    private static final String LATO_BOLD_NAME;
    private static final String OPEN_SANS_BOLD_NAME;
    private static final String OPEN_SANS_EXTRA_BOLD_NAME;
    private static final String OPEN_SANS_LIGHT_NAME;
    private static final String OPEN_SANS_REGULAR_NAME;
    private static final String OPEN_SANS_SEMIBOLD_NAME;

    private static String latoLightName;
    private static String latoRegularName;
    private static String latoBoldName;
    private static String openSansBoldName;
    private static String openSansExtraBoldName;
    private static String openSansLightName;
    private static String openSansRegularName;
    private static String openSansSemiboldName;


    private Fonts() {}


    static {
        try {
            latoLightName         = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/toolboxfx/font/Lato-Lig.otf"), 10).getName();
            latoRegularName       = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/toolboxfx/font/Lato-Reg.otf"), 10).getName();
            latoBoldName          = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/toolboxfx/font/Lato-Bol.otf"), 10).getName();
            openSansBoldName      = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/toolboxfx/font/OpenSans-Bold.ttf"), 10).getName();
            openSansExtraBoldName = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/toolboxfx/font/OpenSans-ExtraBold.ttf"), 10).getName();
            openSansLightName     = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/toolboxfx/font/OpenSans-Light.ttf"), 10).getName();
            openSansRegularName   = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/toolboxfx/font/OpenSans-Regular.ttf"), 10).getName();
            openSansSemiboldName  = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/toolboxfx/font/OpenSans-Semibold.ttf"), 10).getName();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        LATO_LIGHT_NAME           = latoLightName;
        LATO_REGULAR_NAME         = latoRegularName;
        LATO_BOLD_NAME            = latoBoldName;
        OPEN_SANS_BOLD_NAME       = openSansBoldName;
        OPEN_SANS_EXTRA_BOLD_NAME = openSansExtraBoldName;
        OPEN_SANS_LIGHT_NAME      = openSansLightName;
        OPEN_SANS_REGULAR_NAME    = openSansRegularName;
        OPEN_SANS_SEMIBOLD_NAME   = openSansSemiboldName;
    }


    // ******************** Methods *******************************************
    public static Font latoLight(final double SIZE) { return new Font(LATO_LIGHT_NAME, SIZE); }
    public static Font latoRegular(final double SIZE) { return new Font(LATO_REGULAR_NAME, SIZE); }
    public static Font latoBold(final double SIZE) { return new Font(LATO_BOLD_NAME, SIZE); }

    public static Font opensansBold(final double SIZE) { return new Font(OPEN_SANS_BOLD_NAME, SIZE); }
    public static Font opensansExtraBold(final double SIZE) { return new Font(OPEN_SANS_EXTRA_BOLD_NAME, SIZE); }
    public static Font opensansLight(final double SIZE) { return new Font(OPEN_SANS_LIGHT_NAME, SIZE); }
    public static Font opensansRegular(final double SIZE) { return new Font(OPEN_SANS_REGULAR_NAME, SIZE); }
    public static Font opensansSemibold(final double SIZE) { return new Font(OPEN_SANS_SEMIBOLD_NAME, SIZE); }
}
