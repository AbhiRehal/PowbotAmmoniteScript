package PoobsAmmonite.Tasks;

import PoobsAmmonite.AmmoniteMain;
import PoobsAmmonite.Constants;
import PoobsAmmonite.Task;
import org.powbot.api.rt4.Movement;
import org.powbot.api.rt4.Players;

public class MoveToCrabs extends Task {

    AmmoniteMain main;
    Constants c;

    public MoveToCrabs(AmmoniteMain main, Constants c){
        super();
        this.main = main;
        this.c = c;
    }

    public boolean activate(){
        // If youre on fossil island and youre not at the crab spot, you move to the crab spot specified.
        // The time must also be under 10 mins for this to happen as we want to be able to reset NPC aggression
        // when timer > 10 mins.
        return !Players.local().tile().equals(c.userCrabSpot) && c.fossilIsland.containsTile(Players.local().tile()) && (c.time2-c.time1) < 10.0;
    }

    public void execute(){
        Movement.moveTo(c.userCrabSpot);
//        Movement.builder(c.userCrabSpot).setAutoRun(false).move();
//        Movement.builder(c.userCrabSpot).setRunMin(1).setRunMax(99).move();
    }

}
