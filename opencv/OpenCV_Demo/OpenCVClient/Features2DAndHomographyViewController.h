//
//  Features2DAndHomographyViewController.h
//  OpenCVClient
//
//  Created by helios-team on 6/19/12.
//  Copyright (c) 2012 Aptogo Limited. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface Features2DAndHomographyViewController : UIViewController <UIImagePickerControllerDelegate, UINavigationControllerDelegate> {
    UIButton        *_mainButton;
}
@property (nonatomic, retain) IBOutlet UIImageView * imgvobj;
@property (nonatomic, retain) IBOutlet UIImageView * imgvscence;
@property (nonatomic, retain) IBOutlet UIButton * button;

- (IBAction)didButtonClicked:(id)sender;

@end
