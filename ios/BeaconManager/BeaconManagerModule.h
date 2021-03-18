//
//  BeaconManagerModule.h
//  RNGimbal
//
//  Created by Andrew Tran on 3/30/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

#import <Gimbal/Gimbal.h>
#import <React/RCTEventEmitter.h>


@interface BeaconManagerModule : RCTEventEmitter <RCTBridgeModule, GMBLBeaconManagerDelegate>

-(instancetype) initWithBeaconManager:(GMBLBeaconManager *)manager;

@end
