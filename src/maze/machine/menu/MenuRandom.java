package maze.machine.menu;
import java.util.HashMap;
import maze.machine.Core;
import maze.machine.PlotType;
import maze.machine.RandomWorld;
import maze.machine.World;
import static maze.machine.World.rand;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
import simplelibrary.opengl.gui.components.MenuComponentOptionButton;
import simplelibrary.opengl.gui.components.MenuComponentSlider;
public class MenuRandom extends Menu{
    private MenuComponentButton random,start,exit;
    private static MenuComponentSlider enemyDist, goalDist, maxEnemies, vision, following;
    private static MenuComponentOptionButton lockEnemyDist, lockGoalDist, lockMaxEnemies, lockVision, lockFollowing, challenge;
    private static HashMap<PlotType, MenuComponentSlider> chances = new HashMap<>();
    private static HashMap<PlotType, MenuComponentOptionButton> chanceLocks = new HashMap<>();
    private int enemyDistI, goalDistI, maxEnemiesI, visionI;
    private World w;
    private String[] challenges = new String[]{"None", "Collect"};
    public MenuRandom(GUI gui, Menu parent){
        super(gui, parent);
        random = add(new MenuComponentButton(Display.getWidth()/2-200, Display.getHeight()-240, 400, 40, "Randomize and Start", true, true, "/textures/gui/button"));
        start = add(new MenuComponentButton(Display.getWidth()/2-200, Display.getHeight()-160, 400, 40, "Start", true, true, "/textures/gui/button"));
        exit = add(new MenuComponentButton(Display.getWidth()/2-200, Display.getHeight()-80, 400, 40, "Back", true, true, "/textures/gui/button"));
        if(vision==null){
            vision = add(new MenuComponentSlider(Display.getWidth()/2-200, 40, 400, 40, 0, 3, 2, true));
            goalDist = add(new MenuComponentSlider(Display.getWidth()/2-200, 80, 400, 40, 50, 350, 300, true));
            maxEnemies = add(new MenuComponentSlider(Display.getWidth()/2-200, 120, 400, 40, 0, 20, 3, true));
            enemyDist = add(new MenuComponentSlider(Display.getWidth()/2-200, 160, 400, 40, 25, 200, 50, true));
            following = add(new MenuComponentSlider(Display.getWidth()/2-200, 200, 400, 40, 0, 1, .25, 100, true));
            lockVision = add(new MenuComponentOptionButton(vision.x+vision.width, vision.y, vision.width, vision.height, "Status", true, true, "/textures/gui/button", 0, "Unlocked", "Locked"));
            lockGoalDist = add(new MenuComponentOptionButton(goalDist.x+goalDist.width, goalDist.y, goalDist.width, goalDist.height, "Status", true, true, "/textures/gui/button", 0, "Unlocked", "Locked"));
            lockMaxEnemies = add(new MenuComponentOptionButton(maxEnemies.x+maxEnemies.width, maxEnemies.y, maxEnemies.width, maxEnemies.height, "Status", true, true, "/textures/gui/button", 0, "Unlocked", "Locked"));
            lockEnemyDist = add(new MenuComponentOptionButton(enemyDist.x+enemyDist.width, enemyDist.y, enemyDist.width, enemyDist.height, "Status", true, true, "/textures/gui/button", 0, "Unlocked", "Locked"));
            lockFollowing = add(new MenuComponentOptionButton(following.x+following.width, following.y, following.width, following.height, "Status", true, true, "/textures/gui/button", 0, "Unlocked", "Locked"));
        }else{
            add(vision);
            add(goalDist);
            add(maxEnemies);
            add(enemyDist);
            add(following);
            add(lockVision);
            add(lockGoalDist);
            add(lockMaxEnemies);
            add(lockEnemyDist);
            add(lockFollowing);
        }
        double yOffset = 240;
        for(PlotType type : PlotType.values()){
            if(type.isItem()){
                if(chances.containsKey(type)){
                    add(chances.get(type));
                    add(chanceLocks.get(type));
                }else{
                    chances.put(type, add(new MenuComponentSlider(Display.getWidth()/2-200, yOffset, 400, 40, 0, 1, type.defaultChance, 100, true)));
                    chanceLocks.put(type, add(new MenuComponentOptionButton(chances.get(type).x+chances.get(type).width, chances.get(type).y, chances.get(type).width, chances.get(type).height, "Status", true, true, "/textures/gui/button", 0, "Unlocked", "Locked")));
                }
                yOffset+=40;
            }
        }
        challenge = add(new MenuComponentOptionButton(Display.getWidth()/2-400, yOffset, 800, 40, "Challenge", true, true, "/textures/gui/button", RandomWorld.challenge, challenges));
    }
    @Override
    public void keyboardEvent(char character, int key, boolean pressed, boolean repeat){
        super.keyboardEvent(character, key, pressed, repeat);
        if(key==Keyboard.KEY_M&&pressed&&!repeat){
            Core.music = !Core.music;
        }
    }
    @Override
    public void renderBackground(){
        super.renderBackground();
        if(w!=null){
            w.render(0);
        }
        boolean changed = false;
        for(PlotType type : PlotType.values()){
            if(type.isItem()){
                if(!RandomWorld.chances.containsKey(type)){
                    changed = true;
                    break;
                }
                if(RandomWorld.chances.get(type)!=chances.get(type).getValue()){
                    changed = true;
                    break;
                }
            }
        }
        if(enemyDistI!=(int) enemyDist.getValue()
                ||goalDistI!=(int) goalDist.getValue()
                ||maxEnemiesI!=(int) maxEnemies.getValue()
                ||visionI!=(int) vision.getValue()
                ||RandomWorld.followingPercent!=following.getValue()
                ||challenge.getIndex()!=RandomWorld.challenge
                ||changed){
            enemyDistI = (int) enemyDist.getValue();
            goalDistI = (int) goalDist.getValue();
            maxEnemiesI = (int) maxEnemies.getValue();
            visionI = (int) vision.getValue();
            RandomWorld.maxEnemies = maxEnemiesI;
            RandomWorld.minGoalDist = goalDistI;
            RandomWorld.minEnemyDist = enemyDistI;
            RandomWorld.followingPercent = following.getValue();
            RandomWorld.challenge = challenge.getIndex();
            for(PlotType type : PlotType.values()){
                if(type.isItem()){
                    RandomWorld.chances.put(type, chances.get(type).getValue());
                }
            }
            RandomWorld.vision = visionI;
            w = new RandomWorld();
            w.tick();
        }
        GL11.glColor4d(1, 1, 1, 1);
    }
    @Override
    public void render(int millisSinceLastTick){
        super.render(millisSinceLastTick);
        GL11.glColor4d(1, 1, 1, 1);
        drawCenteredText(0, 0, Display.getWidth(), 40, "This is an example maze.");
        drawCenteredText(0, 40, Display.getWidth(), 60, "Vision: "+(visionI==0?"NO MAPPING":(visionI==1?"LIMITED MAPPING":(visionI==2?"MAPPING":"MAPPED"))));
        drawCenteredText(0, 80, Display.getWidth(), 100, "Minimum distance from goal:");
        drawCenteredText(0, 120, Display.getWidth(), 140, "Max enemies:");
        drawCenteredText(0, 160, Display.getWidth(), 180, "Minimum distance from enemies:");
        drawCenteredText(0, 200, Display.getWidth(), 220, "Following enemies (%):");
        double yOffset = 240;
        for(PlotType type : PlotType.values()){
            if(type.isItem()){
                drawCenteredText(0, yOffset, Display.getWidth(), yOffset+20, type.toString()+" spawn rate (%):");
                yOffset+=40;
            }
        }
        drawCenteredText(0, random.y+random.height, Display.getWidth(), random.y+random.height+20, "The challenge is not randomized");
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        if(button==random){
            if(lockMaxEnemies.getIndex()==0){
                RandomWorld.maxEnemies = rand.nextInt(21);
            }
            if(lockGoalDist.getIndex()==0){
                RandomWorld.minGoalDist = rand.nextInt(301)+50;
            }
            if(lockEnemyDist.getIndex()==0){
                RandomWorld.minEnemyDist = rand.nextInt(176)+25;
            }
            if(lockVision.getIndex()==0){
                RandomWorld.vision = rand.nextInt(4);
            }
            if(lockFollowing.getIndex()==0){
                RandomWorld.followingPercent = rand.nextDouble();
            }
            for(PlotType type : PlotType.values()){
                if(type.isItem()){
                    if(chanceLocks.get(type).getIndex()==0){
                        RandomWorld.chances.put(type, rand.nextDouble());
                    }
                }
            }
            gui.open(new MenuRandomMaze(gui, parent));
        }
        if(button==start){
            gui.open(new MenuRandomMaze(gui, parent));
        }
        if(button==exit){
            gui.open(parent);
        }
    }
}