
import cv2
import os

#cascade File
cascade=cv2.CascadeClassifier("cascade.xml")

#Read all the images in positive/rawdata dir
for file in os.listdir("positive/rawdata"):
    img= cv2.imread("positive/rawdata/"+file)
    resized = cv2.resize(img,(400,400))
    gray=cv2.cvtColor(resized,cv2.COLOR_BGR2GRAY)

    apples=cascade.detectMultiScale(gray,3.5, 17)

    #draw rectangles around detected objects
    for(x,y,w,h) in apples:
        resized=cv2.rectangle(resized,(x,y),(x+w,y+h),(0,255,0),2)
    #display the image
    cv2.imshow('img',resized)
    #wait for inputs
    cv2.waitKey(0)
cv2.destroyAllWindows()