package SlaveBot;

import discord4j.core.object.entity.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;

import java.awt.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.function.Consumer;

public class Market implements Serializable {
    static HashMap<String, Item[]> categories = new HashMap<>();

    public static void init(){




        Item[] weapons = {
                new Item("Stick", "A generic stick from a normal tree", 50, new int[]{0,1,2,2,3,4,5,6,7,8,9,10}, false),
                new Item("Baseball Bat", "Weapon of choice for stereotypical gangs", 100, new int[]{0,5,5,5,6,6,6,8,9,9,14,14,16,19,22}, false),
                new Item("Slipper", "Taken from an asian parent", 150, new int[]{0,0,0,1,1,1,1,2,2,2,3,5,9,13,15,35}, false),
                new Item("Wooden Sword", "Two planks and a stick", 200, new int[]{0,20,20,20,20,20,20,20}, false),
                new Item("Actual Sword", "Lightly used", 450, new int[]{0,0,0,15,15,15,16,16,20,20,23,23,34}, false),
                new Item("French Rifle", "Never fired, dropped once", 500, new int[]{0,0,0,0,0,0,0,0,0,0,0,100}, false),
                new Item("German Rifle", "Better dead than red", 650, new int[]{0,0,10,10,25,25,25,25,25,50,50,75,100}, false),
                new Item("Perfume", "Strangely effective", 1000, new int[]{0,0,0,50,75,100}, false)
        };
        categories.put("Weapons", weapons);

        Item[] health = {
                new Item("Rag", "Works in a pinch", 10, new int[]{5,6,7,8,9,10}),
                new Item("Bandage", "Generic medical device", 20, new int[]{7,7,7,7,8,8,8,15}),
                new Item("Antibiotic", "How do pills stop bullet wounds?", 100, new int[]{2,5,10,10,15,15,15,12,11}),
                new Item("Wound sealant", "Glorified glue", 150, new int[]{10,10,10,10,11,11,11,11,12,12,12,13,15,18,20,21}),
                new Item("Potion", "Good healing, but unreliable", 200, new int[]{0,50,65}),
                new Item("Splash potion (Tier III)", "Even better, even more unreliable", 400, new int[]{0,0,0,75,100}),
                new Item("Pills", "Drugs to take your mind off the pain", 600, new int[]{30,30,30,31,36,38,47,50}),
                new Item("MedKit", "Certified medical treatment kit", 1500, new int[]{100})

        };
        categories.put("Medical Items", health);

        Item[] shield = {
                new Item("Newspaper", "Effective against weak, low velocity projectiles", 50, new int[]{1,1,1,1,1,2,2,2,3,4,5}, true),
                new Item("Leather Shield", "At least its better than paper", 100, new int[]{2,2,2,2,2,2,3,3,7,8,9,10,13,12,13,17,18}, true),
                new Item("Iron Sheet", "Strong sheet metal", 200, new int[]{1,4,4,4,4,5,10,12,12,11,13,13,18}, true),
                new Item("Chainmail Armor", "Hard to obtain in Minecraft", 499, new int[]{3,3,3,3,5,7,9,11,13,12,14,15,20,22,25}, true),
                new Item("Shield", "Steel and Iron plating, with a strong wooden base", 600, new int[]{10,16,11,23,29,51,53,55,78}, true),
                new Item("Sans-culottes", "Vive la Révolution", 1000, new int[]{30,35,60,76,76,68,80,100}, true),
                new Item("Programmer's Guard", "`int i = 0;`", 1500, new int[]{100,120,150,134,110}, true)
        };
        categories.put("Shields", shield);



    }

    public static Consumer<EmbedCreateSpec> createEmbedMarket(String category){
        return embed -> {
            embed.setTitle("Available " + category);

            Item[] items = categories.get(category);
            for (Item i : items) {
                embed.addField(i.getName() + " -- $" + i.getPrice(), i.getDescription(), false);
            }
        };
    }

    public static void sendMarket(MessageChannel channel){
        for (String s : categories.keySet()) {
            BotUtils.sendEmbedSpec(channel, createEmbedMarket(s));
        }
    }

    public static boolean itemExists(String item){
        for (String s : categories.keySet()) {
            for (Item i : categories.get(s)) {
                if(i.getName().equalsIgnoreCase(item)) return true;
            }
        }

        return false;
    }

    public static Item getItem(String item){
        for (String s : categories.keySet()) {
            for (Item i : categories.get(s)) {
                if(i.getName().equalsIgnoreCase(item)) return i;
            }
        }

        return null;
    }
}
