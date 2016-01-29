package com.cometproject.server.game.pets.commands;

import com.cometproject.server.game.pets.commands.types.FollowCommand;
import com.cometproject.server.game.pets.commands.types.FreeCommand;
import com.cometproject.server.game.pets.commands.types.HereCommand;
import com.cometproject.server.game.pets.commands.types.SitCommand;
import com.cometproject.server.game.rooms.objects.entities.types.PetEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;

import java.util.HashMap;
import java.util.Map;

public class PetCommandManager {
    private static PetCommandManager petCommandManager;

    private final Map<String, PetCommand> petCommands;

    public PetCommandManager() {
        petCommands = new HashMap<String, PetCommand>() {{
            //todo: locale this stuff
            put("sit", new SitCommand());
            put("free", new FreeCommand());
            put("here", new HereCommand());
            put("follow", new FollowCommand());
        }};
    }

    public boolean executeCommand(String commandKey, PlayerEntity executor, PetEntity petEntity) {
        if(!this.petCommands.containsKey(commandKey)) {
            return false;
        }

        PetCommand command = this.petCommands.get(commandKey);

        if(command.getRequiredLevel() > petEntity.getData().getLevel()) {
            // too low of a level!
            return false;
        }

        if(command.requiresOwner() && executor.getPlayerId() != petEntity.getData().getOwnerId())
            return false;

        return command.execute(executor, petEntity);
    }

    public static PetCommandManager getInstance() {
        if (petCommandManager == null)
            petCommandManager = new PetCommandManager();

        return petCommandManager;
    }
}
