//
//  MainViewController.h
//  DemoCropImage
//
//  Created by helios-team on 5/23/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@class TouchImageView;
@interface MainViewController : UIViewController <UINavigationControllerDelegate, UIImagePickerControllerDelegate> {
    UIImagePickerController *_picker;
    BOOL                    _hasScaledCamera;
    CGAffineTransform       _transformForCamera;
    UIImage                 *_imgPicked;
}
@property (nonatomic, assign) CGAffineTransform transformForCamera;
@property (nonatomic, retain) TouchImageView * bg;
@property (nonatomic, retain) IBOutlet UIButton * capture;
@property (nonatomic, retain) IBOutlet UIButton * crop;
@property (nonatomic, retain) UIImageView * img;
- (IBAction)didCaptureClicked:(id)sender;
- (IBAction)didCropClicked:(id)sender;
@end
