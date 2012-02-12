//
//  PDFDocumentManager.m
//  iPDFReader
//
//  Created by Cuong Tran on 2/12/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "PDFDocumentManager.h"
#import "MFDocumentManager.h"

@implementation PDFDocumentManager
@synthesize documentManager = _documentManager;

+ (PDFDocumentManager *) sharedInstance {
    static dispatch_once_t predicate = 0;
    static PDFDocumentManager *object = nil;
    dispatch_once(&predicate, ^{
        object = [self new];
    });
    return object;
}

- (void) withDocumentManager:(MFDocumentManager *)documentManager {
    if (_documentManager) {
        [_documentManager release];
    }
    _documentManager = nil;
    self.documentManager = [documentManager retain];
}

- (void) dealloc {
    [_documentManager release];
    _documentManager = nil;
    [super dealloc];
}
@end
