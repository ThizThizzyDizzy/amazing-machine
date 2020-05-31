package maze.machine;
import maze.machine.entity.PlayerLocal;
public enum PlotType{
    WALL           (0xFF404040L, "Wall", false, false),
    WALLSEEALL     (0xFF008080L, "Wall (See Everything)", false, false),
    PATH           (0xFF808080L, "Path", true, true),
    PATHVISION     (0xFF00FFFFL, "Path (Vision)", true, true),
    WATER          (0xFF0026FFL, "Water", true, true),
    KEY            (0xFFFFD800L, "Key", true, true),
    WOOD           (0xff7f3300L, "Wood", true, true),
    GOAL           (0xFF007F0EL, "Goal", true, true),
    DOOR           (0xFF7F6A00L, "Door", false, true),
    PIT            (0xFF202020L, "Pit", true, false),
    PLAYER         (0xFFFFFFFFL, "Player", true, false),
    ENEMY          (0xFFFF0000L, "Enemy", true, false),
    ENEMY_FOLLOWING(0x7FFF0000L, "Following Enemy", true, false),
    EVENT          (0xFF7F006EL, "Event", false, true),
    TOUCH_EVENT    (0xFFFF00DCL, "Button", false, true),
    FLASHBANG      (0xFFFFFF7FL, "Flashbang", 'F', .05, 200),
    SCANNERSWEEP   (0xFF59B1FFL, "Scanner Sweep", 'R', .03, 150),
    TELEPORT       (0xFFA500FFL, "Teleport", 'T', .02, 200),
    XRAY           (0xFFE5E5E5L, "Xray", 'X', .04, 250),
    ANIM           (0xFFFF6A00L, "Animation", true, false);
    private double alpha;
    private double blue;
    private boolean canSeeOver;
    private double green;
    private double red;
    private boolean pathable;
    private String name;
    private char activationLetter = ' ';//Item
    public double defaultChance;
    private double time;
    private PlotType(long color, String name, boolean canSeeOver, boolean pathable){
        alpha = ((color>>24)&255)/255d;
        red = ((color>>16)&255)/255d;
        green = ((color>>8)&255)/255d;
        blue = (color&255)/255d;
        this.name = name;
        this.canSeeOver = canSeeOver;
        this.pathable = pathable;
    }
    private PlotType(long color, String name, char activationLetter, double defaultChance, double time){//item
        this(color, name, true, true);
        this.activationLetter = activationLetter;
        this.defaultChance = defaultChance;
        this.time = time;
    }
    public double getRed(){
        return red;
    }
    public double getGreen(){
        return green;
    }
    public double getBlue(){
        return blue;
    }
    public double getAlpha(){
        return alpha;
    }
    public boolean canSeeOver(){
        return canSeeOver;
    }
    public boolean pathable(){
        return pathable;
    }
    public boolean pathable(PlayerLocal player){
        if(this==WATER){
            if(player.wood<=0)return false;
        }
        if(this==DOOR){
            if(player.keys<=0)return false;
        }
        return pathable;
    }
    public char getActivationLetter(){
        return activationLetter;
    }
    public boolean isItem(){
        return activationLetter!=' ';
    }
    @Override
    public String toString(){
        if(name!=null&&!name.isEmpty()){
            return name;
        }
        return super.toString();
    }
    public double getTime(){
        return time;
    }
}