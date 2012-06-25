//
//  UIImage+Addition.h
//  DemoCropImage
//
//  Created by helios-team on 5/22/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIImage (Addition)
- (UIImage *)cropImageWithRect:(CGRect)rect;
- (UIImage *)cropImageWithPath:(CGMutablePathRef)path;
- (UIImage *)cropImageWithBezierPath:(UIBezierPath *)path;
@end
