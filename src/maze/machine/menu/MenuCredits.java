package maze.machine.menu;
import java.util.ArrayList;
import maze.machine.Core;
import maze.machine.Main;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuCredits extends Menu{
    private final MenuComponentButton back;
    public static final ArrayList<String> credits = new ArrayList<>();
    static{
        credits.add("Made By");
        credits.add("");
        credits.add("ThizThizzyDizzy");
        credits.add("");
        credits.add("");
        credits.add("Music");
        credits.add("");
        credits.add("\"Five Armies\"");
        credits.add("");
        credits.add("Kevin MacLeod (incompetech.com)");
        credits.add("Licensed under Creative Commons: By Attribution 3.0 License");
        credits.add("http://creativecommons.org/licenses/by/3.0/");
        credits.add("");
        credits.add("");
        credits.add("Libraries Used");
        credits.add("");
        credits.add("SimpleLibrary");
        credits.add("by computerneek");
        credits.add("");
        credits.add("LWJGL");
        credits.add("by the LWJGL Team");
        credits.add("");
        credits.add("JLayer");
        credits.add("by javazoom");
        credits.add("");
        credits.add("");
        credits.add("Special Thanks");
        credits.add("");
        credits.add("computerneek");
        credits.add("");
        credits.add("");
        credits.add("");
        credits.add("The people and events in "+Main.applicationName+" are entirely fictional.");
        credits.add("Any similarity to actual people or events is unintentional.");
    }
    public MenuCredits(GUI gui){
        super(gui, null);
        back = add(new MenuComponentButton(Display.getWidth()/2-200, 540, 400, 40, "Back", true));
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        if(button==back){
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
    public void renderBackground(){
        GL11.glColor4d(.5, .5, .5, 1);
        drawRect(0, 0, Display.getWidth(), Display.getHeight(), 0);
        GL11.glColor4d(1, 1, 1, 1);
        back.x = Display.getWidth()/2-back.width/2;
        back.y = Display.getHeight()-80;
//        drawRect(Display.getWidth()/4-100, 40, (Display.getWidth()-Display.getWidth()/4)-100+200, 200, ImageStash.instance.getTexture("/textures/logo.png"));
        yOffset = 240;
        totalTextHeight = 0;
        for(String str : credits){
            text(str);
        }
        if(scroll>totalTextHeight){
            scroll = -(back.y-40-240);
        }
    }
    private double yOffset = 240;
    private double scroll = Integer.MAX_VALUE;
    private double textHeight = 20;
    private double totalTextHeight = 0;
    private double textWidth = Display.getWidth()-400;
    public void text(String text){
        if(text==null||text.isEmpty()){
            text();
            return;
        }
        String[] texts = text.split("&");
        double wide = textWidth/(double)texts.length;
        for(int i = 0; i<texts.length; i++){
            drawCenteredTextWithBounds(200+wide*i, yOffset-scroll, 200+wide*(i+1), yOffset+textHeight-scroll, Display.getWidth()/2-textWidth/2, 240, Display.getWidth()/2+textWidth/2, back.y-40, texts[i]);
        }
        totalTextHeight += textHeight;
        yOffset+=textHeight;
    }
    public void text(){
        totalTextHeight += textHeight;
        yOffset+=textHeight;
    }
    @Override
    public void tick(){
        super.tick();
        scroll++;
    }
}