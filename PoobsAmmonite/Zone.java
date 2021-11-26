package PoobsAmmonite;

import org.powbot.api.Tile;
import org.powbot.api.rt4.Movement;

import java.util.*;

public class Zone {

    protected int westX, eastX, northY, southY, zPlane;

    public Zone(int x1, int x2, int y1, int y2, int z){
        westX = x1;
        eastX = x2;
        northY = y1;
        southY = y2;
        zPlane = z;
    }

    public boolean containsTile(Tile t){
        // strictly within zone
        return (westX <= t.x() && t.x() <= eastX) && (northY >= t.y() && t.y() >= southY) && (t.floor() == zPlane);
    }

    public boolean leakyContainsTile(Tile t){
        // loosely within zone
        return (westX < t.x() && t.x() < eastX) && (northY > t.y() && t.y() > southY) && (t.floor() == zPlane);
    }

    public void editZone(int x1, int x2, int y1, int y2, int z){
        westX = x1;
        eastX = x2;
        northY = y1;
        southY = y2;
        zPlane = z;
    }

    public Tile[] getZonesTiles(){
        // Returns the tiles of the zone
        Tile[] tiles = new Tile[441];
        int index = 0;

        for(int i = 0; i < 21; i++){
            for(int j = 0; j < 21; j++){
                tiles[index] = new Tile(westX + j, northY - i, zPlane);
            }
        }

        return tiles;
    }

    public HashSet<Tile> getEdgeTiles(Zone zone){
        // returns HashSet of the outer tiles of the zone on which the function is called
        // which are NOT present in the zone provided
        HashSet<Tile> finalTiles = new HashSet<Tile>();

        for(int i = 0; i < 21; i++){
            Tile temp = new Tile(westX + i, northY, zPlane);
            if(!zone.leakyContainsTile(temp)){
                finalTiles.add(temp);
            }
            temp = new Tile(westX + i, southY, zPlane);
            if(!zone.leakyContainsTile(temp)){
                finalTiles.add(temp);
            }
            temp = new Tile(westX, northY - i, zPlane);
            if(!zone.leakyContainsTile(temp)){
                finalTiles.add(temp);
            }
            temp = new Tile(eastX, northY - i, zPlane);
            if(!zone.leakyContainsTile(temp)){
                finalTiles.add(temp);
            }
        }

        return finalTiles;
    }

    public HashSet<Tile> getResetTiles(HashSet<Tile> T, Zone zone){
        // for each tile in the outer tiles of a zone, creates
        // 4 orthogonal tiles. Adds all the tiles not present in
        // the zone and the passed in zone to a HashSet (no duplicates)
        // Only inserts the tiles if the tiles are NOT blocked

        HashSet<Tile> tiles = new HashSet<Tile>();
        int[][] map = Movement.collisionMap(0).flags();
        Tile north, south, east, west;

        for(Tile t : T){
            north = new Tile(t.x(), t.y() + 1, t.floor());
            south = new Tile(t.x(), t.y() - 1, t.floor());
            east = new Tile(t.x() + 1, t.y(), t.floor());
            west = new Tile(t.x() - 1, t.y(), t.floor());

            if(!containsTile(north) && !zone.containsTile(north) && !north.blocked(map)){
                tiles.add(north);
            }
            if(!containsTile(south) && !zone.containsTile(south) && !south.blocked(map)){
                tiles.add(south);
            }
            if(!containsTile(east) && !zone.containsTile(east) && !east.blocked(map)){
                tiles.add(east);
            }
            if(!containsTile(west) && !zone.containsTile(west) && !west.blocked(map)){
                tiles.add(west);
            }
        }

        return tiles;
    }

    public Tile getClosestTile(HashSet<Tile> T){
        // Creates a TreeMap with the elucidian distances to the tiles
        // and the tile itself. TreeMap sorts itself into ascending order
        // for its keys so we can return the first value

        // This doesnt actually return the shortest path, but to know for sure which
        // path is the shortest is too expensive
        TreeMap<Double, Tile> map = new TreeMap<Double, Tile>();

        for(Tile t : T){
            map.put(t.distance(), t);
        }

        // need to make sure that the tile we are returning is reachable
        Tile t = null;
        for(Map.Entry<Double, Tile> entry : map.entrySet()){
            if(entry.getValue().reachable()){
               t = entry.getValue();
               break;
            }
        }

        return t;
    }

}
