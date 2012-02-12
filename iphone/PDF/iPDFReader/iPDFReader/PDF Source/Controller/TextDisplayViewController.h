//
//  TextDisplayViewController.h
//  iPDFReader
//
//  Created by Cuong Tran on 2/12/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
@class TextDisplayViewController;
@protocol TextDisplayViewControllerDelegate 
-(void)dismissTextDisplayViewController:(TextDisplayViewController *)controller;
@end

@interface TextDisplayViewController : UIViewController {
    IBOutlet UIActivityIndicatorView    *_activityIndicatorView;
	IBOutlet UITextView                 *_textView;
    
    NSString                            *_text;
    NSString                            *_path;
}
-(IBAction)actionBack:(id)sender;
@property (nonatomic,retain) UIActivityIndicatorView *activityIndicatorView;
@property (nonatomic,retain) UITextView *textView;
@property (nonatomic,copy) NSString *text;
@property (nonatomic,assign) id<TextDisplayViewControllerDelegate> delegate;
-(void)clearText;
-(void)updateWithTextOfPage:(NSUInteger)page documentPath:(NSString *) path;
@end
