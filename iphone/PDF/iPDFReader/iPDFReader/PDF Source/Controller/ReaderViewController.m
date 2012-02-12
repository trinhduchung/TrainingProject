//
//  ReaderViewController.m
//  iPDFReader
//
//  Created by Cuong Tran on 2/11/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "ReaderViewController.h"
#import "ReaderDocument.h"
#import "TextDisplayViewController.h"
@implementation ReaderViewController
#pragma mark Constants

#define PAGING_VIEWS 3

#define TOOLBAR_HEIGHT 44.0f
#define PAGEBAR_HEIGHT 48.0f

#define TAP_AREA_SIZE 48.0f

#pragma mark Properties

@synthesize delegate;

#pragma mark Support methods

- (void)updateScrollViewContentSize {
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	NSInteger count = [_document.pageCount integerValue];
    
	if (count > PAGING_VIEWS) count = PAGING_VIEWS; // Limit
    
	CGFloat contentHeight = _theScrollView.bounds.size.height;
    
	CGFloat contentWidth = (_theScrollView.bounds.size.width * count);
    
	_theScrollView.contentSize = CGSizeMake(contentWidth, contentHeight);
}

- (void)updateScrollViewContentViews {
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	[self updateScrollViewContentSize]; // Update the content size
    
	NSMutableIndexSet *pageSet = [NSMutableIndexSet indexSet]; // Page set
    
	[_contentViews enumerateKeysAndObjectsUsingBlock: // Enumerate content views
     ^(id key, id object, BOOL *stop) {
         ReaderContentView *contentView = object; 
         [pageSet addIndex:contentView.tag];
     }];
    
	__block CGRect viewRect = CGRectZero;
    
    viewRect.size = _theScrollView.bounds.size;
    
	__block CGPoint contentOffset = CGPointZero; 
    
    NSInteger page = [_document.pageNumber integerValue];
    
	[pageSet enumerateIndexesUsingBlock: // Enumerate page number set
     ^(NSUInteger number, BOOL *stop) {
     
         NSNumber *key = [NSNumber numberWithInteger:number]; // # key
         
         ReaderContentView *contentView = [_contentViews objectForKey:key];
         
         contentView.frame = viewRect; if (page == number) contentOffset = viewRect.origin;
         
         viewRect.origin.x += viewRect.size.width; // Next view frame position
     }
     ];
    
	if (CGPointEqualToPoint(_theScrollView.contentOffset, contentOffset) == false) {
	
		_theScrollView.contentOffset = contentOffset; // Update content offset
	}
}

