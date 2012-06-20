//
//  Features2DAndHomographyViewController.m
//  OpenCVClient
//
//  Created by helios-team on 6/19/12.
//  Copyright (c) 2012 Aptogo Limited. All rights reserved.
//

#import "Features2DAndHomographyViewController.h"
#import "UIImage+OpenCV.h"
using namespace std;
using namespace cv;

@interface Features2DAndHomographyViewController ()
- (UIImage *) grayishImage :(UIImage *)inputImage;
@end

@implementation Features2DAndHomographyViewController
@synthesize imgvobj, imgvscence;
@synthesize button = _mainButton;

void feature_homography(Mat img_object, Mat img_scene, Mat &img_result);

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        UIImage *colorObject = [UIImage imageNamed:@"Jack.png"];//344
        IplImage * iplObject = [colorObject CreateIplImageFromUIImage:colorObject];
        //IplImage * grayScaleObject = cvCreateImage(cvGetSize(iplObject), IPL_DEPTH_8U, 1);
        CvMat * grayScaleObject = cvCreateMat(iplObject->height, iplObject->width, CV_8UC1);
        cvCvtColor(iplObject, grayScaleObject, CV_RGB2GRAY);
        
        Mat img_object = [[self grayishImage:colorObject] CVGrayscaleMat];
        
        UIImage *colorScene = [UIImage imageNamed:@"IMAG0121.jpg"];
        IplImage * iplScene = [colorScene CreateIplImageFromUIImage:colorScene];
        //IplImage * grayScaleScene = cvCreateImage(cvGetSize(iplScene), IPL_DEPTH_8U, 1);
        CvMat * grayScaleScene = cvCreateMat(iplScene->height, iplScene->width, CV_8UC1);
        cvCvtColor(iplScene, grayScaleScene, CV_RGB2GRAY);
        
        Mat img_scense = [[self grayishImage:colorScene] CVGrayscaleMat];
        Mat result;
        
        feature_homography(grayScaleObject, grayScaleScene, result);
        
        UIImage * img_result = [[UIImage alloc] initWithCVMat:result];
        UIImageView * imgView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 320, 480 - 44)];
        imgView.image = img_result;
        
        [self.view addSubview:imgView];
        [imgView release];
    }
    return self;
}

- (void) didButtonClicked:(id)sender {
    UIImagePickerController * picker = [[UIImagePickerController alloc] init];
    picker.delegate = self;
    picker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
    [self presentModalViewController:picker animated:YES];
    [picker release];
}

#pragma mark - Features2D + Homography to find a known object 
#pragma mark -- http://opencv.itseez.com/trunk/doc/tutorials/features2d/table_of_content_features2d/table_of_content_features2d.html#table-of-content-feature2d

