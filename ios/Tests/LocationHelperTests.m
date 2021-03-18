//
//  LocationHelperTests.m
//  RNGimbalTests
//
//  Created by Andrew Tran on 4/23/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

#import <XCTest/XCTest.h>
#import <Gimbal/Gimbal.h>
#import <OCMock/OCMock.h>
#import "LocationHelper.h"

@interface LocationHelperTests : XCTestCase

@end

@implementation LocationHelperTests

- (void)testLocationsToMapsTranslation {
    GMBLEstablishedLocation *mockLocation = [self createEstablishedLocation];
    NSArray *mockLocations = @[mockLocation];
    
    NSMutableArray *translatedLocations = [LocationHelper locationsToMaps:mockLocations];
    XCTAssertEqual([translatedLocations count], 1, @"Expected size of translatedLocations to equal size of establishedLocations (1)");
    
    NSDictionary *translatedLocation = [translatedLocations firstObject];
    double score = (double)[[translatedLocation valueForKey:@"score"] doubleValue];
    double radius = (double)[[translatedLocation valueForKey:@"boundaryRadius"] doubleValue];
    double latitude = (double)[[translatedLocation valueForKey:@"boundaryCenterLatitude"] doubleValue];
    double longitude = (double)[[translatedLocation valueForKey:@"boundaryCenterLongitude"] doubleValue];
    XCTAssertEqual(1.0, score, @"Expected score 1.0 from translatedEstablishedLocation");
    XCTAssertEqual(2.0, radius, @"Expected radius 2.0 from translatedEstablishedLocation");
    XCTAssertEqual(3.0, latitude, @"Expected latitude 1.0 from translatedEstablishedLocation");
    XCTAssertEqual(4.0, longitude, @"Expected longitude 1.0 from translatedEstablishedLocation");
}

- (void)testMapPropertyKeys {
    XCTAssertEqual([LocationHelper LOCATION_MAP_KEY_SCORE], @"score", @"Expected key 'score' to exist");
    XCTAssertEqual([LocationHelper LOCATION_MAP_KEY_BOUNDARY_RADIUS], @"boundaryRadius", @"Expected key 'boundaryRadius' to exist");
    XCTAssertEqual([LocationHelper LOCATION_MAP_KEY_BOUNDARY_CENTER_LATITUDE], @"boundaryCenterLatitude", @"Expected key 'boundaryCenterLatitude' to exist");
    XCTAssertEqual([LocationHelper LOCATION_MAP_KEY_BOUNDARY_CENTER_LONGITUDE], @"boundaryCenterLongitude", @"Expected key 'boundaryCenterLongitude' to exist");
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
