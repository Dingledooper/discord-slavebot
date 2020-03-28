package SlaveBot;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;

public class Main {

    static DiscordClient client;

    public static void main(String[] args) {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DataSave.saveData();
            System.out.println("Application Terminating ...");
        }));

        DiscordClientBuilder builder = new DiscordClientBuilder("TOKEN HERE");
        client = builder.build();

        client.getEventDispatcher().on(ReadyEvent.class)
                .subscribe(event -> {
                    User self = event.getSelf();
                    System.out.println(String.format("Logged in as %s#%s", self.getUsername(), self.getDiscriminator()));
                });

        new EventProcessor(client.getEventDispatcher().on(MessageCreateEvent.class));

        DataSave.loadData();
        Market.init();

        client.login().block();


    }

    public static User getUserByID(long id){
        return client.getUserById(Snowflake.of(id)).block();
    }

}