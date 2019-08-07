package maze.machine.entity;
import maze.machine.RouteFinder;
import maze.machine.World;
public class PlayerFollowingEnemy extends PlayerEnemy{
    public static final int FOLLOW_DISTANCE = 8;
    public PlayerFollowingEnemy(int x, int y, World world){
        super(x, y, world);
    }
    @Override
    public void doMovement(){
        Player player = world.player;
        if(player!=null&&world.getPlot(player.x, player.y)!=null){
            if(world.getPlot(player.x, player.y).danger>=VISION_DISTANCE-FOLLOW_DISTANCE){
                path = RouteFinder.findRoute(world.world, new int[]{x, y}, new int[]{player.x, player.y}, false);
            }
        }
        super.doMovement();
    }
}
