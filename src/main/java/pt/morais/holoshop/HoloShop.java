package pt.morais.holoshop;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pt.morais.holoshop.command.HoloShopCommand;
import pt.morais.holoshop.dao.ShopDao;
import pt.morais.holoshop.listener.HologramListener;
import pt.morais.holoshop.listener.PlayerListener;
import pt.morais.holoshop.manager.ShopManager;

import java.util.logging.Level;
import java.util.regex.Pattern;

public class HoloShop extends JavaPlugin {

    private static String version;
    private static HoloShop instance;
    private ShopManager shopManager;

    @Override
    public void onEnable() {
        instance = this;
        if (!loadVersion()) return;
        loadManager();
        saveDefaultConfig();
        getCommand("holoshop").setExecutor(new HoloShopCommand(shopManager));
        loadListener();
    }

    private void loadListener() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(shopManager), this);
        Bukkit.getPluginManager().registerEvents(new HologramListener(shopManager), this);
    }

    private void loadManager() {
        this.shopManager = new ShopManager(this);
    }

    private boolean loadVersion() {
        version = Bukkit.getServer().getClass().getPackage().getName().split(Pattern.quote("."))[3];
        switch (version) {
            case "v1_13_R2":
            case "v1_14_R1":
            case "v1_15_R1":
            case "v1_16_R3":
                return true;
            default:
                Bukkit.getServer().getPluginManager().disablePlugin(this);
                getLogger().log(Level.SEVERE, "THIS VERSION IS NOT SUPPORTED!");
                return false;
        }
    }


    public static String getVersion() {
        return version;
    }

    public static HoloShop getInstance() {
        return instance;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

}