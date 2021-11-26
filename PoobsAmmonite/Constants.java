package PoobsAmmonite;

import org.powbot.api.Tile;
import org.powbot.api.rt4.Skills;

import java.util.*;

public class Constants {

    public Constants(){
        super();
    }

    public boolean initialStartup;
    public boolean calculateZones;
    public boolean calculateZone1;
    public boolean calculateZone2;

    public int ticks;
    public int eatThreshold;
    public int attackPotThreshold;
    public int strengthPotThreshold;

    public int mushroomTreeId = 30920;
    public int digsitePendantTeleportWidget = 219;
    public int chatBoxMenu = 1;
    public int fossilIslandOption = 2;
    public int mushroomTreeWidget = 608;
    public int verdantValleyOption = 7;
    public int mushroomMeadowOption = 15;

    public double time1;
    public double time2;

    public String food;
    public String treeTeleport;
    public String[] lootStrings;

    public ArrayList<Task> taskList = new ArrayList<Task>();

    public HashSet<Tile> zone1EdgeTiles;
    public HashSet<Tile> zone2EdgeTiles;
    public HashSet<Tile> zone1ResetTiles;
    public HashSet<Tile> zone2ResetTiles;

    public Tile northern2CrabSpot = new Tile(3716, 3895, 0);
    public Tile middle2CrabSpot = new Tile(3717,3881,0);
    public Tile southern2CrabSpot = new Tile(3718, 3869, 0);
    public Tile southWestern2CrabSpot = new Tile(3715,3848,0);
    public Tile southEastern2CrabSpot = new Tile(3720, 3845, 0);
    public Tile northern3CrabSpot = new Tile(3657, 3875, 0);
    public Tile middle3CrabSpot = new Tile(3734, 3846, 0);
    public Tile southern3CrabSpot = new Tile(3803, 3755, 0);
    public Tile mushroomTree = new Tile(3764, 3879, 1);
    public Tile userCrabSpot;
    public Tile currentPos;
    public Tile previousPos;

    public Zone zone1;
    public Zone zone2;
    public Zone fossilIsland;
    public Zone houseOnHill;
    public Zone lootZone;

    // just some helper functions to set some variables based on the user setup from the GUI

    public void setCrabLocation(String location){
        if(location.equalsIgnoreCase("Northern 2 crab spot")){
            userCrabSpot = northern2CrabSpot;
            treeTeleport = "Mushroom meadow";
            return;
        }
        if(location.equalsIgnoreCase("Middle 2 crab spot")){
            userCrabSpot = middle2CrabSpot;
            treeTeleport = "Mushroom meadow";
            return;
        }
        if(location.equalsIgnoreCase("Southern 2 crab spot")){
            userCrabSpot = southern2CrabSpot;
            return;
        }
        if(location.equalsIgnoreCase("Southwestern 2 crab spot")){
            userCrabSpot = southWestern2CrabSpot;
            treeTeleport = "Mushroom meadow";
            return;
        }
        if(location.equalsIgnoreCase("Southeastern 2 crab spot")){
            userCrabSpot = southEastern2CrabSpot;
            treeTeleport = "Mushroom meadow";
            return;
        }
        if(location.equalsIgnoreCase("Northern 3 crab spot")){
            userCrabSpot = northern3CrabSpot;
            treeTeleport = "Mushroom meadow";
            return;
        }
        if(location.equalsIgnoreCase("Middle 3 crab spot")){
            userCrabSpot = middle3CrabSpot;
            treeTeleport = "Mushroom meadow";
            return;
        }
        if(location.equalsIgnoreCase("Southern 3 crab spot")){
            treeTeleport = "Verdant valley";
            userCrabSpot = southern3CrabSpot;
        }
    }

    public void setFoodToUse(String string){
        // Removes the Eat activity if the user leaves the food blank
        if(food == null){
            taskList.remove(0);
        }
        food = string;
    }

    public void setEatThreshold(int i){
        // If the user enters a number greater than their hp or 0, then the script will eat
        // when hp falls below 50%
        if(i > Skills.realLevel(org.powbot.api.rt4.Constants.SKILLS_HITPOINTS) || i == 0){
            eatThreshold = Skills.realLevel(org.powbot.api.rt4.Constants.SKILLS_HITPOINTS) / 2;
            return;
        }

        eatThreshold = i;
    }

