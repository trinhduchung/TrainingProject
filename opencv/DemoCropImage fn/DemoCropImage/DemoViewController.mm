//
//  DemoViewController.m
//  DemoCropImage
//
//  Created by helios-team on 5/22/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "DemoViewController.h"
#import <ImageIO/ImageIO.h>
#import "TouchImageView.h"
#import "UIImage+Addition.h"
#import "UIImage+OpenCV.h"
@interface DemoViewController ()
- (void) initPicker;
@end

@implementation DemoViewController
@synthesize stillImageOutput = _stillImageOutput;
@synthesize preview = _preview;
@synthesize previewLayer = _previewLayer;
@synthesize captureOutput = _captureOutput;
@synthesize captureSession = _captureSession;
@synthesize bg = _bg;
using namespace cv;
- (id) init {
    self = [super init];
    
    if (self) {
        UIImage * img = [UIImage imageNamed:@"8H.png"];
        
        self.view.backgroundColor = [UIColor whiteColor];
        _bg = [[UIImageView alloc] initWithFrame:CGRectMake(0, 10, 320, 400)];
        _bg.contentMode = UIViewContentModeCenter;
        _bg.backgroundColor = [UIColor yellowColor];
        [self.view addSubview:_bg];
        
        float ratio = [self ratioForImage:img InImageView:_bg];
        img = [self imageWithImage:img scaledToSize:CGSizeMake(img.size.width * ratio, img.size.height * ratio)];
        _bg.image  = img;
        
        _touchImg = [[TouchImageView alloc] initWithFrame:CGRectMake(0, 10, 320, 400)];
        _touchImg.userInteractionEnabled = YES;
        [self.view addSubview:_touchImg];
        
        UIButton * button = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        button.frame = CGRectMake(130, 420, 50, 40);
        [button setTitle:@"crop" forState:UIControlStateNormal];
        [button addTarget:self action:@selector(didCropClicked) forControlEvents:UIControlEventTouchUpInside];
        [self.view addSubview:button];
        
        UIButton * btnPick = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        btnPick.frame = CGRectMake(10, 420, 50, 40);
        [btnPick setTitle:@"pick" forState:UIControlStateNormal];
        [btnPick addTarget:self action:@selector(didPickClicked) forControlEvents:UIControlEventTouchUpInside];
        [self.view addSubview:btnPick];
        
        UIButton * btnCapture = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        btnCapture.frame = CGRectMake(230, 420, 50, 40);
        [btnCapture setTitle:@"capture" forState:UIControlStateNormal];
        [btnCapture addTarget:self action:@selector(didCaptureClicked) forControlEvents:UIControlEventTouchUpInside];
        [self.view addSubview:btnCapture];
        
        _touchImg.hidden = NO;
        _bg.hidden = NO;
        
        
    }
    
    return self;
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    
}

