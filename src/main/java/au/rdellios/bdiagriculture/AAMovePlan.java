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

@Plan
public class AAMovePlan {
    @PlanCapability
    protected ActiveAgent activeAgent;

//    @PlanReason
//    protected MoveTo goal;

    @PlanAPI
    IPlan rplan;

    @PlanBody
    public void body() {
        System.out.println("Starting MovePlan...");
        //If there are no trees left to explore, end the plan
        if (activeAgent.trees.isEmpty()) {
            System.out.println("MovePlan: No Trees Found");
            throw new PlanFailureException();
        }
        //Get the next tree to explore
        ISpaceObject targetObj = activeAgent.trees.remove(0);
        //Get the position of the tree
        IVector2 targetPos = (IVector2) targetObj.getProperty(Space2D.PROPERTY_POSITION);
        // Continue moving until the Agent is at the targetPosition
        while (!this.activeAgent.getPosition().equals(targetPos)) {
            rplan.waitFor(Reference.TIME_STEP * Reference.MOVE_STEPS).get();
            //Which direction is closer? Left/Right/Up/Down
            Direction dir = this.activeAgent.whichDirection(this.activeAgent.env, this.activeAgent.getPosition(), targetPos);
            if (dir != null) {
                this.activeAgent.Move(this.activeAgent.getEnvironment(), this.activeAgent.getMyself(), dir);
            }
        }
        System.out.println("MovePlan: Target Location Reached");
        //Dispatch subgoal to inspect the target object
        System.out.println("MovePlan: Dispatching InspectTree SubGoal");
        rplan.dispatchSubgoal(activeAgent.new InteractTree(targetObj)).get();
    }
}
