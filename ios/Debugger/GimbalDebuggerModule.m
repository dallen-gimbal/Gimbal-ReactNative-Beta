#import "GimbalDebuggerModule.h"
#import <React/RCTConvert.h>

@implementation GimbalDebuggerModule

RCT_EXPORT_MODULE(GimbalDebugger)

#pragma mark - Beacon Sightings Logging

RCT_EXPORT_METHOD(enableBeaconSightingsLogging)
{
    [GMBLDebugger enableBeaconSightingsLogging];
}

RCT_EXPORT_METHOD(disableBeaconSightingsLogging)
{
    [GMBLDebugger disableBeaconSightingsLogging];
}

RCT_REMAP_METHOD(isBeaconSightingsLoggingEnabled, isBeaconSightingsLoggingEnabledWithResolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
  BOOL isBeaconSightingsLoggingEnabled = [GMBLDebugger isBeaconSightingsEnabled];
  resolve(@(isBeaconSightingsLoggingEnabled));
}

#pragma mark - Debug Logging

RCT_EXPORT_METHOD(enableDebugLogging)
{
    [GMBLDebugger enableDebugLogging];
}

RCT_EXPORT_METHOD(disableDebugLogging)
{
    [GMBLDebugger disableDebugLogging];
}

RCT_REMAP_METHOD(isDebugLoggingEnabled, isDebugLoggingEnabledWithResolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
  BOOL isDebugLoggingEnabled = [GMBLDebugger isDebugEnabled];
  resolve(@(isDebugLoggingEnabled));
}

#pragma mark - Place Logging

RCT_EXPORT_METHOD(enablePlaceLogging) {
    [GMBLDebugger enablePlaceLogging];
}

RCT_EXPORT_METHOD(disablePlaceLogging) {
    [GMBLDebugger disablePlaceLogging];
}

RCT_REMAP_METHOD(isPlaceLoggingEnabled, isPlaceLoggingEnabledWithResolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
  BOOL isPlaceLoggingEnabled = [GMBLDebugger isPlaceLoggingEnabled];
  resolve(@(isPlaceLoggingEnabled));
}

@end
