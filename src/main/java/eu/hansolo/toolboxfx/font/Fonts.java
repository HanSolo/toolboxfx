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
    private static final String COUSINE_REGULAR_NAME;
    private static final String COUSINE_BOLD_NAME;
    private static final String MAZZARDSOFTL_BLACK_NAME;
    private static final String MAZZARDSOFTL_BOLD_NAME;
    private static final String MAZZARDSOFTL_EXTRA_BOLD_NAME;
    private static final String MAZZARDSOFTL_EXTRA_LIGHT_NAME;
    private static final String MAZZARDSOFTL_LIGHT_NAME;
    private static final String MAZZARDSOFTL_MEDIUM_NAME;
    private static final String MAZZARDSOFTL_REGULAR_NAME;
    private static final String MAZZARDSOFTL_SEMI_BOLD_NAME;
    private static final String MAZZARDSOFTL_THIN_NAME;

    private static String latoLightName;
    private static String latoRegularName;
    private static String latoBoldName;
    private static String openSansBoldName;
    private static String openSansExtraBoldName;
    private static String openSansLightName;
    private static String openSansRegularName;
    private static String openSansSemiboldName;
    private static String cousineRegularName;
    private static String cousineBoldName;
    private static String mazzardsoftlBlackName;
    private static String mazzardsoftlBoldName;
    private static String mazzardsoftlExtraBoldName;
    private static String mazzardsoftlExtraLightName;
    private static String mazzardsoftlLightName;
    private static String mazzardsoftlMediumName;
    private static String mazzardsoftlRegularName;
    private static String mazzardsoftlSemiBoldName;
    private static String mazzardsoftlThinName;



    private Fonts() {}


    static {
        try {
            latoLightName              = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/toolboxfx/font/Lato-Lig.otf"), 10).getName();
            latoRegularName            = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/toolboxfx/font/Lato-Reg.otf"), 10).getName();
            latoBoldName               = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/toolboxfx/font/Lato-Bol.otf"), 10).getName();
            openSansBoldName           = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/toolboxfx/font/OpenSans-Bold.ttf"), 10).getName();
            openSansExtraBoldName      = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/toolboxfx/font/OpenSans-ExtraBold.ttf"), 10).getName();
            openSansLightName          = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/toolboxfx/font/OpenSans-Light.ttf"), 10).getName();
            openSansRegularName        = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/toolboxfx/font/OpenSans-Regular.ttf"), 10).getName();
            openSansSemiboldName       = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/toolboxfx/font/OpenSans-Semibold.ttf"), 10).getName();
            cousineRegularName         = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/toolboxfx/font/Cousine-Regular.ttf"), 10).getName();
            cousineBoldName            = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/toolboxfx/font/Cousine-Bold.ttf"), 10).getName();
            mazzardsoftlBlackName      = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/toolboxfx/font/MazzardSoftL-Black.otf"), 10).getName();
            mazzardsoftlBoldName       = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/toolboxfx/font/MazzardSoftL-Bold.otf"), 10).getName();
            mazzardsoftlExtraBoldName  = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/toolboxfx/font/MazzardSoftL-ExtraBold.otf"), 10).getName();
            mazzardsoftlExtraLightName = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/toolboxfx/font/MazzardSoftL-ExtraLight.otf"), 10).getName();
            mazzardsoftlLightName      = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/toolboxfx/font/MazzardSoftL-Light.otf"), 10).getName();
            mazzardsoftlMediumName     = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/toolboxfx/font/MazzardSoftL-Medium.otf"), 10).getName();
            mazzardsoftlRegularName    = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/toolboxfx/font/MazzardSoftL-Regular.otf"), 10).getName();
            mazzardsoftlSemiBoldName   = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/toolboxfx/font/MazzardSoftL-SemiBold.otf"), 10).getName();
            mazzardsoftlThinName       = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/toolboxfx/font/MazzardSoftL-Thin.otf"), 10).getName();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        LATO_LIGHT_NAME               = latoLightName;
        LATO_REGULAR_NAME             = latoRegularName;
        LATO_BOLD_NAME                = latoBoldName;
        OPEN_SANS_BOLD_NAME           = openSansBoldName;
        OPEN_SANS_EXTRA_BOLD_NAME     = openSansExtraBoldName;
        OPEN_SANS_LIGHT_NAME          = openSansLightName;
        OPEN_SANS_REGULAR_NAME        = openSansRegularName;
        OPEN_SANS_SEMIBOLD_NAME       = openSansSemiboldName;
        COUSINE_REGULAR_NAME          = cousineRegularName;
        COUSINE_BOLD_NAME             = cousineBoldName;
        MAZZARDSOFTL_BLACK_NAME       = mazzardsoftlBlackName;
        MAZZARDSOFTL_BOLD_NAME        = mazzardsoftlBoldName;
        MAZZARDSOFTL_EXTRA_BOLD_NAME  = mazzardsoftlExtraBoldName;
        MAZZARDSOFTL_EXTRA_LIGHT_NAME = mazzardsoftlExtraLightName;
        MAZZARDSOFTL_LIGHT_NAME       = mazzardsoftlLightName;
        MAZZARDSOFTL_MEDIUM_NAME      = mazzardsoftlMediumName;
        MAZZARDSOFTL_REGULAR_NAME     = mazzardsoftlRegularName;
        MAZZARDSOFTL_SEMI_BOLD_NAME   = mazzardsoftlSemiBoldName;
        MAZZARDSOFTL_THIN_NAME        = mazzardsoftlThinName;
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

    public static Font cousineRegular(final double SIZE) { return new Font(COUSINE_REGULAR_NAME, SIZE); }
    public static Font cousineBold(final double SIZE) { return new Font(COUSINE_BOLD_NAME, SIZE); }

    public static Font mazzardsoftlBlackName(final double SIZE) { return new Font(MAZZARDSOFTL_BLACK_NAME, SIZE); }
    public static Font mazzardsoftlBoldName(final double SIZE) { return new Font(MAZZARDSOFTL_BOLD_NAME, SIZE); }
    public static Font mazzardsoftlExtraBoldName(final double SIZE) { return new Font(MAZZARDSOFTL_EXTRA_BOLD_NAME, SIZE); }
    public static Font mazzardsoftlExtraLightName(final double SIZE) { return new Font(MAZZARDSOFTL_EXTRA_LIGHT_NAME, SIZE); }
    public static Font mazzardsoftlLightName(final double SIZE) { return new Font(MAZZARDSOFTL_LIGHT_NAME, SIZE); }
    public static Font mazzardsoftlMediumName(final double SIZE) { return new Font(MAZZARDSOFTL_MEDIUM_NAME, SIZE); }
    public static Font mazzardsoftlRegularName(final double SIZE) { return new Font(MAZZARDSOFTL_REGULAR_NAME, SIZE); }
    public static Font mazzardsoftlSemiBoldName(final double SIZE) { return new Font(MAZZARDSOFTL_SEMI_BOLD_NAME, SIZE); }
    public static Font mazzardsoftlThinName(final double SIZE) { return new Font(MAZZARDSOFTL_THIN_NAME, SIZE); }
}
