//
//  CoreImageProcessing.m
//  MobionPhoto
//
//  Created by Han Korea on 2/1/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "CoreImageProcessing.h"
#import "CatmullRomSpline.h"
#import <Accelerate/Accelerate.h>
#define DSP_KERNEL_POSITION(x,y,size) (x * size + y)
#define SAFECOLOR(color) MIN(255,MAX(0,color))

static float MIN_COLOR = 0.0f;
static float MAX_COLOR = 255.0f;

CGContextRef createARGBBitmapContext(const size_t width, const size_t height, const size_t bytesPerRow) 
{
    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();
	/// Create the bitmap context, we want pre-multiplied ARGB, 8-bits per component
    CGContextRef bmContext = CGBitmapContextCreate(NULL, width, height, 8/*Bits per component*/, bytesPerRow, colorSpace, kCGBitmapByteOrderDefault | kCGImageAlphaPremultipliedFirst);
	CGColorSpaceRelease(colorSpace);
    
    if (!bmContext) 
		return nil;
    return bmContext;
}

// The following function was taken from the increadibly awesome HockeyKit
// Created by Peter Steinberger on 10.01.11.
// Copyright 2012 Peter Steinberger. All rights reserved.
CGImageRef createGradientImage(const size_t pixelsWide, const size_t pixelsHigh, const CGFloat fromAlpha, const CGFloat toAlpha)
{
	// gradient is always black-white and the mask must be in the gray colorspace
    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceGray();
    
	// create the bitmap context
	CGContextRef gradientBitmapContext = CGBitmapContextCreate(NULL, pixelsWide, pixelsHigh, 8, 0, colorSpace, kCGImageAlphaNone);
    
	// define the start and end grayscale values (with the alpha, even though
	// our bitmap context doesn't support alpha the gradient requires it)
	CGFloat colors[] = {toAlpha, 1.0f, fromAlpha, 1.0f};
    
	// create the CGGradient and then release the gray color space
	CGGradientRef grayScaleGradient = CGGradientCreateWithColorComponents(colorSpace, colors, NULL, 2);
	CGColorSpaceRelease(colorSpace);
    
	// create the start and end points for the gradient vector (straight down)
	CGPoint gradientEndPoint = CGPointZero;
	CGPoint gradientStartPoint = (CGPoint){.x = 0.0f, .y = pixelsHigh};
    
	// draw the gradient into the gray bitmap context
	CGContextDrawLinearGradient(gradientBitmapContext, grayScaleGradient, gradientStartPoint, gradientEndPoint, kCGGradientDrawsAfterEndLocation);
	CGGradientRelease(grayScaleGradient);
    
	// convert the context into a CGImageRef and release the context
	CGImageRef theCGImage = CGBitmapContextCreateImage(gradientBitmapContext);
	CGContextRelease(gradientBitmapContext);
    
	// return the imageref containing the gradient
    return theCGImage;
}



@implementation UIImage (ImageProcessing)

- (UIColor *)colorWithInt:(int)value 
{
	
	int r,b,g;
	b = value &	  0x0000FF;
	g = ((value & 0x00FF00) >> 8);
	r = ((value & 0xFF0000) >> 16);
	UIColor *color		= [UIColor colorWithRed:r/255.0f green:g/255.0f blue:b/255.0f alpha:1.0f];
	return color;
	
}



- (UIImage*) grayscale {
    const size_t width = self.size.width;
    const size_t height = self.size.height;
    const size_t bytesPerRow = width * 4;
    
    /// Create an ARGB bitmap context
    CGContextRef bmContext = createARGBBitmapContext(width, height, bytesPerRow);
    if (!bmContext) 
        return nil;
    
    /// Draw the image in the bitmap context
    CGContextDrawImage(bmContext, (CGRect){.origin.x = 0.0f, .origin.y = 0.0f, .size.width = width, .size.height = height}, self.CGImage);
    
    /// Grab the image raw data
    UInt8* data = (UInt8*)CGBitmapContextGetData(bmContext);
    if (!data)
    {
        CGContextRelease(bmContext);
        return nil;
    }
    
    //init some temp arrays to calculate.
    const size_t pixelsCount = width * height;
    const size_t n = sizeof(float) * pixelsCount;
    float* reds = (float*)malloc(n);
    float* greens = (float*)malloc(n);
    float* blues = (float*)malloc(n);
    float* avgs = (float*)malloc(n);
    float* tmpRed = (float*)malloc(n);
    float* tmpGreen = (float*)malloc(n);
    float* tmpBlue = (float*)malloc(n);
    float* finalRed = (float*)malloc(n);
    float* finalGreen = (float*)malloc(n);
    float* finalBlue = (float*)malloc(n);
    float rate = 3.0f;
    //Calculate Average of r,g,b
    vDSP_vfltu8(data + 1, 4, reds, 1, pixelsCount);
    vDSP_vfltu8(data + 2, 4, greens, 1, pixelsCount);
    vDSP_vfltu8(data + 3, 4, blues, 1, pixelsCount);
    vDSP_vadd(reds, 1, greens, 1, tmpRed, 1, pixelsCount);
    vDSP_vadd(tmpRed, 1, blues, 1, tmpRed, 1, pixelsCount);
    vDSP_vsdiv(tmpRed, 1, &rate, avgs, 1, pixelsCount);
    
    vDSP_vfixu8(avgs, 1, data + 1, 4, pixelsCount); 
    vDSP_vfixu8(avgs, 1, data + 2, 4, pixelsCount);
    vDSP_vfixu8(avgs, 1, data + 3, 4, pixelsCount);
	CGImageRef saturateImageRef = CGBitmapContextCreateImage(bmContext);
    UIImage* saturateImage = [UIImage imageWithCGImage:saturateImageRef];
    
    /// Cleanup
    CGImageRelease(saturateImageRef);
    free(reds), free(greens), free(blues), free(tmpRed), free(tmpGreen), free(tmpBlue), free(finalRed), free(finalGreen), free(finalBlue), free(avgs);
    CGContextRelease(bmContext);
    return saturateImage;
}


