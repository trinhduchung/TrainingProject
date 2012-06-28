//
//  UIImage+OpenCV.mm
//  OpenCVClient
//
//  Created by Robin Summerhill on 02/09/2011.
//  Copyright 2011 Aptogo Limited. All rights reserved.
//
//  Permission is given to use this source code file without charge in any
//  project, commercial or otherwise, entirely at your risk, with the condition
//  that any redistribution (in part or whole) of source code must retain
//  this copyright and permission notice. Attribution in compiled projects is
//  appreciated but not required.
//

#import "UIImage+OpenCV.h"
static inline void premultiplyImage(IplImage *img, BOOL reverse);
static void releaseImage(void *info, const void *data, size_t size);
static void ProviderReleaseDataNOP(void *info, const void *data, size_t size)
{
    // Do not release memory
    return;
}



@implementation UIImage (UIImage_OpenCV)



-(cv::Mat)CVMat
{
    
    CGColorSpaceRef colorSpace = CGImageGetColorSpace(self.CGImage);
    CGFloat cols = self.size.width;
    CGFloat rows = self.size.height;
    
    cv::Mat cvMat(rows, cols, CV_8UC4); // 8 bits per component, 4 channels
    
    CGContextRef contextRef = CGBitmapContextCreate(cvMat.data,                 // Pointer to backing data
                                                    cols,                      // Width of bitmap
                                                    rows,                     // Height of bitmap
                                                    8,                          // Bits per component
                                                    cvMat.step[0],              // Bytes per row
                                                    colorSpace,                 // Colorspace
                                                    kCGImageAlphaNoneSkipLast |
                                                    kCGBitmapByteOrderDefault); // Bitmap info flags
    
    CGContextDrawImage(contextRef, CGRectMake(0, 0, cols, rows), self.CGImage);
    CGContextRelease(contextRef);
    
    return cvMat;
}

-(cv::Mat)CVGrayscaleMat
{
    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceGray();
    CGFloat cols = self.size.width;
    CGFloat rows = self.size.height;
    
    cv::Mat cvMat = cv::Mat(rows, cols, CV_8UC1); // 8 bits per component, 1 channel
 
    CGContextRef contextRef = CGBitmapContextCreate(cvMat.data,                 // Pointer to backing data
                                                    cols,                      // Width of bitmap
                                                    rows,                     // Height of bitmap
                                                    8,                          // Bits per component
                                                    cvMat.step[0],              // Bytes per row
                                                    colorSpace,                 // Colorspace
                                                    kCGImageAlphaNone |
                                                    kCGBitmapByteOrderDefault); // Bitmap info flags
    
    CGContextDrawImage(contextRef, CGRectMake(0, 0, cols, rows), self.CGImage);
    CGContextRelease(contextRef);
    CGColorSpaceRelease(colorSpace);
    
    return cvMat;
}

+ (UIImage *)imageWithCVMat:(const cv::Mat&)cvMat
{
    return [[[UIImage alloc] initWithCVMat:cvMat] autorelease];
}

- (id)initWithCVMat:(const cv::Mat&)cvMat
{
    NSData *data = [NSData dataWithBytes:cvMat.data length:cvMat.elemSize() * cvMat.total()];
    
    CGColorSpaceRef colorSpace;
    
    if (cvMat.elemSize() == 1)
    {
        colorSpace = CGColorSpaceCreateDeviceGray();
    }
    else
    {
        colorSpace = CGColorSpaceCreateDeviceRGB();
    }
    
    CGDataProviderRef provider = CGDataProviderCreateWithCFData((CFDataRef)data);
    
    CGImageRef imageRef = CGImageCreate(cvMat.cols,                                     // Width
                                        cvMat.rows,                                     // Height
                                        8,                                              // Bits per component
                                        8 * cvMat.elemSize(),                           // Bits per pixel
                                        cvMat.step[0],                                  // Bytes per row
                                        colorSpace,                                     // Colorspace
                                        kCGImageAlphaNone | kCGBitmapByteOrderDefault,  // Bitmap info flags
                                        provider,                                       // CGDataProviderRef
                                        NULL,                                           // Decode
                                        false,                                          // Should interpolate
                                        kCGRenderingIntentDefault);                     // Intent   
    
    self = [self initWithCGImage:imageRef];
    CGImageRelease(imageRef);
    CGDataProviderRelease(provider);
    CGColorSpaceRelease(colorSpace);
    
    return self;
}

