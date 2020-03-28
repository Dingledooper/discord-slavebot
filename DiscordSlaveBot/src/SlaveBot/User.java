package SlaveBot;

import discord4j.core.object.entity.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

public class User implements Serializable {

    private static final long serialVersionUID = 42L;

    int money;
    long id;
    String username;

    long lastDaily, lastEscape, lastSteal, lastSlave, lastAttack, lastHeal, lastWork;

    int escapedSlaves = 0, deadSlaves = 0;

    int health = 100, maxHealth = 100;

    int tempIntVar, tempIntVar2;
    String tempStringVar;

    int shieldRemaining = 0;

    int kills = 0, deaths = 0;

    HashMap<Item, Integer> inventory = new HashMap<>();
    ArrayList<String> slaveList = new ArrayList<>();


    public User(long discordID){
        id = discordID;
        username = Main.getUserByID(id).getUsername();
        money = 0;
    }

    public boolean canDaily(){
        Date date1 = new Date(lastDaily);
        Date date2 = new Date();
        if(Math.abs(date1.getTime() - date2.getTime()) >= BotUtils.dailyTime){
            lastDaily = date2.getTime();
            return true;
        }
        return false;
    }

    public boolean canWork(){
        Date date1 = new Date(lastWork);
        Date date2 = new Date();
        if(Math.abs(date1.getTime() - date2.getTime()) >= BotUtils.workTime){
            lastWork = date2.getTime();
            return true;
        }
        return false;
    }


    public boolean canSlave(){
        Date date1 = new Date(lastSlave);
        Date date2 = new Date();
        if(Math.abs(date1.getTime() - date2.getTime()) >= BotUtils.slaveTime){
            lastSlave = date2.getTime();
            return true;
        }
        return false;
    }

    public boolean canEscape(){
        Date date1 = new Date(lastEscape);
        Date date2 = new Date();
        if(Math.abs(date1.getTime() - date2.getTime()) >= BotUtils.escapeTime){
            lastEscape = date2.getTime();
            return true;
        }
        return false;
    }

    public boolean canAttack(){
        Date date1 = new Date(lastAttack);
        Date date2 = new Date();
        if(Math.abs(date1.getTime() - date2.getTime()) >= BotUtils.attackTime){
            lastAttack = date2.getTime();
            return true;
        }
        return false;
    }

    public boolean canHeal(){
        Date date1 = new Date(lastHeal);
        Date date2 = new Date();
        if(Math.abs(date1.getTime() - date2.getTime()) >= BotUtils.healTime){
            lastHeal = date2.getTime();
            return true;
        }
        return false;
    }

    public int getQuantity(Item item){
        if(!containsItem(item)) return 0;

        return inventory.get(getItem(item));
    }

    public boolean containsItem(Item item){
        for (Item i : inventory.keySet()) {
            if(i.getName().equals(item.getName()) && inventory.get(i) >= 1){
                return true;
            }
        }

        return false;
    }

    public Item getItem(Item item){

        if(!containsItem(item)){return null;}
        for (Item i : inventory.keySet()) {
            if(i.getName().equals(item.getName())){
                return i;
            }
        }


        return item;
    }

    public void addItem(Item item){
        if(!containsItem(item)){
            inventory.put(item, 1);
        }
        else{
            inventory.put(getItem(item), inventory.get(getItem(item)) + 1);
        }
    }

    public void addItem(Item item, int amount){
        if(!containsItem(item)){
            inventory.put(item, amount);
        }
        else{
            inventory.put(getItem(item), inventory.get(getItem(item)) + amount);
        }
    }


    public void removeItem(Item item){
        if(!containsItem(item)){return;}
        else if(inventory.get(getItem(item)) - 1 <= 0){
            inventory.remove(getItem(item));
        }
        else{
            inventory.put(getItem(item), inventory.get(getItem(item)) - 1);
        }
    }


    public int getMoney(){
        return money;
    }

    public void addMoney(int amount){
        if((long) money + (long) amount > Integer.MAX_VALUE){money = Integer.MAX_VALUE;}
        else if(amount <= 0){return;}
        else{
            money += amount;
        }
    }

    public void removeMoney(int amount){
        if(money - amount < 0){money = 0;}
        else if(amount <= 0){return;}
        else {
            money -= amount;
        }
    }

    public void addSlave(String slaveName){
        slaveList.add(slaveName);
        addItem(new Item("Slave", "A healthy, working slave", 200));
    }

    public void removeSlave(String slaveName){
        if(!slaveList.contains(slaveName)){return;}

        slaveList.remove(slaveName);
        removeItem(new Item("Slave", "A healthy, working slave", 200));

        verifySlaves();
    }

    public void verifySlaves(){
        int slavesByName, slavesByInventory;
        slavesByName = slaveList.size();
        slavesByInventory = inventory.get(new Item("Slave", "A healthy, working slave", 200));

        if(slavesByInventory > slavesByName){
            int diff = slavesByInventory - slavesByName;
            for (int i = 0; i < diff; i++) {
                slaveList.add("Slave-" + Math.abs(BotUtils.random.nextInt()));
            }
        }
        else if(slavesByName > slavesByInventory){
            int diff = slavesByName - slavesByInventory;
            for (int i = 0; i < diff; i++) {
                slaveList.remove(0);
            }
        }
    }

    public String getFormattedSlaveList(){
        String output = "Displaying captured slaves for " + username + ":\n";
        for (int i = 0; i < slaveList.size(); i++) {
            output += slaveList.get(i) + "\n";
        }

        return output.trim();
    }

    public String getFormattedInventory(){
        String output = "Displaying inventory for " + username + ":\n";
        for (Item i : inventory.keySet()) {
            output += inventory.get(i) + "x " + i.getName() + "\n";
        }
        return output.trim();
    }

    public void sendInventory(MessageChannel channel){
        Consumer<EmbedCreateSpec> embedCreateSpec = embed -> {
            embed.setTitle(username + "'s inventory");
            for (Item i : inventory.keySet()) {
                embed.addField("\u200b",inventory.get(i) + " " + i.getName() + "\n", false);
            }
        };

        BotUtils.sendEmbedSpec(channel, embedCreateSpec);
    }

    public int getHealth(){
        return health;
    }

    public void damage(int health){
        if(shieldRemaining > health) shieldRemaining -= health;
        else{
            health -= shieldRemaining;
            shieldRemaining = 0;
            if(this.health - health <= 0){
                this.health = 0;
            }
            else{
                this.health -= health;
            }
        }
    }

    public void heal(int health){
        if(this.health + health > maxHealth){
            this.health = maxHealth;
        }
        else{
            this.health += health;
        }
    }

    public void setHealth(int health){
        this.health = health;
    }

    public int getMaxHealth(){
        return maxHealth;
    }

    public void setShield(int shield){
        this.shieldRemaining = shield;
    }

    public int getShield(){
        return shieldRemaining;
    }


}