- (UIImage*) sepia {
    if (![CIImage class]) // if ios 5
	{
        //Remove software-render in CIContext
        NSNumber* num = [[NSNumber alloc] initWithBool:NO];
		NSDictionary* opts = [[NSDictionary alloc] initWithObjectsAndKeys:num, kCIContextUseSoftwareRenderer, nil];
		CIContext* ciContext = [CIContext contextWithOptions:opts];
        
        //Filter by sepia
		CIImage* ciImage = [[CIImage alloc] initWithCGImage:self.CGImage];
		CIImage* output = [CIFilter filterWithName:@"CISepiaTone" keysAndValues:kCIInputImageKey, ciImage, @"inputIntensity", [NSNumber numberWithFloat:1.0f], nil].outputImage;
		CGImageRef cgImage = [ciContext createCGImage:output fromRect:[output extent]];
		UIImage* sepia = [UIImage imageWithCGImage:cgImage];
        
        //Cleanup
		CGImageRelease(cgImage);
		return sepia;
	}
	else 
	{
		// faster than before */
		const size_t width = self.size.width;
		const size_t height = self.size.height;
        const size_t bytesPerRow = width * 4;

        /// Create an ARGB bitmap context
		CGContextRef bmContext = createARGBBitmapContext(width, height, bytesPerRow);
		if (!bmContext) 
			return nil;
        
		/// Draw the image in the bitmap context
		CGContextDrawImage(bmContext, (CGRect){.origin.x = 0.0f, .origin.y = 0.0f, .size.width = width, .size.height = height}, self.CGImage);
        
		/// Grab the image raw data
		UInt8* data = (UInt8*)CGBitmapContextGetData(bmContext);
		if (!data)
		{
			CGContextRelease(bmContext);
			return nil;
		}
        
        //init some temp arrays to calculate.
		const size_t pixelsCount = width * height;
		const size_t n = sizeof(float) * pixelsCount;
		float* reds = (float*)malloc(n);
		float* greens = (float*)malloc(n);
		float* blues = (float*)malloc(n);
		float* tmpRed = (float*)malloc(n);
		float* tmpGreen = (float*)malloc(n);
		float* tmpBlue = (float*)malloc(n);
		float* finalRed = (float*)malloc(n);
		float* finalGreen = (float*)malloc(n);
		float* finalBlue = (float*)malloc(n);
        
        float sepiaFactorRedRed = 0.393f;
        float sepiaFactorRedGreen = 0.349f;
        float sepiaFactorRedBlue = 0.272f;
        float sepiaFactorGreenRed = 0.769f;
        float sepiaFactorGreenGreen = 0.686f;
        float sepiaFactorGreenBlue = 0.534f;
        float sepiaFactorBlueRed = 0.189f;
        float sepiaFactorBlueGreen = 0.168f;
        float sepiaFactorBlueBlue = 0.131f;
        
		/// Convert byte components to float
		vDSP_vfltu8(data + 1, 4, reds, 1, pixelsCount);
		vDSP_vfltu8(data + 2, 4, greens, 1, pixelsCount);
		vDSP_vfltu8(data + 3, 4, blues, 1, pixelsCount);
        
		/// Calculate red components
		vDSP_vsmul(reds, 1, &sepiaFactorRedRed, tmpRed, 1, pixelsCount);
		vDSP_vsmul(greens, 1, &sepiaFactorGreenRed, tmpGreen, 1, pixelsCount);
		vDSP_vsmul(blues, 1, &sepiaFactorBlueRed, tmpBlue, 1, pixelsCount);
		vDSP_vadd(tmpRed, 1, tmpGreen, 1, finalRed, 1, pixelsCount);
		vDSP_vadd(finalRed, 1, tmpBlue, 1, finalRed, 1, pixelsCount);
		vDSP_vclip(finalRed, 1, &MIN_COLOR, &MAX_COLOR, finalRed, 1, pixelsCount);
		vDSP_vfixu8(finalRed, 1, data + 1, 4, pixelsCount);
        
		/// Calculate green components
		vDSP_vsmul(reds, 1, &sepiaFactorRedGreen, tmpRed, 1, pixelsCount);
		vDSP_vsmul(greens, 1, &sepiaFactorGreenGreen, tmpGreen, 1, pixelsCount);
		vDSP_vsmul(blues, 1, &sepiaFactorBlueGreen, tmpBlue, 1, pixelsCount);
		vDSP_vadd(tmpRed, 1, tmpGreen, 1, finalGreen, 1, pixelsCount);
		vDSP_vadd(finalGreen, 1, tmpBlue, 1, finalGreen, 1, pixelsCount);
		vDSP_vclip(finalGreen, 1, &MIN_COLOR, &MAX_COLOR, finalGreen, 1, pixelsCount);
		vDSP_vfixu8(finalGreen, 1, data + 2, 4, pixelsCount);
        
		/// Calculate blue components
		vDSP_vsmul(reds, 1, &sepiaFactorRedBlue, tmpRed, 1, pixelsCount);
		vDSP_vsmul(greens, 1, &sepiaFactorGreenBlue, tmpGreen, 1, pixelsCount);
		vDSP_vsmul(blues, 1, &sepiaFactorBlueBlue, tmpBlue, 1, pixelsCount);
		vDSP_vadd(tmpRed, 1, tmpGreen, 1, finalBlue, 1, pixelsCount);
		vDSP_vadd(finalBlue, 1, tmpBlue, 1, finalBlue, 1, pixelsCount);
		vDSP_vclip(finalBlue, 1, &MIN_COLOR, &MAX_COLOR, finalBlue, 1, pixelsCount);
		vDSP_vfixu8(finalBlue, 1, data + 3, 4, pixelsCount);
        
		/// Create an image object from the context
		CGImageRef sepiaImageRef = CGBitmapContextCreateImage(bmContext);
		UIImage* sepia = [UIImage imageWithCGImage:sepiaImageRef];
        
		/// Cleanup
		CGImageRelease(sepiaImageRef);
		free(reds), free(greens), free(blues), free(tmpRed), free(tmpGreen), free(tmpBlue), free(finalRed), free(finalGreen), free(finalBlue);
		CGContextRelease(bmContext);
		return sepia;
	}

}


- (UIImage*) posterize:(int)value {
    float fvalue = (float)value;
    const size_t width = self.size.width;
    const size_t height = self.size.height;
    const size_t bytesPerRow = width * 4;
    
    /// Create an ARGB bitmap context
    CGContextRef bmContext = createARGBBitmapContext(width, height, bytesPerRow);
    if (!bmContext) 
        return nil;
    
    /// Draw the image in the bitmap context
    CGContextDrawImage(bmContext, (CGRect){.origin.x = 0.0f, .origin.y = 0.0f, .size.width = width, .size.height = height}, self.CGImage);
    
    /// Grab the image raw data
    UInt8* data = (UInt8*)CGBitmapContextGetData(bmContext);
    if (!data)
    {
        CGContextRelease(bmContext);
        return nil;
    }
    
    //init some temp arrays to calculate.
    const size_t pixelsCount = width * height;
	float* dataAsFloat = (float*)malloc(sizeof(float) * pixelsCount);
    const int iPixels = (int)pixelsCount;
    
    /// Calculate red components
	vDSP_vfltu8(data + 1, 4, dataAsFloat, 1, pixelsCount);
	vDSP_vsdiv(dataAsFloat, 1, &fvalue, dataAsFloat, 1, pixelsCount);
    vDSP_vsmul(dataAsFloat, 1, &fvalue, dataAsFloat, 1, pixelsCount);
    vvfloorf(dataAsFloat, dataAsFloat, &iPixels);
	vDSP_vclip(dataAsFloat, 1, &MIN_COLOR, &MAX_COLOR, dataAsFloat, 1, pixelsCount);
	vDSP_vfixu8(dataAsFloat, 1, data + 1, 4, pixelsCount);
    
	/// Calculate green components
	vDSP_vfltu8(data + 2, 4, dataAsFloat, 1, pixelsCount);
	vDSP_vsdiv(dataAsFloat, 1, &fvalue, dataAsFloat, 1, pixelsCount);
    vDSP_vsmul(dataAsFloat, 1, &fvalue, dataAsFloat, 1, pixelsCount);
    vvfloorf(dataAsFloat, dataAsFloat, &iPixels);
	vDSP_vclip(dataAsFloat, 1, &MIN_COLOR, &MAX_COLOR, dataAsFloat, 1, pixelsCount);
	vDSP_vfixu8(dataAsFloat, 1, data + 2, 4, pixelsCount);

    
	/// Calculate blue components
	vDSP_vfltu8(data + 3, 4, dataAsFloat, 1, pixelsCount);
	vDSP_vsdiv(dataAsFloat, 1, &fvalue, dataAsFloat, 1, pixelsCount);
    vDSP_vsmul(dataAsFloat, 1, &fvalue, dataAsFloat, 1, pixelsCount);
    vvfloorf(dataAsFloat, dataAsFloat, &iPixels);
	vDSP_vclip(dataAsFloat, 1, &MIN_COLOR, &MAX_COLOR, dataAsFloat, 1, pixelsCount);
	vDSP_vfixu8(dataAsFloat, 1, data + 3, 4, pixelsCount);
    
	CGImageRef posterizeImageRef = CGBitmapContextCreateImage(bmContext);
	UIImage* posterizeImage = [UIImage imageWithCGImage:posterizeImageRef];
    
	/// Cleanup
	CGImageRelease(posterizeImageRef);
	free(dataAsFloat);
	CGContextRelease(bmContext);

	return posterizeImage;
}



