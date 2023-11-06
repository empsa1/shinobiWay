package eportela.shinobiway;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageHandler {
    String message;
    int errorType; //0 = success, 1 = failure
    public MessageHandler(String message, int errorType) {
        this.message = message;
        this.errorType = errorType;
    }
    public void msg(Player target)
    {
        message = ShinobiWay.plugin_version + message;
        if (target != null) {
            if (errorType == 0) {
                message = ChatColor.GREEN + message;
                target.sendMessage(message);
            } else {
                message = ChatColor.RED + message;
                target.sendMessage(message);
            }
        }else if (errorType == 0) {
            System.out.println(message);
        }else{
            System.out.println(message);
        }
    }
}
