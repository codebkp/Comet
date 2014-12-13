package com.cometproject.server.network.messages.incoming.room.action;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.game.rooms.filter.FilterResult;
import com.cometproject.server.network.messages.incoming.IEvent;
import com.cometproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.avatar.ShoutMessageComposer;
import com.cometproject.server.network.messages.types.Event;
import com.cometproject.server.network.sessions.Session;


public class ShoutMessageEvent implements IEvent {
    public void handle(Session client, Event msg) {
        String message = msg.readString();
        int colour = msg.readInt();

        if(message.length() < 1) return;

        if (!TalkMessageEvent.isValidColour(colour, client)) {
            colour = 0;
        }

        String filteredMessage = TalkMessageEvent.filterMessage(message);

        if (client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null)
            return;


        if (!client.getPlayer().getPermissions().hasPermission("bypass_filter")) {
            FilterResult filterResult = RoomManager.getInstance().getFilter().filter(message);

            if (filterResult.isBlocked()) {
                client.send(AdvancedAlertMessageComposer.compose(Locale.get("game.message.blocked").replace("%s", filterResult.getChatMessage())));
                return;
            } else if (filterResult.wasModified()) {
                filteredMessage = filterResult.getChatMessage();
            }
        }

        if (client.getPlayer().getEntity().onChat(filteredMessage)) {
            client.getPlayer().getEntity().getRoom().getEntities().broadcastChatMessage(ShoutMessageComposer.compose(client.getPlayer().getEntity().getId(), filteredMessage, RoomManager.getInstance().getEmotions().getEmotion(filteredMessage), colour), client.getPlayer().getEntity());
        }
    }
}
