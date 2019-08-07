package maze.machine.menu;
import maze.machine.Core;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuRandomMaze extends MenuMaze{
    public MenuRandomMaze(GUI gui, Menu parent){
        super(gui, 0);
        Core.loadRandomWorld();
    }
    @Override
    protected void displayOpeningLore(){}
    @Override
    public void renderTitles(int millisSinceLastTick){
        if(tick<30){
            float shift = tick+(millisSinceLastTick/100f);
            drawRectWithBounds(0, 0, Core.GRID_WIDTH*20, Core.GRID_HEIGHT*20, Core.GRID_WIDTH*(shift-40), 0, Core.GRID_WIDTH*shift, Core.GRID_HEIGHT*20, ImageStash.instance.getTexture("/textures/title.png"));
        }else{
            Core.world.render(millisSinceLastTick);
        }
    }
}