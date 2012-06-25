//
//  UIImage+Addition.m
//  DemoCropImage
//
//  Created by helios-team on 5/22/12.
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

- (UIImage *)cropImageWithRect:(CGRect)rect
{
    CGFloat scale = [[UIScreen mainScreen] scale];
    
    if (scale>1.0) {        
        rect = CGRectMake(rect.origin.x*scale , rect.origin.y*scale, rect.size.width*scale, rect.size.height*scale);        
    }
    
    CGImageRef imageRef = CGImageCreateWithImageInRect([self CGImage], rect);
    UIImage *result = [UIImage imageWithCGImage:imageRef]; 
    CGImageRelease(imageRef);
    return result;
}

- (UIImage *)cropImageWithBezierPath:(UIBezierPath *)path {
    CAShapeLayer *maskLayer = [CAShapeLayer layer];
    maskLayer.frame = CGRectMake(0, 0, self.size.width, self.size.height);
    maskLayer.fillColor = [[UIColor whiteColor] CGColor];
    maskLayer.backgroundColor = [[UIColor clearColor] CGColor];
    maskLayer.path = [path CGPath];
    UIImage * result;
    return result;
}

- (UIImage *)cropImageWithPath:(CGMutablePathRef)path {
    UIGraphicsBeginImageContext(self.size);
    
    CGContextRef bmContext = UIGraphicsGetCurrentContext();   
    CGContextAddPath(bmContext, path);
    CGContextClip(bmContext);
    
    CGContextDrawImage(bmContext, (CGRect){.origin.x = 0.0f, .origin.y = 0.0f, self.size.width , self.size.height}, self.CGImage);
    
    /* Create an image object from the context */
    UIImage *resultImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
	return resultImage;
//    /* get raw data */
//    CGContextRef bmContext = [self allocRawData:self];
//	UInt8* data = (UInt8*)CGBitmapContextGetData(bmContext);
//	if (!data)
//	{
//		CGContextRelease(bmContext);
//		return nil;
//	}
//    CGContextAddPath(bmContext, path);
//    CGContextClip(bmContext);
//    
//    CGContextDrawImage(bmContext, (CGRect){.origin.x = 0.0f, .origin.y = 0.0f, self.size.width , self.size.height}, self.CGImage);
//    /* Create an image object from the context */
//    CGImageRef cropImageRef = CGBitmapContextCreateImage(bmContext);
//	UIImage* resultImage = [UIImage imageWithCGImage:cropImageRef];
//    
//	/* Cleanup */
//	CGImageRelease(cropImageRef);
//	CGContextRelease(bmContext);
//
//    
//	return resultImage;

}
@end
