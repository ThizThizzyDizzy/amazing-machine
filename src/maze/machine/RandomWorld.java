package maze.machine;
import maze.machine.menu.MenuLore;
import maze.machine.menu.MenuMain;
import java.util.ArrayList;
import java.util.HashMap;
import simplelibrary.Stack;
public class RandomWorld extends World{
    public static int minGoalDist = 250;
    public static int minEnemyDist = 50;
    public static int maxEnemies = 9;
    //1 in n chance of spawning enemy at dead end
    private static final int enemyChance = 25;
    public static int vision = 2;
    public static double followingPercent = .25;
    public static HashMap<PlotType, Double> chances = new HashMap<>();
    public static int challenge;
    public RandomWorld(){
        super(0);
        super.vision = vision;
        generate();
    }
    @Override
    public void nextLevel(){
        boolean ret = false;
        if(challenge==1){//COLLECT
            for(Plot[] plots : world){
                for(Plot p : plots){
                    if(p.type.isItem()){
                        ret = true;
                        p.visibilityTimer = 20;
                    }
                }
            }
        }
        if(ret)return;
        Core.gui.open(new MenuMain(Core.gui));
    }
    private void generate(){
        Plot goal = null;
        while(goal==null){
            players.clear();
            players.add(player);
            int enemies = 0;
            for(int X = 0; X<world.length; X++){
                for(int Y = 0; Y<world[0].length; Y++){
                    world[X][Y] = new Plot(X, Y, this);
                }
            }
            Stack<Plot> stack = new Stack<>();
            ArrayList<Plot> visited = new ArrayList<>();
            stack.push(world[rand.nextInt(world.length/2-2)*2+1][rand.nextInt(world[0].length/2-2)*2+1]);//STARTING POINT
            while(true){
                Plot current = stack.peek();
                current.setType(PlotType.PATH);
                if(!visited.contains(current)){
                    visited.add(current);
                }
                Plot up = null, right = null, down = null, left = null;
                int walls = 0;
                boolean u = false;
                if(current.y>2){
                    up = world[current.x][current.y-2];
                    if(!visited.contains(up)){
                        u = true;
                    }
                    if(world[current.x][current.y-1].type==PlotType.WALL){
                        walls++;
                    }
                }else{
                    walls++;
                }
                boolean r = false;
                if(current.x<world.length-2){
                    right = world[current.x+2][current.y];
                    if(!visited.contains(right)){
                        r = true;
                    }
                    if(world[current.x+1][current.y].type==PlotType.WALL){
                        walls++;
                    }
                }else{
                    walls++;
                }
                boolean d = false;
                if(current.y<world[0].length-3){
                    down = world[current.x][current.y+2];
                    if(!visited.contains(down)){
                        d = true;
                    }
                    if(world[current.x][current.y+1].type==PlotType.WALL){
                        walls++;
                    }
                }else{
                    walls++;
                }
                boolean l = false;
                if(current.x>2){
                    left = world[current.x-2][current.y];
                    if(!visited.contains(left)){
                        l = true;
                    }
                    if(world[current.x-1][current.y].type==PlotType.WALL){
                        walls++;
                    }
                }else{
                    walls++;
                }
                ArrayList<Plot> c = new ArrayList<>();
                if(u)c.add(up);
                if(r)c.add(right);
                if(d)c.add(down);
                if(l)c.add(left);
                if(c.isEmpty()){
                    if(stack.size()==1){
                        setPlayerLocation(stack.peek().x, stack.peek().y);
                    }else if(stack.size()>=minGoalDist&&goal==null){
                        goal = current;
                        goal.setType(PlotType.GOAL);
                    }else if(stack.size()>=minEnemyDist&&enemies<maxEnemies&&rand.nextInt(enemyChance)==0){
                        enemies++;
                        if(rand.nextDouble()<=followingPercent){
                            addFollowingEnemy(current.x, current.y);
                        }else{
                            addEnemy(current.x, current.y);
                        }
                    }else if(walls>=3){
                        ArrayList<PlotType> items = new ArrayList<>();
                        for(PlotType p : chances.keySet()){
                            if(rand.nextDouble()<=chances.get(p)){
                                items.add(p);
                            }
                        }
                        if(!items.isEmpty()){
                            current.setType(items.get(rand.nextInt(items.size())));
                        }
                    }
                    stack.pop();
                    if(stack.isEmpty())break;
                    continue;
                }
                Plot p = c.get(rand.nextInt(c.size()));
                world[(p.x+current.x)/2][(p.y+current.y)/2].setType(PlotType.PATH);
                stack.push(p);
            }
        }
    }
    public void fail(){
        Core.gui.open(new MenuLore(Core.gui, new MenuMain(Core.gui), "fail "+player.death+".txt"));
    }
}