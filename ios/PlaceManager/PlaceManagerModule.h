//
//  PlaceManagerModule.h
//  RNGimbal
//
//  Created by Andrew Tran on 1/6/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

#import <Gimbal/Gimbal.h>
#import <React/RCTEventEmitter.h>


@interface PlaceManagerModule : RCTEventEmitter <RCTBridgeModule, GMBLPlaceManagerDelegate>
@end
