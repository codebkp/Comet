package com.cometproject.server.network.messages.outgoing.room.items.lovelock;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.network.messages.composers.MessageComposer;
import com.cometproject.server.network.messages.headers.Composers;

public class LoveLockWidgetMessageComposer extends MessageComposer {
    private final int itemId;

    public LoveLockWidgetMessageComposer(final int itemId) {
        this.itemId = itemId;
    }

    @Override
    public short getId() {
        return Composers.LoveLockWidgetMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.itemId);
        msg.writeBoolean(true);
    }
}