- (UIImage*)saturation:(float)value {
    const size_t width = self.size.width;
    const size_t height = self.size.height;
    const size_t bytesPerRow = width * 4;
    
    /// Create an ARGB bitmap context
    CGContextRef bmContext = createARGBBitmapContext(width, height, bytesPerRow);
    if (!bmContext) 
        return nil;
    
    /// Draw the image in the bitmap context
    CGContextDrawImage(bmContext, (CGRect){.origin.x = 0.0f, .origin.y = 0.0f, .size.width = width, .size.height = height}, self.CGImage);
    
    /// Grab the image raw data
    UInt8* data = (UInt8*)CGBitmapContextGetData(bmContext);
    if (!data)
    {
        CGContextRelease(bmContext);
        return nil;
    }
    
	const size_t bitmapByteCount = self.size.width * 4 * self.size.height;
	for (size_t i = 0; i < bitmapByteCount; i += 4)
	{
        UInt8 r = data[i + 1];
        UInt8 g = data[i + 2];
        UInt8 b = data[i + 3];
        UInt8 avg =  (r + g + b) / 3;
        
        NSInteger newRed = SAFECOLOR(avg + value * (r - avg));
        NSInteger newGreen = SAFECOLOR(avg + value * (g - avg));
        NSInteger newBlue = SAFECOLOR(avg + value * (b - avg));
		data[i + 1] = (UInt8)newRed;
		data[i + 2] = (UInt8)newGreen;
		data[i + 3] = (UInt8)newBlue;
	}
    
	/// Create an image object from the context
	CGImageRef sepiaImageRef = CGBitmapContextCreateImage(bmContext);
	UIImage* sepia = [UIImage imageWithCGImage:sepiaImageRef];
    
	/// Cleanup
	CGImageRelease(sepiaImageRef);
	CGContextRelease(bmContext);
	return sepia;
}


- (UIImage*)saturate:(float)value {
    const size_t width = self.size.width;
    const size_t height = self.size.height;
    const size_t bytesPerRow = width * 4;
    
    /// Create an ARGB bitmap context
    CGContextRef bmContext = createARGBBitmapContext(width, height, bytesPerRow);
    if (!bmContext) 
        return nil;
    
    /// Draw the image in the bitmap context
    CGContextDrawImage(bmContext, (CGRect){.origin.x = 0.0f, .origin.y = 0.0f, .size.width = width, .size.height = height}, self.CGImage);
    
    /// Grab the image raw data
    UInt8* data = (UInt8*)CGBitmapContextGetData(bmContext);
    if (!data)
    {
        CGContextRelease(bmContext);
        return nil;
    }
    
    //init some temp arrays to calculate.
    const size_t pixelsCount = width * height;
    const size_t n = sizeof(float) * pixelsCount;
    float* reds = (float*)malloc(n);
    float* greens = (float*)malloc(n);
    float* blues = (float*)malloc(n);
    float* avgs = (float*)malloc(n);
    float* tmpRed = (float*)malloc(n);
    float* tmpGreen = (float*)malloc(n);
    float* tmpBlue = (float*)malloc(n);
    float* finalRed = (float*)malloc(n);
    float* finalGreen = (float*)malloc(n);
    float* finalBlue = (float*)malloc(n);
    float rate = 3.0f;
    //Calculate Average of r,g,b
    vDSP_vfltu8(data + 1, 4, reds, 1, pixelsCount);
    vDSP_vfltu8(data + 2, 4, greens, 1, pixelsCount);
    vDSP_vfltu8(data + 3, 4, blues, 1, pixelsCount);
    vDSP_vadd(reds, 1, greens, 1, tmpRed, 1, pixelsCount);
    vDSP_vadd(tmpRed, 1, blues, 1, tmpRed, 1, pixelsCount);
    vDSP_vsdiv(tmpRed, 1, &rate, avgs, 1, pixelsCount);

    /// Calculate red components
    vDSP_vsub(reds, 1, avgs, 1, tmpRed, 1, pixelsCount);
    vDSP_vsmul(tmpRed, 1, &value, tmpRed, 1, pixelsCount);
    vDSP_vadd(tmpRed, 1, avgs, 1, finalRed, 1, pixelsCount);
	vDSP_vclip(finalRed, 1, &MIN_COLOR, &MAX_COLOR, finalRed, 1, pixelsCount);
	vDSP_vfixu8(finalRed, 1, data + 1, 4, pixelsCount);
    
	/// Calculate green components
	vDSP_vsub(greens, 1, avgs, 1, tmpGreen, 1, pixelsCount);
    vDSP_vsmul(tmpGreen, 1, &value, tmpGreen, 1, pixelsCount);
    vDSP_vadd(tmpGreen, 1, avgs, 1, finalGreen, 1, pixelsCount);
	vDSP_vclip(finalGreen, 1, &MIN_COLOR, &MAX_COLOR, finalGreen, 1, pixelsCount);
	vDSP_vfixu8(finalGreen, 1, data + 2, 4, pixelsCount);
    
    
	/// Calculate blue components
	vDSP_vsub(blues, 1, avgs, 1, tmpBlue, 1, pixelsCount);
    vDSP_vsmul(tmpBlue, 1, &value, tmpBlue, 1, pixelsCount);
    vDSP_vadd(tmpBlue, 1, avgs, 1, finalBlue, 1, pixelsCount);
	vDSP_vclip(finalBlue, 1, &MIN_COLOR, &MAX_COLOR, finalBlue, 1, pixelsCount);
	vDSP_vfixu8(finalBlue, 1, data + 3, 4, pixelsCount);

    
	CGImageRef saturateImageRef = CGBitmapContextCreateImage(bmContext);
    UIImage* saturateImage = [UIImage imageWithCGImage:saturateImageRef];
    
    /// Cleanup
    CGImageRelease(saturateImageRef);
    free(reds), free(greens), free(blues), free(tmpRed), free(tmpGreen), free(tmpBlue), free(finalRed), free(finalGreen), free(finalBlue), free(avgs);
    CGContextRelease(bmContext);
    return saturateImage;
}



