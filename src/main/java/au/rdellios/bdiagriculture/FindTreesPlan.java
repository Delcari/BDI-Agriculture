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
        String direction = (String) scoutAgent.getMyself().getProperty("direction");

        ISpaceObject closestObjInView = null;
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
                case "left":
                    if ((xDiff >= -3 && xDiff <= 0) && (yDiff <= 1 && yDiff >= -1)) {
                        //Check if the nearGridObject is closer than the current closest object
                        if (closestObjInView == null || isClosestObj(nearGridObject, closestObjInView, scoutAgent.getPosition()))
                            closestObjInView = nearGridObject;
                    }
                    break;
                case "right":
                    if (xDiff >= 0 && xDiff <= 3 && (yDiff <= 1 && yDiff >= -1)) {
                        if (closestObjInView == null || isClosestObj(nearGridObject, closestObjInView, scoutAgent.getPosition()))
                            closestObjInView = nearGridObject;
                    }
                    break;
                case "up":
                    if (yDiff >= -3 && yDiff <= 0 && (xDiff <= 1 && xDiff >= -1)) {
                        if (closestObjInView == null || isClosestObj(nearGridObject, closestObjInView, scoutAgent.getPosition()))
                            closestObjInView = nearGridObject;
                    }
                    break;
                case "down":
                    if (yDiff >= 0 && yDiff <= 3 && (xDiff <= 1 && xDiff >= -1)) {
                        if (closestObjInView == null || isClosestObj(nearGridObject, closestObjInView, scoutAgent.getPosition()))
                            closestObjInView = nearGridObject;
                    }
                    break;
            }
        }
        //If there are no objects in view, throw a PlanFailureException
        if (closestObjInView == null) {
            System.out.println("FindTreesPlan: No Object Found");
            throw new PlanFailureException();
        }

        //Print the objects in view and dispatch a subgoal to move to the closest object
        System.out.println("FindTreesPlan: Found Object: ");
        System.out.println(closestObjInView);
        System.out.println("FindTreesPlan: Dispatching MoveTo Subgoal");
        rplan.dispatchSubgoal(scoutAgent.new MoveTo(closestObjInView)).get();
    }

    private boolean isClosestObj(ISpaceObject obj1, ISpaceObject obj2, IVector2 scoutPosition) {
        //Get the position of the objects
        IVector2 obj1Pos = (IVector2) obj1.getProperty(Space2D.PROPERTY_POSITION);
        IVector2 obj2Pos = (IVector2) obj2.getProperty(Space2D.PROPERTY_POSITION);
        //Get the position difference between the scout and the objects
        int obj1XDiff = obj1Pos.getXAsInteger() - scoutPosition.getXAsInteger();
        int obj1YDiff = obj1Pos.getYAsInteger() - scoutPosition.getYAsInteger();
        int obj2XDiff = obj2Pos.getXAsInteger() - scoutPosition.getXAsInteger();
        int obj2YDiff = obj2Pos.getYAsInteger() - scoutPosition.getYAsInteger();
        //Return true if the first object is closer than the second object
        return Math.abs(obj1XDiff) < Math.abs(obj2XDiff) && Math.abs(obj1YDiff) < Math.abs(obj2YDiff);
    }
}
