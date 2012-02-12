//
//  ReaderContentView.h
//  iPDFReader
//
//  Created by Cuong Tran on 2/11/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
@class ReaderContentView;
@class ReaderContentPage;
@protocol ReaderContentViewDelegate
- (void)contentView:(ReaderContentView *)contentView touchesBegan:(NSSet *)touches;
@end

@interface ReaderContentView : UIScrollView <UIScrollViewDelegate>{
    ReaderContentPage       *_theContentView;
    
    UIView                  *_theContainerView;
    CGFloat                 zoomAmount;
}
@property (nonatomic, assign, readwrite) id <ReaderContentViewDelegate> message;

- (id)initWithFrame:(CGRect)frame fileURL:(NSURL *)fileURL page:(NSUInteger)page password:(NSString *)phrase;

- (void)showPageThumb:(NSURL *)fileURL page:(NSInteger)page password:(NSString *)phrase guid:(NSString *)guid;

- (id)singleTap:(UITapGestureRecognizer *)recognizer;

- (void)zoomIncrement;
- (void)zoomDecrement;
- (void)zoomReset;
@end
