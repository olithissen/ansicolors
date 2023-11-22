/**
 * MIT License
 *
 * Copyright (c) 2023 Oliver Thissen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


/**
 * A dead-simple generator for ANSI foreground and background colors.
 *
 * <pre>{@code
 *      System.out.print(Colors.fg("#ffcc00") + "Hello!" + Colors.reset());
 * }</pre>
 * <p>
 * It supports
 * - Index colors (<a href="https://www.ditig.com/256-colors-cheat-sheet">256 Colors Cheat Sheet</a>)
 * - RGB components
 * - RGB value as integer
 * - Hex colors
 * - HSV colors
 */
public class Colors {
    public static final String HEX_COLOR_REGEX = "^#([A-Fa-f0-9]{6})$";
    private static final String ANSI_ESCAPE_SEQUENCE = "\u001B";
    private static final String ANSI_FOREGROUND = "38";
    private static final String ANSI_BACKGROUND = "48";
    private static final String ANSI_COLOR_MODE_8BIT = "5";
    private static final String ANSI_COLOR_MODE_RGB = "2";
    private static final String MAIN_TEMPLATE = ANSI_ESCAPE_SEQUENCE + "[%s;%s;%sm";
    private static final String RGB_TEMPLATE = "%s;%s;%s";
    private static final String EIGHT_BIT_TEMPLATE = "%s";

    /**
     * Builds the ANSI escape sequence
     *
     * @param level  Background (48) or foreground (38)
     * @param colors An array of 1 (Indexed) or 3 (RGB) values
     * @return ANSI escape sequence for the given color
     */
    private static String build(String level, int[] colors) {
        String colorMode = colors.length > 1 ? ANSI_COLOR_MODE_RGB : ANSI_COLOR_MODE_8BIT;
        String template = colors.length > 1 ? RGB_TEMPLATE : EIGHT_BIT_TEMPLATE;

        Object[] varargs = new Object[colors.length];
        for (int i = 0; i < colors.length; i++) {
            if (colors[i] < 0 || colors[i] > 255) {
                throw new IllegalArgumentException("Color component or index must be >= 0 and <= 255");
            }
            varargs[i] = colors[i];
        }

        return String.format(MAIN_TEMPLATE, level, colorMode, String.format(template, varargs));
    }

    /**
     * Resets colors.
     *
     * <pre>{@code
     *      System.out.print(Colors.reset());
     * }</pre>
     *
     * @return ANSI color reset sequence
     */
    public static String reset() {
        return ANSI_ESCAPE_SEQUENCE + "[0m";
    }

    /**
     * Returns the ANSI escape sequence to set the foreground to the given color index.
     *
     * <pre>{@code
     *      // Prints "Hello!" in gold
     *      System.out.print(Colors.fg((short)220)) + "Hello!" + Colors.reset());
     * }</pre>
     *
     * @param index The index of the color as short
     * @return ANSI escape sequence for the given color
     * @see <a href="https://www.ditig.com/256-colors-cheat-sheet">256 Colors Cheat Sheet</a>
     */
    public static String fg(short index) {
        return build(ANSI_FOREGROUND, new int[]{index});
    }

    /**
     * Returns the ANSI escape sequence to set the foreground to the given red, green and blue components.
     *
     * <pre>{@code
     *      // Prints "Hello!" in gold
     *      System.out.print(Colors.fg(255, 192, 0)) + "Hello!" + Colors.reset());
     * }</pre>
     *
     * @param red   red component (0 - 255)
     * @param green green component (0 - 255)
     * @param blue  blue component (0 - 255)
     * @return ANSI escape sequence for the given color
     */
    public static String fg(int red, int green, int blue) {
        return build(ANSI_FOREGROUND, new int[]{red, green, blue});
    }

