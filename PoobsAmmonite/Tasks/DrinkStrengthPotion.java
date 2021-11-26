package PoobsAmmonite.Tasks;

import PoobsAmmonite.AmmoniteMain;
import PoobsAmmonite.Task;
import org.powbot.api.Condition;
import org.powbot.api.rt4.*;

public class DrinkStrengthPotion extends Task {

    AmmoniteMain main;
    PoobsAmmonite.Constants c;

    public DrinkStrengthPotion(AmmoniteMain main, PoobsAmmonite.Constants c){
        super();
        this.main = main;
        this.c = c;
    }

    public boolean activate() {
        return Skills.realLevel(Constants.SKILLS_STRENGTH) + c.attackPotThreshold > Skills.level(Constants.SKILLS_STRENGTH) && c.lootZone.containsTile(Players.local().tile());
    }

    public void execute() {
        Item potion = Inventory.stream().filtered(i -> i.name().contains("Super strength")).first();
        Npc crab = Npcs.stream().at(Players.local().interacting().tile()).first();

        if(potion.valid()){
            if(potion.interact("Drink")){
                if(Condition.wait(() -> !(Skills.realLevel(Constants.SKILLS_STRENGTH) + 10 > Skills.level(Constants.SKILLS_STRENGTH)), 100, 30)){
                    crab.interact("Attack");
                }
            }
        }
    }

}
