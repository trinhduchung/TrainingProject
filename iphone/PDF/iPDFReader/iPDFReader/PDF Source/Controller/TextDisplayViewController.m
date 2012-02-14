//
//  TextDisplayViewController.m
//  iPDFReader
//
//  Created by Cuong Tran on 2/12/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "TextDisplayViewController.h"
#import "PDFDocumentManager.h"
#import "MFDocumentManager.h"
#import "FPKGlyphBox.h"

@implementation TextDisplayViewController
@synthesize textView, activityIndicatorView;
@synthesize text;
@synthesize delegate;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        
    }
    return self;
}

#pragma mark -
#pragma mark Text extraction in background

-(void)updateTextToTextDisplayView:(NSString *)someText {
	
	// We got the text, now we can send it to the textView.
	self.textView.text = someText;
	self.text = someText;
	
	// Stop the activity indictor.
	[activityIndicatorView stopAnimating];
	
}

-(void)selectorWholeTextForPage:(NSNumber *)page {
    // This is going to be run in the background, so we need to create an autorelease pool for the thread.
	
	NSAutoreleasePool *pool = [[NSAutoreleasePool alloc]init];
	
	// Just call the -wholeTextForPage: method of MFDocumentManager. Pass NULL as profile to use the default profile.
	// If you want to use a different profile pass a reference to a MFProfile.
    
    // Use -(void)test_wholeTextForPage:(NSUInteger)page if you want to test the new text extraction engine instead.
    MFDocumentManager * documentManager = [PDFDocumentManager sharedInstance].documentManager;
    NSString * someText = [[documentManager wholeTextForPage:[page unsignedIntValue]] copy];//convertPDF(_path);
    
//    FPKGlyphBox * glyphBox = [[FPKGlyphBox alloc] initWithBox:CGRectMake(0, 0, 320, 480) unicodes:nil length:320];
    
//	NSArray * glyphBoxs = [documentManager glyphBoxesForPage:1];
//    NSString * glyphText = [FPKGlyphBox textFromBoxArray:glyphBoxs];
//    NSString *text = [someText stringByAppendingString:glyphText];
    
	// NSString *someText = [[documentManager wholeTextForPage:[page intValue] withProfile:NULL]copy];
	
	
	// Call back performed on the main thread.
	[self performSelectorOnMainThread:@selector(updateTextToTextDisplayView:) withObject:someText  waitUntilDone:YES];
	
	// Cleanup.
	[someText release];
	[pool release];

}

-(void)clearText {
	
	// Clear both the view and the saved text.
	self.text = nil;
	textView.text = nil;	
}

-(void)updateWithTextOfPage:(NSUInteger)page documentPath:(NSString *)path{
	
	// Clear the old text (if any), start the activity indicator and launch the selector in background.
	
	[self clearText];
	_path = [path copy];
	[activityIndicatorView startAnimating];
	
	[self performSelectorInBackground:@selector(selectorWholeTextForPage:) withObject:[NSNumber numberWithInt:page]];
}

- (void)didReceiveMemoryWarning
{
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
    
    // Release any cached data, images, etc that aren't in use.
}
#pragma mark -
#pragma mark Actions

-(IBAction)actionBack:(id)sender {
	[self.delegate dismissTextDisplayViewController:self];
}
// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad {
    
	// Set up the view accordingly to the saved text (if any).
	[super viewDidLoad];
	[textView setText:text];
}
#pragma mark - View lifecycle

/*
// Implement loadView to create a view hierarchy programmatically, without using a nib.
- (void)loadView
{
}
*/

/*
// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad
{
    [super viewDidLoad];
}
*/

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
    self.activityIndicatorView = nil;
	
	self.text = self.textView.text;
	self.textView = nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
	return YES;
}
- (void)dealloc {
	
	delegate = nil;
	[textView release],textView = nil;
	[activityIndicatorView release],activityIndicatorView = nil;
	[text release],text = nil;

    [super dealloc];
}
@end