void feature_homography(Mat img_object, Mat img_scene, Mat &img_result) {
    
    if( !img_object.data || !img_scene.data )
    { std::cout<< " --(!) Error reading images " << std::endl; return; }
    
    //-- Step 1: Detect the keypoints using SURF Detector
    int minHessian = 400;
    
    SurfFeatureDetector detector(minHessian);
    
    std::vector<KeyPoint> keypoints_object, keypoints_scene;
    
    detector.detect(img_object, keypoints_object);
    detector.detect(img_scene, keypoints_scene);
    
    //-- Step 2: Calculate descriptors (feature vectors)
    SurfDescriptorExtractor extractor;
    
    Mat descriptors_object, descriptors_scene;
    
    extractor.compute( img_object, keypoints_object, descriptors_object );
    extractor.compute( img_scene, keypoints_scene, descriptors_scene );
    
    //-- Step 3: Matching descriptor vectors using FLANN matcher
    FlannBasedMatcher matcher;
    std::vector< DMatch > matches;
    matcher.match( descriptors_object, descriptors_scene, matches );
    
    double max_dist = 0; double min_dist = 100;
    
    //-- Quick calculation of max and min distances between keypoints
    for( int i = 0; i < descriptors_object.rows; i++ )
    {   
        double dist = matches[i].distance;
        if( dist < min_dist ) min_dist = dist;
        if( dist > max_dist ) max_dist = dist;
    }
    
    printf("-- Max dist : %f \n", max_dist );
    printf("-- Min dist : %f \n", min_dist );
    
    //-- Draw only "good" matches (i.e. whose distance is less than 3*min_dist )
    std::vector< DMatch > good_matches;
    
    for( int i = 0; i < descriptors_object.rows; i++ )
    { if( matches[i].distance < 3 * min_dist )
    { 
        good_matches.push_back( matches[i]); }
    }
    
    Mat img_matches;
    drawMatches( img_object, keypoints_object, img_scene, keypoints_scene,
                good_matches, img_matches, Scalar(255, 0, 0), Scalar(255, 0, 0),
                vector<char>(), DrawMatchesFlags::NOT_DRAW_SINGLE_POINTS );
    
    //-- Localize the object
    std::vector<Point2f> obj;
    std::vector<Point2f> scene;
    
    for( int i = 0; i < good_matches.size(); i++ )
    {
        //-- Get the keypoints from the good matches
        obj.push_back( keypoints_object[ good_matches[i].queryIdx ].pt );
        scene.push_back( keypoints_scene[ good_matches[i].trainIdx ].pt );
    }
    
    Mat H = findHomography( obj, scene, CV_RANSAC );
    
    //-- Get the corners from the image_1 ( the object to be "detected" )
    std::vector<Point2f> obj_corners(4);
    obj_corners[0] = cvPoint(0,0); obj_corners[1] = cvPoint( img_object.cols, 0 );
    obj_corners[2] = cvPoint( img_object.cols, img_object.rows ); obj_corners[3] = cvPoint( 0, img_object.rows );
    
    printf("-- obj_corner[1] : %f, %f \n", obj_corners[1].x, obj_corners[1].y );
    printf("-- obj_corner[2] : %f, %f \n", obj_corners[2].x, obj_corners[2].y );
    printf("-- obj_corner[3] : %f, %f \n", obj_corners[3].x, obj_corners[3].y );
    printf("-- obj_corner[0] : %f, %f \n", obj_corners[0].x, obj_corners[0].y );
    
    std::vector<Point2f> scene_corners(4);
    
    perspectiveTransform( obj_corners, scene_corners, H);
    
    //-- Draw lines between the corners (the mapped object in the scene - image_2 )
    line( img_matches, scene_corners[0] + Point2f( img_object.cols, 0), scene_corners[1] + Point2f( img_object.cols, 0), Scalar(0, 255, 0), 4 );
    
    printf("-- scene_corner[1] : %f, %f \n", scene_corners[1].x, scene_corners[1].y );
    
    line( img_matches, scene_corners[1] + Point2f( img_object.cols, 0), scene_corners[2] + Point2f( img_object.cols, 0), Scalar( 0, 255, 0), 4 );
    printf("-- scene_corner[2] : %f, %f \n", scene_corners[2].x, scene_corners[2].y );
    
    line( img_matches, scene_corners[2] + Point2f( img_object.cols, 0), scene_corners[3] + Point2f( img_object.cols, 0), Scalar( 0, 255, 0), 4 );
    printf("-- scene_corner[3] : %f, %f \n", scene_corners[3].x, scene_corners[3].y );
    
    line( img_matches, scene_corners[3] + Point2f( img_object.cols, 0), scene_corners[0] + Point2f( img_object.cols, 0), Scalar( 0, 255, 0), 4 );
    printf("-- scene_corner[0] : %f, %f \n", scene_corners[0].x, scene_corners[0].y );
    
    //-- Show detected matches
    img_result = img_matches;
}

