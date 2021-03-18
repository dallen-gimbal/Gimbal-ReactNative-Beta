//
//  LocationHelper.m
//  RNGimbal
//
//  Created by Andrew Tran on 4/23/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LocationHelper.h"

@implementation LocationHelper

+ (NSString *) LOCATION_MAP_KEY_SCORE {
    return @"score";
}
+ (NSString *) LOCATION_MAP_KEY_BOUNDARY_RADIUS {
    return @"boundaryRadius";
}
+ (NSString *) LOCATION_MAP_KEY_BOUNDARY_CENTER_LATITUDE {
    return @"boundaryCenterLatitude";
}
+ (NSString *) LOCATION_MAP_KEY_BOUNDARY_CENTER_LONGITUDE {
    return @"boundaryCenterLongitude";
}

+(NSMutableArray *)locationsToMaps:(NSArray *)establishedLocations
{
    NSMutableArray *mapArray = [NSMutableArray arrayWithCapacity:[establishedLocations count]];
    [establishedLocations enumerateObjectsUsingBlock:^(GMBLEstablishedLocation* establishedLocation, NSUInteger idx, BOOL *stop) {

        NSDictionary *map = @{
            [self LOCATION_MAP_KEY_SCORE] : @(establishedLocation.score),
            [self LOCATION_MAP_KEY_BOUNDARY_RADIUS] : @(establishedLocation.boundary.radius),
            [self LOCATION_MAP_KEY_BOUNDARY_CENTER_LATITUDE] : @(establishedLocation.boundary.center.latitude),
            [self LOCATION_MAP_KEY_BOUNDARY_CENTER_LONGITUDE] : @(establishedLocation.boundary.center.longitude)
        };
        
        [mapArray addObject:map];
    }];

    return mapArray;
}

@end
