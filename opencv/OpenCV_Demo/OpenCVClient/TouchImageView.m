//
//  TouchImageView.m
//  OpenCVClient
//
//  Created by helios-team on 5/17/12.
//  Copyright (c) 2012 Aptogo Limited. All rights reserved.
//

#import "TouchImageView.h"
@interface TouchImageView ()
- (BOOL)containsPoint:(CGPoint)point onPath:(UIBezierPath*)path inFillArea:(BOOL)inFill;
@end

@implementation TouchImageView
@synthesize paths, currentPath, startLocation, endLocation;
@synthesize _A,_B, _C, _replacedPoint, _positionReplacing;
@synthesize _cropPath;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        self.backgroundColor = [UIColor clearColor];
        
        self.currentPath = [UIBezierPath bezierPathWithRect:CGRectMake(10, 10, 200, 200)];

        currentPath.lineWidth = 3.0;
        startLocation = CGPointMake(-1, -1);
        _top = CGPointMake(10, 10);
        _left = CGPointMake(10, 450);
        _bottom = CGPointMake(310, 450);
        _right = CGPointMake(310, 10);//240,449
        _isContains = FALSE;
        _positionReplacing = 0;
        _cropPath = CGPathCreateMutable();
    }
    return self;
}


// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    /*
    [self.currentPath stroke];
    if ( [self containsPoint:startLocation onPath:self.currentPath inFillArea:NO] ) {
        NSLog(@"contains");
    }
    
    
    // Create an oval shape to draw.
    UIBezierPath* aPath = [UIBezierPath bezierPathWithOvalInRect:
                           CGRectMake(0, 0, 200, 100)];
    
    // Set the render colors
    [[UIColor blackColor] setStroke];
//    [[UIColor redColor] setFill];
    
    CGContextRef aRef = UIGraphicsGetCurrentContext();
    
    // If you have content to draw after the shape,
    // save the current state before changing the transform
    //CGContextSaveGState(aRef);
    
    // Adjust the view's origin temporarily. The oval is
    // now drawn relative to the new origin point.
    CGContextTranslateCTM(aRef, 50, 50);
    
    // Adjust the drawing options as needed.
    aPath.lineWidth = 5;
    
    // Fill the path before stroking it so that the fill
    // color does not obscure the stroked line.
//    [aPath fill];
    [aPath stroke];
    
    // Restore the graphics state before drawing any other content.
    //CGContextRestoreGState(aRef);
     */
    
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGMutablePathRef path = CGPathCreateMutable();
    
    [[UIColor redColor] setStroke];
    CGContextSetLineWidth(context, 3.0);
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
    
    if (!_isContains && [self containsPoint:startLocation onPath:path]) {
        _isContains = TRUE;
        NSLog(@"contains");
    }
    
    _cropPath = path;
//    [self swapCoordinates];
    
//    CGPathRelease(path);
}

- (void) swapCoordinates {
    CGPoint newLeft = CGPointMake( _top.y, _top.x);
    CGPoint newTop = CGPointMake(_left.y, _left.x);
    CGPoint newRight = CGPointMake(_bottom.y,_bottom.x);
    CGPoint newBottom = CGPointMake(_right.y, _right.x);
    
    CGPoint points[] = {newTop, newLeft, newBottom, newRight, newTop};
    CGPathAddLines(_cropPath, nil, points, 5);
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
            mode = kCGPathFill;
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

- (BOOL) isBetween:(CGPoint) A and:(CGPoint) C and:(CGPoint) B {
    const double esilon = 1.0;
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
    [self getContainsTouchPointLine];
    [self replacingPoint];
    
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
            UITouch * touch1 = [[allTouches allObjects] objectAtIndex:0];
            UITouch * touch2 = [[allTouches allObjects] lastObject];
           
            break;
        }
        default:
            break;
    }
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
            UITouch *touch = [[allTouches allObjects] objectAtIndex:0];
            
            
        } break;
        case 2: {
            //The image is being zoomed in or out.
            
            UITouch *touch1 = [[allTouches allObjects] objectAtIndex:0];
            UITouch *touch2 = [[allTouches allObjects] objectAtIndex:1];
            
            
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
