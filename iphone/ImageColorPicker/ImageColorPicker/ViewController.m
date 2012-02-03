//
//  ViewController.m
//  ImageColorPicker
//
//  Created by Cuong Tran on 2/2/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "ViewController.h"
#import "CustomImageView.h"
#import "AutoScrollLabel.h"
#import <CoreGraphics/CoreGraphics.h>
#import <QuartzCore/CoreAnimation.h>
//transform values for full screen support
#define CAMERA_TRANSFORM_X 1
#define CAMERA_TRANSFORM_Y 1//(320.0f/431.0f)
//iphone screen dimensions
#define SCREEN_WIDTH  320
#define SCREEN_HEIGTH 480
#define BUTTON_HEIGHT 30
@interface ViewController()
- (void) animateColorWheelToShow:(BOOL)show duration:(NSTimeInterval)duration;
- (void) initPicker;
@end
@implementation ViewController
@synthesize transformForCamera = _transformForCamera;
@synthesize imgPicker = _imgPicker;
- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Release any cached data, images, etc that aren't in use.
}

#pragma mark - View lifecycle

- (id) init {
    self = [super init];
    if (self) {
        self.title = @"Image Color Picker";
        
        UIButton * button = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        button.frame = CGRectMake(61, 350, 198, 61);
        button.tintColor = [UIColor whiteColor];
        [button setTitle:@"Capture" forState:UIControlStateNormal];
        [button addTarget:self action:@selector(didCaptureClicked) forControlEvents:UIControlEventTouchUpInside];
        [self.view addSubview:button];
        [button release];
        
        _imgPicker = [[CustomImageView alloc] initWithFrame:CGRectMake(10, 7, 300, 340)];
        _imgPicker.pickedColorDelegate = self;
        _imgPicker.userInteractionEnabled = YES;
        _imgPicker.image = [UIImage imageNamed:@"suit.png"];
        [self.view addSubview:_imgPicker];
        
        UILabel *hint = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 320, 30)];
        hint.textColor = [UIColor redColor];
        hint.backgroundColor = [UIColor clearColor];
        hint.font = [UIFont systemFontOfSize:16];
        hint.text = @"Touch on image to pick color";
        hint.textAlignment = UITextAlignmentCenter;
        [self.view addSubview:hint];
        [hint release];
        
//        AutoScrollLabel * autoText = [[AutoScrollLabel alloc] initWithFrame:CGRectMake(100, 0, 100, 30)];
//        autoText.text = @"Touch on image to pick color";
//        autoText.textColor = [UIColor yellowColor];
//        [self.view addSubview:autoText];
//        [autoText release];
        
    }
    return self;
}
#pragma CustomImageView delegate
- (void) pickedColor:(UIColor *)lastcolor {
    NSLog(@"picker delegate");
    [self animateColorWheelToShow:NO duration:0.3];
    self.view.backgroundColor = lastcolor;
    [self.view setNeedsDisplay];
}

- (void) animateColorWheelToShow:(BOOL)show duration:(NSTimeInterval)duration {
    _isImagePickerShow = show;
	int x;
	float angle;
	float scale;
	if (show == NO) { 
		x = -320;
		angle = -3.12;
		scale = 0.01;
		self.imgPicker.hidden = YES;
	} else {
		x = 0;
		angle = 0;
		scale = 1;
		[self.imgPicker setNeedsDisplay];
		self.imgPicker.hidden = NO;
	}
	[UIView beginAnimations:nil context:NULL];
	[UIView setAnimationDuration:duration];
	
	CATransform3D transform = CATransform3DMakeTranslation(0,0,0);
	transform = CATransform3DScale(transform, scale,scale,1);
	self.imgPicker.transform = CATransform3DGetAffineTransform(transform);
	self.imgPicker.layer.transform = transform;
	[UIView commitAnimations];
}

- (void) didCaptureClicked {
    NSLog(@"capture");
    if (_isImagePickerShow) {
        [self animateColorWheelToShow:NO duration:0.3];
    } else {
        [self initPicker];
    }
}

- (void) initPicker {
    if (_picker) {
        [_picker release];
        _picker = nil;
    }
    _picker = [[UIImagePickerController alloc] init];
    _picker.delegate = self;
    _picker.allowsEditing = YES;
    
    _picker.sourceType = UIImagePickerControllerSourceTypeCamera;
    [_picker setToolbarHidden:YES];
    [_picker setNavigationBarHidden:YES];
    [_picker setModalTransitionStyle:UIModalTransitionStyleCrossDissolve];
    _picker.wantsFullScreenLayout = YES;
    if (!_hasScaledCamera) {
        self.transformForCamera = CGAffineTransformScale(_picker.cameraViewTransform,CAMERA_TRANSFORM_X,CAMERA_TRANSFORM_Y);
        _hasScaledCamera = YES;
    }
    _picker.cameraViewTransform = self.transformForCamera;
    _picker.showsCameraControls = YES;
    
    UIImageView * img = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"suit.png"]];
    img.frame = CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGTH);
    _picker.cameraOverlayView = img;
    [img release];
    //if([UIImagePickerController isCameraDeviceAvailable:UIImagePickerControllerCameraDeviceFront]) {
        //_picker.cameraDevice = UIImagePickerControllerCameraDeviceFront;
    //}
    //else {
        //_picker.cameraDevice = UIImagePickerControllerCameraDeviceRear;
    //}
    if (_picker) {
        [self presentModalViewController:_picker animated:YES];
    }
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    
}

#pragma UIImagePickerController delegate
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info {
    
    UIImage *image;
    if (picker.sourceType == UIImagePickerControllerSourceTypePhotoLibrary) {
        image = [info objectForKey:UIImagePickerControllerEditedImage];
    } else {
        image = [info objectForKey:UIImagePickerControllerOriginalImage];
        _picker.cameraViewTransform = CGAffineTransformScale(_picker.cameraViewTransform, CAMERA_TRANSFORM_X, 1.0f/CAMERA_TRANSFORM_Y);
        
        UIImageWriteToSavedPhotosAlbum(image, self, @selector(image:didFinishSavingWithError:contextInfo:), NULL);
    }

    [_picker dismissModalViewControllerAnimated:NO];
    if (_picker) {
        [_picker release];
        _picker = nil;        
    }
    if (image) {
        NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];
        //xu ly
        _imgPicker.image = [image retain];
        [self animateColorWheelToShow:YES duration:0.3];
        image = nil;
        [pool release];          
    }
    
}


- (void)image:(UIImage *) image didFinishSavingWithError: (NSError *) error contextInfo: (void *) contextInfo {
    NSLog(@"SAVE IMAGE COMPLETE");
    if(error != nil) {
        NSLog(@"ERROR SAVING:%@",[error localizedDescription]);
    }
}

- (void) dealloc {
    if (_picker) {
        [_picker release];
        _picker = nil;
    }
    [_imgPicker release];
    [super dealloc];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
}

- (void)viewWillDisappear:(BOOL)animated
{
	[super viewWillDisappear:animated];
}

- (void)viewDidDisappear:(BOOL)animated
{
	[super viewDidDisappear:animated];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

@end
