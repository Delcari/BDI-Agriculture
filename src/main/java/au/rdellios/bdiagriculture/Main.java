package au.rdellios.bdiagriculture;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;

public class Main {
    public static void main(String[] args) {
        IPlatformConfiguration platformConfiguration;
        platformConfiguration = PlatformConfigurationHandler.getDefault();
        //platformConfiguration.getGui();
        IExternalAccess platform = Starter.createPlatform(platformConfiguration).get();
        CreationInfo ci = new CreationInfo().setFilename("src/main/java/au/rdellios/bdiagriculture/bdiAgriculture.application.xml");
        platform.createComponent(ci).get();
    }
}