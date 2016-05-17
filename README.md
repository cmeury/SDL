# SDLang Library for Java

This library only has JUnit 4 as an external dependency.
sdlang-java requires JRE 1.7 or higher.

See the tests in src/org/ikayzo/sdl/test and Tag Javadoc:
doc/org/ikayzo/sdl/Tag.html
for examples.

For more info see:
http://sdl.ikayzo.org/docs

Changes since beta 4:
- Changed identifier definition to allow periods (.)
- Fixed bug disallowing use of "$" in identifiers
- Fixed numerous documentation errors
- Various updates to documentation

## Documentation

Javadoc of `boilerplate` branch: https://cdn.rawgit.com/cmeury/SDL/boilerplate/doc/index.html

## Gradle

* Build the project: `gradle build`
* Run all tests: `gradle test`
* Create the JAR `build/libs/sdlang-java-1.x.y.jar`: `gradle jar`
* Generate Javadoc in `doc`: `gradle doc`