/////////
- (IplImage *)createIplImageWithNumberOfChannels:(int)channels
{
    NSAssert(channels == 1 || channels == 3 || channels == 4, @"Invalid number of channels");
    
    CGImageRef cgImage = [self CGImage];
    BOOL drawTransposed;
    CGAffineTransform transform = [self transformForOrientationDrawnTransposed:&drawTransposed];
    
    CvSize cvsize = cvSize(drawTransposed ? CGImageGetHeight(cgImage) : CGImageGetWidth(cgImage),
                           drawTransposed ? CGImageGetWidth(cgImage) : CGImageGetHeight(cgImage));
    IplImage *iplImage = cvCreateImage(cvsize, IPL_DEPTH_8U, (channels == 3) ? 4 : channels);       // CG can only write into 4 byte aligned bitmaps
    
    CGBitmapInfo bitmapInfo = kCGImageAlphaNone;
    if (channels == 3) {
        bitmapInfo = kCGImageAlphaNoneSkipFirst | kCGBitmapByteOrder32Little;        // BGRX. CV_BGRA2BGR will discard the uninitialized alpha channel data.
    } else if (channels == 4) {
        bitmapInfo = kCGImageAlphaPremultipliedFirst | kCGBitmapByteOrder32Little;   // BGRA. Must unpremultiply the image.
    }
    
    CGColorSpaceRef colorSpace = (channels == 1) ? CGColorSpaceCreateDeviceGray() : CGColorSpaceCreateDeviceRGB();
    CGContextRef bitmapContext = CGBitmapContextCreate(iplImage->imageData,
                                                       iplImage->width,
                                                       iplImage->height,
                                                       iplImage->depth,
                                                       iplImage->widthStep,
                                                       colorSpace,
                                                       bitmapInfo);
    CGColorSpaceRelease(colorSpace);
    
    
    // Rotate and/or flip the image if required by its orientation
    CGContextConcatCTM(bitmapContext, transform);
    
    // Copy the source bitmap into the destination, ignoring any data in the uninitialized destination
    CGContextSetBlendMode(bitmapContext, kCGBlendModeCopy);
    
    // Drawing CGImage to CGContext
    CGRect rect = CGRectMake(0.0, 0.0, CGImageGetWidth(cgImage), CGImageGetHeight(cgImage));
    CGContextDrawImage(bitmapContext, rect, cgImage);
    CGContextRelease(bitmapContext);
    
    // Unpremultiply the alpha channel if the source image had one (since otherwise the alphas are 1)
    CGImageAlphaInfo alphaInfo = CGImageGetAlphaInfo(cgImage);
    if (channels == 4 && (alphaInfo != kCGImageAlphaNone && alphaInfo != kCGImageAlphaNoneSkipFirst && alphaInfo != kCGImageAlphaNoneSkipLast)) {
        premultiplyImage(iplImage, YES);
    }
    
    // Convert BGRA images to BGR
    if (channels == 3) {
        IplImage *temp = cvCreateImage(cvGetSize(iplImage), IPL_DEPTH_8U, channels);
        cvCvtColor(iplImage, temp, CV_BGRA2BGR);
        cvReleaseImage(&iplImage);
        iplImage = temp;
    }
    
    return iplImage;
}

- (id)initWithIplImage:(IplImage *)iplImage
{
    return [self initWithIplImage:iplImage orientation:UIImageOrientationUp];
}

