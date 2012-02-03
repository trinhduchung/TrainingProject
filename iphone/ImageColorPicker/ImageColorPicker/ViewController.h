//
//  ViewController.h
//  ImageColorPicker
//
//  Created by Cuong Tran on 2/2/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "CustomImageView.h"
@interface ViewController : UIViewController <UIImagePickerControllerDelegate, UINavigationControllerDelegate, CustomImageViewPickedColorDelegate>{
    UIImagePickerController                 *_picker;
    BOOL                                    _hasScaledCamera;
    CGAffineTransform                       _transformForCamera;
    CustomImageView                         *_imgPicker;
    BOOL                                    _isImagePickerShow;
}
@property (nonatomic, assign) CGAffineTransform transformForCamera;
@property (nonatomic, retain) CustomImageView *imgPicker;
@end
