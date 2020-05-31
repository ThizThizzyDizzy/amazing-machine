package maze.machine;
import maze.machine.entity.PlayerLocal;
import maze.machine.entity.PlayerEnemy;
import maze.machine.entity.PlayerFollowingEnemy;
import maze.machine.entity.Player;
import maze.machine.menu.MenuLore;
import maze.machine.menu.MenuMain;
import maze.machine.menu.MenuMaze;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import javax.imageio.ImageIO;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.Renderer2D;
public class World extends Renderer2D{
    public int vision = 2;
    public Plot[][] world = new Plot[Core.GRID_WIDTH][Core.GRID_HEIGHT];
    protected final ArrayList<Player> players = new ArrayList<>();
    private final int level;
    private final int event;
    public static final Random rand = new Random();
    public int cursorX;
    public int cursorY;
    public PlayerLocal player;
    public HashMap<PlotType, Double> powerups = new HashMap<>();
    HashMap<Animation, int[]> animations = new HashMap<>();
    private File custom = null;
    public boolean isNull = false;
    public World(){
        this(0);
    }
    public World(int level){
        this(level, 0);
    }
    public World(int level, int event){
        this.level = level;
        this.event = event;
        BufferedImage img = null;
        if(level>=1){
            InputStream stream = World.class.getResourceAsStream(Levels.getLevelPath(level, event));
            try{
                img = ImageIO.read(stream);
            }catch(IOException | IllegalArgumentException | NullPointerException ex){
                System.err.println("Failed loading level "+level+"!");
                isNull = true;
                return;
            }
        }
        for(int i = 0; i<world.length; i++){
            for(int j = 0; j<world[0].length; j++){
                if(world[i][j]!=null)continue;
                if(img==null||i>=img.getWidth()||j>=img.getHeight()){
                    world[i][j] = new Plot(i, j, this);
                }else{
                    world[i][j] = new Plot(i, j, this).processType(this, img.getRGB(i, j));
                }
            }
        }
        for(int i = 0; i<world.length-2; i++){
            for(int j = 0; j<world[0].length-2; j++){
                if(world[i][j].type==PlotType.ANIM){
                    int animR = (int) Math.round(world[i+1][j+1].red*255);
                    int animG = (int) Math.round(world[i+1][j+1].green*255);
                    int animB = (int) Math.round(world[i+1][j+1].blue*255);
                    int animA = (int) Math.round(world[i+1][j+1].alpha*255);
                    PlotType type = world[i+1][j].type;
                    for(int I = i; I<i+3; I++){
                        for(int J = j; J<j+3; J++){
                            world[I][J].setType(type);
                        }
                    }
                    addAnim(i+1, j+1, animR, animG, animB, animA);
                }
            }
        }
    }
    public World(File saveFolder) throws IOException{
        this(saveFolder, 0);
    }
    public World(File saveFolder, int event) throws IOException{
        this.event = event;
        level = 0;
        custom = saveFolder;
        BufferedImage img = null;
        File file = new File(Levels.getLevelPath(saveFolder, event));
        try{
            img = ImageIO.read(file);
        }catch(IOException | IllegalArgumentException ex){
            Core.gui.open(new MenuMain(Core.gui));
            System.err.println("Failed loading level "+file.getAbsolutePath()+"!");
            event = 0;
            return;
        }
        for(int i = 0; i<world.length; i++){
            for(int j = 0; j<world[0].length; j++){
                if(world[i][j]!=null)continue;
                if(img==null||i>=img.getWidth()||j>=img.getHeight()){
                    world[i][j] = new Plot(i, j, this);
                }else{
                    world[i][j] = new Plot(i, j, this).processType(this, img.getRGB(i, j));
                }
            }
        }
        for(int i = 0; i<world.length-2; i++){
            for(int j = 0; j<world[0].length-2; j++){
                if(world[i][j].type==PlotType.ANIM){
                    int animR = (int) Math.round(world[i+1][j+1].red*255);
                    int animG = (int) Math.round(world[i+1][j+1].green*255);
                    int animB = (int) Math.round(world[i+1][j+1].blue*255);
                    int animA = (int) Math.round(world[i+1][j+1].alpha*255);
                    PlotType type = world[i+1][j].type;
                    for(int I = i; I<i+3; I++){
                        for(int J = j; J<j+3; J++){
                            world[I][J].setType(type);
                        }
                    }
                    addAnim(new File(saveFolder, "\\anims"), i+1, j+1, animR, animG, animB, animA);
                }
            }
        }
        event = 0;
    }
    public void render(int millisSinceLastTick){
        FOR:for(int x = 0; x<world.length; x++){
            for(int y = 0; y<world[x].length; y++){
                if(world[x][y]==null){
                    continue;
                }
                if(world[x][y].type==PlotType.WALLSEEALL){
                    vision = 3;
                    world[x][y].setType(PlotType.WALL);
                    break FOR;
                }
            }
        }
        if(vision==3){
            for(int X = 0; X<world.length; X++){
                for(int Y = 0; Y<world[0].length; Y++){
                    if(world[X][Y]==null){
                        continue;
                    }
                    world[X][Y].isVisible = true;
                }
            }
        }
        for(Plot[] plots : world){
            for(Plot p : plots){
                if(p!=null)p.render();
            }
        }
        for(Player p : players){
            p.render(millisSinceLastTick);
        }
        for(Animation anim : animations.keySet()){
            int f = Math.min(anim.anim.size()-1, anim.frame);
            Plot[][] frame = anim.anim.get(f);
            int[] coords = animations.get(anim);
            if(anim.collisionSettings>0){
                for(int i = 0; i<frame.length; i++){
                    for(int j = 0; j<frame[0].length; j++){
                        if(frame[i][j]==null)continue;
                        if(getPlot(i+coords[0], j+coords[1]).isVisible){
                            GL11.glColor4d(frame[i][j].red, frame[i][j].green, frame[i][j].blue, frame[i][j].alpha);
                            drawRect((coords[0]+i)*Plot.size, (coords[1]+j)*Plot.size, (coords[0]+i+1)*Plot.size, (coords[1]+j+1)*Plot.size, 0);
                        }
                    }
                }
            }
        }
        if(powerups.containsKey(PlotType.FLASHBANG)){
            double flashbang = powerups.get(PlotType.FLASHBANG);
            double flashbangDuration = PlotType.FLASHBANG.getTime();
            GL11.glColor4d(1, 1, 1, flashbang*2/flashbangDuration-1);
            drawRect(0,0,Display.getWidth(),Display.getHeight(), 0);
        }
        if(player!=null&&player.dead){
            GL11.glColor4d(1, 0, 0, .5+(Player.deadTime-player.deadTimer)/(double)Player.deadTime/2);
            drawRect(0,0,Display.getWidth(),Display.getHeight(), 0);
        }
    }
    public void tick(){
        if(player!=null&&player.dead){
            player.deadTimer--;
            if(player.deadTimer<0){
                fail();
            }
            return;
        }
        for(Animation anim : animations.keySet()){
            anim.timer++;
            if(anim.timer>=anim.delay){
                anim.frame++;
                anim.timer = 0;
            }
            if(anim.collisionSettings==0){
                if(anim.underneath==null){
                    anim.underneath = new Plot[anim.anim.get(0).length][anim.anim.get(0)[0].length];
                    for(int i = 0; i<anim.underneath.length; i++){
                        for(int j = 0; j<anim.underneath[0].length; j++){
                            anim.underneath[i][j] = getPlot(i+animations.get(anim)[0], j+animations.get(anim)[1]);
                        }
                    }
                }
                int f = Math.min(anim.anim.size()-1, anim.frame);
                Plot[][] frame = anim.anim.get(f);
                int[] coords = animations.get(anim);
                for(int i = 0; i<frame.length; i++){
                    for(int j = 0; j<frame[0].length; j++){
                        if(frame[i][j].alpha==0){
                            world[i+coords[0]][j+coords[1]].setType(anim.underneath[i][j].type);
                        }else{
                            world[i+coords[0]][j+coords[1]].setType(frame[i][j].type);
                        }
                    }
                }
            }
            if(anim.loopSettings>1){
                int loopTime = 255-anim.loopSettings;
                if(anim.frame>=anim.anim.size()+loopTime){
                    anim.frame = 0;
                }
            }
        }
        if(powerups.containsKey(PlotType.TELEPORT)){
            Plot p = Core.world.getPlot(Core.world.cursorX/Plot.size, Core.world.cursorY/Plot.size);
            if(p.type==PlotType.PATH&&p.isVisible){
                powerups.remove(PlotType.TELEPORT);
                Core.world.teleportPlayer(Core.world.cursorX/Plot.size, Core.world.cursorY/Plot.size);
            }
        }
        ArrayList<PlotType> rem = new ArrayList<>();
        for(PlotType p : powerups.keySet()){
            powerups.put(p, powerups.get(p)-1);
            if(powerups.get(p)<=0){
                rem.add(p);
            }
        }
        for(PlotType p : rem){
            powerups.remove(p);
        }
        for(Plot[] plots : world){
            for(Plot p : plots){
                if(p!=null)
                p.tick();
            }
        }
        for(Player player : players){
            player.tick();
        }
    }
    public void addEnemy(int x, int y){
        players.add(new PlayerEnemy(x, y, this));
    }
    void addFollowingEnemy(int x, int y){
        players.add(new PlayerFollowingEnemy(x, y, this));
    }
    public void setPlayerLocation(int x, int y){
        players.remove(player);
        player = new PlayerLocal(x, y, this);
        players.add(player);
    }
    public void teleportPlayer(int x, int y){
        player.x = x;
        player.y = y;
        player.path.clear();
    }
    public Plot getPlot(int x, int y){
        if(x<0||y<0)return null;
        return world[x][y];
    }
    public void attemptPath(Player player, int x, int y, boolean b){
        player.path = RouteFinder.findRoute(world, new int[]{player.x, player.y}, new int[]{x, y}, b);
    }
    public void nextLevel(){
        if(level==1){
            Core.gui.open(new MenuMain(Core.gui));
            return;
        }
        if(custom!=null){
            Core.gui.open(new MenuLore(Core.gui, custom, true));
            return;
        }
        player.x = player.y = -20;
        Core.gui.open(new MenuLore(Core.gui, new MenuMaze(Core.gui, level+1), "end "+level+".txt"));
    }
    public void event(){
        if(custom!=null){
            Core.gui.open(new MenuMaze(Core.gui, custom, event+1));
            return;
        }
        MenuMaze maze = (MenuMaze) Core.gui.menu;
        if(maze.tick>31){
            Core.gui.open(new MenuMaze(Core.gui, level, event+1));
        }
    }
    private void addAnim(int x, int y, int red, int green, int blue, int alpha){
        Animation animation = Animation.loadAnim(this, red, green, blue, alpha);
        if(animation==null){
            return;
        }
        x-=animation.latchX;
        y-=animation.latchY;
        animations.put(animation, new int[]{x,y});
    }
    private void addAnim(File file, int x, int y, int red, int green, int blue, int alpha){
        Animation animation = Animation.loadAnim(this, file, red, green, blue, alpha);
        if(animation==null){
            return;
        }
        x-=animation.latchX;
        y-=animation.latchY;
        animations.put(animation, new int[]{x,y});
    }
    public void fail(){
        Core.gui.open(new MenuLore(Core.gui, new MenuMaze(Core.gui, level), "fail "+player.death+".txt"));
    }
}