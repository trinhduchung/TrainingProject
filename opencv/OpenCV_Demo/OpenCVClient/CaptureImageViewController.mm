//
//  CaptureImageViewController.m
//  OpenCVClient
//
//  Created by helios-team on 5/17/12.
//  Copyright (c) 2012 Aptogo Limited. All rights reserved.
//

#import "CaptureImageViewController.h"
#import "TouchImageView.h"
#import "UIImage+OpenCV.h"
#import "UIImage+Addition.h"
using namespace cv;
int     ind = 0;
@interface CaptureImageViewController ()

@end

@implementation CaptureImageViewController

//double angle( cv::Point pt1, cv::Point pt2, cv::Point pt0 ) {
//    double dx1 = pt1.x - pt0.x;
//    double dy1 = pt1.y - pt0.y;
//    double dx2 = pt2.x - pt0.x;
//    double dy2 = pt2.y - pt0.y;
//    return (dx1*dx2 + dy1*dy2)/sqrt((dx1*dx1 + dy1*dy1)*(dx2*dx2 + dy2*dy2) + 1e-10);
//}
//
////- (void) find_squares:(cv::Mat&) image withVector:(cv::vector<cv::vector<cv::Point> >&) squares
//void find_squares(cv::Mat& image, std::vector<std::vector<cv::Point> >& squares)
//{
//    // blur will enhance edge detection
//    cv::Mat blurred(image);
//    medianBlur(image, blurred, 9);
//    
//    cv::Mat gray0(blurred.size(), CV_8U), gray;
//    std::vector<std::vector<cv::Point> > contours;
//    
//    // find squares in every color plane of the image
//    for (int c = 0; c < 3; c++)
//    {
//        int ch[] = {c, 0};
//        mixChannels(&blurred, 1, &gray0, 1, ch, 1);
//        
//        // try several threshold levels
//        const int threshold_level = 2;
//        for (int l = 0; l < threshold_level; l++)
//        {
//            // Use Canny instead of zero threshold level!
//            // Canny helps to catch squares with gradient shading
//            if (l == 0)
//            {
//                Canny(gray0, gray, 10, 20, 3); // 
//                
//                // Dilate helps to remove potential holes between edge segments
//                dilate(gray, gray, cv::Mat(), cv::Point(-1,-1));
//            }
//            else
//            {
//                gray = gray0 >= (l+1) * 255 / threshold_level;
//            }
//            
//            // Find contours and store them in a list
//            findContours(gray, contours, CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);
//            
//            // Test contours
//            std::vector<cv::Point> approx;
//            for (size_t i = 0; i < contours.size(); i++)
//            {
//                // approximate contour with accuracy proportional
//                // to the contour perimeter
//                approxPolyDP(cv::Mat(contours[i]), approx, arcLength(cv::Mat(contours[i]), true)*0.02, true);
//                
//                // Note: absolute value of an area is used because
//                // area may be positive or negative - in accordance with the
//                // contour orientation
//                if (approx.size() == 4 &&
//                    fabs(contourArea(cv::Mat(approx))) > 1000 &&
//                    isContourConvex(cv::Mat(approx)))
//                {
//                    double maxCosine = 0;
//                    
//                    for (int j = 2; j < 5; j++)
//                    {
//                        double cosine = fabs(angle(approx[j%4], approx[j-2], approx[j-1]));
//                        maxCosine = MAX(maxCosine, cosine);
//                    }
//                    
//                    if (maxCosine < 0.3)
//                        squares.push_back(approx);
//                }
//            }
//        }
//    }
//}

