//
//  PDFDocumentManager.h
//  iPDFReader
//
//  Created by Cuong Tran on 2/12/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
@class MFDocumentManager;
@interface PDFDocumentManager : NSObject {
    MFDocumentManager           *_documentManager;
}
@property (nonatomic, retain) MFDocumentManager *documentManager;

+ (PDFDocumentManager *) sharedInstance;
- (void) withDocumentManager:(MFDocumentManager *) documentManager;
@end
