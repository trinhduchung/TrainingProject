//
//  ViewController.h
//  ImageProcessing
//
//  Created by Han Korea on 2/2/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ViewController : UIViewController <UIActionSheetDelegate> {
    IBOutlet UIButton *_filterButton;
    IBOutlet UIImageView *_imageView;
    UIImage              *_image;
}
- (IBAction)filterButton_clicked:(id)sender;

@end
