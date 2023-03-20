package au.rdellios.bdiagriculture;


import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.*;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.service.annotation.OnStart;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentFeature;


@Agent(type = BDIAgentFactory.TYPE)
@Plans({@Plan(trigger = @Trigger(goals = ScoutAgent.LocateTree.class), body = @Body(FindTreesPlan.class)), @Plan(trigger = @Trigger(goals = ScoutAgent.MoveTo.class), body = @Body(MovePlan.class))})
public class ScoutAgent extends BaseAgent {
    @AgentFeature
    protected IBDIAgentFeature bdiFeature;

    //@Belief
    //protected List<ISpaceObject> trees = new ArrayList<ISpaceObject>();

    @Belief
    protected ISpaceObject targetObj;

    @Goal(excludemode = ExcludeMode.Never, recur = true, recurdelay = 500)
    public class LocateTree {
    }

    @Goal(excludemode = ExcludeMode.Never)// deliberation=@Deliberation(inhibits={LocateTree.class}))
    public class MoveTo {
        @GoalParameter
        protected ISpaceObject targetObj;
        @GoalParameter
        protected long overlayObjId;

        public ISpaceObject getTargetObj() {
            return targetObj;
        }

        public long getOverlayObjId() {
            return overlayObjId;
        }

        public MoveTo(ISpaceObject targetObj) {
            this.targetObj = targetObj;
        }

        public MoveTo(ISpaceObject targetObj, long overlayObjId) {
            this.targetObj = targetObj;
            this.overlayObjId = overlayObjId;
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

    @OnStart
    public void body() {
        System.out.println("Agent Created: ScoutAgent");
        bdiFeature.dispatchTopLevelGoal(new LocateTree()).get();
    }
}









