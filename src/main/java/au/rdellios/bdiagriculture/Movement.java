package au.rdellios.bdiagriculture;

import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;

public class Movement {
    public enum MoveDir {
        LEFT, RIGHT, UP, DOWN
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
}
