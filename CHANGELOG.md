# Change Log
All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](http://semver.org/).

## [Unreleased]
### Added
- `PrivacyManager` Android bridge module for controlling GDPR consents
- `GimbalDebugger` Android & iOS bridge module for controlling SDK logging
- `PlaceManager` Android & iOS bridge module for monitoring SDK place/location events
- `BeaconManager` Android & iOS bridge module for receiving beacon events
- .npmignore file to exclude test files and libraries
- Initial API documentation to README for bridged module methods
- `EstablishedLocationsManager` Android & iOS bridge module for receiving established locations

### Fixed
- Remove `promise` from method that caused unexpected behavior on demo app
