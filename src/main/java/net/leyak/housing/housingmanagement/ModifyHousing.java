package net.leyak.housing.housingmanagement;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import net.leyak.housing.Housing;

public class ModifyHousing {

    private final MVWorldManager worldManager;

    public ModifyHousing(Housing plugin) {
        this.worldManager = plugin.getCore().getMVWorldManager();
    }

    public void modifyHousing(String world) {
        worldManager.getMVWorld(world).setStyle("NORMAL");
        worldManager.getMVWorld(world).setColor("WHITE");
    }

}
