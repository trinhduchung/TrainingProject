//
//  ViewController.m
//  ImageProcessing
//
//  Created by Han Korea on 2/2/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "ViewController.h"
#import "CoreImageProcessing.h"
@implementation ViewController

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Release any cached data, images, etc that aren't in use.
}

#pragma mark - View lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    _image = [[UIImage imageNamed:@"background.png"] retain];
    _imageView.image = _image;
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
}

- (void)viewWillDisappear:(BOOL)animated
{
	[super viewWillDisappear:animated];
}

- (void)viewDidDisappear:(BOOL)animated
{
	[super viewDidDisappear:animated];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return (interfaceOrientation != UIInterfaceOrientationPortraitUpsideDown);
}


- (IBAction)filterButton_clicked:(id)sender {
    UIActionSheet *actionSheet = [[UIActionSheet alloc] initWithTitle:@"Choose Filter" 
                                                             delegate:self 
                                                    cancelButtonTitle:@"Cancel"
                                               destructiveButtonTitle:nil 
                                                    otherButtonTitles:@"Nostolagia", @"Pencil", @"Reset", nil];
    [actionSheet showInView:self.view];
    [actionSheet release];
}


- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex {
    switch (buttonIndex) {
        case 0:
            _imageView.image = [_image noslagia];
            break;
        case 1:
            _imageView.image = [_image pencilSketchFilter];
            break;
        case 2:
             _imageView.image = _image;
            break;
        default:
            break;
    }
}
@end
