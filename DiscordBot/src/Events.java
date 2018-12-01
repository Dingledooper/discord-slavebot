import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class Events {

    @EventSubscriber
    public void onMessageReceived(MessageReceivedEvent event){

        String[] lowerArgs = event.getMessage().getContent().toLowerCase().split(" ");
        String[] nonProcessedArgs = event.getMessage().getContent().split(" ");

        if(!lowerArgs[0].startsWith(BotUtils.BOT_PREFIX)){
            return;
        }

        User sender = Tools.getUser(event.getAuthor().getLongID());
        switch (lowerArgs[0].substring(1)){
            case "profile":{
                BotUtils.sendMessage(event.getChannel(), "Displaying profile for user `" + sender.username +
                        "`\n" +
                        "You have `$" + sender.getMoney() + "`");
                break;
            }
            case "daily":{
                if(sender.canDaily()){
                    sender.addMoney(300);
                    BotUtils.sendMessage(event.getChannel(), "Added 300 Canadian Pesos into your account");
                }
                else {
                    BotUtils.sendMessage(event.getChannel(), "It hasn't been 24 hours yet!");
                }
                break;
            }
            case "money":{
                if(lowerArgs.length < 2){
                    BotUtils.sendMessage(event.getChannel(), "You have `$" + sender.getMoney() + "` in your account");
                    break;
                }

                User target = BotUtils.getUserFromMention(lowerArgs[1]);
                if(target == null){
                    BotUtils.sendMessage(event.getChannel(), "Target user not found!");
                    break;
                }

                BotUtils.sendMessage(event.getChannel(), target.username + " has `$" + target.getMoney() + "`");
                break;
            }
            case "give":{
                if(lowerArgs.length < 3){
                    BotUtils.sendMessage(event.getChannel(), "This command is to be used like `give <mentionedUser> <amount>");
                    break;
                }
                User target = BotUtils.getUserFromMention(lowerArgs[1]);
                if(target == null){
                    BotUtils.sendMessage(event.getChannel(), "Target user not found!");
                    break;
                }

                int amount = 0;
                try{
                    amount = Integer.parseInt(lowerArgs[2]);
                }catch (NumberFormatException nfe){
                    BotUtils.sendMessage(event.getChannel(), "You didn't enter a number as your second argument!");
                    break;
                }

                if(sender.getMoney() < amount){
                    BotUtils.sendMessage(event.getChannel(), "Uou can't give what you don't have");
                    break;
                }

                sender.removeMoney(amount);
                target.addMoney(amount);
                BotUtils.sendMessage(event.getChannel(), "Transferred `$" + amount + "` over to "+ target.username);
                break;
            }
            case "addmoney":{
                if(!LongStream.of(BotUtils.ADMINS).anyMatch(x -> x == sender.id)){
                    BotUtils.sendMessage(event.getChannel(), "Error: Permission Denied");
                    break;
                }
                else if(lowerArgs.length < 2){
                    BotUtils.sendMessage(event.getChannel(), "This command is to be used like `addmoney <amount>");
                    break;
                }

                int amount;
                try{
                    amount = Integer.parseInt(lowerArgs[1]);
                }catch (NumberFormatException nfe){
                    BotUtils.sendMessage(event.getChannel(), "You didn't enter a number as your second argument!");
                    break;
                }

                sender.addMoney(amount);
                BotUtils.sendMessage(event.getChannel(), "Money added successfully!");
                break;
            }
            case "inventory":{
                if(lowerArgs.length < 2){
                    BotUtils.sendMessage(event.getChannel(), sender.getFormattedInventory());
                    break;
                }

                User target = BotUtils.getUserFromMention(lowerArgs[1]);
                if(target == null){
                    BotUtils.sendMessage(event.getChannel(), "Target user not found!");
                    break;
                }

                BotUtils.sendMessage(event.getChannel(), target.getFormattedInventory());
                break;
            }
            case "capture":{
                if(lowerArgs.length < 2){
                    BotUtils.sendMessage(event.getChannel(), "Mention the user you want to target!");
                    break;
                }
                else if(sender.getMoney() < 200){
                    BotUtils.sendMessage(event.getChannel(), "You don't have enough money to deal with the fine!");
                    break;
                }

                User target = BotUtils.getUserFromMention(lowerArgs[1]);
                if(target == null){
                    BotUtils.sendMessage(event.getChannel(), "Target user not found!");
                    break;
                }

                if(BotUtils.random.nextInt(100) > 84){
                    BotUtils.sendMessage(event.getChannel(), "You have captured " + target.username + " as your slave!");
                    sender.addSlave(target.username);
                }
                else{
                    BotUtils.sendMessage(event.getChannel(), "You were caught and fined $200");
                    sender.removeMoney(200);
                }
                break;
            }
            case "slave":{
                if(lowerArgs.length < 2){
                    BotUtils.sendMessage(event.getChannel(), "Input a command!");
                    break;
                }

                if(lowerArgs[1].equalsIgnoreCase("list")){
                    BotUtils.sendMessage(event.getChannel(), sender.getFormattedSlaveList());
                }
                else if(lowerArgs[1].equalsIgnoreCase("kill")){

                }

                break;
            }
        }
    }

}