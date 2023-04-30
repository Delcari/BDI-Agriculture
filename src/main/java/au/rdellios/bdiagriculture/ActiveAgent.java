package au.rdellios.bdiagriculture;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.IBDIAgent;
import jadex.bdiv3.annotation.*;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bridge.service.annotation.OnStart;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.micro.annotation.*;

import java.util.ArrayList;
import java.util.List;


@Agent(type = BDIAgentFactory.TYPE)
@Plans({
        @Plan(trigger = @Trigger(goals = ActiveAgent.MoveTo.class), body = @Body(AAMovePlan.class)),
        @Plan(trigger=@Trigger(service=@ServiceTrigger(type=IInformTree.class)), body = @Body(InformTreePlan.class)),
})
@ProvidedServices(@ProvidedService(name="infotree", type=IInformTree.class,
        implementation=@Implementation(IBDIAgent.class)))
public class ActiveAgent extends BaseAgent {
    @AgentFeature
    protected IBDIAgentFeature bdiFeature;
    @Belief
    protected List<ISpaceObject> trees = new ArrayList<ISpaceObject>();


//        //the agent will wait for a message from the scout, and then it will process the message
//        //if its given a series of trees that it needs to spray, it will fill a vat and begin spraying otherwise it may handthin, or do nothing

    @Goal(deliberation = @Deliberation(cardinalityone = true))
    public class MoveTo {
        @GoalCreationCondition(rawevents = @RawEvent(value = ChangeEvent.FACTADDED, second = "trees"))
        public static boolean checkTree(ISpaceObject obj) {
            return obj != null;
        }
    }

    @OnStart
    public void body() {
        System.out.println("Agent Created: ActiveAgent");
    }
}









