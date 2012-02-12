//
//  ReaderContentTile.m
//  iPDFReader
//
//  Created by Cuong Tran on 2/11/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "ReaderContentTile.h"

@implementation ReaderContentTile
#pragma constants
#define LEVELS_OF_DETAIL 4
#define LEVELS_OF_DETAIL_BIAS 3

#pragma mark ReaderContentTile class methods

+ (CFTimeInterval)fadeDuration {
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	return 0.001; // iOS bug workaround
    
	//return 0.0; // No fading wanted
}

#pragma mark ReaderContentTile instance methods

- (id)init {
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	if ((self = [super init]))
	{
		self.levelsOfDetail = LEVELS_OF_DETAIL;
        
		self.levelsOfDetailBias = LEVELS_OF_DETAIL_BIAS;
        
		UIScreen *mainScreen = [UIScreen mainScreen]; // Screen
        
		CGFloat screenScale = [mainScreen scale]; // Screen scale
        
		CGRect screenBounds = [mainScreen bounds]; // Screen bounds
        
		CGFloat w_pixels = (screenBounds.size.width * screenScale);
        
		CGFloat h_pixels = (screenBounds.size.height * screenScale);
        
		CGFloat max = ((w_pixels < h_pixels) ? h_pixels : w_pixels);
        
		CGFloat sizeOfTiles = ((max < 512.0f) ? 512.0f : 1024.0f);
        
		self.tileSize = CGSizeMake(sizeOfTiles, sizeOfTiles);
	}
    
	return self;
}

- (void)dealloc {
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
	[super dealloc];
}

@end
