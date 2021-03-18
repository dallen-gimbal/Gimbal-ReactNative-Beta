#import "PrivacyManagerModule.h"
#import <React/RCTConvert.h>

@implementation PrivacyManagerModule

RCT_EXPORT_MODULE(PrivacyManager)

static NSString *GDPR_CONSENT_NOT_REQUIRED = @"GDPR_CONSENT_NOT_REQUIRED";
static NSString *GDPR_CONSENT_REQUIRED = @"GDPR_CONSENT_REQUIRED";
static NSString *GDPR_CONSENT_REQUIREMENT_UNKNOWN = @"GDPR_CONSENT_REQUIREMENT_UNKNOWN";
static NSString *GDPR_CONSENT_TYPE_PLACES = @"GDPR_CONSENT_TYPE_PLACES";
static NSString *GDPR_CONSENT_STATE_UNKNOWN = @"GDPR_CONSENT_STATE_UNKNOWN";
static NSString *GDPR_CONSENT_STATE_GRANTED = @"GDPR_CONSENT_STATE_GRANTED";
static NSString *GDPR_CONSENT_STATE_REFUSED = @"GDPR_CONSENT_STATE_REFUSED";

static NSInteger E_VALUE_UNKNOWN_STATE = -1;
static NSInteger E_VALUE_UNKNOWN_TYPE = -1;

- (NSDictionary *)constantsToExport
{
    NSDictionary *constants = @{
        GDPR_CONSENT_NOT_REQUIRED : [NSNumber numberWithInteger: GMBLGDPRConsentNotRequired],
        GDPR_CONSENT_REQUIRED : [NSNumber numberWithInteger: GMBLGDPRConsentRequired],
        GDPR_CONSENT_REQUIREMENT_UNKNOWN : [NSNumber numberWithInteger: GMBLGDPRConsentRequirementUnknown],
        GDPR_CONSENT_TYPE_PLACES : [NSNumber numberWithInteger: GMBLPlacesConsent],
        GDPR_CONSENT_STATE_UNKNOWN : [NSNumber numberWithInteger: GMBLConsentUnknown],
        GDPR_CONSENT_STATE_REFUSED : [NSNumber numberWithInteger: GMBLConsentRefused],
        GDPR_CONSENT_STATE_GRANTED : [NSNumber numberWithInteger: GMBLConsentGranted],
    };

    return constants;
}

+ (BOOL)requiresMainQueueSetup
{
  return YES;  // necessary because we're implementing constantsToExport
}

#pragma mark - Export Methods

RCT_REMAP_METHOD(getGdprConsentRequirement, getGdprConsentRequirementWithResolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    GDPRConsentRequirement consentRequirement = [GMBLPrivacyManager gdprConsentRequirement];
    resolve([NSNumber numberWithInteger:consentRequirement]);
}

RCT_EXPORT_METHOD(setUserConsent:(GMBLConsentType)consentType
                  toState:(GMBLConsentState)consentState)
{
    [GMBLPrivacyManager setUserConsentFor:consentType toState:consentState];
}

RCT_EXPORT_METHOD(getUserConsent:(GMBLConsentType)consentType
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    resolve([NSNumber numberWithInteger:[GMBLPrivacyManager userConsentFor:consentType]]);
}

@end

#pragma mark - ENUM Converters

@implementation RCTConvert(GMBLConsentType)
     RCT_ENUM_CONVERTER(GMBLConsentType,
                        (@{ GDPR_CONSENT_TYPE_PLACES : @(GMBLPlacesConsent)}),
                        E_VALUE_UNKNOWN_TYPE,
                        integerValue)
@end

@implementation RCTConvert(GMBLConsentState)
     RCT_ENUM_CONVERTER(GMBLConsentState,
                        (@{
                            GDPR_CONSENT_STATE_UNKNOWN : @(GMBLConsentUnknown),
                            GDPR_CONSENT_STATE_REFUSED : @(GMBLConsentRefused),
                            GDPR_CONSENT_STATE_GRANTED : @(GMBLConsentGranted),
                        }),
                        E_VALUE_UNKNOWN_STATE,
                        integerValue)
@end
