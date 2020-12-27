package mocchi2002.moep;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class AchieveCommand implements CommandExecutor {

    private final Main plugin;
    private final PlayerAchieveRepository playerAchieveRepository;


    public AchieveCommand(Main plugin, PlayerAchieveRepository playerAchieveRepository) {
        this.plugin = plugin;
        this.playerAchieveRepository = playerAchieveRepository;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /*
            label → "achieve", "ACHIEVE", "acHiEVe" など 大文字小文字は問わず入ってくるが、
                    全て小文字にした時、確実に"achieve"になることはBukkit(サーバーソフトウェアの名前)により
                    保証されている
            args → コマンドの引数、何が入ってるかはこの時点では不明
         */

        if (args.length == 0) {
            //引数が一つも無い時、つまり、`/achieve` とだけ入力された時

            sender.sendMessage(ChatColor.RED + "引数が不足しています。");
            //引数が不足している旨のエラーメッセージを表示する

            //多くのプラグインはここでコマンド一覧を表示することが多い

            return true;
            //返り値がboolean型だからtrue/falseを指定する必要があるが、
            //onCommand()内では全てtrueで問題無い(理由はDiscordで後述)
        }

        switch (args[0]) {
            case "set": {
                //args[0] が "set" の場合

                if (args.length <= 2) {
                    // `/achieve set` か `/achieve set [player-name]` までしか入力していない場合
                    //称号まで絶対に入力してもらう必要があるので、引数が不足していれば弾く

                    //エラーメッセージを送信する (sender.sendMessage(String))

                    return true;
                }

                //これ以降は、プレイヤー名も称号も指定されていて、args[0]とargs[1]には、
                //何かしらの値が入っている事が保証されている

                UUID specifiedPlayerUniqueId = playerUniqueId(args[1]);
                //指定されたプレイヤーのUUIDを取得する
                //playerUniqueIdメソッドは下の方で定義している



                String newAchieve = args[2];
                //新しい称号を設定する

                newAchieve = ChatColor.translateAlternateColorCodes('&', newAchieve);

                playerAchieveRepository.setAchieve(specifiedPlayerUniqueId, newAchieve);
                //指定されたプレイヤーのUUIDに、新しい称号を設定する

                sender.sendMessage(ChatColor.GREEN + args[1] + "に" + newAchieve + ChatColor.GREEN + "の称号を設定しました。");

                break;
                //Javaのswitch文はフォールスルーなのでcase毎にbreakしてswitch文を脱出しないといけない
            }
            case "remove": {
                //args[0] が "remove" の場合
                if (args.length <= 1) {
                    warnSenderAboutMissingSpecifiedPlayer(sender);

                    return true;
                }

                UUID specifiedPlayerUniqueId = playerUniqueId(args[1]);
                if (specifiedPlayerUniqueId == null) {
                    warnSenderAboutMissingSpecifiedPlayer(sender);
                    return true;
                }

                playerAchieveRepository.removeAchieve(specifiedPlayerUniqueId);

                plugin.getConfig().set(specifiedPlayerUniqueId.toString(), null);
                plugin.saveConfig();

                break;
            }
            default: {
                //args[0]がsetでもremoveでもなかった場合

                //エラーメッセージを出す
                break;
            }
        }

        return true;
    }

    private static UUID playerUniqueId(String playerName) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        /*
            プレイヤー名からOfflinePlayer(別にオンラインでも取得できる)を取得する
            これはサーバーに一度もログインしことがないプレイヤーであっても、
            nullは返ってこず何らかのインスタンスが返される
         */

        return offlinePlayer.hasPlayedBefore() ? offlinePlayer.getUniqueId() : null;
        /*
            OfflinePlayer#hasPlayedBefore() は、プレイヤーが一度でもサーバーに参加したことが
            あるかどうかをboolean型で返す true→参加したことがある / false→参加したことがない
            参加したことがあればUUIDを返し、そうでなければnullを返す
            条件式 ? trueの場合 : falseの場合
            この形の演算子( ? : )を三項演算子という
                → 一つ目の項 ? 二つ目の項 : 三つ目の項
                → だから三項演算子
            if (条件式) {
                trueの場合
            } else {
                falseの場合
            }
            の簡略版
            本当に短い処理を書く時は三項演算子を使うと綺麗に書ける
            条件式 ? (条件式 ? true&trueの場合 : true&falseの場合) : falseの場合
            みたいな入れ子構造にはせず、そういう時は普通にif文で書いた方が判読性が高まる
            だから、本当に短い処理を書く時だけに使う
         */
    }

    private static void warnSenderAboutMissingArguments(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "引数が不足しています。");
    }

    private static void warnSenderAboutMissingSpecifiedPlayer(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "指定されたプレイヤーは存在しません。");
    }

}