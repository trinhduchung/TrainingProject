//
//  TouchImageView.m
//  OpenCVClient
//
//  Created by helios-team on 5/17/12.
//  Copyright (c) 2012  __MyCompanyName__. All rights reserved.
//

#import "TouchImageView.h"
@interface TouchImageView ()
- (BOOL)containsPoint:(CGPoint)point onPath:(UIBezierPath*)path inFillArea:(BOOL)inFill;
@end

@implementation TouchImageView
@synthesize paths, currentPath, startLocation, endLocation;
@synthesize _A,_B, _C, _replacedPoint, _positionReplacing;
@synthesize _cropPath;
@synthesize left = _left, top = _top, right = _right, bottom = _bottom;
@synthesize lRect, tRect, rRect,bRect;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        self.backgroundColor = [UIColor clearColor];
        int w = self.frame.size.width;
        int h = self.frame.size.height;
        self.currentPath = [UIBezierPath bezierPathWithRect:CGRectMake(10, 10, 200, 200)];

        currentPath.lineWidth = 3.0;
        startLocation = CGPointMake(-1, -1);
        _replacedPoint = CGPointMake(-10, -10);
        _top = CGPointMake(20, 20);
        _left = CGPointMake(20, h - 20);
        _bottom = CGPointMake(w - 20, h - 20);
        _right = CGPointMake(w - 20, 20);
        _isContains = FALSE;
        _positionReplacing = 0;
        _cropPath = CGPathCreateMutable();
    }
    return self;
}

- (void)resetPath {
    int w = self.frame.size.width;
    int h = self.frame.size.height;
    
    _top = CGPointMake(20, 20);
    _left = CGPointMake(20, h - 20);
    _bottom = CGPointMake(w - 20, h - 20);
    _right = CGPointMake(w - 20, 20);
    
    [self setNeedsDisplay];
}

- (void)initPathCVPoints:(CGPoint [4])result {
    _top = result[0];
    _left = result[1];
    _bottom = result[2];
    _right = result[3];
    
    [self setNeedsDisplay];
}

// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGMutablePathRef path = CGPathCreateMutable();
    
    [[UIColor colorWithPatternImage:[UIImage imageNamed:@"pattern.jpg"]] setStroke];
    CGContextSetLineWidth(context, 5.0);
    switch (_positionReplacing) {
        case 0:
        {
            CGPoint points[]= {_top, _left, _bottom, _right, _top};    
            CGPathAddLines(path, nil, points, 5);
            break;
        }
        case 1:
        {
            CGPoint points[]= {_replacedPoint, _left, _bottom, _right, _replacedPoint};    
            CGPathAddLines(path, nil, points, 5);
            break;
        }
        case 2:
        {
            CGPoint points[]= {_top, _replacedPoint, _bottom, _right, _top};    
            CGPathAddLines(path, nil, points, 5);
            break;
        }
        case 3:
        {
            CGPoint points[]= {_top, _left, _replacedPoint, _right, _top};    
            CGPathAddLines(path, nil, points, 5);
            break;
        }
        case 4:
        {
            CGPoint points[]= {_top, _left, _bottom, _replacedPoint, _top};    
            CGPathAddLines(path, nil, points, 5);
            break;
        }
        default:
            break;
    }
    
    CGContextAddPath(context, path);
    CGContextStrokePath(context);
    
    /* Draw circle*/
    int radius = 8;
    int detectRadius = 15;
    
    CGContextSetRGBFillColor(context, 0, 1, 0, 1);
    
    tRect = CGRectMake(_top.x - detectRadius, _top.y - detectRadius, 2 * detectRadius, 2 * detectRadius);
    lRect = CGRectMake(_left.x - detectRadius, _left.y - detectRadius, 2 * detectRadius, 2 * detectRadius);
    bRect = CGRectMake(_bottom.x - detectRadius, _bottom.y - detectRadius, 2 * detectRadius, 2 * detectRadius);
    rRect = CGRectMake(_right.x - detectRadius, _right.y - detectRadius, 2 * detectRadius, 2 * detectRadius);
    
    if (_positionReplacing != 1)
        CGContextFillEllipseInRect(context, CGRectMake(_top.x - radius, _top.y - radius, 2 * radius, 2 * radius));
    if (_positionReplacing != 2)
        CGContextFillEllipseInRect(context, CGRectMake(_left.x - radius, _left.y - radius, 2 * radius, 2 * radius));
    if (_positionReplacing != 3)
        CGContextFillEllipseInRect(context, CGRectMake(_bottom.x - radius, _bottom.y - radius, 2 * radius, 2 * radius));
    if (_positionReplacing != 4)
        CGContextFillEllipseInRect(context, CGRectMake(_right.x - radius, _right.y - radius, 2 * radius, 2 * radius));
    if (_positionReplacing > 0)
        CGContextFillEllipseInRect(context, CGRectMake(_replacedPoint.x - radius, _replacedPoint.y - radius, 2 * radius, 2 * radius));
    
    if (!_isContains && (CGRectContainsPoint(tRect, startLocation)|| CGRectContainsPoint(lRect, startLocation) || CGRectContainsPoint(bRect, startLocation) || CGRectContainsPoint(rRect, startLocation))) {
        _isContains = TRUE;
        NSLog(@"contains");
    }
    
    _cropPath = path;
    