- (void) compute_skew {
    UIImage * testImage = [UIImage imageNamed:@"inverted.jpg"];
    cv::Mat src = [testImage CVGrayscaleMat];
    cv::Size size = src.size();
    cv::bitwise_not(src, src);
    std::vector<cv::Vec4i> lines;
    cv::HoughLinesP(src, lines, 1, CV_PI/180, 100, size.width / 2.f, 20);
    cv::Mat disp_lines(size, CV_8UC1, cv::Scalar(0, 0, 0));
    double angle = 0.;
    unsigned nb_lines = lines.size();
    for (unsigned i = 0; i < nb_lines; ++i)
    {
        cv::line(disp_lines, cv::Point(lines[i][0], lines[i][1]),
                 cv::Point(lines[i][2], lines[i][3]), cv::Scalar(255, 0 ,0));
        angle += atan2((double)lines[i][3] - lines[i][1],
                       (double)lines[i][2] - lines[i][0]);
    }
    angle /= nb_lines; // mean angle, in radians.
    
    std::cout << "Image skew angles : " << angle * 180 / CV_PI << std::endl;
    
}

cv::Mat rotateImage(const cv::Mat& source, double angle)
{
    /*
    cv::Point2f srcTri[3];
    cv::Point2f dstTri[3];
    
    cv::Mat rot_mat( 2, 3, CV_32FC1 );
    cv::Mat warp_mat( 2, 3, CV_32FC1 );
    cv::Mat src, warp_dst, warp_rotate_dst;
    
    /// Load the image
    src = source;
    
    /// Set the dst image the same type and size as src
    warp_dst = cv::Mat::zeros( src.rows, src.cols, src.type() );
    
    /// Set your 3 points to calculate the  Affine Transform
    srcTri[0] = cv::Point2f( 0,0 );
    srcTri[1] = cv::Point2f( src.cols - 1, 0 );
    srcTri[2] = cv::Point2f( 0, src.rows - 1 );
    
    dstTri[0] = cv::Point2f( src.cols*0.0, src.rows*0.33 );
    dstTri[1] = cv::Point2f( src.cols*0.85, src.rows*0.25 );
    dstTri[2] = cv::Point2f( src.cols*0.15, src.rows*0.7 );
    
    /// Get the Affine Transform
    warp_mat = getAffineTransform( srcTri, dstTri );
    
    /// Apply the Affine Transform just found to the src image
    warpAffine( src, warp_dst, warp_mat, warp_dst.size() );
    
    // Rotating the image after Warp 
    
    /// Compute a rotation matrix with respect to the center of the image
    cv::Point center = cv::Point( warp_dst.cols/2, warp_dst.rows/2 );
//    double angle = -50.0;
    double scale = 0.6;
    
    /// Get the rotation matrix with the specifications above
    rot_mat = getRotationMatrix2D( center, angle, scale );
    
    /// Rotate the warped image
    warpAffine( warp_dst, warp_rotate_dst, rot_mat, warp_dst.size() );
    */
    
    cv::Point2f src_center(source.cols/2.0F, source.rows/2.0F);
    cv::Mat rot_mat = getRotationMatrix2D(src_center, angle, 1.0);
    cv::Mat dst;
    warpAffine(source, dst, rot_mat, source.size());
    
    return dst;
}

/// Global variables
Mat src, dst;
Mat map_x, map_y;

/**
 * @function update_map
 * @brief Fill the map_x and map_y matrices with 4 types of mappings
 */
void update_map( void )
{
    ind = ind % 4;
    for( int j = 0; j < src.rows; j++ )
    { 
        for( int i = 0; i < src.cols; i++ )
        {
            switch( ind )
            {
                case 0:
                    if( i > src.cols*0.25 && i < src.cols*0.75 && j > src.rows*0.25 && j < src.rows*0.75 )
                    {map_x.at<float>(j,i) = 2*( i - src.cols*0.25 ) + 0.5 ;
                        map_y.at<float>(j,i) = 2*( j - src.rows*0.25 ) + 0.5 ;
                    }
                    else
                    { map_x.at<float>(j,i) = 0 ;
                        map_y.at<float>(j,i) = 0 ;
                    }
                    break;
                case 1:
                    map_x.at<float>(j,i) = i ;
                    map_y.at<float>(j,i) = src.rows - j ;
                    break;
                case 2:
                    map_x.at<float>(j,i) = src.cols - i ;
                    map_y.at<float>(j,i) = j ;
                    break;
                case 3:
                    map_x.at<float>(j,i) = src.cols - i ;
                    map_y.at<float>(j,i) = src.rows - j ;
                    break;
            } // end of switch
        }
    }
    ind++;
}

