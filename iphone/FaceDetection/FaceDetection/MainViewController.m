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

        UIImage *faceImage = [FACE_IMAGES objectAtIndex:self.currentIndex];
        
        
        faceImage = [self imageWithImage:faceImage scaledToSize:self.activeImageView.frame.size];
        CIImage *image = [[CIImage alloc] initWithImage:faceImage];

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

- (UIImage*)imageWithImage:(UIImage*)image 
              scaledToSize:(CGSize)newSize;
{
    UIGraphicsBeginImageContext( newSize );
    [image drawInRect:CGRectMake(0,0,newSize.width,newSize.height)];
    UIImage* newImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    return newImage;
}

-(UIImage*)CropImage:(UIBezierPath *)trackPath withImageView:(UIImageView *) aImgView {
    CGContextRef mainViewContentContext;
    CGColorSpaceRef colorSpace;
    
    colorSpace = CGColorSpaceCreateDeviceRGB();
    
    // create a bitmap graphics context the size of the image
    mainViewContentContext = CGBitmapContextCreate(NULL, aImgView.frame.size.width, aImgView.frame.size.height, 8, aImgView.frame.size.width * 4, colorSpace, kCGImageAlphaPremultipliedLast);
    
    // free the rgb colorspace
    CGColorSpaceRelease(colorSpace);
    
    //Translate and scale image
    CGContextTranslateCTM(mainViewContentContext, 0, aImgView.frame.size.height);
    CGContextScaleCTM(mainViewContentContext, 1.0, -1.0);
    
    //the mask
    CGContextAddPath(mainViewContentContext, trackPath.CGPath);
    CGContextClip(mainViewContentContext);
    
    //Translate and scale image
    CGContextTranslateCTM(mainViewContentContext, 0, aImgView.frame.size.height);
    CGContextScaleCTM(mainViewContentContext, 1.0, -1.0);

    //the main image
    CGContextDrawImage(mainViewContentContext, CGRectMake(0, 0, aImgView.frame.size.width, aImgView.frame.size.height), aImgView.image.CGImage);
    
    //the outline
    CGContextSetLineWidth(mainViewContentContext, 1);
    CGContextSetRGBStrokeColor(mainViewContentContext, 181.0/256, 181.0/256, 181.0/256, 1.0);
    
    // Create CGImageRef of the main view bitmap content, and then
    // release that bitmap context
    CGImageRef mainViewContentBitmapContext = CGBitmapContextCreateImage(mainViewContentContext);
    CGContextRelease(mainViewContentContext);
    
    // convert the finished resized image to a UIImage
    UIImage *newImage = [UIImage imageWithCGImage:mainViewContentBitmapContext];
    
    // image is retained by the property setting above, so we can
    // release the original
    CGImageRelease(mainViewContentBitmapContext);
    
    return newImage;
}

- (void)drawImageAnnotatedWithFeatures:(NSArray *)features {

	UIImage *faceImage = [FACE_IMAGES objectAtIndex:self.currentIndex];
    
    
    faceImage = [self imageWithImage:faceImage scaledToSize:self.activeImageView.frame.size];
    CGRect tempRect = ((CIFaceFeature*)[features objectAtIndex:0]).bounds;
    CGRect tempRect1 = ((CIFaceFeature*)[features objectAtIndex:1]).bounds;
    
    //NSLog(@"===%f : %f", self.activeImageView.bounds.origin.x, self.activeImageView.bounds.origin.y);
    NSLog(@"---%f : %f", tempRect.origin.x, tempRect.origin.y);
    NSLog(@"---%f : %f", tempRect1.origin.x, tempRect1.origin.y);
    UIGraphicsBeginImageContextWithOptions(faceImage.size, YES, 0);
    
    [faceImage drawInRect:self.activeImageView.bounds];

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
    
    CGFloat x = tempRect.origin.x;
    CGFloat y = tempRect.origin.y;
    CGFloat width = tempRect.size.width;
    CGFloat height = tempRect.size.height;
    
    CGFloat x1 = tempRect1.origin.x;
    CGFloat y1 = tempRect1.origin.y;
    CGFloat width1 = tempRect1.size.width;
    CGFloat height1 = tempRect1.size.height;
    
    UIBezierPath *arc = [UIBezierPath bezierPathWithOvalInRect:CGRectMake(x, y, width / 3 * 2, height)];
    [[UIColor blueColor] setStroke];
    [arc stroke];
    
    UIGraphicsGetCurrentContext();
    
    UIBezierPath *arc1 = [UIBezierPath bezierPathWithOvalInRect:CGRectMake(x1, y1, width1 / 3 * 2, height1)];
    [[UIColor yellowColor] setStroke];
    [arc1 stroke];

    ///crop image with path
    
    UIImage *face1 = [self CropImage:arc withImageView:self.activeImageView];
    UIImage *face2 = [self CropImage:arc1 withImageView:self.activeImageView];
    [face1 drawInRect:CGRectMake(tempRect1.origin.x, tempRect1.origin.y - 140, face1.size.width, face1.size.height)];
    [face2 drawInRect:CGRectMake(tempRect.origin.x, tempRect.origin.y - 140, face2.size.width, face2.size.height)];


    
    
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
