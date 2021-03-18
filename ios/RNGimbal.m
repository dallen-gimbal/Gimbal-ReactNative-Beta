#import "RNGimbal.h"


@implementation RNGimbal

RCT_EXPORT_MODULE()

RCT_REMAP_METHOD(isStarted, isStartedWithResolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        resolve([NSNumber numberWithBool:[Gimbal isStarted]]);
    });
}

RCT_EXPORT_METHOD(start)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [Gimbal start];
    });
}

RCT_EXPORT_METHOD(stop)
{
    [Gimbal stop];
}

RCT_REMAP_METHOD(getApplicationInstanceIdentifier, getApplicationInstanceIdentifierWithResolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        resolve(@[[NSString stringWithFormat: @"%@", [Gimbal applicationInstanceIdentifier]]]);
    });
}

RCT_EXPORT_METHOD(resetApplicationInstanceIdentifier)
{
    [Gimbal resetApplicationInstanceIdentifier];
}

@end
