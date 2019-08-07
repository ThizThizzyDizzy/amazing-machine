package maze.machine.menu;
import java.io.File;
import java.util.ArrayList;
import maze.machine.Core;
import maze.machine.entity.PlayerEnemy;
import maze.machine.entity.PlayerFollowingEnemy;
import maze.machine.Plot;
import maze.machine.PlotType;
import maze.machine.World;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuMaze extends Menu{
    private final int level;
    public int tick;
    private int pathDelay = 10;
    public MenuMaze(GUI gui, int level){
        super(gui, null);
        this.level = level;
        if(level==1){
            tick = -35;//Negative values used for the title.png in the render loop, heh heh heh (All other levels start tick=0)
            //Offset by 1/2 second to give the window time to open & position and the graphics & stuff to start before we start trying to draw things to it
            //Future levels don't need this, it's already open
        }
        if(level>0){
            Core.loadWorld(level);
        }
    }
    public MenuMaze(GUI gui, int level, int event){
        super(gui, null);
        this.level = level;
        tick = 31;
        Core.loadWorld(level, event);
        Core.world.player.doVision(false);
    }
    public MenuMaze(GUI gui, File saveFolder){
        super(gui, null);
        tick = -35;
        level = 0;
        Core.loadWorld(saveFolder);
    }
    public MenuMaze(GUI gui, File saveFolder, int event){
        super(gui, null);
        tick = 31;
        level = 0;
        Core.loadWorld(saveFolder, event);
    }
    @Override
    public void onGUIOpened(){
        if(Core.world.isNull){
            gui.open(new MenuCampaignEnd(gui, new MenuMain(gui)));
        }
    }
    @Override
    public void tick(){
        pathDelay--;
        tick++;//Separate tick counter from the main; this allows level-specific time-related events, such as appearance/disappearance of images & messages
        if(tick==30&&level>0){
            displayOpeningLore();
        }
        if(tick>30){
            Core.world.tick();
        }
        //Do things like moving updates
        if(Core.world.player==null)return;
        for(Plot p : Core.world.player.adding.keySet()){
            Core.world.player.adding.put(p, Core.world.player.adding.get(p)+1);
        }
        for(Plot p : Core.world.player.fillingWater.keySet()){
            Core.world.player.fillingWater.put(p, Core.world.player.fillingWater.get(p)+1);
        }
        for(Plot p : Core.world.player.openingDoors.keySet()){
            Core.world.player.openingDoors.put(p, Core.world.player.openingDoors.get(p)+1);
        }
    }
    public double warningFrame;
    @Override
    public void render(int millisSinceLastTick){
        Core.world.cursorX = Mouse.getX();
        Core.world.cursorY = Display.getHeight()-Mouse.getY();
        renderTitles(millisSinceLastTick);
        do{
            if(Mouse.isButtonDown(0)&&pathDelay<=0){
                int X = Mouse.getX()/20;
                int Y = (Display.getHeight()-Mouse.getY())/20;
                if(X<0||X>=Core.GRID_WIDTH||Y<0||Y>=Core.GRID_HEIGHT||!Core.world.getPlot(X, Y).isVisible){
                    break;
                }else{
                    Core.world.attemptPath(Core.world.player, X, Y, true);
                    pathDelay = 6;
                }
            }
        }while(false);
        if(Core.world.player==null){
            System.err.println("Player is null!");
            return;
        }
        if(Core.world.player.wood>0){
            GL11.glColor4d(127/255d, 51/255d, 0, 1);
            drawRect(10, 10, 30, 30, 0);
            GL11.glColor4d(1, 1, 1, 1);
            drawText(30, 10, Display.getWidth(), 30, Core.world.player.wood+"");
        }
        if(Core.world.player.keys>0){
            GL11.glColor4d(1, 216/255d, 0, 1);
            drawRect(10, 30, 30, 50, 0);
            GL11.glColor4d(1, 1, 1, 1);
            drawText(30, 30, Display.getWidth(), 50, Core.world.player.keys+"");
        }
        double yOffset = 50;
        for(PlotType type : PlotType.values()){
            if(type.isItem()){
                if(Core.world.player.items.get(type)>0){
                    GL11.glColor4d(type.getRed(), type.getGreen(), type.getBlue(), type.getAlpha());
                    drawRect(10, yOffset, 30, yOffset+20, 0);
                    GL11.glColor4d(1, 1, 1, 1);
                    drawText(30, yOffset, Display.getWidth(), yOffset+20, Core.world.player.items.get(type)+"");
                    drawText(10, yOffset, 30, yOffset+20, type.getActivationLetter()+"");
                    yOffset+=20;
                }
            }
        }
        ArrayList<Plot> rem = new ArrayList<>();
        for(Plot p : Core.world.player.adding.keySet()){
            double percent = Core.world.player.adding.get(p)/5d+millisSinceLastTick/50d;
            if(percent>1){
                switch(p.type){
                    case WOOD:
                        Core.world.player.wood++;
                        break;
                    case KEY:
                        Core.world.player.keys++;
                        break;
                    default:
                        if(p.type.isItem()){
                            Core.world.player.items.put(p.type, Core.world.player.items.get(p.type)+1);
                        }else{
                            throw new UnsupportedOperationException("Unregistered item type: "+p.type.toString());
                        }
                }
                rem.add(p);
                continue;
            }
            double x = p.x*20;
            double y = p.y*20;
            double xShift = -x+10;
            double yShift = -y-10;
            switch(p.type){
                case XRAY:
                    yShift+=20;
                case TELEPORT:
                    yShift+=20;
                case SCANNERSWEEP:
                    yShift+=20;
                case FLASHBANG:
                    yShift+=20;
                case KEY:
                    yShift+=20;
                case WOOD:
                    yShift+=20;
                    break;
                default:
                    throw new UnsupportedOperationException("Unrecognized item type: "+p.type.toString());
            }
            double X = x+xShift*percent;
            double Y = y+yShift*percent;
            GL11.glColor4d(p.type.getRed(), p.type.getGreen(), p.type.getBlue(), p.type.getAlpha());
            drawRect(X, Y, X+Plot.size, Y+Plot.size, 0);
        }
        for(Plot p : rem){
            Core.world.player.adding.remove(p);
        }
        GL11.glColor4d(PlotType.WOOD.getRed(), PlotType.WOOD.getGreen(), PlotType.WOOD.getBlue(), PlotType.WOOD.getAlpha());
        for(Plot p : Core.world.player.fillingWater.keySet()){
            double percent = Core.world.player.fillingWater.get(p)/5d+millisSinceLastTick/50d;
            if(percent>1){
                p.setType(PlotType.PATH);
                rem.add(p);
                continue;
            }
            double x = 10;
            double y = 10;
            double xShift = p.x*20-10;
            double yShift = p.y*20-10;
            double X = x+xShift*percent;
            double Y = y+yShift*percent;
            drawRect(X, Y, X+Plot.size, Y+Plot.size, 0);
        }
        for(Plot p : rem){
            Core.world.player.fillingWater.remove(p);
        }
        rem.clear();
        GL11.glColor4d(PlotType.KEY.getRed(), PlotType.KEY.getGreen(), PlotType.KEY.getBlue(), PlotType.KEY.getAlpha());
        for(Plot p : Core.world.player.openingDoors.keySet()){
            double percent = Core.world.player.openingDoors.get(p)/5d+millisSinceLastTick/50d;
            if(percent>1){
                p.setType(PlotType.PATH);
                rem.add(p);
                continue;
            }
            double x = 10;
            double y = 10;
            double xShift = p.x*20-10;
            double yShift = p.y*20-30;
            double X = x+xShift*percent;
            double Y = y+yShift*percent;
            drawRect(X, Y, X+Plot.size, Y+Plot.size, 0);
        }
        for(Plot p : rem){
            Core.world.player.openingDoors.remove(p);
        }
        GL11.glColor4d(1, 1, 1, 1);
        int danger = Core.world.getPlot(Core.world.player.x, Core.world.player.y).danger;
        double slidePercent = Math.min(1,danger/(double)(PlayerEnemy.VISION_DISTANCE-PlayerFollowingEnemy.FOLLOW_DISTANCE));
        double slide = 100*slidePercent;
        drawRect(0, -100+slide, 400, slide, ImageStash.instance.getTexture("/textures/warningLight.png"));
        drawRect(Display.getWidth()-400, -100+slide, Display.getWidth(), slide, ImageStash.instance.getTexture("/textures/warningLight.png"));
        drawRect(0, Display.getHeight()+100-slide, 400, Display.getHeight()-slide, ImageStash.instance.getTexture("/textures/warningLight.png"));
        drawRect(Display.getWidth()-400, Display.getHeight()+100-slide, Display.getWidth(), Display.getHeight()-slide, ImageStash.instance.getTexture("/textures/warningLight.png"));
        if(danger>=(PlayerEnemy.VISION_DISTANCE-PlayerFollowingEnemy.FOLLOW_DISTANCE)){
            warningFrame+=.125;
            if(warningFrame>=2*Math.PI){
                warningFrame = 0;
            }
            double width = Math.abs(Math.sin(warningFrame)*400);
            drawRect(200-width/2, -100+slide, 200+width/2, slide, ImageStash.instance.getTexture("/textures/warning.png"));
            drawRect(Display.getWidth()-200-width/2, -100+slide, Display.getWidth()-200+width/2, slide, ImageStash.instance.getTexture("/textures/warning.png"));
            drawRect(200-width/2, Display.getHeight()+100-slide, 200+width/2, Display.getHeight()-slide, ImageStash.instance.getTexture("/textures/warning.png"));
            drawRect(Display.getWidth()-200-width/2, Display.getHeight()+100-slide, Display.getWidth()-200+width/2, Display.getHeight()-slide, ImageStash.instance.getTexture("/textures/warning.png"));
        }
    }
    boolean pressed = false;
    @Override
    public void keyboardEvent(char character, int key, boolean pressed, boolean repeat){
        super.keyboardEvent(character, key, pressed, repeat);
        if(pressed&&!repeat){
            for(PlotType p : PlotType.values()){
                if(p.isItem()){
                    if(Character.toLowerCase(character)==Character.toLowerCase(p.getActivationLetter())){
                        if(Core.world.player.items.get(p)>0){
                            Core.world.player.items.put(p, Core.world.player.items.get(p)-1);
                            Core.world.powerups.put(p, p.getTime());
                        }
                    }
                }
            }
        }
        if(key==Keyboard.KEY_M&&pressed&&!repeat){
            Core.music = !Core.music;
        }
    }
    protected void displayOpeningLore(){
        gui.open(new MenuLore(gui, this, "start "+level+".txt"));
    }
    public void renderTitles(int millisSinceLastTick){
        if(tick>30){
            GL11.glColor4d(40/255d, 40/255d, 40/255d, 1);
            drawRect(0, 0, Display.getWidth(), Display.getHeight(), 0);
            GL11.glColor4d(1, 1, 1, 1);
        }
        if(tick<0){
            float shift = tick*2+(millisSinceLastTick/100f);//For smoothest possible animation- amazing!
            drawRectWithBounds(0, 0, Core.GRID_WIDTH*20, Core.GRID_HEIGHT*20, Core.GRID_WIDTH*(shift+20), 0, Core.GRID_WIDTH*(shift+60), Core.GRID_HEIGHT*20, ImageStash.instance.getTexture("/textures/title.png"));
        }else if(tick<30){
            String path = "/maze/machine/images/level "+level+".png";
            if(World.class.getResourceAsStream(path)==null){
                tick = 29;
            }
            float shift = tick*2+(millisSinceLastTick/100f);
            drawRectWithBounds(0, 0, Core.GRID_WIDTH*20, Core.GRID_HEIGHT*20, Core.GRID_WIDTH*(shift-40), 0, Core.GRID_WIDTH*shift, Core.GRID_HEIGHT*20, Core.getTexture("level "+level+".png"));
        }else{
            Core.world.render(millisSinceLastTick);
        }
    }
}