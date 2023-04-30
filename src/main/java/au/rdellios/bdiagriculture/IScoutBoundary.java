package au.rdellios.bdiagriculture;

import jadex.commons.future.IFuture;
import jadex.extension.envsupport.math.IVector2;

public interface IScoutBoundary {
    public IFuture<String> testGoal(IVector2[] boundary);
}