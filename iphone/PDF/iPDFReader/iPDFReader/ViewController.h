//
//  ViewController.h
//  iPDFReader
//
//  Created by Cuong Tran on 2/11/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ReaderViewController.h"
@class FileHelper;

@interface ViewController : UIViewController <UITableViewDataSource, UITableViewDelegate ,ReaderViewControllerDelegate>{
    UITableView     *_tableView;
    NSMutableArray  *_pdfFiles;
}

@end
