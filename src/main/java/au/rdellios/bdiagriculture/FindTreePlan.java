package au.rdellios.bdiagriculture;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.math.Vector1Int;

@Plan
public class FindTreePlan {
    @PlanCapability
    protected ScoutAgent scoutAgent;
    @PlanAPI
    protected IPlan rplan;


    @PlanBody
    public void body() {
        System.out.println("Starting FindTreePlan...");
        ISpaceObject nearObj = scoutAgent.env.getNearestObject(scoutAgent.getPosition(), new Vector1Int(5), "weed");
        if (nearObj != null) {
            System.out.println("FindTreePlan: Object Found");
        } else {
            System.out.println("FindTreePlan: No Objects Found");
            throw new PlanFailureException();
        }
        try {
            System.out.println("Dispatching subgoal - MoveTo...");
            //rplan.dispatchSubgoal(scoutAgent.new MoveTo(nearObj)).get();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }

}
