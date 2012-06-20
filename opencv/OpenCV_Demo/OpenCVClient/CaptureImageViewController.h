//
//  CaptureImageViewController.h
//  OpenCVClient
//
//  Created by helios-team on 5/17/12.
//  Copyright (c) 2012 Aptogo Limited. All rights reserved.
//

#import <UIKit/UIKit.h>

@class TouchImageView;
@interface CaptureImageViewController : UIViewController {
    cv::VideoCapture *_videoCapture;
    cv::Mat _lastFrame;
    
    TouchImageView      *_touchImg;
    UIImageView         *_bg;
}

@end
