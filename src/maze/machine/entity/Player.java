package maze.machine.entity;
import java.util.ArrayList;
import simplelibrary.opengl.Renderer2D;
public abstract class Player extends Renderer2D{
    public int x;
    public int y;
    public ArrayList<int[]> path;
    public int lastX;
    public int lastY;
    public boolean dead = false;
    public int death = -1;
    public static final int deadTime = 50;
    public int deadTimer = deadTime;
    public Player(int x, int y){
        lastX = this.x = x;
        lastY = this.y = y;
    }
    public abstract void render(int millisSinceLastTick);
    public abstract void tick();
}