//
//  FindObjectViewController.h
//  OpenCVClient
//
//  Created by helios-team on 6/20/12.
//  Copyright (c) 2012 Aptogo Limited. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ImageGaleryView.h"

@interface FindObjectViewController : UIViewController <ImageGalleryViewDelegate, UINavigationControllerDelegate, UIImagePickerControllerDelegate>{
    UIImagePickerController         *_picker;
    UIImage                         *_pickedImage;
}

@property (nonatomic, retain) UIImage       *pickedImage;
@property (nonatomic, retain) UIImageView   *imgCorrespond;
@property (nonatomic, retain) IBOutlet UIImageView * img1;
@property (nonatomic, retain) IBOutlet ImageGaleryView * galeryView;
@property (nonatomic, retain) NSMutableArray * listCard;

@end
