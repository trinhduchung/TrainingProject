//
//  MainViewController.m
//  OpenCVClient
//
//  Created by helios-team on 6/18/12.
//  Copyright (c) 2012 Aptogo Limited. All rights reserved.
//

#import "MainViewController.h"
#import "UIImage+OpenCV.h"
using namespace cv;
using namespace std;

@interface MainViewController ()

@end

double angle( cv::Point pt1, cv::Point pt2, cv::Point pt0 ) {
    double dx1 = pt1.x - pt0.x;
    double dy1 = pt1.y - pt0.y;
    double dx2 = pt2.x - pt0.x;
    double dy2 = pt2.y - pt0.y;
    return (dx1*dx2 + dy1*dy2)/sqrt((dx1*dx1 + dy1*dy1)*(dx2*dx2 + dy2*dy2) + 1e-10);
}

void find_squares(cv::Mat& image, std::vector<std::vector<cv::Point> >& squares)
{
    // blur will enhance edge detection
    cv::Mat blurred(image);
    medianBlur(image, blurred, 9);
    
    cv::Mat gray0(blurred.size(), CV_8U), gray;
    std::vector<std::vector<cv::Point> > contours;
    
    // find squares in every color plane of the image
    for (int c = 0; c < 3; c++)
    {
        int ch[] = {c, 0};
        mixChannels(&blurred, 1, &gray0, 1, ch, 1);
        
        // try several threshold levels
        const int threshold_level = 1;
        for (int l = 0; l < threshold_level; l++)
        {
            // Use Canny instead of zero threshold level!
            // Canny helps to catch squares with gradient shading
            if (l == 0)
            {
                Canny(gray0, gray, 10, 50, 3); // 
                
                // Dilate helps to remove potential holes between edge segments
                dilate(gray, gray, cv::Mat(), cv::Point(-1,-1));
            }
            else
            {
                gray = gray0 >= (l+1) * 255 / threshold_level;
            }
            
            // Find contours and store them in a list
            findContours(gray, contours, CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);
            
            // Test contours
            std::vector<cv::Point> approx;
            for (size_t i = 0; i < contours.size(); i++)
            {
                // approximate contour with accuracy proportional
                // to the contour perimeter
                approxPolyDP(cv::Mat(contours[i]), approx, arcLength(cv::Mat(contours[i]), true)*0.02, true);
                
                // Note: absolute value of an area is used because
                // area may be positive or negative - in accordance with the
                // contour orientation
                if (approx.size() == 4 &&
                    fabs(contourArea(cv::Mat(approx))) > 1000 &&
                    isContourConvex(cv::Mat(approx)))
                {
                    double maxCosine = 0;
                    
                    for (int j = 2; j < 5; j++)
                    {
                        double cosine = fabs(angle(approx[j%4], approx[j-2], approx[j-1]));
                        maxCosine = MAX(maxCosine, cosine);
                    }
                    
                    if (maxCosine < 0.3)
                        squares.push_back(approx);
                }
            }
        }
    }
}

Point2f computeIntersect(Vec2f line1, Vec2f line2);
vector<Point2f> lineToPointPair(Vec2f line);
bool acceptLinePair(Vec2f line1, Vec2f line2, float minTheta);

bool acceptLinePair(Vec2f line1, Vec2f line2, float minTheta)
{
    float theta1 = line1[1], theta2 = line2[1];
    
    if(theta1 < minTheta)
    {
        theta1 += CV_PI; // dealing with 0 and 180 ambiguities...
    }
    
    if(theta2 < minTheta)
    {
        theta2 += CV_PI; // dealing with 0 and 180 ambiguities...
    }
    
    return abs(theta1 - theta2) > minTheta;
}

// the long nasty wikipedia line-intersection equation...bleh...
Point2f computeIntersect(Vec2f line1, Vec2f line2)
{
    vector<Point2f> p1 = lineToPointPair(line1);
    vector<Point2f> p2 = lineToPointPair(line2);
    
    float denom = (p1[0].x - p1[1].x)*(p2[0].y - p2[1].y) - (p1[0].y - p1[1].y)*(p2[0].x - p2[1].x);
    Point2f intersect(((p1[0].x*p1[1].y - p1[0].y*p1[1].x)*(p2[0].x - p2[1].x) -
                       (p1[0].x - p1[1].x)*(p2[0].x*p2[1].y - p2[0].y*p2[1].x)) / denom,
                      ((p1[0].x*p1[1].y - p1[0].y*p1[1].x)*(p2[0].y - p2[1].y) -
                       (p1[0].y - p1[1].y)*(p2[0].x*p2[1].y - p2[0].y*p2[1].x)) / denom);
    
    return intersect;
}

