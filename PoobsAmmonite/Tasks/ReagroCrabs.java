package PoobsAmmonite.Tasks;

import PoobsAmmonite.AmmoniteMain;
import PoobsAmmonite.Constants;
import PoobsAmmonite.Task;
import org.powbot.api.Condition;
import org.powbot.api.Tile;
import org.powbot.api.rt4.Movement;

public class ReagroCrabs extends Task {

    AmmoniteMain main;
    Constants c;

    public ReagroCrabs(AmmoniteMain main, Constants c){
        super();
        this.main = main;
        this.c = c;
    }

    public boolean activate(){
        // reset agro after 10 mins. Could be made smarter to kill the npcs before resetting but yea maybe later
        return c.time2 - c.time1 > 10.0;
    }

    public void execute(){
        // Compares which of the 2 potential tiles we want to use to reset our agro. This isnt always the closest or
        // best tile to use, but its good enough without generating 50+ paths to determine the best tile to walk to.

        Tile zone1ResetTile = c.zone1.getClosestTile(c.zone1ResetTiles);
        Tile zone2ResetTile = c.zone2.getClosestTile(c.zone2ResetTiles);

        if(zone1ResetTile.distance() < zone2ResetTile.distance()){
            Movement.moveTo(zone1ResetTile);
//            Movement.builder(zone1ResetTile).setAutoRun(false).move();
//            Movement.builder(zone1ResetTile).setRunMin(99).setRunMax(100).move();
            Condition.wait(() -> c.time2 - c.time1 < 10.0, 100, 50);
            return;
        }

        Movement.moveTo(zone2ResetTile);
//        Movement.builder(zone2ResetTile).setAutoRun(false).move();
//        Movement.builder(zone2ResetTile).setRunMin(99).setRunMax(100).move();
        Condition.wait(() -> c.time2 - c.time1 < 10.0, 100, 50);
    }
}
