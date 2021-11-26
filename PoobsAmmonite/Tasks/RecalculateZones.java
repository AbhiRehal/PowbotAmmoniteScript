package PoobsAmmonite.Tasks;

import PoobsAmmonite.AmmoniteMain;
import PoobsAmmonite.Constants;
import PoobsAmmonite.Task;
import PoobsAmmonite.Zone;
import org.powbot.api.Tile;
import org.powbot.api.rt4.Players;

public class RecalculateZones extends Task {

    AmmoniteMain main;
    Constants c;

    public RecalculateZones(AmmoniteMain main, Constants c){
        super();
        this.main = main;
        this.c = c;
    }

    public boolean activate() {
        return !c.zone1.containsTile(Players.local().tile()) && !c.zone2.containsTile(Players.local().tile());
    }

    public void execute() {
        Tile currentPos = c.currentPos;

        if (c.calculateZone1) {
            c.zone1.editZone(currentPos.x() - 10, currentPos.x() + 10, currentPos.y() + 10, currentPos.y() - 10, currentPos.floor());
            c.calculateZone1 = false;
            c.calculateZone2 = true;
            c.calculateZones = false;
            c.time1 = System.currentTimeMillis() / 60_000.0;
            return;
        }

        if (c.calculateZone2) {
            c.zone2.editZone(currentPos.x() - 10, currentPos.x() + 10, currentPos.y() + 10, currentPos.y() - 10, currentPos.floor());
            c.calculateZone2 = false;
            c.calculateZone1 = true;
            c.calculateZones = false;
            c.time1 = System.currentTimeMillis() / 60_000.0;
            return;
        }

        if (c.initialStartup) {
            c.zone1 = new Zone(currentPos.x() - 10, currentPos.x() + 10, currentPos.y() + 10, currentPos.y() - 10, currentPos.floor());
            c.zone2 = new Zone(currentPos.x() - 10, currentPos.x() + 10, currentPos.y() + 10, currentPos.y() - 10, currentPos.floor());
            c.fossilIsland = new Zone(3634, 3840, 3902, 3696, 0);
            c.houseOnHill = new Zone(3757, 3771, 3883, 3866, 1);
            c.initialStartup = false;
            c.calculateZones = false;
            c.calculateZone2 = true;
            c.time1 = System.currentTimeMillis() / 60_000.0;
        }
    }

    public void lootZone(Tile t){
        c.lootZone = new Zone(t.x() - 3, t.x() + 3, t.y() + 3, t.y() - 3, t.floor());
    }

}

//      You can ignore below here

//      After some investigating i think its working fine because you can end up in this situation where you move more
//      that 2 tiles before updating which is impossible to move more than 2 tiles in 1 tick therefore I think its just
//      broken ass rs doing its thing
//
//      ___|___             ___|___X
//      ___|___             ___|____
//      ___|___             ___|____
//      __X|___     ->      __P|____
//      ___|___

//      Potential bug, not 100% sure yet but sometimes you can be in a situation like this:
//
//    zone1|zone2  zone1|zone2
//      ___|#        ___|_X_
//      __X|#    ->  __P|___
//      ___|#        ___|___
//
//      The # represent reset tiles that you can walk onto to recalculate zone, what can
//      happen is that you can walk one tile too far in a tick if youre running like this from
//      the edge of zone 1 -> zone 2 which causes the zones to be calculated incorrectly, POTENTIALLY.
//
//      a fix for this could be that you could generate a ring of tiles around the previous P, and current
//      position X like this:
//
//                  __#|##_         ___|___
//      _##|#       __#|X#_         ___|#X_
//      _#P|#   ->  __#|##_     ->  __P|#__
//      _##|#       ___|___         ___|___
//
//      You can see that the 2 ring tiles that both P and X have are the only 2 potential tiles that
//      the character could have walked through, which means we can infer where the character came from
//      if the tiles P and X are direct N/S or W/E of ea other, then the tile that the character came
//      through was the tile with the same x/y coord. if the tiles P and X are NE/NW/SE/SW of each other
//      then the character pathed through the reset tile with the same x coord OR y coord as P. This is very
//      important as it has to be either or, the reset tile CANNOT in this case have the same x AND y coord,
//      it must be either or because if they both were the same then P and X would be in the same location

//      Using the function below, make 2 hashsets of "rings" around P and X. Then get the interest of those
//      hashsets using HashSet<Tile> mergedTiles = new HashSet<Tile>(xRingTiles);
//      mergedTiles.retainAll(pRingTiles);

//      Then loop through the reset tiles in from the main that we already have calculated and figure out if
//      you even need to do these calculations or not because if your currentPos == resetTile then you dont have
//      to worry about any of this.

//    public HashSet<Tile> createRing(Tile position){
//        HashSet<Tile> temp = new HashSet<Tile>();
//
//        temp.add(new Tile(position.x() - 1, position.y() + 1, position.floor()));
//        temp.add(new Tile(position.x(), position.y() + 1, position.floor()));
//        temp.add(new Tile(position.x() + 1, position.y() + 1, position.floor()));
//        temp.add(new Tile(position.x() - 1, position.y(), position.floor()));
//        temp.add(new Tile(position.x() + 1, position.y(), position.floor()));
//        temp.add(new Tile(position.x() -1, position.y() - 1, position.floor()));
//        temp.add(new Tile(position.x(), position.y() - 1, position.floor()));
//        temp.add(new Tile(position.x() + 1, position.y() - 1, position.floor()));
//
//        return temp;
//    }
