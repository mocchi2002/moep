package mocchi2002.moep;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main extends JavaPlugin {

    private static Main instance;
    private BukkitTask playerListNameSwitchTask;
    private PlayerAchieveRepository playerAchieveRepository;

    @Override
    public void onEnable() {
        System.out.println("moep plugin has been loaded!");

        instance = this;

        saveDefaultConfig();

        HashMap<UUID, String> playersUniqueIdsToAchieves = new HashMap<>();
        //プレイヤーのUUIDと称号と紐付けて保持するマップを作成(new)する

        for (String playerUniqueIdString : getConfig().getKeys(false)) {
            /*
                config.ymlの
                ------------------------
                player-uuid1: `achieve1`
                player-uuid2: `achieve2`
                player-uuid3: `achieve3`
                ------------------------
                player-uuid1, player-uuid2, player-uuid3　を Set<String>型の値で返すのが、
                getKeys(false) → false指定でなければならない
                ここではplayer-uuidNより深い階層は無いが、trueを指定すると
                最初の階層だけでなくそれより下の階層のキーまで取得してくる
                Set<String>とは何か
                Set=集合の意 Set<String> → Stringの集合
                特に、List<String> と違う点は、その中身に同じ値が二つとして存在しないこと
                List<String> list = ...; list.get(0) → "aaa" list.get(1) → "aaa"
                    → Set<String> ではこれはあり得ず、そのインスタンス内では値の一意性が保証されている
                つまり、ここではconfig.yml一階層目のキーの集まり(重複無し)を
                String playerUniqueIdString として一つひとつ処理していく
             */

            String achieve = getConfig().getString(playerUniqueIdString);
            //プレイヤーのUUIDに結び付けられた称号をコンフィグから文字列として取得する(getString(String))

            UUID playerUniqueId = UUID.fromString(playerUniqueIdString);
            //String(文字列型)だったplayerUniqueIdStringをUUID型にする
            //UUID.fromString(String)は、引数として与えられた文字列がUUIDの形式であれば、
            //UUID型の値としてそれを返す

            playersUniqueIdsToAchieves.put(playerUniqueId, achieve);
            //UUIDと称号を結び付けてマップに保持させる(put)
        }
        //for文を抜けると、全ての称号がUUIDと結び付いてplayersUniqueIdsToAchievesマップに保持された状態になる

        playerAchieveRepository = new PlayerAchieveRepository(playersUniqueIdsToAchieves);
        //読み込んだデータをPlayerAchieveRepositoryに渡す
        //PlayerAchieveRepositoryはフィールド(クラス変数)を利用して保持する

        playerListNameSwitchTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new PlayerListNameSwitchTask(this, playerAchieveRepository), 80L, 80L);
        //PlayerListNameSwitchTask#run()を、40tick(2秒)後から、40tick(2秒)間隔で非同期に実行する
        //タスクはplayerListNameSwitchTaskで保持

        getCommand("achieve").setExecutor(new AchieveCommand(this,playerAchieveRepository));

    }

    @Override
    public void onDisable() {
        //サーバー終了時に実行される処理

        playerListNameSwitchTask.cancel();
        //保持しておいたタスクをキャンセル

        for (Map.Entry<UUID, String> playerUniqueIdToAchieve : playerAchieveRepository.playerUniqueIdsToAchieves.entrySet()) {
            String key = playerUniqueIdToAchieve.getKey().toString();
            getConfig().set(key, playerUniqueIdToAchieve.getValue());

        }
        saveConfig();

        }

    }