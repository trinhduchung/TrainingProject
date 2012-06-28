//
//  ViewController.m
//  DemoCropImage
//
//  Created by Han Korea on 5/22/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "ViewController.h"
#import "UIImage+Addition.h"
@interface ViewController ()
- (void)testCrop;
@end

@implementation ViewController
@synthesize originImage;
@synthesize cropImage;

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    [self testCrop];
}

- (void)viewDidUnload
{
    [self setOriginImage:nil];
    [self setCropImage:nil];
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone) {
        return (interfaceOrientation != UIInterfaceOrientationPortraitUpsideDown);
    } else {
        return YES;
    }
}

- (void)testCrop {
    /* init path */
    CGMutablePathRef path = CGPathCreateMutable();
    /* create test point */
    CGPoint p1 = CGPointMake(0, 0);
    CGPoint p2 = CGPointMake(10, 100);
    CGPoint p3 = CGPointMake(200, 100);
    CGPoint p4 = CGPointMake(100, 0);
    CGPoint points[]= {p1, p2, p3, p4};
    CGPathAddLines(path, nil, points, 4);
    /* crop image then set image for crop image view */
    UIImage *image = [self.originImage.image cropImageWithPath:path];
    self.cropImage.image = image;
    /* cleanup */
    CGPathRelease(path);
    
}


- (void)dealloc {
    [originImage release];
    [cropImage release];
    [super dealloc];
}
@end
