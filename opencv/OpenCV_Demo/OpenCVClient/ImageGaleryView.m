//
//  ImageGaleryView.m
//  Bars
//
//  Created by Cuong Tran on 2/25/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "ImageGaleryView.h"
#import "Utils.h"

#define kItemPerPage 1
#define PAGE_WIDTH  200
#define PAGE_HEIGH  188
#define BORDER 10
@interface ImageGaleryView (private)
- (void) generateScroll;
- (void) updateArrowButton;
- (void) scrollToPage:(int) pageIndex;
- (void) getAllCardObjectImage;
- (void) updateImageVisible;
@end

@implementation ImageGaleryView 
@synthesize totalItems = _totalItems;
@synthesize delegate = _delegate;
@synthesize scrollView = _scrollView;
@synthesize mapCard;

- (id) initWithCoder:(NSCoder *)aDecoder {
    self = [super initWithCoder:aDecoder];
    if (self) {
        CGRect frame = CGRectMake(0, 94, 320, 200);
        self.backgroundColor = [UIColor clearColor];
        
        _views = [[NSMutableArray alloc] init];
        if(_imageLinks){
            [_imageLinks release];
            _imageLinks = nil;
        }
        _imageLinks = [[NSMutableArray alloc] init];
        _locations = [[NSMutableArray alloc] init];
        
        _scrollFrame = CGRectMake((frame.size.width - PAGE_WIDTH) / 2, BORDER + BORDER + 1, PAGE_WIDTH, PAGE_HEIGH);
        
        UIImageView *bg = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"image_border.png"]];
        bg.frame = CGRectMake((frame.size.width - 247) / 2 , 0, 248, 233);
//        [self addSubview:bg];
        [bg release];
        
        _scrollView = [[UIScrollView alloc] initWithFrame:_scrollFrame];
        
        _scrollView.delegate = self;
        _scrollView.pagingEnabled = YES;
        _scrollView.showsHorizontalScrollIndicator = NO;
        _scrollView.clipsToBounds = NO;
        _scrollView.showsHorizontalScrollIndicator = NO;
        _scrollView.showsVerticalScrollIndicator = NO;
        _scrollView.backgroundColor = [UIColor clearColor];
        _scrollView.scrollEnabled = NO;
        [self addSubview:_scrollView];
        
        _leftArrow = [[UIButton alloc] init];
        _leftArrow.frame = CGRectMake(0, (233 - 46) / 2, 46, 46);
        _leftArrow.tintColor = [UIColor blueColor];
        [_leftArrow setImage:[UIImage imageNamed:@"ArrowRight"] forState:UIControlStateNormal];
        [_leftArrow addTarget:self action:@selector(didArrowClicked:) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:_leftArrow];
        
        _rightArrow = [[UIButton alloc] init];
        _rightArrow.frame = CGRectMake(274, (233 - 46) / 2, 46, 46);
        [_rightArrow setImage:[UIImage imageNamed:@"ArrowLeft"] forState:UIControlStateNormal];
        [_rightArrow addTarget:self action:@selector(didArrowClicked:) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:_rightArrow];
        
        [self notifyDataSetChanged];

    }
    
    return self;
}

- (void) notifyDataSetChanged {
//    NSLog(@"notifyDataSetChanged");
    [_imageLinks removeAllObjects];
    [_views removeAllObjects];
    
    [self getAllCardObjectImage];
    [self generateScroll];
    
}

- (void) getAllCardObjectImage {
    NSString *cardImgDir = [Utils getImageCardObjectDir];
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSArray *arr = [fileManager contentsOfDirectoryAtPath:cardImgDir error:nil];
    NSPredicate *fltr = [NSPredicate predicateWithFormat:@"self ENDSWITH '.JPG'"];
    NSArray * arrOnlyImages = [arr filteredArrayUsingPredicate:fltr];
    if(_imageLinks){
        [_imageLinks release];
        _imageLinks = nil;
    }
    _imageLinks = [[NSMutableArray alloc] initWithArray:arrOnlyImages];
    if (mapCard) {
        [mapCard release];
        mapCard = nil;
    }
    mapCard = [[NSMutableDictionary alloc] init];
    for (NSString * link in _imageLinks) {
        [mapCard setValue:[link stringByDeletingPathExtension] forKey:link];
    }
}

- (void) slideShow {
    (_currentItemIndex + 1) >= _totalItems ? (_currentItemIndex = _totalItems - 1) : (_currentItemIndex = _currentItemIndex + 1);
    [self scrollToPage:_currentItemIndex];
    [self updateArrowButton];
}

- (BOOL) canNext {
    return _currentItemIndex < _totalItems - 1;
}

- (void) resetGallery {
    _currentItemIndex = 0;
    [self scrollToPage:_currentItemIndex];
    [self updateArrowButton];
    [_delegate didArrowButtonClicked];
}

