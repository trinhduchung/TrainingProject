//
//  DemoViewController.h
//  DemoCropImage
//
//  Created by helios-team on 5/22/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AVFoundation/AVFoundation.h>

@class TouchImageView;
@interface DemoViewController : UIViewController <UINavigationControllerDelegate, UIImagePickerControllerDelegate, AVCaptureVideoDataOutputSampleBufferDelegate>{
    TouchImageView      *_touchImg;
    UIImageView         *_bg;
    UIImagePickerController *_picker;
    UIImage             *_originalImage;
    
}

@property (nonatomic, retain) AVCaptureSession * captureSession;
@property (nonatomic, retain) AVCaptureVideoDataOutput * captureOutput;
@property (nonatomic, retain) AVCaptureStillImageOutput *stillImageOutput;
@property (nonatomic, retain) AVCaptureVideoPreviewLayer *previewLayer;
@property (nonatomic, retain) UIView  *preview;
@property (nonatomic, retain) UIImageView * bg;
@end
