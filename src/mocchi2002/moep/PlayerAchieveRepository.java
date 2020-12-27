package mocchi2002.moep;

import java.util.HashMap;
import java.util.UUID;

public class PlayerAchieveRepository {

    final HashMap<UUID, String> playerUniqueIdsToAchieves;

    public PlayerAchieveRepository(HashMap<UUID, String> playerUniqueIdsToAchieves) {
        this.playerUniqueIdsToAchieves = playerUniqueIdsToAchieves;
    }

    public String achieve(UUID playerUniqueId) {
        return playerUniqueIdsToAchieves.get(playerUniqueId);
    }

    public void setAchieve(UUID playerUniqueId, String achieve) {
        playerUniqueIdsToAchieves.put(playerUniqueId, achieve);
    }

    public void removeAchieve(UUID playerUniqueId) {
        playerUniqueIdsToAchieves.remove(playerUniqueId);
    }
}
