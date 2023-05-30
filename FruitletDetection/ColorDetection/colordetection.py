import cv2
import os
import numpy as np
import time
from tools import resizeRect;

debug = True
erosionFilter = False
imWidth = 400
imHeight = 400
resFileName = "out/colorResults.txt"

#A file containing a list of samples you would like to test
#Format: "fileName/loc"
#Example: "rawdata/31.jpg"
sampleFile = "redSamples.txt"

#create/open a file to write the results to
resFile = open(resFileName, "w")
#the file containing the object locations
files = open(sampleFile)


#Read all the images in positive/rawdata dir
for file in files:
    #img=cv2.imread("../../../Cascade/training/positive/rawdata/"+file)
    file = file.strip()
    print(file)
    img=cv2.imread(file)
    height, width = img.shape[:2]
    #Resize the image if desired
    if (imWidth != 0 and imHeight != 0):
        img=cv2.resize(img,(imWidth,imHeight))
        
    orig=img
    blank = np.zeros(img.shape, dtype='uint8')
    img = cv2.GaussianBlur(img,(5,5),0)
    #Convert the image to the HSV colorspace
    hsvImg = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)

    #Generate a mask using the defined upper a lower bounds
    mask1 = cv2.inRange(hsvImg, (0,0,0), (20, 255,255))
    mask2 = cv2.inRange(hsvImg, (160,0,0), (180, 255, 255))

    #Combine Masks
    mask = cv2.bitwise_or(mask1, mask2)
    #Apply the mask to the image
    maskImg = cv2.bitwise_and(img,img, mask= mask)
    #Convert to image to grayscale
    #gray = cv2.cvtColor(mImg, cv2.COLOR_BGR2GRAY)

    kernel = np.ones((5,5),np.uint8)
    mask = cv2.erode(mask,kernel,iterations = 3)

    if erosionFilter:
        #Apply erosion filter to the image
        mask = cv2.erode(mask,kernel,iterations = 3)


    contours,_ = cv2.findContours(mask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_NONE)
    #Sort the contours based on the amount of area they cover
    contours = sorted(contours, key=cv2.contourArea, reverse=True)
    #mask = cv2.Canny(mask, 500, 600)

    detections = 0
    detectionBoundaries = ""
    for cnt in contours:
        #cv2.drawContours(img,[cnt],0,(122,212,255),-1)
        area = cv2.contourArea(cnt)
       # if area < 200:
         #   continue
        if erosionFilter:
            #get bounding box from contours
            x,y,w,h = cv2.boundingRect(cnt)
            #draw bounding box
            cv2.rectangle(img,(x,y),(x+w,y+h),(0,255,0),2)
        else:
            a = cv2.approxPolyDP(cnt, .02*cv2.arcLength(cnt,True),True)
            if len(a) >= 7:
                detections = detections + 1
                #cv2.drawContours(img,[a],0,(122,212,78),-1)
                cv2.drawContours(blank,[a],0,(255,255,255),-1)
                #get bounding box from contours
                x,y,w,h = cv2.boundingRect(cnt)
                #draw bounding box
                cv2.rectangle(img,(x,y),(x+w,y+h),(0,255,0),2)
                #Resize the detected objects if the image was resized
                if (imWidth != 0 and imHeight != 0):
                    logRect = resizeRect([x,y,w,h], (float)(width/imWidth), (float)(height/imHeight))
                detectionBoundaries += (" " + str(logRect[0]) + " " + str(logRect[1]) + " " + str(logRect[2]) + " " + str(logRect[3]))
    resFile.write(file + " " + str(detections) + detectionBoundaries)
    resFile.write("\n")

    if (debug):
        if erosionFilter:
            maskFilteredImg = cv2.bitwise_and(orig, orig, mask=mask)
        else:
            maskFilteredImg = cv2.bitwise_and(orig, blank)

        
        cv2.imshow('HSV Image', hsvImg)
        cv2.imshow('Original', orig)
        cv2.imshow('Color Mask Applied', maskImg)
        if erosionFilter:
            cv2.imshow('Erosion Filter Applied', mask)
        cv2.imshow('Filtered Contours', maskFilteredImg)
        cv2.imshow('Detection', img)

        cv2.waitKey(0)
cv2.destroyAllWindows()
