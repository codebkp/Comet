package com.cometproject.server.storage.queries.player;

import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.players.PlayerManager;
import com.cometproject.server.game.players.data.PlayerData;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.game.players.types.PlayerSettings;
import com.cometproject.server.game.players.types.PlayerStatistics;
import com.cometproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class PlayerDao {
    public static Player getPlayer(String ssoTicket) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT p.id as playerId, p.username AS playerData_username, p.figure AS playerData_figure, p.motto AS playerData_motto, p.credits AS playerData_credits, p.vip_points AS playerData_vipPoints, p.rank AS playerData_rank, p.vip AS playerData_vip, p.gender AS playerData_gender, p.last_online AS playerData_lastOnline, p.reg_timestamp AS playerData_regTimestamp, p.reg_date AS playerData_regDate, p.favourite_group AS playerData_favouriteGroup, p.achievement_points AS playerData_achievementPoints, p.email AS playerData_email, p.activity_points AS playerData_activityPoints,\n" +
                    "  pSettings.volume AS playerSettings_volume, pSettings.home_room AS playerSettings_homeRoom, pSettings.hide_online AS playerSettings_hideOnline, pSettings.hide_inroom AS playerSettings_hideInRoom, pSettings.ignore_invites AS playerSettings_ignoreInvites, \n" +
                    "   pSettings.allow_friend_requests AS playerSettings_allowFriendRequests, pSettings.allow_trade AS playerSettings_allowTrade, pSettings.wardrobe AS playerSettings_wardrobe, pSettings.playlist AS playerSettings_playlist, pSettings.chat_oldstyle AS playerSettings_useOldChat,\n" +
                    "  pStats.achievement_score AS playerStats_achievementPoints, pStats.daily_respects AS playerStats_dailyRespects, pStats.total_respect_points AS playerStats_totalRespectPoints, pStats.help_tickets AS playerStats_helpTickets, pStats.help_tickets_abusive AS playerStats_helpTicketsAbusive, pStats.cautions AS playerStats_cautions, pStats.bans AS playerStats_bans\n" +
                    "FROM players p\n" +
                    " JOIN player_settings pSettings ON pSettings.player_id = p.id \n" +
                    " JOIN player_stats pStats ON pStats.player_id = p.id\n" +
                    "\n" +
                    "WHERE p.auth_ticket = ?", sqlConnection);
            preparedStatement.setString(1, ssoTicket);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return new Player(resultSet, false);
            }

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return null;
    }

    public static Player getPlayerFallback(String ssoTicket) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT p.id as playerId, p.username AS playerData_username, p.figure AS playerData_figure, p.motto AS playerData_motto, p.credits AS playerData_credits, p.vip_points AS playerData_vipPoints, p.rank AS playerData_rank, p.vip AS playerData_vip, p.gender AS playerData_gender, p.last_online AS playerData_lastOnline, p.reg_timestamp AS playerData_regTimestamp, p.reg_date AS playerData_regDate, p.favourite_group AS playerData_favouriteGroup, p.achievement_points AS playerData_achievementPoints, p.email AS playerData_email, p.activity_points AS playerData_activityPoints\n" +
                    "FROM players p\n" +
                    "WHERE p.auth_ticket = ?", sqlConnection);
            preparedStatement.setString(1, ssoTicket);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return new Player(resultSet, true);
            }

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return null;
    }

    public static PlayerData getDataById(int id) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT id, username, motto, figure, gender, email, rank, credits, vip_points, activity_points, reg_date, last_online, vip, achievement_points, reg_timestamp, favourite_group, last_ip FROM players WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, id);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return new PlayerData(resultSet.getInt("id"), resultSet.getString("username"), resultSet.getString("motto"), resultSet.getString("figure"), resultSet.getString("gender"),
                        resultSet.getString("email") == null ? "" : resultSet.getString("email"), resultSet.getInt("rank"), resultSet.getInt("credits"), resultSet.getInt("vip_points"),
                        resultSet.getInt("activity_points"), resultSet.getString("reg_date"), resultSet.getInt("last_online"), resultSet.getString("vip").equals("1"),
                        resultSet.getInt("achievement_points"), resultSet.getInt("reg_timestamp"), resultSet.getInt("favourite_group"), resultSet.getString("last_ip"));
            }

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return null;
    }

    public static PlayerSettings getSettingsById(int id) {
        // TODO: Cache, cache, cache!

        Connection sqlConnection = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM player_settings WHERE player_id = ? LIMIT 1;", sqlConnection);
            preparedStatement.setInt(1, id);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new PlayerSettings(resultSet, false);
            } else {
                // close old statement
                SqlHelper.closeSilently(preparedStatement);

                preparedStatement = SqlHelper.prepare("INSERT into player_settings (`player_id`) VALUES(?)", sqlConnection);
                preparedStatement.setInt(1, id);

                SqlHelper.executeStatementSilently(preparedStatement, false);

                return new PlayerSettings();
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return new PlayerSettings();
    }

    public static PlayerStatistics getStatisticsById(int id) {
        Connection sqlConnection = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM player_stats WHERE player_id = ? LIMIT 1;", sqlConnection);
            preparedStatement.setInt(1, id);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new PlayerStatistics(resultSet, false);
            } else {
                SqlHelper.closeSilently(preparedStatement);

                preparedStatement = SqlHelper.prepare("INSERT into player_stats (`player_id`) VALUES(?)", sqlConnection);
                preparedStatement.setInt(1, id);

                SqlHelper.executeStatementSilently(preparedStatement, false);

                return new PlayerStatistics(id);
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return new PlayerStatistics(id);
    }

    public static void updatePlayerStatus(Player player, boolean online, boolean setLastOnline) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE players SET online = ?" + (setLastOnline ? ", last_online = ?, last_ip = ?" : "") + " WHERE id = ?", sqlConnection);
            preparedStatement.setString(1, online ? "1" : "0");

            if (setLastOnline) {
                preparedStatement.setLong(2, Comet.getTime());
                preparedStatement.setString(3, player.getData().getIpAddress());
                preparedStatement.setInt(4, player.getId());
            } else {
                preparedStatement.setInt(2, player.getId());
            }

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static String getUsernameByPlayerId(int playerId) {
        // TODO: Cache, cache cache!

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT `username` FROM players WHERE `id` = ?", sqlConnection);
            preparedStatement.setInt(1, playerId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return resultSet.getString("username");
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return null;
    }

    public static int getIdByUsername(String username) {
        if (PlayerManager.getInstance().getPlayerIdByUsername(username) != -1)
            return PlayerManager.getInstance().getPlayerIdByUsername(username);

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT `id` FROM players WHERE `username` = ?", sqlConnection);
            preparedStatement.setString(1, username);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return 0;
    }

    public static String getIpAddress(int playerId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT `last_ip` FROM players WHERE `id` = ?", sqlConnection);
            preparedStatement.setInt(1, playerId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return resultSet.getString("last_ip");
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return "";
    }

    public static void updatePlayerData(int id, String username, String motto, String figure, int credits, int points, String gender, int favouriteGroup, int activtyPoints) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE players SET username = ?, motto = ?, figure = ?, credits = ?, vip_points = ?, gender = ?, favourite_group = ?, activity_points = ? WHERE id = ?", sqlConnection);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, motto);
            preparedStatement.setString(3, figure);
            preparedStatement.setInt(4, credits);
            preparedStatement.setInt(5, points);
            preparedStatement.setString(6, gender);
            preparedStatement.setInt(7, favouriteGroup);
            preparedStatement.setInt(8, activtyPoints);
            preparedStatement.setInt(9, id);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static boolean updatePlayerStatistics(int achievementPoints, int respectPoints, int dailyRespects, int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE player_stats SET achievement_score = ?, total_respect_points = ?, daily_respects = ? WHERE player_id = ?", sqlConnection);
            preparedStatement.setInt(1, achievementPoints);
            preparedStatement.setInt(2, respectPoints);
            preparedStatement.setInt(3, dailyRespects);
            preparedStatement.setInt(4, userId);

            return preparedStatement.execute();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return false;
    }

    public static void updateHomeRoom(int homeRoom, int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE player_settings SET home_room = ? WHERE player_id = ?", sqlConnection);
            preparedStatement.setInt(1, homeRoom);
            preparedStatement.setInt(2, userId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void saveWardrobe(String wardrobeData, int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE player_settings SET wardrobe = ? WHERE player_id = ?", sqlConnection);
            preparedStatement.setString(1, wardrobeData);
            preparedStatement.setInt(2, userId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void saveVolume(String volumeData, int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE player_settings SET volume = ? WHERE player_id = ?", sqlConnection);
            preparedStatement.setString(1, volumeData);
            preparedStatement.setInt(2, userId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void saveChatStyle(boolean useOldChat, int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE player_settings SET chat_oldstyle = ? WHERE player_id = ?", sqlConnection);
            preparedStatement.setString(1, useOldChat ? "1" : "0");
            preparedStatement.setInt(2, userId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void saveIgnoreInvitations(boolean ignoreInvitations, int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE player_settings SET ignore_invites = ? WHERE player_id = ?", sqlConnection);
            preparedStatement.setString(1, ignoreInvitations ? "1" : "0");
            preparedStatement.setInt(2, userId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }


    public static boolean usernameIsAvailable(String username) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT `id` FROM players WHERE username = ? LIMIT 1", sqlConnection);
            preparedStatement.setString(1, username);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return true;
            }

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return false;
    }

    public static void resetHomeRoom(int roomId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE player_settings SET home_room = 0 WHERE player_id = ?", sqlConnection);
            preparedStatement.setInt(1, roomId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }
}
