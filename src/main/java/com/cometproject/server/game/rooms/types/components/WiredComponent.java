package com.cometproject.server.game.rooms.types.components;

import com.cometproject.server.game.GameEngine;
import com.cometproject.server.game.rooms.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.items.FloorItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.wired.data.WiredDataFactory;
import com.cometproject.server.game.wired.data.WiredDataInstance;
import com.cometproject.server.game.wired.misc.WiredSquare;
import com.cometproject.server.game.wired.types.TriggerType;
import com.cometproject.server.network.messages.types.Event;
import javolution.util.FastList;

public class WiredComponent {
    private Room room;

    private FastList<WiredSquare> squares;

    public WiredComponent(Room room) {
        this.room = room;

        this.squares = new FastList<>();
    }

    public void dispose() {
        this.squares.clear();
        this.squares = null;
        this.room = null;
    }

    public boolean isWiredSquare(int x, int y) {
        for(WiredSquare square : this.squares) {
            if(square.getX() == x && square.getY() == y) {
                return true;
            }
        }

        return false;
    }

    public boolean trigger(TriggerType type, Object data, PlayerEntity user) {
        if(this.squares.size() == 0) {
            return false;
        }

        boolean wasTriggered = false;

        for(WiredSquare s : this.squares) {
            for(FloorItem item : this.getRoom().getItems().getItemsOnSquare(s.getX(), s.getY())) {
                if(GameEngine.getWired().isWiredTrigger(item)) {
                    if(item.getDefinition().getInteraction().equals(GameEngine.getWired().getString(type))) {
                        if(type == TriggerType.ON_SAY) {
                            if(!item.getExtraData().equals(data)) {
                                continue;
                            }
                        } else if(type == TriggerType.ON_FURNI) {
                            WiredDataInstance wiredData = WiredDataFactory.get(item);
                            int itemId = (int) data;

                            if(!wiredData.getItems().contains(itemId)) {
                                continue;
                            }
                        }

                        GameEngine.getWired().getTrigger(item.getDefinition().getInteraction()).onTrigger(data, user, s);
                        wasTriggered = true;
                    }
                }
            }
        }

        return wasTriggered;
    }

    public void handleSave(FloorItem item, Event msg) {
        if(item.getDefinition().getInteraction().startsWith("wf_trg_")) {
            GameEngine.getWired().getTrigger(item.getDefinition().getInteraction()).onSave(msg, item);

        } else if(item.getDefinition().getInteraction().startsWith("wf_act_")) {
            GameEngine.getWired().getEffect(item.getDefinition().getInteraction()).onSave(msg, item);
        }
    }

    public void add(int x, int y) {
        this.squares.add(new WiredSquare(x, y));
    }

    public Room getRoom() {
        return this.room;
    }
}
