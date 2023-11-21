# ansicolors
A tiny Java class without dependencies to generate ANSI color codes.
And a dead-simple one as well: No dependencies, no imports and possibly compatible with Java 8.

## Usage
Just copy `Colors.java` to your project.
An actual dependency would seem a bit much

## API
Use `Colors.fg(...)` for foreground colors, `Colors.bg(...)` for background colors and `Colors.reset()` to reset. 
`fg` and `bg` behave identically regarding their parameters.
All public methods just return an ANSI escape sequence that sets the color on supported terminals.

```java
  System.out.print(Colors.fg((short) 220) + "Hello World!" + Colors.reset());
```

### Indexed colors (parameters: short)
`Colors.fg((short) 220)`

This sets the color to an indexed color as described in [256 Colors Cheat Sheet](https://www.ditig.com/256-colors-cheat-sheet).

### RGB components (parameters: int, int, int)
`Colors.fg(255, 192, 0)`

The usual RGB color components used everywhere where monitors support colors.

### RGB value (parameters: int)
`Colors.fg(16763904)`

RGB components multiplied give you a 24-bit color value.
This is somewhat handy if your color value source is something like `java.awt.colors` or anything else that justreturns 24-bit color values.

### Hex color (parameters: String)
`Colors.fg("#ffcc00")`

The hex color codes known from around the web.
Basically a hex representation of RGB components.

### HSV color (parameters: double, double, double)
`Colors.fg(48.0, 1.0, 1.0)`

Something like a 3D representation of a color space.
Handy for calculating gradients as the first parameter (`hue`) describes a full circle along the rainbow with 360 degrees.
`saturation` and `value` are values between 0.0 and 1.0.
