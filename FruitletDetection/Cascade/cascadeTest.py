from tools import resizeRect;

import cv2
import numpy as np
#Cascade file
cascade=cv2.CascadeClassifier("cascade.xml")
#Scaling factor
percScaling = (float)(0)
#Debug mode
debug = True
colorFilter = False
morphOp = True
#Image size
imWidth = 400
imHeight = 400
resFileName = "out/cascadeResultsRed.txt"
sampleFile = "redSamples.txt"

#A file containing a list of samples you would like to test
#Format: "fileName/loc"
#Example: "rawdata/31.jpg"
files = open(sampleFile)

#create/open a file to write the results to
resFile = open(resFileName, "w")

for file in files:
    if file == "\n":
        continue
    file = file.strip()
    #Get the image name
    filename = file
    img= cv2.imread(filename)
    #Get the initial image size
    height, width = img.shape[:2]
    #Resize the image if desired
    if (imWidth != 0 and imHeight != 0):
        img = cv2.resize(img,(imWidth,imHeight))

    #new ----
    #3,3 gaussian blur improved recall
    img = cv2.GaussianBlur(img,(3,3),0)

    #Convert the image to grayscale
    grayImg=cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)
    if (colorFilter):
        hsvImg=cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
        #Generate a mask using the defined upper a lower bounds
        mask1 = cv2.inRange(hsvImg, (0,0,0), (20, 255,255))
        mask2 = cv2.inRange(hsvImg, (160,0,0), (180, 255, 255))
        #Combine Masks
        mask = cv2.bitwise_or(mask1, mask2)
        #ellipse kernel
        if (morphOp):
            kernel = cv2.getStructuringElement(cv2.MORPH_ELLIPSE,(7,7))
            mask = cv2.morphologyEx(mask, cv2.MORPH_CLOSE, kernel,iterations=8)
        #Apply the mask to the image
        maskImg = cv2.bitwise_and(grayImg,grayImg, mask= mask)

    #Detect the objects in the image
    if (colorFilter):
        detections=cascade.detectMultiScale(maskImg,4, 14)
    else: 
        detections=cascade.detectMultiScale(grayImg,4, 14)


    #Log the image name and the number of detected objects
    resFile.write(filename + " " + str(len(detections)))
    for(x,y,w,h) in detections:
        img=cv2.rectangle(img,(x,y),(x+w,y+h),(0,255,0),2)
        #Resize the detected objects if the image was resized
        if (imWidth != 0 and imHeight != 0):
            logRect = resizeRect([x,y,w,h], (float)(width/imWidth), (float)(height/imHeight))
        #Log the location of the detected object
        resFile.write(" " + str(logRect[0]) + " " + str(logRect[1]) + " " + str(logRect[2]) + " " + str(logRect[3]))
        #Draw the rectangles around the detected objects
    resFile.write("\n")

    #display the image
    if (debug):
        cv2.imshow('img',img)
        if (colorFilter):
            cv2.imshow('mask',maskImg)

    #wait for inputs
    if debug:
        cv2.waitKey(0)
resFile.close() 
cv2.destroyAllWindows()