- (id)initWithIplImage:(IplImage *)iplImage orientation:(UIImageOrientation)orientation
{
    // CGImage requries either 8-bit or 32-bit aligned images
    IplImage *formattedImage;
    if (iplImage->nChannels == 3) {
        formattedImage = cvCreateImage(cvGetSize(iplImage), IPL_DEPTH_8U, 4);
        cvCvtColor(iplImage, formattedImage, CV_BGR2BGRA);
    } else if (iplImage->nChannels == 4) {
        formattedImage = cvCloneImage(iplImage);
        premultiplyImage(formattedImage, NO);
    } else {
        formattedImage = cvCloneImage(iplImage);
    }
    
    CGDataProviderRef provider = CGDataProviderCreateWithData(formattedImage, formattedImage->imageData, formattedImage->imageSize, releaseImage);
    
    CGBitmapInfo bitmapInfo = (iplImage->nChannels == 1) ? kCGImageAlphaNone : (kCGImageAlphaPremultipliedFirst | kCGBitmapByteOrder32Little);    
    CGColorSpaceRef colorSpace = (formattedImage->nChannels == 1) ? CGColorSpaceCreateDeviceGray() : CGColorSpaceCreateDeviceRGB();    
    CGImageRef cgImage = CGImageCreate(formattedImage->width,
                                       formattedImage->height,
                                       formattedImage->depth,
                                       formattedImage->depth * formattedImage->nChannels,
                                       formattedImage->widthStep,
                                       colorSpace,
                                       bitmapInfo,
                                       provider,
                                       NULL,
                                       false,
                                       kCGRenderingIntentDefault);
    CGColorSpaceRelease(colorSpace);
    CGDataProviderRelease(provider);
    
    self = [self initWithCGImage:cgImage scale:1.0 orientation:orientation];
    CGImageRelease(cgImage);
    
    return self;
}

static inline void premultiplyImage(IplImage *img, BOOL reverse)
{
    NSCAssert(img->depth == IPL_DEPTH_8U, @"depth not IPL_DEPTH_8U");
    uchar *row = (uchar *)img->imageData;
    
    for (int i = 0; i < img->height; i++) {
        for (int j = 0; j < img->width; j+= img->nChannels) {
            uchar alpha = row[j + 3];
            if (alpha != UCHAR_MAX && (!reverse || alpha != 0)) {
                for (int k = 0; k < 3; k++) {
                    if (reverse) {
                        row[j + k] = ((int)row[j + k] * UCHAR_MAX + alpha / 2 - 1) / alpha;
                    } else {
                        row[j + k] = ((int)row[j + k] * alpha + UCHAR_MAX / 2 - 1) / UCHAR_MAX;
                    }
                }
            }
        }
        row += img->widthStep;
    }
}

static void releaseImage(void *info, const void *data, size_t size)
{
    IplImage *image = (IplImage *)info;
    cvReleaseImage(&image);
}

- (CGAffineTransform)transformForOrientationDrawnTransposed:(BOOL *)drawTransposed
{
    UIImageOrientation imageOrientation = [self imageOrientation];
    CGAffineTransform transform = CGAffineTransformIdentity;
    CGSize size = [self size];  // already transposed by UIImage
    
    switch (imageOrientation) {
        case UIImageOrientationDown:           // EXIF orientation 3
        case UIImageOrientationDownMirrored:   // EXIF orientation 4
            transform = CGAffineTransformTranslate(transform, size.width, size.height);
            transform = CGAffineTransformRotate(transform, M_PI);
            break;
            
        case UIImageOrientationLeft:           // EXIF orientation 6
        case UIImageOrientationLeftMirrored:   // EXIF orientation 5
            transform = CGAffineTransformTranslate(transform, size.width, 0);
            transform = CGAffineTransformRotate(transform, M_PI_2);
            break;
            
        case UIImageOrientationRight:          // EXIF orientation 8
        case UIImageOrientationRightMirrored:  // EXIF orientation 7
            transform = CGAffineTransformTranslate(transform, 0, size.height);
            transform = CGAffineTransformRotate(transform, -M_PI_2);
            break;
        default:
            break;
    }
    
    switch (imageOrientation) {
        case UIImageOrientationUpMirrored:     // EXIF orientation 2
        case UIImageOrientationDownMirrored:   // EXIF orientation 4
            transform = CGAffineTransformTranslate(transform, size.width, 0);
            transform = CGAffineTransformScale(transform, -1.0, 1.0);
            break;
            
        case UIImageOrientationLeftMirrored:   // EXIF orientation 5
        case UIImageOrientationRightMirrored:  // EXIF orientation 7
            transform = CGAffineTransformTranslate(transform, size.height, 0);
            transform = CGAffineTransformScale(transform, -1.0, 1.0);
            break;
        default:
            break;
    }
    
    if (drawTransposed) {
        switch (imageOrientation) {
            case UIImageOrientationLeft:
            case UIImageOrientationLeftMirrored:
            case UIImageOrientationRight:
            case UIImageOrientationRightMirrored:
                *drawTransposed = YES;
                break;
                
            default:
                *drawTransposed = NO;
        }
    }
    
    return transform;
}



