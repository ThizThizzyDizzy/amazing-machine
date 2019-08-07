package maze.machine;
import java.util.ArrayList;
public class GridPos{
    public final Plot[][] grid;
    public final int x;
    public final int y;
    public GridPos(Plot[][] grid, int[] pos){
        this(grid, pos[0], pos[1]);
    }
    public GridPos(Plot[][] grid, int x, int y){
        this.grid = grid;
        this.x = x;
        this.y = y;
    }
    public int distance(GridPos other){
        return grid!=other.grid?Integer.MAX_VALUE:Math.abs(x-other.x)+Math.abs(y-other.y);
    }
    public boolean equals(Object o){
        return o!=null&&o instanceof GridPos&&distance((GridPos)o)<1;
    }
    public ArrayList<GridPos> getAllPathableSquares(boolean isFriendly){
        ArrayList<GridPos> val = new ArrayList<>();
        if(grid[x][y].type!=PlotType.PATH){
            return val;//Can't cross over things like keys, wood blocks, & doors, though they are marked as pathable so you can click on them
        }
        //World bounds checking built-in to the loop
        for(int i = (x<1?0:x-1); i<=x+1&&i<grid.length; i++){
            for(int j = (y<1?0:y-1); j<=y+1&&j<grid[i].length; j++){
                GridPos o = new GridPos(grid, i, j);
                if(equals(o)){
                    continue;
                }
                if(pathable(o, isFriendly)){
                    if(Math.abs(i-x)+Math.abs(j-y)>1){
                        if(i<x&&pathable(new GridPos(grid, x-1, y), isFriendly)){
                            continue;
                        }else if(i>x&&pathable(new GridPos(grid, x+1, y), isFriendly)){
                            continue;
                        }else if(j<y&&pathable(new GridPos(grid, x, y-1), isFriendly)){
                            continue;
                        }else if(j>y&&pathable(new GridPos(grid, x, y+1), isFriendly)){
                            continue;
                        }
                    }
                    val.add(o);
                }
            }
        }
        return val;
    }
    private static boolean pathable(GridPos o, boolean isFriendly){
        return o.pathable()&&(!isFriendly||o.visible());
    }
    private boolean pathable(){
        return grid[x][y].type.pathable();
    }
    private boolean visible(){
        return grid[x][y].isVisible;
    }
}
