//
//  FindObjectViewController.m
//  OpenCVClient
//
//  Created by helios-team on 6/20/12.
//  Copyright (c) 2012 Aptogo Limited. All rights reserved.
//

#import "FindObjectViewController.h"
#import "UIImage+OpenCV.h"
using namespace std;
using namespace cv;


@interface FindObjectViewController ()
- (void) showAlert;
@end

@implementation FindObjectViewController
@synthesize img1, galeryView;
@synthesize imgCorrespond;
@synthesize pickedImage = _pickedImage;
@synthesize listCard;

#pragma mark - C++ 
void help()
{
    printf(
           "This program demonstrated the use of the SURF Detector and Descriptor using\n"
           "either FLANN (fast approx nearst neighbor classification) or brute force matching\n"
           "on planar objects.\n"
           );
    return;
}

// define whether to use approximate nearest-neighbor search
#define USE_FLANN

IplImage* image = 0;

double
compareSURFDescriptors( const float* d1, const float* d2, double best, int length )
{
    double total_cost = 0;
    assert( length % 4 == 0 );
    for( int i = 0; i < length; i += 4 )
    {
        double t0 = d1[i  ] - d2[i  ];
        double t1 = d1[i+1] - d2[i+1];
        double t2 = d1[i+2] - d2[i+2];
        double t3 = d1[i+3] - d2[i+3];
        total_cost += t0*t0 + t1*t1 + t2*t2 + t3*t3;
        if( total_cost > best )
            break;
    }
    return total_cost;
}


int
naiveNearestNeighbor( const float* vec, int laplacian,
                     const CvSeq* model_keypoints,
                     const CvSeq* model_descriptors )
{
    int length = (int)(model_descriptors->elem_size/sizeof(float));
    int i, neighbor = -1;
    double d, dist1 = 1e6, dist2 = 1e6;
    CvSeqReader reader, kreader;
    cvStartReadSeq( model_keypoints, &kreader, 0 );
    cvStartReadSeq( model_descriptors, &reader, 0 );
    
    for( i = 0; i < model_descriptors->total; i++ )
    {
        const CvSURFPoint* kp = (const CvSURFPoint*)kreader.ptr;
        const float* mvec = (const float*)reader.ptr;
    	CV_NEXT_SEQ_ELEM( kreader.seq->elem_size, kreader );
        CV_NEXT_SEQ_ELEM( reader.seq->elem_size, reader );
        if( laplacian != kp->laplacian )
            continue;
        d = compareSURFDescriptors( vec, mvec, dist2, length );
        if( d < dist1 )
        {
            dist2 = dist1;
            dist1 = d;
            neighbor = i;
        }
        else if ( d < dist2 )
            dist2 = d;
    }
    if ( dist1 < 0.6*dist2 )
        return neighbor;
    return -1;
}

void
findPairs( const CvSeq* objectKeypoints, const CvSeq* objectDescriptors,
          const CvSeq* imageKeypoints, const CvSeq* imageDescriptors, vector<int>& ptpairs )
{
    int i;
    CvSeqReader reader, kreader;
    cvStartReadSeq( objectKeypoints, &kreader );
    cvStartReadSeq( objectDescriptors, &reader );
    ptpairs.clear();
    
    for( i = 0; i < objectDescriptors->total; i++ )
    {
        const CvSURFPoint* kp = (const CvSURFPoint*)kreader.ptr;
        const float* descriptor = (const float*)reader.ptr;
        CV_NEXT_SEQ_ELEM( kreader.seq->elem_size, kreader );
        CV_NEXT_SEQ_ELEM( reader.seq->elem_size, reader );
        int nearest_neighbor = naiveNearestNeighbor( descriptor, kp->laplacian, imageKeypoints, imageDescriptors );
        if( nearest_neighbor >= 0 )
        {
            ptpairs.push_back(i);
            ptpairs.push_back(nearest_neighbor);
        }
    }
}


