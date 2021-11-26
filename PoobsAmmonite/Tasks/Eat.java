package PoobsAmmonite.Tasks;

import PoobsAmmonite.AmmoniteMain;
import PoobsAmmonite.Task;
import org.powbot.api.Condition;
import org.powbot.api.rt4.*;

public class Eat extends Task {

    AmmoniteMain main;
    PoobsAmmonite.Constants c;

    public Eat(AmmoniteMain main, PoobsAmmonite.Constants c){
        super();
        this.main = main;
        this.c = c;
    }

    public boolean activate(){
        return Skills.level(Constants.SKILLS_HITPOINTS) < c.eatThreshold;
    }

    public void execute(){
        Npc crab = Npcs.stream().at(Players.local().interacting().tile()).first();
        Item food = Inventory.stream().name(c.food).first();

        if(food.valid()){
            if(food.interact("Eat")) {
                if (Condition.wait(() -> !(Skills.level(Constants.SKILLS_HITPOINTS) < 50), 100, 30)) {
                    crab.interact("Attack");
                }
            }
        }
    }

}
