package au.rdellios.bdiagriculture;


import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.runtime.IPlan;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;

@Plan
public class AreaScoutPlan {
    @PlanCapability
    protected ScoutAgent scoutAgent;
    @PlanAPI
    protected IPlan rplan;

    private int getBoundaryWidth(IVector2 start, IVector2 end) {
        return (Math.abs(start.getXAsInteger() - end.getXAsInteger()) + 1);
    }

    private int getBoundaryHeight(IVector2 start, IVector2 end) {
        return (Math.abs(start.getYAsInteger() - end.getYAsInteger()) + 1);
    }

    //This plan will be triggered when the scout agent has a boundary to scout
    @PlanBody
    public void body(IVector2[] boundary) {
        System.out.println("Starting AreaScoutPlan...");

        //Get the start and end positions of the boundary
        IVector2 start = boundary[0];
        IVector2 end = boundary[1];

        //Get the width and height of the boundary
        int areaHeight = getBoundaryHeight(start, end);
        int areaWidth = getBoundaryWidth(start, end);
        //Get the vision range of the scout
        int visionRange = (int) scoutAgent.getMyself().getProperty("vision_range");


        IVector2 startPos;
        IVector2 endPos;
        IVector2 targetPos;
        //Check if the area is taller than it is wide
        if (areaHeight < areaWidth) {
            //Get the number of passes required to cover the area
            int numPasses = (int) Math.ceil((double) areaHeight / (double) visionRange);
            //Loop through the passes
            for (int i = 0; i < numPasses; i++) {
                //Get the start and end positions for the current pass
                startPos = new Vector2Int(start.getXAsInteger(), i * visionRange + 1 + start.getYAsInteger());
                endPos = new Vector2Int(end.getXAsInteger(), startPos.getYAsInteger());

                //If the scout is on the last pass, check if the end position is outside the boundary
                if (i==numPasses - 1) {
                    if (endPos.getYAsInteger() > end.getYAsInteger()) {
                        endPos = new Vector2Int(endPos.getXAsInteger(), end.getYAsInteger());
                        startPos = new Vector2Int(startPos.getXAsInteger(), end.getYAsInteger());
                    }
                }

                //Check which position is closer to the scout
                if (!scoutAgent.isClosestObj(startPos, endPos, this.scoutAgent.getPosition())) {
                    IVector2 temp = startPos;
                    startPos = endPos;
                    endPos = temp;
                }

                //Set the target position to the start position
                targetPos = startPos;
                //Loop until the scout has reached the end position
                do {
                    rplan.waitFor(250).get();

                    //If the scout has reached the target position (startPos), update the target position
                    if (this.scoutAgent.getPosition().equals(startPos)) targetPos = endPos;

                    //Find which direction the scout should move in
                    MoveDir dir = this.scoutAgent.whichDirection(this.scoutAgent.env, this.scoutAgent.getPosition(), targetPos);
                    //Move the scout
                    if (dir != null) {
                        this.scoutAgent.Move(this.scoutAgent.getEnvironment(), this.scoutAgent.getMyself(), dir);
                    }
                } while (!this.scoutAgent.getPosition().equals(endPos));
            }
        } else {
            int numPasses = (int) Math.ceil((double) areaWidth / (double) visionRange);

            for (int i = 0; i < numPasses; i++) {
                startPos = new Vector2Int(i * visionRange + 1 + start.getXAsInteger(), start.getYAsInteger());
                endPos = new Vector2Int(startPos.getXAsInteger(), end.getYAsInteger());

                if (i==numPasses - 1) {
                    if (endPos.getXAsInteger() > end.getXAsInteger()) {
                        endPos = new Vector2Int(end.getXAsInteger(), endPos.getYAsInteger());
                        startPos = new Vector2Int(end.getXAsInteger(), startPos.getYAsInteger());
                    }
                }

                if (!scoutAgent.isClosestObj(startPos, endPos, this.scoutAgent.getPosition())) {
                    IVector2 temp = startPos;
                    startPos = endPos;
                    endPos = temp;
                }

                targetPos = startPos;
                do {
                    rplan.waitFor(250).get();

                    if (this.scoutAgent.getPosition().equals(startPos)) targetPos = endPos;

                    MoveDir dir = this.scoutAgent.whichDirection(this.scoutAgent.env, this.scoutAgent.getPosition(), targetPos);
                    if (dir != null) {
                        this.scoutAgent.Move(this.scoutAgent.getEnvironment(), this.scoutAgent.getMyself(), dir);
                    }
                } while (!this.scoutAgent.getPosition().equals(endPos));
            }
        }

        System.out.println("Finished plan - AreaScoutPlan...");
    }
}