package me.d4rk.fracassadobot.commands;

import me.d4rk.fracassadobot.core.economy.EconomySystemHandler;
import me.d4rk.fracassadobot.core.economy.EconomyUser;
import me.d4rk.fracassadobot.core.permission.BotPerms;
import me.d4rk.fracassadobot.utils.RandomUtils;
import me.d4rk.fracassadobot.core.command.Command;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CmdDaily {

    @Command(name="daily", description = "Get your daily dose of coins.", category = "Interaction", usage = "", perms = {BotPerms.BASE})
    public static void daily(GuildMessageReceivedEvent event, String[] args) {
        EconomyUser user = EconomySystemHandler.getUser(event.getGuild().getId(), event.getAuthor().getId());
        if((System.currentTimeMillis() - user.getLastDaily()) > 86400000) {
            int streak = user.getStreak();
            if((System.currentTimeMillis() - user.getLastStreak()) <= 172800000) streak++;
            else streak = 1;

            long newMoney = user.getMoney()+500;
            if(streak == 7) newMoney += 1000;

            StringBuilder string = new StringBuilder();
            string.append(
                    ":arrow_right: **Aqui estão suas _:dollar: 500 FracassoCoins :dollar:_ diárias "+
                    event.getAuthor().getAsMention()+"**  \n" + "       Você agora possuí "+newMoney+" moedas!\n\n"
            );
            switch (user.getStreak()+1) {
                case 1:
                    string.append("**Streak: **:green_square: :red_square: :red_square: :red_square: :red_square: :red_square: :red_square:   (1/7)");
                    break;
                case 2:
                    string.append("**Streak: **:green_square: :green_square: :red_square: :red_square: :red_square: :red_square: :red_square:   (2/7)");
                    break;
                case 3:
                    string.append("**Streak: **:green_square: :green_square: :green_square: :red_square: :red_square: :red_square: :red_square:   (3/7)");
                    break;
                case 4:
                    string.append("**Streak: **:green_square: :green_square: :green_square: :green_square: :red_square: :red_square: :red_square:   (4/7)");
                    break;
                case 5:
                    string.append("**Streak: **:green_square: :green_square: :green_square: :green_square: :green_square: :red_square: :red_square:   (5/7)");
                    break;
                case 6:
                    string.append("**Streak: **:green_square: :green_square: :green_square: :green_square: :green_square: :green_square: :red_square:   (6/7)");
                    break;
                case 7:
                    string.append("**Streak: **:star: :star: :star: :star: :star: :star: :star:  (7/7)\n**Parabéns por completar um streak de bonus diarios! Você ganhou 1000 FracassoCoins de bonus.**");
                    break;
                default:
                    string.append("**Streak: **:red_square: :red_square: :red_square: :red_square: :red_square: :red_square: :red_square:   (0/7)");
            }
            event.getChannel().sendMessage(string.toString()).queue();
            EconomySystemHandler.updateDaily(event.getGuild().getId(), event.getAuthor().getId(), false);
        }else{
            long timeLeft = 86400000-(System.currentTimeMillis() - user.getLastDaily());
            event.getChannel().sendMessage(":x: **Você ainda precisa esperar :clock4: _"+ RandomUtils.getTime(timeLeft) +"_ :clock4: para pegar o seu daily**").queue();
        }
    }

}
