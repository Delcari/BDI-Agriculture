package au.rdellios.bdiagriculture;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;

@Plan
public class MovePlan {
    @PlanCapability
    protected ScoutAgent scoutAgent;

//    @PlanReason
//    protected MoveTo goal;

    @PlanBody
    public void body(ISpaceObject targetObj, long overlayObjId) {
        System.out.println("Starting MovePlan...");
        IVector2 targetPos = (IVector2) targetObj.getProperty(Space2D.PROPERTY_POSITION);
        Object oid = this.scoutAgent.env.getAvatar(this.scoutAgent.getAgent().getDescription()).getId();
        // Continue moving until the Scout is at the targetPosition
        while (!this.scoutAgent.getPosition().equals(targetPos)) {
            this.scoutAgent.getAgent().waitForDelay(250).get();
            //Which direction is closer? Left/Right/Up/Down
            int closestDir = (this.scoutAgent.env.getShortestDirection(this.scoutAgent.getPosition().getX(), targetPos.getX(), true)).getAsInteger();
            //Move in specified direction
            if (closestDir != 0) {
                if ((closestDir < 0)) {
                    this.scoutAgent.Move(this.scoutAgent.getEnvironment(), this.scoutAgent.getMyself(), MoveDir.LEFT);
                } else {
                    this.scoutAgent.Move(this.scoutAgent.getEnvironment(), this.scoutAgent.getMyself(), MoveDir.RIGHT);
                }
            } else {
                closestDir = (this.scoutAgent.env.getShortestDirection(this.scoutAgent.getPosition().getY(), targetPos.getY(), false)).getAsInteger();
                if (closestDir != 0) {
                    if ((closestDir < 0)) {
                        this.scoutAgent.Move(this.scoutAgent.getEnvironment(), this.scoutAgent.getMyself(), MoveDir.UP);
                    } else {
                        this.scoutAgent.Move(this.scoutAgent.getEnvironment(), this.scoutAgent.getMyself(), MoveDir.DOWN);
                    }
                }
            }
        }

        //Remove the overlay
        this.scoutAgent.env.destroySpaceObject(overlayObjId);
        System.out.println("MovePlan: Target Location Reached");
    }
}