- (UIImage*)brightness:(float)value {
    const size_t width = self.size.width;
    const size_t height = self.size.height;
    const size_t bytesPerRow = width * 4;
    
    /// Create an ARGB bitmap context
    CGContextRef bmContext = createARGBBitmapContext(width, height, bytesPerRow);
    if (!bmContext) 
        return nil;
    if (!bmContext) 
        return nil;
    
    /// Draw the image in the bitmap context
    CGContextDrawImage(bmContext, (CGRect){.origin.x = 0.0f, .origin.y = 0.0f, .size.width = width, .size.height = height}, self.CGImage);
    
    /// Grab the image raw data
    UInt8* data = (UInt8*)CGBitmapContextGetData(bmContext);
	if (!data)
	{
		CGContextRelease(bmContext);
		return nil;
	}
    
	const size_t pixelsCount = width * height;
	float* dataAsFloat = (float*)malloc(sizeof(float) * pixelsCount);
	/// Calculate red components
	vDSP_vfltu8(data + 1, 4, dataAsFloat, 1, pixelsCount);
	vDSP_vsadd(dataAsFloat, 1, &value, dataAsFloat, 1, pixelsCount);
	vDSP_vclip(dataAsFloat, 1, &MIN_COLOR, &MAX_COLOR, dataAsFloat, 1, pixelsCount);
	vDSP_vfixu8(dataAsFloat, 1, data + 1, 4, pixelsCount);
    
	/// Calculate green components
	vDSP_vfltu8(data + 2, 4, dataAsFloat, 1, pixelsCount);
	vDSP_vsadd(dataAsFloat, 1, &value, dataAsFloat, 1, pixelsCount);
	vDSP_vclip(dataAsFloat, 1, &MIN_COLOR, &MAX_COLOR, dataAsFloat, 1, pixelsCount);
	vDSP_vfixu8(dataAsFloat, 1, data + 2, 4, pixelsCount);
    
	/// Calculate blue components
	vDSP_vfltu8(data + 3, 4, dataAsFloat, 1, pixelsCount);
	vDSP_vsadd(dataAsFloat, 1, &value, dataAsFloat, 1, pixelsCount);
	vDSP_vclip(dataAsFloat, 1, &MIN_COLOR, &MAX_COLOR, dataAsFloat, 1, pixelsCount);
	vDSP_vfixu8(dataAsFloat, 1, data + 3, 4, pixelsCount);
    
	CGImageRef brightenedImageRef = CGBitmapContextCreateImage(bmContext);
	UIImage* brightened = [UIImage imageWithCGImage:brightenedImageRef];
    
	/// Cleanup
	CGImageRelease(brightenedImageRef);
	free(dataAsFloat);
	CGContextRelease(bmContext);
    
	return brightened;
}


- (UIImage*)gamma:(float)value {
    const size_t width = self.size.width;
    const size_t height = self.size.height;
    const size_t bytesPerRow = width * 4;
    
    /// Create an ARGB bitmap context
    CGContextRef bmContext = createARGBBitmapContext(width, height, bytesPerRow);
    if (!bmContext) 
        return nil;
    if (!bmContext) 
        return nil;
    
    /// Draw the image in the bitmap context
    CGContextDrawImage(bmContext, (CGRect){.origin.x = 0.0f, .origin.y = 0.0f, .size.width = width, .size.height = height}, self.CGImage);
    
    /// Grab the image raw data
    UInt8* data = (UInt8*)CGBitmapContextGetData(bmContext);
	if (!data)
	{
		CGContextRelease(bmContext);
		return nil;
	}
    
	const size_t pixelsCount = width * height;
	float* dataAsFloat = (float*)malloc(sizeof(float) * pixelsCount);
    const int iPixels = (int)pixelsCount;
    
	/// Calculate red components
	vDSP_vfltu8(data + 1, 4, dataAsFloat, 1, pixelsCount);
	vvpowf(dataAsFloat, &value, dataAsFloat, &iPixels);
	vDSP_vclip(dataAsFloat, 1, &MIN_COLOR, &MAX_COLOR, dataAsFloat, 1, pixelsCount);
	vDSP_vfixu8(dataAsFloat, 1, data + 1, 4, pixelsCount);
    
	/// Calculate green components
	vDSP_vfltu8(data + 2, 4, dataAsFloat, 1, pixelsCount);
	vvpowf(dataAsFloat, &value, dataAsFloat, &iPixels);
	vDSP_vclip(dataAsFloat, 1, &MIN_COLOR, &MAX_COLOR, dataAsFloat, 1, pixelsCount);
	vDSP_vfixu8(dataAsFloat, 1, data + 2, 4, pixelsCount);
    
	/// Calculate blue components
	vDSP_vfltu8(data + 3, 4, dataAsFloat, 1, pixelsCount);
	vvpowf(dataAsFloat, &value, dataAsFloat, &iPixels);
	vDSP_vclip(dataAsFloat, 1, &MIN_COLOR, &MAX_COLOR, dataAsFloat, 1, pixelsCount);
	vDSP_vfixu8(dataAsFloat, 1, data + 3, 4, pixelsCount);
    
	CGImageRef gammaImageRef = CGBitmapContextCreateImage(bmContext);
	UIImage* gammaImage = [UIImage imageWithCGImage:gammaImageRef];
    
	/// Cleanup
	CGImageRelease(gammaImageRef);
	free(dataAsFloat);
	CGContextRelease(bmContext);
    
	return gammaImage;

    
}


- (UIImage*) opacity:(double)amount {
    
}


- (UIImage*) contrast:(float)value {
    /// Create an ARGB bitmap context
	const size_t width = self.size.width;
	const size_t height = self.size.height;
    const size_t bytesPerRow = width * 4;

	CGContextRef bmContext = createARGBBitmapContext(width, height, width * bytesPerRow);
	if (!bmContext) 
		return nil;
    
	/// Draw the image in the bitmap context
	CGContextDrawImage(bmContext, (CGRect){.origin.x = 0.0f, .origin.y = 0.0f, .size.width = width, .size.height = height}, self.CGImage);
    
	/// Grab the image raw data
	UInt8* data = (UInt8*)CGBitmapContextGetData(bmContext);
	if (!data)
	{
		CGContextRelease(bmContext);
		return nil;
	}
    
	const size_t pixelsCount = width * height;
	float* dataAsFloat = (float*)malloc(sizeof(float) * pixelsCount);
    
	/// Contrast correction factor
	const float factor = (259.0f * (value + 255.0f)) / (255.0f * (259.0f - value));
    
	float v1 = -128.0f, v2 = 128.0f;
    
	/// Calculate red components
	vDSP_vfltu8(data + 1, 4, dataAsFloat, 1, pixelsCount);
	vDSP_vsadd(dataAsFloat, 1, &v1, dataAsFloat, 1, pixelsCount);
	vDSP_vsmul(dataAsFloat, 1, &factor, dataAsFloat, 1, pixelsCount);
	vDSP_vsadd(dataAsFloat, 1, &v2, dataAsFloat, 1, pixelsCount);
	vDSP_vclip(dataAsFloat, 1, &MIN_COLOR, &MAX_COLOR, dataAsFloat, 1, pixelsCount);
	vDSP_vfixu8(dataAsFloat, 1, data + 1, 4, pixelsCount);
    
	/// Calculate green components
	vDSP_vfltu8(data + 2, 4, dataAsFloat, 1, pixelsCount);
	vDSP_vsadd(dataAsFloat, 1, &v1, dataAsFloat, 1, pixelsCount);
	vDSP_vsmul(dataAsFloat, 1, &factor, dataAsFloat, 1, pixelsCount);
	vDSP_vsadd(dataAsFloat, 1, &v2, dataAsFloat, 1, pixelsCount);
	vDSP_vclip(dataAsFloat, 1, &MIN_COLOR, &MAX_COLOR, dataAsFloat, 1, pixelsCount);
	vDSP_vfixu8(dataAsFloat, 1, data + 2, 4, pixelsCount);
    
	/// Calculate blue components
	vDSP_vfltu8(data + 3, 4, dataAsFloat, 1, pixelsCount);
	vDSP_vsadd(dataAsFloat, 1, &v1, dataAsFloat, 1, pixelsCount);
	vDSP_vsmul(dataAsFloat, 1, &factor, dataAsFloat, 1, pixelsCount);
	vDSP_vsadd(dataAsFloat, 1, &v2, dataAsFloat, 1, pixelsCount);
	vDSP_vclip(dataAsFloat, 1, &MIN_COLOR, &MAX_COLOR, dataAsFloat, 1, pixelsCount);
	vDSP_vfixu8(dataAsFloat, 1, data + 3, 4, pixelsCount);
    
	/// Create an image object from the context
	CGImageRef contrastedImageRef = CGBitmapContextCreateImage(bmContext);
	UIImage* contrasted = [UIImage imageWithCGImage:contrastedImageRef];
    
	/// Cleanup
	CGImageRelease(contrastedImageRef);
	free(dataAsFloat);
	CGContextRelease(bmContext);
    
	return contrasted;

}


