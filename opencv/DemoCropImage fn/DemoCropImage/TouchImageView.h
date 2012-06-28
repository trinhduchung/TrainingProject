//
//  TouchImageView.h
//  OpenCVClient
//
//  Created by helios-team on 5/17/12.
//  Copyright (c) 2012  __MyCompanyName__. All rights reserved.
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
@property (nonatomic, readwrite) CGPoint left;
@property (nonatomic, readwrite) CGPoint top;
@property (nonatomic, readwrite) CGPoint right;
@property (nonatomic, readwrite) CGPoint bottom;
@property (nonatomic, readwrite) CGRect  lRect;
@property (nonatomic, readwrite) CGRect  tRect;
@property (nonatomic, readwrite) CGRect  rRect;
@property (nonatomic, readwrite) CGRect  bRect;

- (void)resetPath;
- (void)initPathCVPoints:(CGPoint[4]) result;
@end
