package maze.machine;
import maze.machine.entity.PlayerLocal;
import maze.machine.entity.PlayerEnemy;
import maze.machine.entity.PlayerFollowingEnemy;
import maze.machine.entity.Player;
import java.io.File;
import java.io.IOException;
public class EditableWorld extends World{
    public EditableWorld(World w){
        super();
        for(int i = 0; i<world.length; i++){
            for(int j = 0; j<world[0].length; j++){
                if(w.world[i][j]==null)continue;
                world[i][j] = new Plot(w.world[i][j]);
                world[i][j].world = this;
                world[i][j].isVisible = true;
            }
        }
        for(Animation anim : w.animations.keySet()){
            int X = anim.latchX+w.animations.get(anim)[0];
            int Y = anim.latchY+w.animations.get(anim)[1];
            world[X-1][Y-1].setType(PlotType.ANIM);
            world[X-1][Y+1].setType(PlotType.ANIM);
            world[X+1][Y-1].setType(PlotType.ANIM);
            world[X+1][Y+1].setType(PlotType.ANIM);
            world[X][Y].setType(PlotType.WALL);
            world[X][Y].red = anim.r;
            world[X][Y].green = anim.g;
            world[X][Y].blue = anim.b;
            world[X][Y].alpha = anim.a;
            world[X][Y-1].setType(PlotType.WALL);
            world[X][Y-1].red = anim.delay/255D;
            world[X][Y-1].green = anim.startingFrame/255D;
            world[X][Y-1].blue = anim.collisionSettings/255D;
            world[X][Y-1].alpha = anim.loopSettings/255D;
        }
        for(Player p : w.players){
            if(p instanceof PlayerLocal){
                world[p.x][p.y].setType(PlotType.PLAYER);
            }
            if(p instanceof PlayerEnemy){
                world[p.x][p.y].setType(PlotType.ENEMY);
            }
            if(p instanceof PlayerFollowingEnemy){
                world[p.x][p.y].setType(PlotType.ENEMY_FOLLOWING);
            }
        }
        vision = 3;
    }
    public EditableWorld(){
        super();
        vision = 3;
    }
    public EditableWorld(File f) throws IOException{
        super(f.getParentFile(), Integer.parseInt(f.getName().replace(".png", "")));
    }
    @Override
    public void render(int millisSinceLastTick){
        super.render(millisSinceLastTick);
    }
}