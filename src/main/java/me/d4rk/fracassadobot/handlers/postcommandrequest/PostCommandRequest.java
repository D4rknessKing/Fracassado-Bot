package me.d4rk.fracassadobot.handlers.postcommandrequest;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PostCommandRequest {

    public enum ENUM {
        USE_DEBUFF(new PostCommandRequest("Por favor, digite o nome do usuario do qual deseja utilizar o debuff:", "**Error: **Não foi possivel encontrar o usuário. Por favor tente novamente."));

        private final PostCommandRequest pcr;

        ENUM(PostCommandRequest pcr){
            this.pcr = pcr;
        }

        public PostCommandRequest getPcr() {
            return pcr;
        }
    }

    private String guildId, info;
    private final long requestTime;
    private final String requestMessage, errorMessage;

    private PostCommandRequest(String requestMessage, String errorMessage) {
        this.guildId = null;
        this.info = "";
        this.requestTime = 0;
        this.requestMessage = requestMessage;
        this.errorMessage = errorMessage;
    }

    public PostCommandRequest(GuildMessageReceivedEvent event, PostCommandRequest pcr, String info) {
        event.getChannel().sendMessage(pcr.requestMessage).queue();
        this.info = info;
        this.guildId = event.getGuild().getId();
        this.requestTime = System.currentTimeMillis();
        this.requestMessage = pcr.requestMessage;
        this.errorMessage = pcr.errorMessage;
    }

    public String getGuildId() {
        return guildId;
    }

    public String getInfo() {
        return info;
    }

    public long getRequestTime() {
        return requestTime;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getId() {
        String id = null;
        for (PostCommandRequest.ENUM entry : PostCommandRequest.ENUM.values()) {
            if(entry.getPcr().requestMessage.equals(this.requestMessage)) id = entry.name();
        }
        return id;
    }
}
