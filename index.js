import { NativeModules } from 'react-native';

const { RNGimbal, PrivacyManager, GimbalDebugger, PlaceManager, BeaconManager, EstablishedLocationsManager } = NativeModules;

export { RNGimbal as default, PrivacyManager, GimbalDebugger, PlaceManager, BeaconManager, EstablishedLocationsManager };
