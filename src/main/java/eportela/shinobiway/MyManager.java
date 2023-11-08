package eportela.shinobiway;

public class MyManager {
    private ShinobiWay plugin;

    public MyManager(ShinobiWay plugin) {
        this.plugin = plugin;
    }

    public ShinobiWay getPlugin() {
        return plugin;
    }
    // You can now access the plugin instance using 'this.plugin'
}