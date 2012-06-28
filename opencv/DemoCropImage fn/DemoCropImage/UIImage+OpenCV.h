//
//  UIImage+OpenCV.h
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

#import <UIKit/UIKit.h>

@interface UIImage (UIImage_OpenCV)
+(UIImage *)imageWithCVMat:(const cv::Mat&)cvMat;
-(id)initWithCVMat:(const cv::Mat&)cvMat;
// Creates an IplImage in gray, BGR or BGRA format. It is the caller's responsibility to cvReleaseImage() the return value.
- (IplImage *)createIplImageWithNumberOfChannels:(int)channels;

// Returns a UIImage by copying the IplImage's bitmap data. 
- (id)initWithIplImage:(IplImage *)iplImage;
- (id)initWithIplImage:(IplImage *)iplImage orientation:(UIImageOrientation)orientation;

// Returns an affine transform that takes into account the image orientation when drawing a scaled image
- (CGAffineTransform)transformForOrientationDrawnTransposed:(BOOL *)drawTransposed;

+ (IplImage *)CreateIplImageFromUIImage:(UIImage *)image;
- (UIImage *)UIImageFromIplImage:(IplImage *)image;

IplImage* rotate(IplImage* image, float angle);

@property(nonatomic, readonly) cv::Mat CVMat;
@property(nonatomic, readonly) cv::Mat CVGrayscaleMat;

@end
