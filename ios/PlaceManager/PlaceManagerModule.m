//
//  PlaceManagerModule.m
//  RNGimbal
//
//  Created by Andrew Tran on 1/6/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

#import "PlaceManagerModule.h"
#import "BeaconHelper.h"
#import <React/RCTConvert.h>
#import <React/RCTEventEmitter.h>

@implementation PlaceManagerModule {
    GMBLPlaceManager *placeManager;
    bool hasListeners;
}

RCT_EXPORT_MODULE(PlaceManager);

#pragma mark - String Constants
static NSString *BATTERY_LEVEL_LOW = @"BATTERY_LEVEL_LOW";
static NSString *BATTERY_LEVEL_MED_LOW = @"BATTERY_LEVEL_MED_LOW";
static NSString *BATTERY_LEVEL_MED_HIGH = @"BATTERY_LEVEL_MED_HIGH";
static NSString *BATTERY_LEVEL_HIGH = @"BATTERY_LEVEL_HIGH";

static NSString *EVENT_MAP_KEY_DELAY = @"delay";
static NSString *EVENT_MAP_KEY_PLACE = @"place";
static NSString *EVENT_MAP_KEY_VISIT_ID = @"visitId";
static NSString *EVENT_MAP_KEY_ARRIVAL_TIME = @"arrivalTimeInMillis";
static NSString *EVENT_MAP_KEY_DEPARTURE_TIME = @"departureTimeInMillis";
static NSString *EVENT_MAP_KEY_DWELL_TIME = @"dwellTimeInMillis";
static NSString *EVENT_MAP_KEY_BEACON_SIGHTING = @"beaconSighting";
static NSString *EVENT_MAP_KEY_VISITS = @"visits";
static NSString *EVENT_MAP_KEY_LATITUDE = @"latitude";
static NSString *EVENT_MAP_KEY_LONGITUDE = @"longitude";
static NSString *EVENT_MAP_KEY_ACCURACY = @"accuracy";
static NSString *EVENT_MAP_KEY_ATTRIBUTES = @"attributes";

static NSString *EVENT_NAME_VISIT_START = @"VisitStart";
static NSString *EVENT_NAME_VISIT_START_WITH_DELAY = @"VisitStartWithDelay";
static NSString *EVENT_NAME_VISIT_END = @"VisitEnd";
static NSString *EVENT_NAME_BEACON_SIGHTING = @"BeaconSightingFromPlaceManager";
static NSString *EVENT_NAME_LOCATION_DETECTED = @"LocationDetected";

static NSString *BEACON_SIGHTING_KEY = @"BEACON_SIGHTING";

-(NSDictionary *)constantsToExport
{
    NSDictionary *constants = @{
        BATTERY_LEVEL_LOW : BATTERY_LEVEL_LOW,
        BATTERY_LEVEL_MED_LOW : BATTERY_LEVEL_MED_LOW,
        BATTERY_LEVEL_MED_HIGH : BATTERY_LEVEL_MED_HIGH,
        BATTERY_LEVEL_HIGH : BATTERY_LEVEL_HIGH,
        EVENT_NAME_VISIT_START : EVENT_NAME_VISIT_START,
        EVENT_NAME_VISIT_START_WITH_DELAY : EVENT_NAME_VISIT_START_WITH_DELAY,
        EVENT_NAME_VISIT_END : EVENT_NAME_VISIT_END,
        BEACON_SIGHTING_KEY : EVENT_NAME_BEACON_SIGHTING,
        EVENT_NAME_LOCATION_DETECTED : EVENT_NAME_LOCATION_DETECTED
    };
    
    return constants;
}

#pragma mark - Init
-(instancetype) init
{
    self = [super init];
    if (self) {
        placeManager = [GMBLPlaceManager new];
        placeManager.delegate = self;
    }
    return self;
}

#pragma mark - Export Methods
RCT_EXPORT_METHOD(startMonitoring)
{
    [GMBLPlaceManager startMonitoring];
}

RCT_EXPORT_METHOD(stopMonitoring)
{
    [GMBLPlaceManager stopMonitoring];
}

RCT_REMAP_METHOD(isMonitoring,
                 isMonitoringWithResolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject)
{
    BOOL isMonitoring = [GMBLPlaceManager isMonitoring];
    resolve(@(isMonitoring));
}

#pragma mark - RN Overrides
- (NSArray<NSString *> *)supportedEvents
{
    return @[
        EVENT_NAME_VISIT_START,
        EVENT_NAME_VISIT_START_WITH_DELAY,
        EVENT_NAME_VISIT_END,
        EVENT_NAME_BEACON_SIGHTING,
        EVENT_NAME_LOCATION_DETECTED
    ];
}

+ (BOOL)requiresMainQueueSetup // necessary for constantsToExport
{
  return YES;
}

- (void)startObserving
{
    hasListeners = YES;
}

- (void)stopObserving
{
    hasListeners = NO;
}

