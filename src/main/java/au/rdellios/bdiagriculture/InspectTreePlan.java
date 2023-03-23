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
        //Log the time of the last interaction with the tree
        targetTree.setProperty("lastInteraction", System.currentTimeMillis());
        //Highlight the tree
        ScoutAgent.updateHighlight(targetTree, new Color(149, 255, 83, 85));
        System.out.println("InspectTreePlan: Tree Inspected");
    }
}

