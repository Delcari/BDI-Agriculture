package au.rdellios.bdiagriculture;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jadex.application.EnvironmentService;
import jadex.bdiv3.BDIAgentFactory;
import jadex.bridge.IInternalAccess;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;
import jadex.micro.annotation.Agent;


@Agent(type = BDIAgentFactory.TYPE)
public abstract class BaseAgent {
    // Annotation to inform FindBugs that the uninitialized field is not a bug.
    @SuppressFBWarnings(value = "UR_UNINIT_READ", justification = "Agent field injected by interpreter")

    @Agent
    protected IInternalAccess agent;

    //move steps
    protected int moveSteps = 0;

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

    public IVector2 Move(Grid2D env, ISpaceObject obj, Direction moveDir) {
        Object oid = obj.getId();
        IVector2 newPos = (IVector2) obj.getProperty(Space2D.PROPERTY_POSITION);
        //Which direction is the agent going to move in?
        moveSteps++;
        switch (moveDir) {
            case WEST:
                //Update the direction the object is facing
                obj.setProperty("direction", moveDir.toString());
                //Update the target position
                if (newPos.getXAsInteger() > 0)
                    newPos.add(new Vector2Int(-1, 0));
                break;
            case EAST:
                obj.setProperty("direction", moveDir.toString());
                if (newPos.getXAsInteger() < this.env.getAreaSize().getXAsInteger() - 1)
                    newPos.add(new Vector2Int(1, 0));
                break;
            case NORTH:
                obj.setProperty("direction", moveDir.toString());
                if (newPos.getYAsInteger() > 0)
                    newPos.add(new Vector2Int(0, -1));
                break;
            case SOUTH:
                obj.setProperty("direction", moveDir.toString());
                if (newPos.getYAsInteger() < this.env.getAreaSize().getYAsInteger() - 1)
                    newPos.add(new Vector2Int(0, 1));
                break;
        }
        return newPos;
    }

    //Check if the object is within the vision range of the agent
    public boolean inVision(ISpaceObject obj) {
        int visionRange = (int) this.getMyself().getProperty("vision_range");
        Direction direction = Direction.valueOf((String) this.getMyself().getProperty("direction"));
        IVector2 objPosition = (IVector2) obj.getProperty(Space2D.PROPERTY_POSITION);

        //Get the position difference between the agent and the object
        int xDiff = objPosition.getXAsInteger() - this.getPosition().getXAsInteger();
        int yDiff = objPosition.getYAsInteger() - this.getPosition().getYAsInteger();

        switch (direction) {
            case WEST:
                if ((xDiff >= -visionRange && xDiff < 0) && (yDiff <= Math.floor(visionRange / 2) && yDiff >= -Math.floor(visionRange / 2)))
                    return true;
                break;
            case EAST:
                if ((xDiff > 0 && xDiff <= visionRange) && (yDiff <= Math.floor(visionRange / 2) && yDiff >= -Math.floor(visionRange / 2)))
                    return true;
                break;
            case NORTH:
                if ((yDiff >= -visionRange && yDiff < 0) && (xDiff <= Math.floor(visionRange / 2) && xDiff >= -Math.floor(visionRange / 2)))
                    return true;
                break;
            case SOUTH:
                if ((yDiff > 0 && yDiff <= visionRange) && (xDiff <= Math.floor(visionRange / 2) && xDiff >= -Math.floor(visionRange / 2)))
                    return true;
                break;
        }
        return false;
    }

    //Which direction should the Agent move in to get closer to the target position?
    public Direction whichDirection(Grid2D env, IVector2 currentPos, IVector2 targetPos) {
        int closestDir = (env.getShortestDirection(currentPos.getX(), targetPos.getX(), true)).getAsInteger();
        //Which direction is the closest?
        if (closestDir != 0) {
            if ((closestDir < 0)) {
                return Direction.WEST;
            } else {
                return Direction.EAST;
            }
        } else {
            closestDir = (env.getShortestDirection(currentPos.getY(), targetPos.getY(), false)).getAsInteger();
            if (closestDir != 0) {
                if ((closestDir < 0)) {
                    return Direction.NORTH;
                } else {
                    return Direction.SOUTH;
                }
            }
        }
        return null;
    }
}
