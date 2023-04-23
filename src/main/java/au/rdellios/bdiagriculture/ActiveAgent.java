package au.rdellios.bdiagriculture;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.*;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.service.annotation.OnStart;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentFeature;

import java.util.ArrayList;
import java.util.List;


@Agent(type = BDIAgentFactory.TYPE)
@Plans({
        //@Plan(trigger = @Trigger(goals = ActiveAgent.AcquireMessage.class), body = @Body(ListenForMessage.class))
})
public class ActiveAgent extends BaseAgent {
    @AgentFeature
    protected IBDIAgentFeature bdiFeature;
    @Belief
    protected List<ISpaceObject> trees = new ArrayList<ISpaceObject>();

    @Goal
    public class AcquireMessage {
        //read booktrading example - to understand comms
        //the agent will wait for a message from the scout, and then it will process the message
        //if its given a series of trees that it needs to spray, it will fill a vat and begin spraying otherwise it may handthin, or do nothing
    }

    @OnStart
    public void body() {
        System.out.println("Agent Created: ActiveAgent");
    }
}