- (void) showDocumentPage:(NSInteger) page {
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    if (page != _currentPage) {
        NSInteger minValue; NSInteger maxValue;
		NSInteger maxPage = [_document.pageCount integerValue];
		NSInteger minPage = 1;
        
		if ((page < minPage) || (page > maxPage)) return;
        
		if (maxPage <= PAGING_VIEWS) {// Few pages
		
			minValue = minPage;
			maxValue = maxPage;
		}
		else {// Handle more pages
		
			minValue = (page - 1);
			maxValue = (page + 1);
            
			if (minValue < minPage)
            {minValue++; maxValue++;}
			else
				if (maxValue > maxPage)
                {minValue--; maxValue--;}
		}
        
        NSMutableIndexSet *newPageSet = [NSMutableIndexSet new];
        
		NSMutableDictionary *unusedViews = [_contentViews mutableCopy];
        
		CGRect viewRect = CGRectZero; viewRect.size = _theScrollView.bounds.size;
        
		for (NSInteger number = minValue; number <= maxValue; number++) {
		
			NSNumber *key = [NSNumber numberWithInteger:number]; // # key
            
			ReaderContentView *contentView = [_contentViews objectForKey:key];
            
			if (contentView == nil) {// Create a brand new document content view
			
				NSURL *fileURL = _document.fileURL; NSString *phrase = _document.password; // Document properties
                
				contentView = [[ReaderContentView alloc] initWithFrame:viewRect fileURL:fileURL page:number password:phrase];
                
				[_theScrollView addSubview:contentView]; [_contentViews setObject:contentView forKey:key];
                
				contentView.message = self; [contentView release]; [newPageSet addIndex:number];
			}
			else {// Reposition the existing content view
			
				contentView.frame = viewRect; [contentView zoomReset];
                
				[unusedViews removeObjectForKey:key];
			}
            
			viewRect.origin.x += viewRect.size.width;
		}
        
		[unusedViews enumerateKeysAndObjectsUsingBlock: // Remove unused views
         ^(id key, id object, BOOL *stop) {
             [_contentViews removeObjectForKey:key];
             
             ReaderContentView *contentView = object;
             
             [contentView removeFromSuperview];
         }
         ];
        
        [unusedViews release], unusedViews = nil; // Release unused views
        
		CGFloat viewWidthX1 = viewRect.size.width;
		CGFloat viewWidthX2 = (viewWidthX1 * 2.0f);
        
		CGPoint contentOffset = CGPointZero;
        
		if (maxPage >= PAGING_VIEWS) {
			if (page == maxPage)
				contentOffset.x = viewWidthX2;
			else
				if (page != minPage)
					contentOffset.x = viewWidthX1;
		}
		else
			if (page == (PAGING_VIEWS - 1))
				contentOffset.x = viewWidthX1;
        
		if (CGPointEqualToPoint(_theScrollView.contentOffset, contentOffset) == false) {
        
			_theScrollView.contentOffset = contentOffset; // Update content offset
		}
        
		if ([_document.pageNumber integerValue] != page) {// Only if different
		
			_document.pageNumber = [NSNumber numberWithInteger:page]; // Update page number
		}
        
		NSURL *fileURL = _document.fileURL; NSString *phrase = _document.password; NSString *guid = _document.guid;
        
		if ([newPageSet containsIndex:page] == YES) {// Preview visible page first
		
			NSNumber *key = [NSNumber numberWithInteger:page]; // # key
            
			ReaderContentView *targetView = [_contentViews objectForKey:key];
            
            //			[targetView showPageThumb:fileURL page:page password:phrase guid:guid];
            
			[newPageSet removeIndex:page]; // Remove visible page from set
		}
        
		[newPageSet enumerateIndexesWithOptions:NSEnumerationReverse usingBlock: // Show previews
         ^(NSUInteger number, BOOL *stop) {
         
             NSNumber *key = [NSNumber numberWithInteger:number]; // # key
             
             ReaderContentView *targetView = [_contentViews objectForKey:key];
             
             //             [targetView showPageThumb:fileURL page:number password:phrase guid:guid];
         }
         ];
        
		[newPageSet release], newPageSet = nil; // Release new page set
        
        //		[mainPagebar updatePagebar]; // Update the pagebar display
        
        //		[self updateToolbarBookmarkIcon]; // Update bookmark
        
		_currentPage = page; // Track current page number
        
    }
}

- (void) showDocument:(id) object {
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	[self updateScrollViewContentSize]; // Set content size
    
	[self showDocumentPage:[_document.pageNumber integerValue]]; // Show
    
	_document.lastOpen = [NSDate date]; // Update last opened date
    
	_isVisible = YES; // iOS present modal bodge
}

- (id) initWithReaderDocument:(ReaderDocument *)document {
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	id reader = nil; // ReaderViewController object
    
	if ((document != nil) && ([document isKindOfClass:[ReaderDocument class]])) {
	
		if ((self = [super initWithNibName:nil bundle:nil])) {// Designated initializer
		
			NSNotificationCenter *notificationCenter = [NSNotificationCenter defaultCenter];
            
			[notificationCenter addObserver:self selector:@selector(applicationWill:) name:UIApplicationWillTerminateNotification object:nil];
            
			[notificationCenter addObserver:self selector:@selector(applicationWill:) name:UIApplicationWillResignActiveNotification object:nil];
            
			[document updateProperties]; _document = [document retain]; // Retain the supplied ReaderDocument object for our use
            
			//[ReaderThumbCache touchThumbCacheWithGUID:object.guid]; // Touch the document thumb cache directory
            
			reader = self; // Return an initialized ReaderViewController object
		}
	}
    
	return reader;
    
}

