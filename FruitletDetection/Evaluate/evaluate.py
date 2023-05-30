import cv2
import os
import numpy as np
from tools import calc_iou, resizeRect

imWidth = 400
imHeight = 400

#The name of the file/location of the file which logged the predictions here.
#Format: "imageName/Loc numDetections x y w h"...
#Example: "rawdata/13.jpg 2 315 222 180 84 307 128 180 84"
predFileName = "out/colorResultsRed.txt"
#Ground predictions - same format
truthFileName = "rawdata/annotations.txt"
outputFileName = "out/eval/colorResultsRed.csv"

#function to read line and get all detection boxes
def getDetections(line, scale = 1):
    line = line.strip()
    try:
        filename, objCount, line = line.split(' ', 2)
    except:
        filename, line = line.split(' ', 1)

        return Detections(filename)
    detections = Detections(filename)
    objCount = (int)(objCount)
    for i in range(objCount):  
        if (i == objCount - 1):
            x,y,w,h = line.split(' ', 3)
        else:
            x,y,w,h,line = line.split(' ', 4)
        detections.addDetection([int(x),int(y),int(w),int(h)])
    return detections



#create an object to store the list lists of [x,y,w,h] and filename
class Detections:
    def __init__(self, filename):
        self.filename = filename
        self.detection = []

    def addDetection(self, a):
        self.detection.append(a)                      

    def getDetectionList(self):
        return self.detection

    def getFilename(self):
        return self.filename

outputFile = open(outputFileName, "w")
outputFile.write("filename" + "," + "visibleFruitlets" + "," + "truePositives" + "," + "falsePositives" + "\n")
truthFile = open(truthFileName)
predFile = open(predFileName)

#create new applelist
truthList = []
predList = []

#Read the truth file
for line in predFile:                           
    predList.append(getDetections(line))
#Read the pred file
for line in truthFile:
    truthList.append(getDetections(line))

#Compare the detections
for pred in predList:
    for truth in truthList:
        #if the filenames match, load the image
        if (pred.getFilename() == truth.getFilename()):
            img = cv2.imread(truth.getFilename())
        else: 
            continue
        #Get the image dimensions
        height, width = img.shape[:2]
        if (imWidth != 0 or imHeight != 0):
            #Update the image dimensions
            img = cv2.resize(img, (imWidth, imHeight))

            #Get the detections
            predDetections = pred.getDetectionList()
            truthDetections = truth.getDetectionList()
            totalTruths = len(truthDetections)


            for i in range(len(truthDetections)):
                #Scale the detections
                truthDetections[i] = resizeRect(truthDetections[i], imWidth/width, imHeight/height)
                #Draw the ground truth
                img = cv2.rectangle(img,(truthDetections[i][0],truthDetections[i][1]),(truthDetections[i][0]+truthDetections[i][2],truthDetections[i][1]+truthDetections[i][3]),(0,0,255),2)
            tp = 0
            fp = 0
            for i in range(len(predDetections)):
                predDetections[i] = resizeRect(predDetections[i], imWidth/width, imHeight/height)
                #Draw the prediction
                img = cv2.rectangle(img,(predDetections[i][0],predDetections[i][1]),(predDetections[i][0]+predDetections[i][2],predDetections[i][1]+predDetections[i][3]),(0,255,0),2)
                
                fp += 1
                for truthDetection in truthDetections:
                    #Calculate the iou
                    iou = calc_iou(predDetections[i], truthDetection)
                    #If the iou is greater than 0.5, it is a true positive

                    if (iou > 0.5):
                        tp += 1
                        fp -= 1
                        iou = round(iou,2)
                        x = predDetections[i][0]
                        y = predDetections[i][1]
                        img=cv2.putText(img,str(iou), (x,y-10), cv2.FONT_HERSHEY_SIMPLEX, 0.9, (36,255,12), 2)
                        #remove the item from the truth list 
                        truthDetections.remove(truthDetection)
        #log the stats
        print("Image: " + pred.getFilename())
        print("Ground Truth: " + str(totalTruths))
        print("True Positives: " + str(tp))
        print("False Positives: " + str(fp))
        outputFile.write(pred.getFilename() + "," + str(totalTruths) + "," + str(tp) + "," + str(fp) + "\n")
        cv2.imshow("Image", img)
        cv2.waitKey(0)
        cv2.destroyAllWindows()
        cv2.waitKey(1)

outputFile.close()




