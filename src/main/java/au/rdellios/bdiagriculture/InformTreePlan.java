package au.rdellios.bdiagriculture;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.extension.envsupport.environment.ISpaceObject;

@Plan
public class InformTreePlan {
    @PlanCapability
    protected ActiveAgent activeAgent;

    @PlanBody
    public boolean body(ISpaceObject obj) {
        System.out.println("Starting InformTreePlan...");
        activeAgent.trees.add(obj);
        System.out.println("InformTreePlan: Tree successfully added");
        return true;
    }
}
