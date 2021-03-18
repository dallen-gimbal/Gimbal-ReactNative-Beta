//
//  BeaconManagerModule.m
//  RNGimbal
//
//  Created by Andrew Tran on 3/30/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

#import "BeaconManagerModule.h"
#import <React/RCTEventEmitter.h>
#import "BeaconHelper.h"

@implementation BeaconManagerModule {
    GMBLBeaconManager *beaconManager;
    bool hasListeners;
}

RCT_EXPORT_MODULE(BeaconManager)

#pragma mark - String Constants
static NSString *EVENT_NAME_BEACON_SIGHTING = @"BeaconSightingFromBeaconManager";
static NSString *BEACON_SIGHTING_KEY = @"BEACON_SIGHTING";

-(NSDictionary *)constantsToExport
{
    NSDictionary *constants = @{
        BeaconHelper.EVENT_MAP_KEY_BATTERY_LEVEL : BeaconHelper.EVENT_MAP_KEY_BATTERY_LEVEL,
        BeaconHelper.EVENT_MAP_KEY_BEACON : BeaconHelper.EVENT_MAP_KEY_BEACON,
        BeaconHelper.EVENT_MAP_KEY_ICON_URL : BeaconHelper.EVENT_MAP_KEY_ICON_URL,
        BeaconHelper.EVENT_MAP_KEY_IDENTIFIER : BeaconHelper.EVENT_MAP_KEY_IDENTIFIER,
        BeaconHelper.EVENT_MAP_KEY_NAME : BeaconHelper.EVENT_MAP_KEY_NAME,
        BeaconHelper.EVENT_MAP_KEY_RSSI : BeaconHelper.EVENT_MAP_KEY_RSSI,
        BeaconHelper.EVENT_MAP_KEY_TEMPERATURE : BeaconHelper.EVENT_MAP_KEY_TEMPERATURE,
        BeaconHelper.EVENT_MAP_KEY_TIME : BeaconHelper.EVENT_MAP_KEY_TIME,
        BeaconHelper.EVENT_MAP_KEY_UUID : BeaconHelper.EVENT_MAP_KEY_UUID,
        BEACON_SIGHTING_KEY : EVENT_NAME_BEACON_SIGHTING
    };
    
    return constants;
}

#pragma mark - Init
-(instancetype) init
{
    self = [super init];
    if (self) {
        beaconManager = [GMBLBeaconManager new];
        beaconManager.delegate = self;
    }
    return self;
}

-(instancetype) initWithBeaconManager:(GMBLBeaconManager *)manager
{
    self = [super init];
    if (self) {
        beaconManager = manager;
        manager.delegate = self;
    }
    
    return self;
}

#pragma mark - Export Methods
RCT_EXPORT_METHOD(startListening)
{
    hasListeners = YES;
    [beaconManager startListening];
}

RCT_EXPORT_METHOD(stopListening)
{
    [beaconManager stopListening];
    hasListeners = NO;
}

#pragma mark - RN Overrides
- (NSArray<NSString *> *)supportedEvents // necessary to emit events
{
    return @[
        EVENT_NAME_BEACON_SIGHTING,
    ];
}

+ (BOOL)requiresMainQueueSetup // necessary for constantsToExport
{
  return YES;
}

#pragma mark - Delegate Methods
- (void)beaconManager:(GMBLBeaconManager *)manager didReceiveBeaconSighting:(GMBLBeaconSighting *)sighting
{
    NSDictionary *sightingMap = [BeaconHelper toMapFromSighting:sighting];
    [self checkListenersAndSendEventWithName:EVENT_NAME_BEACON_SIGHTING body:sightingMap];
}

#pragma mark - Utility Methods
- (void)checkListenersAndSendEventWithName:(NSString *)name body:(NSDictionary *)body
{
    if (hasListeners) {
        [self sendEventWithName:name body:body];
    }
}

@end
