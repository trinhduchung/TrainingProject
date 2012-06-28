//
//  UIImage+Addition.m
//  DemoCropImage
//
//  Created by Han Korea on 5/22/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "UIImage+Addition.h"

@implementation UIImage (Addition)
- (CGContextRef)allocRawData:(UIImage *)image {
    CGImageRef cgImage = image.CGImage;
	const size_t originalWidth = CGImageGetWidth(cgImage);
	const size_t originalHeight = CGImageGetHeight(cgImage);
	const size_t bytesPerRow = originalWidth * 4;
    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();
    
    /* Create the bitmap context, we want pre-multiplied ARGB, 8-bits per component */
	CGContextRef bmContext = CGBitmapContextCreate(NULL, 
                                                   originalWidth, 
                                                   originalHeight, 
                                                   8/*Bits per component*/, 
                                                   bytesPerRow, 
                                                   colorSpace, 
                                                   kCGBitmapByteOrderDefault | kCGImageAlphaPremultipliedFirst);
    
	CGColorSpaceRelease(colorSpace);
    
    if (!bmContext) 
		return nil;
    return bmContext;
}

- (UIImage *)fixOrientation {
    
    // No-op if the orientation is already correct
    if (self.imageOrientation == UIImageOrientationUp) return self;
    
    // We need to calculate the proper transformation to make the image upright.
    // We do it in 2 steps: Rotate if Left/Right/Down, and then flip if Mirrored.
    CGAffineTransform transform = CGAffineTransformIdentity;
    
    switch (self.imageOrientation) {
        case UIImageOrientationDown:
        case UIImageOrientationDownMirrored:
            transform = CGAffineTransformTranslate(transform, self.size.width, self.size.height);
            transform = CGAffineTransformRotate(transform, M_PI);
            break;
            
        case UIImageOrientationLeft:
        case UIImageOrientationLeftMirrored:
            transform = CGAffineTransformTranslate(transform, self.size.width, 0);
            transform = CGAffineTransformRotate(transform, M_PI_2);
            break;
            
        case UIImageOrientationRight:
        case UIImageOrientationRightMirrored:
            transform = CGAffineTransformTranslate(transform, 0, self.size.height);
            transform = CGAffineTransformRotate(transform, -M_PI_2);
            break;
        case UIImageOrientationUp:
        case UIImageOrientationUpMirrored:
            break;
    }
    
    switch (self.imageOrientation) {
        case UIImageOrientationUpMirrored:
        case UIImageOrientationDownMirrored:
            transform = CGAffineTransformTranslate(transform, self.size.width, 0);
            transform = CGAffineTransformScale(transform, -1, 1);
            break;
            
        case UIImageOrientationLeftMirrored:
        case UIImageOrientationRightMirrored:
            transform = CGAffineTransformTranslate(transform, self.size.height, 0);
            transform = CGAffineTransformScale(transform, -1, 1);
            break;
        case UIImageOrientationUp:
        case UIImageOrientationDown:
        case UIImageOrientationLeft:
        case UIImageOrientationRight:
            break;
    }
    
    // Now we draw the underlying CGImage into a new context, applying the transform
    // calculated above.
    CGContextRef ctx = CGBitmapContextCreate(NULL, self.size.width, self.size.height,
                                             CGImageGetBitsPerComponent(self.CGImage), 0,
                                             CGImageGetColorSpace(self.CGImage),
                                             CGImageGetBitmapInfo(self.CGImage));
    CGContextConcatCTM(ctx, transform);
    switch (self.imageOrientation) {
        case UIImageOrientationLeft:
        case UIImageOrientationLeftMirrored:
        case UIImageOrientationRight:
        case UIImageOrientationRightMirrored:
            // Grr...
            CGContextDrawImage(ctx, CGRectMake(0,0,self.size.height,self.size.width), self.CGImage);
            break;
            
        default:
            CGContextDrawImage(ctx, CGRectMake(0,0,self.size.width,self.size.height), self.CGImage);
            break;
    }
    
    // And now we just create a new UIImage from the drawing context
    CGImageRef cgimg = CGBitmapContextCreateImage(ctx);
    UIImage *img = [UIImage imageWithCGImage:cgimg];
    CGContextRelease(ctx);
    CGImageRelease(cgimg);
    return img;
}

- (UIImage *)cropImageWithPath:(CGMutablePathRef)path {
    /* get raw data */
    UIGraphicsBeginImageContext(self.size);
    
    CGContextRef bmContext = UIGraphicsGetCurrentContext();  
    CGContextSetAllowsAntialiasing(bmContext, true);
    CGContextSetShouldAntialias(bmContext, true);
    CGContextAddPath(bmContext, path);
    CGContextClip(bmContext);
    
    [self drawInRect:CGRectMake(0, 0, self.size.width, self.size.height)];
    CGContextSetAllowsAntialiasing(bmContext, false);
    /* Create an image object from the context */
    UIImage *resultImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
	return resultImage;

}
@end
