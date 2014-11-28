package com.cometproject.server.network.messages.incoming.room.item.gifts;

import com.cometproject.server.game.CometManager;
import com.cometproject.server.game.catalog.types.CatalogItem;
import com.cometproject.server.game.catalog.types.CatalogPage;
import com.cometproject.server.game.catalog.types.gifts.GiftData;
import com.cometproject.server.game.items.types.ItemDefinition;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.floor.GiftFloorItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.incoming.IEvent;
import com.cometproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.cometproject.server.network.messages.outgoing.room.gifts.OpenGiftMessageComposer;
import com.cometproject.server.network.messages.types.Event;
import com.cometproject.server.network.sessions.Session;

public class OpenGiftMessageEvent implements IEvent {
    @Override
    public void handle(Session client, Event msg) throws Exception {
        final int floorItemId = msg.readInt();

        if(client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null) return;

        Room room = client.getPlayer().getEntity().getRoom();
        RoomItemFloor floorItem = room.getItems().getFloorItem(floorItemId);

        if(floorItem == null || !(floorItem instanceof GiftFloorItem)) return;

        final GiftData giftData = ((GiftFloorItem) floorItem).getGiftData();

        final CatalogPage catalogPage = CometManager.getCatalog().getPage(giftData.getPageId());
        if(catalogPage == null) return;

        final CatalogItem catalogItem = catalogPage.getItems().get(giftData.getItemId());
        if(catalogItem == null) return;

        floorItem.onInteract(client.getPlayer().getEntity(), 0, false);
        client.send(OpenGiftMessageComposer.compose(floorItemId, floorItem.getDefinition().getType(), ((GiftFloorItem) floorItem).getGiftData(), CometManager.getItems().getDefinition(Integer.parseInt(catalogItem.getItemId()))));
    }
}
