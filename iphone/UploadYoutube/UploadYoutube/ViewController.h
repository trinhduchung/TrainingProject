//
//  ViewController.h
//  UploadYoutube
//
//  Created by Cuong Tran on 2/1/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "GData.h"

@interface ViewController : UIViewController {
    NSString                    *_username;
    NSString                    *_password;
    BOOL                        _isSignIn;
    
    NSString                    *_clientID;
    NSString                    *_clientSecret;
    BOOL                        _hasClientID;
    
    NSString                    *_devKey;
    BOOL                        _hasDevKey;
    
    NSString                    *_videoTitle;
    BOOL                        _hasTitle;
    
    NSString                    *_path;
    NSString                    *_uploadLocationURL;
    BOOL                        _hasPath;
    
    BOOL                        _canUpload;
    BOOL                        _isUploading;
    BOOL                        _canRestartUpload;
    BOOL                        _isUploadPaused;
    
    GDataServiceTicket          *_uploadTicket;
    NSError                     *_uploadError;
}
+ (ViewController *) sharedViewController;
@end
