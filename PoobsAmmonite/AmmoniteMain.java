package PoobsAmmonite;

import PoobsAmmonite.Tasks.*;
import com.google.common.eventbus.Subscribe;
import org.powbot.api.Color;
import org.powbot.api.Tile;
import org.powbot.api.event.RenderEvent;
import org.powbot.api.event.TickEvent;
import org.powbot.api.rt4.*;
import org.powbot.api.rt4.walking.model.Skill;
import org.powbot.api.script.*;
import org.powbot.api.script.paint.Paint;
import org.powbot.api.script.paint.PaintBuilder;
import org.powbot.mobile.drawing.Graphics;
import org.powbot.mobile.script.ScriptManager;
import org.powbot.mobile.service.ScriptUploader;

@ScriptManifest(
        name = "Poobs Ammonites",
        description = "This script kills ammonite crabs. This script requires you to have a digsite pendant in your inventory" +
                "and access to all of the relevant mushroom trees (you do not need to have travelled to the sticky swamp tree).",
        version = "1.0.0"
)

@ScriptConfiguration.List(
        {
                @ScriptConfiguration(
                        name = "Crab spot",
                        description = "Select where you would like to kill the crabs.",
                        optionType = OptionType.STRING,
                        allowedValues = {"Northern 2 crab spot", "Middle 2 crab spot", "Southern 2 crab spot", "Southwestern 2 crab spot",
                                        "Southeastern 2 crab spot", "Northern 3 crab spot", "Middle 3 crab spot", "Southern 3 crab spot"}
                ),
                @ScriptConfiguration(
                        name =  "Use food",
                        description = "Tick if you want/need to eat food.",
                        optionType = OptionType.BOOLEAN
                ),
                @ScriptConfiguration(
                        name =  "Food to use",
                        description = "Enter the name of the food you would like to use (CASE SENSITIVE). Currently does NOT support partially eaten food i.e. 2/3rds cake.",
                        optionType = OptionType.STRING,
                        visible = false
                ),
                @ScriptConfiguration(
                        name =  "When to eat",
                        description = "Enter the hitpoints level at which you would like to eat",
                        optionType = OptionType.INTEGER,
                        visible = false
                ),
                @ScriptConfiguration(
                        name =  "Use potions",
                        description = "Tick if you want to use super attack/strength potions. If you do want to use potions, enter the boost you would like to maintain." +
                                " i.e. if you want to be boosted at least +10, enter 10. If you leave it blank or enter a number which is impossible to boost to, the bot will" +
                                " drink a dose every time the boost falls by 5 levels.",
                        optionType = OptionType.BOOLEAN
                ),
                @ScriptConfiguration(
                        name =  "When to pot attack",
                        description = "When to pot attack",
                        optionType = OptionType.INTEGER,
                        visible = false
                ),
                @ScriptConfiguration(
                        name =  "When to pot strength",
                        description = "When to pot strength",
                        optionType = OptionType.INTEGER,
                        visible = false
                ),
                @ScriptConfiguration(
                        name =  "Take loot",
                        description = "Tick if you want to loot.",
                        optionType = OptionType.BOOLEAN
                ),
                @ScriptConfiguration(
                        name =  "Loot",
                        description = "Enter the loot you would like to collect separated by a single comma ' , '.",
                        optionType = OptionType.STRING,
                        visible = false
                )
        }
)

public class AmmoniteMain extends AbstractScript {

    public static void main(String[] args){
        new ScriptUploader().uploadAndStart("Poobs Ammonites", "", "127.0.0.1:5564", true, false);
    }

    @ValueChanged(keyName = "Use food")
    public void foodChecked(Boolean b){
        updateVisibility("Food to use", b);
        updateVisibility("When to eat", b);
    }

    @ValueChanged(keyName = "Use potions")
    public void potionsChecked(Boolean b){
        updateVisibility("When to pot attack", b);
        updateVisibility("When to pot strength", b);
    }

    @ValueChanged(keyName = "Take loot")
    public void lootChecked(Boolean b){
        updateVisibility("Loot", b);
    }

    public final Constants c = new Constants();
    RecalculateZones rezone;

