package au.rdellios.bdiagriculture;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.runtime.IPlan;
import jadex.extension.envsupport.environment.ISpaceObject;

import java.awt.*;

@Plan
public class InspectTreePlan {
    @PlanCapability
    protected ScoutAgent scoutAgent;

    @PlanAPI
    protected IPlan rplan;


    //Plan Proposal ------------------------------------------------------------------
    //Move diagonal around object
    //Update BeliefBase - Information about Tree - proposed job
    //Send message to another agent - about job
    //--------------------------------------------------------------------------------
    @PlanBody
    public void body(ISpaceObject targetTree) {
        System.out.println("Starting InspectTreePlan...");

        //Scout instead checks whether the tree is in an acceptable state to be inspected
        //Optimal - Suboptimal - Treated
        //If the state is treated - this means the tree has been interacted with by the other agent, and requires another review.
        //The tree could still be in a suboptimal state or optimal state, but the scout will not know this until it has been reinspected.
        //The tree can only be inspected after the applied treatment cooldown has been met.
        targetTree.setProperty("cropLoad", "subOptimal");

        //Move this to the ActiveAgent - ActiveAgent interacts with the tree - Sets cooldown, telling the scout when to check the tree again for updates.
        //Log the time of the last interaction with the tree
        targetTree.setProperty("lastInteraction", System.currentTimeMillis());
        //Highlight the tree
        ScoutAgent.updateHighlight(targetTree, new Color(149, 255, 83, 85));
        System.out.println("InspectTreePlan: Tree Inspected");
    }
}

