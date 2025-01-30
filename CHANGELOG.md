# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 1.5.4 - 2025-01-30 [Fabric 1.19 only]

### Fixed

- Crash due to unregistered enum argument type

## 1.5.3 - 2024-05-27 [Fabric only]

### Fixed

- Crash due to incorrect mixin remapping

## 1.5.2 - 2024-05-27 [Fabric only]

### Changed

- Removed Porting Lib base module because Create Fabric already has it
- Use API provided by Forge Config API Port for command enum arguments in 1.19 or below

## 1.5.1 - 2024-05-26 [Fabric only]

### Changed

- Removed unnecessary Porting Lib modules to reduce jar size

### Fixed

- A bug in Porting Lib that prevents cross-dimensional chunk loading

## 1.5.0 - 2024-05-21

### Fixed

- Incompatibility with other mods causing clients to be kicked from server when a train station is placed
  - This fix introduces breaking changes in the networking code of Power Loader. Update is required on both the client and server.

## 1.4.3 - 2024-03-18 [Fabric only]

### Changed

- Now targeting Create mod 0.5.1f

## 1.4.2 - 2024-02-26 [Fabric only]

### Fixed

- Invalid crafting recipe due to Forge-specific tags

## 1.4.1 - 2024-02-22 [Fabric only]

### Fixed

- Crash on start due to incorrect refmap path
- Crash on start due to incorrect mixin remap
- Crash on start due to missing Porting Lib dependency

## 1.4.0 - 2024-01-27

### Added

- Translations for Japanese (thanks @Abbage230)

### Changed

- **Config overhaul**
    - All configs are now separate for andesite and brass chunk loaders
    - Allows the static mode to be disabled
    - Allows the contraption and train modes to be configured separately
    - Allows loading range to be configured separately for contraption, train and station modes

## 1.3.3 - 2024-01-12

### Added

- `/powerloader list` command to list all active chunk loaders
- `/powerloader summary` command to count all chunk loaders

## 1.3.2 - 2024-01-06

### Fixed

- Loaded chunks not updating if a train station is removed while an attached chunk loader is active
- Loaded chunks not updating if a chunk loader is placed before a train station

## 1.3.1 - 2024-01-05

### Fixed

- Crash on dedicated server due to client class loading (#7)

## 1.3.0 - 2024-01-04

### Added

- Chunk loaders attaching to Train Stations to only load chunks when a train arrives at the station

### Changed

- Chunk loaders on trains now function using the track graph, which increases reliability for interdimensional
  trains

### Fixed

- Loading incorrect chunks across dimensions
- Chunks not being loaded when a train is coming out of a nether portal and the portal is not already loaded
- Some internal data not being persisted across restart

## 1.2.4 - 2023-12-24

### Added

- Translations for Simplified Chinese (thanks @Huantanhua)

## 1.2.3 - 2023-12-14

### Added

- Advancement for recipe unlock

## 1.2.2 - 2023-11-30

### Added

- Compatibility with contraption controls

## 1.2.1 - 2023-11-30

### Fixed

- Crash if JEI is not installed (#2)

## 1.2.0 - 2023-11-15

### Added

- Empty chunk loaders which can capture ghasts to become functional chunk loaders
- Ponder scenes for ghast capturing
- Configs to control whether chunk loaders work on contraptions

### Changed

- Chunk loader core texture to include a ghast

## 1.1.1 - 2023-11-14

### Fixed

- Mod incompatibility due to partial models being loaded too late (#1)

## 1.1.0 - 2023-11-12

### Added

- Comparator output for chunk loaders
- Config for chunk update interval
- Config for delay before chunk unload
- Ponder scenes for redstone and unloading delay

### Fixed

- Block entity lighting for the chunk loader cores
- Missing datagen

## 1.0.1 - 2023-11-11

### Fixed

- I18n for ponder scenes

## 1.0.0 - 2023-11-11

### Added

- **Andesite chunk loader**
- **Brass chunk loader**
- Ponder scenes
- Crafting recipes
- Speed multiplier config