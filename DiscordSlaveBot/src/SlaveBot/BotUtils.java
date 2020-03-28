package SlaveBot;

import discord4j.core.object.Embed;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.util.Snowflake;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Mono;

import java.util.Random;
import java.util.function.Consumer;

class BotUtils {

    // Constants for use throughout the bot
    static String BOT_PREFIX = "$";
    static long MILLIS_IN_MINUTE = 1000 * 60;
    static long MILLIS_IN_HOUR = 1000 * 60 * 60;

    static long slaveTime = BotUtils.MILLIS_IN_MINUTE;
    static long dailyTime = BotUtils.MILLIS_IN_HOUR * 24;
    static long escapeTime = BotUtils.MILLIS_IN_MINUTE / 2;
    static long attackTime = BotUtils.MILLIS_IN_MINUTE / 3;
    static long healTime = BotUtils.MILLIS_IN_MINUTE * 2/ 3;
    static long workTime = BotUtils.MILLIS_IN_MINUTE * 7;


    static long[] ADMINS = {363906015051120641L, 384018076858974208L, 692088524962267217L, 506696814490288128L};

    static Random random = new Random();

    public static void sendMessage(MessageChannel channel, String message){
        channel.createMessage(message).block();
    }

    public static User getInternalUserFromMention(String mention){
        if(mention.contains("!")){
            return Tools.getUser(Long.parseLong(mention.substring(mention.indexOf("!") + 1, mention.indexOf(">"))));
        }
        else{
            return Tools.getUser(Long.parseLong(mention.substring(mention.indexOf("@") + 1, mention.indexOf(">"))));
        }
    }

    public static void sendRatelimitMessage(MessageChannel channel, long millisRemaining){
        channel.createMessage(":clock10: Please wait " + convertSecondsToHMmSs(millisRemaining/1000) + " longer").block();
    }

    public static void sendEmbedSpec(MessageChannel channel, Consumer<EmbedCreateSpec> spec){

        channel.createMessage(embed -> embed.setEmbed(spec)).block();
    }



    public static String convertSecondsToHMmSs(long seconds) {

        long s = seconds % 60;

        long m = (seconds / 60) % 60;

        long h = (seconds / (60 * 60)) % 24;

        return String.format("%d:%02d:%02d", h,m,s);

    }
}