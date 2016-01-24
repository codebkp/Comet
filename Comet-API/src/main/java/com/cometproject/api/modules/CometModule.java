package com.cometproject.api.modules;

import com.cometproject.api.config.ModuleConfig;
import com.cometproject.api.events.EventListenerContainer;
import com.cometproject.api.events.modules.OnModuleLoadEvent;
import com.cometproject.api.events.modules.OnModuleUnloadEvent;
import com.cometproject.api.server.IGameService;

import java.util.UUID;

public abstract class CometModule implements EventListenerContainer {

    /**
     * Module configuration
     */
    private final ModuleConfig config;

    /**
     * Assign a random UUD to the module at runtime, so the system can tell it apart from other modules.
     */
    private final UUID moduleId;

    /**
     * The bridge between Comet modules & the main server is the GameService, an object which allows you to
     * attach listeners to specific events which are fired within the server and also allows you to access
     * the game server's main components.
     */
    private final IGameService gameService;

    public CometModule(ModuleConfig config, IGameService gameService) {
        this.moduleId = UUID.randomUUID();
        this.gameService = gameService;
        this.config = config;

        this.gameService.getEventHandler().addListenerContainer(this);
    }

    /**
     * Load all the module resources and then fire the "onModuleLoad" event.
     */
    public void loadModule() {
        this.gameService.getEventHandler().handleEvent(new OnModuleLoadEvent());
    }

    /**
     * Unload all module resources and then fire the "onModuleUnload" event.
     */
    public void unloadModule() {
        this.gameService.getEventHandler().handleEvent(new OnModuleUnloadEvent());
    }

    /**
     * The random Module ID
     * @return The random Module ID
     */
    public UUID getModuleId() {
        return moduleId;
    }

    /**
     * Get the main game service
     * @return Main game service
     */
    public IGameService getGameService() {
        return this.gameService;
    }

    /**
     * Get the module configuration
     * @return Module configuration
     */
    public ModuleConfig getConfig() {
        return config;
    }
}