void
flannFindPairs( const CvSeq*, const CvSeq* objectDescriptors,
               const CvSeq*, const CvSeq* imageDescriptors, vector<int>& ptpairs )
{
	int length = (int)(objectDescriptors->elem_size/sizeof(float));
    
    cv::Mat m_object(objectDescriptors->total, length, CV_32F);
	cv::Mat m_image(imageDescriptors->total, length, CV_32F);
    
    
	// copy descriptors
    CvSeqReader obj_reader;
	float* obj_ptr = m_object.ptr<float>(0);
    cvStartReadSeq( objectDescriptors, &obj_reader );
    for(int i = 0; i < objectDescriptors->total; i++ )
    {
        const float* descriptor = (const float*)obj_reader.ptr;
        CV_NEXT_SEQ_ELEM( obj_reader.seq->elem_size, obj_reader );
        memcpy(obj_ptr, descriptor, length*sizeof(float));
        obj_ptr += length;
    }
    CvSeqReader img_reader;
	float* img_ptr = m_image.ptr<float>(0);
    cvStartReadSeq( imageDescriptors, &img_reader );
    for(int i = 0; i < imageDescriptors->total; i++ )
    {
        const float* descriptor = (const float*)img_reader.ptr;
        CV_NEXT_SEQ_ELEM( img_reader.seq->elem_size, img_reader );
        memcpy(img_ptr, descriptor, length*sizeof(float));
        img_ptr += length;
    }
    
    // find nearest neighbors using FLANN
    cv::Mat m_indices(objectDescriptors->total, 2, CV_32S);
    cv::Mat m_dists(objectDescriptors->total, 2, CV_32F);
    cv::flann::Index flann_index(m_image, cv::flann::KDTreeIndexParams(4));  // using 4 randomized kdtrees
    flann_index.knnSearch(m_object, m_indices, m_dists, 2, cv::flann::SearchParams(64) ); // maximum number of leafs checked
    
    int* indices_ptr = m_indices.ptr<int>(0);
    float* dists_ptr = m_dists.ptr<float>(0);
    for (int i=0;i<m_indices.rows;++i) {
    	if (dists_ptr[2*i]<0.6*dists_ptr[2*i+1]) {
    		ptpairs.push_back(i);
    		ptpairs.push_back(indices_ptr[2*i]);
    	}
    }
}


/* a rough implementation for object location */
int
locatePlanarObject( const CvSeq* objectKeypoints, const CvSeq* objectDescriptors,
                   const CvSeq* imageKeypoints, const CvSeq* imageDescriptors,
                   const CvPoint src_corners[4], CvPoint dst_corners[4] )
{
    double h[9];
    CvMat _h = cvMat(3, 3, CV_64F, h);
    vector<int> ptpairs;
    vector<CvPoint2D32f> pt1, pt2;
    CvMat _pt1, _pt2;
    int i, n;
    
#ifdef USE_FLANN
    flannFindPairs( objectKeypoints, objectDescriptors, imageKeypoints, imageDescriptors, ptpairs );
#else
    findPairs( objectKeypoints, objectDescriptors, imageKeypoints, imageDescriptors, ptpairs );
#endif
    
    n = (int)(ptpairs.size()/2);
    if( n < 4 )
        return 0;
    
    pt1.resize(n);
    pt2.resize(n);
    for( i = 0; i < n; i++ )
    {
        pt1[i] = ((CvSURFPoint*)cvGetSeqElem(objectKeypoints,ptpairs[i*2]))->pt;
        pt2[i] = ((CvSURFPoint*)cvGetSeqElem(imageKeypoints,ptpairs[i*2+1]))->pt;
    }
    
    _pt1 = cvMat(1, n, CV_32FC2, &pt1[0] );
    _pt2 = cvMat(1, n, CV_32FC2, &pt2[0] );
    if( !cvFindHomography( &_pt1, &_pt2, &_h, CV_RANSAC, 5 ))
        return 0;
    
    for( i = 0; i < 4; i++ )
    {
        double x = src_corners[i].x, y = src_corners[i].y;
        double Z = 1./(h[6]*x + h[7]*y + h[8]);
        double X = (h[0]*x + h[1]*y + h[2])*Z;
        double Y = (h[3]*x + h[4]*y + h[5])*Z;
        dst_corners[i] = cvPoint(cvRound(X), cvRound(Y));
    }
    
    return 1;
}