    /**
     * Returns the ANSI escape sequence to set the foreground to the given color value.
     * This is especially useful in combination with other Color sources like java.awt.color
     *
     * <pre>{@code
     *      // Prints "Hello!" in gold
     *      System.out.print(Colors.fg(16763904L) + "Hello!" + Colors.reset());
     * }</pre>
     *
     * @param color Color value between 0 and 16777215
     * @return ANSI escape sequence for the given color
     */
    public static String fg(int color) {
        return build(ANSI_FOREGROUND, intToRgb(color));
    }

    /**
     * Returns the ANSI escape sequence to set the foreground to the given hex color.
     *
     * <pre>{@code
     *      // Prints "Hello!" in gold
     *      System.out.print(Colors.fg("#ffcc00") + "Hello!" + Colors.reset());
     * }</pre>
     *
     * @param hexColor A hex colorin the form of '#ffcc00'
     * @return ANSI escape sequence for the given color
     */
    public static String fg(String hexColor) {
        return build(ANSI_FOREGROUND, hexToRgb(hexColor));
    }

    /**
     * Returns the ANSI escape sequence to set the foreground to the given HSV color.
     *
     * <pre>{@code
     *      // Prints "Hello!" in gold
     *      System.out.print(Colors.fg(48.0, 1.0, 1.0) + "Hello!" + Colors.reset());
     * }</pre>
     *
     * @param hue        the hue value of the color (in degrees, 0 <= hue <= 360)
     * @param saturation the saturation value of the color (0.0 <= saturation <= 1.0)
     * @param value      the value of the color (0.0 <= value <= 1.0)
     * @return ANSI escape sequence for the given color
     */
    public static String fg(double hue, double saturation, double value) {
        return build(ANSI_FOREGROUND, hsvToRgb(hue, saturation, value));
    }

    /**
     * Returns the ANSI escape sequence to set the background to the given color index.
     *
     * <pre>{@code
     *      // Prints "Hello!" on gold background
     *      System.out.print(Colors.bg((short)220)) + "Hello!" + Colors.reset());
     * }</pre>
     *
     * @param index The index of the color
     * @return ANSI escape sequence for the given color
     * @see <a href="https://www.ditig.com/256-colors-cheat-sheet">256 Colors Cheat Sheet</a>
     */
    public static String bg(short index) {
        return build(ANSI_BACKGROUND, new int[]{index});
    }

    /**
     * Returns the ANSI escape sequence to set the background to the given red, green and blue components.
     *
     * <pre>{@code
     *      // Prints "Hello!" on gold background
     *      System.out.print(Colors.bg(255, 192, 0)) + "Hello!" + Colors.reset());
     * }</pre>
     *
     * @param red   red component (0 - 255)
     * @param green green component (0 - 255)
     * @param blue  blue component (0 - 255)
     * @return ANSI escape sequence for the given color
     */
    public static String bg(int red, int green, int blue) {
        return build(ANSI_BACKGROUND, new int[]{red, green, blue});
    }

    /**
     * Returns the ANSI escape sequence to set the background to the given color value.
     * This is especially useful in combination with other Color sources like java.awt.color
     *
     * <pre>{@code
     *      // Prints "Hello!" on gold background
     *      System.out.print(Colors.bg(16763904L) + "Hello!" + Colors.reset());
     * }</pre>
     *
     * @param color Color value between 0 and 16777215
     * @return ANSI escape sequence for the given color
     */
    public static String bg(int color) {
        return build(ANSI_BACKGROUND, intToRgb(color));
    }

    /**
     * Returns the ANSI escape sequence to set the background to the given hex color.
     *
     * <pre>{@code
     *      // Prints "Hello!" on gold background
     *      System.out.print(Colors.bg("#ffcc00") + "Hello!" + Colors.reset());
     * }</pre>
     *
     * @param hexColor A hex colorin the form of '#ffcc00'
     * @return ANSI escape sequence for the given color
     */
    public static String bg(String hexColor) {
        return build(ANSI_BACKGROUND, hexToRgb(hexColor));
    }

