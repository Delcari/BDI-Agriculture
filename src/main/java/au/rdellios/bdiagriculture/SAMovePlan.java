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

import java.util.Objects;

@Plan
public class SAMovePlan {
    @PlanCapability
    protected ScoutAgent scoutAgent;

//    @PlanReason
//    protected MoveTo goal;

    @PlanAPI
    IPlan rplan;
    @PlanBody
    public void body() {
        //starttime
        long startTime = System.currentTimeMillis();
        System.out.println("Starting MovePlan...");
        //If there are no trees left to explore, end the plan
        if (scoutAgent.trees.isEmpty()) {
            System.out.println("MovePlan: No Trees Found");
            throw new PlanFailureException();
        }
        //Get the next tree to explore
        int i = 0;
        ISpaceObject targetObj = scoutAgent.trees.get(i);
        while ((long) targetObj.getProperty("interactCooldown") > 0 || Objects.equals((String) targetObj.getProperty("cropLoad"), "suboptimal")) {
            ++i;
            i = i % scoutAgent.trees.size();
            targetObj = scoutAgent.trees.get(i);
        }
        //Calculating time spent idling
        scoutAgent.idleTime = scoutAgent.idleTime + (System.currentTimeMillis() - startTime);

        //Get the position of the tree
        IVector2 targetPos = (IVector2) targetObj.getProperty(Space2D.PROPERTY_POSITION);
        //Get the ID of the Scout
        Object oid = this.scoutAgent.env.getAvatar(this.scoutAgent.getAgent().getDescription()).getId();
        // Continue moving until the Scout is at the targetPosition
        while (this.scoutAgent.getPosition().getDistance(targetPos).getAsInteger() > 1) {
            rplan.waitFor(Reference.TIME_STEP * Reference.MOVE_STEPS).get();
            //Which direction is closer? Left/Right/Up/Down
            Direction dir = this.scoutAgent.whichDirection(this.scoutAgent.env, this.scoutAgent.getPosition(), targetPos);
            //If the agent is one tile away from the target
            if (dir != null) {
                this.scoutAgent.Move(this.scoutAgent.getEnvironment(), this.scoutAgent.getMyself(), dir);
            }
        }
        System.out.println("MovePlan: Target Location Reached");
        //Dispatch subgoal to inspect the target object
        System.out.println("MovePlan: Dispatching InspectTree SubGoal");
        rplan.dispatchSubgoal(scoutAgent.new InspectTree(targetObj)).get();
    }
}
