//
//  GimbalDebuggerTests.m
//  RNGimbalTests
//
//  Created by Andrew Tran on 4/27/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

#import <XCTest/XCTest.h>
#import <Gimbal/Gimbal.h>
#import "GimbalDebuggerModule.h"
#import <OCMock/OCMock.h>

#import <React/RCTModuleMethod.h>

@interface GimbalDebuggerTests : XCTestCase

@property (nonatomic) GimbalDebuggerModule *module;
@property (nonatomic) id mockDebugger;

@end

@implementation GimbalDebuggerTests

- (void)setUp {
    self.mockDebugger = [OCMockObject niceMockForClass:[GMBLDebugger class]];
    self.module = [[GimbalDebuggerModule alloc] init];
}

- (void)tearDown {
    [self.mockDebugger stopMocking];
}

- (void)testEnableBeaconSightingsLoggingCalled {
    static RCTMethodInfo methodInfo = {
        .objcName = "enableBeaconSightingsLogging",
        .jsName = "",
        .isSync = false
    };
    RCTModuleMethod *method = [[RCTModuleMethod alloc] initWithExportedMethod:&methodInfo moduleClass:[GimbalDebuggerModule class]];
        XCTAssertNotNil(method, "@RCTModuleMethod 'enableBeaconSightingsLogging' should have been created");
    
    [[self.mockDebugger expect] enableBeaconSightingsLogging];
    [method invokeWithBridge:nil module:self.module arguments:nil];
    [self.mockDebugger verify];
}

- (void)testDisableBeaconSightingsLoggingCalled {
    static RCTMethodInfo methodInfo = {
        .objcName = "disableBeaconSightingsLogging",
        .jsName = "",
        .isSync = false
    };
    RCTModuleMethod *method = [[RCTModuleMethod alloc] initWithExportedMethod:&methodInfo moduleClass:[GimbalDebuggerModule class]];
        XCTAssertNotNil(method, "@RCTModuleMethod 'disableBeaconSightingsLogging' should have been created");
    
    [[self.mockDebugger expect] disableBeaconSightingsLogging];
    [method invokeWithBridge:nil module:self.module arguments:nil];
    [self.mockDebugger verify];
}

- (void)testIsBeaconSightingsLoggingCalled {
    static RCTMethodInfo methodInfo = {
        .objcName = "isBeaconSightingsLoggingEnabledWithResolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject",
        .jsName = "isBeaconSightingsLoggingEnabled",
        .isSync = false
    };
    RCTModuleMethod *method = [[RCTModuleMethod alloc] initWithExportedMethod:&methodInfo moduleClass:[GimbalDebuggerModule class]];
        XCTAssertNotNil(method, "@RCTModuleMethod 'isBeaconSightingsLoggingEnabled' should have been created");
    
    [[self.mockDebugger expect] isBeaconSightingsEnabled];
    NSArray *mockArguments = @[@(1), @(2)];
    [method invokeWithBridge:nil module:self.module arguments:mockArguments];
    [self.mockDebugger verify];
}

- (void)testEnableDebugLoggingCalled {
    static RCTMethodInfo methodInfo = {
        .objcName = "enableDebugLogging",
        .jsName = "",
        .isSync = false
    };
    RCTModuleMethod *method = [[RCTModuleMethod alloc] initWithExportedMethod:&methodInfo moduleClass:[GimbalDebuggerModule class]];
        XCTAssertNotNil(method, "@RCTModuleMethod 'enableDebugLogging' should have been created");
    
    [[self.mockDebugger expect] enableDebugLogging];
    [method invokeWithBridge:nil module:self.module arguments:nil];
    [self.mockDebugger verify];
}

- (void)testDisableDebugLoggingCalled {
    static RCTMethodInfo methodInfo = {
        .objcName = "disableDebugLogging",
        .jsName = "",
        .isSync = false
    };
    RCTModuleMethod *method = [[RCTModuleMethod alloc] initWithExportedMethod:&methodInfo moduleClass:[GimbalDebuggerModule class]];
        XCTAssertNotNil(method, "@RCTModuleMethod 'disableDebugLogging' should have been created");
    
    [[self.mockDebugger expect] disableDebugLogging];
    [method invokeWithBridge:nil module:self.module arguments:nil];
    [self.mockDebugger verify];
}

- (void)testIsDebugLoggingEnabledCalled {
    static RCTMethodInfo methodInfo = {
        .objcName = "isDebugLoggingEnabledWithResolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject",
        .jsName = "isDebugLoggingEnabled",
        .isSync = false
    };
    RCTModuleMethod *method = [[RCTModuleMethod alloc] initWithExportedMethod:&methodInfo moduleClass:[GimbalDebuggerModule class]];
        XCTAssertNotNil(method, "@RCTModuleMethod 'isDebugLoggingEnabled' should have been created");
    
    [[self.mockDebugger expect] isDebugEnabled];
    NSArray *mockArguments = @[@(1), @(2)];
    [method invokeWithBridge:nil module:self.module arguments:mockArguments];
    [self.mockDebugger verify];
}

- (void)testEnablePlaceLoggingCalled {
    static RCTMethodInfo methodInfo = {
        .objcName = "enablePlaceLogging",
        .jsName = "",
        .isSync = false
    };
    RCTModuleMethod *method = [[RCTModuleMethod alloc] initWithExportedMethod:&methodInfo moduleClass:[GimbalDebuggerModule class]];
        XCTAssertNotNil(method, "@RCTModuleMethod 'enablePlaceLogging' should have been created");
    
    [[self.mockDebugger expect] enablePlaceLogging];
    [method invokeWithBridge:nil module:self.module arguments:nil];
    [self.mockDebugger verify];
}

- (void)testDisablePlaceLoggingCalled {
    static RCTMethodInfo methodInfo = {
        .objcName = "disablePlaceLogging",
        .jsName = "",
        .isSync = false
    };
    RCTModuleMethod *method = [[RCTModuleMethod alloc] initWithExportedMethod:&methodInfo moduleClass:[GimbalDebuggerModule class]];
        XCTAssertNotNil(method, "@RCTModuleMethod 'disablePlaceLogging' should have been created");
    
    [[self.mockDebugger expect] disablePlaceLogging];
    [method invokeWithBridge:nil module:self.module arguments:nil];
    [self.mockDebugger verify];
}

- (void)testIsPlaceLoggingEnabledCalled {
    static RCTMethodInfo methodInfo = {
        .objcName = "isPlaceLoggingEnabledWithResolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject",
        .jsName = "isPlaceLoggingEnabled",
        .isSync = false
    };
    RCTModuleMethod *method = [[RCTModuleMethod alloc] initWithExportedMethod:&methodInfo moduleClass:[GimbalDebuggerModule class]];
        XCTAssertNotNil(method, "@RCTModuleMethod 'isPlaceLoggingEnabled' should have been created");
    
    [[self.mockDebugger expect] isPlaceLoggingEnabled];
    NSArray *mockArguments = @[@(1), @(2)];
    [method invokeWithBridge:nil module:self.module arguments:mockArguments];
    [self.mockDebugger verify];
}

@end
