package maze.machine.entity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import maze.machine.Core;
import maze.machine.GridPos;
import maze.machine.Plot;
import maze.machine.PlotType;
import maze.machine.RouteFinder;
import maze.machine.World;
import org.lwjgl.opengl.GL11;
public class PlayerEnemy extends Player{
    public static final int VISION_DISTANCE = 25;
    private Random rand = new Random();
    private int wood = 0;
    private int keys = 0;
    World world;
    public PlayerEnemy(int x, int y, World world){
        super(x, y);
        this.world = world;
    }
    @Override
    public void render(int millisSinceLastTick){
        if((!world.getPlot(x, y).isVisible||world.getPlot(x, y).isPathHidden)&&!world.powerups.containsKey(PlotType.SCANNERSWEEP)){
            return;
        }
        GL11.glColor3d(1, 0, 0);
        if(world.powerups.containsKey(PlotType.SCANNERSWEEP)){
            double scanner = world.powerups.get(PlotType.SCANNERSWEEP);
            double scannerDuration = PlotType.SCANNERSWEEP.getTime();
            GL11.glColor4d(1, 0, 0, scanner/scannerDuration);
        }
        double percent = millisSinceLastTick/100d;
        double xShift = -(x-lastX)*percent;
        double yShift = -(y-lastY)*percent;
        drawRect(x*20+xShift*20, y*20+yShift*20, x*20+xShift*20+20, y*20+yShift*20+20, 0);
        GL11.glColor3d(0, 0, 0);
    }
    @Override
    public void tick(){
        lastX = x;
        lastY = y;
        doVision();
        if(world.powerups.containsKey(PlotType.FLASHBANG))return;
        doMovement();
        Player player = world.player;
        if(player==null)return;
        if(x==player.x&&y==player.y){
            player.dead = true;
            player.death = 0;
        }
        if(player.x>=x-1&&player.y>=y-1&&player.x<=x+1&&player.y<=y+1){
            player.dead = true;
            player.death = 0;
        }
    }
    public void doVision(){
        world.world[x][y].danger+=VISION_DISTANCE;
        ArrayList<ArrayList<GridPos>> paths = new ArrayList<>(Arrays.asList((ArrayList<GridPos>[])new ArrayList[]{new ArrayList<>(Arrays.asList(new GridPos[]{new GridPos(world.world, x, y)}))}));
        ArrayList<GridPos> covered = new ArrayList<>(paths.get(0));
        while(!paths.isEmpty()){
            ArrayList<GridPos> path = paths.remove(0);
            GridPos last = path.get(path.size()-1);
            if(last.grid[last.x][last.y].type.pathable()){
                ArrayList<GridPos> dest = last.getAllPathableSquares(false);
                dest.removeAll(covered);
                for(GridPos p : dest){
                    covered.add(p);
                    if(p.grid[p.x][p.y].type.pathable()){
                        p.grid[p.x][p.y].danger+=(VISION_DISTANCE-path.size());
                        if(path.size()<VISION_DISTANCE-1){
                            ArrayList<GridPos> _new = new ArrayList<>(path);
                            _new.add(p);
                            paths.add(_new);
                        }
                    }
                }
            }
        }
    }
    public void doMovement(){
        if(path!=null&&!path.isEmpty()){
            int[] nextSpot = path.remove(0);
            Plot p = world.getPlot(nextSpot[0], nextSpot[1]);
            if(p.type==PlotType.PATH){
                x = p.x;
                y = p.y;
            }
        }else{
            path = RouteFinder.findRoute(world.world, new int[]{x, y}, new int[]{rand.nextInt(Core.GRID_WIDTH), rand.nextInt(Core.GRID_HEIGHT)}, false);
        }
    }
}
