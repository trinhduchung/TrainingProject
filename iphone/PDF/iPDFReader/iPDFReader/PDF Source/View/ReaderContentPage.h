//
//  ReaderContentPage.h
//  iPDFReader
//
//  Created by Cuong Tran on 2/11/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ReaderContentPage : UIView {
    NSMutableArray          *_links;
    CGPDFDocumentRef        _PDFDocRef;
    CGPDFPageRef            _PDFPageRef;
    
    NSInteger               _pageAngle;
    CGSize                  _pageSize;
}

- (id) initWithURL:(NSURL *) fileURL page:(NSInteger) page password:(NSString *) phrase;
- (id) singleTap:(UITapGestureRecognizer *) recognizer;

@end

#pragma mark -
//
// ReaderDocumentLink class interface
//

@interface ReaderDocumentLink : NSObject {
@private
    CGPDFDictionaryRef      _dictionary;
    CGRect                  _rect;
}
@property (nonatomic, assign, readonly) CGRect rect;

@property (nonatomic, assign, readonly) CGPDFDictionaryRef dictionary;

+ (id)withRect:(CGRect)linkRect dictionary:(CGPDFDictionaryRef)linkDictionary;

- (id)initWithRect:(CGRect)linkRect dictionary:(CGPDFDictionaryRef)linkDictionary;
@end