package PoobsAmmonite.Tasks;

import PoobsAmmonite.AmmoniteMain;
import PoobsAmmonite.Constants;
import PoobsAmmonite.Task;
import org.powbot.api.Condition;
import org.powbot.api.rt4.*;

public class TeleportToFossilIsland extends Task {

    AmmoniteMain main;
    Constants c;

    public TeleportToFossilIsland(AmmoniteMain main, Constants c){
        super();
        this.main = main;
        this.c = c;
    }

    public boolean activate(){
        // if youre not on fossil island or in the house on the hill, you need to teleport to the house on the hill
        return !c.fossilIsland.containsTile(Players.local().tile()) && !c.houseOnHill.containsTile(Players.local().tile());
    }

    public void execute(){
        Item digsitePendant = Inventory.stream().filtered(i -> i.name().contains("Digsite pendant")).first();
        Component digsitePendantMenu = Widgets.widget(c.digsitePendantTeleportWidget).component(c.chatBoxMenu).component(c.fossilIslandOption);

        if(digsitePendantMenu.valid() && digsitePendantMenu.visible()){
            if(digsitePendantMenu.click()){
                Condition.wait(() -> c.houseOnHill.containsTile(Players.local().tile()), 100, 20);
            }
            return;
        }

        if(digsitePendant.valid()){
            if(digsitePendant.interact("Rub")){
                Condition.wait(digsitePendantMenu::visible, 100, 20);
            }
        }

    }

}
