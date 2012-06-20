//
//  Utils.h
//  OpenCVClient
//
//  Created by Trinh Hung on 6/20/12.
//  Copyright (c) 2012 Aptogo Limited. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Utils : NSObject
+ (NSString*)documentsPath;
+ (NSString *) getImageCardObjectDir;
+ (void) createDirectoryAtPath:(NSString *) path;
@end
