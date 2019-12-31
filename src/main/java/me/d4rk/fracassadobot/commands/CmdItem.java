package me.d4rk.fracassadobot.commands;

import javafx.util.Pair;
import me.d4rk.fracassadobot.handlers.economy.EconomyItem;
import me.d4rk.fracassadobot.handlers.economy.EconomySystemHandler;
import me.d4rk.fracassadobot.handlers.economy.EconomyUser;
import me.d4rk.fracassadobot.handlers.postcommandrequest.PostCommandRequest;
import me.d4rk.fracassadobot.handlers.postcommandrequest.PostCommandRequestHandler;
import me.d4rk.fracassadobot.utils.Config;
import me.d4rk.fracassadobot.utils.EnumPerms;
import me.d4rk.fracassadobot.utils.command.Command;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Random;

public class CmdItem {

    @Command(name="item", description = "Efetua o uso de determinado item", category = "Interaction", usage = "(item)", perms = {EnumPerms.BASE})
    public static void item(GuildMessageReceivedEvent event, String[] args) {
        EconomyUser economyUser = EconomySystemHandler.getUser(event.getGuild().getId(), event.getAuthor().getId());

        String shit = String.join(" ",args);
        EconomyItem wantedItem = null;
        for(EconomyItem entry : EconomyItem.values()) if(entry.getName().toLowerCase().equals(shit.toLowerCase())) wantedItem = entry;
        if(wantedItem == null) event.getChannel().sendMessage("**Error: **Item desconhecido").queue();
        else{
            if(economyUser.getInventory().contains(wantedItem.getId())) {
                if(wantedItem.getId().startsWith("DEBUFF"))
                    PostCommandRequestHandler.addPcr(event.getAuthor().getId(), new PostCommandRequest(event, PostCommandRequest.ENUM.USE_DEBUFF.getPcr(), wantedItem.getId()));
                else if(wantedItem.getId().startsWith("BUFF")) {
                    boolean containsEffect = false;
                    System.out.println(economyUser.getEffects());
                    for(Pair<String, Long> effect : economyUser.getEffects()) {
                        if(wantedItem.getEffect().name().equals(effect.getKey())) containsEffect = true;
                        System.out.println(wantedItem.getEffect().name()+" - "+effect.getKey());
                    }
                    if(containsEffect) {
                        event.getChannel().sendMessage("**O usuario já possui um debuff desse tipo ativo!**").queue();
                    }else{
                        EconomySystemHandler.useItem(event.getGuild().getId(), event.getAuthor().getId(), event.getAuthor().getId(), wantedItem);
                        event.getChannel().sendMessage("**O item foi utilizado com sucesso!**").queue();
                    }
                }else if(wantedItem.getId().equals("LOTTERY")) {
                    EconomySystemHandler.useItem(event.getGuild().getId(), event.getAuthor().getId(), wantedItem);
                    Random rng = new Random();
                    if(rng.nextInt(10000) == 69) {
                        event.getChannel().sendMessage("**:tada: Parabens! Você ganhou na loteria! Aqui estão suas :moneybag: _20.000 FracassoCoins_mas :tada:**").queue();
                        EconomySystemHandler.addMoney(event.getGuild().getId(), event.getAuthor().getId(), 20000);
                    }else{
                        event.getChannel().sendMessage("**Infelizmente não foi dessa vez! :money_with_wings: :pensive:**").queue();
                    }
                }else if(wantedItem.getId().equals("TRASHDAY")) {

                }else if(wantedItem.getId().equals("OWNROLE")) {

                }else{
                    event.getChannel().sendMessage("**Error: **Esse item não pode ser utilizado!\n_Para mais informações sobre o funcionamento de um item, digite "+ Config.prefix +"shop info (nome do item)_").queue();
                }
            }else{
                event.getChannel().sendMessage("**Error: **Você não possui esse item no seu inventário").queue();
            }
        }


    }


}
