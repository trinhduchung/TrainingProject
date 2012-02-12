//
//  ReaderUtil.m
//  iPDFReader
//
//  Created by Cuong Tran on 2/11/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "ReaderUtil.h"
#import "CGPDFDocument.h"
#import <fcntl.h>
@implementation ReaderUtil

+ (ReaderUtil *) sharedInstance {
    static dispatch_once_t predicate = 0;
    static ReaderUtil *object = nil;
    dispatch_once(&predicate, ^{object = [self new];});
    return object;
}

+ (NSString *)GUID {
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	CFUUIDRef theUUID;
	CFStringRef theString;
    
	theUUID = CFUUIDCreate(NULL);
    
	theString = CFUUIDCreateString(NULL, theUUID);
    
	NSString *unique = [NSString stringWithString:(id)theString];
    
	CFRelease(theString); CFRelease(theUUID); // Cleanup
    
	return unique;
}

+ (NSString *)documentsPath {
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	NSArray *documentsPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    
	return [documentsPaths objectAtIndex:0]; // Path to the application's "~/Documents" directory
}

+ (NSString *)applicationPath {
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	NSArray *documentsPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    
	return [[documentsPaths objectAtIndex:0] stringByDeletingLastPathComponent]; // Strip "Documents" component
}

+ (NSString *)applicationSupportPath {
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	NSFileManager *fileManager = [[NSFileManager new] autorelease]; // File manager instance
    
	NSURL *pathURL = [fileManager URLForDirectory:NSApplicationSupportDirectory inDomain:NSUserDomainMask appropriateForURL:nil create:YES error:NULL];
    
	return [pathURL path]; // Path to the application's "~/Library/Application Support" directory
}

+ (NSString *)relativeFilePath:(NSString *)fullFilePath {
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	assert(fullFilePath != nil); // Ensure that the full file path is not nil
    
	NSString *applicationPath = [ReaderUtil applicationPath]; // Get the application path
    
	NSRange range = [fullFilePath rangeOfString:applicationPath]; // Look for the application path
    
	assert(range.location != NSNotFound); // Ensure that the application path is in the full file path
    
	return [fullFilePath stringByReplacingCharactersInRange:range withString:@""]; // Strip it out
}

+ (NSString *)archiveFilePath:(NSString *)filename {
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	assert(filename != nil); // Ensure that the archive file name is not nil
    
	//NSString *archivePath = [ReaderDocument documentsPath]; // Application's "~/Documents" path
    
	NSString *archivePath = [ReaderUtil applicationSupportPath]; // Application's "~/Library/Application Support" path
    
	NSString *archiveName = [[filename stringByDeletingPathExtension] stringByAppendingPathExtension:@"plist"];
    
	return [archivePath stringByAppendingPathComponent:archiveName]; // "{archivePath}/'filename'.plist"
}

+ (BOOL)isPDF:(NSString *)filePath {
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	BOOL state = NO;
    
	if (filePath != nil) // Must have a file path
	{
		const char *path = [filePath fileSystemRepresentation];
        
		int fd = open(path, O_RDONLY); // Open the file
        
		if (fd > 0) // We have a valid file descriptor
		{
			const unsigned char sig[4]; // File signature
            
			ssize_t len = read(fd, (void *)&sig, sizeof(sig));
            
			if (len == 4)
				if (sig[0] == '%')
					if (sig[1] == 'P')
						if (sig[2] == 'D')
							if (sig[3] == 'F')
								state = YES;
            
			close(fd); // Close the file
		}
	}
    
	return state;
}


@end
