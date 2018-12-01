import java.io.*;
import java.rmi.server.ExportException;
import java.util.ArrayList;

public class DataSave {
    public static void saveData(){
        if(!new File("botData.ser").exists()){
            try {
                new File("botData.ser").createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream("botData.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(Tools.users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadData(){
        try{
            FileInputStream fileIn = new FileInputStream("botData.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Tools.users = (ArrayList) in.readObject();
            in.close();
            fileIn.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