vector<Point2f> lineToPointPair(Vec2f line)
{
    vector<Point2f> points;
    
    float r = line[0], t = line[1];
    double cos_t = cos(t), sin_t = sin(t);
    double x0 = r*cos_t, y0 = r*sin_t;
    double alpha = 1000;
    
    points.push_back(Point2f(x0 + alpha*(-sin_t), y0 + alpha*cos_t));
    points.push_back(Point2f(x0 - alpha*(-sin_t), y0 - alpha*cos_t));
    
    return points;
}

void iHoughLines(Mat &occludedSquare) {
    resize(occludedSquare, occludedSquare, cv::Size(0, 0), 0.25, 0.25);
    
    Mat occludedSquare8u;
    cvtColor(occludedSquare, occludedSquare8u, CV_BGR2GRAY);
    
    Mat thresh;
    threshold(occludedSquare8u, thresh, 200.0, 255.0, THRESH_BINARY);
    
    GaussianBlur(thresh, thresh, cv::Size(7, 7), 2.0, 2.0);
    
    Mat edges;
    Canny(thresh, edges, 66.0, 133.0, 3);
    
    vector<Vec2f> lines;
    HoughLines( edges, lines, 1, CV_PI/180, 50, 0, 0 );
    
    cout << "Detected " << lines.size() << " lines." << endl;
    
    // compute the intersection from the lines detected...
    vector<Point2f> intersections;
    for( size_t i = 0; i < lines.size(); i++ )
    {
        for(size_t j = 0; j < lines.size(); j++)
        {
            Vec2f line1 = lines[i];
            Vec2f line2 = lines[j];
            if(acceptLinePair(line1, line2, CV_PI / 32))
            {
                Point2f intersection = computeIntersect(line1, line2);
                intersections.push_back(intersection);
            }
        }
        
    }
    
    if(intersections.size() > 0)
    {
        vector<Point2f>::iterator i;
        for(i = intersections.begin(); i != intersections.end(); ++i)
        {
            cout << "Intersection is " << i->x << ", " << i->y << endl;
            circle(occludedSquare, *i, 1, Scalar(0, 255, 0), 3);
        }
    }
}

@implementation MainViewController

- (id)init
{
    self = [super init];
    if (self) {
        // Custom initialization
        _mainButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        [_mainButton addTarget:self action:@selector(didButtonClicked) forControlEvents:UIControlEventTouchUpInside];
        _mainButton.frame = CGRectMake(10, 10, 30, 30);
        [self.view addSubview:_mainButton];
    }
    return self;
}

- (void) didButtonClicked {
    UIImagePickerController * picker = [[UIImagePickerController alloc] init];
    picker.delegate = self;
    picker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
    [self presentModalViewController:picker animated:YES];
    [picker release];
}

#pragma mark - UIImagePickerControllerDelegate implementation

RNG rng(12345);

- (void)imagePickerController:(UIImagePickerController *)picker
        didFinishPickingImage:(UIImage *)image
                  editingInfo:(NSDictionary *)editingInfo
{
    [picker dismissModalViewControllerAnimated:YES];
    
    cv::Mat mat = [image CVMat];
    
    std::vector<std::vector<cv::Point> > squares;
    
    find_squares(mat, squares);
    
    UIImage *tempImage = [[UIImage alloc] initWithCVMat:mat];
    IplImage * iplImg = [tempImage CreateIplImageFromUIImage:tempImage];
    
    vector<cv::Rect> boundRect( squares.size() );
    vector<Point2f>center( squares.size() );
    vector<float>radius( squares.size() );
    for (size_t i = 0; i < squares.size(); i++)
    {
        approxPolyDP( Mat(squares[i]), squares[i], 3, true );
        boundRect[i] = boundingRect( Mat(squares[i]) );
        minEnclosingCircle( squares[i], center[i], radius[i] );
    }
    
    for (int i = 0; i < squares.size();i++) {
        Scalar color = Scalar( rng.uniform(0, 255), rng.uniform(0,255), rng.uniform(0,255) );
        cvRectangle(iplImg, boundRect[i].tl(),boundRect[i].br(), color, 10, 8, 0);
    }
    
    UIImageView * imgView = [[UIImageView alloc] initWithFrame:CGRectMake(10, 10, 300, 440)];
//    iHoughLines(mat);
    imgView.image = [tempImage UIImageFromIplImage:iplImg];
    
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
