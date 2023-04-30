package au.rdellios.bdiagriculture;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.extension.envsupport.environment.ISpaceObject;

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
            if (scoutAgent.inVision(nearGridObject)) {
                //Apply a highlight to the object, if it's within the scouts vision range
                overlayObjs.add(ScoutAgent.updateHighlight(nearGridObject, new Color(246, 213, 46, 85)));
                //Ignore the object if it's already been added to the trees list
                if (scoutAgent.trees.contains(nearGridObject) || scoutAgent.exploredTrees.contains(nearGridObject))
                    continue;
                //Add the object to the trees list
                scoutAgent.trees.add(nearGridObject);
                objectsFound++;
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
