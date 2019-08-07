package maze.machine.menu;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import maze.machine.Core;
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
public class MenuEditAnimation extends Menu{
    public ArrayList<Plot[][]> anim = new ArrayList<>();
    public PlotType[] selectableTypes = new PlotType[]{
        PlotType.PATH,PlotType.WALL,
        PlotType.GOAL,
        PlotType.WATER,PlotType.WOOD,
        PlotType.DOOR,PlotType.KEY,
        PlotType.PIT,
        PlotType.EVENT,PlotType.TOUCH_EVENT,
        PlotType.FLASHBANG,PlotType.SCANNERSWEEP,PlotType.TELEPORT,PlotType.XRAY,
        PlotType.ANIM};
    public PlotType selectedPlot = PlotType.PATH;
    public int selectedIndex = 0;
    public MenuComponentSlider r,g,b,a;
    public MenuComponentSlider delay,start,collision,loop;
    private int selectedColor = 0;
    public int event = 0;
    public MenuComponentTextBox name;
    public MenuComponentButton exit,delete;
    private int[] latch = null;
    private final String saveName;
    public MenuEditAnimation(GUI gui, Menu parent, String saveName){
        super(gui, parent);
        anim.add(new Plot[Core.GRID_WIDTH][Core.GRID_HEIGHT]);
        r = add(new MenuComponentSlider(Display.getWidth()/2-400, -120, 800, 40, 0, 255, 64, true));
        g = add(new MenuComponentSlider(Display.getWidth()/2-400, -80, 800, 40, 0, 255, 64, true));
        b = add(new MenuComponentSlider(Display.getWidth()/2-400, -40, 800, 40, 0, 255, 64, true));
        a = add(new MenuComponentSlider(Display.getWidth()/2-400, -40, 800, 40, 0, 255, 255, true));
        delay = add(new MenuComponentSlider(Display.getWidth()/2-400, -120, 800, 40, 0, 255, 64, true));
        start = add(new MenuComponentSlider(Display.getWidth()/2-400, -80, 800, 40, 0, 255, 64, true));
        collision = add(new MenuComponentSlider(Display.getWidth()/2-400, -40, 800, 40, 0, 255, 64, true));
        loop = add(new MenuComponentSlider(Display.getWidth()/2-400, -40, 800, 40, 0, 255, 255, true));
        name = add(new MenuComponentTextBox(0, -50, Display.getWidth(), 50, "Unnamed", true));
        exit = add(new MenuComponentButton(name.x, Display.getHeight(), name.width, name.height, "Exit", true, true, "/textures/gui/button"));
        delete = add(new MenuComponentButton(name.x, -Display.getHeight(), name.width, name.height, "Delete", true, true, "/textures/gui/button"));
        this.saveName = saveName;
    }
    public MenuEditAnimation(GUI gui, Menu parent, String saveName, File file){
        super(gui, parent);
        this.saveName = saveName;
        r = add(new MenuComponentSlider(Display.getWidth()/2-400, -120, 800, 40, 0, 255, 64, true));
        g = add(new MenuComponentSlider(Display.getWidth()/2-400, -80, 800, 40, 0, 255, 64, true));
        b = add(new MenuComponentSlider(Display.getWidth()/2-400, -40, 800, 40, 0, 255, 64, true));
        a = add(new MenuComponentSlider(Display.getWidth()/2-400, -40, 800, 40, 0, 255, 255, true));
        int dela = 0, startingFrame = 0, collisionSettings = 0, loopSettings = 0, latchX = -1, latchY = -1;
        for(int i = 0; i<file.listFiles().length; i++){
            try{
                File f = new File(file, "\\"+i+".png");
                BufferedImage image = ImageIO.read(f);
                Plot[][] frame = new Plot[image.getWidth()][image.getHeight()];
                if(i==0){
                    for(int x = 0; x<image.getWidth()-2; x++){
                        for(int y = 0; y<image.getHeight()-2; y++){
                            int rgba = image.getRGB(x, y);
                            double a = ((rgba>>24)&255)/255d;
                            double r = ((rgba>>16)&255)/255d;
                            double g = ((rgba>>8)&255)/255d;
                            double b = (rgba&255)/255d;
                            if(latchX==-1&&latchY==-1&&r==PlotType.ANIM.getRed()&&g==PlotType.ANIM.getGreen()&&b==PlotType.ANIM.getBlue()&&a==PlotType.ANIM.getAlpha()){
                                int RGBA = image.getRGB(x+1, y);
                                int A = ((RGBA>>24)&255);
                                int R = ((RGBA>>16)&255);
                                int G = ((RGBA>>8)&255);
                                int B = (RGBA&255);
                                dela = R;
                                startingFrame = G;
                                collisionSettings = B;
                                loopSettings = A;
                                latchX = x+1;
                                latchY = y+1;
                            }
                        }
                    }
                }
                for(int x = 0; x<image.getWidth(); x++){
                    for(int y = 0; y<image.getHeight(); y++){
                        if(image.getRGB(x, y)==new Color(0, 0, 0, 0).getRGB()){
                            continue;
                        }
                        frame[x][y] = new Plot(x, y, null).processType(null, image.getRGB(x, y));
                    }
                }
                anim.add(frame);
            }catch(IOException ex){
                throw new RuntimeException(ex);
            }
        }
        delay = add(new MenuComponentSlider(Display.getWidth()/2-400, -120, 800, 40, 0, 255, dela, true));
        start = add(new MenuComponentSlider(Display.getWidth()/2-400, -80, 800, 40, 0, 255, startingFrame, true));
        collision = add(new MenuComponentSlider(Display.getWidth()/2-400, -40, 800, 40, 0, 255, collisionSettings, true));
        loop = add(new MenuComponentSlider(Display.getWidth()/2-400, -40, 800, 40, 0, 255, loopSettings, true));
        name = add(new MenuComponentTextBox(0, -50, Display.getWidth(), 50, file.getName(), true));
        exit = add(new MenuComponentButton(name.x, Display.getHeight(), name.width, name.height, "Exit", true, true, "/textures/gui/button"));
        delete = add(new MenuComponentButton(name.x, -Display.getHeight(), name.width, name.height, "Delete", true, true, "/textures/gui/button"));
        if(latchX>-1&&latchY>-1){
            latch = new int[]{latchX,latchY};
        }
    }
    @Override
    public void renderBackground(){
        if(delete.y>0){
            if(Mouse.isButtonDown(0)&&delete.isSelected&&Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)&&Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){
                double y = (Display.getHeight()-Mouse.getY())-delete.height/2;
                delete.y = Math.min(exit.y-delete.height, Math.max(name.y+name.height, y));
                if(delete.y>=exit.y-delete.height){
                    delete();
                }
            }else{
                if(delete.isSelected){
                    delete.isSelected = false;
                    selected = null;
                }
                delete.y = name.y+name.height;
            }
        }
    }
    @Override
    public void render(int millisSinceLastTick){
        DO:do{
            if(Mouse.isButtonDown(1)&&!Keyboard.isKeyDown(Keyboard.KEY_SPACE)&&exit.y>=Display.getHeight()){
                int X = Mouse.getX()/20;
                int Y = (Display.getHeight()-Mouse.getY())/20;
                if(latch!=null&&Math.abs(latch[0]-X)<=1&&Math.abs(latch[1]-Y)<=1){
                    for(int i = latch[0]-1; i<=latch[0]+1; i++){
                        for(int j = latch[1]-1; j<=latch[1]+1; j++){
                            updatePlot(event, i, j, event, latch[0], latch[1]-1);
                        }
                    }
                }else{
                    if(anim.get(event)[Mouse.getX()/20][(Display.getHeight()-Mouse.getY())/20]!=null){
                        anim.get(event)[Mouse.getX()/20][(Display.getHeight()-Mouse.getY())/20] = null;
                    }
                }
            }
            if(Mouse.isButtonDown(0)&&!Keyboard.isKeyDown(Keyboard.KEY_SPACE)&&exit.y>=Display.getHeight()){
                int X = Mouse.getX()/20;
                int Y = (Display.getHeight()-Mouse.getY())/20;
                if(latch!=null){
                    if(selectedPlot==PlotType.ANIM){
                        if(Math.abs(latch[0]-X)<=2&&Math.abs(latch[1]-Y)<=2){
                            break;
                        }
                    }else{
                        if((X==latch[0]||X==latch[0]-1)&&Y==latch[1]){
                        }else if((Y==latch[1]||Y==latch[1]-1)&&X==latch[0]){
                        }else if(Math.abs(latch[0]-X)<=1&&Math.abs(latch[1]-Y)<=1){
                            break;
                        }
                    }
                }
                if(selectedPlot==PlotType.PLAYER){
                    for (Plot[] plots : anim.get(event)) {
                        for (Plot plot : plots) {
                            if (plot.type == PlotType.PLAYER) {
                                plot.setType(PlotType.PATH);
                            }
                        }
                    }
                }
                if(selectedPlot==PlotType.ANIM){
                    if(X>0&&Y>0&&X<anim.get(event).length-1&&Y<anim.get(event)[0].length-1){
                        if(latch!=null){
                            for(int i = latch[0]-1; i<=latch[0]+1; i++){
                                for(int j = latch[1]-1; j<=latch[1]+1; j++){
                                    updatePlot(event, i, j, event, latch[0], latch[1]-1);
                                }
                            }
                            latch = null;
                        }
                        updatePlot(event, X, Y-1, event, X, Y);
                        updatePlot(event, X-1, Y-1, PlotType.ANIM);
                        updatePlot(event, X+1, Y-1, PlotType.ANIM);
                        updatePlot(event, X-1, Y+1, PlotType.ANIM);
                        updatePlot(event, X+1, Y+1, PlotType.ANIM);
                        double red = r.getValue()/255d;
                        double green = g.getValue()/255d;
                        double blue = b.getValue()/255d;
                        double alpha = a.getValue()/255d;
                        updatePlot(event, X, Y, PlotType.WALL, red, green, blue, alpha);
                        latch = new int[]{X,Y};
                    }
                }else{
                    if(selectedPlot==PlotType.WALL){
                        updatePlot(event, Mouse.getX()/20, (Display.getHeight()-Mouse.getY())/20, selectedPlot, r.getValue()/255d, g.getValue()/255d, b.getValue()/255d, a.getValue()/255d);
                    }else{
                        updatePlot(event, Mouse.getX()/20, (Display.getHeight()-Mouse.getY())/20, selectedPlot);
                    }
                }
            }
        }while(false);
        World w = new World();
        w.world = anim.get(event);
        w.vision = 3;
        w.render(millisSinceLastTick);
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
        r.y = exit.y>=Display.getHeight()&&(selectedPlot==PlotType.WALL||selectedPlot==PlotType.ANIM)&&Keyboard.isKeyDown(Keyboard.KEY_SPACE)?0:-120;
        g.y = exit.y>=Display.getHeight()&&(selectedPlot==PlotType.WALL||selectedPlot==PlotType.ANIM)&&Keyboard.isKeyDown(Keyboard.KEY_SPACE)?40:-80;
        b.y = exit.y>=Display.getHeight()&&(selectedPlot==PlotType.WALL||selectedPlot==PlotType.ANIM)&&Keyboard.isKeyDown(Keyboard.KEY_SPACE)?80:-40;
        a.y = exit.y>=Display.getHeight()&&(selectedPlot==PlotType.WALL||selectedPlot==PlotType.ANIM)&&Keyboard.isKeyDown(Keyboard.KEY_SPACE)?120:-40;
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
        if(anim.size()>1){
            GL11.glColor4d(PlotType.TOUCH_EVENT.getRed(), PlotType.TOUCH_EVENT.getGreen(), PlotType.TOUCH_EVENT.getBlue(), PlotType.TOUCH_EVENT.getAlpha());
            drawRect(0, 0, Display.getWidth(), 2, 0);
            GL11.glColor4d(PlotType.EVENT.getRed(), PlotType.EVENT.getGreen(), PlotType.EVENT.getBlue(), PlotType.EVENT.getAlpha());
            drawRect(event/(double)anim.size()*Display.getWidth(), 0, Display.getWidth(), 2, 0);
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
        super.render(millisSinceLastTick);
    }
    @Override
    public void tick(){
        if(anim.isEmpty()){
            anim.add(new Plot[Core.GRID_WIDTH][Core.GRID_HEIGHT]);
        }
    }
    @Override
    public void keyboardEvent(char character, int key, boolean pressed, boolean repeat){
        if(pressed&&!repeat){
            switch (key){
                case Keyboard.KEY_CAPITAL:
                    if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
                        if(exit.y<Display.getHeight()){
                            name.y = -name.height;
                            exit.y = Display.getHeight();
                            delete.y = -Display.getHeight();
                        }else{
                            name.y = 0;
                            exit.y = Display.getHeight()-exit.height;
                            delete.y = name.y+name.height;
                        }
                    }   break;
                case Keyboard.KEY_TAB:
                    if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)&&Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)&&event>=1){
                        event--;
                    }else if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)&&!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
                        event++;
                        if(event>=anim.size()){
                            anim.add(new Plot[Core.GRID_WIDTH][Core.GRID_HEIGHT]);
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
                        if(selectableTypes[selectedIndex]==PlotType.ANIM&&event>0){
                            selectedIndex = 0;
                        }
                        selectedPlot = selectableTypes[selectedIndex];
                    }   break;
                case Keyboard.KEY_BACK:
                    if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)&&Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)&&event==anim.size()-1&&anim.size()>1){
                        event--;
                        anim.remove(anim.size()-1);
                    }   break;
                default:
                    break;
            }
        }
        super.keyboardEvent(character, key, pressed, repeat);
        if(key==Keyboard.KEY_M&&pressed&&!repeat){
            Core.music = !Core.music;
        }
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        if(button==exit){
            save();
            gui.open(parent);
        }
    }
    public void save(){
        String name = this.name.text;
        File anims = new File(Main.getAppdataRoot(), "\\editor\\"+saveName+"\\anims");
        File animDir = new File(anims, "\\"+name);
        if(animDir.exists()){
            deleteFolder(animDir);
        }
        animDir.mkdirs();
        for(int i = 0; i<anim.size(); i++){
            Plot[][] frame = anim.get(i);
            File file = new File(animDir, "\\"+i+".png");
            BufferedImage image = new BufferedImage(frame.length, frame[0].length, BufferedImage.TYPE_INT_ARGB);
            for(int x = 0; x<frame.length; x++){
                for(int y = 0; y<frame[x].length; y++){
                    if(frame[x][y]==null){
                        image.setRGB(x, y, new Color(0, 0, 0, 0).getRGB());
                        continue;
                    }
                    float R = (float) frame[x][y].red;
                    float G = (float) frame[x][y].green;
                    float B = (float) frame[x][y].blue;
                    float A = (float) frame[x][y].alpha;
                    image.setRGB(x, y, new Color(R, G, B, A).getRGB());
                }
            }
            try{
                if(!file.getParentFile().exists()){
                    file.getParentFile().mkdirs();
                }
                if(!file.exists()){
                    file.createNewFile();
                }
                ImageIO.write(image, "png", file);
            }catch(IOException ex){
                throw new RuntimeException(ex);
            }
        }
    }
    private void delete(){
        File file = new File(Main.getAppdataRoot(), "\\editor\\"+saveName+"\\anims\\"+name.text+"\\");
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
    public void updatePlot(int event, int x, int y, int event2, int x2, int y2){
        if(anim.get(event2)[x2][y2]==null){
            anim.get(event)[x][y] = null;
            return;
        }
        updatePlot(event, x, y, anim.get(event2)[x2][y2].type, anim.get(event2)[x2][y2].red, anim.get(event2)[x2][y2].green, anim.get(event2)[x2][y2].blue, anim.get(event2)[x2][y2].alpha);
    }
    public void updatePlot(int event, int x, int y, PlotType type, double r, double g, double b, double a){
        if(anim.get(event)[x][y]==null){
            anim.get(event)[x][y] = new Plot(x,y,null);
        }
        anim.get(event)[x][y].setType(type);
        anim.get(event)[x][y].red = r;
        anim.get(event)[x][y].green = g;
        anim.get(event)[x][y].blue = b;
        anim.get(event)[x][y].alpha = a;
    }
    public void updatePlot(int event, int x, int y, PlotType type){
        if(anim.get(event)[x][y]==null){
            anim.get(event)[x][y] = new Plot(x,y,null);
        }
        anim.get(event)[x][y].setType(type);
    }
}