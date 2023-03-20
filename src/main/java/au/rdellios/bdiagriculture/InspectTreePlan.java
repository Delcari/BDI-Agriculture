package au.rdellios.bdiagriculture;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.runtime.IPlan;

@Plan
public class InspectTreePlan {
    @PlanCapability
    protected ScoutAgent scoutAgent;

    @PlanAPI
    protected IPlan rplan;

    @PlanBody
    public void body()
    {
        //Move diagonal around object
        //Update BeliefBase - Information about Tree - proposed job
        //Send message to another agent - about job
    }
}