- (void)viewDidLoad {
#ifdef DEBUGX
	NSLog(@"%s %@", __FUNCTION__, NSStringFromCGRect(self.view.bounds));
#endif
    
	[super viewDidLoad];
    
	NSAssert(!(_document == nil), @"ReaderDocument == nil");
    
	assert(self.splitViewController == nil); // Not supported (sorry)
    
	self.view.backgroundColor = [UIColor scrollViewTexturedBackgroundColor];
    
	CGRect viewRect = self.view.bounds; // View controller's view bounds
    
	_theScrollView = [[UIScrollView alloc] initWithFrame:viewRect]; // All
    
	_theScrollView.scrollsToTop = NO;
	_theScrollView.pagingEnabled = YES;
	_theScrollView.delaysContentTouches = NO;
	_theScrollView.showsVerticalScrollIndicator = NO;
	_theScrollView.showsHorizontalScrollIndicator = NO;
	_theScrollView.contentMode = UIViewContentModeRedraw;
	_theScrollView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
	_theScrollView.backgroundColor = [UIColor clearColor];
	_theScrollView.userInteractionEnabled = YES;
	_theScrollView.autoresizesSubviews = NO;
	_theScrollView.delegate = self;
    
	[self.view addSubview:_theScrollView];
    /*
     CGRect toolbarRect = viewRect;
     toolbarRect.size.height = TOOLBAR_HEIGHT;
     
     mainToolbar = [[ReaderMainToolbar alloc] initWithFrame:toolbarRect document:document]; // At top
     
     mainToolbar.delegate = self;
     
     [self.view addSubview:mainToolbar];
     */
    /*
     CGRect pagebarRect = viewRect;
     pagebarRect.size.height = PAGEBAR_HEIGHT;
     pagebarRect.origin.y = (viewRect.size.height - PAGEBAR_HEIGHT);
     
     mainPagebar = [[ReaderMainPagebar alloc] initWithFrame:pagebarRect document:document]; // At bottom
     
     mainPagebar.delegate = self;
     
     [self.view addSubview:mainPagebar];
     */
	UITapGestureRecognizer *singleTapOne = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleSingleTap:)];
	singleTapOne.numberOfTouchesRequired = 1; singleTapOne.numberOfTapsRequired = 1; singleTapOne.delegate = self;
    
	UITapGestureRecognizer *doubleTapOne = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleDoubleTap:)];
	doubleTapOne.numberOfTouchesRequired = 1; doubleTapOne.numberOfTapsRequired = 2; doubleTapOne.delegate = self;
    
	UITapGestureRecognizer *doubleTapTwo = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleDoubleTap:)];
	doubleTapTwo.numberOfTouchesRequired = 2; doubleTapTwo.numberOfTapsRequired = 2; doubleTapTwo.delegate = self;
    
	[singleTapOne requireGestureRecognizerToFail:doubleTapOne]; // Single tap requires double tap to fail
    
	[self.view addGestureRecognizer:singleTapOne]; [singleTapOne release];
	[self.view addGestureRecognizer:doubleTapOne]; [doubleTapOne release];
	[self.view addGestureRecognizer:doubleTapTwo]; [doubleTapTwo release];
    
	_contentViews = [NSMutableDictionary new]; 
    _lastHideTime = [NSDate new];
    
    //add Text Button
    UIBarButtonItem *btnText = [[UIBarButtonItem alloc] initWithTitle:@"text" style:UIBarButtonItemStyleBordered target:self action:@selector(actionText)];
    self.navigationItem.rightBarButtonItem = btnText;
    [btnText release];
}

- (void) actionText {
    if (!_waitForTextInput) {
        _waitForTextInput = YES;
        UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Text" message:@"Select the page you want the text of." delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
		[alert show];
		[alert release];
    } else {
        _waitForTextInput = NO;
    }
}

- (void)viewWillAppear:(BOOL)animated {
#ifdef DEBUGX
	NSLog(@"%s %@", __FUNCTION__, NSStringFromCGRect(self.view.bounds));
#endif
    
	[super viewWillAppear:animated];
    
	if (CGSizeEqualToSize(_lastAppearSize, CGSizeZero) == false) {
		if (CGSizeEqualToSize(_lastAppearSize, self.view.bounds.size) == false) {
			[self updateScrollViewContentViews]; // Update content views
		}
        
		_lastAppearSize = CGSizeZero; // Reset view size tracking
	}
}

