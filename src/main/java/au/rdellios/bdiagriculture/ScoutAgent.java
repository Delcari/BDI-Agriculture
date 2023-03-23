package au.rdellios.bdiagriculture;


import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.*;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bridge.service.annotation.OnStart;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentFeature;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


@Agent(type = BDIAgentFactory.TYPE)
@Plans({
        @Plan(trigger = @Trigger(goals = ScoutAgent.LocateTree.class), body = @Body(FindTreesPlan.class)),
        @Plan(trigger = @Trigger(goals = ScoutAgent.MoveTo.class), body = @Body(MovePlan.class)),
        @Plan(trigger = @Trigger(goals = ScoutAgent.InspectTree.class), body = @Body(InspectTreePlan.class)),})
public class ScoutAgent extends BaseAgent {
    @AgentFeature
    protected IBDIAgentFeature bdiFeature;

    @Belief
    protected List<ISpaceObject> trees = new ArrayList<ISpaceObject>();
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

    @Goal
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

    public IVector2 Move(Grid2D env, ISpaceObject obj, MoveDir moveDir) {
        Object oid = obj.getId();
        IVector2 newPos = (IVector2) obj.getProperty(Space2D.PROPERTY_POSITION);
        //Which direction is the agent going to move in?
        switch (moveDir) {
            case LEFT:
                //Update the direction the object is facing
                obj.setProperty("direction", "left");
                //Update the target position
                newPos.add(new Vector2Int(-1, 0));
                break;
            case RIGHT:
                obj.setProperty("direction", "right");
                newPos.add(new Vector2Int(1, 0));
                break;
            case UP:
                obj.setProperty("direction", "up");
                newPos.add(new Vector2Int(0, -1));
                break;
            case DOWN:
                obj.setProperty("direction", "down");
                newPos.add(new Vector2Int(0, 1));
                break;
        }
        //Set the position of the object
        env.setPosition(oid, newPos);
        return newPos;
    }

    //Update the highlight of an object
    public static ISpaceObject updateHighlight(ISpaceObject obj, Color color) {
        if (obj.getProperty("highlight").equals(new Color(149, 255, 83, 85))) return obj;
        obj.setProperty("highlight", color);
        return obj;
    }

    @OnStart
    public void body() {
        System.out.println("Agent Created: ScoutAgent");
        bdiFeature.dispatchTopLevelGoal(new LocateTree()).get();
    }
}









