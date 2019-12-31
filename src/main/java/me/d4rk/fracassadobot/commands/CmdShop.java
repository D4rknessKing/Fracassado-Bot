package me.d4rk.fracassadobot.commands;

import me.d4rk.fracassadobot.Bot;
import me.d4rk.fracassadobot.handlers.economy.EconomyItem;
import me.d4rk.fracassadobot.handlers.economy.EconomySystemHandler;
import me.d4rk.fracassadobot.handlers.economy.EconomyUser;
import me.d4rk.fracassadobot.utils.Config;
import me.d4rk.fracassadobot.utils.Emoji;
import me.d4rk.fracassadobot.utils.EnumPerms;
import me.d4rk.fracassadobot.utils.command.Command;
import me.d4rk.fracassadobot.utils.command.SubCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CmdShop {

    @Command(name="shop", description = "Shows what's up for sale in the bot's shop", category = "Interaction", usage = "", perms = {EnumPerms.BASE})
    public static void shop(GuildMessageReceivedEvent event, String[] args){
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setAuthor("Loja do FracassadoBot: ", null, Bot.jda.getSelfUser().getAvatarUrl())
                .setDescription("**:moneybag: Bem vindo a loja do FracassadoBot! :moneybag: **\n\n:exclamation: Caso deseje efetuar uma compra, digite o comando "+Config.prefix+"shop buy (nome do item) \n :exclamation: Em caso de duvida sobre o que cada item faz, digite "+Config.prefix+"shop info (nome do item) \n\n**Veja abaixo o que está a venda:**")
                .setColor(event.getMember().getColor())
                .setFooter("Requested by: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getAuthor().getAvatarUrl());
        for(EconomyItem entry : EconomyItem.values()) {
            embedBuilder.addField(entry.getName(), entry.getEmote()+" _"+entry.getPrice()+"$_\n", true);
        }
        event.getChannel().sendMessage(embedBuilder.build()).queue();
    }

    @SubCommand(name="info", description = "Shows a detailed description about a desired item.", usage= "(item)", perms = {EnumPerms.BASE})
    public static void info(GuildMessageReceivedEvent event, String[] args) {
        String shit = String.join(" ",args);
        EconomyItem wantedItem = null;
        for(EconomyItem entry : EconomyItem.values()) if(entry.getName().toLowerCase().equals(shit.toLowerCase())) wantedItem = entry;
        if(wantedItem == null) event.getChannel().sendMessage("**Error: **Item desconhecido").queue();
        else {
            String itemInfo = "\n_:dollar:   "+wantedItem.getPrice()+" FracassoCoins   :dollar:_\n:arrow_right: "+wantedItem.getDescription();
            if(wantedItem.isUsable()) itemInfo += "\n`Para utilizar o item digite "+Config.prefix+"item "+wantedItem.getName()+"`";
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(wantedItem.getName());
            embed.setDescription(itemInfo);
            embed.setColor(event.getMember().getColor());
            embed.setFooter("Requested by: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getAuthor().getAvatarUrl());
            Emote emote = null;
            try{ emote = Bot.jda.getEmoteById(wantedItem.getEmote().replace(">", "").substring(wantedItem.getEmote().length()-19, wantedItem.getEmote().length()-1)); }catch (Exception ignored){}
            if(emote != null) embed.setThumbnail(emote.getImageUrl());
            else{
                if(Emoji.getEmojiList().contains(wantedItem.getEmote())) {
                    int unicode = wantedItem.getEmote().codePointAt(0);
                    String unicodeString;
                    if (unicode > 0xFFFF) unicodeString = Integer.toHexString(wantedItem.getEmote().codePointAt(0) | 0x10000);
                    else unicodeString = Integer.toHexString(wantedItem.getEmote().codePointAt(0) | 0x10000).substring(1);
                    embed.setThumbnail("https://github.com/twitter/twemoji/raw/master/assets/72x72/"+unicodeString+".png");
                }
            }
            event.getChannel().sendMessage(embed.build()).queue();
        }
    }

    @SubCommand(name="buy", description = "Buys something from the bot's shop using in-guild currency.", usage = "(item)", perms = {EnumPerms.BASE})
    public static void buy(GuildMessageReceivedEvent event, String[] args) {
        String shit = String.join(" ",args);
        EconomyItem wantedItem = null;
        for(EconomyItem entry : EconomyItem.values()) if(entry.getName().toLowerCase().equals(shit.toLowerCase())) wantedItem = entry;
        if(wantedItem == null) event.getChannel().sendMessage("**Error: **Item desconhecido").queue();
        else{
            EconomyUser buyer = EconomySystemHandler.getUser(event.getGuild().getId(), event.getAuthor().getId());
            if(buyer.getMoney() >= wantedItem.getPrice()) {
                if(buyer.getInventory().size() >= 20) event.getChannel().sendMessage("**Error: **Seu inventário está cheio!").queue();
                else {
                    EconomySystemHandler.buyItem(event.getGuild().getId(), event.getAuthor().getId(), wantedItem);
                    event.getChannel().sendMessage("**O item foi comprado com sucesso!**").queue();
                }
            }else event.getChannel().sendMessage("**Error: **Você não possui dinheiro suficiente").queue();
        }
    }

}
