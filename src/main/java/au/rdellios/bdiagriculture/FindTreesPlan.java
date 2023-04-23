package au.rdellios.bdiagriculture;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Plan
public class FindTreesPlan {
    @PlanCapability
    protected ScoutAgent scoutAgent;

    @PlanAPI
    protected IPlan rplan;

    @PlanBody
    public void body() {
        System.out.println("Starting FindTreesPlan...");
        //Type of Objects to find
        String[] types = {"weed"};
        int visionRange = (int) scoutAgent.getMyself().getProperty("vision_range");
        //Get all objects within 3 spaces of the scout
        Set<ISpaceObject> nearGridObjects = scoutAgent.getEnvironment().getNearGridObjects(scoutAgent.getPosition(), visionRange, types);
        //Get direction of the scout

        Direction direction = Direction.valueOf((String) scoutAgent.getMyself().getProperty("direction"));
        //Objects in View
        List<ISpaceObject> overlayObjs = new ArrayList<>();
        int objectsFound = 0;

        //Loop through the nearGridObjects
        System.out.println("FindTreesPlan: " + nearGridObjects.size() + " objects Found surrounding the scout at a max distance of " + visionRange);
        for (ISpaceObject nearGridObject : nearGridObjects) {
            //Get the position of the nearGridObject
            IVector2 objPosition = (IVector2) nearGridObject.getProperty(Space2D.PROPERTY_POSITION);

            //Get the position difference between the scout and the nearGridObject
            int xDiff = objPosition.getXAsInteger() - scoutAgent.getPosition().getXAsInteger();
            int yDiff = objPosition.getYAsInteger() - scoutAgent.getPosition().getYAsInteger();

            //Check if the nearGridObject is in the direction the scout is facing
            switch (direction) {
                case LEFT:
                    if ((xDiff >= -3 && xDiff < 0) && (yDiff <= 1 && yDiff >= -1)) {
                        //Apply a highlight to the object, if it's within the scouts vision range
                        overlayObjs.add(ScoutAgent.updateHighlight(nearGridObject, new Color(246, 213, 46, 85)));
                        //Ignore the object if it's already been added to the trees list
                        if (scoutAgent.trees.contains(nearGridObject) || scoutAgent.exploredTrees.contains(nearGridObject))
                            continue;
                        //Add the object to the trees list
                        scoutAgent.trees.add(nearGridObject);
                        objectsFound++;
                    }

                    break;
                case RIGHT:
                    if (xDiff > 0 && xDiff <= 3 && (yDiff <= 1 && yDiff >= -1)) {
                        overlayObjs.add(ScoutAgent.updateHighlight(nearGridObject, new Color(246, 213, 46, 85)));
                        if (scoutAgent.trees.contains(nearGridObject) || scoutAgent.exploredTrees.contains(nearGridObject))
                            continue;
                        scoutAgent.trees.add(nearGridObject);
                        objectsFound++;
                    }
                    break;
                case UP:
                    if (yDiff >= -3 && yDiff < 0 && (xDiff <= 1 && xDiff >= -1)) {
                        //  ScoutAgent.trees.add(nearGridObject);
                        overlayObjs.add(ScoutAgent.updateHighlight(nearGridObject, new Color(246, 213, 46, 85)));
                        if (scoutAgent.trees.contains(nearGridObject) || scoutAgent.exploredTrees.contains(nearGridObject))
                            continue;
                        scoutAgent.trees.add(nearGridObject);
                        objectsFound++;
                    }
                    break;
                case DOWN:
                    if (yDiff > 0 && yDiff <= 3 && (xDiff <= 1 && xDiff >= -1)) {
                        // ScoutAgent.trees.add(nearGridObject);
                        overlayObjs.add(ScoutAgent.updateHighlight(nearGridObject, new Color(246, 213, 46, 85)));
                        if (scoutAgent.trees.contains(nearGridObject) || scoutAgent.exploredTrees.contains(nearGridObject))
                            continue;
                        scoutAgent.trees.add(nearGridObject);
                        objectsFound++;
                    }
                    break;
            }
        }
        rplan.waitFor(250).get();
        //Clear all object overlays
        for (ISpaceObject objId : overlayObjs) {
            ScoutAgent.updateHighlight(objId, new Color(0, 0, 0, 0));
        }
        //If there are no objects in view, throw a PlanFailureException
        if (objectsFound == 0) {
            System.out.println("FindTreesPlan: No Object Found");
            throw new PlanFailureException();
        }
        //Print the number of objects found
        System.out.println("FindTreesPlan: Object Found: " + objectsFound);
    }
}