- (float) ratioForImage:(UIImage *) image InImageView:(UIImageView *) imageView {
    float ratio = 1;
    float imgW = image.size.width;
    float imgH = image.size.height;
    
    float imgViewW = imageView.bounds.size.width;
    float imgViewH = imageView.bounds.size.height;
    
    float ratioW = (imgViewW + 0.1 )/ imgW;
    float ratioH = (imgViewH + 0.1 ) / imgH;
    
    ratioW <= ratioH ? ratio = ratioW : ratio = ratioH;
    
    return ratio;
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

- (void) didPickClicked {
    [self setupCaptureSession];
    //[self initPicker];
}

- (void) initPicker {
    if (_picker) {
        [_picker release];
        _picker = nil;
    }
    _picker = [[UIImagePickerController alloc] init];
    _picker.delegate = self;
    _picker.allowsEditing = YES;
    
#if TARGET_IPHONE_SIMULATOR
    _picker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
#else
    _picker.sourceType = UIImagePickerControllerSourceTypeCamera;
#endif
    [_picker setToolbarHidden:YES];
    [_picker setNavigationBarHidden:YES];
    [_picker setModalTransitionStyle:UIModalTransitionStyleCrossDissolve];
    _picker.wantsFullScreenLayout = YES;
    
    if (_picker) {
        [self presentModalViewController:_picker animated:YES];
    }
}

#pragma UIImagePickerController delegate
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info {
    
    UIImage *image;
    if (picker.sourceType == UIImagePickerControllerSourceTypePhotoLibrary) {
        image = [info objectForKey:UIImagePickerControllerEditedImage];
    } else {
        image = [info objectForKey:UIImagePickerControllerEditedImage];
    }
    
    image = [UIImage imageNamed:@"TestCrop2.jpg"];
    
    [_picker dismissModalViewControllerAnimated:YES];
    
    if (_picker) {
        [_picker release];
        _picker = nil;        
    }
    if (image) {
        NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];
        //

        IplImage * iplImg = [UIImage CreateIplImageFromUIImage:image];
        CvPoint result[4];
        CvMemStorage* storage = 0;
        storage = cvCreateMemStorage(0);
        CvSeq * squares = findSquares(iplImg, storage);
        drawSquares(iplImg, squares, result);
        
        CGPoint points[4];
        
        for (int i = 0;i < 4;i++) {
            result[i] = mapPointInRealImageToImageView(image.size.width, image.size.height, _bg.bounds.size.width, _bg.bounds.size.height, result[i]);
            NSLog(@"%d, %d", result[i].x, result[i].y);
            CGPoint point = CGPointMake(result[i].x, result[i].y);
            points[i] = point;
            NSLog(@"%f, %f", points[i].x, points[i].y);
        }
        
        [_touchImg initPathCVPoints:points];
        
        float ratio = [self ratioForImage:image InImageView:_bg];
        NSLog(@"ratio : %f", ratio);
        image = [self imageWithImage:image scaledToSize:CGSizeMake(image.size.width * ratio, image.size.height * ratio)];
        _bg.image  = image;
        
        _touchImg.hidden = NO;
        
//        [_touchImg resetPath];
        _bg.hidden = NO;
        
        image = nil;
        [pool release];          
    }
    
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker
{
    [picker dismissModalViewControllerAnimated:YES];
}

#pragma mark -
#pragma mark opencv find squares support methods
/**
 Map the coordinate of a pixel in real image to coordinate of that pixel in the showing imageview
 @ param imageWidth: width of the real image
 @ param imageHeight: height of the real image
 @ param viewWidth: width of the imageview
 @ param viewHeight: height of the imageview
 @ param point: coordinate of the pixel in real image
 @ return coordinate of the pixel in imageview
 **/
CvPoint mapPointInRealImageToImageView(int imageWidth, int imageHeight, int viewWidth, int viewHeight, CvPoint point) {
    CvPoint result;
    CvPoint newTop;
    //Find the ratio between real image and display size
    double ratio;
    ratio = imageWidth/viewWidth > imageHeight/viewHeight ? imageWidth/viewWidth : imageHeight/viewHeight;
    
    //Find coordinate of the new top-left pixel
    CvPoint newCenterPoint = cvPoint(viewWidth/2, viewHeight/2);
    newTop.x = newCenterPoint.x - 0.5*(imageWidth/ratio);
    newTop.y = newCenterPoint.y - 0.5*(imageHeight/ratio);
    
    //return result
    result.x = newTop.x + (point.x/ratio);
    result.y = newTop.y + (point.y/ratio);
    return result;
}

/**
 Map the coordinate of a pixel in displaying view to coordinate of that pixel in the real image
 @ param imageWidth: width of the real image
 @ param imageHeight: height of the real image
 @ param viewWidth: width of the imageview
 @ param viewHeight: height of the imageview
 @ param point: coordinate of the pixel in displaying view
 @ return coordinate of the pixel in real image
 **/
CvPoint mapPointInImageViewToRealImage(int imageWidth, int imageHeight, int viewWidth, int viewHeight, CvPoint point){
    CvPoint result;
    CvPoint top;
    //Find the ratio between real image and display size
    double ratio;
    ratio = imageWidth/viewWidth > imageHeight/viewHeight ? imageWidth/viewWidth : imageHeight/viewHeight;
    
    //Find coordinate of the top-left pixel in imageview
    CvPoint centerPoint = cvPoint(viewWidth/2, viewHeight/2);
    top.x = centerPoint.x - 0.5*(imageWidth/ratio);
    top.y = centerPoint.y - 0.5*(imageHeight/ratio);
    
    //return result
    result.x = (point.x - top.x)*ratio;
    result.y = (point.y - top.y)*ratio;  
    return result;
}

// helper function:
// finds a cosine of angle between vectors
// from pt0->pt1 and from pt0->pt2
double angle( CvPoint * pt1, CvPoint * pt2, CvPoint * pt0 )
{
    double dx1 = pt1->x - pt0->x;
    double dy1 = pt1->y - pt0->y;
    double dx2 = pt2->x - pt0->x;
    double dy2 = pt2->y - pt0->y;
    return (dx1*dx2 + dy1*dy2)/sqrt((dx1*dx1 + dy1*dy1)*(dx2*dx2 + dy2*dy2) + 1e-10);
}

int distant(CvPoint point1,CvPoint point2){
    return sqrt((point1.x - point2.x)*(point1.x - point2.x) + (point1.y - point2.y)*(point1.y - point2.y));
}

CvSeq* findSquares( IplImage* img, CvMemStorage* storage )
{
    int thresh = 50;
    CvSeq* contours;
    int i, c, l, N = 11;
    CvSize sz = cvSize( img->width & -2, img->height & -2 );
    IplImage* timg = cvCloneImage( img ); // make a copy of input image
    IplImage* gray = cvCreateImage( sz, 8, 1 ); 
    IplImage* pyr = cvCreateImage( cvSize(sz.width/2, sz.height/2), 8, 3 );
    IplImage* tgray;
    CvSeq* result;
    double s, t;
    // create empty sequence that will contain points -
    // 4 points per square (the square's vertices)
    CvSeq* squares = cvCreateSeq( 0, sizeof(CvSeq), sizeof(CvPoint), storage );
    
    // select the maximum ROI in the image
    // with the width and height divisible by 2
    cvSetImageROI( timg, cvRect( 0, 0, sz.width, sz.height ));
    
    // down-scale and upscale the image to filter out the noise
    cvPyrDown( timg, pyr, 7 );
    cvPyrUp( pyr, timg, 7 );
    tgray = cvCreateImage( sz, 8, 1 );
    
    // find squares in every color plane of the image
    for( c = 0; c < 3; c++ )
    {
        // extract the c-th color plane
        cvSetImageCOI( timg, c+1 );
        cvCopy( timg, tgray, 0 );
        
        // try several threshold levels
        for( l = 0; l < N; l++ )
        {
            // hack: use Canny instead of zero threshold level.
            // Canny helps to catch squares with gradient shading 
            if( l == 0 )
            {
                // apply Canny. Take the upper threshold from slider
                // and set the lower to 0 (which forces edges merging) 
                cvCanny( tgray, gray, 0, thresh, 5 );
                // dilate canny output to remove potential
                // holes between edge segments 
                cvDilate( gray, gray, 0, 1 );
            }
            else
            {
                // apply threshold if l!=0:
                //     tgray(x,y) = gray(x,y) < (l+1)*255/N ? 255 : 0
                cvThreshold( tgray, gray, (l+1)*255/N, 255, CV_THRESH_BINARY );
            }
            
            // find contours and store them all as a list
            cvFindContours( gray, storage, &contours, sizeof(CvContour),
                           CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE, cvPoint(0,0) );
            // test each contour
            while( contours )
            {
                CvRect rect;
                rect=cvBoundingRect(contours, NULL);
                if(rect.width < 300 || rect.height < 300 || rect.width > 1500 || rect.height > 1500){ //limited border result, remove small contour and too large contour in threshold 1500
                    // quit, go to the next contour
                    contours = contours->h_next;
                    //continue;
                } else {
                    //NSLog(@"Total of contour: %d Header size: %d", contours->total, contours->elem_size);
                    // approximate contour with accuracy proportional
                    // to the contour perimeter
                    result = cvApproxPoly( contours, sizeof(CvContour), storage,
                                          CV_POLY_APPROX_DP, cvContourPerimeter(contours)*0.02, 0 );
                    //cvDrawContours( img, contours, cvScalar(0, 255, 0), cvScalar(0, 255, 0),0 , 10, 8, cvPoint(0,0) );
                    // square contours should have 4 vertices after approximation
                    // relatively large area (to filter out noisy contours)
                    // and be convex.
                    // Note: absolute value of an area is used because
                    // area may be positive or negative - in accordance with the
                    // contour orientation
                    if( result->total == 4 &&
                       fabs(cvContourArea(result,CV_WHOLE_SEQ)) > 1000 &&
                       cvCheckContourConvexity(result) )
                    {
                        s = 0;
                        printf("ciclo for annidato fino a 5\t\n");
                        for( i = 0; i < 5; i++ )
                        {
                            // find minimum angle between joint
                            // edges (maximum of cosine)
                            if( i >= 2 )
                            {
                                t = fabs(angle(
                                               (CvPoint*)cvGetSeqElem( result, i ),
                                               (CvPoint*)cvGetSeqElem( result, i-2 ),
                                               (CvPoint*)cvGetSeqElem( result, i-1 )));
                                s = s > t ? s : t;
                            }
                        }
                        
                        // if cosines of all angles are small
                        // (all angles are ~90 degree) then write quandrange
                        // vertices to resultant sequence 
                        if( s < 0.3 )
                            for( i = 0; i < 4; i++ )
                                cvSeqPush( squares,
                                          (CvPoint*)cvGetSeqElem( result, i ));
                    }
                    
                    // take the next contour
                    contours = contours->h_next;
                }
                
            }
        }
    }
    
    // release all the temporary images
    cvReleaseImage( &gray );
    cvReleaseImage( &pyr );
    cvReleaseImage( &tgray );
    cvReleaseImage( &timg );
    
    return squares;
}

// the function draws all the squares in the image
 void drawSquares(IplImage* img ,CvSeq* squares, CvPoint result[4] )
{
    CvSeqReader reader;
    //IplImage* cpy = cvCloneImage( img );
    int i;
    
    // initialize reader of the sequence
    cvStartReadSeq( squares, &reader, 0 );
    
    //CvPoint maxRect[4], *drawRect = maxRect;
    double boundaryLength = 0;
    // read 4 sequence elements at a time (all vertices of a square)
    for( i = 0; i < squares->total; i += 4 )
    {
        CvPoint pt[4];
        
        // read 4 vertices
        CV_READ_SEQ_ELEM( pt[0], reader );
        CV_READ_SEQ_ELEM( pt[1], reader );
        CV_READ_SEQ_ELEM( pt[2], reader );
        CV_READ_SEQ_ELEM( pt[3], reader );
        
        //find the largest rectangle (the main object)
        if ((distant(pt[0], pt[1]) + distant(pt[1], pt[2]) + distant(pt[2], pt[3]) + distant(pt[0], pt[3])) > boundaryLength) {
            result[0].x = pt[0].x; 
            result[0].y = pt[0].y;
            
            result[1].x = pt[1].x; 
            result[1].y = pt[1].y;
            
            result[2].x = pt[2].x; 
            result[2].y = pt[2].y;
            
            result[3].x = pt[3].x; 
            result[3].y = pt[3].y;
        }
    }

    // draw the square as a closed polyline  
    //int count = 4;
    //cvPolyLine( img, &drawRect, &count, 1, 1, CV_RGB(0,255,0), 10, CV_AA, 0 );
}
/*
- (void) opencvFindSquare{
    CvMemStorage* storage = 0;
    NSString* names[] = { @"IMAG0156.jpg", 0 };
    for( int i = 0; names[i] != 0; i++ )
    {
        storage = cvCreateMemStorage(0);
        IplImage * image = CreateIplImageFromUIImage([UIImage imageNamed:names[i]]);
        CvSeq * squares = findSquares(image, storage);
        drawSquares(image, squares);
        UIImageWriteToSavedPhotosAlbum(UIImageFromIplImage(image), self, @selector(finishUIImageWriteToSavedPhotosAlbum:didFinishSavingWithError:contextInfo, nil);
                                       cvClearMemStorage( storage );
                                       }
*/

/* Transform a card and save it*/
- (void)doTransformAndSave:(UIImage*)srcImg :(CvPoint)topLeft :(CvPoint)topRight: (CvPoint) botRight :(CvPoint)botLeft{
    /*****************
     *Perspective Transform
     *****************/
    
    Point2f srcTri[4];
    Point2f dstTri[4];
    
    int finalImageWidth;
    int finalImageHeight;
    double ratio = distant(topLeft, topRight)/400 > distant(topRight, botRight)/320 ? distant(topLeft, topRight)/400 : distant(topRight, botRight)/320;
    
    finalImageWidth = distant(topLeft, topRight)/ratio;
    finalImageHeight = distant(topRight, botRight)/ratio;
    
    Mat rot_mat( 2, 3, CV_32FC1 );
    Mat warp_mat( 3, 3, CV_32FC1 );
    Mat src, warp_dst, final_mat;
    src = [srcImg CVMat];
    /// Set the dst image the same type and size as src
    warp_dst = Mat::zeros( finalImageHeight, finalImageWidth, src.type() );
    srcTri[0] = Point2f( topLeft.x, topLeft.y);
    srcTri[1] = Point2f( topRight.x, topRight.y);
    srcTri[2] = Point2f( botRight.x, botRight.y);
    srcTri[3] = Point2f( botLeft.x, botLeft.y);
    
    dstTri[0] = Point2f( 0, 0 );
    dstTri[1] = Point2f( finalImageWidth, 0);
    dstTri[2] = Point2f( finalImageWidth, finalImageHeight);
    dstTri[3] = Point2f( 0, finalImageHeight);
    warp_mat = getPerspectiveTransform(srcTri, dstTri);
    
    //warpPerspective(<#InputArray src#>, <#OutputArray dst#>, <#InputArray M#>, <#Size dsize#>)
    warpPerspective(src, warp_dst, warp_mat, warp_dst.size()); 
    
//    if (warp_dst.rows < warp_dst.cols) {
//        resize(warp_dst, final_mat, cvSize(150, 100));
//    } else {
//        resize(warp_dst, final_mat, cvSize(100, 150));
//    }
//    UIImage * result = [UIImage imageWithCVMat:final_mat];
    UIImage * result = [UIImage imageWithCVMat:warp_dst];//not final_mat
    
    _bg.image = result;
    _touchImg.hidden = YES;
    
    UIImageWriteToSavedPhotosAlbum(result, self, @selector(image:didFinishSavingWithError:contextInfo:), NULL);
}

- (void)image:(UIImage *) image didFinishSavingWithError: (NSError *) error contextInfo: (void *) contextInfo {
    NSLog(@"SAVE IMAGE COMPLETE");
    if(error != nil) {
        NSLog(@"ERROR SAVING:%@",[error localizedDescription]);
    }
}

- (void) didCropClicked {
    CvPoint top = mapPointInImageViewToRealImage(1920, 1080, _bg.bounds.size.width, _bg.bounds.size.height, cvPoint(_touchImg.top.x, _touchImg.top.y));
    
    CvPoint left = mapPointInImageViewToRealImage(1920, 1080, _bg.bounds.size.width, _bg.bounds.size.height, cvPoint(_touchImg.left.x, _touchImg.left.y));
    
    CvPoint bottom = mapPointInImageViewToRealImage(1920, 1080, _bg.bounds.size.width, _bg.bounds.size.height, cvPoint(_touchImg.bottom.x, _touchImg.bottom.y));
    
    CvPoint right = mapPointInImageViewToRealImage(1920, 1080, _bg.bounds.size.width, _bg.bounds.size.height, cvPoint(_touchImg.right.x, _touchImg.right.y));
    
    [self doTransformAndSave:_originalImage :top :left :bottom :right];
    
    //[self doTransformAndSave:_bg.image :cvPoint(_touchImg.top.x, _touchImg.top.y) :cvPoint(_touchImg.left.x, _touchImg.left.y) :cvPoint(_touchImg.bottom.x, _touchImg.bottom.y) :cvPoint(_touchImg.right.x, _touchImg.right.y)];
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

// Create and configure a capture session and start it running
- (void)setupCaptureSession 
{
    //satify release
    if (self.captureSession) {
        [self.captureSession release];
        self.captureSession = nil;
    }
    
    if (self.stillImageOutput) {
        [self.stillImageOutput release];
        self.stillImageOutput = nil;
    }
    
    NSError *error = nil;
    
    // Create the session
    AVCaptureSession *session = [[AVCaptureSession alloc] init];
    
    // Configure the session to produce lower resolution video frames, if your 
    // processing algorithm can cope. We'll specify medium quality for the
    // chosen device.
    session.sessionPreset = AVCaptureSessionPreset1920x1080;
    
    // Find a suitable AVCaptureDevice
    AVCaptureDevice *device = [AVCaptureDevice
                               defaultDeviceWithMediaType:AVMediaTypeVideo];
    
    // Create a device input with the device and add it to the session.
    AVCaptureDeviceInput *input = [AVCaptureDeviceInput deviceInputWithDevice:device 
                                                                        error:&error];
    if (!input)
    {
        NSLog(@"PANIC: no media input");
    }
    [session addInput:input];
    
    
    // Create a VideoDataOutput and add it to the session
    AVCaptureVideoDataOutput *output = [[AVCaptureVideoDataOutput alloc] init];
    [session addOutput:output];
    
    // Configure your output.
    //dispatch_queue_t queue = dispatch_queue_create("myQueue", NULL);
    //[output setSampleBufferDelegate:self queue:queue];
    //dispatch_release(queue);
    
    // Specify the pixel format
    output.videoSettings = 
    [NSDictionary dictionaryWithObject:
     [NSNumber numberWithInt:kCVPixelFormatType_32BGRA] 
                                forKey:(id)kCVPixelBufferPixelFormatTypeKey];
    
    
    // If you wish to cap the frame rate to a known value, such as 15 fps, set 
    // minFrameDuration.
    AVCaptureConnection *conn = [output connectionWithMediaType:AVMediaTypeVideo];
    
    if (conn.supportsVideoMinFrameDuration)
        conn.videoMinFrameDuration = CMTimeMake(5,1);
    if (conn.supportsVideoMaxFrameDuration)
        conn.videoMaxFrameDuration = CMTimeMake(5,1);
    
    [self setCaptureOutput:output];//not set
     /*
    self.stillImageOutput = [[AVCaptureStillImageOutput alloc] init];
    NSDictionary *outputSettings = [[NSDictionary alloc] initWithObjectsAndKeys: AVVideoCodecJPEG, AVVideoCodecKey, nil];
    [self.stillImageOutput setOutputSettings:outputSettings];
    
    [session addOutput:self.stillImageOutput];
    */
    ///set preview layer

    AVCaptureVideoPreviewLayer *previewLayer = [AVCaptureVideoPreviewLayer layerWithSession:session];
    CGRect videoRect = CGRectMake(0.0, 10.0, 320.0, 400.0);
    previewLayer.frame = videoRect; // Assume you want the preview layer to fill the view.
    [previewLayer setVideoGravity:AVLayerVideoGravityResizeAspectFill];
    [_bg.layer addSublayer:previewLayer];
    self.previewLayer = [previewLayer retain];
    
    // Start the session running to start the flow of data
    [session startRunning];
    
    // Assign session to an ivar.
    [self setSession:session];
}


- (UIView *) videoPreviewWithFrame:(CGRect) frame {
    AVCaptureVideoPreviewLayer *tempPreviewLayer = [[AVCaptureVideoPreviewLayer alloc] initWithSession:[self captureSession]];
    [tempPreviewLayer setVideoGravity:AVLayerVideoGravityResizeAspectFill];
    tempPreviewLayer.frame = frame;
    
    UIView* tempView = [[UIView alloc] init];
    [tempView.layer addSublayer:tempPreviewLayer];
    tempView.frame = frame;
    
    [tempPreviewLayer autorelease];
    return [tempView autorelease];
}

// Delegate routine that is called when a sample buffer was written
- (void)captureOutput:(AVCaptureOutput *)captureOutput 
didOutputSampleBuffer:(CMSampleBufferRef)sampleBuffer 
       fromConnection:(AVCaptureConnection *)connection
{ 
    NSLog(@"captureOutput: didOutputSampleBufferFromConnection");
    
    // Create a UIImage from the sample buffer data
    //NSData *data = [AVCaptureStillImageOutput jpegStillImageNSDataRepresentation:sampleBuffer];//
    UIImage *image = [self imageFromSampleBuffer:sampleBuffer];//[[UIImage alloc] initWithData:data];
    _originalImage = [image retain];
    //< Add your code here that uses the image >
    
    //detect
    
    IplImage * iplImg = [UIImage CreateIplImageFromUIImage:image];
    CvPoint result[4];
    CvMemStorage* storage = 0;
    storage = cvCreateMemStorage(0);
    CvSeq * squares = findSquares(iplImg, storage);
    drawSquares(iplImg, squares, result);
    
    CGPoint points[4];
    
    for (int i = 0;i < 4;i++) {
        result[i] = mapPointInRealImageToImageView(image.size.width, image.size.height, _bg.bounds.size.width, _bg.bounds.size.height, result[i]);
        NSLog(@"%d, %d", result[i].x, result[i].y);
        CGPoint point = CGPointMake(result[i].x, result[i].y);
        points[i] = point;
        NSLog(@"%f, %f", points[i].x, points[i].y);
    }
    
    [_touchImg initPathCVPoints:points];
    
    ///resize image
    float ratio = [self ratioForImage:image InImageView:_bg];
    image = [self imageWithImage:image scaledToSize:CGSizeMake(image.size.width * ratio, image.size.height * ratio)];
    _bg.image  = image;
    NSLog(@"w = %f, h = %f", image.size.width, image.size.height);
    
    _touchImg.hidden = NO;
}


// Create a UIImage from sample buffer data
- (UIImage *) imageFromSampleBuffer:(CMSampleBufferRef) sampleBuffer 
{
    NSLog(@"imageFromSampleBuffer: called");
    // Get a CMSampleBuffer's Core Video image buffer for the media data
    CVImageBufferRef imageBuffer = CMSampleBufferGetImageBuffer(sampleBuffer); 
    // Lock the base address of the pixel buffer
    CVPixelBufferLockBaseAddress(imageBuffer, 0); 
    
    // Get the number of bytes per row for the pixel buffer
    void *baseAddress = CVPixelBufferGetBaseAddress(imageBuffer); 
    
    // Get the number of bytes per row for the pixel buffer
    size_t bytesPerRow = CVPixelBufferGetBytesPerRow(imageBuffer); 
    // Get the pixel buffer width and height
    size_t width = CVPixelBufferGetWidth(imageBuffer); 
    size_t height = CVPixelBufferGetHeight(imageBuffer); 
    
    // Create a device-dependent RGB color space
    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB(); 
    
    // Create a bitmap graphics context with the sample buffer data
    CGContextRef context = CGBitmapContextCreate(baseAddress, width, height, 8, 
                                                 bytesPerRow, colorSpace, kCGBitmapByteOrder32Little | kCGImageAlphaPremultipliedFirst); 
    // Create a Quartz image from the pixel data in the bitmap graphics context
    CGImageRef quartzImage = CGBitmapContextCreateImage(context); 
    // Unlock the pixel buffer
    CVPixelBufferUnlockBaseAddress(imageBuffer,0);
    
    
    // Free up the context and color space
    CGContextRelease(context); 
    CGColorSpaceRelease(colorSpace);
    
    // Create an image object from the Quartz image
    UIImage *image = [UIImage imageWithCGImage:quartzImage];
    
    // Release the Quartz image
    CGImageRelease(quartzImage);
    
    return (image);
}

-(void)setSession:(AVCaptureSession *)session
{
    NSLog(@"setting session...");
    self.captureSession = session;
}

- (void) didCaptureClicked {
    // Configure your output.
    
    
    dispatch_queue_t queue = dispatch_queue_create("myQueue", NULL);
    [self.captureOutput setSampleBufferDelegate:self queue:queue];
    dispatch_release(queue);
    
    [self.captureSession stopRunning];
    [self.preview removeFromSuperview];
    [self.previewLayer removeFromSuperlayer];
     
    /*
    //config out put
    [self.previewLayer removeFromSuperlayer];

    AVCaptureConnection *videoConnection = nil;
	for (AVCaptureConnection *connection in self.stillImageOutput.connections)
	{
		for (AVCaptureInputPort *port in [connection inputPorts])
		{
			if ([[port mediaType] isEqual:AVMediaTypeVideo] )
			{
				videoConnection = connection;
				break;
			}
		}
		if (videoConnection) { break; }
	}
    
	NSLog(@"about to request a capture from: %@", self.stillImageOutput);
	[self.stillImageOutput captureStillImageAsynchronouslyFromConnection:videoConnection completionHandler: ^(CMSampleBufferRef imageSampleBuffer, NSError *error)
     {
		 CFDictionaryRef exifAttachments = (CFDictionaryRef)CMGetAttachment( imageSampleBuffer, kCGImagePropertyExifDictionary, NULL);
		 if (exifAttachments)
		 {
             // Do something with the attachments.
             NSLog(@"attachements: %@", exifAttachments);
		 }
         else
             NSLog(@"no attachments");
         
         NSData *imageData = [AVCaptureStillImageOutput jpegStillImageNSDataRepresentation:imageSampleBuffer];
         UIImage *image = [[UIImage alloc] initWithData:imageData];
         
         ///do some things
         _originalImage = [image retain];
         //< Add your code here that uses the image >
         [self.captureSession stopRunning];
         
         //detect
         
         IplImage * iplImg = [UIImage CreateIplImageFromUIImage:image];
         CvPoint result[4];
         CvMemStorage* storage = 0;
         storage = cvCreateMemStorage(0);
         CvSeq * squares = findSquares(iplImg, storage);
         drawSquares(iplImg, squares, result);
         
         CGPoint points[4];
         
         for (int i = 0;i < 4;i++) {
             result[i] = mapPointInRealImageToImageView(image.size.width, image.size.height, _bg.bounds.size.width, _bg.bounds.size.height, result[i]);
             NSLog(@"%d, %d", result[i].x, result[i].y);
             CGPoint point = CGPointMake(result[i].x, result[i].y);
             points[i] = point;
             NSLog(@"%f, %f", points[i].x, points[i].y);
         }
         
         [_touchImg initPathCVPoints:points];
         
         ///resize image
         float ratio = [self ratioForImage:image InImageView:_bg];
         image = [self imageWithImage:image scaledToSize:CGSizeMake(image.size.width * ratio, image.size.height * ratio)];
         _bg.image  = image;
         NSLog(@"w = %f, h = %f", image.size.width, image.size.height);
         
         _touchImg.hidden = NO;
         
	 }];
     */
}

@end