- (UIImage*)bias:(double)amount {
    
}


- (UIImage*) fill:(double)r green:(double)g blue:(double)b {
    
}


- (UIImage *)negativeImage
{
    // get width and height as integers, since we'll be using them as
    // array subscripts, etc, and this'll save a whole lot of casting
    CGSize size = self.size;
    int width = size.width;
    int height = size.height;
    
    // Create a suitable RGB+alpha bitmap context in BGRA colour space
    CGColorSpaceRef colourSpace = CGColorSpaceCreateDeviceRGB();
    unsigned char *memoryPool = (unsigned char *)calloc(width*height*4, 1);
    CGContextRef context = CGBitmapContextCreate(memoryPool, width, height, 8, width * 4, colourSpace, kCGBitmapByteOrder32Big | kCGImageAlphaPremultipliedLast);
    CGColorSpaceRelease(colourSpace);
    
    // draw the current image to the newly created context
    CGContextDrawImage(context, CGRectMake(0, 0, width, height), [self CGImage]);
    
    // run through every pixel, a scan line at a time...
    for(int y = 0; y < height; y++)
    {
        // get a pointer to the start of this scan line
        unsigned char *linePointer = &memoryPool[y * width * 4];
        
        // step through the pixels one by one...
        for(int x = 0; x < width; x++)
        {
            // get RGB values. We're dealing with premultiplied alpha
            // here, so we need to divide by the alpha channel (if it
            // isn't zero, of course) to get uninflected RGB. We
            // multiply by 255 to keep precision while still using
            // integers
            int r, g, b; 
            if(linePointer[3])
            {
                r = linePointer[0] * 255 / linePointer[3];
                g = linePointer[1] * 255 / linePointer[3];
                b = linePointer[2] * 255 / linePointer[3];
            }
            else
                r = g = b = 0;
            
            // perform the colour inversion
            r = 255 - r;
            g = 255 - g;
            b = 255 - b;
            
            // multiply by alpha again, divide by 255 to undo the
            // scaling before, store the new values and advance
            // the pointer we're reading pixel data from
            linePointer[0] = r * linePointer[3] / 255;
            linePointer[1] = g * linePointer[3] / 255;
            linePointer[2] = b * linePointer[3] / 255;
            linePointer += 4;
        }
    }
    
    // get a CG image from the context, wrap that into a
    // UIImage
    CGImageRef cgImage = CGBitmapContextCreateImage(context);
    UIImage *returnImage = [UIImage imageWithCGImage:cgImage];
    
    // clean up
    CGImageRelease(cgImage);
    CGContextRelease(context);
    free(memoryPool);
    
    // and return
    return returnImage;
}


-(UIImage*)invert
{
	/// Create an ARGB bitmap context
	const size_t width = self.size.width;
	const size_t height = self.size.height;
    const size_t bytesPerRow = width * 4;
    
	CGContextRef bmContext = createARGBBitmapContext(width, height, width * bytesPerRow);
	if (!bmContext) 
		return nil;
    
	/// Draw the image in the bitmap context
	CGContextDrawImage(bmContext, (CGRect){.origin.x = 0.0f, .origin.y = 0.0f, .size.width = width, .size.height = height}, self.CGImage);
    
	/// Grab the image raw data
	UInt8* data = (UInt8*)CGBitmapContextGetData(bmContext);
	if (!data)
	{
		CGContextRelease(bmContext);
		return nil;
	}
    
	const size_t pixelsCount = width * height;
	float* dataAsFloat = (float*)malloc(sizeof(float) * pixelsCount);
	UInt8* dataRed = data + 1;
	UInt8* dataGreen = data + 2;
	UInt8* dataBlue = data + 3;
    float negativeMultiplier = -1;
	/// vDSP_vsmsa() = multiply then add
	/// slightly faster than the couple vDSP_vneg() & vDSP_vsadd()
	/// Probably because there are 3 function calls less
    
	/// Calculate red components
	vDSP_vfltu8(dataRed, 4, dataAsFloat, 1, pixelsCount);
	vDSP_vsmsa(dataAsFloat, 1, &negativeMultiplier, &MAX_COLOR, dataAsFloat, 1, pixelsCount);
	vDSP_vclip(dataAsFloat, 1, &MIN_COLOR, &MAX_COLOR, dataAsFloat, 1, pixelsCount);
	vDSP_vfixu8(dataAsFloat, 1, dataRed, 4, pixelsCount);
    
	/// Calculate green components
	vDSP_vfltu8(dataGreen, 4, dataAsFloat, 1, pixelsCount);
	vDSP_vsmsa(dataAsFloat, 1, &negativeMultiplier, &MAX_COLOR, dataAsFloat, 1, pixelsCount);
	vDSP_vclip(dataAsFloat, 1, &MIN_COLOR, &MAX_COLOR, dataAsFloat, 1, pixelsCount);
	vDSP_vfixu8(dataAsFloat, 1, dataGreen, 4, pixelsCount);
    
	/// Calculate blue components
	vDSP_vfltu8(dataBlue, 4, dataAsFloat, 1, pixelsCount);
	vDSP_vsmsa(dataAsFloat, 1, &negativeMultiplier, &MAX_COLOR, dataAsFloat, 1, pixelsCount);
	vDSP_vclip(dataAsFloat, 1, &MIN_COLOR, &MAX_COLOR, dataAsFloat, 1, pixelsCount);
	vDSP_vfixu8(dataAsFloat, 1, dataBlue, 4, pixelsCount);
    
	CGImageRef invertedImageRef = CGBitmapContextCreateImage(bmContext);
	UIImage* inverted = [UIImage imageWithCGImage:invertedImageRef];
    
	/// Cleanup
	CGImageRelease(invertedImageRef);
	free(dataAsFloat);
	CGContextRelease(bmContext);
    
	return inverted;
}


static float __f_gaussianblur_kernel_5x5[25] = { 
	1.0f/256.0f,  4.0f/256.0f,  6.0f/256.0f,  4.0f/256.0f, 1.0f/256.0f,
	4.0f/256.0f, 16.0f/256.0f, 24.0f/256.0f, 16.0f/256.0f, 4.0f/256.0f,
	6.0f/256.0f, 24.0f/256.0f, 36.0f/256.0f, 24.0f/256.0f, 6.0f/256.0f,
	4.0f/256.0f, 16.0f/256.0f, 24.0f/256.0f, 16.0f/256.0f, 4.0f/256.0f,
	1.0f/256.0f,  4.0f/256.0f,  6.0f/256.0f,  4.0f/256.0f, 1.0f/256.0f
};

