package mocchi2002.moep;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class PlayerListNameSwitchTask implements Runnable {

    private final JavaPlugin plugin;
    private final PlayerAchieveRepository playerAchieveRepository;

    public PlayerListNameSwitchTask(JavaPlugin plugin, PlayerAchieveRepository playerAchieveRepository) {
        this.plugin = plugin;
        this.playerAchieveRepository = playerAchieveRepository;
    }

    @Override
    public void run() {
        HashMap<Player, String> playersToOriginalPlayerListNames = new HashMap<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            playersToOriginalPlayerListNames.put(player, player.getPlayerListName());

            String playerAchieve = playerAchieveRepository.achieve(player.getUniqueId());


            if (playerAchieve != null) {
                player.setPlayerListName(playerAchieve);
            }
        }


        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                String originalPlayerListname = playersToOriginalPlayerListNames.get(player);
                if(originalPlayerListname != null) {
                    player.setPlayerListName(originalPlayerListname);
                }
            }
        }, 40L);
    }
}
