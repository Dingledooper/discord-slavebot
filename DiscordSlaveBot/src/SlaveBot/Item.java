package SlaveBot;

import java.io.Serializable;

public class Item implements Serializable {

    public static final long serialVersionUID = 10L;

    String name;
    String description;

    int price;

    int[] damage = {0};

    boolean shield, utility, heal;

    public Item(String name, String description, int price){
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Item(String name, String description, int price, int[] damage, boolean shield){
        this.name = name;
        this.description = description;
        this.price = price;
        this.damage = damage;
        this.shield = shield;
    }

    public Item(String name, String description, int price, int[] damage){
        this.name = name;
        this.description = description;
        this.price = price;
        this.damage = damage;
        heal = true;
    }

    public int getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public int getDamage() {
        return damage[BotUtils.random.nextInt(damage.length)];
    }

    public String getDescription() {
        return description;
    }

    public void setDamage(int[] damage) {
        this.damage = damage;
    }

    public boolean isWeapon(){
        return !(shield || utility || heal);
    }


    public boolean isHeal(){
        return heal;
    }

    public boolean isShield(){
        return shield;
    }


}
