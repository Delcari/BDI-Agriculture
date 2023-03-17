package au.rdellios.bdiagriculture;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector1;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;

@Plan
public class MovePlan {
    @PlanCapability
    protected ScoutAgent scoutAgent;

//    @PlanReason
//    protected MoveTo goal;

    @PlanBody
    public void body(ISpaceObject targetObj) {
        System.out.println("Starting MovePlan...");
        IVector2 targetPos = (IVector2) targetObj.getProperty(Space2D.PROPERTY_POSITION);
        Object oid = scoutAgent.env.getAvatar(scoutAgent.getAgent().getDescription()).getId();
        // Continue moving until the Scout is at the targetPosition
        while (scoutAgent.getPosition() != targetPos) {
            scoutAgent.getAgent().waitForDelay(250).get();
            //Which direction is closer? Left/Right/Up/Down
            int movement = (scoutAgent.env.getShortestDirection(scoutAgent.getPosition().getX(), targetPos.getX(), true)).getAsInteger();
            //Move in specified direction
            if (movement != 0) {
                if ((movement < 0)) {
                    //Rotate Scout to face left
                    scoutAgent.myself.setProperty("direction", "left");
                } else {
                    scoutAgent.myself.setProperty("direction", "right");
                }
                //Move towards target
                scoutAgent.env.setPosition(oid, new Vector2Int(scoutAgent.getPosition().getX().getAsInteger() + movement, scoutAgent.getPosition().getYAsInteger()));
            } else {
                movement = (scoutAgent.env.getShortestDirection(scoutAgent.getPosition().getY(), targetPos.getY(), false)).getAsInteger();
                if (movement != 0) {
                    if ((movement < 0)) {
                        scoutAgent.myself.setProperty("direction", "up");
                    } else {
                        scoutAgent.myself.setProperty("direction", "down");
                    }
                    scoutAgent.env.setPosition(oid, new Vector2Int(scoutAgent.getPosition().getX().getAsInteger(), scoutAgent.getPosition().getYAsInteger() + movement));
                }
            }
        }
        System.out.println("MovePlan: Target Location Reached");
    }
}
