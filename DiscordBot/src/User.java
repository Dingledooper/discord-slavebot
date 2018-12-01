import java.io.Serializable;
import java.util.*;

public class User implements Serializable {

    int money;
    long id;
    String username;

    long lastDaily, lastEscape, lastSteal;

    int tempIntVar, tempIntVar2;
    String tempStringVar;

    HashMap<String, Integer> inventory = new HashMap<>();
    ArrayList<String> slaveList = new ArrayList<>();


    public User(long discordID){
        id = discordID;
        username = Main.cli.fetchUser(discordID).getName();
        money = 0;
    }

    public boolean canDaily(){
        Date date1 = new Date(lastDaily);
        Date date2 = new Date();
        if(Math.abs(date1.getTime() - date2.getTime()) >= BotUtils.MILLIS_IN_HOUR * 24){
            lastDaily = date2.getTime();
            return true;
        }
        return false;
    }

    public boolean containsItem(String item){
        return inventory.containsKey(item) && inventory.get(item) >= 1;
    }

    public int getItem(String item){
        if(!inventory.containsKey(item)){return 0;}
        return inventory.get(item);
    }

    public void addItem(String item){
        if(!inventory.containsKey(item)){
            inventory.put(item, 1);
        }
        else{
            inventory.put(item, inventory.get(item) + 1);
        }
    }

    public void addItem(String item, int amount){
        if(!inventory.containsKey(item)){
            inventory.put(item, amount);
        }
        else{
            inventory.put(item, inventory.get(item) + amount);
        }
    }

    public void removeItem(String item){
        if(!inventory.containsKey(item)){return;}
        else if(inventory.get(item) - 1 <= 0){
            inventory.remove(item);
        }
        else{
            inventory.put(item, inventory.get(item) - 1);
        }
    }

    public void removeItem(String item, int amount){
        if(!inventory.containsKey(item)){return;}
        else if(inventory.get(item) - amount <= 0){
            inventory.remove(item);
        }
        else{
            inventory.put(item, inventory.get(item) - amount);
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
        addItem("slave");
    }

    public void removeSlave(String slaveName){
        if(!slaveList.contains(slaveName)){return;}

        slaveList.remove(slaveName);
        removeItem("slave");

        verifySlaves();
    }

    public void verifySlaves(){
        int slavesByName, slavesByInventory;
        slavesByName = slaveList.size();
        slavesByInventory = inventory.get("slave");

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
        String output = "Displaying inventory " + username + ":\n";
        Iterator it = inventory.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            output += pair.getValue() + "x " + pair.getKey();
            it.remove(); // avoids a ConcurrentModificationException
        }
        return output.trim();
    }
}
