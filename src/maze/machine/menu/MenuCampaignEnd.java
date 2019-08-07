package maze.machine.menu;
import maze.machine.Core;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuCampaignEnd extends Menu{
    public MenuCampaignEnd(GUI gui, Menu parent){
        super(gui, parent);
    }
    @Override
    public void mouseEvent(int button, boolean pressed, float x, float y, float xChange, float yChange, int wheelChange){
        if(button==0||button==1&&pressed){
            gui.open(new MenuMain(gui));
        }
    }
    @Override
    public void keyboardEvent(char character, int key, boolean pressed, boolean repeat){
        super.keyboardEvent(character, key, pressed, repeat);
        if(key==Keyboard.KEY_M&&pressed&&!repeat){
            Core.music = !Core.music;
        }
    }
    @Override
    public void render(int millisSinceLastTick){
        drawText(0, Display.getHeight()/2-50, Display.getWidth(), Display.getHeight()/2, "You have reached the end of the campaign.");
        drawText(0, Display.getHeight()/2, Display.getWidth(), Display.getHeight()/2+50, "More levels coming soon.");
        drawText(0, Display.getHeight()/2+50, Display.getWidth(), Display.getHeight()/2+100, "Click to return to the main menu.");
    }
}
