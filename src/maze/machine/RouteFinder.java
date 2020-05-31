package maze.machine;
import java.util.ArrayList;
import java.util.Arrays;
public class RouteFinder {
    public static ArrayList<int[]> findRoute(Plot[][] grid, int[] start, int[] end, boolean isFriendly){
        GridPos begin = new GridPos(grid, start);
        GridPos finish = new GridPos(grid, end);
        //Neighboring grid square check; does not go for diagonals
        if(begin.distance(finish)<2){
            ArrayList<int[]> path = new ArrayList<>();
            path.add(start);
            if(begin.distance(finish)>0){
                path.add(end);
            }
            return path;
        }
        ArrayList<GridPos> covered = new ArrayList<>();
        ArrayList<ArrayList<GridPos>> routes = new ArrayList<>();
        routes.add(new ArrayList<GridPos>(Arrays.asList(new GridPos[]{begin})));
        ArrayList<GridPos> winner = null;
        while(!routes.isEmpty()&&winner==null){
            ArrayList<GridPos> route = routes.remove(0);
            ArrayList<ArrayList<GridPos>> lst = route(route, covered, isFriendly);
            for(ArrayList<GridPos> g : lst){
                GridPos pos = g.get(g.size()-1);
                if(pos.distance(finish)<2&&pos.grid[pos.x][pos.y].type==PlotType.PATH){
                    winner = g;
                    if(!finish.equals(g.get(g.size()-1))){
                        g.add(finish);
                    }
                    break;
                }
            }routes.addAll(lst);
        }
        if(winner!=null){
            winner.remove(0);
        }
        return toIntList(winner);
    }
    private static ArrayList<ArrayList<GridPos>> route(ArrayList<GridPos> route, ArrayList<GridPos> covered, boolean isFriendly){
        GridPos from = route.get(route.size()-1);
        ArrayList<GridPos> possible = from.getAllPathableSquares(isFriendly);
        possible.removeAll(covered);
        ArrayList<ArrayList<GridPos>> val = new ArrayList<>();
        for(GridPos p : possible){
            ArrayList<GridPos> lst = new ArrayList<>(route);
            lst.add(p);
            val.add(lst);
            covered.add(p); 
        }
        return val;
    }
    private static ArrayList<int[]> toIntList(ArrayList<GridPos> winner){
        if(winner==null){
            return new ArrayList<>();
        }
        ArrayList<int[]> val = new ArrayList<>();
        for(GridPos p : winner){
            val.add(new int[]{p.x, p.y});
        }
        return val;
    }
}
