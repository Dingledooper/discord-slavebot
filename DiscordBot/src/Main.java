import sx.blah.discord.api.IDiscordClient;

public class Main {

    public static IDiscordClient cli = null;

    public static void main(String[] args){

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Saving user data");
                DataSave.saveData();
            }
        });

        /*if(args.length != 1){
            System.out.println("Please enter the bots token as the first argument e.g java -jar thisjar.jar tokenhere");
            return;
        }*/

        cli = BotUtils.getBuiltDiscordClient("NDE3MTQ2OTc5ODI3NzEyMDAy.DrKzJA.Fw2quwUy5OgdUp5Qr_H2EymgHrc");

        /*
        // Commented out as you don't really want duplicate listeners unless you're intentionally writing your code 
        // like that.
        // Register a listener via the IListener interface
        cli.getDispatcher().registerListener(new IListener<MessageReceivedEvent>() {
            public void handle(MessageReceivedEvent event) {
                if(event.getMessage().getContent().startsWith(BotUtils.BOT_PREFIX + "test"))
                    BotUtils.sendMessage(event.getChannel(), "I am sending a message from an IListener listener");
            }
        });
        */

        // Register a listener via the EventSubscriber annotation which allows for organisation and delegation of events
        cli.getDispatcher().registerListener(new Events());

        // Only login after all events are registered otherwise some may be missed.
        cli.login();



        DataSave.loadData();
    }

}