//    CGPathRelease(path);
}

- (BOOL) containsPoint:(CGPoint)point onPath:(CGPathRef) path {
    CGContextRef context = UIGraphicsGetCurrentContext();
    BOOL    isHit = NO;
    // Save the graphics state so that the path can be
    // removed later.
    CGContextSaveGState(context);
    CGContextAddPath(context, path);
    
    // Do the hit detection.
    isHit = CGContextPathContainsPoint(context, point, kCGPathFillStroke);
    
    CGContextRestoreGState(context);
    
    return isHit;
}

- (BOOL)containsPoint:(CGPoint)point onPath:(UIBezierPath*)path inFillArea:(BOOL)inFill
{
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGPathRef cgPath = path.CGPath;
    BOOL    isHit = NO;
    
    // Determine the drawing mode to use. Default to
    // detecting hits on the stroked portion of the path.
    CGPathDrawingMode mode = kCGPathStroke;
    if (inFill)
    {
        // Look for hits in the fill area of the path instead.
        if (path.usesEvenOddFillRule)
            mode = kCGPathEOFill;
        else
            mode = kCGPathFillStroke;
    }
    
    // Save the graphics state so that the path can be
    // removed later.
    CGContextSaveGState(context);
    CGContextAddPath(context, cgPath);
    
    // Do the hit detection.
    isHit = CGContextPathContainsPoint(context, point, mode);
    
    CGContextRestoreGState(context);
    
    return isHit;
}

- (double) distance:(CGPoint) A and:(CGPoint) B {
    return sqrt(pow((B.x - A.x), 2.0) + pow((B.y - A.y), 2.0));
}

- (CGPoint) minDistanceToTouchPoint {
    
    double dt = [self distance:_top and:startLocation];
    double dl = [self distance:_left and:startLocation];
    double dr = [self distance:_right and:startLocation];
    double db = [self distance:_bottom and:startLocation];
    
    double min = dt;
    if (dl < min) {
        min = dl;
    }
    if (dr < min) {
        min = dr;
    }
    if (db < min) {
        min = db;
    }
    
    if (min == dl) {
        return _left;
    } else if (min == dr) {
        return _right;
    } else if (min == db) {
        return _bottom;
    }
    
    return _top;
}

- (BOOL) isBetween:(CGPoint) A and:(CGPoint) C and:(CGPoint) B {
    const double esilon = 6.0;
    NSLog(@"is between : %f", [self distance:A and:C] + [self distance:C and:B] - [self distance:A and:B]);
    return ([self distance:A and:C] + [self distance:C and:B] - [self distance:A and:B]) > -esilon && ([self distance:A and:C] + [self distance:C and:B] - [self distance:A and:B]) < esilon;
}

- (void) getContainsTouchPointLine {
    
    NSLog(@"start %f,%f", startLocation.x, startLocation.y);
    if ([self isBetween:_top and:startLocation and:_right]) {
        NSLog(@"top------right");
        _A = _top;
        _B = _right;
        _C = startLocation;
    } else if ([self isBetween:_top and:startLocation and:_left]) {
        NSLog(@"top------left");
        _A = _top;
        _B = _left;
        _C = startLocation;
    } else if ([self isBetween:_left and:startLocation and:_bottom]) {
        NSLog(@"left------bottom");
        _A = _left;
        _B = _bottom;
        _C = startLocation;
    } else if ([self isBetween:_bottom and:startLocation and:_right]) {
        NSLog(@"bottom------right");
        _A = _bottom;
        _B = _right;
        _C = startLocation;
    }
}