- (void) remapping {
    /// Create dst, map_x and map_y with the same size as src:
    src = _lastFrame;
    
    dst.create( src.size(), src.type() );
    map_x.create( src.size(), CV_32FC1 );
    map_y.create( src.size(), CV_32FC1 );
    update_map();
    remap( src, dst, map_x, map_y, CV_INTER_LINEAR, BORDER_CONSTANT, Scalar(0,0, 0) );
    
    UIImage *tempImage = [[UIImage alloc] initWithCVMat:dst];
    UIImageView * imgView = [[UIImageView alloc] initWithImage:tempImage];
    imgView.frame = CGRectMake(0, 0, 320, 480);
    [tempImage release];
    [self.view addSubview:imgView];
    [imgView release];

}

// Rotate the image clockwise (or counter-clockwise if negative).
// Remember to free the returned image.
IplImage *rotate_Image(const IplImage *src, float angleDegrees)
{
	// Create a map_matrix, where the left 2x2 matrix
	// is the transform and the right 2x1 is the dimensions.
	float m[6];
	CvMat M = cvMat(2, 3, CV_32F, m);
	int w = src->width;
	int h = src->height;
	float angleRadians = angleDegrees * ((float)CV_PI / 180.0f);
	m[0] = (float)( cos(angleRadians) );
	m[1] = (float)( sin(angleRadians) );
	m[3] = -m[1];
	m[4] = m[0];
	m[2] = w*0.5f;  
	m[5] = h*0.5f;  
    
	// Make a spare image for the result
	CvSize sizeRotated;
	sizeRotated.width = cvRound(w);
	sizeRotated.height = cvRound(h);
    
	// Rotate
	IplImage *imageRotated = cvCreateImage( sizeRotated,
                                           src->depth, src->nChannels );
    
	// Transform the image
	cvGetQuadrangleSubPix( src, imageRotated, &M);
    
	return imageRotated;
}

// Returns a new image that is a cropped version (rectangular cut-out)
// of the original image.
IplImage* cropImage(const IplImage *img, const CvRect region)
{
	IplImage *imageCropped;
	CvSize size;
    
	if (img->width <= 0 || img->height <= 0
		|| region.width <= 0 || region.height <= 0) {
		//cerr << "ERROR in cropImage(): invalid dimensions." << endl;
		exit(1);
	}
    
	if (img->depth != IPL_DEPTH_8U) {
		//cerr << "ERROR in cropImage(): image depth is not 8." << endl;
		exit(1);
	}
    
	// Set the desired region of interest.
	cvSetImageROI((IplImage*)img, region);
	// Copy region of interest into a new iplImage and return it.
	size.width = region.width;
	size.height = region.height;
	imageCropped = cvCreateImage(size, IPL_DEPTH_8U, img->nChannels);
	cvCopy(img, imageCropped);	// Copy just the region.
    
	return imageCropped;
}

