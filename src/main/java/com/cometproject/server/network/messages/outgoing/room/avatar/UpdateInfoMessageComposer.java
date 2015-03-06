package com.cometproject.server.network.messages.outgoing.room.avatar;

import com.cometproject.server.network.messages.composers.MessageComposer;
import com.cometproject.server.network.messages.headers.Composers;
import com.cometproject.server.network.messages.types.Composer;


public class UpdateInfoMessageComposer extends MessageComposer {

    private final int playerId;
    private final String figure;
    private final String gender;
    private final String motto;
    private final int achievementPoints;

    public UpdateInfoMessageComposer(final int playerId, final String figure, final String gender, final String motto, final int achievementPoints) {
        this.playerId = playerId;
        this.figure = figure;
        this.gender = gender;
        this.motto = motto;
        this.achievementPoints = achievementPoints;
    }

    @Override
    public short getId() {
        return Composers.UpdateUserDataMessageComposer;
    }

    @Override
    public void compose(Composer msg) {
        msg.writeInt(playerId);
        msg.writeString(figure);
        msg.writeString(gender.toLowerCase());
        msg.writeString(motto);
        msg.writeInt(achievementPoints);
    }


//    public static Composer compose(GenericEntity entity) {
//        return compose(entity.getId(), entity.getFigure(), entity.getGender(), entity.getMotto(), (entity instanceof PlayerEntity) ? ((PlayerEntity) entity).getPlayer().getData().getAchievementPoints() : 0);
//    }
//
//    public static Composer compose(boolean isMe, GenericEntity entity) {
//        if (!isMe) {
//            return compose(entity.getId(), entity.getFigure(), entity.getGender(), entity.getMotto(), (entity instanceof PlayerEntity) ? ((PlayerEntity) entity).getPlayer().getData().getAchievementPoints() : 0);
//        } else {
//            return compose(-1, entity.getFigure(), entity.getGender(), entity.getMotto(), (entity instanceof PlayerEntity) ? ((PlayerEntity) entity).getPlayer().getData().getAchievementPoints() : 0);
//        }
//    }
}
