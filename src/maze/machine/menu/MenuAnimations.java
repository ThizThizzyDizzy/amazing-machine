package maze.machine.menu;
import java.awt.event.ActionEvent;
import java.io.File;
import maze.machine.Core;
import maze.machine.Main;
import maze.machine.PlotType;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
import simplelibrary.opengl.gui.components.MenuComponentList;
public class MenuAnimations extends Menu{
    public MenuComponentList list;
    public MenuAnimations(GUI gui, Menu parent, String saveName){
        super(gui, parent);
        list = add(new MenuComponentList(0, 0, Display.getWidth(), Display.getHeight(), Display.getWidth()/250, false));
        MenuAnimations that = this;
        File folder = new File(Main.getAppdataRoot(), "\\editor\\"+saveName+"\\anims");
        if(folder.exists()){
            for(File file : folder.listFiles()){
                if(file.isDirectory()){
                    MenuComponentButton button = new MenuComponentButton(0, 0, Display.getWidth(), 100, file.getName(), true, true, "/textures/gui/button");
                    button.addActionListener((ActionEvent e) -> {
                        gui.open(new MenuEditAnimation(gui, that, saveName, file));
                    });
                    list.add(button);
                }
            }
        }
        MenuComponentButton button = new MenuComponentButton(0, 0, Display.getWidth(), 100, "New", true, true, "/textures/gui/button");
        button.addActionListener((ActionEvent e) -> {
            gui.open(new MenuEditAnimation(gui, that, saveName));
        });
        list.add(button);
        button = new MenuComponentButton(0, 0, Display.getWidth(), 100, "Back", true, true, "/textures/gui/button");
        button.addActionListener((ActionEvent e) -> {
            gui.open(parent);
        });
        list.add(button);
    }
    @Override
    public void keyboardEvent(char character, int key, boolean pressed, boolean repeat){
        super.keyboardEvent(character, key, pressed, repeat);
        if(key==Keyboard.KEY_M&&pressed&&!repeat){
            Core.music = !Core.music;
        }
    }
    @Override
    public void renderBackground(){
        GL11.glColor4d(PlotType.PATH.getRed(), PlotType.PATH.getGreen(), PlotType.PATH.getBlue(), PlotType.PATH.getAlpha());
        drawRect(0, 0, Display.getWidth(), Display.getHeight(), 0);
        GL11.glColor4d(1, 1, 1, 1);
    }
}