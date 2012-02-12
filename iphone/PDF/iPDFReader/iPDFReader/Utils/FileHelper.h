//
//  FileHelper.h
//  iPDFReader
//
//  Created by Cuong Tran on 2/11/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface FileHelper : NSObject {
    NSMutableArray *_pdfFiles;
}
@property (nonatomic, retain) NSMutableArray *pdfFiles;

+ (FileHelper *) sharedInstance;
+ (NSString *)bundlePath:(NSString *)fileName;
+ (NSString*)documentsPath;
+ (NSString *)documentsPathWithFileName:(NSString *)fileName;

- (BOOL)loadFilesToArrayWithType: (NSString*)type;
+ (BOOL)copyFileFrom:(NSString*)pathRes toDes:(NSString*)pathDes;

@end
