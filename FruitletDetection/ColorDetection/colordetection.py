import cv2
import os

#Read all the images in positive/rawdata dir
for file in os.listdir("../../../Cascade/training/positive/rawdata"):
    img=cv2.imread("../../../Cascade/training/positive/rawdata/"+file)
    img=cv2.resize(img,(400,400))
    #Convert the image to the HSV colorspace
    hsvImg = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)

    #Generate a mask using the defined upper a lower bounds
    mask = cv2.inRange(hsvImg, (0, 30, 100), (25, 255, 255))

    #Find the contours in the mask
    contours,_ = cv2.findContours(mask, cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE)
    #Sort the contours based on the amount of area they cover
    contours = sorted(contours, key=cv2.contourArea, reverse=True)

    # if the countour is round enough, draw a circle
    for cnt in contours:
        (x,y),radius = cv2.minEnclosingCircle(cnt)
        center = (int(x),int(y))
        radius = int(radius)
        if radius > 30:
            cv2.circle(img,center,radius,(0,255,0),2)
        
    #cv2.drawContours( img, contours, -1, (255,0,0), 3 )
    cv2.imshow('img', img)


    cv2.waitKey(0)
cv2.destroyAllWindows()