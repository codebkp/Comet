package com.cometproject.server.game.commands.user.group;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.groups.GroupManager;
import com.cometproject.server.game.groups.types.Group;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.game.rooms.objects.items.RoomItem;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.RoomItemWall;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.rooms.RoomItemDao;
import com.google.common.collect.Lists;

import java.util.List;

public class EjectAllCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        Room room = client.getPlayer().getEntity().getRoom();
        Group group = room.getGroup();

        if (group.getData().getOwnerId() != client.getPlayer().getId()) {
            final List<RoomItem> itemsToRemove = Lists.newArrayList();

            for(RoomItemFloor roomItemFloor : client.getPlayer().getEntity().getRoom().getItems().getFloorItems().values()) {
                if(roomItemFloor.getOwner() == client.getPlayer().getId()) {
                    itemsToRemove.add(roomItemFloor);
                }
            }

            for(RoomItemWall roomItemWall : client.getPlayer().getEntity().getRoom().getItems().getWallItems().values()) {
                if(roomItemWall.getOwner() == client.getPlayer().getId()) {
                    itemsToRemove.add(roomItemWall);
                }
            }

            for(RoomItem item : itemsToRemove) {
                if(item instanceof RoomItemFloor) {
                    client.getPlayer().getEntity().getRoom().getItems().removeItem((RoomItemFloor) item, client);
                } else {
                    client.getPlayer().getEntity().getRoom().getItems().removeItem(((RoomItemWall) item), client, true);
                }
            }
        } else {
            for (Integer groupMemberId : group.getMembershipComponent().getMembers().keySet()) {
                Session groupMemberSession = NetworkManager.getInstance().getSessions().getByPlayerId(groupMemberId);

                List<RoomItem> floorItemsOwnedByPlayer = Lists.newArrayList();

                for (RoomItemFloor floorItem : room.getItems().getFloorItems().values()) {

                    if (floorItem.getOwner() == groupMemberId) {
                        floorItemsOwnedByPlayer.add(floorItem);
                    }
                }

                for (RoomItemWall wallItem : room.getItems().getWallItems().values()) {
                    if (wallItem.getOwner() == groupMemberId) {
                        floorItemsOwnedByPlayer.add(wallItem);
                    }
                }

                if (groupMemberSession != null && groupMemberSession.getPlayer() != null) {
                    groupMemberSession.getPlayer().getGroups().remove(new Integer(group.getId()));

                    if (groupMemberSession.getPlayer().getData().getFavouriteGroup() == group.getId()) {
                        groupMemberSession.getPlayer().getData().setFavouriteGroup(0);
                    }

                    for (RoomItem roomItem : floorItemsOwnedByPlayer) {
                        if (roomItem instanceof RoomItemFloor)
                            room.getItems().removeItem(((RoomItemFloor) roomItem), groupMemberSession);
                        else if (roomItem instanceof RoomItemWall)
                            room.getItems().removeItem(((RoomItemWall) roomItem), groupMemberSession, true);
                    }
                } else {
                    for (RoomItem roomItem : floorItemsOwnedByPlayer) {
                        RoomItemDao.removeItemFromRoom(roomItem.getId(), groupMemberId);
                    }
                }

                floorItemsOwnedByPlayer.clear();
            }
        }
    }

    @Override
    public String getPermission() {
        return "ejectall_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.ejectall.description", "Removes all items you own in the room");
    }
}