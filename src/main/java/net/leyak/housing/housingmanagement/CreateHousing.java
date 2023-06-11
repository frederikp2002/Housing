package net.leyak.housing.housingmanagement;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import net.leyak.housing.Housing;

public class CreateHousing {

    private final MVWorldManager worldManager;

    public CreateHousing(Housing plugin) {
        this.worldManager = plugin.getCore().getMVWorldManager();
    }

    public void createHousing(String name) {
        worldManager.cloneWorld("housing", name);

    }

}