static int16_t __s_gaussianblur_kernel_5x5[25] = {
	1, 4, 6, 4, 1, 
	4, 16, 24, 16, 4,
	6, 24, 36, 24, 6,
	4, 16, 24, 16, 4,
	1, 4, 6, 4, 1
};



-(UIImage*)gaussianBlur:(NSInteger)radius
{
	/// Create an ARGB bitmap context
	const size_t width = self.size.width;
    const size_t height = self.size.height;
    const size_t bytesPerRow = width * 4;

    /// Create an ARGB bitmap context
    CGContextRef bmContext = createARGBBitmapContext(width, height, bytesPerRow);
    if (!bmContext) 
        return nil;    
	/// Draw the image in the bitmap context
	CGContextDrawImage(bmContext, (CGRect){.origin.x = 0.0f, .origin.y = 0.0f, .size.width = width, .size.height = height}, self.CGImage); 
    
	/// Grab the image raw data
	UInt8* data = (UInt8*)CGBitmapContextGetData(bmContext);
	if (!data)
	{
		CGContextRelease(bmContext);
		return nil;
	}
    
    int matrixDimension = (radius*2)+1;
    float sigma = radius/3;
    float twoSigmaSquared = 2*pow(sigma, 2);
    float oneOverSquareRootOfTwoPiSigmaSquared = 1/(sqrt(M_PI*twoSigmaSquared));
    
    float kernel[matrixDimension];
    
    int index = 0;
    for (int offset = -radius; offset <= radius; offset++) {
        
        float xSquared = pow(offset, 2);
        float exponent = -(xSquared/twoSigmaSquared);
        float eToThePower = pow(M_E, exponent);
        float multFactor = oneOverSquareRootOfTwoPiSigmaSquared*eToThePower;
        
        kernel[index] = multFactor;
        
        index++;
    }
    
    //Normalize the kernel such that all its values will add to 1
    float sum = 0;
    for (int i = 0; i < matrixDimension; i++) {
        sum += kernel[i];
    }
    for (int i = 0; i < matrixDimension; i++) {
        kernel[i] = kernel[i]/sum;
    }

	/// vImage (iOS 5)
	if ((&vImageConvolveWithBias_ARGB8888))
	{
		const size_t n = sizeof(UInt8) * width * height * 4;
		void* outt = malloc(n);
		vImage_Buffer src = {data, height, width, bytesPerRow};
		vImage_Buffer dest = {outt, height, width, bytesPerRow};
		vImageConvolveWithBias_ARGB8888(&src, &dest, NULL, 0, 0, __s_gaussianblur_kernel_5x5, 5, 5, 256/*divisor*/, radius, NULL, kvImageCopyInPlace);
		memcpy(data, outt, n);
		free(outt);
	}
	else
	{
		const size_t pixelsCount = width * height;
		const size_t n = sizeof(float) * pixelsCount;
		float* dataAsFloat = malloc(n);
		float* resultAsFloat = malloc(n);
        
		/// Red components
		vDSP_vfltu8(data + 1, 4, dataAsFloat, 1, pixelsCount);
		vDSP_f5x5(dataAsFloat, height, width, __f_gaussianblur_kernel_5x5, resultAsFloat);
		vDSP_vfixu8(resultAsFloat, 1, data + 1, 4, pixelsCount);
        
		/// Green components
		vDSP_vfltu8(data + 2, 4, dataAsFloat, 1, pixelsCount);
		vDSP_f5x5(dataAsFloat, height, width, __f_gaussianblur_kernel_5x5, resultAsFloat);
		vDSP_vfixu8(resultAsFloat, 1, data + 2, 4, pixelsCount);
        
		/// Blue components
		vDSP_vfltu8(data + 3, 4, dataAsFloat, 1, pixelsCount);
        vDSP_imgfir(dataAsFloat, height, width, kernel, resultAsFloat, 1, 1);
		vDSP_f5x5(dataAsFloat, height, width, __f_gaussianblur_kernel_5x5, resultAsFloat);
		vDSP_vfixu8(resultAsFloat, 1, data + 3, 4, pixelsCount);
        
		free(resultAsFloat);
		free(dataAsFloat);
	}
    
	CGImageRef blurredImageRef = CGBitmapContextCreateImage(bmContext);
	UIImage* blurred = [UIImage imageWithCGImage:blurredImageRef];
    
	/// Cleanup
	CGImageRelease(blurredImageRef);
	CGContextRelease(bmContext);
    
	return blurred;
}


typedef struct
{
	CurveChannel channel;
	CGPoint *points;
	int length;
} CurveEquation;


double valueGivenCurve(CurveEquation equation, double xValue)
{
	assert(xValue <= 255);
	assert(xValue >= 0);
	
	CGPoint point1 = CGPointZero;
	CGPoint point2 = CGPointZero;
	NSInteger idx = 0;
	
	for (idx = 0; idx < equation.length; idx++)
	{
		CGPoint point = equation.points[idx];
		if (xValue < point.x)
		{
			point2 = point;
			if (idx - 1 >= 0)
			{
				point1 = equation.points[idx-1];
			}
			else
			{
				point1 = point2;
			}
			
			break;
		}		
	}
	
	double m = (point2.y - point1.y)/(point2.x - point1.x);
	double b = point2.y - (m * point2.x);
	double y = m * xValue + b;
	return y;
}


- (void)curveAjustment:(CGContextRef)bmContext equation:(CurveEquation)equation {
    UInt8* data = (UInt8*)CGBitmapContextGetData(bmContext);
	if (!data) {
		CGContextRelease(bmContext);
        return;
	}
    
	const size_t bitmapByteCount = self.size.width * 4 * self.size.height;
	for (size_t i = 0; i < bitmapByteCount; i += 4) {
        
        int red = data[i + 1];
        int green = data[i + 2];
        int blue = data[i + 3];
        
        red = equation.channel & CurveChannelRed ? valueGivenCurve(equation, red) : red;
        green = equation.channel & CurveChannelGreen ? valueGivenCurve(equation, green) : green;
        blue = equation.channel & CurveChannelBlue ? valueGivenCurve(equation, blue) : blue;
        
        data[i + 1] = SAFECOLOR(red);
        data[i + 2] = SAFECOLOR(green);
        data[i + 3] = SAFECOLOR(blue);
	}  
}


- (UIImage*) applyCurve:(CurveEquation)equation {
    const size_t width = self.size.width;
    const size_t height = self.size.height;
    const size_t bytesPerRow = width * 4;
    
    /// Create an ARGB bitmap context
    CGContextRef bmContext = createARGBBitmapContext(width, height, bytesPerRow);
    if (!bmContext) 
        return nil;    
	/// Draw the image in the bitmap context
	CGContextDrawImage(bmContext, (CGRect){.origin.x = 0.0f, .origin.y = 0.0f, .size.width = width, .size.height = height}, self.CGImage); 
    
	/// Grab the image raw data
    
	UInt8* data = (UInt8*)CGBitmapContextGetData(bmContext);
	if (!data) {
		CGContextRelease(bmContext);
		return nil;
	}
    
	const size_t bitmapByteCount = self.size.width * 4 * self.size.height;
	for (size_t i = 0; i < bitmapByteCount; i += 4) {
        
        int red = data[i + 1];
        int green = data[i + 2];
        int blue = data[i + 3];
        
        red = equation.channel & CurveChannelRed ? valueGivenCurve(equation, red) : red;
        green = equation.channel & CurveChannelGreen ? valueGivenCurve(equation, green) : green;
        blue = equation.channel & CurveChannelBlue ? valueGivenCurve(equation, blue) : blue;
        
        data[i + 1] = SAFECOLOR(red);
        data[i + 2] = SAFECOLOR(green);
        data[i + 3] = SAFECOLOR(blue);
        
	}  
	CGImageRef newImageRef = CGBitmapContextCreateImage(bmContext);
	UIImage* newImage = [UIImage imageWithCGImage:newImageRef];
	CGImageRelease(newImageRef);
	CGContextRelease(bmContext);
	return newImage;
}




