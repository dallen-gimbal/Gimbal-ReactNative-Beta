//
//  EstablishedLocationManagerTests.m
//  RNGimbalTests
//
//  Created by Andrew Tran on 4/20/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

#import <XCTest/XCTest.h>
#import <Gimbal/Gimbal.h>
#import <OCMock/OCMock.h>
#import <React/RCTModuleMethod.h>
#import <React/RCTBridgeModule.h>
#import "EstablishedLocationsManagerModule.h"
#import "LocationHelper.h"

@interface EstablishedLocationManagerTests : XCTestCase

@property (nonatomic) EstablishedLocationsManagerModule *module;
@property (nonatomic) id mockLocationHelper;
@property (nonatomic) id mockGMBLManager;

@end

@implementation EstablishedLocationManagerTests

- (void)setUp {
    self.mockGMBLManager = [OCMockObject niceMockForClass:[GMBLEstablishedLocationManager class]];
    self.mockLocationHelper = [OCMockObject niceMockForClass:[LocationHelper class]];
    self.module = [[EstablishedLocationsManagerModule alloc] init];
}

- (void)tearDown {
    [self.mockGMBLManager stopMocking];
    [self.mockLocationHelper stopMocking];
}

#pragma mark Tests
- (void)testStartMonitoringCalled {
    static RCTMethodInfo methodInfo = {
        .objcName = "startMonitoring",
        .jsName = "",
        .isSync = false
    };
    
    RCTModuleMethod *method = [[RCTModuleMethod alloc] initWithExportedMethod:&methodInfo moduleClass:[EstablishedLocationsManagerModule class]];
    
    XCTAssertNotNil(method, @"RCTModuleMethod 'startMonitoring' should have been created");
    
    [[self.mockGMBLManager expect] startMonitoring];
    [method invokeWithBridge:nil module:self.module arguments:nil];
    [self.mockGMBLManager verify];
}

- (void)testStopMonitoringCalled {
    static RCTMethodInfo methodInfo = {
        .objcName = "stopMonitoring",
        .jsName = "",
        .isSync = false
    };
    
    RCTModuleMethod *method = [[RCTModuleMethod alloc] initWithExportedMethod:&methodInfo moduleClass:[EstablishedLocationsManagerModule class]];
    
    XCTAssertNotNil(method, @"RCTModuleMethod 'stopMonitoring' should have been created");
    
    [[self.mockGMBLManager expect] stopMonitoring];
    [method invokeWithBridge:nil module:self.module arguments:nil];
    [self.mockGMBLManager verify];
}

- (void)testIsMonitoringCalled {
    static RCTMethodInfo methodInfo = {
        .objcName = "isMonitoringWithResolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject",
        .jsName = "isMonitoring",
        .isSync = NO
    };
    
    RCTModuleMethod *method = [[RCTModuleMethod alloc] initWithExportedMethod:&methodInfo moduleClass:[EstablishedLocationsManagerModule class]];
    
    XCTAssertNotNil(method, @"RCTModuleMethod 'isMonitoring' should have been created");
    
    [[self.mockGMBLManager expect] isMonitoring];
    
    /*
     It is unclear why NSNumbers must be passed as arguments here. It would be expected that,
     as a Promise-based exported method, 'isMonitoring' would take a RCTPromiseResolveBlock and
     RCTPromiseRejectBlock as parameters, but when arguments of these types are passed in,
     a typing error is detected in RCTModuleMethod.mm; there, a macro-defined method 'BLOCK_CASE'
     expects these arguments to be of type NSNumber. This is possible a bug in the React Native
     framework.
     **/
    NSArray *mockArguments = @[@(1), @(2)];
    [method invokeWithBridge:nil module:self.module arguments:mockArguments];
    [self.mockGMBLManager verify];
}

- (void)testLocationHelperCalledToTranslateMap {
    GMBLEstablishedLocation *mockLocation = [self createEstablishedLocation];
    NSArray *mockLocations = @[mockLocation];
    [[[self.mockGMBLManager stub] andReturn:mockLocations] establishedLocations];

    static RCTMethodInfo methodInfo = {
        .objcName = "establishedLocationsWithResolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)",
        .jsName = "getEstablishedLocations",
        .isSync = NO
    };
    RCTModuleMethod *method = [[RCTModuleMethod alloc] initWithExportedMethod:&methodInfo moduleClass:[EstablishedLocationsManagerModule class]];
    XCTAssertNotNil(method, @"RCTModuleMethod 'getEstablishedLocations' should have been created");
    
    [[self.mockLocationHelper expect] locationsToMaps:mockLocations];
    
    NSArray *mockArguments = @[@(1), @(2)];
    [method invokeWithBridge:nil module:self.module arguments:mockArguments];
    [self.mockLocationHelper verify];
}

#pragma mark Helper Functions
- (GMBLEstablishedLocation *)createEstablishedLocation {
    id mockLocation = [OCMockObject mockForClass:[GMBLEstablishedLocation class]];
    [(GMBLEstablishedLocation *)[[mockLocation stub] andReturnValue:OCMOCK_VALUE(1.0)] score];
    
    id mockBoundary = [OCMockObject mockForClass:[GMBLCircle class]];
    [(GMBLCircle *)[[mockBoundary stub] andReturnValue:OCMOCK_VALUE(2.0)] radius];
    [(GMBLEstablishedLocation *)[[mockLocation stub] andReturn:mockBoundary] boundary];
    
    CLLocationCoordinate2D mockCenter = { .latitude = 3.0, .longitude = 4.0 };
    [(GMBLCircle *)[[mockBoundary stub] andReturnValue:[NSValue value:&mockCenter withObjCType:@encode(CLLocationCoordinate2D)]] center];
    
    return mockLocation;
}

@end
