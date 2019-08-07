package maze.machine;
import maze.machine.entity.PlayerEnemy;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.Renderer2D;
public class Plot extends Renderer2D{
    public boolean isVisible = false;
    int visibilityTimer = 0;
    public boolean isPathHidden = false;
    public boolean xrayed = false;
    public PlotType type;
    public final int x;
    public final int y;
    public int danger = 0;
    public static final int size = 20;
    public World world;
    public double red;
    public double green;
    public double blue;
    public double alpha;
    public Plot(int x, int y, World world){
        this.x = x;
        this.y = y;
        setType(PlotType.WALL);
        this.world = world;
    }
    public Plot(Plot p){
        x = p.x;
        y = p.y;
        type = p.type;
        world = p.world;
        red = p.red;
        green = p.green;
        blue = p.blue;
        alpha = p.alpha;
    }
    public Plot processType(World world, int argb){
        red = type.getRed();
        green = type.getGreen();
        blue = type.getBlue();
        alpha = type.getAlpha();
        switch(argb){
            case 0xFF808080://Grey, path
                setType(PlotType.PATH);
                break;
            case 0xFF0026FF://Blue, water
                setType(PlotType.WATER);
                break;
            case 0xFFFFD800://Yellow, key
                setType(PlotType.KEY);
                break;
            case 0xFF7F3300://Brown, wood
                setType(PlotType.WOOD);
                break;
            case 0xFF007F0E://Green, goal
                setType(PlotType.GOAL);
                break;
            case 0xFF7F6A00://Tan-ish, door
                setType(PlotType.DOOR);
                break;
            case 0xFF7F006E://purple, event
                setType(PlotType.EVENT);
                break;
            case 0xFFFF00DC://purplish, touch event
                setType(PlotType.TOUCH_EVENT);
                break;
            case 0xFF202020://Really dark grey- bottomless pit!
                setType(PlotType.PIT);
                break;
            case 0xFF00FFFF://Turqoise- see from it
                isVisible = true;
                setType(PlotType.PATHVISION);
                break;
            case 0xFF008080://Cyan - see everything
                isVisible = true;
                setType(PlotType.WALLSEEALL);
                break;
            case 0xFFFFFFFF://White- the player
                isVisible = true;
                setType(PlotType.PATH);
                if(world!=null){
                    world.setPlayerLocation(x, y);
                }
                break;
            case 0xFFFF0000://Red- enemy
                setType(PlotType.PATH);
                if(world!=null){
                    world.addEnemy(x, y);
                }
                break;
            case 0x80FF0000://50% transparent red- persuing enemy
                setType(PlotType.PATH);
                if(world!=null){
                    world.addFollowingEnemy(x, y);
                }
                break;
            case 0xFFFFFF7F://Creamish - Flashbang (F)
                setType(PlotType.FLASHBANG);
                break;
            case 0xFF59B1FF://Greyish blue - Scanner sweep
                setType(PlotType.SCANNERSWEEP);
                break;
            case 0xFFA500FF://Mana purple - Teleport (T)
                setType(PlotType.TELEPORT);
                break;
            case 0xFFE5E5E5://Very light gray - X-ray vision (X)
                setType(PlotType.XRAY);
                break;
            case 0xFFFF6A00://Orange, Animation Latch
                setType(PlotType.ANIM);
                break;
            default:
                setType(PlotType.WALL);
                if(alpha==0&&red==0&&green==0&&blue==0){
                    break;
                }
                alpha = ((argb>>24)&255)/255f;
                red = ((argb>>16)&255)/255f;
                green = ((argb>>8)&255)/255f;
                blue = (argb&255)/255f;
                break;
        }
        return this;
    }
    public Plot setType(PlotType type){
        this.type = type;
        red = type.getRed();
        green = type.getGreen();
        blue = type.getBlue();
        alpha = type.getAlpha();
        return this;
    }
    public void render(){
        if(type==PlotType.PATHVISION&&Core.world==world&&world!=null){
            for(int i = x==0?0:-1; i<(x==Core.GRID_WIDTH-1?1:2); i++){
                for(int j = y==0?0:-1; j<(y==Core.GRID_HEIGHT-1?1:2); j++){
                    world.getPlot(x+i, y+j).isVisible = true;
                }
            }
            setType(PlotType.PATH);
        }
        if(!isVisible&&(world!=null&&!world.powerups.containsKey(PlotType.SCANNERSWEEP))&&visibilityTimer<=0){
            return;
        }
        GL11.glColor4d(red, green, blue, alpha);
        if(world!=null&&world.powerups.containsKey(PlotType.SCANNERSWEEP)&&!isVisible){
            double scanner = world.powerups.get(PlotType.SCANNERSWEEP);
            double scannerDuration = PlotType.SCANNERSWEEP.getTime();
            GL11.glColor4d(red, green, blue, alpha*(scanner/scannerDuration));
        }else if(visibilityTimer>0&&!isVisible){
            GL11.glColor4d(red, green, blue, alpha*(visibilityTimer/20d));
        }
        drawRect(x*size, y*size, x*size+size, y*size+size, 0);
        if(isPathHidden&&type==PlotType.PATH&&(world!=null&&!world.powerups.containsKey(PlotType.SCANNERSWEEP))){
            GL11.glColor4d(0, 0, 0, .25);
            drawRect(x*size, y*size, x*size+size, y*size+size, 0);
        }else{
            if(danger>0){
                GL11.glColor4d(1, 0, 0, Math.min(.875, danger/(PlayerEnemy.VISION_DISTANCE*1.25)));
                if(world!=null&&world.powerups.containsKey(PlotType.SCANNERSWEEP)&&!isVisible){
                    double scanner = world.powerups.get(PlotType.SCANNERSWEEP);
                    double scannerDuration = PlotType.SCANNERSWEEP.getTime();
                    GL11.glColor4d(1, 0, 0, Math.min(.875, danger/(PlayerEnemy.VISION_DISTANCE*1.25)*(scanner/scannerDuration)));
                }
                drawRect(x*size, y*size, x*size+size, y*size+size, 0);
            }
        }
        int x = Mouse.getX()-this.x*size, y = (Display.getHeight()-Mouse.getY())-this.y*size;
        if(x>=0&&x<size&&y>=0&&y<size){
            GL11.glColor4f(1, 1, 1, 0.5f);
            drawRect(this.x*size, this.y*size, this.x*size+size, this.y*size+size, 0);
        }
        if(xrayed){
            if(!type.canSeeOver()){
                GL11.glColor4d(1, 1, 1, 0.15);
                drawRect(this.x*size, this.y*size, this.x*size+size, this.y*size+size, 0);
            }
        }
        GL11.glColor4f(1, 1, 1, 1);
    }
    public void tick(){
        if(danger>0){
            danger=0;
        }
        if(visibilityTimer>0){
            visibilityTimer--;
        }
    }
}
