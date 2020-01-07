package me.d4rk.fracassadobot.commands;

import me.d4rk.fracassadobot.core.DataHandler;
import me.d4rk.fracassadobot.core.command.Command;
import me.d4rk.fracassadobot.core.economy.EconomySystemHandler;
import me.d4rk.fracassadobot.core.economy.EconomyUser;
import me.d4rk.fracassadobot.core.permission.BotPerms;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Random;

public class CmdGamble {

    private static final Random random = new Random();

    @Command(name = "gamble", description = "Gambles the amount of money you want.", category = "Interaction", usage = "[amount]", perms = {BotPerms.BASE})
    public static void gamble(GuildMessageReceivedEvent event, String[] args) {
        if (args.length < 1) {
            event.getChannel().sendMessage("**Insira uma quantidade de dinheiro para apostar!**").queue();
            return;
        }
        EconomyUser user = EconomySystemHandler.getUser(event.getGuild().getId(), event.getAuthor().getId());
        int amount;
        try {
            amount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            event.getChannel().sendMessage("**Você não inseriu uma quantidade de dinheiro valida para apostar!**").queue();
            return;
        }
        if (amount < 10) {
            event.getChannel().sendMessage("**Você precisa apostar pelo menos 10 FracassoCoins!**").queue();
            return;
        } else if (amount > user.getMoney()) {
            event.getChannel().sendMessage("**Você não pode apostar mais do que você tem!**").queue();
            return;
        }
        int r = random.nextInt(100);
        if (r > 80) {
            event.getChannel().sendMessage("**Você apostou " + amount +  " e ganhou " + amount  * 2 + "**").queue();
            EconomySystemHandler.addMoney(event.getGuild().getId(), event.getAuthor().getId(), amount);
        } else {
            event.getChannel().sendMessage("**Você apostou " + amount + " e perdeu!**").queue();
            DataHandler.database.table("guildEconomy").get(event.getGuild().getId()).update(
                    DataHandler.r.hashMap(event.getAuthor().getId(), DataHandler.r.hashMap("money", user.getMoney()-amount))
            ).run(DataHandler.conn);
        }
    }
}
