#import "EstablishedLocationsManagerModule.h"
#import <React/RCTConvert.h>
#import "LocationHelper.h"

@implementation EstablishedLocationsManagerModule

RCT_EXPORT_MODULE(EstablishedLocationsManager)

#pragma mark - String Constants

RCT_EXPORT_METHOD(startMonitoring)
{
    [GMBLEstablishedLocationManager startMonitoring];
}

RCT_EXPORT_METHOD(stopMonitoring)
{
    [GMBLEstablishedLocationManager stopMonitoring];
}

RCT_REMAP_METHOD(isMonitoring,
                 isMonitoringWithResolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject)
{
    BOOL isMonitoring = [GMBLEstablishedLocationManager isMonitoring];
    resolve(@(isMonitoring));
}

RCT_REMAP_METHOD(getEstablishedLocations,
                 establishedLocationsWithResolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject)
{
    NSMutableArray *establishedLocations = [LocationHelper locationsToMaps:[GMBLEstablishedLocationManager establishedLocations]];
    resolve(establishedLocations);
}

@end
