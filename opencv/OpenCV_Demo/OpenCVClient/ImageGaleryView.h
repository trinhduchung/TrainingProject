//
//  ImageGaleryView.h
//  Bars
//
//  Created by Trinh Hung on 2/25/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol ImageGalleryViewDelegate <NSObject>

- (void) didArrowButtonClicked;

@end

@interface ImageGaleryView : UIView <UIScrollViewDelegate>{
    NSMutableArray              *_views;
    NSMutableArray              *_imageLinks;
    NSMutableArray              *_locations;
    
    UIScrollView                *_scrollView;
    CGRect                      _scrollFrame;
    
    UIButton                    *_leftArrow;
    UIButton                    *_rightArrow;
    
    int                         _currentItemIndex;
    int                         _totalItems;
    
    id                          _delegate;

}
@property (nonatomic, retain) UIScrollView *scrollView;
@property (nonatomic, assign) int totalItems;
@property (nonatomic, assign) id delegate;
- (id) init;
- (void) notifyDataSetChanged;
- (UIImage *) getCurrentImageDisplay;
- (void) slideShow;
@end