#pragma mark - UIImage processing
- (UIImage *) convertToGreyscale:(UIImage *)i {
    
    int kRed = 1;
    int kGreen = 2;
    int kBlue = 4;
    
    int colors = kGreen;
    int m_width = i.size.width;
    int m_height = i.size.height;
    
    uint32_t *rgbImage = (uint32_t *) malloc(m_width * m_height * sizeof(uint32_t));
    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();
    CGContextRef context = CGBitmapContextCreate(rgbImage, m_width, m_height, 8, m_width * 4, colorSpace, kCGBitmapByteOrder32Little | kCGImageAlphaNoneSkipLast);
    CGContextSetInterpolationQuality(context, kCGInterpolationHigh);
    CGContextSetShouldAntialias(context, NO);
    CGContextDrawImage(context, CGRectMake(0, 0, m_width, m_height), [i CGImage]);
    CGContextRelease(context);
    CGColorSpaceRelease(colorSpace);
    
    // now convert to grayscale
    uint8_t *m_imageData = (uint8_t *) malloc(m_width * m_height);
    for(int y = 0; y < m_height; y++) {
        for(int x = 0; x < m_width; x++) {
            uint32_t rgbPixel=rgbImage[y*m_width+x];
            uint32_t sum=0,count=0;
            if (colors & kRed) {sum += (rgbPixel>>24)&255; count++;}
            if (colors & kGreen) {sum += (rgbPixel>>16)&255; count++;}
            if (colors & kBlue) {sum += (rgbPixel>>8)&255; count++;}
            m_imageData[y*m_width+x]=sum/count;
        }
    }
    free(rgbImage);
    
    // convert from a gray scale image back into a UIImage
    uint8_t *result = (uint8_t *) calloc(m_width * m_height *sizeof(uint32_t), 1);
    
    // process the image back to rgb
    for(int i = 0; i < m_height * m_width; i++) {
        result[i*4]=0;
        int val=m_imageData[i];
        result[i*4+1]=val;
        result[i*4+2]=val;
        result[i*4+3]=val;
    }
    
    // create a UIImage
    colorSpace = CGColorSpaceCreateDeviceRGB();
    context = CGBitmapContextCreate(result, m_width, m_height, 8, m_width * sizeof(uint32_t), colorSpace, kCGBitmapByteOrder32Little | kCGImageAlphaNoneSkipLast);
    CGImageRef image = CGBitmapContextCreateImage(context);
    CGContextRelease(context);
    CGColorSpaceRelease(colorSpace);
    UIImage *resultUIImage = [UIImage imageWithCGImage:image];
    CGImageRelease(image);
    
    free(m_imageData);
    
    // make sure the data will be released by giving it to an autoreleased NSData
    [NSData dataWithBytesNoCopy:result length:m_width * m_height];
    
    return resultUIImage;
}


- (UIImage *) grayishImage :(UIImage *)inputImage {
    
    // Create a graphic context.
    UIGraphicsBeginImageContextWithOptions(inputImage.size, YES, 1.0);
    CGRect imageRect = CGRectMake(0, 0, inputImage.size.width, inputImage.size.height);
    
    // Draw the image with the luminosity blend mode.
    // On top of a white background, this will give a black and white image.
    [inputImage drawInRect:imageRect blendMode:kCGBlendModeLuminosity alpha:1.0];
    
    // Get the resulting image.
    UIImage *filteredImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    return filteredImage;
}

#pragma mark - UIImagePickerControllerDelegate implementation

- (void)imagePickerController:(UIImagePickerController *)picker
        didFinishPickingImage:(UIImage *)image
                  editingInfo:(NSDictionary *)editingInfo
{
    [picker dismissModalViewControllerAnimated:YES];
    
    UIImage *colorObject = [UIImage imageNamed:@"IMG_0344.jpg"];
    IplImage * iplObject = [colorObject CreateIplImageFromUIImage:colorObject];
    IplImage * grayScaleObject = cvCreateImage(cvGetSize(iplObject), IPL_DEPTH_8U, 1);
    cvCvtColor(iplObject, grayScaleObject, CV_RGB2GRAY);
    
    Mat img_object = [[self grayishImage:colorObject] CVGrayscaleMat];
    
    UIImage *colorScene = [UIImage imageNamed:@"IMAG0106.jpg"];
    IplImage * iplScene = [colorScene CreateIplImageFromUIImage:colorScene];
    IplImage * grayScaleScene = cvCreateImage(cvGetSize(iplScene), IPL_DEPTH_8U, 1);
    cvCvtColor(iplScene, grayScaleScene, CV_RGB2GRAY);
    
    Mat scense = [[self grayishImage:colorScene] CVGrayscaleMat];
    Mat result;
    
    feature_homography(img_object, scense, result);
    
    UIImage * img_result = [[UIImage alloc] initWithCVMat:result];
    UIImageView * imgView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 320, 480 - 44)];
    imgView.image = img_result;
    
    [self.view addSubview:imgView];
    [imgView release];
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker
{
    [picker dismissModalViewControllerAnimated:YES];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

@end