    /**
     * Returns the ANSI escape sequence to set the background to the given HSV color.
     *
     * <pre>{@code
     *      // Prints "Hello!" on gold background
     *      System.out.print(Colors.bg(48.0, 1.0, 1.0) + "Hello!" + Colors.reset());
     * }</pre>
     *
     * @param hue        the hue value of the color (in degrees, 0 <= hue <= 360)
     * @param saturation the saturation value of the color (0.0 <= saturation <= 1.0)
     * @param value      the value of the color (0.0 <= value <= 1.0)
     * @return ANSI escape sequence for the given color
     */
    public static String bg(double hue, double saturation, double value) {
        return build(ANSI_BACKGROUND, hsvToRgb(hue, saturation, value));
    }

    /**
     * Converts an integer value color to RGB (red, green, blue) components.
     *
     * @param color A color value between 0 and 16777215
     * @return an integer array containing the RGB values of the color (in range 0-255)
     */
    public static int[] intToRgb(int color) {
        if (color < 0 || color > 16777215) {
            throw new IllegalArgumentException("Color must be >= 0 and <= 16777215");
        }

        int red = (int) ((color >> 16) & 0xFF);
        int green = (int) ((color >> 8) & 0xFF);
        int blue = (int) (color & 0xFF);
        return new int[]{red, green, blue};
    }

    /**
     * Converts a given hex color to RGB (red, green, blue) components.
     *
     * @param hexColor A hex color in the form of "#ffcc00"
     * @return an integer array containing the RGB values of the color (in range 0-255)
     */
    public static int[] hexToRgb(String hexColor) {
        if (!hexColor.matches(HEX_COLOR_REGEX)) {
            throw new IllegalArgumentException("Color must be in the format '#ffcc00'");
        }

        int red = Integer.parseInt(hexColor.substring(1, 3), 16);
        int green = Integer.parseInt(hexColor.substring(3, 5), 16);
        int blue = Integer.parseInt(hexColor.substring(5, 7), 16);
        return new int[]{red, green, blue};
    }

    /**
     * Converts a given HSV (hue, saturation, value) color to RGB (red, green, blue) color space.
     * <p>
     * https://www.cs.rit.edu/~ncs/color/t_convert.html
     *
     * @param hue        the hue value of the color (in degrees, 0 <= hue <= 360)
     * @param saturation the saturation value of the color (0.0 <= saturation <= 1.0)
     * @param value      the value of the color (0.0 <= value <= 1.0)
     * @return an integer array containing the RGB values of the color (in range 0-255)
     */
    private static int[] hsvToRgb(double hue, double saturation, double value) {
        if (hue < 0 || hue > 360) {
            throw new IllegalArgumentException("Hue must be >= 0.0 and <= 360.0.");
        }
        if (saturation < 0 || saturation > 1) {
            throw new IllegalArgumentException("Saturation must be >= 0.0 and <= 1.0.");
        }
        if (value < 0 || value > 1) {
            throw new IllegalArgumentException("Value must be >= 0.0 and <= 1.0.");
        }

        double sectorDouble = (hue / 60.0) % 6;
        int sector = (int) sectorDouble;
        double f = sectorDouble - sector;
        double p = value * (1 - saturation);
        double q = value * (1 - f * saturation);
        double t = value * (1 - (1 - f) * saturation);
        double[] rgb;
        switch (sector) {
            case 0:
                rgb = new double[]{value, t, p};
                break;
            case 1:
                rgb = new double[]{q, value, p};
                break;
            case 2:
                rgb = new double[]{p, value, t};
                break;
            case 3:
                rgb = new double[]{p, q, value};
                break;
            case 4:
                rgb = new double[]{t, p, value};
                break;
            case 5:
                rgb = new double[]{value, p, q};
                break;
            default:
                rgb = new double[]{0, 0, 0};
                break;
        }
        int r = (int) (rgb[0] * 255);
        int g = (int) (rgb[1] * 255);
        int b = (int) (rgb[2] * 255);
        return new int[]{r, g, b};
    }
}
