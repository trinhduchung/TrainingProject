//
//  ReaderContentView.m
//  iPDFReader
//
//  Created by Cuong Tran on 2/11/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "ReaderContentView.h"
#import "ReaderContentPage.h"
#import <QuartzCore/QuartzCore.h>
@implementation ReaderContentView
#pragma mark constants
#define ZOOM_LEVELS 4
#if (READER_SHOW_SHADOWS == TRUE) // Option
#define CONTENT_INSET 4.0f
#else
#define CONTENT_INSET 2.0f
#endif // end of READER_SHOW_SHADOWS Option

#define PAGE_THUMB_LARGE 240
#define PAGE_THUMB_SMALL 144

#pragma mark Properties

@synthesize message;

#pragma mark ReaderContentView functions

static inline CGFloat ZoomScaleThatFits(CGSize target, CGSize source)
{
	CGFloat w_scale = (target.width / source.width);
	CGFloat h_scale = (target.height / source.height);
    
	return ((w_scale < h_scale) ? w_scale : h_scale);
}

#pragma mark ReaderContentView instance methods

- (void) updateMinimumMaximumZoom {
    CGRect targetRect = CGRectInset(self.bounds, CONTENT_INSET, CONTENT_INSET);
    
	CGFloat zoomScale = ZoomScaleThatFits(targetRect.size, _theContentView.bounds.size);
    
	self.minimumZoomScale = zoomScale; // Set the minimum and maximum zoom scales
    
	self.maximumZoomScale = (zoomScale * ZOOM_LEVELS); // Max number of zoom levels
    
	zoomAmount = ((self.maximumZoomScale - self.minimumZoomScale) / ZOOM_LEVELS);
}

- (id) initWithFrame:(CGRect)frame fileURL:(NSURL *)fileURL page:(NSUInteger)page password:(NSString *)phrase {
    
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	if ((self = [super initWithFrame:frame]))
	{
        self.scrollsToTop = NO;
		self.delaysContentTouches = NO;
		self.showsVerticalScrollIndicator = NO;
		self.showsHorizontalScrollIndicator = NO;
		self.contentMode = UIViewContentModeRedraw;
		self.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
		self.backgroundColor = [UIColor clearColor];
		self.userInteractionEnabled = YES;
		self.autoresizesSubviews = NO;
		self.bouncesZoom = YES;
		self.delegate = self;
    }
    
    _theContentView = [[ReaderContentPage alloc] initWithURL:fileURL page:page password:phrase];
    
    if (_theContentView != nil) {
        // Must have a valid and initialized content view
        _theContainerView = [[UIView alloc] initWithFrame:_theContentView.bounds];
        _theContainerView.autoresizesSubviews = NO;
        _theContainerView.userInteractionEnabled = NO;
        _theContainerView.contentMode = UIViewContentModeRedraw;
        _theContainerView.autoresizingMask = UIViewAutoresizingNone;
        _theContainerView.backgroundColor = [UIColor whiteColor];
        
#if (READER_SHOW_SHADOWS == TRUE) // Option
        
        _theContainerView.layer.shadowOffset = CGSizeMake(0.0f, 0.0f);
        _theContainerView.layer.shadowRadius = 4.0f; 
        _theContainerView.layer.shadowOpacity = 1.0f;
        _theContainerView.layer.shadowPath = [UIBezierPath bezierPathWithRect:_theContainerView.bounds].CGPath;
        
#endif // end of READER_SHOW_SHADOWS Option
        self.contentSize = _theContentView.bounds.size; // Content size same as view size
        self.contentOffset = CGPointMake((0.0f - CONTENT_INSET), (0.0f - CONTENT_INSET)); // Offset
        self.contentInset = UIEdgeInsetsMake(CONTENT_INSET, CONTENT_INSET, CONTENT_INSET, CONTENT_INSET);
        
        [_theContainerView addSubview:_theContentView];
        
        [self addSubview:_theContainerView];
        [self updateMinimumMaximumZoom];
        self.zoomScale = self.minimumZoomScale;// Set zoom to fit page content
    }
    
    [self addObserver:self forKeyPath:@"frame" options:NSKeyValueObservingOptionNew context:NULL];
    
    self.tag = page; // Tag the view with the page number
    
    return self;
}

- (void)dealloc
{
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	[self removeObserver:self forKeyPath:@"frame"];
    
	[_theContainerView release], _theContainerView = nil;
    
	[_theContentView release], _theContentView = nil;
    
	[super dealloc];
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context
{
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	if ((object == self) && [keyPath isEqualToString:@"frame"])
	{
		CGFloat oldMinimumZoomScale = self.minimumZoomScale;
        
		[self updateMinimumMaximumZoom]; // Update zoom scale limits
        
		if (self.zoomScale == oldMinimumZoomScale) // Old minimum
		{
			self.zoomScale = self.minimumZoomScale;
		}
		else // Check against minimum zoom scale
		{
			if (self.zoomScale < self.minimumZoomScale)
			{
				self.zoomScale = self.minimumZoomScale;
			}
			else // Check against maximum zoom scale
			{
				if (self.zoomScale > self.maximumZoomScale)
				{
					self.zoomScale = self.maximumZoomScale;
				}
			}
		}
	}
}

- (void)layoutSubviews
{
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	[super layoutSubviews];
    
	CGSize boundsSize = self.bounds.size;
	CGRect viewFrame = _theContainerView.frame;
    
	if (viewFrame.size.width < boundsSize.width)
		viewFrame.origin.x = (((boundsSize.width - viewFrame.size.width) / 2.0f) + self.contentOffset.x);
	else
		viewFrame.origin.x = 0.0f;
    
	if (viewFrame.size.height < boundsSize.height)
		viewFrame.origin.y = (((boundsSize.height - viewFrame.size.height) / 2.0f) + self.contentOffset.y);
	else
		viewFrame.origin.y = 0.0f;
    
	_theContainerView.frame = viewFrame;
}

- (id)singleTap:(UITapGestureRecognizer *)recognizer {
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	return [_theContentView singleTap:recognizer];
}

- (void)zoomIncrement
{
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	CGFloat zoomScale = self.zoomScale;
    
	if (zoomScale < self.maximumZoomScale)
	{
		zoomScale += zoomAmount; // += value
        
		if (zoomScale > self.maximumZoomScale)
		{
			zoomScale = self.maximumZoomScale;
		}
        
		[self setZoomScale:zoomScale animated:YES];
	}
}

- (void)zoomDecrement
{
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	CGFloat zoomScale = self.zoomScale;
    
	if (zoomScale > self.minimumZoomScale)
	{
		zoomScale -= zoomAmount; // -= value
        
		if (zoomScale < self.minimumZoomScale)
		{
			zoomScale = self.minimumZoomScale;
		}
        
		[self setZoomScale:zoomScale animated:YES];
	}
}

- (void)zoomReset
{
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	if (self.zoomScale > self.minimumZoomScale)
	{
		self.zoomScale = self.minimumZoomScale;
	}
}

#pragma mark UIScrollViewDelegate methods

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
	[super touchesBegan:touches withEvent:event]; // Message superclass
    
	[message contentView:self touchesBegan:touches]; // Message delegate
}

- (void)touchesCancelled:(NSSet *)touches withEvent:(UIEvent *)event
{
	[super touchesCancelled:touches withEvent:event]; // Message superclass
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event
{
	[super touchesEnded:touches withEvent:event]; // Message superclass
}

- (void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event
{
	[super touchesMoved:touches withEvent:event]; // Message superclass
}


@end
