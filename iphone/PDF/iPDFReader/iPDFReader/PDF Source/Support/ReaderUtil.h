//
//  ReaderUtil.h
//  iPDFReader
//
//  Created by Cuong Tran on 2/11/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ReaderUtil : NSObject {
    
}
+ (ReaderUtil *) sharedInstance;
+ (BOOL)isPDF:(NSString *)filePath;
+ (NSString *)archiveFilePath:(NSString *)filename;
+ (NSString *)relativeFilePath:(NSString *)fullFilePath;
+ (NSString *)applicationSupportPath;
+ (NSString *)applicationPath;
+ (NSString *)documentsPath;
+ (NSString *)GUID;
@end