- (void) applyCurve:(CGContextRef)context point:(NSArray*)points toChannel:(CurveChannel)channel
{
	assert([points count] > 1);
	
	CGPoint firstPoint = ((NSValue*)[points objectAtIndex:0]).CGPointValue;
	CatmullRomSpline *spline = [CatmullRomSpline catmullRomSplineAtPoint:firstPoint];	
	NSInteger idx = 0;
	NSInteger length = [points count];
	for (idx = 1; idx < length; idx++)
	{
		CGPoint point = ((NSValue*)[points objectAtIndex:idx]).CGPointValue;
		[spline addPoint:point];
	}		
	
	NSArray *splinePoints = [spline asPointArray];		
	length = [splinePoints count];
	CGPoint *cgPoints = malloc(sizeof(CGPoint) * length);
	memset(cgPoints, 0, sizeof(CGPoint) * length);
	for (idx = 0; idx < length; idx++)
	{
		CGPoint point = ((NSValue*)[splinePoints objectAtIndex:idx]).CGPointValue;
		cgPoints[idx].x = point.x;
		cgPoints[idx].y = point.y;
	}
	
	CurveEquation equation;
	equation.length = length;
	equation.points = cgPoints;	
	equation.channel = channel;
    
    
	[self curveAjustment:context equation:equation];	
	free(cgPoints);
}


- (UIImage*) maskImage:(UIImage *)maskImage {
	
	CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();
	CGImageRef maskImageRef = [maskImage CGImage];
	
	// create a bitmap graphics context the size of the image
	CGContextRef mainViewContentContext = CGBitmapContextCreate (NULL, maskImage.size.width, maskImage.size.height, 8, 0, colorSpace, kCGImageAlphaPremultipliedLast);
	
	if (mainViewContentContext == NULL)
		return NULL;
	
	CGFloat ratio = 0;
	ratio = maskImage.size.width/ self.size.width;
	
	if (ratio * self.size.height < maskImage.size.height) {
		ratio = maskImage.size.height/ self.size.height;
	} 
	
	CGRect rect1 = {
		{0, 0}, 
		{
            maskImage.size.width, maskImage.size.height
        }
	};
	
	CGRect rect2 = {
		{
			-((self.size.width*ratio)-maskImage.size.width)/2 , 
			-((self.size.height*ratio)-maskImage.size.height)/2}, 
		{
			self.size.width*ratio, self.size.height*ratio
		}
	};
	
	CGContextClipToMask(mainViewContentContext, rect1, maskImageRef);
	CGContextDrawImage(mainViewContentContext, rect2, self.CGImage);
	
	// Create CGImageRef of the main view bitmap content, and then
	// release that bitmap context
	CGImageRef newImage = CGBitmapContextCreateImage(mainViewContentContext) ;
	CGContextRelease(mainViewContentContext);
	UIImage *theImage = [UIImage imageWithCGImage:newImage];
	CGImageRelease(newImage);
	
	// return the image
	return theImage;
	
}


-(UIImage*)maskWithImage:(UIImage*)maskImage
{
	/// Create a bitmap context with valid alpha
	const size_t originalWidth = self.size.width;
	const size_t originalHeight = self.size.height;
	CGContextRef bmContext = createARGBBitmapContext(originalWidth, originalHeight, 0);
	if (!bmContext)
		return nil;
    
	/// Image quality
	CGContextSetShouldAntialias(bmContext, true);
	CGContextSetAllowsAntialiasing(bmContext, true);
	CGContextSetInterpolationQuality(bmContext, kCGInterpolationHigh);
    
	/// Image mask
	CGImageRef cgMaskImage = maskImage.CGImage; 
	CGImageRef mask = CGImageMaskCreate(maskImage.size.width, maskImage.size.height, CGImageGetBitsPerComponent(cgMaskImage), CGImageGetBitsPerPixel(cgMaskImage), CGImageGetBytesPerRow(cgMaskImage), CGImageGetDataProvider(cgMaskImage), NULL, false);
    
	/// Draw the original image in the bitmap context
	const CGRect r = (CGRect){.origin.x = 0.0f, .origin.y = 0.0f, .size.width = originalWidth, .size.height = originalHeight};
	CGContextClipToMask(bmContext, r, cgMaskImage);
	CGContextDrawImage(bmContext, r, self.CGImage);
    
	/// Get the CGImage object
	CGImageRef imageRefWithAlpha = CGBitmapContextCreateImage(bmContext);
	/// Apply the mask
	CGImageRef maskedImageRef = CGImageCreateWithMask(imageRefWithAlpha, mask);
    
	UIImage* result = [UIImage imageWithCGImage:maskedImageRef];
    
	/// Cleanup
	CGImageRelease(maskedImageRef);
	CGImageRelease(imageRefWithAlpha);
	CGContextRelease(bmContext);
	CGImageRelease(mask);
    
    return result;
}


