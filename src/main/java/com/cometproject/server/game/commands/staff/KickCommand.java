package com.cometproject.server.game.commands.staff;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.entities.RoomEntityType;
import com.cometproject.server.game.rooms.entities.types.PlayerEntity;
import com.cometproject.server.network.sessions.Session;

public class KickCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 1) {
            return;
        }
        String username = params[0];

        PlayerEntity entity = (PlayerEntity) client.getPlayer().getEntity().getRoom().getEntities().getEntityByName(username, RoomEntityType.PLAYER); //Comet.getServer().getNetwork().getSessions().getByPlayerUsername(username);

        if (entity == null)
            return;

        if (entity.getUsername().equals(client.getPlayer().getData().getUsername())) {
            return;
        }

        if (entity.getPlayer().getPermissions().hasPermission("user_unkickable")) {

            sendChat(Locale.get("command.kick.unkickable"), client);
            return;
        }

        entity.leaveRoom(false, true, true);
    }

    @Override
    public String getPermission() {
        return "kick_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.kick.description");
    }
}
