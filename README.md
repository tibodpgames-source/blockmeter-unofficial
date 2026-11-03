# Block Meter Unofficial

A Fabric mod for measuring block distances and boxes in Minecraft Java Edition 26.3.

**[Download the mod (.jar)](https://github.com/tibodpgames-source/blockmeter-unofficial/releases/download/v22.1.0-beta.2-26.3/BlockMeter-22.1.0-beta.2-26.3.jar)**

Place the JAR in your Minecraft `mods` folder. Requires Fabric and [Fabric API](https://modrinth.com/mod/fabric-api) for Minecraft 26.3. Replace older Block Meter versions.

This is an unofficial port of [Block Meter](https://modrinth.com/mod/blockmeter), based on [ModProg/BlockMeter](https://github.com/ModProg/BlockMeter) at commit `5df2024bebb79bc7c77be1dbfdc5e6b3f434ebe3`. It is not endorsed by the original authors. Their credits, source history and [MIT license](LICENSE) are preserved.

## Requirements

- Minecraft Java Edition 26.3
- Java 25
- Fabric Loader 0.19.5 or later
- Fabric API for Minecraft 26.3, version 0.161.0 or later
- Mod Menu 21 for Minecraft 26.3 is optional

Cloth Config 26.3.159 is bundled. Install only one Block Meter version at a time.

## Controls

| Control | Action |
| --- | --- |
| M | Activate or disable measuring with the held item |
| Right-click two blocks | Set opposite corners of a measurement box |
| Mouse button 4 | Select corners without assigning an item |
| Left Alt | Open colors and options while measuring |
| Shift + M | Remove the last box |
| Ctrl + M | Clear all boxes |

Bindings can be changed in Minecraft's Controls menu. Local measurements do not require a server installation. Sharing measurements requires a compatible server installation.

## Changes for 26.3

- Use Minecraft's SDL-compatible keyboard and mouse input.
- Fix the crash caused by the old unbound-key value in beta.1.
- Normalize invalid saved keyboard codes for Block Meter's own bindings.
- Skip measuring ticks outside a loaded world or while a menu holds the mouse.
- Check server support before sending shared measurements.
- Respect the setting to keep boxes when disabling measuring.

Beta.2 passed the build and all 7 regression tests. The 26.3 port, tests and documentation were developed with coding-assistant help.

## Build

Use JDK 25 and the included Gradle wrapper:

```sh
./gradlew --no-daemon --max-workers=2 test build
```

The mod and sources JARs are written to `build/libs/`.
