//
//  CustomImageView.h
//  ImageColorPicker
//
//  Created by Cuong Tran on 2/3/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol CustomImageViewPickedColorDelegate

@optional
- (void) pickedColor:(UIColor *) lastcolor;

@end

@interface CustomImageView : UIImageView {
    UIColor     *_lastColor;
    id          _pickedColorDelegate;
}
@property (nonatomic, retain) UIColor   *lastColor;
@property (nonatomic, assign) id pickedColorDelegate;

- (UIColor*) getPixelColorAtLocation:(CGPoint)point;
- (CGContextRef) createARGBBitmapContextFromImage:(CGImageRef)inImage;


@end
