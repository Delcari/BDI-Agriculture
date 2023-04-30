package au.rdellios.bdiagriculture;

import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.ISpaceObject;

public interface IInformTree {
    public IFuture<Boolean> informTree(ISpaceObject obj);
}