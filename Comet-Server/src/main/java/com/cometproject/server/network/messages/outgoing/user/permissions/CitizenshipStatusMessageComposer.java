package com.cometproject.server.network.messages.outgoing.user.permissions;

import com.cometproject.api.networking.messages.IComposer;
import com.cometproject.server.network.messages.composers.MessageComposer;
import com.cometproject.server.network.messages.headers.Composers;


public class CitizenshipStatusMessageComposer extends MessageComposer {
    @Override
    public short getId() {
        return Composers.CitizenshipStatusMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeString("helper");
        msg.writeInt(4);
        msg.writeInt(4);

    }
}
