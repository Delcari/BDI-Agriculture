package au.rdellios.bdiagriculture;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
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
        // System.out.println("Starting FindTreesPlan...");

        if (scoutAgent.boundary[0] == (null)) {
            //System.out.println("FindTreesPlan: No Boundary Found");
            throw new PlanFailureException();
        }

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
        // System.out.println("FindTreesPlan: " + nearGridObjects.size() + " objects Found surrounding the scout at a max distance of " + visionRange);
        for (ISpaceObject nearGridObject : nearGridObjects) {
            if (scoutAgent.inVision(nearGridObject)) {
                //Apply a highlight to the object, if it's within the scouts vision range
                overlayObjs.add(ScoutAgent.updateHighlight(nearGridObject, Reference.HL_YELLOW));
                IVector2 objPos = (IVector2) nearGridObject.getProperty(Grid2D.PROPERTY_POSITION);
                //Ignore the object if it's outside the scouts boundary
                if (!((scoutAgent.boundary[0].getXAsInteger() <= objPos.getXAsInteger() && objPos.getXAsInteger() <= scoutAgent.boundary[1].getXAsInteger()) &&
                        (scoutAgent.boundary[0].getYAsInteger() <= objPos.getYAsInteger() && objPos.getYAsInteger() <= scoutAgent.boundary[1].getYAsInteger())))
                    continue;
                //Ignore the object if it's already been added to the trees list
                if (scoutAgent.trees.contains(nearGridObject) || scoutAgent.exploredTrees.contains(nearGridObject) || (String) (nearGridObject.getProperty("cropLoad")) == "optimal")
                    continue;
                //Add the object to the trees list
                System.out.println("OBJ Added: " + nearGridObject.getId());
                scoutAgent.trees.add(nearGridObject);
                objectsFound++;
            }
        }
        rplan.waitFor(Reference.TIME_STEP * Reference.HIGHLIGHT_STEPS).get();
        //Clear all object overlays
        for (ISpaceObject objId : overlayObjs) {
            ScoutAgent.updateHighlight(objId, new Color(0, 0, 0, 0));
        }
        //If there are no objects in view, throw a PlanFailureException
        if (objectsFound == 0) {
            // System.out.println("FindTreesPlan: No Object Found");
            throw new PlanFailureException();
        }
        //Print the number of objects found
        // System.out.println("FindTreesPlan: Object Found: " + objectsFound);
    }
}