// Creates a new image copy that is of a desired size. The aspect ratio will
// be kept constant if 'keepAspectRatio' is true, by cropping undesired parts
// so that only pixels of the original image are shown, instead of adding
// extra blank space.
// Remember to free the new image later.
IplImage* resizeImage(const IplImage *origImg, int newWidth,
                      int newHeight, bool keepAspectRatio)
{
	IplImage *outImg = 0;
	int origWidth;
	int origHeight;
	if (origImg) {
		origWidth = origImg->width;
		origHeight = origImg->height;
	}
	if (newWidth <= 0 || newHeight <= 0 || origImg == 0
		|| origWidth <= 0 || origHeight <= 0) {
		//cerr << "ERROR: Bad desired image size of " << newWidth
		//	<< "x" << newHeight << " in resizeImage().\n";
		exit(1);
	}
    
	if (keepAspectRatio) {
		// Resize the image without changing its aspect ratio,
		// by cropping off the edges and enlarging the middle section.
		CvRect r;
		// input aspect ratio
		float origAspect = (origWidth / (float)origHeight);
		// output aspect ratio
		float newAspect = (newWidth / (float)newHeight);
		// crop width to be origHeight * newAspect
		if (origAspect > newAspect) {
			int tw = (origHeight * newWidth) / newHeight;
			r = cvRect((origWidth - tw)/2, 0, tw, origHeight);
		}
		else {	// crop height to be origWidth / newAspect
			int th = (origWidth * newHeight) / newWidth;
			r = cvRect(0, (origHeight - th)/2, origWidth, th);
		}
		IplImage *croppedImg = cropImage(origImg, r);
        
		// Call this function again, with the new aspect ratio image.
		// Will do a scaled image resize with the correct aspect ratio.
		outImg = resizeImage(croppedImg, newWidth, newHeight, false);
		cvReleaseImage( &croppedImg );
        
	}
	else {
        
		// Scale the image to the new dimensions,
		// even if the aspect ratio will be changed.
		outImg = cvCreateImage(cvSize(newWidth, newHeight),
                               origImg->depth, origImg->nChannels);
		if (newWidth > origImg->width && newHeight > origImg->height) {
			// Make the image larger
			cvResetImageROI((IplImage*)origImg);
			// CV_INTER_LINEAR: good at enlarging.
			// CV_INTER_CUBIC: good at enlarging.			
			cvResize(origImg, outImg, CV_INTER_LINEAR);
		}
		else {
			// Make the image smaller
			cvResetImageROI((IplImage*)origImg);
			// CV_INTER_AREA: good at shrinking (decimation) only.
			cvResize(origImg, outImg, CV_INTER_AREA);
		}
        
	}
	return outImg;
}

- (void) threeDRotationAndTranslation {
    int w = 0;
    int h = 0;
    int dist = 0;
    double alpha = 1;
    int f = 1;
    // Projection 2D -> 3D matrix
    Mat A1 = (Mat_<double>(4,3) <<
              1, 0, -w/2,
              0, 1, -h/2,
              0, 0,    0,
              0, 0,    1);
    
    // Rotation matrices around the X axis
    Mat R = (Mat_<double>(4, 4) <<
             1,          0,           0, 0,
             0, cos(alpha), -sin(alpha), 0,
             0, sin(alpha),  cos(alpha), 0,
             0,          0,           0, 1);
    
    // Translation matrix on the Z axis 
    Mat T = (Mat_<double>(4, 4) <<
             1, 0, 0, 0,
             0, 1, 0, 0,
             0, 0, 1, dist,
             0, 0, 0, 1);
    
    // Camera Intrisecs matrix 3D -> 2D
    Mat A2 = (Mat_<double>(3,4) <<
              f, 0, w/2, 0,
              0, f, h/2, 0,
              0, 0,   1, 0);
    
    Mat transfo = A2 * (T * (R * A1));
    
    UIImage *testImage = [UIImage imageNamed:@"card"];
    
    Mat source = [testImage CVMat];;
    Mat destination;
    
    warpPerspective(source, destination, transfo, source.size(), INTER_CUBIC | WARP_INVERSE_MAP);
    
    UIImage *tempImage = [[UIImage alloc] initWithCVMat:destination];
    UIImageView * imgView = [[UIImageView alloc] initWithImage:tempImage];
    imgView.frame = CGRectMake(0, 0, 320, 480);
    [tempImage release];
    [self.view addSubview:imgView];
    [imgView release];
}

- (id) init {
    self = [super init];
    
    if (self) {
        
        UIImage * img = [UIImage imageNamed:@"card.png"];
        
        _bg = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 320, 480)];
        _bg.image = img;
       [self.view addSubview:_bg];
        
        _touchImg = [[TouchImageView alloc] initWithFrame:CGRectMake(0, 0, 320, 480)];
        _touchImg.userInteractionEnabled = YES;
        [self.view addSubview:_touchImg];
        
        UIButton * button = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        button.frame = CGRectMake(100, 100, 50, 40);
        [button setTitle:@"crop" forState:UIControlStateNormal];
        [button addTarget:self action:@selector(didCropClicked) forControlEvents:UIControlEventTouchUpInside];
