# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 1.3.1 - 2024-01-05

### Fixed

- Crash on dedicated server due to client class loading

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

- Translation for Simplified Chinese (thanks @Huantanhua)

## 1.2.3 - 2023-12-14

### Added

- Advancement for recipe unlock

## 1.2.2 - 2023-11-30

### Added

- Compatibility with contraption controls

## 1.2.1 - 2023-11-30

### Fixed

- Crash if JEI is not installed

## 1.2.0 - 2023-11-15

### Added

- Empty chunk loaders which can capture ghasts to become functional chunk loaders
- Ponder scenes for ghast capturing
- Configs to control whether chunk loaders work on contraptions

### Changed

- Chunk loader core texture to include a ghast

## 1.1.1 - 2023-11-14

### Fixed

- Mod incompatibility due to partial models being loaded too late

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