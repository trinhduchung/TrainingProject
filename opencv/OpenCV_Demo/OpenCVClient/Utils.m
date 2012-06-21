//
//  Utils.m
//  OpenCVClient
//
//  Created by Trinh Hung on 6/20/12.
//  Copyright (c) 2012 Aptogo Limited. All rights reserved.
//

#import "Utils.h"

@implementation Utils
+ (NSString*)documentsPath
{
    return [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) lastObject];
}

+ (void) createDirectoryAtPath:(NSString *)path {
    NSFileManager *fm = [NSFileManager defaultManager];
    NSError *error;
    [fm createDirectoryAtPath:path withIntermediateDirectories:YES attributes:nil error:&error];
}

+ (NSString *) getImageCardObjectDir {
    NSString *dir = [Utils documentsPath];
//    NSString *imgDir = [dir stringByAppendingFormat:@"/%@",@"card_object"];
    NSFileManager *fm = [NSFileManager defaultManager];
    if (![fm fileExistsAtPath:dir]) {
        [Utils createDirectoryAtPath:dir];
    }
    return dir;
}
@end
