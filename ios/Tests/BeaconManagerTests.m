//
//  BeaconManagerTests.m
//  BeaconManagerTests
//
//  Created by Andrew Tran on 4/3/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

#import <XCTest/XCTest.h>
#import <Gimbal/Gimbal.h>
#import "BeaconManagerModule.h"
#import <OCMock/OCMock.h>

#import <React/RCTModuleMethod.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTLog.h>
#import <React/RCTRootView.h>
#import <React/RCTBridge.h>

@interface BeaconManagerTests : XCTestCase

@property (nonatomic) BeaconManagerModule *module;
@property (nonatomic) id mockManager;
@property (nonatomic) NSDate* now;

@end

@implementation BeaconManagerTests

- (void)setUp {
    self.now = [NSDate date];
    self.mockManager = [OCMockObject niceMockForClass:[GMBLBeaconManager class]];
    self.module = [[BeaconManagerModule alloc] initWithBeaconManager:self.mockManager];
}

- (void)tearDown {
    [self.mockManager stopMocking];
}

- (void)testConstantsExportCorrectly {
    NSDictionary *exportedConstants = [self.module constantsToExport];
    XCTAssertEqual(exportedConstants[@"BEACON_SIGHTING"],
                   @"BeaconSightingFromBeaconManager",
                   "BeaconManagerModule should export the correct beacon sighting event name.");
}

- (void)testStartListeningCalled {
    static RCTMethodInfo methodInfo = {
        .objcName = "startListening",
        .jsName = "",
        .isSync = false
    };
    
    RCTModuleMethod *method = [[RCTModuleMethod alloc] initWithExportedMethod:&methodInfo
                                                       moduleClass:[BeaconManagerModule class]];
    XCTAssertNotNil(method, "@RCTModuleMethod 'startListening' should have been created");
    
    [[self.mockManager expect] startListening];
    [method invokeWithBridge:nil module:self.module arguments:nil];
    [self.mockManager verify];
}

- (void)testStopListeningCalled {
    static RCTMethodInfo methodInfo = {
        .objcName = "stopListening",
        .jsName = "",
        .isSync = false
    };
    RCTModuleMethod *method = [[RCTModuleMethod alloc] initWithExportedMethod:&methodInfo
                                                       moduleClass:[BeaconManagerModule class]];
    
    XCTAssertNotNil(method, "@RCTModuleMethod 'stopListening' should have been created");
    
    [[self.mockManager expect] stopListening];
    [method invokeWithBridge:nil module:self.module arguments:nil];
    [self.mockManager verify];
}

- (void)testSightingEmitsSightingAsMap {
    id mockModule = [OCMockObject partialMockForObject:self.module];
    
    static RCTMethodInfo methodInfo = {
        .objcName = "startListening",
        .jsName = "",
        .isSync = false
    };
    RCTModuleMethod *method = [[RCTModuleMethod alloc] initWithExportedMethod:&methodInfo
                                                       moduleClass:[BeaconManagerModule class]];
    [method invokeWithBridge:nil module:mockModule arguments:nil];
    
    [[mockModule expect] sendEventWithName:[OCMArg checkWithBlock:^BOOL(NSString* emittedEventName){
        NSString* expectedEventName = @"BeaconSightingFromBeaconManager";
        
        return [emittedEventName isEqualToString:expectedEventName];
    }] body:[OCMArg checkWithBlock:^BOOL(NSDictionary* emittedEventBody){

        int sightingRssi = (int)[emittedEventBody[@"rssi"] integerValue];
        XCTAssertEqual(sightingRssi, -50, @"Emitted event body did not contain the correct RSSI");
        NSTimeInterval sightingDate = [[emittedEventBody objectForKey:@"timeInMillis"] integerValue];
        NSTimeInterval expectedSightingDate = floor([self.now timeIntervalSinceReferenceDate] * 1000);
        XCTAssertEqual((double)sightingDate, expectedSightingDate);
        NSMutableDictionary* sightingBeacon = emittedEventBody[@"beacon"];
        XCTAssertNotNil(sightingBeacon, @"Emitted event body did not contain a beacon");
        int beaconBatteryLevel = (int) [emittedEventBody[@"batteryLevel"] integerValue];
        XCTAssertEqual(beaconBatteryLevel, GMBLBatteryLevelLow, @"Emitted event body had an incorrect battery level");
        
        XCTAssertEqual(sightingBeacon[@"iconURL"], @"ICON_URL", @"Emitted event body had an incorrect icon url");
        XCTAssertEqual(sightingBeacon[@"identifier"], @"BEACON_ID", @"Emitted event body had an incorrect beacon id");
        XCTAssertEqual(sightingBeacon[@"name"], @"BEACON_NAME", @"Emitted event body had an incorrect beacon name");
        int beaconTemperature = (int)[sightingBeacon[@"temperature"] integerValue];
        XCTAssertEqual(beaconTemperature, 71, @"Emitted event body had an incorrect temperature");
        XCTAssertEqual(sightingBeacon[@"uuid"], @"UUID", @"Emitted event body had an incorrect uuid");
        
        return YES;
    }]];
    
    [mockModule beaconManager:self.mockManager didReceiveBeaconSighting:[self createSighting]];
    
    [mockModule verify];
}

- (id)createSighting {
    id mockBeacon = [self createBeacon];
    id mockSighting = [OCMockObject mockForClass:[GMBLBeaconSighting class]];
    [[[mockSighting stub] andReturnValue:OCMOCK_VALUE(-50)] RSSI];
    [[[mockSighting stub] andReturnValue:OCMOCK_VALUE(self.now)] date];
    [[[mockSighting stub] andReturn:mockBeacon] beacon];
    
    return mockSighting;
}

- (id)createBeacon {
    id mockBeacon = [OCMockObject mockForClass:[GMBLBeacon class]];
    [(GMBLBeacon *)[[mockBeacon stub] andReturnValue:OCMOCK_VALUE(GMBLBatteryLevelLow)] batteryLevel];
    [(GMBLBeacon *)[[mockBeacon stub] andReturnValue:OCMOCK_VALUE(@"ICON_URL")] iconURL];
    [(GMBLBeacon *)[[mockBeacon stub] andReturnValue:OCMOCK_VALUE(@"BEACON_ID")] identifier];
    [(GMBLBeacon *)[[mockBeacon stub] andReturnValue:OCMOCK_VALUE(@"BEACON_NAME")] name];
    [(GMBLBeacon *)[[mockBeacon stub] andReturnValue:OCMOCK_VALUE(71)] temperature];
    [(GMBLBeacon *)[[mockBeacon stub] andReturnValue:OCMOCK_VALUE(@"UUID")] uuid];
    
    return mockBeacon;
}

@end
