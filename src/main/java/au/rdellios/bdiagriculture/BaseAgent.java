package au.rdellios.bdiagriculture;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jadex.application.EnvironmentService;
import jadex.bdiv3.BDIAgentFactory;
import jadex.bridge.IInternalAccess;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.micro.annotation.Agent;


@Agent(type = BDIAgentFactory.TYPE)
public abstract class BaseAgent {
    // Annotation to inform FindBugs that the uninitialized field is not a bug.
    @SuppressFBWarnings(value = "UR_UNINIT_READ", justification = "Agent field injected by interpreter")

    @Agent
    protected IInternalAccess agent;

    // The Environment
    protected Grid2D env = (Grid2D) EnvironmentService.getSpace(agent, "mygc2dspace").get();

    // The Object - Myself
    protected ISpaceObject myself = env.getAvatar(agent.getDescription(), agent.getModel().getFullName());

    //Get the environment
    public Grid2D getEnvironment() {
        return env;
    }

    //Get the position of Myself.
    public IVector2 getPosition() {
        return (IVector2) myself.getProperty(Space2D.PROPERTY_POSITION);
    }

    //Get myself
    public ISpaceObject getMyself() {
        return myself;
    }

    //Get the agent
    public IInternalAccess getAgent() {
        return agent;
    }
}
