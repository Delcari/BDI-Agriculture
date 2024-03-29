package au.rdellios.bdiagriculture;


import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.*;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bridge.service.annotation.OnStart;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.math.IVector2;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentFeature;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


@Agent(type = BDIAgentFactory.TYPE)
@Goals(@Goal(clazz = ScoutBoundary.class, publish = @Publish(type = IScoutBoundary.class), deliberation = @Deliberation(inhibits = ScoutAgent.MoveTo.class)))
@Plans({
        @Plan(trigger = @Trigger(goals = ScoutAgent.LocateTree.class), body = @Body(FindTreesPlan.class)),
        //@Plan(trigger = @Trigger(goals = ScoutBoundary.class), body = @Body(FindTreePlan.class)),
        @Plan(trigger = @Trigger(goals = ScoutAgent.MoveTo.class), body = @Body(SAMovePlan.class)),
        @Plan(trigger = @Trigger(goals = ScoutAgent.InspectTree.class), body = @Body(InspectTreePlan.class)),
        @Plan(trigger = @Trigger(goals = ScoutBoundary.class), body = @Body(AreaScoutPlan.class)),
})

public class ScoutAgent extends BaseAgent {
    //Track how long the agent spent idling
    protected long idleTime = 0;
    protected long objectiveStartTime = 0;
    protected int totalTreesTreated = 0;
    protected int totalTreatments = 0;

    @AgentFeature
    protected IBDIAgentFeature bdiFeature;
    @Belief
    protected List<ISpaceObject> trees = new ArrayList<ISpaceObject>();

    @Belief
    protected IVector2[] boundary = new IVector2[2];

    @Belief
    protected boolean boundaryExplored = false;

    //Once all of the trees have been explored, the scout will move the exploredTrees back into the trees list
    //and then wait for the cooldown to finish before inspecting the trees again. Once the trees all have reached an optimal state,
    //the boundary has then been successfully optimised and the scout will move to the next boundary.
    protected List<ISpaceObject> exploredTrees = new ArrayList<ISpaceObject>();

    @Goal(recur = true)
    public class LocateTree {
    }

    @Goal(deliberation = @Deliberation(cardinalityone = true))
    public class MoveTo {
        @GoalCreationCondition(rawevents = @RawEvent(value = ChangeEvent.FACTADDED, second = "trees"))
        public static boolean checkTree(ISpaceObject obj) {
            return obj != null;
        }

        public MoveTo() {
        }
    }

    @Goal(deliberation = @Deliberation(inhibits = ScoutAgent.LocateTree.class))
    public class InspectTree {
        @GoalParameter
        protected ISpaceObject targetObj;

        public ISpaceObject getTargetObj() {
            return targetObj;
        }

        public InspectTree(ISpaceObject targetObj) {
            this.targetObj = targetObj;
        }
    }

    //Which object is closer the 'scoutPosition'?
    public boolean isClosestObj(IVector2 obj1Pos, IVector2 obj2Pos, IVector2 scoutPosition) {
        int obj1XDiff = obj1Pos.getXAsInteger() - scoutPosition.getXAsInteger();
        int obj1YDiff = obj1Pos.getYAsInteger() - scoutPosition.getYAsInteger();
        int obj2XDiff = obj2Pos.getXAsInteger() - scoutPosition.getXAsInteger();
        int obj2YDiff = obj2Pos.getYAsInteger() - scoutPosition.getYAsInteger();
        //Return true if the first object is closer than the second object
        return Math.abs(obj1XDiff) <= Math.abs(obj2XDiff) && Math.abs(obj1YDiff) <= Math.abs(obj2YDiff);
    }

    //Update the highlight of an object
    public static ISpaceObject updateHighlight(ISpaceObject obj, Color color) {
        if (obj.getProperty("highlight").equals(Reference.HL_GREEN)) return obj;
        if (obj.getProperty("highlight").equals(Reference.HL_ORANGE) && color != Reference.HL_GREEN) return obj;

        obj.setProperty("highlight", color);
        return obj;
    }

    @OnStart
    public void body() {
        System.out.println("Agent Created: ScoutAgent");
        bdiFeature.dispatchTopLevelGoal(new LocateTree()).get();
    }
}