//        [self.view addSubview:button];
        
        //
        //[self threeDRotationAndTranslation];
        // Load a test image and demonstrate conversion between UIImage and cv::Mat
        UIImage *testImage = [UIImage imageNamed:@"card"];
        
        double t;
        int times = 10;
        
        /*
        //--------------------------------
        // Convert from UIImage to cv::Mat
        
        NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];
        
        t = (double)cv::getTickCount();
        
        for (int i = 0; i < times; i++)
        {
            cv::Mat tempMat = [testImage CVMat];
        }
        
        t = 1000 * ((double)cv::getTickCount() - t) / cv::getTickFrequency() / times;
        
        [pool release];
        
        NSLog(@"UIImage to cv::Mat: %gms", t);
        
        //------------------------------------------
        // Convert from UIImage to grayscale cv::Mat
        pool = [[NSAutoreleasePool alloc] init];
        
        t = (double)cv::getTickCount();
        
        for (int i = 0; i < times; i++)
        {
            cv::Mat tempMat = [testImage CVGrayscaleMat];
        }
        
        t = 1000 * ((double)cv::getTickCount() - t) / cv::getTickFrequency() / times;
        
        [pool release];
        
        NSLog(@"UIImage to grayscale cv::Mat: %gms", t);
        */
        
        IplImage * src = [testImage CreateIplImageFromUIImage:testImage];
        IplImage * dsc = resizeImage(src, 320, 480, true);//(src, 0);
        UIImage * imgDsc = [[UIImage alloc] initWithIplImage:dsc];
        UIImageView * imgView = [[UIImageView alloc] initWithImage:imgDsc];
        imgView.frame = CGRectMake(0, 0, 320, 480);
        [imgDsc release];
//        [self.view addSubview:imgView];
        [imgView release];
        
        //--------------------------------
        // Convert from cv::Mat to UIImage
        cv::Mat testMat = [testImage CVMat];
        _lastFrame = testMat;//rotateImage(testMat, 0);
//        [self remapping];
        
        // Process test image and force update of UI 
//        _lastFrame = testMat;
        
        std::vector<std::vector<cv::Point> > squares;
//        find_squares(_lastFrame, squares);
        
        // Convert from cv::Mat to UIImage 
        t = (double)cv::getTickCount();
        
        for (int i = 0; i < times; i++)
        {
            UIImage *tempImage = [[UIImage alloc] initWithCVMat:_lastFrame];
            UIImageView * imgView = [[UIImageView alloc] initWithImage:tempImage];
            imgView.frame = CGRectMake(10, 10, 300, 440);
            [tempImage release];
            [self.view addSubview:imgView];
            [imgView release];
           
        }
        
        t = 1000 * ((double)cv::getTickCount() - t) / cv::getTickFrequency() / times;
        
        NSLog(@"cv::Mat to UIImage: %gms", t);
        
        //test skew angles
        //[self compute_skew];
        
        /*
        IplImage * originImg = [testImage CreateIplImageFromUIImage:[UIImage imageNamed:@"test.JPG"]];
        IplImage * rotationImg = rotate(originImg, 90);
        UIImage * imgFinal = [testImage UIImageFromIplImage:rotationImg];
        
        UIImageView * imgView = [[UIImageView alloc] initWithImage:imgFinal];
        [imgFinal release];
        imgView.frame = CGRectMake(0, 0, 320, 480);
        [self.view addSubview:imgView];
        [imgView release];
         */
    }
    
    return self;
}

- (void) didCropClicked {
    UIImage * cropImg = [_bg.image cropImageWithPath:_touchImg._cropPath];
    _bg.image = cropImg;
}

- (void) dealloc {
    [super dealloc];
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
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
