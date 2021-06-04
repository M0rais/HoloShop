package pt.morais.holoshop.command.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pt.morais.holoshop.command.SubCommand;
import pt.morais.holoshop.manager.ShopManager;

public class CreateSubCommand implements SubCommand {

    private final ShopManager shopManager;

    public CreateSubCommand(ShopManager shopManager) {
        this.shopManager = shopManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly users can use this command.");
            return;
        }

        if (args.length < 4) {
            sender.sendMessage("§c/holoshop create <key> <amount> <price> <sellPrice>(Optional)");
            return;
        }

        Player player = (Player) sender;

        ItemStack item = player.getItemInHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage("§cYou must have a valid item in your hand!");
            return;
        }

        String key = args[1];
        Location location = player.getLocation();

        try {
            int amount = Integer.parseInt(args[2]);

            if (amount <= 0) {
                player.sendMessage("§cAmount must be higher than 0");
                return;
            }

            double price = Double.parseDouble(args[3]);
            double sellPrice = args.length < 5 ? 0 : Double.parseDouble(args[4]);

            if (price < 0 || sellPrice < 0) {
                player.sendMessage("§cThe price or sell price must be higher than -1");
                return;
            }

            if (price == 0 && sellPrice == 0) {
                player.sendMessage("§cThe price or sell price must be higher than 0");
                return;
            }

            if (shopManager.create(key, amount, location, item, price, sellPrice))
                player.sendMessage("§aHologram created.");
            else
                player.sendMessage("§cThat key is already in use!");
        } catch (NumberFormatException e) {
            player.sendMessage("§cThe price or sell price isn't valid, it must be a number");
        }

    }

}