IplImage * convertRGB2Grayscale(IplImage * src) {
    /* get image properties */
    int width  = src->width;
    int height = src->height;
    
    /* create new image for the grayscale version */
    IplImage *dst = cvCreateImage( cvSize( width, height ), IPL_DEPTH_8U, 1 );
    
    /* CV_RGB2GRAY: convert RGB image to grayscale */
    cvCvtColor( src, dst, CV_RGB2GRAY );
    
    return dst;
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        
        //help();
        
    }
    return self;
}

- (BOOL) findObject:(IplImage *) object andScene:(IplImage *) image {
    
    BOOL success = FALSE;
//    NSTimeInterval start = [[NSDate date] timeIntervalSince1970];
    
//    double tt = (double)cvGetTickCount();
    CvMemStorage* storage = cvCreateMemStorage(0);
    
    static CvScalar colors[] = 
    {
        {{0,0,255}},
        {{0,128,255}},
        {{0,255,255}},
        {{0,255,0}},
        {{255,128,0}},
        {{255,255,0}},
        {{255,0,0}},
        {{255,0,255}},
        {{255,255,255}}
    };
    
    IplImage* object_color = cvCreateImage(cvGetSize(object), 8, 3);
    cvCvtColor( object, object_color, CV_GRAY2BGR );
    
    CvSeq* objectKeypoints = 0, *objectDescriptors = 0;
    CvSeq* imageKeypoints = 0, *imageDescriptors = 0;
    int i;
    CvSURFParams params = cvSURFParams(500, 1);
    
    //        double tt = (double)cvGetTickCount();
    cvExtractSURF( object, 0, &objectKeypoints, &objectDescriptors, storage, params );
//    printf("Object Descriptors: %d\n", objectDescriptors->total);
    
    cvExtractSURF( image, 0, &imageKeypoints, &imageDescriptors, storage, params );
//    printf("Image Descriptors: %d\n", imageDescriptors->total);
    //        tt = (double)cvGetTickCount() - tt;
    
    //        printf( "Extraction time = %gms\n", tt/(cvGetTickFrequency()*1000.));
    
    CvPoint src_corners[4] = {{0,0}, {object->width,0}, {object->width, object->height}, {0, object->height}};
    CvPoint dst_corners[4];
    IplImage* correspond = cvCreateImage( cvSize(image->width, object->height+image->height), 8, 1 );
    cvSetImageROI( correspond, cvRect( 0, 0, object->width, object->height ) );
    cvCopy( object, correspond );
    cvSetImageROI( correspond, cvRect( 0, object->height, correspond->width, correspond->height ) );
    cvCopy( image, correspond );
    cvResetImageROI( correspond );
    
#ifdef USE_FLANN
//    printf("Using approximate nearest neighbor search\n");
#endif
    
    if(locatePlanarObject( objectKeypoints, objectDescriptors, imageKeypoints,
                          imageDescriptors, src_corners, dst_corners ) == 1)
    {
        NSLog(@"object found %@", [galeryView getCurrentCard]);
        success = TRUE;
        for( i = 0; i < 4; i++ )
        {
            CvPoint r1 = dst_corners[i%4];
            CvPoint r2 = dst_corners[(i+1)%4];
            cvLine( correspond, cvPoint(r1.x, r1.y+object->height ),
                   cvPoint(r2.x, r2.y+object->height ), colors[8] );
        }
    } else {
        success = FALSE;
        NSLog(@"object not found %@", [galeryView getCurrentCard]);
    }
    vector<int> ptpairs;
#ifdef USE_FLANN
    flannFindPairs( objectKeypoints, objectDescriptors, imageKeypoints, imageDescriptors, ptpairs );
#else
    findPairs( objectKeypoints, objectDescriptors, imageKeypoints, imageDescriptors, ptpairs );
#endif
    for( i = 0; i < (int)ptpairs.size(); i += 2 )
    {
        CvSURFPoint* r1 = (CvSURFPoint*)cvGetSeqElem( objectKeypoints, ptpairs[i] );
        CvSURFPoint* r2 = (CvSURFPoint*)cvGetSeqElem( imageKeypoints, ptpairs[i+1] );
        cvLine( correspond, cvPointFrom32f(r1->pt),
               cvPoint(cvRound(r2->pt.x), cvRound(r2->pt.y+object->height)), colors[8] );
    }
    
    //show object correspond
    //cvShowImage( "Object Correspond", correspond );
    IplImage *correspondColor = cvCreateImage(cvGetSize(correspond), 8, 3);
    cvCvtColor( correspond, correspondColor, CV_GRAY2BGR );
    UIImage * img_correspond = [[UIImage alloc] initWithIplImage:correspondColor];
    
    [NSThread detachNewThreadSelector:@selector(updateResultImage:) toTarget:self withObject:img_correspond];
    
    /*
     for( i = 0; i < objectKeypoints->total; i++ )
     {
     CvSURFPoint* r = (CvSURFPoint*)cvGetSeqElem( objectKeypoints, i );
     CvPoint center;
     int radius;
     center.x = cvRound(r->pt.x);
     center.y = cvRound(r->pt.y);
     radius = cvRound(r->size*1.2/9.*2);
     cvCircle( object_color, center, radius, colors[0], 1, 8, 0 );
     }
     */
    
    //show image
    //cvShowImage( "Object", object_color );
//    printf( "Extraction time = %gms\n", 1000 * ((double)cvGetTickCount() - tt) / (cvGetTickFrequency()));
//    NSTimeInterval stop = [[NSDate date] timeIntervalSince1970];
//    NSLog(@"total time = %f", (stop - start));
    
    
    return success;
}

