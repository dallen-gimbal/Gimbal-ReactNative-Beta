//
//  LocationHelper.h
//  RNGimbal
//
//  Created by Andrew Tran on 4/23/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

#import <Gimbal/Gimbal.h>

@interface LocationHelper : NSObject

@property (class, readonly) NSString *LOCATION_MAP_KEY_SCORE;
@property (class, readonly) NSString *LOCATION_MAP_KEY_BOUNDARY_RADIUS;
@property (class, readonly) NSString *LOCATION_MAP_KEY_BOUNDARY_CENTER_LATITUDE;
@property (class, readonly) NSString *LOCATION_MAP_KEY_BOUNDARY_CENTER_LONGITUDE;

+(NSMutableArray *)locationsToMaps:(NSArray *)establishedLocations;

@end
