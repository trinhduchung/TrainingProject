//
//  CPersistentCache.h
//  PDFReader
//
//  Created by Jonathan Wight on 06/02/11.
//  Copyright 2011 toxicsoftware.com. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface CPersistentCache : NSObject {
    
}

@property (readonly, nonatomic, retain) NSString *name;
@property (readonly, nonatomic, copy) BOOL (^converterBlock)(id inObject, NSData **outData, NSString **outType, NSError **outError); 
@property (readonly, nonatomic, copy) BOOL (^reverseConverterBlock)(NSData *inData, NSString *inType, id *outObject, NSError **outError); 

- (BOOL)containsObjectForKey:(id)key;
- (id)objectForKey:(id)key;
- (void)setObject:(id)obj forKey:(id)key;
- (void)setObject:(id)obj forKey:(id)key cost:(NSUInteger)g;
- (void)removeObjectForKey:(id)key;

@end