#pragma mark - PlaceManagerDelegate Methods
- (void)placeManager:(GMBLPlaceManager *)manager didBeginVisit:(GMBLVisit *)visit
{
    NSDictionary *eventBody = [self toMapFromVisit:visit];
    [self checkListenersAndSendEventWithName:EVENT_NAME_VISIT_START body:eventBody];
}

- (void)placeManager:(GMBLPlaceManager *)manager didBeginVisit:(GMBLVisit *)visit withDelay:(NSTimeInterval)delayTime
{
    NSMutableDictionary *eventBody = [self toMapFromVisit:visit];
    [self safelyAddObject:[NSNumber numberWithDouble:delayTime] andKey:EVENT_MAP_KEY_DELAY toMap:&eventBody];
    [self checkListenersAndSendEventWithName:EVENT_NAME_VISIT_START_WITH_DELAY body: eventBody];
}

- (void)placeManager:(GMBLPlaceManager *)manager didReceiveBeaconSighting:(GMBLBeaconSighting *)sighting forVisits:(NSArray *)visits
{
    NSDictionary *eventBody = [self toMap:sighting withVisits:visits];
    [self checkListenersAndSendEventWithName:EVENT_NAME_BEACON_SIGHTING body:eventBody];
}

- (void)placeManager:(GMBLPlaceManager *)manager didEndVisit:(GMBLVisit *)visit
{
    NSDictionary *eventBody = [self toMapFromVisit:visit];
    [self checkListenersAndSendEventWithName:EVENT_NAME_VISIT_END body:eventBody];
}

- (void)placeManager:(GMBLPlaceManager *)manager didDetectLocation:(CLLocation *)location
{
    NSDictionary *eventBody = [self toMapFromLocation:location];
    [self checkListenersAndSendEventWithName:EVENT_NAME_LOCATION_DETECTED body:eventBody];
}

#pragma mark - Utility Methods
-(NSMutableDictionary *)toMapFromVisit:(GMBLVisit *)visit
{
    NSMutableDictionary *map = [NSMutableDictionary new];
    [self safelyAddObject:[self toMapFromPlace:visit.place] andKey:EVENT_MAP_KEY_PLACE toMap:&map];
    [self safelyAddObject:visit.visitID andKey:EVENT_MAP_KEY_VISIT_ID toMap:&map];
    [self safelyAddObject:@([visit.arrivalDate timeIntervalSinceReferenceDate]*1000) andKey:EVENT_MAP_KEY_ARRIVAL_TIME toMap:&map];
    [self safelyAddObject:@([visit.departureDate timeIntervalSinceReferenceDate]*1000) andKey:EVENT_MAP_KEY_DEPARTURE_TIME toMap:&map];
    [self safelyAddObject:@(visit.dwellTime) andKey:EVENT_MAP_KEY_DWELL_TIME toMap:&map];
    
    return map;
}

-(NSDictionary *)toMapFromPlace:(GMBLPlace *)place
{
    NSMutableDictionary *placeAttributesMap = [self toMapFromAttributes:place.attributes];
    
    NSDictionary *map = @{
        BeaconHelper.EVENT_MAP_KEY_NAME : place.name,
        BeaconHelper.EVENT_MAP_KEY_IDENTIFIER : place.identifier,
        EVENT_MAP_KEY_ATTRIBUTES : placeAttributesMap,
    };
    
    return map;
}

-(NSMutableDictionary *)toMapFromAttributes:(GMBLAttributes *)attributes
{
    NSMutableDictionary *map = [NSMutableDictionary new];
    NSArray *attributeKeys = attributes.allKeys;
    for (id key in attributeKeys) {
        [map setObject:[attributes stringForKey:key] forKey:key];
    }
    return map;
}

-(NSDictionary *)toMap:(GMBLBeaconSighting *)beaconSighting withVisits:(NSArray*)visits
{
    NSMutableArray *visitList = [NSMutableArray new];
    for (id visit in visits) {
        [visitList addObject:[self toMapFromVisit:visit]];
    }

    NSDictionary *sightingMap = [BeaconHelper toMapFromSighting:beaconSighting];
    NSDictionary *map = @{
        EVENT_MAP_KEY_BEACON_SIGHTING : sightingMap,
        EVENT_MAP_KEY_VISITS : visitList
    };
    
    return map;
}

-(NSDictionary *)toMapFromLocation:(CLLocation *)location
{
    NSDictionary *map = @{
        EVENT_MAP_KEY_LATITUDE : @(location.coordinate.latitude),
        EVENT_MAP_KEY_LONGITUDE : @(location.coordinate.longitude),
        EVENT_MAP_KEY_ACCURACY : @(location.horizontalAccuracy)
    };
    
    return map;
}

- (void)checkListenersAndSendEventWithName:(NSString *)name body:(NSDictionary *)body
{
    if (hasListeners) {
        [self sendEventWithName:name body:body];
    }
}

-(void)safelyAddObject:(id)object andKey:(NSString*)key toMap:(NSMutableDictionary**)map
{
    if (object == nil) {
        [*map setObject: [NSNull null] forKey:key];
    } else {
        [*map setObject: object forKey:key];
    }
}

@end
