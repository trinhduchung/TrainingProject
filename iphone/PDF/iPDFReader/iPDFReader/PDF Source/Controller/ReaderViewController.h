//
//  ReaderViewController.h
//  iPDFReader
//
//  Created by Cuong Tran on 2/11/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ReaderContentView.h"
#import "TextDisplayViewController.h"
@class ReaderDocument;
@class ReaderViewController;

@protocol ReaderViewControllerDelegate <NSObject>

- (void)dismissReaderViewController:(ReaderViewController *)viewController;

@end

@interface ReaderViewController : UIViewController <UIScrollViewDelegate, UIGestureRecognizerDelegate, ReaderContentViewDelegate, TextDisplayViewControllerDelegate>{
    NSMutableDictionary *_contentViews;
    ReaderDocument      *_document;
    NSInteger           _currentPage;
    UIScrollView        *_theScrollView;
    CGSize              _lastAppearSize;
    NSDate              *_lastHideTime;
    BOOL                _isVisible;
    
    BOOL                _waitForTextInput;
}
@property (nonatomic, assign, readwrite) id<ReaderViewControllerDelegate> delegate;
- (id) initWithReaderDocument:(ReaderDocument *) document;
@end
