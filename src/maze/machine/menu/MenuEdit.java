package maze.machine.menu;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import javax.imageio.ImageIO;
import maze.machine.Animation;
import maze.machine.Core;
import maze.machine.EditableWorld;
import maze.machine.Main;
import maze.machine.Plot;
import maze.machine.PlotType;
import maze.machine.World;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
import simplelibrary.opengl.gui.components.MenuComponentSlider;
import simplelibrary.opengl.gui.components.MenuComponentTextBox;
import simplelibraryextended.opengl.gui.components.MenuComponentTextArea;
public class MenuEdit extends Menu{
    public ArrayList<World> world = new ArrayList<>();
    public PlotType[] selectableTypes = new PlotType[]{
        PlotType.PATH,PlotType.WALL,
        PlotType.PLAYER, PlotType.GOAL,
        PlotType.WATER,PlotType.WOOD,
        PlotType.DOOR,PlotType.KEY,
        PlotType.PIT,
        PlotType.PATHVISION, PlotType.WALLSEEALL,
        PlotType.ENEMY, PlotType.ENEMY_FOLLOWING,
        PlotType.EVENT,PlotType.TOUCH_EVENT,
        PlotType.FLASHBANG,PlotType.SCANNERSWEEP,PlotType.TELEPORT,PlotType.XRAY,
        PlotType.ANIM};
    public PlotType selectedPlot = PlotType.PATH;
    public int selectedIndex = 0;
    public MenuComponentSlider r,g,b,a;
    private int selectedColor = 0;
    public int event = 0;
    public MenuComponentTextArea preLore, endLore;
    public MenuComponentTextBox name;
    public MenuComponentButton animations,test,exit,delete;
    public ArrayList<Animation> anims = new ArrayList<>();
    private ArrayList<int[]> anms = new ArrayList<>();
    public MenuEdit(GUI gui, Menu parent){
        super(gui, parent);
        world.add(new EditableWorld());
        r = add(new MenuComponentSlider(Display.getWidth()/2-400, -120, 800, 40, 0, 255, 64, true));
        g = add(new MenuComponentSlider(Display.getWidth()/2-400, -80, 800, 40, 0, 255, 64, true));
        b = add(new MenuComponentSlider(Display.getWidth()/2-400, -40, 800, 40, 0, 255, 64, true));
        a = add(new MenuComponentSlider(Display.getWidth()/2-400, -40, 800, 40, 0, 255, 255, true));
        preLore = add(new MenuComponentTextArea(-400, 0, 400, Display.getHeight()/2, 20, new String[0], true));
        endLore = add(new MenuComponentTextArea(-400, Display.getHeight()/2, 400, Display.getHeight()/2, 20, new String[0], true));
        name = add(new MenuComponentTextBox(endLore.width, -50, Display.getWidth()-endLore.width, 50, "Unnamed", true));
        animations = add(new MenuComponentButton(name.x, Display.getHeight(), name.width, name.height, "Animations", true, true, "/textures/gui/button"));
        test = add(new MenuComponentButton(name.x, Display.getHeight(), name.width, name.height, "Test", true, true, "/textures/gui/button"));
        exit = add(new MenuComponentButton(name.x, Display.getHeight(), name.width, name.height, "Exit", true, true, "/textures/gui/button"));
        delete = add(new MenuComponentButton(name.x, -Display.getHeight(), name.width, name.height, "Delete", true, true, "/textures/gui/button"));
    }
    public MenuEdit(GUI gui, Menu parent, File file){
        super(gui, parent);
        world = Core.loadWorlds(file);
        r = add(new MenuComponentSlider(Display.getWidth()/2-400, -120, 800, 40, 0, 255, 64, true));
        g = add(new MenuComponentSlider(Display.getWidth()/2-400, -80, 800, 40, 0, 255, 64, true));
        b = add(new MenuComponentSlider(Display.getWidth()/2-400, -40, 800, 40, 0, 255, 64, true));
        a = add(new MenuComponentSlider(Display.getWidth()/2-400, -40, 800, 40, 0, 255, 255, true));
        preLore = add(new MenuComponentTextArea(-400, 0, 400, Display.getHeight()/2, 20, new String[0], true));
        endLore = add(new MenuComponentTextArea(-400, Display.getHeight()/2, 400, Display.getHeight()/2, 20, new String[0], true));
        name = add(new MenuComponentTextBox(endLore.width, -50, Display.getWidth()-endLore.width, 50, file.getName(), true));
        animations = add(new MenuComponentButton(name.x, Display.getHeight(), name.width, name.height, "Animations", true, true, "/textures/gui/button"));
        test = add(new MenuComponentButton(name.x, Display.getHeight(), name.width, name.height, "Test", true, true, "/textures/gui/button"));
        exit = add(new MenuComponentButton(name.x, Display.getHeight(), name.width, name.height, "Exit", true, true, "/textures/gui/button"));
        delete = add(new MenuComponentButton(name.x, -Display.getHeight(), name.width, name.height, "Delete", true, true, "/textures/gui/button"));
        try{
            File root = new File(Main.getAppdataRoot(), "\\editor\\"+name.text+"\\lore");
            File start = new File(root, "begin.txt");
            if(start.exists()){
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(start)));
                String line;
                while((line = reader.readLine())!=null){
                    preLore.text.add(line);
                }
                reader.close();
            }
            File end = new File(root, "end.txt");
            if(end.exists()){
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(end)));
                String line;
                while((line = reader.readLine())!=null){
                    endLore.text.add(line);
                }
                reader.close();
            }
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
    @Override
    public void renderBackground(){
        if(delete.y>0){
            if(Mouse.isButtonDown(0)&&delete.isSelected&&Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)&&Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){
                double y = (Display.getHeight()-Mouse.getY())-delete.height/2;
                delete.y = Math.min(exit.y-delete.height, Math.max(name.y+name.height, y));
                if(delete.y>=test.y-delete.height){
                    delete();
                }
            }else{
                if(delete.isSelected){
                    selected = null;
                    delete.isSelected = false;
                }
                delete.y = name.y+name.height;
            }
        }
    }
    @Override
    public void render(int millisSinceLastTick){
        DO:do{
            if(Mouse.isButtonDown(1)&&!Keyboard.isKeyDown(Keyboard.KEY_SPACE)&&preLore.x<0){
                int X = world.get(event).cursorX/20;
                int Y = world.get(event).cursorY/20;
                for (Iterator<int[]> it = anms.iterator(); it.hasNext();) {
                    int[] anm = it.next();
                    if(Math.abs(anm[0]-X)<=1&&Math.abs(anm[1]-Y)<=1){
                        Plot p = new Plot(world.get(event).world[anm[0]][anm[1]-1]);
                        PlotType type = p.type;
                        double red = p.red;
                        double green = p.green;
                        double blue = p.blue;
                        double alpha = p.alpha;
                        for(int i = anm[0]-1; i<=anm[0]+1; i++){
                            for(int j = anm[1]-1; j<=anm[1]+1; j++){
                                world.get(event).world[i][j].setType(type);
                                world.get(event).world[i][j].red = red;
                                world.get(event).world[i][j].green = green;
                                world.get(event).world[i][j].blue = blue;
                                world.get(event).world[i][j].alpha = alpha;
                            }
                        }
                        it.remove();
                        break;
                    }
                }
            }
            if(Mouse.isButtonDown(0)&&!Keyboard.isKeyDown(Keyboard.KEY_SPACE)&&preLore.x<0){
                int X = world.get(event).cursorX/20;
                int Y = world.get(event).cursorY/20;
                for(int[] anm : anms){
                    if(selectedPlot==PlotType.ANIM){
                        if(Math.abs(anm[0]-X)<=2&&Math.abs(anm[1]-Y)<=2){
                            break DO;
                        }
                    }else{
                        if(Math.abs(anm[0]-X)<=1&&Math.abs(anm[1]-Y)<=1){
                            break DO;
                        }
                    }
                }
                if(selectedPlot==PlotType.PLAYER){
                    for (Plot[] plots : world.get(event).world) {
                        for (Plot plot : plots) {
                            if (plot.type == PlotType.PLAYER) {
                                plot.setType(PlotType.PATH);
                            }
                        }
                    }
                }
                if(selectedPlot==PlotType.ANIM){
                    if(X>0&&Y>0&&X<world.get(event).world.length-1&&Y<world.get(event).world[0].length-1){
                        world.get(event).world[X][Y-1].setType(world.get(event).world[X][Y].type);
                        world.get(event).world[X][Y-1].red = world.get(event).world[X][Y].red;
                        world.get(event).world[X][Y-1].green = world.get(event).world[X][Y].green;
                        world.get(event).world[X][Y-1].blue = world.get(event).world[X][Y].blue;
                        world.get(event).world[X][Y-1].alpha = world.get(event).world[X][Y].alpha;
                        world.get(event).world[X-1][Y-1].setType(PlotType.ANIM);
                        world.get(event).world[X+1][Y-1].setType(PlotType.ANIM);
                        world.get(event).world[X-1][Y+1].setType(PlotType.ANIM);
                        world.get(event).world[X+1][Y+1].setType(PlotType.ANIM);
                        world.get(event).world[X][Y].setType(PlotType.WALL);
                        double red = r.getValue()/255d;
                        double green = g.getValue()/255d;
                        double blue = b.getValue()/255d;
                        double alpha = a.getValue()/255d;
                        world.get(event).world[X][Y].red = red;
                        world.get(event).world[X][Y].green = green;
                        world.get(event).world[X][Y].blue = blue;
                        world.get(event).world[X][Y].alpha = alpha;
                        anms.add(new int[]{X,Y});
                    }
                }else{
                    world.get(event).getPlot(world.get(event).cursorX/20, world.get(event).cursorY/20).setType(selectedPlot);
                    if(selectedPlot==PlotType.WALL){
                        double red = r.getValue()/255d;
                        double green = g.getValue()/255d;
                        double blue = b.getValue()/255d;
                        double alpha = a.getValue()/255d;
                        world.get(event).getPlot(world.get(event).cursorX/20, world.get(event).cursorY/20).red = red;
                        world.get(event).getPlot(world.get(event).cursorX/20, world.get(event).cursorY/20).green = green;
                        world.get(event).getPlot(world.get(event).cursorX/20, world.get(event).cursorY/20).blue = blue;
                        world.get(event).getPlot(world.get(event).cursorX/20, world.get(event).cursorY/20).alpha = alpha;
                    }
                }
            }
        }while(false);
        world.get(event).render(millisSinceLastTick);
        GL11.glColor4d(0, 0, 0, 1);
        drawRect(9, 9, 31, 31, 0);
        double red = selectedPlot.getRed();
        double green = selectedPlot.getGreen();
        double blue = selectedPlot.getBlue();
        double alpha = selectedPlot.getAlpha();
        if(selectedPlot==PlotType.WALL||selectedPlot==PlotType.ANIM){
            red = r.getValue()/255d;
            green = g.getValue()/255d;
            blue = b.getValue()/255d;
            alpha = a.getValue()/255d;
        }
        GL11.glColor4d(red, green, blue, alpha);
        drawRect(10, 10, 30, 30, 0);
        r.y = preLore.x<0&&(selectedPlot==PlotType.WALL||selectedPlot==PlotType.ANIM)&&Keyboard.isKeyDown(Keyboard.KEY_SPACE)?0:-120;
        g.y = preLore.x<0&&(selectedPlot==PlotType.WALL||selectedPlot==PlotType.ANIM)&&Keyboard.isKeyDown(Keyboard.KEY_SPACE)?40:-80;
        b.y = preLore.x<0&&(selectedPlot==PlotType.WALL||selectedPlot==PlotType.ANIM)&&Keyboard.isKeyDown(Keyboard.KEY_SPACE)?80:-40;
        a.y = preLore.x<0&&(selectedPlot==PlotType.WALL||selectedPlot==PlotType.ANIM)&&Keyboard.isKeyDown(Keyboard.KEY_SPACE)?120:-40;
        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)&&selectedPlot==PlotType.WALL||selectedPlot==PlotType.ANIM){
            if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)){
                MenuComponentSlider slider = selectedColor==0?r:(selectedColor==1?g:(selectedColor==2?b:a));
                slider.setValue(slider.getValue()-1);
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)){
                MenuComponentSlider slider = selectedColor==0?r:(selectedColor==1?g:(selectedColor==2?b:a));
                slider.setValue(slider.getValue()+1);
            }
        }
        if(world.size()>1){
            GL11.glColor4d(PlotType.TOUCH_EVENT.getRed(), PlotType.TOUCH_EVENT.getGreen(), PlotType.TOUCH_EVENT.getBlue(), PlotType.TOUCH_EVENT.getAlpha());
            drawRect(0, 0, Display.getWidth(), 2, 0);
            GL11.glColor4d(PlotType.EVENT.getRed(), PlotType.EVENT.getGreen(), PlotType.EVENT.getBlue(), PlotType.EVENT.getAlpha());
            drawRect(event/(double)world.size()*Display.getWidth(), 0, Display.getWidth(), 2, 0);
        }
        GL11.glColor4d(1, 1, 1, 1);
        DO:do{
            if(selectedPlot==PlotType.WALL){
                for(PlotType p : selectableTypes){
                    if(red==p.getRed()&&green==p.getGreen()&&blue==p.getBlue()){
                        drawText(40, 0, Display.getWidth(), 20, p.toString());
                        break DO;
                    }
                }
            }
            drawText(40, 0, Display.getWidth(), 20, selectedPlot.toString());
        }while(false);
        drawRect(preLore.x, 0, preLore.width+preLore.x, Display.getHeight(), 0);
        super.render(millisSinceLastTick);
    }
    @Override
    public void tick(){
        if(world.isEmpty()){
            world.add(new EditableWorld());
        }
        world.get(event).tick();
    }
    @Override
    public void mouseEvent(int button, boolean pressed, float x, float y, float xChange, float yChange, int wheelChange){
        super.mouseEvent(button, pressed, x, y, xChange, yChange, wheelChange);
        if(preLore.x<0){
            world.get(event).cursorX = (int)x;
            world.get(event).cursorY = (int)y;
        }
    }
    @Override
    public void keyboardEvent(char character, int key, boolean pressed, boolean repeat){
        if(pressed&&!repeat){
            switch(key){
                case Keyboard.KEY_CAPITAL:
                    if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
                        if(preLore.x>=0){
                            name.y = -name.height;
                            preLore.x = -preLore.width;
                            endLore.x = -endLore.width;
                            animations.y = Display.getHeight();
                            test.y = Display.getHeight();
                            exit.y = Display.getHeight();
                            delete.y = -Display.getHeight();
                        }else{
                            name.y = 0;
                            preLore.x = 0;
                            endLore.x = 0;
                            exit.y = Display.getHeight()-exit.height;
                            animations.y = exit.y-animations.height;
                            test.y = animations.y-test.height;
                            delete.y = name.y+name.height;
                        }
                    }   break;
                case Keyboard.KEY_TAB:
                    if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)&&Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)&&event>=1){
                        event--;
                    }else if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)&&!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)&&(hasEvent()||world.size()>event+1)){
                        event++;
                        if(event>=world.size()){
                            world.add(new EditableWorld(world.get(world.size()-1)));
                        }
                    }else if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)&&(selectedPlot==PlotType.WALL||selectedPlot==PlotType.ANIM)){
                        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
                            selectedColor--;
                            if(selectedColor<0)selectedColor = 3;
                        }else{
                            selectedColor++;
                            if(selectedColor>3)selectedColor = 0;
                        }
                        r.selectedColor = r.color = selectedColor==0?new Color(128, 128, 255):Color.WHITE;
                        g.selectedColor = g.color = selectedColor==1?new Color(128, 128, 255):Color.WHITE;
                        b.selectedColor = b.color = selectedColor==2?new Color(128, 128, 255):Color.WHITE;
                        a.selectedColor = a.color = selectedColor==3?new Color(128, 128, 255):Color.WHITE;
                    }else{
                        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
                            selectedIndex--;
                        }else{
                            selectedIndex++;
                        }
                        if(selectedIndex>=selectableTypes.length)selectedIndex = 0;
                        if(selectedIndex<0)selectedIndex = selectableTypes.length-1;
                        selectedPlot = selectableTypes[selectedIndex];
                    }   break;
                case Keyboard.KEY_BACK:
                    if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)&&Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)&&event==world.size()-1&&world.size()>1){
                        event--;
                        world.remove(world.size()-1);
                    }   break;
                default:
                    break;
            }
        }
        if(key==Keyboard.KEY_M&&pressed&&!repeat){
            Core.music = !Core.music;
        }
        super.keyboardEvent(character, key, pressed, repeat);
    }
    private boolean hasEvent(){
        for(Plot[] plots : world.get(event).world){
            for(Plot plot : plots){
                if(plot.type==PlotType.EVENT||plot.type==PlotType.TOUCH_EVENT)return true;
            }
        }
        return false;
    }
    public void save(){
        String name = this.name.text;
        try {
            File images = new File(Main.getAppdataRoot(), "\\editor\\"+name+"\\images");
            if(images.exists()){
                images.delete();
            }
            images.mkdirs();
            File lore = new File(Main.getAppdataRoot(), "\\editor\\"+name+"\\lore");
            if(lore.exists()){
                lore.delete();
            }
            lore.mkdirs();
            for(int i = 0; i<world.size(); i++){
                World w = world.get(i);
                File file = new File(images, "\\"+i+".png");
                BufferedImage image = new BufferedImage(w.world.length, w.world[0].length, BufferedImage.TYPE_INT_ARGB);
                for(int x = 0; x<w.world.length; x++){
                    for(int y = 0; y<w.world[x].length; y++){
                        float R = (float) w.world[x][y].red;
                        float G = (float) w.world[x][y].green;
                        float B = (float) w.world[x][y].blue;
                        float A = (float) w.world[x][y].alpha;
                        image.setRGB(x, y, new Color(R, G, B, A).getRGB());
                    }
                }
                try{
                    ImageIO.write(image, "png", file);
                }catch(IOException ex){
                    throw new RuntimeException(ex);
                }
            }
            File beginLore = new File(lore, "begin.txt");
            beginLore.createNewFile();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(beginLore)));
            for(String str : preLore.text){
                writer.write(str+"\n");
            }
            writer.close();
            File finishLore = new File(lore, "end.txt");
            finishLore.createNewFile();
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(finishLore)));
            for(String str : endLore.text){
                writer.write(str+"\n");
            }
            writer.close();
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        if(button==animations){
            gui.open(new MenuAnimations(gui, this, name.text));
        }
        if(button==test){
            save();
            gui.open(new MenuLore(gui, new File(Main.getAppdataRoot(), "\\editor\\"+name.text), false));
        }
        if(button==exit){
            save();
            gui.open(new MenuOpenEditor(gui, new MenuMain(gui)));
        }
    }
    private void delete(){
        File file = new File(Main.getAppdataRoot(), "\\editor\\"+name.text+"\\");
        deleteFolder(file);
        gui.open(new MenuOpenEditor(gui, new MenuMain(gui)));
    }
    private void deleteFolder(File file){
        if(!file.exists()){
            return;
        }
        if(file.isDirectory()){
            for(File f : file.listFiles()){
                deleteFolder(f);
            }
            file.delete();
        }else{
            file.delete();
        }
    }
}