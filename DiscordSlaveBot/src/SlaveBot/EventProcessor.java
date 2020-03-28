package SlaveBot;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.Embed;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.*;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import reactor.core.publisher.Flux;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.function.Consumer;
import java.util.stream.LongStream;

public class EventProcessor {
    Flux<MessageCreateEvent> on;

    public EventProcessor(Flux<MessageCreateEvent> on) {
        this.on = on;

        on.subscribe(messageCreateEvent -> {
            try {
                onMessageReceived(messageCreateEvent.getMessage());
            }catch (Exception e){
                System.out.println(e.getClass().getSimpleName());
            }
        });
    }

    public void onMessageReceived(Message message){


        String body = message.getContent().get();
        MessageChannel channel = message.getChannel().block();
        Guild guild = message.getGuild().block();

        String[] lowerArgs = body.toLowerCase().split(" ");

        discord4j.core.object.entity.User sender = message.getAuthor().get();
        User internalSender = Tools.getUser(sender.getId().asLong());


        if(!body.contains(BotUtils.BOT_PREFIX)) return;

        /* Person Commands */
        switch (lowerArgs[0].substring(1)){
            case "ping":{
                BotUtils.sendMessage(channel, "Pong! Bot is online!");
                break;
            }
            case "profile":{
                if(lowerArgs.length < 2){
                    Consumer<EmbedCreateSpec> embedCreateSpec = embed -> {
                        embed.setTitle(sender.getUsername() + "'s profile");
                        embed.addField("Balance", ":moneybag: " + internalSender.getMoney(), true);
                        embed.addField("Health", ":heart: " + internalSender.getHealth(), true);
                        embed.addField("Shield", ":shield: " + internalSender.getShield(), true);
                        embed.addField("Kills:", "" + internalSender.kills, true);
                        embed.addField("Deaths:", "" + internalSender.deaths, true);
                        embed.addField("Slaves:", "**Alive** " + internalSender.slaveList.size() + "    **Escaped** 0   **Dead** 0", false);
                    };
                    BotUtils.sendEmbedSpec(channel,embedCreateSpec);
                }
                else{
                    User target = BotUtils.getInternalUserFromMention(lowerArgs[1]);
                    if(target == null){
                        BotUtils.sendMessage(channel, "Target user not found!");
                        break;
                    }

                    Consumer<EmbedCreateSpec> embedCreateSpec = embed -> {
                        embed.setTitle(target.username + "'s profile");
                        embed.addField("Balance", ":moneybag: " + target.getMoney(), true);
                        embed.addField("Health", ":heart: " + target.getHealth(), true);
                        embed.addField("Shield", ":shield: " + target.getShield(), true);
                        embed.addField("Kills:", "" + target.kills, true);
                        embed.addField("Deaths:", "" + target.deaths, true);
                        embed.addField("Slaves:", "**Alive** " + target.slaveList.size() + "    **Escaped** 0   **Dead** 0", false);
                    };
                    BotUtils.sendEmbedSpec(channel,embedCreateSpec);
                }

                break;
            }
            case "daily":{
                if(internalSender.canDaily()){
                    internalSender.addMoney(1000);
                    BotUtils.sendMessage(channel, "Added 1000 Canadian Pesos into your account");
                }
                else {
                    BotUtils.sendRatelimitMessage(channel, BotUtils.dailyTime - (new Date().getTime() - internalSender.lastDaily));
                }
                break;
            }
            case "work":{
                if(internalSender.canWork()){
                    int money = BotUtils.random.nextInt(500) + 200;
                    internalSender.addMoney(money);
                    BotUtils.sendMessage(channel, "Your labour netted you a total of $" + money);
                }
                else {
                    BotUtils.sendRatelimitMessage(channel, BotUtils.workTime - (new Date().getTime() - internalSender.lastWork));
                }
                break;
            }
            case "money":{
                if(lowerArgs.length < 2){
                    BotUtils.sendMessage(channel, "You have `$" + internalSender.getMoney() + "` in your account");
                    break;
                }

                User target = BotUtils.getInternalUserFromMention(lowerArgs[1]);
                if(target == null){
                    BotUtils.sendMessage(channel, "Target user not found!");
                    break;
                }

                BotUtils.sendMessage(channel, target.username + " has `$" + target.getMoney() + "`");
                break;
            }
            case "give": {
                if (lowerArgs.length < 3) {
                    BotUtils.sendMessage(channel, "This command is to be used like `give <mentionedUser> <amount>");
                    break;
                }
                User target = BotUtils.getInternalUserFromMention(lowerArgs[1]);
                if (target == null) {
                    BotUtils.sendMessage(channel, "Target user not found!");
                    break;
                }

                int amount = 0;
                try {
                    amount = Integer.parseInt(lowerArgs[2]);
                } catch (NumberFormatException nfe) {
                    BotUtils.sendMessage(channel, "You didn't enter a number as your second argument!");
                    break;
                }

                if (internalSender.getMoney() < amount) {
                    BotUtils.sendMessage(channel, "You can't give what you don't have");
                    break;
                }

                internalSender.removeMoney(amount);
                target.addMoney(amount);
                BotUtils.sendMessage(channel, "Transferred `$" + amount + "` over to " + target.username);
                break;
            }
            case "addmoney":{
                System.out.println(internalSender.id);
                if(!LongStream.of(BotUtils.ADMINS).anyMatch(x -> x == internalSender.id)){
                    BotUtils.sendMessage(channel, "Error: Permission Denied");
                    break;
                }
                else if(lowerArgs.length < 2){
                    BotUtils.sendMessage(channel, "This command is to be used like `addmoney <amount>`");
                    break;
                }

                int amount;
                try{
                    amount = Integer.parseInt(lowerArgs[1]);
                }catch (NumberFormatException nfe){
                    BotUtils.sendMessage(channel, "You didn't enter a number as your second argument!");
                    break;
                }

                internalSender.addMoney(amount);
                BotUtils.sendMessage(channel, "Money added successfully!");
                break;
            }
            case "inventory":{
                if(lowerArgs.length < 2){
                    internalSender.sendInventory(channel);
                    break;
                }

                User target = BotUtils.getInternalUserFromMention(lowerArgs[1]);
                if(target == null){
                    BotUtils.sendMessage(channel, "Target user not found!");
                    break;
                }

                internalSender.sendInventory(channel);
                break;
            }
            case "slave":{
                if(lowerArgs.length < 2){
                    BotUtils.sendMessage(channel, "Input a command!");
                    break;
                }

                if(lowerArgs[1].equalsIgnoreCase("list")){
                    BotUtils.sendMessage(channel, internalSender.getFormattedSlaveList());
                }
                else if(lowerArgs[1].equalsIgnoreCase("kill")){
                    
                }
                else if(lowerArgs[1].equalsIgnoreCase("work")){
                    ArrayList<String> removeList = new ArrayList<>();
                    Consumer<EmbedCreateSpec> embedCreateSpec = embed -> {
                        embed.setTitle("Slave report for " + sender.getUsername());

                        for (String s : internalSender.slaveList) {
                            if(BotUtils.random.nextInt(100) > 90){
                                removeList.add(s);
                                embed.addField(s,  "was killed by exhaustion", true);
                            }
                            else{
                                int money = BotUtils.random.nextInt(50);
                                internalSender.addMoney(money);
                                embed.addField(s, "$" + money , true);
                            }
                        }

                        for (String s : removeList) {
                            internalSender.removeSlave(s);
                        }
                    };

                    BotUtils.sendEmbedSpec(channel, embedCreateSpec);


                }

                break;
            }
            case "capture":{
                if(!internalSender.canSlave()){
                    BotUtils.sendRatelimitMessage(channel, BotUtils.slaveTime - (new Date().getTime() - internalSender.lastSlave));
                    break;
                }
                if(lowerArgs.length < 2){
                    BotUtils.sendMessage(channel, "Mention the user you want to target!");
                    break;
                }
                else if(internalSender.getMoney() < 200){
                    BotUtils.sendMessage(channel, "You don't have enough money to deal with the fine!");
                    break;
                }

                User target = BotUtils.getInternalUserFromMention(lowerArgs[1]);
                if(target == null){
                    BotUtils.sendMessage(channel, "Target user not found!");
                    break;
                }

                if(BotUtils.random.nextInt(100) > 84){
                    BotUtils.sendMessage(channel, "You have captured " + target.username + " as your slave!");
                    internalSender.addSlave(target.username);
                }
                else{
                    BotUtils.sendMessage(channel, "You were caught and fined $200");
                    internalSender.removeMoney(200);
                }
                break;
            }
            case "escape":{
                if(!internalSender.canEscape()){
                    BotUtils.sendRatelimitMessage(channel, BotUtils.escapeTime - (new Date().getTime() - internalSender.lastEscape));
                    break;
                }
                if(BotUtils.random.nextInt(100) > 76){
                    BotUtils.sendMessage(channel, "**Attention**! " + sender.getMention() + " has escaped captivity!");
                    for (User u : Tools.users) {
                        if(u.slaveList.contains(internalSender.username)){
                            u.removeSlave(internalSender.username);
                        }
                    }
                }
                else{
                    BotUtils.sendMessage(channel, "You failed to escape and were severely beaten");
                }
                break;
            }
            case "shop":{
                Market.sendMarket(channel);
                break;
            }
            case "buy":{
                if(lowerArgs.length < 3){
                    BotUtils.sendMessage(channel, "Command usage: `buy <amount> <item>`");
                    break;
                }

                String item = "";
                for (int i = 2; i < lowerArgs.length; i++) {
                    item += lowerArgs[i] + " ";
                }
                item = item.trim();
                if(!Market.itemExists(item)){
                    BotUtils.sendMessage(channel, "The specified item does not exist!");
                    break;
                }

                Item result = Market.getItem(item);
                if(internalSender.getMoney() < result.getPrice() * Integer.parseInt(lowerArgs[1])){
                    BotUtils.sendMessage(channel, ("You do not have the required funds\n```Available: " + internalSender.getMoney() + "\nNeeded: " +
                            result.getPrice()*Integer.parseInt(lowerArgs[1]) + "```"));
                    break;
                }

                internalSender.addItem(result, Integer.parseInt(lowerArgs[1]));
                internalSender.removeMoney((result.price * Integer.parseInt(lowerArgs[1])));
                BotUtils.sendMessage(channel, "Item bought successfully! You now have `" + internalSender.getQuantity(result) + "` " + result.getName() + "(s)");
                break;

            }
            case "attack":{
                if(lowerArgs.length < 3){
                    BotUtils.sendMessage(channel, "Command usage: `attack <@user> <weapon>`");
                    break;
                }
                if(!internalSender.canAttack()){
                    BotUtils.sendRatelimitMessage(channel, BotUtils.attackTime - (new Date().getTime() - internalSender.lastAttack));
                    break;
                }
                String item = "";
                for (int i = 2; i < lowerArgs.length; i++) {
                    item += lowerArgs[i] + " ";
                }
                item = item.trim();

                Item weapon = Market.getItem(item);


                User target = BotUtils.getInternalUserFromMention(lowerArgs[1]);
                if(target == null){
                    BotUtils.sendMessage(channel, "Target user not found!");
                    break;
                }
                if(!internalSender.containsItem(weapon)){
                    BotUtils.sendMessage(channel, "You do not have any " + weapon.getName() + "(s) to use!");
                    break;
                }

                if(!weapon.isWeapon()){
                    BotUtils.sendMessage(channel, weapon.getName() + " is not a weapon!");
                    break;
                }

                int dmg = weapon.getDamage();
                if(dmg == 0){
                    BotUtils.sendMessage(channel, "Your attack missed!");
                }
                else{
                    target.damage(dmg);
                    if(target.getHealth() <= 0){
                        BotUtils.sendMessage(channel, (Main.getUserByID(internalSender.id).getMention() +" hit " + Main.getUserByID(target.id).getMention() + " for " + dmg + " damage!\nThey have been killed!"));
                        int moneyGained = BotUtils.random.nextInt(((3* target.money) / 4)+1) + (target.money / 4);
                        BotUtils.sendMessage(channel, (Main.getUserByID(internalSender.id).getMention() + " managed to loot $" + moneyGained + " from the dead body!"));
                        target.setHealth(target.getMaxHealth());
                        target.removeMoney(moneyGained);
                        internalSender.addMoney(moneyGained);
                        internalSender.kills++;
                        target.deaths++;
                    }
                    else if(target.getShield() > 0){
                        BotUtils.sendMessage(channel, (Main.getUserByID(internalSender.id).getMention() + " hit " + Main.getUserByID(target.id).getMention() + " for " + dmg + " damage!\nTheir shield took the blow!"));
                    }
                    else{
                        BotUtils.sendMessage(channel, (Main.getUserByID(internalSender.id).getMention() + " hit " + Main.getUserByID(target.id).getMention() + " for " + dmg + " damage!\nThey have `"
                                + target.getHealth() + "`hp remaining"));
                    }
                }

                internalSender.removeItem(weapon);
                break;
            }
            case "heal":{
                if(lowerArgs.length < 2){
                    BotUtils.sendMessage(channel, "Command usage: `heal <item>");
                    break;
                }
                if(!internalSender.canHeal()){
                    BotUtils.sendRatelimitMessage(channel, BotUtils.healTime - (new Date().getTime() - internalSender.lastHeal));
                    break;
                }

                String item = "";
                for (int i = 1; i < lowerArgs.length; i++) {
                    item += lowerArgs[i] + " ";
                }
                item = item.trim();

                Item heal = Market.getItem(item);

                if(!internalSender.containsItem(heal)){
                    BotUtils.sendMessage(channel, "You do not any " + heal.getName() + "(s) to use!");
                    break;
                }
                if(!heal.isHeal()){
                    BotUtils.sendMessage(channel, heal.getName() + " is not a healing item!");
                    break;
                }

                int amount = heal.getDamage();
                if(amount == 0){
                    BotUtils.sendMessage(channel, "Your item did not manage to heal you (Try something more reliable?)");
                }
                else{
                    internalSender.heal(amount);
                    if(internalSender.getHealth() >= internalSender.getMaxHealth()){
                        BotUtils.sendMessage(channel, (Main.getUserByID(internalSender.id).getMention() +" was healed to full health"));
                    }
                    else{
                        BotUtils.sendMessage(channel, (Main.getUserByID(internalSender.id).getMention() + " was healed by " + amount + "hp!\nThey now have `"
                                + internalSender.getHealth() + "`hp"));
                    }
                }

                internalSender.removeItem(heal);

                break;
            }
            case "shield":{
                if(lowerArgs.length < 2){
                    BotUtils.sendMessage(channel, "Command usage: `shield <item>");
                    break;
                }
                if(internalSender.getShield() > 0){
                    BotUtils.sendMessage(channel, "Cannot deploy shield while another shield is active!");
                    break;
                }

                String item = "";
                for (int i = 1; i < lowerArgs.length; i++) {
                    item += lowerArgs[i] + " ";
                }
                item = item.trim();

                Item shield = Market.getItem(item);

                if(!internalSender.containsItem(shield)){
                    BotUtils.sendMessage(channel, "You do not any " + shield.getName() + "(s) to use!");
                    break;
                }
                if(!shield.isShield()){
                    BotUtils.sendMessage(channel, shield.getName() + " is not a shield!");
                    break;
                }

                int amount = shield.getDamage();
                if(amount == 0){
                    BotUtils.sendMessage(channel, "Your shield failed to deploy!");
                }
                else{
                    internalSender.setShield(amount);
                    BotUtils.sendMessage(channel, "You deployed a shield that provided you with " + amount + "hp worth of protection");
                }

                internalSender.removeItem(shield);
                break;
            }
            case "removeitem":{
                if(lowerArgs.length < 2){
                    BotUtils.sendMessage(channel, "Command usage: `removeitem <item>`");
                }
            }
        }
    }
}