- (void) replacingPoint {
    double CA = [self distance:_A and:_C];
    double CB = [self distance:_B and:_C];
    CA <= CB ? (_replacedPoint = _A): (_replacedPoint = _B);
}

#pragma mark -
#pragma mark <Touches Began/Moved/Ended/Cancelled Methods>
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    //NSLog(@"touch began");
    
    UITouch * touch = [touches anyObject];

    startLocation = [touch locationInView:self];
    _replacedPoint = [self minDistanceToTouchPoint];
    //[self getContainsTouchPointLine];
    //[self replacingPoint];
    
    if (CGPointEqualToPoint(_replacedPoint, _top)) {
        NSLog(@"top");
        _positionReplacing = 1;
    } else if (CGPointEqualToPoint(_replacedPoint, _left)) {
        NSLog(@"left");
        _positionReplacing = 2;
    } else if (CGPointEqualToPoint(_replacedPoint, _bottom)) {
        NSLog(@"bottom");
        _positionReplacing = 3;
    } else if (CGPointEqualToPoint(_replacedPoint, _right)) {
        NSLog(@"right");
        _positionReplacing = 4;
    } else {
        _positionReplacing = 0;
    }
    
    [self setNeedsDisplay];
    
    NSSet * allTouches = [event allTouches];
    
    switch ([allTouches count]) {
        case 1://single touch
        {
            //get the first touch
            UITouch * firstTouch = [[allTouches allObjects] objectAtIndex:0];
            
            switch ([firstTouch tapCount]) {
                    
                case 1://single tap
                {
                   
                    break;
                }
                case 2: //double tap
                {
                    
                    break;
                }
                default:
                    break;
            }
            break;
        }
        case 2:
        {
            //UITouch * touch1 = [[allTouches allObjects] objectAtIndex:0];
            //UITouch * touch2 = [[allTouches allObjects] lastObject];
           
            break;
        }
        default:
            break;
    }
}
//If touch point has y > image.size.y + image.size.width or < image.size.y
- (CGPoint) reLocateTouchPoint:(CGPoint) touchPoint {
    return CGPointMake(0, 0);
}

- (void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event {
    //NSLog(@"touch moved");
    UITouch * touch = [touches anyObject];
    if (_isContains) {
        
        CGPoint touchPoint = [touch locationInView:self];
        
        _replacedPoint = touchPoint;
        
        [self setNeedsDisplay];
    }
    
    NSSet *allTouches = [event allTouches];
    
    switch ([allTouches count])
    {
        case 1: {
            //The image is being panned (moved left or right)
            //UITouch *touch = [[allTouches allObjects] objectAtIndex:0];
            
            
        } break;
        case 2: {
            //The image is being zoomed in or out.
            
            //UITouch *touch1 = [[allTouches allObjects] objectAtIndex:0];
            //UITouch *touch2 = [[allTouches allObjects] objectAtIndex:1];
            
            
        } break;
    }
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event { 
	//NSLog(@"touch ended");

    UITouch * touch = [touches anyObject];
    if (_isContains) {
        CGPoint touchPoint = [touch locationInView:self];
        _replacedPoint = touchPoint;
        
        switch (_positionReplacing) {
            case 1:
                _top = _replacedPoint;
                break;
            case 2:
                _left = _replacedPoint;
                break;
            case 3:
                _bottom = _replacedPoint;
                break;
            case 4:
                _right = _replacedPoint;
                break;
            default:
                break;
        }
        _positionReplacing = 0;
        _isContains = FALSE;
        
        [self setNeedsDisplay];
    }
	//Get all the touches.
	NSSet *allTouches = [event allTouches];
	
	//Number of touches on the screen
	switch ([allTouches count])
	{
		case 1:
		{
			//Get the first touch.
			UITouch *touch = [[allTouches allObjects] objectAtIndex:0];
			
			switch([touch tapCount])
			{
				case 1://Single tap
					
					break;
				case 2://Double tap.
					
					break;
			}
		}	
            break;
	}
	
}



@end
