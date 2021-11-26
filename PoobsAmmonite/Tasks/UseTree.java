package PoobsAmmonite.Tasks;

import PoobsAmmonite.AmmoniteMain;
import PoobsAmmonite.Constants;
import PoobsAmmonite.Task;
import org.powbot.api.Condition;
import org.powbot.api.rt4.*;

public class UseTree extends Task {

    AmmoniteMain main;
    Constants c;

    public UseTree(AmmoniteMain main, Constants c){
        super();
        this.main = main;
        this.c = c;
    }

    public boolean activate(){
        return c.houseOnHill.containsTile(Players.local().tile());
    }

    public void execute(){
        GameObject mushroomTree = Objects.stream().id(c.mushroomTreeId).first();
        Component treeTele;

        // selects the teleport we are going to use
        if(c.treeTeleport.equalsIgnoreCase("Verdant valley")){
            treeTele = Widgets.widget(c.mushroomTreeWidget).component(c.verdantValleyOption);
        } else {
            treeTele = Widgets.widget(c.mushroomTreeWidget).component(c.mushroomMeadowOption);
        }

        if(treeTele.valid() && treeTele.visible()){
            if(treeTele.click()){
                Condition.wait(() -> !c.houseOnHill.containsTile(Players.local().tile()), 100, 20);
            }
            return;
        }

        if(mushroomTree.valid() && mushroomTree.inViewport()){
            if(mushroomTree.interact("Use")){
                Condition.wait(treeTele::visible, 100, 20);
            }
            return;
        }

        // walks to mushroom tree instead of trying to turn camera etc because users have their zooms at different
        // levels so its easier to just walk up to the tree.
        if(!mushroomTree.inViewport()){
            Movement.moveTo(c.mushroomTree);
            Condition.wait(mushroomTree::inViewport, 100, 20);
        }
    }

}
