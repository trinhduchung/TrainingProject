//
//  SaveFilesViewController.h
//  iBack
//
//  Created by bohemian on 1/11/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "FileViewCell.h"
#import "AlertManager.h"
#import "GData.h"

@interface SaveFilesViewController : UIViewController<UITableViewDataSource, UITableViewDelegate, 
UINavigationControllerDelegate, FileViewCellDelegate, UITextFieldDelegate, FileSelectionAlertDelegate>
{
    IBOutlet UITableView    *filesTable;
    NSMutableArray          *movFiles;
    NSMutableDictionary     *needDeleteFiles;
    BOOL                    isEditTable;
    NSInteger               fileSelectedIndex;    
    
    GDataServiceTicket *mUploadTicket;
}
@property (nonatomic, retain)NSMutableArray *movFiles;
@property (nonatomic, retain)NSDictionary   *needDeleteFiles;
@property (nonatomic, retain)UITableView    *filesTable;
@property (nonatomic, assign)BOOL           isEditTable;
@property (nonatomic, assign)NSInteger      fileSelectedIndex;
- (void)loadFilesIntoArray;

- (GDataServiceTicket *)uploadTicket;
- (void)setUploadTicket:(GDataServiceTicket *)ticket;
- (GDataServiceGoogleYouTube *)youTubeService;
@end
