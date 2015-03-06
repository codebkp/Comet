package com.cometproject.server.network.messages.outgoing.room.engine;

import com.cometproject.server.network.messages.composers.MessageComposer;
import com.cometproject.server.network.messages.headers.Composers;
import com.cometproject.server.network.messages.types.Composer;


public class PapersMessageComposer extends MessageComposer {
    private final String key;
    private final String value;

    public PapersMessageComposer(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public short getId() {
        return Composers.RoomSpacesMessageComposer;
    }

    @Override
    public void compose(Composer msg) {
        msg.writeString(this.key);
        msg.writeString(this.value);
    }
}