- (void)viewDidAppear:(BOOL)animated {
#ifdef DEBUGX
	NSLog(@"%s %@", __FUNCTION__, NSStringFromCGRect(self.view.bounds));
#endif
    
	[super viewDidAppear:animated];
    
	if (CGSizeEqualToSize(_theScrollView.contentSize, CGSizeZero)) {// First time
	
		[self performSelector:@selector(showDocument:) withObject:nil afterDelay:0.02];
	}
    
#if (READER_DISABLE_IDLE == TRUE) // Option
    
	[UIApplication sharedApplication].idleTimerDisabled = YES;
    
#endif // end of READER_DISABLE_IDLE Option
}

- (void)viewWillDisappear:(BOOL)animated {
#ifdef DEBUGX
	NSLog(@"%s %@", __FUNCTION__, NSStringFromCGRect(self.view.bounds));
#endif
    
	[super viewWillDisappear:animated];
    
	_lastAppearSize = self.view.bounds.size; // Track view size
    
#if (READER_DISABLE_IDLE == TRUE) // Option
    
	[UIApplication sharedApplication].idleTimerDisabled = NO;
    
#endif // end of READER_DISABLE_IDLE Option
}

- (void)viewDidDisappear:(BOOL)animated {
#ifdef DEBUGX
	NSLog(@"%s %@", __FUNCTION__, NSStringFromCGRect(self.view.bounds));
#endif
    
	[super viewDidDisappear:animated];
}

- (void)viewDidUnload {
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
    //	[mainToolbar release], mainToolbar = nil; [mainPagebar release], mainPagebar = nil;
    
	[_theScrollView release], _theScrollView = nil; [_contentViews release], _contentViews = nil;
    
	[_lastHideTime release], _lastHideTime = nil; _lastAppearSize = CGSizeZero; _currentPage = 0;
    
	[super viewDidUnload];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
#ifdef DEBUGX
	NSLog(@"%s (%d)", __FUNCTION__, interfaceOrientation);
#endif
    
	return YES;
}

- (void)willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration {
#ifdef DEBUGX
	NSLog(@"%s %@ (%d)", __FUNCTION__, NSStringFromCGRect(self.view.bounds), toInterfaceOrientation);
#endif
    
	if (_isVisible == NO) return; // iOS present modal bodge
    /*
     if ([UIDevice currentDevice].userInterfaceIdiom == UIUserInterfaceIdiomPad)
     {
     if (printInteraction != nil) [printInteraction dismissAnimated:NO];
     }
     */
}

- (void)willAnimateRotationToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation duration:(NSTimeInterval)duration {
#ifdef DEBUGX
	NSLog(@"%s %@ (%d)", __FUNCTION__, NSStringFromCGRect(self.view.bounds), interfaceOrientation);
#endif
    
	if (_isVisible == NO) return; // iOS present modal bodge
    
	[self updateScrollViewContentViews]; // Update content views
    
	_lastAppearSize = CGSizeZero; // Reset view size tracking
}

- (void)didRotateFromInterfaceOrientation:(UIInterfaceOrientation)fromInterfaceOrientation {
#ifdef DEBUGX
	NSLog(@"%s %@ (%d to %d)", __FUNCTION__, NSStringFromCGRect(self.view.bounds), fromInterfaceOrientation, self.interfaceOrientation);
#endif
    
	//if (isVisible == NO) return; // iOS present modal bodge
    
	//if (fromInterfaceOrientation == self.interfaceOrientation) return;
}

- (void)didReceiveMemoryWarning {
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	[super didReceiveMemoryWarning];
}

- (void)dealloc {
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	[[NSNotificationCenter defaultCenter] removeObserver:self];
    
    //	[mainToolbar release], mainToolbar = nil; [mainPagebar release], mainPagebar = nil;
    
	[_theScrollView release], _theScrollView = nil; [_contentViews release], _contentViews = nil;
    
	[_lastHideTime release], _lastHideTime = nil; [_document release], _document = nil;
    
	[super dealloc];
}
#pragma mark UIScrollViewDelegate methods
- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView {
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	__block NSInteger page = 0;
    
	CGFloat contentOffsetX = scrollView.contentOffset.x;
    
	[_contentViews enumerateKeysAndObjectsUsingBlock: // Enumerate content views
     ^(id key, id object, BOOL *stop) {
         ReaderContentView *contentView = object;
         
         if (contentView.frame.origin.x == contentOffsetX) {
             page = contentView.tag; *stop = YES;
         }
     }
     ];
    
	if (page != 0) [self showDocumentPage:page]; // Show the page
}

