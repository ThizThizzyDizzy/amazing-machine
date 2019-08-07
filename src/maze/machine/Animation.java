package maze.machine;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;
public class Animation{
    public final String name;
    public final int delay;
    public final int startingFrame;
    public final int collisionSettings;
    public final int loopSettings;
    public final ArrayList<Plot[][]> anim;
    public int frame;
    public final int latchX;
    public final int latchY;
    public static final HashMap<Integer, String> anims = new HashMap<>();
    public int timer;
    public Plot[][] underneath = null;
    public final double r;
    public final double g;
    public final double b;
    public final double a;
    public Animation(String name, double r, double g, double b, double a, int delay, int startingFrame, int collisionSettings, int loopSettings, ArrayList<Plot[][]> anim, int latchX, int latchY){
        this.name = name;
        this.a = a;
        this.delay = delay;
        this.startingFrame = startingFrame;
        this.collisionSettings = collisionSettings;
        this.loopSettings = loopSettings;
        this.anim = anim;
        frame = startingFrame;
        this.latchX = latchX;
        this.latchY = latchY;
        this.r = r;
        this.g = g;
        this.b = b;
    }
    public static Animation loadAnim(World world, int red, int green, int blue, int alpha){
        String nam = null;
        double theA = 0, theR = 0, theG = 0, theB = 0;
        for(Integer rgba : anims.keySet()){
            int a = ((rgba>>24)&255);
            int r = ((rgba>>16)&255);
            int g = ((rgba>>8)&255);
            int b = (rgba&255);
            if(a==alpha&&r==red&&g==green&&b==blue){
                nam = anims.get(rgba);
                theA = a/255d;
                theR = r/255d;
                theG = g/255d;
                theB = b/255d;
                break;
            }
        }
        if(nam==null)return null;
        final String name = nam;
        try{
            String path = Animation.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File file = new File(path);
            if(!file.exists()) throw new IllegalStateException("Could not find myself!");//Never thrown
            if(file.isDirectory()){
                //NOT a jarfile; running in development environment
                File anim = new File(file, "maze\\machine\\images\\animations\\"+name+"\\0.png");
                BufferedImage img = ImageIO.read(anim);
                for(int x = 0; x<img.getWidth()-2; x++){
                    for(int y = 0; y<img.getHeight()-2; y++){
                        int rgba = img.getRGB(x, y);
                        double a = ((rgba>>24)&255)/255d;
                        double r = ((rgba>>16)&255)/255d;
                        double g = ((rgba>>8)&255)/255d;
                        double b = (rgba&255)/255d;
                        if(r==PlotType.ANIM.getRed()&&g==PlotType.ANIM.getGreen()&&b==PlotType.ANIM.getBlue()&&a==PlotType.ANIM.getAlpha()){
                            int RGBA = img.getRGB(x+1, y);
                            int A = ((RGBA>>24)&255);
                            int R = ((RGBA>>16)&255);
                            int G = ((RGBA>>8)&255);
                            int B = (RGBA&255);
                            int delay = R;
                            int startingFrame = G;
                            int collisionSettings = B;
                            int loopSettings = A;
                            HashMap<Integer, Plot[][]> animation = new HashMap<>();
                            int latchX = x+1;
                            int latchY = y+1;
                            for(int I = x; I<x+3; I++){
                                for(int J = y; J<y+3; J++){
                                    img.setRGB(I, J, img.getRGB(x, y+1));
                                }
                            }
                            File folder = new File(file, "maze\\machine\\images\\animations\\"+name);
                            File[] frames = folder.listFiles();
                            for(File f : frames){
                                BufferedImage image = ImageIO.read(f);
                                if(f.getName().equals("0.png")){
                                    image = img;
                                }
                                Plot[][] frame = new Plot[image.getWidth()][image.getHeight()];
                                for(int I = 0; I<image.getWidth(); I++){
                                    for(int J = 0; J<image.getHeight(); J++){
                                        frame[I][J] = new Plot(I, J, world).processType(world, image.getRGB(I, J));
                                    }
                                }
                                animation.put(Integer.parseInt(f.getName().replace(".png", "")),frame);
                            }
                            ArrayList<Plot[][]> actualAnim = new ArrayList<>();
                            for(int I = 0; I<animation.size(); I++){
                                actualAnim.add(animation.get(I));
                            }
                            return new Animation(name, theR, theG, theB, theA, delay, startingFrame, collisionSettings, loopSettings, actualAnim, latchX, latchY);
                        }
                    }
                }
            }else{
                //in jarfile
                try {
                    System.out.println("Loading "+name);
                    InputStream in = Animation.class.getResourceAsStream("/maze/machine/images/animations/"+name+"/0.png");
                    BufferedImage img = ImageIO.read(in);
                    for(int i = 0; i<img.getWidth()-2; i++){
                        for(int j = 0; j<img.getHeight()-2; j++){
                            int rgba = img.getRGB(i, j);
                            double a = ((rgba>>24)&255)/255d;
                            double r = ((rgba>>16)&255)/255d;
                            double g = ((rgba>>8)&255)/255d;
                            double b = (rgba&255)/255d;
                            if(r==PlotType.ANIM.getRed()&&g==PlotType.ANIM.getGreen()&&b==PlotType.ANIM.getBlue()&&a==PlotType.ANIM.getAlpha()){
                                int RGBA = img.getRGB(i+1, j);
                                int A = ((RGBA>>24)&255);
                                int R = ((RGBA>>16)&255);
                                int G = ((RGBA>>8)&255);
                                int B = (RGBA&255);
                                int delay = R;
                                int startingFrame = G;
                                int collisionSettings = B;
                                int loopSettings = A;
                                ArrayList<Plot[][]> animation = new ArrayList<>();
                                int latchX = i+1;
                                int latchY = j+1;
                                for(int I = i; I<i+3; I++){
                                    for(int J = j; J<j+3; J++){
                                        img.setRGB(I, J, img.getRGB(i, j+1));
                                    }
                                }
                                do{
                                    Plot[][] frame = new Plot[img.getWidth()][img.getHeight()];
                                    for(int I = 0; I<img.getWidth(); I++){
                                        for(int J = 0; J<img.getHeight(); J++){
                                            frame[I][J] = new Plot(I, J, world).processType(world, img.getRGB(I, J));
                                        }
                                    }
                                    animation.add(frame);
                                    in = Animation.class.getResourceAsStream("/maze/machine/images/animations/"+name+"/"+animation.size()+".png");
                                    if(in!=null) img = ImageIO.read(in);
                                }while(in!=null);
                                return new Animation(name, theR, theG, theB, theA, delay, startingFrame, collisionSettings, loopSettings, animation, latchX, latchY);
                            }
                        }
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }catch(URISyntaxException | IOException ex){
            throw new RuntimeException(ex);
        }
        throw new IllegalStateException("Failed to find animation! If you see this, the programmers are idiots and somehow screwed something up.");
    }
    public static Animation loadAnim(World world, File animFolder, int red, int green, int blue, int alpha){
        HashMap<Integer, String> localAnims = new HashMap<>();
        for(File folder : animFolder.listFiles()){
            try {
                File anim = new File(folder, "\\0.png");
                BufferedImage img = ImageIO.read(anim);
                for(int i = 0; i<img.getWidth()-2; i++){
                    for(int j = 0; j<img.getHeight()-2; j++){
                        int rgba = img.getRGB(i, j);
                        double a = ((rgba>>24)&255)/255d;
                        double r = ((rgba>>16)&255)/255d;
                        double g = ((rgba>>8)&255)/255d;
                        double b = (rgba&255)/255d;
                        if(r==PlotType.ANIM.getRed()&&g==PlotType.ANIM.getGreen()&&b==PlotType.ANIM.getBlue()&&a==PlotType.ANIM.getAlpha()){
                            int animARGB = img.getRGB(i+1, j+1);
                            localAnims.put(animARGB, folder.getName());
                        }
                    }
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        String nam = null;
        double theA = 0, theR = 0, theG = 0, theB = 0;
        for(Integer rgba : localAnims.keySet()){
            int a = ((rgba>>24)&255);
            int r = ((rgba>>16)&255);
            int g = ((rgba>>8)&255);
            int b = (rgba&255);
            if(a==alpha&&r==red&&g==green&&b==blue){
                nam = localAnims.get(rgba);
                theA = a/255d;
                theR = r/255d;
                theG = g/255d;
                theB = b/255d;
            }
        }
        if(nam==null)return null;
        final String name = nam;
        try{
            //NOT a jarfile; running in development environment
            File anim = new File(animFolder, name+"\\0.png");
            BufferedImage img = ImageIO.read(anim);
            for(int x = 0; x<img.getWidth()-2; x++){
                for(int y = 0; y<img.getHeight()-2; y++){
                    int rgba = img.getRGB(x, y);
                    double a = ((rgba>>24)&255)/255d;
                    double r = ((rgba>>16)&255)/255d;
                    double g = ((rgba>>8)&255)/255d;
                    double b = (rgba&255)/255d;
                    if(r==PlotType.ANIM.getRed()&&g==PlotType.ANIM.getGreen()&&b==PlotType.ANIM.getBlue()&&a==PlotType.ANIM.getAlpha()){
                        int RGBA = img.getRGB(x+1, y);
                        int A = ((RGBA>>24)&255);
                        int R = ((RGBA>>16)&255);
                        int G = ((RGBA>>8)&255);
                        int B = (RGBA&255);
                        int delay = R;
                        int startingFrame = G;
                        int collisionSettings = B;
                        int loopSettings = A;
                        HashMap<Integer, Plot[][]> animation = new HashMap<>();
                        int latchX = x+1;
                        int latchY = y+1;
                        for(int I = x; I<x+3; I++){
                            for(int J = y; J<y+3; J++){
                                img.setRGB(I, J, img.getRGB(x, y+1));
                            }
                        }
                        File folder = new File(animFolder, name);
                        File[] frames = folder.listFiles();
                        for(File f : frames){
                            BufferedImage image = ImageIO.read(f);
                            if(f.getName().equals("0.png")){
                                image = img;
                            }
                            Plot[][] frame = new Plot[image.getWidth()][image.getHeight()];
                            for(int I = 0; I<image.getWidth(); I++){
                                for(int J = 0; J<image.getHeight(); J++){
                                    if(image.getRGB(I, J)==new Color(0,0,0,0).getRGB()){
                                        continue;
                                    }
                                    frame[I][J] = new Plot(I, J, world).processType(world, image.getRGB(I, J));
                                }
                            }
                            animation.put(Integer.parseInt(f.getName().replace(".png", "")),frame);
                        }
                        ArrayList<Plot[][]> actualAnim = new ArrayList<>();
                        for(int I = 0; I<animation.size(); I++){
                            actualAnim.add(animation.get(I));
                        }
                        return new Animation(name, theR, theG, theB, theA, delay, startingFrame, collisionSettings, loopSettings, actualAnim, latchX, latchY);
                    }
                }
            }
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
        throw new IllegalStateException("Failed to find animation! If you see this, the programmers are idiots and somehow screwed something up.");
    }
    static{
        try {
            String path = Animation.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File file = new File(path);
            if(!file.exists()) throw new IllegalStateException("Could not find myself!");//Never thrown
            if(file.isDirectory()){
                //NOT a jarfile; running in development environment
                File directory = new File(file, "maze\\machine\\images\\animations");
                File[] files = null;
                if(directory.exists()){
                    files = directory.listFiles();
                    for(File folder : files){
                        File anim = new File(folder, "\\0.png");
                        BufferedImage img = ImageIO.read(anim);
                        for(int i = 0; i<img.getWidth()-2; i++){
                            for(int j = 0; j<img.getHeight()-2; j++){
                                int rgba = img.getRGB(i, j);
                                double alpha = ((rgba>>24)&255)/255d;
                                double red = ((rgba>>16)&255)/255d;
                                double green = ((rgba>>8)&255)/255d;
                                double blue = (rgba&255)/255d;
                                if(red==PlotType.ANIM.getRed()&&green==PlotType.ANIM.getGreen()&&blue==PlotType.ANIM.getBlue()&&alpha==PlotType.ANIM.getAlpha()){
                                    int animARGB = img.getRGB(i+1, j+1);
                                    anims.put(animARGB, folder.getName());
                                }
                            }
                        }
                    }
                }
            }else{
                try(ZipFile zip = new ZipFile(file)){
                    ArrayList<ZipEntry> lst = new ArrayList<>();
                    zip.stream()
                            .filter((e)->{System.out.println(e.getName()); return e.getName().endsWith("/0.png")&&e.getName().startsWith("maze/machine/images/animations");})
                            .forEach((e)->lst.add(e));
                    lst.stream().forEach((e)->{
                        try {
                            InputStream in = zip.getInputStream(e);
                            BufferedImage img = ImageIO.read(in);
                            for(int i = 0; i<img.getWidth()-2; i++){
                                for(int j = 0; j<img.getHeight()-2; j++){
                                    int rgba = img.getRGB(i, j);
                                    double alpha = ((rgba>>24)&255)/255d;
                                    double red = ((rgba>>16)&255)/255d;
                                    double green = ((rgba>>8)&255)/255d;
                                    double blue = (rgba&255)/255d;
                                    if(red==PlotType.ANIM.getRed()&&green==PlotType.ANIM.getGreen()&&blue==PlotType.ANIM.getBlue()&&alpha==PlotType.ANIM.getAlpha()){
                                        int animARGB = img.getRGB(i+1, j+1);
                                        String name = e.getName().substring("maze/machine/images/animations/".length());
                                        anims.put(animARGB, name.substring(0, name.length()-"/0.png".length()));
                                    }
                                }
                            }
                        } catch (IOException ex) {}
                    });
                }
            }
        }catch(URISyntaxException | IOException ex){
            throw new RuntimeException(ex);
        }
    }
}