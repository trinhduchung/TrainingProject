//
//  TouchImageView.h
//  OpenCVClient
//
//  Created by helios-team on 5/17/12.
//  Copyright (c) 2012 Aptogo Limited. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface TouchImageView : UIView {
    CGPoint     _left;
    CGPoint     _top;
    CGPoint     _right;
    CGPoint     _bottom;
    BOOL        _isContains;
}
@property (nonatomic, retain) UIBezierPath * currentPath;
@property (nonatomic, retain) NSMutableArray * paths;
@property (nonatomic, readwrite) CGPoint startLocation;
@property (nonatomic, readwrite) CGPoint endLocation;
@property (nonatomic, readwrite) CGPoint _A;
@property (nonatomic, readwrite) CGPoint _B;
@property (nonatomic, readwrite) CGPoint _C;
@property (nonatomic, readwrite) CGPoint _replacedPoint;
@property (nonatomic, readwrite) int _positionReplacing;
@property (nonatomic, readwrite) CGMutablePathRef _cropPath;

@end
