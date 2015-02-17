package com.cometproject.server.network.messages.incoming.handshake;

import com.cometproject.server.game.CometManager;
import com.cometproject.server.network.messages.incoming.IEvent;
import com.cometproject.server.network.messages.types.Event;
import com.cometproject.server.network.sessions.Session;


public class CheckReleaseMessageEvent implements IEvent {
    @Override
    public void handle(Session client, Event msg) {
        if(Session.CLIENT_VERSION == null) {
            Session.CLIENT_VERSION = msg.readString();
        }

        CometManager.getLogger().debug("Client running on release: " + Session.CLIENT_VERSION);
    }
}
