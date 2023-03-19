package au.rdellios.bdiagriculture;


import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.*;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.service.annotation.OnStart;
import jadex.extension.envsupport.environment.ISpaceObject;
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

    @OnStart
    public void body() {
        System.out.println("Agent Created: ScoutAgent");
        bdiFeature.dispatchTopLevelGoal(new LocateTree()).get();
    }
}









