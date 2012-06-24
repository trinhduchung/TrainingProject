//
//  MIT License
//
//  Copyright (c) 2012 Bob McCune http://bobmccune.com/
//  Copyright (c) 2012 TapHarmonic, LLC http://tapharmonic.com
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.
//

#import "MainViewController.h"
#import <QuartzCore/QuartzCore.h>
#import "UIImage+Addition.h"
#define IMG(name) [UIImage imageNamed:name]
#define FACE_IMAGES [NSArray arrayWithObjects:IMG(@"abc"),IMG(@"marilyn"), IMG(@"ali"), IMG(@"ayn"), IMG(@"kennedy"), IMG(@"hoff"), IMG(@"ava1"), IMG(@"ava2"), nil]

#define IMG_WIDTH 280.0f
#define IMG_HEIGHT 310.0f


@interface MainViewController ()

@property (nonatomic, strong) UIImageView *activeImageView;
@property (nonatomic, assign) BOOL useHighAccuracy;
@property (nonatomic, assign) NSUInteger currentIndex;

- (void)drawImageAnnotatedWithFeatures:(NSArray *)features;
- (void)drawFeatureInContext:(CGContextRef)context atPoint:(CGPoint)featurePoint;
- (void)drawTest:(CGContextRef)context atPoint:(CGRect)rect;
@end


@implementation MainViewController

@synthesize scrollView = _scrollView;
@synthesize activityView = _activityView;
@synthesize detectingView = _detectingView;
@synthesize useHighAccuracy = _useHighAccuracy;
@synthesize activeImageView = _activeImageView;
@synthesize currentIndex = _currentIndex;

- (void)viewDidLoad {
    [super viewDidLoad];
    self.detectingView.hidden = YES;
    self.detectingView.layer.cornerRadius = 8.0f;
	
	CGFloat currentX = 0.0f;
	CGRect viewRect = CGRectMake(currentX, 0, IMG_WIDTH, IMG_HEIGHT);
	self.scrollView.contentSize = CGSizeMake(viewRect.size.width * [FACE_IMAGES count], viewRect.size.height);
	
	for (UIImage *image in FACE_IMAGES) {
		UIImageView *imageView = [[UIImageView alloc] initWithImage:image];
		imageView.frame = CGRectMake(currentX, 0.0f, IMG_WIDTH, IMG_HEIGHT);
		[self.scrollView addSubview:imageView];
		currentX += IMG_WIDTH;
		if (!self.activeImageView) {
			self.activeImageView = imageView;
		}
	}
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView {
	self.currentIndex = scrollView.contentOffset.x / IMG_WIDTH;
	self.activeImageView = [[scrollView subviews] objectAtIndex:self.currentIndex];
}

- (IBAction)resetImage {
    self.activeImageView.image = [FACE_IMAGES objectAtIndex:self.currentIndex];
}

- (IBAction)useHighAccuracy:(id)sender {
    self.useHighAccuracy = [sender isOn];
}

- (IBAction)detectFacialFeatures:(id)sender {

    self.detectingView.hidden = NO;
	self.scrollView.scrollEnabled = NO;

    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{

        CIImage *image = [[CIImage alloc] initWithImage:[FACE_IMAGES objectAtIndex:self.currentIndex]];

        NSString *accuracy = self.useHighAccuracy ? CIDetectorAccuracyHigh : CIDetectorAccuracyLow;
        NSDictionary *options = [NSDictionary dictionaryWithObject:accuracy forKey:CIDetectorAccuracy];
        CIDetector *detector = [CIDetector detectorOfType:CIDetectorTypeFace context:nil options:options];

        NSArray *features = [detector featuresInImage:image];

        dispatch_async(dispatch_get_main_queue(), ^{
            [self drawImageAnnotatedWithFeatures:features];
        });

    });
}
- (UIImage *)cropImage:(UIImage*)img withRect:(CGRect)rect
{
//    CGFloat scale = [[UIScreen mainScreen] scale];
//    
//    if (scale>1.0) {        
//        rect = CGRectMake(rect.origin.x*scale , rect.origin.y*scale, rect.size.width*scale, rect.size.height*scale);        
//    }
    
    CGImageRef imageRef = CGImageCreateWithImageInRect([img CGImage], rect);
    UIImage *result = [UIImage imageWithCGImage:imageRef]; 
    CGImageRelease(imageRef);
    return result;
}

int max(int w, int h) {
    return w >= h ? w : h;
}