- (void) updateResultImage:(UIImage *) img_correspond {
    img1.image = img_correspond;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.galeryView.delegate = self;
    imgCorrespond = [[UIImageView alloc] init];
    imgCorrespond.frame = self.view.frame;
    
    _pickedImage = [[UIImage alloc] init];
    _pickedImage = [UIImage imageNamed:@"2.jpg"];
    
    UIBarButtonItem *btnShowHide = [[UIBarButtonItem alloc] initWithTitle:@"Detect" 
                                                                   style:UIBarButtonItemStyleBordered 
                                                                  target:self 
                                                                  action:@selector(didDetectButtonClicked)];
    self.navigationItem.rightBarButtonItem = btnShowHide;
    [btnShowHide release];
    
    UIBarButtonItem *btnCapture = [[UIBarButtonItem alloc] initWithTitle:@"Take Image" 
                                                                    style:UIBarButtonItemStyleBordered 
                                                                   target:self 
                                                                  action:@selector(takePicture:)];
    self.navigationItem.leftBarButtonItem = btnCapture;
    [btnCapture release];
    
    self.img1.image = _pickedImage;
    listCard = [[NSMutableArray alloc] init];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (void) takePicture:(id) sender {
    if (_picker) {
        [_picker release];
        _picker = nil;
    }
    _picker = [[UIImagePickerController alloc] init];
    _picker.delegate = self;
#if TARGET_IPHONE_SIMULATOR
    _picker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
#else
    _picker.sourceType = UIImagePickerControllerSourceTypeCamera;
#endif
    
    [self presentModalViewController:_picker animated:YES];
}

- (void) showHide:(BOOL) isHiden {
    galeryView.scrollView.hidden = isHiden;
}

- (BOOL) findObject:(UIImage *) scene {
    UIImage * img = [UIImage imageNamed:@"box.png"];
    IplImage * object = [img CreateIplImageFromUIImage:[UIImage imageNamed:@"ar.jpg"]];
    if (galeryView.totalItems > 0) {
        UIImage * currImg =  [[galeryView getCurrentImageDisplay] retain];
        object = [img CreateIplImageFromUIImage:currImg];
        
    }
    object = convertRGB2Grayscale(object);
    if (scene == nil) {
        scene = [UIImage imageNamed:@"2.jpg"];
    }
    img1.image = scene;
    
    IplImage * image = [img CreateIplImageFromUIImage:scene];
    image = convertRGB2Grayscale(image);
    
    BOOL success = [self findObject:object andScene:image];
    
    [self showHide:YES];
    
    return success;
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

#pragma mark - UIImagePickerControllerDelegate implementation

- (void)imagePickerController:(UIImagePickerController *)picker
        didFinishPickingImage:(UIImage *)image
                  editingInfo:(NSDictionary *)editingInfo
{
    [picker dismissModalViewControllerAnimated:YES];
    //[self imageWithImage:image scaledToSize:CGSizeMake(image.size.width / 5, image.size.height / 5)];
    Mat mScene = [image CVMat];
    Mat mRSScene;
    resize(mScene, mRSScene, cvSize(mScene.rows / 5, mScene.cols / 5));
    UIImage *scene = [[UIImage alloc] initWithCVMat:mRSScene];
    
    self.img1.image = scene;
    if (_pickedImage) {
        [_pickedImage release];
        _pickedImage = nil;
    }
    _pickedImage = [scene retain];
    
    self.title = @"Detecting ...";
    [self showHide:NO];
    
    [galeryView resetGallery];
    [NSThread detachNewThreadSelector:@selector(createQueue) toTarget:self withObject:nil];
}

- (void) createQueue {
    NSTimeInterval start = [[NSDate date] timeIntervalSince1970];
    NSOperationQueue *queue = [NSOperationQueue new];
    NSMutableArray * arrOperation = [[NSMutableArray alloc] init];
    for (int i = 0;i < galeryView.totalItems;i++) {
        NSInvocationOperation *operation = [[NSInvocationOperation alloc] initWithTarget:self
                                                                                selector:@selector(doDetect:)
                                                                                  object:nil];
        [arrOperation addObject:operation];
        [operation release];
    }
    
    [queue setMaxConcurrentOperationCount:1];
    [queue addOperations:arrOperation waitUntilFinished:YES];
    [queue release];

    NSTimeInterval stop = [[NSDate date] timeIntervalSince1970];
    NSLog(@"total time = %f", (stop - start));

    [NSThread detachNewThreadSelector:@selector(showAlert) toTarget:self withObject:nil];
}

- (void) showAlert {
    NSString * message = @"";
    for (NSString * str in listCard) {
        [message stringByAppendingFormat:@"- %@", str];
    }
    
    if ([message isEqualToString:@""]) {
        message = @"no card found";
    }
    
    UIAlertView * alert = [[UIAlertView alloc] initWithTitle:@"Alert" message:[listCard description] delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil, nil];
    [alert show];
    [alert release];
}

- (void) doDetect:(id) object {
    BOOL success = [self findObject:_pickedImage];
    NSString * title = [NSString stringWithFormat:@"found %@", [galeryView getCurrentCard]];
    if (success) {
        [listCard addObject:[galeryView getCurrentCard]];
    } else {
        title = [NSString stringWithFormat:@"not found %@", [galeryView getCurrentCard]];
    }
    
    [NSThread detachNewThreadSelector:@selector(updateGallery:) toTarget:self withObject:title];
}

- (void) updateGallery: (NSString *) title {
    self.title = title;
    [galeryView slideShow];
    [self showHide:NO];
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker
{
    [picker dismissModalViewControllerAnimated:YES];
}

- (void) didDetectButtonClicked {
    [self showHide:NO];
    self.title = @"Detecting ...";
    self.img1.image = _pickedImage;
    if (_pickedImage) {
        [NSThread detachNewThreadSelector:@selector(findObject:) toTarget:self withObject:_pickedImage];
    } else {
        [NSThread detachNewThreadSelector:@selector(findObject:) toTarget:self withObject:nil];
    }
}

- (void) didArrowButtonClicked {
    [self showHide:NO];
}

@end