- (void) didArrowClicked:(id) sender {
    if (sender == _leftArrow) {
        (_currentItemIndex - 1) < 0 ? (_currentItemIndex = 0) : (_currentItemIndex = _currentItemIndex - 1);
    } else if (sender == _rightArrow) {
        (_currentItemIndex + 1) >= _totalItems ? (_currentItemIndex = _totalItems - 1) : (_currentItemIndex = _currentItemIndex + 1);
    }
    
    [self scrollToPage:_currentItemIndex];
    [self updateArrowButton];
    [_delegate didArrowButtonClicked];
}


- (void) scrollToPage:(int)pageIndex {
    CGRect lastVisibleRect;
    CGSize contentSize = [_scrollView contentSize];
    lastVisibleRect.size.height = contentSize.height; //We want the visible rect height to be the content view height as we are only scrolling horizontally
    lastVisibleRect.origin.y = 0.0; // So the y origin should be 0
    lastVisibleRect.size.width = PAGE_WIDTH; // The visible rect width should be as wide as the screen
    lastVisibleRect.origin.x  = contentSize.width - PAGE_WIDTH * (_totalItems - pageIndex); // And the x position should be all the way to the right - one page width (assumes a page is as wide as the screen)
    [_scrollView scrollRectToVisible:lastVisibleRect animated:NO];
}


- (void) generateScroll {
    if ([_scrollView.subviews count] > 0) {
        for (UIView *subview in _scrollView.subviews) {
            [subview removeFromSuperview];
        }
    }
    
    for (int j = 0; j < [_imageLinks count]; j++) {
        NSString *imgPath = [_imageLinks objectAtIndex:j];
        NSString *fullPath = [[Utils getImageCardObjectDir] stringByAppendingString:[NSString stringWithFormat:@"/%@", imgPath]];
        
        UIImage *img = [UIImage imageWithContentsOfFile:fullPath];
        UIImageView *imgView = [[UIImageView alloc] initWithImage:img];
        [_views addObject:imgView];
        [imgView release];
    }
    _totalItems = [_views count];
    
//    _totalItems == 0 ? (_currentItemIndex = 0) : (_currentItemIndex = _totalItems - 1);
    _currentItemIndex = 0;
    
    for (UIView *view in _scrollView.subviews) {
        [view removeFromSuperview];
    }
    int i;
    for (i = 0;i < _totalItems;i++) {
        UIImageView *img = [[_views objectAtIndex:i] retain];
        img.frame = CGRectMake(PAGE_WIDTH * i,0, PAGE_WIDTH, PAGE_HEIGH);
        [_scrollView addSubview:img];
        [img release];
    }
    
    _scrollView.contentSize = CGSizeMake(_scrollFrame.size.width * _totalItems, _scrollFrame.size.height);
    
    [self scrollToPage:_currentItemIndex];
    
    [self updateArrowButton];
}

- (void) updateArrowButton {
    if (_currentItemIndex == 0) {
        if ([_views count] == 0 || [_views count] == 1) {
            _leftArrow.hidden = YES;
            _rightArrow.hidden = YES;
        } else {
            _leftArrow.hidden = YES;
            _rightArrow.hidden = NO;
        }
    } else if (_currentItemIndex == _totalItems - 1) {
        _leftArrow.hidden = NO;
        _rightArrow.hidden = YES;
    } else {
        _leftArrow.hidden = NO;
        _rightArrow.hidden = NO;
    }
    
    [self updateImageVisible];
}

- (void) updateImageVisible {
    for (int i = 0;i < [_views count];i++) {
        (_currentItemIndex == i) ? [[_views objectAtIndex:i] setHidden:NO] : [[_views objectAtIndex:i] setHidden:YES];
    }
}

#pragma mark Scrool view delegate functions
- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView{

    int pageWidth = PAGE_WIDTH;
    int page = floor((scrollView.contentOffset.x - pageWidth / 2) / pageWidth) + 1;
    if (0 <= page && page < _totalItems) {
        _currentItemIndex = page;
//        NSLog(@"%d", _currentItemIndex);
    }
    
    [self updateArrowButton];
}

#pragma ImageGalleryView public method
- (UIImage *) getCurrentImageDisplay {
    NSString *imgPath = [_imageLinks objectAtIndex:_currentItemIndex];
    NSString *fullPath = [[Utils getImageCardObjectDir] stringByAppendingString:[NSString stringWithFormat:@"/%@", imgPath]];
    
    UIImage *img = [UIImage imageWithContentsOfFile:fullPath];
    return img;
}

- (NSString *) getCurrentCard {
    NSString *imgPath = [_imageLinks objectAtIndex:_currentItemIndex];
    return [mapCard valueForKey:imgPath];
}

- (void) dealloc {
    [_leftArrow release];
    [_rightArrow release];
    [_views release];
    [_scrollView release];
    [_imageLinks release];
    [super dealloc];
}

@end
