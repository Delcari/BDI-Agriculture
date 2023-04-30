package au.rdellios.bdiagriculture;

import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.extension.envsupport.math.IVector2;

@Goal
public class ScoutBoundary {
    @GoalParameter
    IVector2[] boundary;

    public ScoutBoundary(IVector2[] boundary) {
        this.boundary = boundary;
    }
}