#pragma mark -
#pragma mark OpenCV Support Methods

// NOTE you SHOULD cvReleaseImage() for the return value when end of the code.
+ (IplImage *)CreateIplImageFromUIImage:(UIImage *)image {
    CGImageRef imageRef = image.CGImage;
    
    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();
    IplImage *iplimage = cvCreateImage(cvSize(image.size.width, image.size.height), IPL_DEPTH_8U, 4);
    CGContextRef contextRef = CGBitmapContextCreate(iplimage->imageData, iplimage->width, iplimage->height,
                                                    iplimage->depth, iplimage->widthStep,
                                                    colorSpace, kCGImageAlphaPremultipliedLast|kCGBitmapByteOrderDefault);
    CGContextDrawImage(contextRef, CGRectMake(0, 0, image.size.width, image.size.height), imageRef);
    CGContextRelease(contextRef);
    CGColorSpaceRelease(colorSpace);
    
    IplImage *ret = cvCreateImage(cvGetSize(iplimage), IPL_DEPTH_8U, 3);
    cvCvtColor(iplimage, ret, CV_RGBA2BGR);
    cvReleaseImage(&iplimage);
    
    return ret;
}

// NOTE You should convert color mode as RGB before passing to this function
- (UIImage *)UIImageFromIplImage:(IplImage *)image {
    NSLog(@"IplImage (%d, %d) %d bits by %d channels, %d bytes/row %s", image->width, image->height, image->depth, image->nChannels, image->widthStep, image->channelSeq);
    
    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();
    NSData *data = [NSData dataWithBytes:image->imageData length:image->imageSize];
    CGDataProviderRef provider = CGDataProviderCreateWithCFData((CFDataRef)data);
    CGImageRef imageRef = CGImageCreate(image->width, image->height,
                                        image->depth, image->depth * image->nChannels, image->widthStep,
                                        colorSpace, kCGImageAlphaNone|kCGBitmapByteOrderDefault,
                                        provider, NULL, false, kCGRenderingIntentDefault);
    UIImage *ret = [UIImage imageWithCGImage:imageRef];
    CGImageRelease(imageRef);
    CGDataProviderRelease(provider);
    CGColorSpaceRelease(colorSpace);
    return ret;
}

IplImage* rotate(IplImage* image, float angle) {
    
    IplImage *rotatedImage = cvCreateImage(cvSize(480,320), IPL_DEPTH_8U,image->nChannels);
    
    CvPoint2D32f center;
    center.x = 160;center.y = 160;
    CvMat *mapMatrix = cvCreateMat( 2, 3, CV_32FC1 );
    
    cv2DRotationMatrix(center, angle, 1.0, mapMatrix);
    cvWarpAffine(image, rotatedImage, mapMatrix, CV_INTER_LINEAR + CV_WARP_FILL_OUTLIERS, cvScalarAll(0));
    
    cvReleaseImage(&image);
    cvReleaseMat(&mapMatrix);
    
    return rotatedImage;
}

@end