- (void)drawImageAnnotatedWithFeatures:(NSArray *)features {

	UIImage *faceImage = [FACE_IMAGES objectAtIndex:self.currentIndex];
//    
    CGRect tempRect = ((CIFaceFeature*)[features objectAtIndex:0]).bounds;
    CGRect tempRect1 = ((CIFaceFeature*)[features objectAtIndex:1]).bounds;
    
    UIImage *face1 = [self cropImage:faceImage withRect:CGRectMake(tempRect.origin.x, tempRect.origin.y-130, tempRect.size.width, tempRect.size.height)];
    UIImage *face2 = [self cropImage:faceImage withRect:CGRectMake(tempRect1.origin.x, tempRect1.origin.y-40, tempRect1.size.width, tempRect1.size.height)];
    
    UIGraphicsGetCurrentContext();
    int x = tempRect.origin.x;
    int y = tempRect.origin.y;
    int width = tempRect.size.width;
    int height = tempRect.size.height;
    float startAngle = 0;
    float endAngle = 90;
    CGPoint center = CGPointMake(x + width / 2.0, y + height / 2.0);
    UIBezierPath* clip = [UIBezierPath bezierPathWithArcCenter:center
                                                        radius:max(width, height)
                                                    startAngle:startAngle
                                                      endAngle:endAngle
                                                     clockwise:YES];
    [clip addLineToPoint:center];
    [clip closePath];
    [clip addClip];
    
    UIBezierPath *arc = [UIBezierPath bezierPathWithOvalInRect:CGRectMake(x, y, width, height)];
    [[UIColor blackColor] setStroke];
    [arc stroke];
    
    UIGraphicsEndImageContext();
//    UIImageView *v1 = [[UIImageView alloc] initWithFrame:((CIFaceFeature*)[features objectAtIndex:1]).bounds];
//    v1.image = face1;
//    UIImageView *v2 = [[UIImageView alloc] initWithFrame:((CIFaceFeature*)[features objectAtIndex:0]).bounds];
//    v2.image = face2;
//    [self.scrollView addSubview:v1];
//    [self.scrollView addSubview:v2];
    //[face1 drawInRect:((CIFaceFeature*)[features objectAtIndex:1]).bounds];
    //[face2 drawInRect:((CIFaceFeature*)[features objectAtIndex:0]).bounds];
    
    UIGraphicsBeginImageContextWithOptions(faceImage.size, YES, 0);
    [faceImage drawInRect:self.activeImageView.bounds];
    [face1 drawInRect:CGRectMake(tempRect1.origin.x, tempRect1.origin.y-40, tempRect1.size.width, tempRect1.size.height)];
    [face2 drawInRect:CGRectMake(tempRect.origin.x, tempRect.origin.y-130, tempRect.size.width, tempRect.size.height)];
    // Get image context reference
    CGContextRef context = UIGraphicsGetCurrentContext();

    // Flip Context
    CGContextTranslateCTM(context, 0, self.activeImageView.bounds.size.height);
    CGContextScaleCTM(context, 1.0f, -1.0f);

//    CGFloat scale = [UIScreen mainScreen].scale;
//
//    if (scale > 1.0) {
//        // Loaded 2x image, scale context to 50%
//        CGContextScaleCTM(context, 0.5, 0.5);
//    }
//
//    for (CIFaceFeature *feature in features) {
//
//        CGContextSetRGBFillColor(context, 0.0f, 0.0f, 0.0f, 0.5f);
//        CGContextSetStrokeColorWithColor(context, [UIColor whiteColor].CGColor);
//        CGContextAddEllipseInRect(context, feature.bounds);
//        //CGContextAddRect(context, feature.bounds);
//        CGContextDrawPath(context, kCGPathFillStroke);
//
//        // Set red feature color
//        CGContextSetRGBFillColor(context, 1.0f, 0.0f, 0.0f, 0.4f);
//
//        if (feature.hasLeftEyePosition) {
//            //CGFloat faceWidth = feature.bounds.size.width;
//            [self drawFeatureInContext:context atPoint:feature.leftEyePosition];
//            //[self drawTest:context atPoint:CGRectMake(feature.leftEyePosition.x-faceWidth*0.15, feature.leftEyePosition.y-faceWidth*0.15, faceWidth*0.3, faceWidth*0.3)];
//        }
//
//        if (feature.hasRightEyePosition) {
//            [self drawFeatureInContext:context atPoint:feature.rightEyePosition];
//        }
//
//        if (feature.hasMouthPosition) {
//            [self drawFeatureInContext:context atPoint:feature.mouthPosition];
//        }
//    }

    self.activeImageView.image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();

    self.detectingView.hidden = YES;
	self.scrollView.scrollEnabled = YES;
}
- (void)drawTest:(CGContextRef)context atPoint:(CGRect)rect
{
    CGContextAddRect(context, rect);
    CGContextDrawPath(context, kCGPathFillStroke);
    
}
- (void)drawFeatureInContext:(CGContextRef)context atPoint:(CGPoint)featurePoint {
    CGFloat radius = 20.0f * [UIScreen mainScreen].scale;
    CGContextAddArc(context, featurePoint.x, featurePoint.y, radius, 0, M_PI * 2, 1);
    CGContextDrawPath(context, kCGPathFillStroke);
}

@end
