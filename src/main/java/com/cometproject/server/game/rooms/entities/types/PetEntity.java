package com.cometproject.server.game.rooms.entities.types;

import com.cometproject.server.game.pets.data.PetData;
import com.cometproject.server.game.rooms.avatars.misc.Position3D;
import com.cometproject.server.game.rooms.entities.GenericEntity;
import com.cometproject.server.game.rooms.entities.types.ai.BotAI;
import com.cometproject.server.game.rooms.entities.types.ai.DefaultAI;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.types.Composer;

public class PetEntity extends GenericEntity {
    private PetData data;
    private BotAI ai;
    private int cycleCount = 0;

    public PetEntity(PetData data, int identifier, Position3D startPosition, int startBodyRotation, int startHeadRotation, Room roomInstance) {
        super(identifier, startPosition, startBodyRotation, startHeadRotation, roomInstance);

        this.data = data;
        this.ai = new DefaultAI(this);
    }

    @Override
    public void joinRoom(Room room, String password) {

    }

    @Override
    protected void finalizeJoinRoom() {

    }

    @Override
    public void leaveRoom(boolean isOffline, boolean isKick, boolean toHotelView) {

    }

    @Override
    protected void finalizeLeaveRoom() {

    }

    @Override
    public boolean onChat(String message) {
        return false;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public String getMotto() {
        return null;
    }

    @Override
    public String getFigure() {
        return null;
    }

    @Override
    public String getGender() {
        return null;
    }

    @Override
    public void compose(Composer msg) {
        msg.writeInt(this.data.getId());
        msg.writeString(this.data.getName());
        msg.writeString("PET_MOTTO");
        msg.writeString(data.getLook() + " 2 2 " + data.getHair() + " " + data.getHairDye() + " 3 " + data.getHair() + " " + data.getHairDye());
        msg.writeInt(this.getVirtualId());

        msg.writeInt(this.getPosition().getX());
        msg.writeInt(this.getPosition().getY());
        msg.writeDouble(this.getPosition().getZ());

        msg.writeInt(0); // 2 = user 4 = bot 0 = pet ??????
        msg.writeInt(2); // 1 = user 2 = pet 3 = bot ??????

        msg.writeInt(this.data.getRaceId());
        msg.writeInt(this.data.getOwnerId());
        msg.writeString("Leon"); // TODO: this :P
        msg.writeInt(1);
        msg.writeBoolean(false); // has saddle
        msg.writeBoolean(false); // shit knows nigga

        msg.writeInt(0);
        msg.writeInt(0);
        msg.writeString("");
    }

    public PetData getData() {
        return data;
    }

    public BotAI getAi() {
        return ai;
    }


    public int getCycleCount() {
        return this.cycleCount;
    }

    public void decrementCycleCount() {
        cycleCount--;
    }

    public void incrementCycleCount() {
        cycleCount++;
    }

    public void resetCycleCount() {
        this.cycleCount = 0;
    }

}
