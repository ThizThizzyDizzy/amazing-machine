package maze.machine.menu;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import maze.machine.Core;
import maze.machine.Levels;
import maze.machine.World;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuLore extends Menu{
    public static Random rand = new Random();
    private ArrayList<String> lore = new ArrayList<>();
    public MenuLore(GUI gui, Menu parent, String lore){
        super(gui, parent);
        InputStream stream = World.class.getResourceAsStream(Levels.getLorePath(lore));
        if(stream!=null){
            try(BufferedReader in = new BufferedReader(new InputStreamReader(stream))){
                String line;
                while((line = in.readLine())!=null){
                    this.lore.add(line);
                }
            }catch(IOException ex){
                this.lore = null;
            }
            interpretLore();
        }
    }
    public MenuLore(GUI gui, File saveFolder, boolean end){
        super(gui, end?new MenuMain(gui):new MenuMaze(gui, saveFolder));
        File file = new File(saveFolder, "\\lore\\"+(end?"end":"begin")+".txt");
        if(file.exists()){
            try(BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)))){
                String line;
                while((line = in.readLine())!=null){
                    this.lore.add(line);
                }
            }catch(IOException ex){
                lore = null;
            }
            interpretLore();
        }
    }
    @Override
    public void render(int millisSinceLastTick){
        int line = 0;
        GL11.glColor3f(1, 1, 1);
        for(String str : lore){
            if(str.contains("<r>")){
                GL11.glColor4d(.75, 0, 0, 1);
                str = str.replace("<r>", "");
            }else if(str.contains("<o>")){
                GL11.glColor4d(.75, .75/2, 0, 1);
                str = str.replace("<o>", "");
            }else if(str.contains("<y>")){
                GL11.glColor4d(.75, .75, 0, 1);
                str = str.replace("<y>", "");
            }else if(str.contains("<g>")){
                GL11.glColor4d(0, .75, 0, 1);
                str = str.replace("<g>", "");
            }else if(str.contains("<b>")){
                GL11.glColor4d(0, 0, 0.75, 1);
                str = str.replace("<b>", "");
            }else if(str.contains("<p>")){
                GL11.glColor4d(.75, 0, .75, 1);
                str = str.replace("<p>", "");
            }
            if(str.contains("<sm>")){
                while(!str.isEmpty()){
                    str = drawCenteredTextWithWrap(100, 100+30*line+4, Core.GRID_WIDTH*20-100, 120+30*line-4, str.replace("<sm>", ""));
                    line++;
                }
            }else if(str.contains("<lg>")){
                while(!str.isEmpty()){
                    str = drawCenteredTextWithWrap(100, 100+30*line, Core.GRID_WIDTH*20-100, 120+30*(line+1), str.replace("<lg>", ""));
                    line+=2;
                }
            }else{
                while(!str.isEmpty()){
                    str = drawCenteredTextWithWrap(100, 100+30*line, Core.GRID_WIDTH*20-100, 120+30*line, str);
                    line++;
                }
            }
            GL11.glColor4d(1, 1, 1, 1);
        }
    }
    public void tick(){
        if(lore.isEmpty()){
            gui.open(parent);
        }
    }
    public void keyboardEvent(char character, int key, boolean pressed, boolean repeat){
        if(key==Keyboard.KEY_ESCAPE){
            gui.open(parent);
        }
        if(key==Keyboard.KEY_M&&pressed&&!repeat){
            Core.music = !Core.music;
        }
    }
    @Override
    public void mouseEvent(int button, boolean pressed, float x, float y, float xChange, float yChange, int wheelChange){
        if(pressed&&button==0){
            gui.open(parent);
        }
    }
    private void interpretLore() {
        for(int i = 0; i<lore.size(); i++){
            String str = lore.get(i);
            while(str.contains("[")){
                str = interpretCommand(str,str.indexOf('['),str.indexOf(']'));
                System.out.println(str);
            }
            lore.set(i, str);
        }
    }
    private String interpretCommand(String str, int start, int end){
        String fullCommand = str.substring(start, end+1);
        String command = str.substring(start+1, end);
        String actualCommand = str.substring(start+1, str.indexOf('{'));
        String fullParameters = str.substring(str.indexOf('{'),str.indexOf('}')+1);
        String parameters = str.substring(str.indexOf('{')+1,str.indexOf('}'));
        String[] fullParam = parameters.split("~");
        ArrayList<String> param = new ArrayList<>();
        for(String s : fullParam){
            if(!s.equalsIgnoreCase("~")){
                param.add(s);
            }
        }
        if(Core.debugMode){
            System.out.println("FullCommand "+fullCommand);
            System.out.println("Command "+command);
            System.out.println("ActualCommand "+actualCommand);
            System.out.println("FullParameters "+fullParameters);
            System.out.println("Parameters "+parameters);
            for(String s : fullParam){
                System.out.println("FullParam "+s);
            }
            for(String s : param){
                System.out.println("Param "+s);
            }
        }
        String newStr = command(actualCommand,param.toArray(new String[param.size()]));
        return replaceFirst(str,fullCommand, newStr);
    }
    private String replaceFirst(String str, String target, String replacement){
        String first = str.substring(0,str.indexOf(target));
        String last = str.substring(str.indexOf(target)+target.length());
        return first+replacement+last;
    }
    private String command(String command, String[] param) {
        switch(command){
            case "RAND":
                int min = Integer.parseInt(param[0]);
                int max = Integer.parseInt(param[1]);
                return rand.nextInt(max-min)+min+"";
        }
        return "";
    }
}
