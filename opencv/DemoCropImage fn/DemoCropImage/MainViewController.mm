//
//  MainViewController.m
//  DemoCropImage
//
//  Created by helios-team on 5/23/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "MainViewController.h"
#import <CoreGraphics/CoreGraphics.h>
#import <QuartzCore/CoreAnimation.h>
#import "TouchImageView.h"
#import "UIImage+Addition.h"
#import "UIImage+OpenCV.h"
//transform values for full screen support
#define CAMERA_TRANSFORM_X 1
#define CAMERA_TRANSFORM_Y 1//(320.0f/431.0f)
//iphone screen dimensions
#define SCREEN_WIDTH  320
#define SCREEN_HEIGTH 480
#define BUTTON_HEIGHT 30
@interface MainViewController ()
- (void) initPicker;
@end

@implementation MainViewController
using namespace cv;

@synthesize transformForCamera = _transformForCamera;
@synthesize crop, capture, bg, img;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        img = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 320, 400)];
        [self.view addSubview:img];
        
        bg = [[TouchImageView alloc] initWithFrame:CGRectMake(0, 0, 320, 400)];
        bg.userInteractionEnabled = YES;
        [self.view addSubview:bg];
        
        
        img.hidden = YES;
        bg.hidden = YES;
        crop.enabled = NO;
    }
    return self;
}

- (void)didCropClicked:(id)sender {
    bg.hidden = YES;
    
    UIImage *cropImg = [_imgPicked cropImageWithPath:bg._cropPath];
    img.image = cropImg;
    
    /*
    //format
    Mat _srcMat = [cropImg CVMat];
    //
     //Perspective Transform
     //
    Point2f srcTri[4];
    Point2f dstTri[4];
    
    Mat rot_mat( 2, 3, CV_32FC1 );
    Mat warp_mat( 3, 3, CV_32FC1 );
    Mat src, warp_dst, warp_rotate_dst;
    src = _srcMat;
    /// Set the dst image the same type and size as src
    warp_dst = Mat::zeros( src.rows, src.cols, src.type() );
    srcTri[0] = Point2f( 45,5 );
    srcTri[1] = Point2f( src.cols - 55, 0 );
    srcTri[2] = Point2f( 0, src.rows);
    srcTri[3] = Point2f( src.cols, src.rows - 10);
    
    dstTri[0] = Point2f( 0, 0 );
    dstTri[1] = Point2f( src.cols, 0);
    dstTri[2] = Point2f( 0, src.rows);
    dstTri[3] = Point2f( src.cols, src.rows);
    warp_mat = getPerspectiveTransform(srcTri, dstTri);
    
    //warpPerspective(<#InputArray src#>, <#OutputArray dst#>, <#InputArray M#>, <#Size dsize#>)
    warpPerspective(src, warp_dst, warp_mat, warp_dst.size()); 
    
    //Rotating the image after Warp 
    
    // Compute a rotation matrix with respect to the center of the image
    CvPoint center = cvPoint( warp_dst.cols/2, warp_dst.rows/2 );
    double angle = 0.0;
    double scale = 1.0;
    
    /// Get the rotation matrix with the specifications above
    rot_mat = getRotationMatrix2D( center, angle, scale );
    
    /// Rotate the warped image
    warpAffine( warp_dst, warp_rotate_dst, rot_mat, warp_dst.size() );
    
    // Display result 
    self.img.image = [UIImage imageWithCVMat:warp_rotate_dst];
    */
}

- (void)didCaptureClicked:(id)sender {
    [self initPicker];
}

- (void) initPicker {
    if (_picker) {
        [_picker release];
        _picker = nil;
    }
    _picker = [[UIImagePickerController alloc] init];
    _picker.delegate = self;
    _picker.allowsEditing = YES;
    
    _picker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
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
    }
    
    [_picker dismissModalViewControllerAnimated:NO];
    
    if (_picker) {
        [_picker release];
        _picker = nil;        
    }
    if (image) {
        NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];
        //
        _imgPicked = image;
        img.image = image;
        img.hidden = NO;
        bg.hidden = NO;
        crop.enabled = YES;
        image = nil;
        [pool release];          
    }
    
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
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

@end