    public void setPotionThreshold(String string, int i){
        // If the user leaves the pot thresholds empty or enters a treshold thats too high, then it will
        // default to potting when the boost drops 5 levels
        if(string.equalsIgnoreCase("Att")){
            if(i > (Skills.realLevel(org.powbot.api.rt4.Constants.SKILLS_ATTACK) * 0.15) + 5 || i == 0){
                attackPotThreshold = (int) (Skills.realLevel(org.powbot.api.rt4.Constants.SKILLS_ATTACK) * 0.15);
            } else {
                attackPotThreshold = i;
            }
            return;
        }

        if(i > (Skills.realLevel(org.powbot.api.rt4.Constants.SKILLS_STRENGTH) * 0.15) + 5 || i == 0){
            strengthPotThreshold = (int) (Skills.realLevel(org.powbot.api.rt4.Constants.SKILLS_ATTACK) * 0.15);
        } else {
            strengthPotThreshold = i;
        }
    }

    public void setLootToTake(String string){
        lootStrings = string.split("\\s*,\\s*");
    }




    // As of refactoring, the Areas are still bugged so i'm just going to use my own zones class

//    public Area zone1;
//    public Area zone2;
//    public Area fossilIsland;
//    public Area houseOnHill;
//    public Area lootZone;


    // The functions below were to be used on Powbot Areas instead of making a library I threw them in here
    // not used as the Areas in the api are still bugged.

//    public boolean leakyContainsTile(Area A, Tile T){
//        int westX = A.get_tiles()[0].x();
//        int southY = A.get_tiles()[0].y();
//        int eastX = A.get_tiles()[2].x();
//        int northY = A.get_tiles()[2].y();
//        int zPlane = A.get_tiles()[2].floor();
//        return ((westX < T.x() && T.x() < eastX) && (northY > T.y() && T.y() > southY) && (T.floor() == zPlane));
//    }
//
//    public HashSet<Tile> getEdgeTiles(Area A, Area B){
//
//        int westX = A.get_tiles()[1].x();
//        int northY = A.get_tiles()[1].y();
//        int eastX = A.get_tiles()[3].x();
//        int southY = A.get_tiles()[3].y();
//        int zPlane = A.get_tiles()[3].floor();
//
//        HashSet<Tile> finalTiles = new HashSet<Tile>();
//
//        for(int i = 0; i < 21; i++){
//            Tile temp = new Tile(westX + i, northY, zPlane);
//            if(!leakyContainsTile(B, temp)){
//                finalTiles.add(temp);
//            }
//            temp = new Tile(westX + i, southY, zPlane);
//            if(!leakyContainsTile(B, temp)){
//                finalTiles.add(temp);
//            }
//            temp = new Tile(westX, northY - i, zPlane);
//            if(!leakyContainsTile(B, temp)){
//                finalTiles.add(temp);
//            }
//            temp = new Tile(eastX, northY - i, zPlane);
//            if(!leakyContainsTile(B, temp)){
//                finalTiles.add(temp);
//            }
//        }
//
//        return finalTiles;
//    }
//
//    public HashSet<Tile> getResetTiles(HashSet<Tile> T, Area A, Area B){
//        int[][] map = Movement.collisionMap(0).flags();
//        Tile north, south, east, west;
//        HashSet<Tile> tiles = new HashSet<Tile>();
//
//        for(Tile t : T){
//            north = new Tile(t.x(), t.y() + 1, t.floor());
//            south = new Tile(t.x(), t.y() - 1, t.floor());
//            east = new Tile(t.x() + 1, t.y(), t.floor());
//            west = new Tile(t.x() - 1, t.y(), t.floor());
//
//            if(!A.contains(north) && !B.contains(north) && !north.blocked(map)){
//                tiles.add(north);
//            }
//            if(!A.contains(south) && !B.contains(south) && !south.blocked(map)){
//                tiles.add(south);
//            }
//            if(!A.contains(east) && !B.contains(east) && !east.blocked(map)){
//                tiles.add(east);
//            }
//            if(!A.contains(west) && !B.contains(west) && !west.blocked(map)){
//                tiles.add(west);
//            }
//        }
//
//        return tiles;
//    }
//
//    public Tile getClosestTile(HashSet<Tile> T){
//        TreeMap<Double, Tile> map = new TreeMap<Double, Tile>();
//
//        for(Tile t : T){
//            map.put(t.distance(), t);
//        }
//
//        return map.get(map.firstKey());
//    }

}
