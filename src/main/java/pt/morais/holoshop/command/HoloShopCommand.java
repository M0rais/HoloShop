package pt.morais.holoshop.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pt.morais.holoshop.command.impl.CreateSubCommand;
import pt.morais.holoshop.command.impl.RemoveSubCommand;

public class HoloShopCommand implements CommandExecutor {

    private final CreateSubCommand createSubCommand = new CreateSubCommand();
    private final RemoveSubCommand removeSubCommand = new RemoveSubCommand();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (args.length == 0) {
            sender.sendMessage("§aarranjar mensagem depois");
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                createSubCommand.execute(sender, args);
                break;
            case "remove":
                removeSubCommand.execute(sender, args);
                break;
        }


        return false;
    }
}
