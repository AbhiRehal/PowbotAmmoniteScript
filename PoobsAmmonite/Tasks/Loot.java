package PoobsAmmonite.Tasks;

import PoobsAmmonite.AmmoniteMain;
import PoobsAmmonite.Constants;
import PoobsAmmonite.Task;
import org.powbot.api.Condition;
import org.powbot.api.rt4.*;

public class Loot extends Task {

    AmmoniteMain main;
    Constants c;

    public Loot(AmmoniteMain main, Constants c){
        super();
        this.main = main;
        this.c = c;
    }

    public boolean activate(){
        return c.lootZone.containsTile(Players.local().tile());
    }

    public void execute(){

        // its worth mentioning here that i didn't use .within() because it was
        // throwing an error like NoVirtualMethod or something like that and would
        // kill the script everytime
        GroundItems.stream().name(c.lootStrings).forEach(groundItem -> {
            if(c.lootZone.containsTile(groundItem.tile()) && groundItem.valid()){
                if(groundItem.interact("Take")){
                    Condition.wait(() -> !groundItem.valid(), 100, 30);
                }
            }
        });

    }

}