- (void)scrollViewDidEndScrollingAnimation:(UIScrollView *)scrollView {
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	[self showDocumentPage:_theScrollView.tag]; // Show page
    
	_theScrollView.tag = 0; // Clear page number tag
}
#pragma mark UIGestureRecognizerDelegate methods
- (BOOL)gestureRecognizer:(UIGestureRecognizer *)recognizer shouldReceiveTouch:(UITouch *)touch {
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	if ([touch.view isKindOfClass:[UIScrollView class]]) return YES;
    
	return NO;
}
#pragma mark UIGestureRecognizer action methods
- (void)decrementPageNumber {
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	if (_theScrollView.tag == 0) {// Scroll view did end
	
		NSInteger page = [_document.pageNumber integerValue];
		NSInteger maxPage = [_document.pageCount integerValue];
		NSInteger minPage = 1; // Minimum
        
		if ((maxPage > minPage) && (page != minPage)) {
		
			CGPoint contentOffset = _theScrollView.contentOffset;
            
			contentOffset.x -= _theScrollView.bounds.size.width; // -= 1
            
			[_theScrollView setContentOffset:contentOffset animated:YES];
            
			_theScrollView.tag = (page - 1); // Decrement page number
		}
	}
}

- (void)incrementPageNumber {

#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	if (_theScrollView.tag == 0) {// Scroll view did end
	
		NSInteger page = [_document.pageNumber integerValue];
		NSInteger maxPage = [_document.pageCount integerValue];
		NSInteger minPage = 1; // Minimum
        
		if ((maxPage > minPage) && (page != maxPage)) {
		
			CGPoint contentOffset = _theScrollView.contentOffset;
            
			contentOffset.x += _theScrollView.bounds.size.width; // += 1
            
			[_theScrollView setContentOffset:contentOffset animated:YES];
            
			_theScrollView.tag = (page + 1); // Increment page number
		}
	}
}
#pragma TextDisplayViewController delegate
-(void)dismissTextDisplayViewController:(TextDisplayViewController *)controller {
    
    [self dismissModalViewControllerAnimated:YES];
}

- (void)handleSingleTap:(UITapGestureRecognizer *)recognizer {

#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	if (recognizer.state == UIGestureRecognizerStateRecognized) {
	
		CGRect viewRect = recognizer.view.bounds; // View bounds
        
		CGPoint point = [recognizer locationInView:recognizer.view];
        
		CGRect areaRect = CGRectInset(viewRect, TAP_AREA_SIZE, 0.0f); // Area
        
		if (CGRectContainsPoint(areaRect, point)) {// Single tap is inside the area
		
			NSInteger page = [_document.pageNumber integerValue]; // Current page #
            
			NSNumber *key = [NSNumber numberWithInteger:page]; // Page number key
            
			ReaderContentView *targetView = [_contentViews objectForKey:key]; //target page
            
			id target = [targetView singleTap:recognizer]; // Process tap
            
			if (target != nil) {// Handle the returned target object
			
				if ([target isKindOfClass:[NSURL class]]) {// Open a URL
				
					[[UIApplication sharedApplication] openURL:target];
				}
				else {// Not a URL, so check for other possible object type
				
					if ([target isKindOfClass:[NSNumber class]]) {// Goto page
					
						NSInteger value = [target integerValue]; // Number
                        
						[self showDocumentPage:value]; // Show the page
					}
				}
			}
			else {// Nothing active tapped in the target content view or other custom tapped....
                
                if (_waitForTextInput) { //added select text.
                    _waitForTextInput = NO;
                    targetView.userInteractionEnabled = YES;
                    NSLog(@"present model : TextDisplayViewController");
                    
                    TextDisplayViewController * controller = [[TextDisplayViewController alloc] initWithNibName:@"TextDisplayView" bundle:nil];
                    controller.delegate = self;
                    
                    [controller updateWithTextOfPage:_currentPage documentPath:[_document.fileURL absoluteString]];
                    [self presentModalViewController:controller animated:YES];
                     
                }
                
				if ([_lastHideTime timeIntervalSinceNow] < -0.75) {// Delay since hide
				
                    /*
                     if ((mainToolbar.hidden == YES) || (mainPagebar.hidden == YES))
                     {
                     [mainToolbar showToolbar]; [mainPagebar showPagebar]; // Show
                     }
                     */
                    
				}
			}
            
			return;
		}
        
		CGRect nextPageRect = viewRect;
		nextPageRect.size.width = TAP_AREA_SIZE;
		nextPageRect.origin.x = (viewRect.size.width - TAP_AREA_SIZE);
        
		if (CGRectContainsPoint(nextPageRect, point)) {// page++ area
		
			[self incrementPageNumber]; return;
		}
        
		CGRect prevPageRect = viewRect;
		prevPageRect.size.width = TAP_AREA_SIZE;
        
		if (CGRectContainsPoint(prevPageRect, point)) {// page-- area
		
			[self decrementPageNumber]; return;
		}
	}
}

