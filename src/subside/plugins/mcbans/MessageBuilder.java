package subside.plugins.mcbans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;

public class MessageBuilder {
    StrObj message;

    private class StrObj {
        private List<String> message;

        private StrObj(String[] message) {
            this.message = new ArrayList<String>();
            Collections.addAll(this.message, message);
        }

        protected StrObj replaceAll(String search, String replace) {
            for (int x = 0; x < message.size(); x++) {
                try {
                    message.set(x, message.get(x).replaceAll(search, replace));
                } catch(Exception e){
                    System.out.println(search+" : "+replace);
                    e.printStackTrace();
                }
            }
            return this;
        }

        protected List<String> build() {
            for (int x = 0; x < message.size(); x++) {
                message.set(x, ChatColor.translateAlternateColorCodes('&', message.get(x)));
            }
            return message;
        }

    }

    public MessageBuilder(String[] msg) {
        this.message = new StrObj(msg);
    }

    public MessageBuilder(List<String> msg) {
        this.message = new StrObj(msg.toArray(new String[msg.size()]));
    }
    
    public MessageBuilder(String msg){
        this.message = new StrObj(new String[]{msg});
    }


    public MessageBuilder handler(String handler) {
        message.replaceAll("%handler%", handler);
        return this;
    }
    
    public MessageBuilder from(String from){
        message.replaceAll("%from%", from);
        return this;
    }
    
    public MessageBuilder till(String till){
        message.replaceAll("%till%", till);
        return this;
    }
    
    public MessageBuilder reason(String reason){
        message.replaceAll("%reason%", reason);
        return this;
    }

    public List<String> build() {
        return message.build();
    }
    
    public String[] buildArray(){
        List<String> array = message.build();
        return array.toArray(new String[array.size()]);
    }
}
