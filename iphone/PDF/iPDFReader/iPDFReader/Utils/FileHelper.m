//
//  FileHelper.m
//  iPDFReader
//
//  Created by Cuong Tran on 2/11/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "FileHelper.h"
#import "ReaderUtil.h"
@implementation FileHelper 

@synthesize pdfFiles = _pdfFiles;

static FileHelper* __sharedHelper = nil;

+ (FileHelper *) sharedInstance {
    static dispatch_once_t predicate = 0;
    static FileHelper *object = nil;
    dispatch_once(&predicate, ^{object = [self new];});
    return object;
}

+ (id)allocWithZone:(NSZone *)zone {
    @synchronized(self) {
        if (__sharedHelper == nil) {
            __sharedHelper = [super allocWithZone:zone];
            return __sharedHelper;
        }
    }
    return nil;
}

- (id)copyWithZone:(NSZone *)zone {
    return self;
}

- (id)retain {
    return self;
}

- (id)autorelease {
    return self;
}


- (void) dealloc {
    [_pdfFiles release];
    [super dealloc];
}

#pragma mark - ...
+ (NSString*)documentsPath
{
    return [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) lastObject];
}

+ (NSString *)bundlePath:(NSString *)fileName {
	return [[[NSBundle mainBundle] bundlePath] stringByAppendingPathComponent:fileName];
}

+ (NSString *)documentsPathWithFileName:(NSString *)fileName {
	NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
	NSString *documentsDirectory = [paths objectAtIndex:0];
	return [documentsDirectory stringByAppendingPathComponent:fileName];
}

+ (BOOL)copyFileFrom:(NSString*)pathRes toDes:(NSString*)pathDes
{
    NSFileManager *fileManager = [NSFileManager defaultManager];
    
    BOOL isExistFile = [fileManager fileExistsAtPath:pathDes];
    if(!isExistFile){
        NSError *error;
        BOOL success = [fileManager copyItemAtPath:pathRes toPath:pathDes error:&error];
        
        if(!success){
            NSAssert1(0, @"Failed to save file with message'%@'.", [error localizedDescription]);
            return TRUE;
        }else{
            NSLog(@"save success with path: %@", pathDes);
        }
    }
    
    return FALSE;
}

- (BOOL)loadFilesToArrayWithType: (NSString*)type
{
    if(self.pdfFiles) {
        [self.pdfFiles release];
        self.pdfFiles = nil;
    }
    NSError *error = nil;
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSArray *files = [fileManager contentsOfDirectoryAtPath:[FileHelper documentsPath] error:&error];
    
    if(!error) {
        self.pdfFiles = [[NSMutableArray alloc] init];
        for(NSString* file in files) {
            if ([ReaderUtil isPDF:[FileHelper documentsPathWithFileName:file]]) {
                [self.pdfFiles addObject:file]; 
            }
        }
        
        return TRUE;
    }
    
    return FALSE;
}
@end
