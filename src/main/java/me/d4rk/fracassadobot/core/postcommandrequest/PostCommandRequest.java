package me.d4rk.fracassadobot.core.postcommandrequest;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PostCommandRequest {

    public enum ENUM {
        USE_DEBUFF(new PostCommandRequest("**Por favor, mencione o usuario no qual deseja utilizar o debuff:**")),
        USE_OWNROLE1(new PostCommandRequest("**Por favor, digite o nome do cargo que deseja criar: **")),
        USE_OWNROLE2(new PostCommandRequest("**Por favor, digite a cor do cargo que deseja criar: (HEX ou R G B)**"));

        private final PostCommandRequest pcr;

        ENUM(PostCommandRequest pcr){
            this.pcr = pcr;
        }

        public PostCommandRequest getPcr() {
            return pcr;
        }
    }

    private String guildId, info;
    private final String requestMessage;

    private PostCommandRequest(String requestMessage) {
        this.guildId = null;
        this.info = "";
        this.requestMessage = requestMessage;
    }

    public PostCommandRequest(GuildMessageReceivedEvent event, PostCommandRequest pcr, String info) {
        event.getChannel().sendMessage(pcr.requestMessage).queue();
        this.info = info;
        this.guildId = event.getGuild().getId();
        this.requestMessage = pcr.requestMessage;
    }

    public String getGuildId() {
        return guildId;
    }

    public String getInfo() {
        return info;
    }

    public String getId() {
        String id = null;
        for (PostCommandRequest.ENUM entry : PostCommandRequest.ENUM.values()) {
            if(entry.getPcr().requestMessage.equals(this.requestMessage)) id = entry.name();
        }
        return id;
    }
}