    @Override
    public void onStart(){

        Paint p = new PaintBuilder().trackSkill(Skill.Strength).build();
        addPaint(p);

        c.time1 = System.currentTimeMillis() / 60_000.0;

        c.initialStartup = true;
        c.calculateZone1 = false;
        c.calculateZone2 = false;

        c.ticks = 0;

        c.currentPos = Players.local().tile();

        c.setCrabLocation(getOption("Crab spot"));

        if(getOption("Use food")){
            c.taskList.add(new Eat(this, c));
            c.setFoodToUse(getOption("Food to use"));
            c.setEatThreshold(getOption("When to eat"));
            c.taskList.add(new Eat(this, c));
        }

        if(getOption("Use potions")){
            c.setPotionThreshold("Att", getOption("When to pot attack"));
            c.setPotionThreshold("Str", getOption("When to pot strength"));
            c.taskList.add(new DrinkAttackPotion(this, c));
            c.taskList.add(new DrinkStrengthPotion(this, c));
        }

        if(getOption("Take loot")){
            c.setLootToTake(getOption("Loot"));
            c.taskList.add(new Loot(this, c));
        }

        rezone = new RecalculateZones(this, c);

//        // set up the lootzone for the spot specified
        rezone.lootZone(c.userCrabSpot);

//        // run once to calculate initial zones
        rezone.execute();

        c.taskList.add(new ReagroCrabs(this, c));

        c.taskList.add(new MoveToCrabs(this, c));

        // right now the script does not support already being on fossil island, so these checks arent needed.
        // its for a future feature which would allow you to already be on fossil island somewhere
        if(!c.fossilIsland.containsTile(c.currentPos)){
            c.taskList.add(new UseTree(this, c));
        }

        if(!c.fossilIsland.containsTile(c.currentPos) && !c.houseOnHill.containsTile(c.currentPos)){
            c.taskList.add(new TeleportToFossilIsland(this, c));
        }
    }

    @Override
    public void poll(){
        for(Task t : c.taskList){
            if(t.activate()){
                t.execute();
            }
        }
    }

    @Subscribe
    public void tickRecieved(TickEvent tickEvent){
        // every tick, it sets the previousPos to whatever our position was on the last tick
        // and sets the currentPos to the position in the current tick
        c.previousPos = c.currentPos;
        c.currentPos = Players.local().tile();

        if(rezone.activate()){
            rezone.execute();
        }

        c.ticks++;

        // if at any point you die, the script will stop
        if(Skills.level(org.powbot.api.rt4.Constants.SKILLS_HITPOINTS) == 0){
            ScriptManager.INSTANCE.stop();
        }
    }


    @Subscribe
    public void onRender(RenderEvent e){
        Graphics g = e.getGraphics();
        g.setScale(1.0f);

        c.time2 = System.currentTimeMillis() / 60_000.0;

        c.zone1EdgeTiles = c.zone1.getEdgeTiles(c.zone2);
        c.zone2EdgeTiles = c.zone2.getEdgeTiles(c.zone1);
        c.zone1ResetTiles = c.zone1.getResetTiles(c.zone1EdgeTiles, c.zone2);
        c.zone2ResetTiles = c.zone2.getResetTiles(c.zone2EdgeTiles, c.zone1);

        // draws zone 1 edge tiles in white
        for(Tile t : c.zone1EdgeTiles){
            t.drawOnScreen(g, null, Color.getORANGE(), null);
        }

        // draws zone 2 edge tiles in orange
        for(Tile t : c.zone2EdgeTiles){
            t.drawOnScreen(g, null, Color.getORANGE(), null);
        }

        // draws zone 1 reset tiles in black
        for(Tile t : c.zone1ResetTiles){
            t.drawOnScreen(g, null, Color.getCYAN(), null);
        }

        // draws zone 2 reset tiles in cyan
        for(Tile t : c.zone2ResetTiles){
            t.drawOnScreen(g, null, Color.getCYAN(), null);
        }

        // draws the closest zone 1 reset tile in red
//        c.zone1.getClosestTile(c.zone1ResetTiles).drawOnScreen(g, "ONE", Color.getRED(), null);

        // draws the closest zone 2 reset tile in red
//        c.zone2.getClosestTile(c.zone2ResetTiles).drawOnScreen(g, "TWO", Color.getRED(), null);

        // draws player location 24/7
        Players.local().tile().drawOnScreen(g, null, Color.getGREEN(), null);

        // draws crab spot chosen
        c.userCrabSpot.drawOnScreen(g, "Crab spot", Color.getWHITE(), null);
    }


}
