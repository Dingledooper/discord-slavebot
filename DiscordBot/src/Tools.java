import java.util.ArrayList;

public class Tools {
    static ArrayList<User> users = new ArrayList<>();

    public static User getUser(long DiscordID){
        for (User u: users) {
            if(u.id == DiscordID){
                return u;
            }
        }

        if(Main.cli.fetchUser(DiscordID) == null){return null;}
        User newUser = new User(DiscordID);
        users.add(newUser);
        return newUser;
    }
}
