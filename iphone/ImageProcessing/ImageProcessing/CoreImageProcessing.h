//
//  CoreImageProcessing.h
//  MobionPhoto
//
//  Created by Han Korea on 2/1/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
enum {
    CurveChannelNone                 = 0,
    CurveChannelRed					 = 1 << 0,
    CurveChannelGreen				 = 1 << 1,
    CurveChannelBlue				 = 1 << 2,
};
typedef NSUInteger CurveChannel;
@interface UIImage (ImageProcessing)
- (UIImage*) grayscale;
- (UIImage*) sepia;
- (UIImage*) posterize:(int)levels;
- (UIImage*) saturate:(float)amount;
- (UIImage*) brightness:(float)amount;
- (UIImage*) gamma:(float)amount;
- (UIImage*) opacity:(float)amount;
- (UIImage*) contrast:(float)amount;
- (UIImage*) bias:(float)amount;
- (UIImage*) fill:(float)r green:(float)g blue:(float)b;
- (UIImage*)gaussianBlur:(NSInteger)bias;
- (UIImage *)noslagia;
- (UIImage *)pencilSketchFilter;
@end
