package maze.machine;
import java.io.File;
/**
 * Returns filepaths of level images or lore.
 * @author James
 */
public class Levels{
    public static String getLorePath(String lore){
        String path = "/maze/machine/lore/"+lore;
        return path;
    }
    public static String getLevelPath(int level, int event){
        String path = "/levels/level "+level+"/level/"+event+".png";
        if(World.class.getResourceAsStream(path)==null){
            path = "/maze/machine/images/lvl "+level+"/"+event+".png";
        }
        if(World.class.getResourceAsStream(path)==null){
            path = "/maze/machine/images/lvl "+level+(event==0?"":"-"+event)+".png";
        }
        return path;
    }
    /**
     * Gets the path to the lore file for a level
     * @param level which level to get lore for
     * @param event what event to search for lore of (0 = before level starts, 1 = before first event)
     * @return the path to the Lore file.
     */
    public static String getLorePath(int level, int event){
        String path = "/levels/level "+level+"/lore/"+event+".txt";
        if(World.class.getResourceAsStream(path)==null){
            path = "/maze/machine/lore/lvl "+level+"/"+event+".txt";
        }
        if(World.class.getResourceAsStream(path)==null){
            path = "/maze/machine/lore/lvl "+level+(event==0?"":"-"+event)+".txt";
        }
        if(World.class.getResourceAsStream(path)==null){
            path = "/maze/machine/lore/"+(event==0?"start":"end")+" "+level+".txt";
        }
        return path;
    }
    public static String getDeathMessagePath(DeathType type){
        String path = "/deaths/death "+type+".txt";
        return path;
    }
    static String getLevelPath(File saveFolder, int event){
        if(new File(saveFolder, "\\level\\"+event+".png").exists()){
            return saveFolder.getAbsolutePath()+"\\level\\"+event+".png";
        }
        return saveFolder.getAbsolutePath()+"\\images\\"+event+".png";
    }
}