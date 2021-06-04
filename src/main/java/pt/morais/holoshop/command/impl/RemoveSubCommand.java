package pt.morais.holoshop.command.impl;

import org.bukkit.command.CommandSender;
import pt.morais.holoshop.command.SubCommand;
import pt.morais.holoshop.dao.ShopDao;
import pt.morais.holoshop.manager.ShopManager;
import pt.morais.holoshop.model.Shop;

public class RemoveSubCommand implements SubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c/holoshop remove <key>");
            return;
        }

        Shop shop = ShopDao.getShopByKey(args[1]);

        if (shop == null) {
            sender.sendMessage("§cHologram key not found!");
            return;
        }

        ShopManager.remove(shop);
        sender.sendMessage("§aHologram removed.");

    }

}
