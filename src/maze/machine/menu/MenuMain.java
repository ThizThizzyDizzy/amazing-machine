package maze.machine.menu;
import maze.machine.Core;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuMain extends Menu{
    private final MenuComponentButton tutorial;
    private final MenuComponentButton campaign;
    private final MenuComponentButton random;
    private final MenuComponentButton edit;
    private final MenuComponentButton exit;
    private final MenuComponentButton credits;
    public MenuMain(GUI gui){
        super(gui, null);
        tutorial = add(new MenuComponentButton(Display.getWidth()/2-400, 100, 800, 40, "Play Tutorial", true, true, "/textures/gui/button"));
        campaign = add(new MenuComponentButton(Display.getWidth()/2-400, 200, 800, 80, "Start Campaign", true, true, "/textures/gui/button"));
        random = add(new MenuComponentButton(Display.getWidth()/2-400, 300, 800, 80, "Play Random Maze", true, true, "/textures/gui/button"));
        edit = add(new MenuComponentButton(Display.getWidth()/2-400, 400, 800, 80, "Level Editor", true, true, "/textures/gui/button"));
        exit = add(new MenuComponentButton(Display.getWidth()/2-400, Display.getHeight()-100, 800, 80, "Exit", true, true, "/textures/gui/button"));
        credits = add(new MenuComponentButton(Display.getWidth()-200, Display.getHeight()-40, 200, 40, "Credits", true, true, "/textures/gui/button"));
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
        GL11.glColor4d(.5, .5, .5, 1);
        drawRect(0, 0, Display.getWidth(), Display.getHeight(), 0);
        tutorial.x = campaign.x = random.x = edit.x = exit.x = Display.getWidth()/2-400;
        exit.y = Display.getHeight()-100;
        credits.x = Display.getWidth()-200;
        credits.y = Display.getHeight()-40;
        GL11.glColor4d(1, 1, 1, 1);
        drawCenteredText(0, Display.getHeight()-50, Display.getWidth(), Display.getHeight(), "Press M to Toggle Music");
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        if(button==tutorial){
            gui.open(new MenuTutorial(gui));
        }
        if(button==campaign){
            gui.open(new MenuMaze(gui, 2));
        }
        if(button==random){
            gui.open(new MenuRandom(gui, this));
        }
        if(button==credits){
            gui.open(new MenuCredits(gui));
        }
        if(button==edit){
            gui.open(new MenuOpenEditor(gui, this));
        }
        if(button==exit){
            Core.helper.running = false;
        }
    }
}