- (UIImage *)noslagia {
    const size_t width = self.size.width;
    const size_t height = self.size.height;
    const size_t bytesPerRow = width * 4;
    
    /// Create an ARGB bitmap context
    CGContextRef bmContext = createARGBBitmapContext(width, height, bytesPerRow);
    if (!bmContext) 
        return nil;    
	/// Draw the image in the bitmap context
	CGContextDrawImage(bmContext, (CGRect){.origin.x = 0.0f, .origin.y = 0.0f, .size.width = width, .size.height = height}, self.CGImage); 
    
	/// Grab the image raw data
	UInt8* data = (UInt8*)CGBitmapContextGetData(bmContext);
	if (!data)
	{
		CGContextRelease(bmContext);
		return nil;
	}
    

    //Make guassian blur image
    UIImage *guassianImage = [self gaussianBlur:40];
    CGContextSetBlendMode (bmContext, kCGBlendModeSoftLight);
    CGContextSetAlpha(bmContext, 0.33f);
    CGContextDrawImage (bmContext, (CGRect){.origin.x = 0.0f, .origin.y = 0.0f, .size.width = width, .size.height = height}, guassianImage.CGImage);
    
    //Overlay film layer
    UIImage *filmImage = [UIImage imageNamed:@"fx-film-filmgrain.jpg"];
    CGContextSetBlendMode(bmContext, kCGBlendModeOverlay);
    CGContextSetAlpha(bmContext, 0.8f);
    CGContextDrawImage (bmContext, (CGRect){.origin.x = 0.0f, .origin.y = 0.0f, .size.width = width, .size.height = height}, filmImage.CGImage);
    

    //Mask stone image
    UIImage *stoneImage = [UIImage imageNamed:@"fx-film-nostalgia-stone.jpg"];
    UIImage *maskImage = [UIImage imageNamed:@"mask.png"];
    UIImage *filterImage = [stoneImage maskImage:maskImage];
    CGContextSetBlendMode(bmContext, kCGBlendModeOverlay);
    CGContextSetAlpha(bmContext, 0.45f);
    CGContextDrawImage (bmContext, (CGRect){.origin.x = 0.0f, .origin.y = 0.0f, .size.width = width, .size.height = height}, filterImage.CGImage);

    
    //Mask brown image
    UIImage *brownImage = [UIImage imageNamed:@"fx-film-nostalgia-brown.jpg"];
    UIImage* filterBrownImage = [brownImage maskImage:maskImage];
    CGContextSetBlendMode(bmContext, kCGBlendModeSoftLight);
    CGContextSetAlpha(bmContext, 0.8f);
    CGContextDrawImage (bmContext, (CGRect){.origin.x = 0.0f, .origin.y = 0.0f, .size.width = width, .size.height = height}, filterBrownImage.CGImage);

    //Dark vintage
    CGContextSetBlendMode(bmContext, kCGBlendModeOverlay);
    CGContextSetAlpha(bmContext, 0.5f);
	CGFloat blackComps[] = {0.0, 0.0, 0.0, 1.0,  0.0, 0.0, 0.0, 0.0};
    CGFloat locs[] = {0, 1};
    CGColorSpaceRef space = CGColorSpaceCreateDeviceRGB();
    CGGradientRef blackGradient = CGGradientCreateWithColorComponents(space, blackComps, locs, 2);
    CGContextDrawRadialGradient(bmContext, blackGradient, CGPointMake(self.size.width/2, self.size.height/2), 0, CGPointMake(self.size.width/2, self.size.height/2) , self.size.width/2, 0);
    CGGradientRelease(blackGradient);
    
    //White vintage
    CGContextSetBlendMode(bmContext, kCGBlendModeOverlay);
    CGContextSetAlpha(bmContext, 0.5f);
	CGFloat whiteComps[] = {1.0, 1.0, 1.0, 1.0,  1.0, 1.0, 1.0, 0.0};
    CGGradientRef whiteGradient = CGGradientCreateWithColorComponents(space, whiteComps, locs, 2);
    CGContextDrawRadialGradient(bmContext, whiteGradient, CGPointMake(self.size.width/2, self.size.height/2), 0, CGPointMake(self.size.width/2, self.size.height/2) , self.size.width/2, 0);
    CGColorSpaceRelease(space);
    CGGradientRelease(whiteGradient);

    //Saturate +30
    UIImage *saturateImage = [self saturation:1.3];
    CGContextSetBlendMode(bmContext, kCGBlendModeNormal);
    CGContextSetAlpha(bmContext, 0.4f);
    CGContextDrawImage (bmContext, (CGRect){.origin.x = 0.0f, .origin.y = 0.0f, .size.width = width, .size.height = height}, saturateImage.CGImage);
    
    //Vintage curve
    NSArray *redPoints = [NSArray arrayWithObjects:
                          [NSValue valueWithCGPoint:CGPointMake(0, 0)],
                          [NSValue valueWithCGPoint:CGPointMake(84, 52)],
                          [NSValue valueWithCGPoint:CGPointMake(177, 192)],
						  [NSValue valueWithCGPoint:CGPointMake(222, 255)],
                          [NSValue valueWithCGPoint:CGPointMake(255, 255)],
                          nil];
	NSArray *greenPoints = [NSArray arrayWithObjects:
                            [NSValue valueWithCGPoint:CGPointMake(0, 0)],
                            [NSValue valueWithCGPoint:CGPointMake(66, 60)],
                            [NSValue valueWithCGPoint:CGPointMake(180, 207)],
                            [NSValue valueWithCGPoint:CGPointMake(255, 255)],
                            nil];
	NSArray *bluePoints = [NSArray arrayWithObjects:
                           [NSValue valueWithCGPoint:CGPointMake(0, 27)],
                           [NSValue valueWithCGPoint:CGPointMake(255, 255)],
                           nil];

    [self applyCurve:bmContext point:redPoints toChannel:CurveChannelRed];
    [self applyCurve:bmContext point:greenPoints toChannel:CurveChannelGreen];
    [self applyCurve:bmContext point:bluePoints toChannel:CurveChannelBlue];
    
    //Fill with pink color
    CGContextSetBlendMode(bmContext, kCGBlendModeColor);
    CGContextSetAlpha(bmContext, 0.1f);
    CGContextSetFillColorWithColor(bmContext, [self colorWithInt:0xf21bef].CGColor);
    CGContextFillRect(bmContext, (CGRect){.origin.x = 0.0f, .origin.y = 0.0f, .size.width = width, .size.height = height});
    
    //Fill with yellow color
    CGContextSetBlendMode(bmContext, kCGBlendModeSoftLight);
    CGContextSetAlpha(bmContext, 0.2f);
    CGContextSetFillColorWithColor(bmContext, [self colorWithInt:0xfeebc3].CGColor);
    CGContextFillRect(bmContext, (CGRect){.origin.x = 0.0f, .origin.y = 0.0f, .size.width = width, .size.height = height});
    
    //Create result image
    CGImageRef blurredImageRef = CGBitmapContextCreateImage(bmContext);
	UIImage* blurred = [UIImage imageWithCGImage:blurredImageRef];
    
	/// Cleanup
	CGImageRelease(blurredImageRef);
	CGContextRelease(bmContext);
    
	return blurred;

}



- (UIImage *)pencilSketchFilter {
    const size_t width = self.size.width;
    const size_t height = self.size.height;
    const size_t bytesPerRow = width * 4;
    
    /// Create an ARGB bitmap context
    CGContextRef bmContext = createARGBBitmapContext(width, height, bytesPerRow);
    if (!bmContext) 
        return nil;    
	/// Draw the image in the bitmap context
	CGContextDrawImage(bmContext, (CGRect){.origin.x = 0.0f, .origin.y = 0.0f, .size.width = width, .size.height = height}, self.CGImage); 
    
	/// Grab the image raw data
	UInt8* data = (UInt8*)CGBitmapContextGetData(bmContext);
	if (!data)
	{
		CGContextRelease(bmContext);
		return nil;
	}
    
    //Black and white layer
    UIImage *grayImage = [self grayscale];
    CGContextSetBlendMode (bmContext, kCGBlendModeNormal);
    CGContextSetAlpha(bmContext, 1.0f);
    CGContextDrawImage (bmContext, (CGRect){.origin.x = 0.0f, .origin.y = 0.0f, .size.width = width, .size.height = height}, grayImage.CGImage);
    
    //Invert
    UIImage *invertImage = [[grayImage negativeImage] gaussianBlur:50];
    CGContextSetBlendMode (bmContext, kCGBlendModeColorDodge);
    CGContextSetAlpha(bmContext, 1.0f);
    CGContextDrawImage (bmContext, (CGRect){.origin.x = 0.0f, .origin.y = 0.0f, .size.width = width, .size.height = height}, invertImage.CGImage);

    //Create result image
    CGImageRef blurredImageRef = CGBitmapContextCreateImage(bmContext);
	UIImage* blurred = [UIImage imageWithCGImage:blurredImageRef];
    
	/// Cleanup
	CGImageRelease(blurredImageRef);
	CGContextRelease(bmContext);
    
	return blurred;

}


@end
