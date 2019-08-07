package maze.machine.entity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import maze.machine.Core;
import maze.machine.Plot;
import maze.machine.PlotType;
import maze.machine.World;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
public class PlayerLocal extends Player{
    static final int VISION_DISTANCE = 10;
    public int wood = 0;
    public int keys = 0;
    public HashMap<PlotType, Integer> items = new HashMap<>();
    public HashMap<Plot, Integer> adding = new HashMap<>();
    public HashMap<Plot, Integer> openingDoors = new HashMap<>();
    public HashMap<Plot, Integer> fillingWater = new HashMap<>();
    private final World world;
    public PlayerLocal(int x, int y, World world){
        super(x, y);
        this.world = world;
        for(PlotType type : PlotType.values()){
            if(type.isItem()){
                items.put(type, 0);
            }
        }
    }
    @Override
    public void render(int millisSinceLastTick){
        GL11.glColor3f(0, 1, 0);
        double percent = millisSinceLastTick/100d;
        double xShift = -(x-lastX)*percent;
        double yShift = -(y-lastY)*percent;
        drawRect(x*20+xShift*20, y*20+yShift*20, x*20+xShift*20+20, y*20+yShift*20+20, 0);
        GL11.glColor3f(0, 0, 0);
    }
    @Override
    public void tick(){
        Plot plot = world.getPlot(x, y);
        if(dead==true&&death==-1){
            switch(plot.type){
                case ANIM:
                    death = 2;
                    break;
                case DOOR:
                    death = 3;
                    break;
                case EVENT:
                    death = 4;
                    break;
                case FLASHBANG:
                    death = 5;
                    break;
                case GOAL:
                    death = 6;
                    break;
                case KEY:
                    death = 7;
                    break;
                case PATH:
                    death = 8;
                    break;
                case PIT:
                    death = 9;
                    break;
                case SCANNERSWEEP:
                    death = 10;
                    break;
                case TELEPORT:
                    death = 11;
                    break;
                case TOUCH_EVENT:
                    death = 12;
                    break;
                case WALL:
                    death = 13;
                    break;
                case WATER:
                    death = 14;
                    break;
                case WOOD:
                    death = 15;
                    break;
                case XRAY:
                    death = 16;
                    break;
                default:
                    death = 1;
                    System.err.println("Unknown Cause of death: plot type "+plot.type.toString());
            }
        }
        if(plot!=null&&!plot.type.pathable()){
            dead = true;
            switch(plot.type){
                case ANIM:
                    death = 2;
                    break;
                case DOOR:
                    death = 3;
                    break;
                case EVENT:
                    death = 4;
                    break;
                case FLASHBANG:
                    death = 5;
                    break;
                case GOAL:
                    death = 6;
                    break;
                case KEY:
                    death = 7;
                    break;
                case PATH:
                    death = 8;
                    break;
                case PIT:
                    death = 9;
                    break;
                case SCANNERSWEEP:
                    death = 10;
                    break;
                case TELEPORT:
                    death = 11;
                    break;
                case TOUCH_EVENT:
                    death = 12;
                    break;
                case WALL:
                    death = 13;
                    break;
                case WATER:
                    death = 14;
                    break;
                case WOOD:
                    death = 15;
                    break;
                case XRAY:
                    death = 16;
                    break;
                default:
                    death = 1;
                    System.err.println("Unknown Cause of death: plot type "+plot.type.toString());
            }
        }
        KeyboardInput input = new KeyboardInput(
                Keyboard.isKeyDown(Keyboard.KEY_UP)||Keyboard.isKeyDown(Keyboard.KEY_W),
                Keyboard.isKeyDown(Keyboard.KEY_RIGHT)||Keyboard.isKeyDown(Keyboard.KEY_D),
                Keyboard.isKeyDown(Keyboard.KEY_DOWN)||Keyboard.isKeyDown(Keyboard.KEY_S),
                Keyboard.isKeyDown(Keyboard.KEY_LEFT)||Keyboard.isKeyDown(Keyboard.KEY_A)
        );
        if(input.ok){
            if(path==null) path = new ArrayList<>();
            else path.clear();
            Optional<int[]> v = input.getDirections().stream().map((e)->{return new int[]{e[0]+x, e[1]+y};})
                    .filter((e)->{return world.getPlot(e[0], e[1])==null?false:world.getPlot(e[0], e[1]).type.pathable(this);}).findFirst();
            if(v.isPresent()) path.add(v.get());
        }
        lastX = x;
        lastY = y;
        doMovement();
        doVision(true);
    }
    public void doVision(boolean activateEvents){
        if(x<0||y<0)return;
        if(world.vision==3){
            for(int X = 0; X<world.world.length; X++){
                for(int Y = 0; Y<world.world[0].length; Y++){
                    world.world[X][Y].isVisible = true;
                }
            }
        }
        if(world.vision==0||world.vision==1){
            for(int X = 0; X<world.world.length; X++){
                for(int Y = 0; Y<world.world[0].length; Y++){
                    if(world.vision==0){
                        world.world[X][Y].isVisible = false;
                    }
                    world.world[X][Y].isPathHidden = false;
                    world.world[X][Y].xrayed = false;
                    if(world.vision==1&&world.world[X][Y].type==PlotType.PATH){
                        world.world[X][Y].isPathHidden = true;
                    }
                }
            }
        }
        for(int X = 0; X<world.world.length; X++){
            for(int Y = 0; Y<world.world[0].length; Y++){
                world.world[X][Y].xrayed = false;
            }
        }
        if(world.getPlot(x, y)!=null){
            world.getPlot(x, y).isVisible = true;
            world.getPlot(x, y).isPathHidden = false;
        }
        for(float i = 0; i<360; i+=0.5f){
            double radians = Math.PI*i/180;
            double x = Math.cos(radians);
            double y = Math.sin(radians);
            int seeOvers = 0;
            if(world.powerups.containsKey(PlotType.XRAY)){
                seeOvers = 10;
            }
            for(int j = 10; j<VISION_DISTANCE*10; j++){
                int X = (int)(x*j/10+0.5f+this.x);
                int Y = (int)(y*j/10+0.5f+this.y);
                if(X<0||X>=Core.GRID_WIDTH||Y<0||Y>=Core.GRID_HEIGHT){
                    break;
                }
                world.getPlot(X, Y).isVisible = true;
                world.getPlot(X, Y).isPathHidden = false;
                if(world.getPlot(X, Y).type==PlotType.EVENT&&activateEvents){
                    world.event();
                }
                if(!world.getPlot(X, Y).type.canSeeOver()){
                    seeOvers--;
                    world.getPlot(X, Y).xrayed = true;
                    if(seeOvers<0){
                        world.getPlot(X, Y).xrayed = false;
                        break;
                    }
                }
            }
        }
    }
    private void doMovement(){
        if(path!=null&&!path.isEmpty()){
            int[] nextSpot = path.remove(0);
            Plot p = world.getPlot(nextSpot[0], nextSpot[1]);
            if(p.type==PlotType.KEY||p.type==PlotType.WOOD||p.type==PlotType.FLASHBANG||p.type==PlotType.SCANNERSWEEP||p.type==PlotType.TELEPORT||p.type==PlotType.XRAY){
                adding.put(new Plot(p),0);
                p.setType(PlotType.PATH);
            }else if(p.type==PlotType.TOUCH_EVENT){
                world.event();
                p.setType(PlotType.WALL);
                path.clear();
            }else if(p.type==PlotType.DOOR&&keys>0){
                if(openingDoors.containsKey(p))return;
                openingDoors.put(p,0);
                keys--;
                path.clear();
            }else if(p.type==PlotType.WATER&&wood>0){
                if(fillingWater.containsKey(p))return;
                fillingWater.put(p,0);
                wood--;
                path.clear();
            }else if(p.type==PlotType.GOAL){
                world.nextLevel();
            }else if(p.type==PlotType.PATH){
                x = p.x;
                y = p.y;
            }else if(p.type==PlotType.WALL){
                path.clear();
            }else if(p.type==PlotType.EVENT){
                path.clear();
            }else{
                throw new UnsupportedOperationException("Cannot path to unknown plot type: "+p.type);
            }
        }
    }
    private static class KeyboardInput{
        private boolean up, rt, dn, lt, ok;
        private KeyboardInput(boolean up, boolean rt, boolean dn, boolean lt) {
            if(up&&dn) up=dn=false;
            if(lt&&rt) lt=rt=false;
            this.up = up;
            this.rt = rt;
            this.dn = dn;
            this.lt = lt;
            this.ok = up||rt||dn||lt;
        }
        private ArrayList<int[]> getDirections(){
            ArrayList<int[]> lst = new ArrayList<>();
            if(up&&lt) lst.add(new int[]{-1, -1});
            if(up&&rt) lst.add(new int[]{1, -1});
            if(dn&&lt) lst.add(new int[]{-1, 1});
            if(dn&&rt) lst.add(new int[]{1, 1});
            if(up) lst.add(new int[]{0, -1});
            if(dn) lst.add(new int[]{0, 1});
            if(lt) lst.add(new int[]{-1, 0});
            if(rt) lst.add(new int[]{1, 0});
            return lst;
        }
    }
}
