//
//  ViewController.m
//  iPDFReader
//
//  Created by Cuong Tran on 2/11/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "ViewController.h"
#import "ReaderDocument.h"
#import "ReaderViewController.h"
#import "PDFDocumentManager.h"
#import "MFDocumentManager.h"
#import "FPKGlyphBox.h"
#import "MFTextItem.h"

@implementation ViewController
#define DEMO_VIEW_CONTROLLER_PUSH TRUE
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
#ifdef DEBUGX
	NSLog(@"%s %@", __FUNCTION__, NSStringFromCGRect(self.view.bounds));
#endif
    NSDictionary *infoDictionary = [[NSBundle mainBundle] infoDictionary];
	NSString *name = [infoDictionary objectForKey:@"CFBundleName"];
	NSString *version = [infoDictionary objectForKey:@"CFBundleVersion"];
	self.title = [NSString stringWithFormat:@"%@ v%@", name, version];
    
    _tableView = [[UITableView alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    [self.view addSubview:_tableView];
    
    _pdfFiles = [[FileHelper sharedInstance] pdfFiles];
}

#pragma tableView delegate and datasource
// Customize the number of sections in the table view.
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [_pdfFiles count];
}

// Customize the appearance of table view cells.
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier] autorelease];
        if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone) {
            cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        }
    }
    
    // Configure the cell.
    //cell.textLabel.text = NSLocalizedString(@"Detail", @"Detail");
    
    cell.textLabel.text = [[_pdfFiles objectAtIndex:indexPath.row] stringByDeletingPathExtension];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
    NSString *phrase = nil; // Document password (for unlocking most encrypted PDF files)
//    
//	NSArray *pdfs = [[NSBundle mainBundle] pathsForResourcesOfType:@"pdf" inDirectory:nil];
    
	NSString *filePath = [_pdfFiles objectAtIndex:indexPath.row];//[pdfs objectAtIndex:1]; 
    filePath = [FileHelper documentsPathWithFileName:filePath];
    assert(filePath != nil); // Path to last PDF file
    NSLog(@"%@",filePath);
    //set Document manager for test FPK lib
    //NSString *documentName = [[_pdfFiles objectAtIndex:indexPath.row] stringByDeletingPathExtension];
    //NSString *documentPath = [[NSBundle mainBundle] pathForResource:documentName ofType:PDF];
    
    NSURL *documentUrl = [NSURL fileURLWithPath:filePath];
    
    MFDocumentManager *documentManager = [[MFDocumentManager alloc] initWithFileUrl:documentUrl];
    
    int numberpage = [documentManager numberOfPages];
    for (int i = 0;i < numberpage;i++) {
        unsigned int *unicodes;
//        *unicodes = 32;
        NSLog(@"%d",*unicodes);
        FPKGlyphBox * box = [[FPKGlyphBox alloc] initWithBox:CGRectMake(300, 400, 100, 100) unicodes:unicodes length:4];
        NSLog(@"%d",*unicodes);
        NSString * text = [box text];
        NSLog(@"text : %@",text);
        NSArray *arr = [[NSArray alloc] init];
        arr = [documentManager glyphBoxesForPage:i];
        if (arr != nil && [arr count] > 0) {
            FPKGlyphBox * item = (FPKGlyphBox *)[arr objectAtIndex:0];
            
            NSLog(@"%@",item.text);
        }
    }
    
    [[PDFDocumentManager sharedInstance] withDocumentManager:documentManager];
    documentManager.resourceFolder = [[NSBundle mainBundle] resourcePath];
    
	ReaderDocument *document = [ReaderDocument withDocumentFilePath:filePath password:phrase];

    if (document != nil) {
        ReaderViewController *readerViewController = [[ReaderViewController alloc] initWithReaderDocument:document];
        readerViewController.delegate = self;
#if (DEMO_VIEW_CONTROLLER_PUSH == TRUE)
        
        [self.navigationController pushViewController:readerViewController animated:YES];
        
#else // present in a modal view controller
        
        readerViewController.modalTransitionStyle = UIModalTransitionStyleCrossDissolve;
        readerViewController.modalPresentationStyle = UIModalPresentationFullScreen;
        
        [self presentModalViewController:readerViewController animated:YES];
        
#endif // DEMO_VIEW_CONTROLLER_PUSH
        
        [readerViewController release]; // Release the ReaderViewController
    }
}

#pragma mark ReaderViewControllerDelegate methods

- (void)dismissReaderViewController:(ReaderViewController *)viewController
{
#ifdef DEBUGX
	NSLog(@"%s", __FUNCTION__);
#endif
    
#if (DEMO_VIEW_CONTROLLER_PUSH == TRUE)
    
	[self.navigationController popViewControllerAnimated:YES];
    
#else // dismiss the modal view controller
    
	[self dismissModalViewControllerAnimated:YES];
    
#endif // DEMO_VIEW_CONTROLLER_PUSH
}


- (void) dealloc {
    [_pdfFiles release];
    [_tableView release];
    [super dealloc];
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
    if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone) {
        return (interfaceOrientation != UIInterfaceOrientationPortraitUpsideDown);
    } else {
        return YES;
    }
}

@end
