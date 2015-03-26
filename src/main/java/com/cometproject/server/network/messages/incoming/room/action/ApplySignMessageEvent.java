package com.cometproject.server.network.messages.incoming.room.action;

import com.cometproject.server.game.rooms.objects.entities.RoomEntityStatus;
import com.cometproject.server.network.messages.incoming.IEvent;
import com.cometproject.server.network.messages.types.Event;
import com.cometproject.server.network.sessions.Session;


public class ApplySignMessageEvent implements IEvent {
    public void handle(Session client, Event msg) {
        if(client.getPlayer() == null || client.getPlayer().getEntity() == null) return;

        // / UnIdle the entity
        client.getPlayer().getEntity().unIdle();

        // Add the sign status
        client.getPlayer().getEntity().addStatus(RoomEntityStatus.SIGN, String.valueOf(msg.readInt()));

        // Set the entity to displaying a sign
        client.getPlayer().getEntity().markDisplayingSign();
        client.getPlayer().getEntity().markNeedsUpdate();
    }
}