- (void)handleDoubleTap:(UITapGestureRecognizer *)recognizer {

#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	if (recognizer.state == UIGestureRecognizerStateRecognized) {
	
		CGRect viewRect = recognizer.view.bounds; // View bounds
        
		CGPoint point = [recognizer locationInView:recognizer.view];
        
		CGRect zoomArea = CGRectInset(viewRect, TAP_AREA_SIZE, TAP_AREA_SIZE);
        
		if (CGRectContainsPoint(zoomArea, point)) {// Double tap is in the zoom area
		
			NSInteger page = [_document.pageNumber integerValue]; // Current page #
            
			NSNumber *key = [NSNumber numberWithInteger:page]; // Page number key
            
			ReaderContentView *targetView = [_contentViews objectForKey:key];
            
			switch (recognizer.numberOfTouchesRequired) // Touches count
			{
				case 1: // One finger double tap: zoom ++
				{
					[targetView zoomIncrement]; break;
				}
                    
				case 2: // Two finger double tap: zoom --
				{
					[targetView zoomDecrement]; break;
				}
			}
            
			return;
		}
        
		CGRect nextPageRect = viewRect;
		nextPageRect.size.width = TAP_AREA_SIZE;
		nextPageRect.origin.x = (viewRect.size.width - TAP_AREA_SIZE);
        
		if (CGRectContainsPoint(nextPageRect, point)) {// page++ area
		
			[self incrementPageNumber]; return;
		}
        
		CGRect prevPageRect = viewRect;
		prevPageRect.size.width = TAP_AREA_SIZE;
        
		if (CGRectContainsPoint(prevPageRect, point)) {// page-- area
		
			[self decrementPageNumber]; return;
		}
	}
}

#pragma mark ReaderContentViewDelegate methods

- (void)contentView:(ReaderContentView *)contentView touchesBegan:(NSSet *)touches {
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
    //	if ((mainToolbar.hidden == NO) || (mainPagebar.hidden == NO))
    //	{
    if (touches.count == 1) // Single touches only
    {
        UITouch *touch = [touches anyObject]; // Touch info
        
        CGPoint point = [touch locationInView:self.view]; // Touch location
        
        CGRect areaRect = CGRectInset(self.view.bounds, TAP_AREA_SIZE, TAP_AREA_SIZE);
        
        if (CGRectContainsPoint(areaRect, point) == false) return;
    }
    
    //		[mainToolbar hideToolbar]; [mainPagebar hidePagebar]; // Hide
    
    [_lastHideTime release]; _lastHideTime = [NSDate new];
    //	}
}

#pragma mark UIApplication notification methods

- (void)applicationWill:(NSNotification *)notification {
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	[_document saveReaderDocument]; // Save any ReaderDocument object changes
    
	if ([UIDevice currentDevice].userInterfaceIdiom == UIUserInterfaceIdiomPad)
	{
        //		if (printInteraction != nil) [printInteraction dismissAnimated:NO];
	}
}


#pragma mark - View lifecycle

/*
 // Implement loadView to create a view hierarchy programmatically, without using a nib.
 - (void)loadView
 {
 }
 */

/*
 // Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
 - (void)viewDidLoad
 {
 [super viewDidLoad];
 }
 */

@end
