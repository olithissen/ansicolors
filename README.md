# ansicolors
A tiny Java class without dependencies to generate ANSI color codes.
And a dead-simple one as well: No dependencies, no imports and possibly compatible with Java 8.

## Usage
Just copy `Colors.java` to your project.
An actual dependency would seem a bit much

## API
Use `Colors.fg(...)` for foreground colors, `Colors.bg(...)` for background colors and `Colors.reset()` to reset. 
`fg` and `bg` behave identically regarding their parameters
