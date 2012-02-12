//
//  ReaderDocument.m
//  iPDFReader
//
//  Created by Cuong Tran on 2/11/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "ReaderDocument.h"
#import "ReaderUtil.h"
#import "CGPDFDocument.h"
@implementation ReaderDocument
#pragma mark Properties

@synthesize guid = _guid;
@synthesize fileDate = _fileDate;
@synthesize fileSize = _fileSize;
@synthesize pageCount = _pageCount;
@synthesize pageNumber = _pageNumber;
@synthesize bookmarks = _bookmarks;
@synthesize lastOpen = _lastOpen;
@synthesize password = _password;
@dynamic fileName, fileURL;

#pragma mark ReaderDocument class methods

+ (ReaderDocument *) unarchiveFromFileName:(NSString *)filename password:(NSString *)phrase {
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	ReaderDocument *document = nil; // ReaderDocument object
    
	NSString *withName = [filename lastPathComponent]; // File name only
    
	NSString *archiveFilePath = [ReaderUtil archiveFilePath:withName];
    
    // Unarchive an archived ReaderDocument object from its property list
    @try {
        document = [NSKeyedUnarchiver unarchiveObjectWithFile:archiveFilePath];
        
        if (document != nil && phrase != nil) {
            [document setValue:[[phrase copy] autorelease] forKey:@"password"];
        }
    }
    @catch (NSException *exception) {
        #ifdef DEBUG
            NSLog(@"%s Caught %@: %@", __FUNCTION__, [exception name], [exception reason]);
        #endif
    }
    @finally {
        
    }
    
    return document;
}

+ (ReaderDocument *) withDocumentFilePath:(NSString *)filename password:(NSString *)phrase {
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif

    ReaderDocument *document = nil;//Reader Document Object;
    document = [ReaderDocument unarchiveFromFileName:filename password:phrase];
    if (document == nil) {
        document = [[[ReaderDocument alloc] initWithFilePath:filename password:phrase] autorelease];
    }
    
    return document;
}

#pragma mark ReaderDocument instance methods

- (id)initWithFilePath:(NSString *)fullFilePath password:(NSString *)phrase
{
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    id object = nil; //Reader document object
    
    if ([ReaderUtil isPDF:fullFilePath] == YES) { //file must exist
        self = [super init];
        if (self) {
            _guid = [ReaderUtil GUID];
            _password = [phrase copy];
            _bookmarks = [NSMutableIndexSet new];
            _pageNumber = [[NSNumber numberWithInteger:1] retain];//start with page 1,retain for using.
            _fileName = [[ReaderUtil relativeFilePath:fullFilePath] retain];
            CFURLRef docURLRef = (CFURLRef) [self fileURL];//CFURLRef from NSURL
            CGPDFDocumentRef thePDFDocRef = CGPDFDocumentCreateX(docURLRef, _password);
            
            if(thePDFDocRef != NULL) { // Get the number of pages in a document
                NSInteger pageCount = CGPDFDocumentGetNumberOfPages(thePDFDocRef);
                _pageCount = [[NSNumber numberWithInteger:pageCount] retain];
                CGPDFDocumentRelease(thePDFDocRef);//clearup
            } else {
                NSAssert(NO, @"CGPDFDocumentRef == NULL");
            }
            
            NSFileManager *fileManager = [NSFileManager new];//File Manager
            _lastOpen = [[NSDate dateWithTimeIntervalSinceReferenceDate:0.0] retain];
            
            NSDictionary *fileAttributes = [fileManager attributesOfItemAtPath:fullFilePath error:NULL];
            _fileDate = [[fileAttributes objectForKey:NSFileModificationDate] retain];//file date
            _fileSize = [fileAttributes objectForKey:NSFileSize];
            [fileManager release];
            [self saveReaderDocument];//save
            
            object = self;
        }
    }
    return object;
}

- (void)dealloc
{
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	[_guid release], _guid = nil;
    
	[_fileURL release], _fileURL = nil;
    
	[_password release], _password = nil;
    
	[_fileName release], _fileName = nil;
    
	[_pageCount release], _pageCount = nil;
    
	[_pageNumber release], _pageNumber = nil;
    
	[_bookmarks release], _bookmarks = nil;
    
	[_fileSize release], _fileSize = nil;
    
	[_fileDate release], _fileDate = nil;
    
	[_lastOpen release], _lastOpen = nil;
    
	[super dealloc];
}

- (NSString *)fileName
{
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	return [_fileName lastPathComponent];
}

- (NSURL *)fileURL
{
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	if (_fileURL == nil) // Create and keep the file URL the first time it is requested
	{
		NSString *fullFilePath = [[ReaderUtil applicationPath] stringByAppendingPathComponent:_fileName];
        
		_fileURL = [[NSURL alloc] initFileURLWithPath:fullFilePath isDirectory:NO]; // File URL from full file path
	}
    
	return _fileURL;
}

- (BOOL)archiveWithFileName:(NSString *)filename
{
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	NSString *archiveFilePath = [ReaderUtil archiveFilePath:filename];
    
	return [NSKeyedArchiver archiveRootObject:self toFile:archiveFilePath];
}

- (void)saveReaderDocument
{
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	[self archiveWithFileName:[self fileName]];
}

- (void)updateProperties
{
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
}

#pragma mark NSCoding protocol methods

- (void) encodeWithCoder:(NSCoder *)encoder {
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    [encoder encodeObject:_guid forKey:@"FileGUID"];
    
	[encoder encodeObject:_fileName forKey:@"FileName"];
    
	[encoder encodeObject:_fileDate forKey:@"FileDate"];
    
	[encoder encodeObject:_pageCount forKey:@"PageCount"];
    
	[encoder encodeObject:_pageNumber forKey:@"PageNumber"];
    
	[encoder encodeObject:_bookmarks forKey:@"Bookmarks"];
    
	[encoder encodeObject:_fileSize forKey:@"FileSize"];
    
	[encoder encodeObject:_lastOpen forKey:@"LastOpen"];
}

- (id) initWithCoder:(NSCoder *)decoder {
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	if ((self = [super init])) // Superclass init
	{
		_guid = [[decoder decodeObjectForKey:@"FileGUID"] retain];
        
		_fileName = [[decoder decodeObjectForKey:@"FileName"] retain];
        
		_fileDate = [[decoder decodeObjectForKey:@"FileDate"] retain];
        
		_pageCount = [[decoder decodeObjectForKey:@"PageCount"] retain];
        
		_pageNumber = [[decoder decodeObjectForKey:@"PageNumber"] retain];
        
		_bookmarks = [[decoder decodeObjectForKey:@"Bookmarks"] mutableCopy];
        
		_fileSize = [[decoder decodeObjectForKey:@"FileSize"] retain];
        
		_lastOpen = [[decoder decodeObjectForKey:@"LastOpen"] retain];
        
		if (_bookmarks == nil) _bookmarks = [NSMutableIndexSet new];
        
		if (_guid == nil) _guid = [[ReaderUtil GUID] retain];
	}
    
	return self;

}



@end
