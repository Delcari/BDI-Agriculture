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
        while (((IVector2) scoutAgent.getPosition() != targetPos)) {
            //Which direction is closer? Left/Right/Up/Down
            IVector1 movement = scoutAgent.env.getShortestDirection(scoutAgent.getPosition().getX(), targetPos.getX(), true);
            if (movement.getAsInteger() != 0) {
                //Move towards target
                scoutAgent.env.setPosition(oid, new Vector2Int(scoutAgent.getPosition().getX().getAsInteger() + movement.getAsInteger(), scoutAgent.getPosition().getYAsInteger()));
            } else {
                movement = scoutAgent.env.getShortestDirection(scoutAgent.getPosition().getY(), targetPos.getY(), false);
                if (movement.getAsInteger() != 0) {
                    scoutAgent.env.setPosition(oid, new Vector2Int(scoutAgent.getPosition().getX().getAsInteger(), scoutAgent.getPosition().getYAsInteger() + movement.getAsInteger()));
                }
            }
        }
        System.out.println("MovePlan: Target Location Reached");
    }
}
