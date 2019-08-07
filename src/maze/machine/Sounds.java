package maze.machine;
import maze.machine.menu.MenuMusicDownload;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import org.lwjgl.LWJGLException;
import simplelibrary.openal.Autoplayer;
import simplelibrary.openal.SoundSystem;
public class Sounds{
    private static SoundSystem soundSystem;
    private static Autoplayer autoplayer = new Autoplayer() {
            @Override
            public String next(){
                ArrayList<String> strs = new ArrayList<>();
                getPlayableMusic(strs);
                if(!strs.isEmpty()){
                    String sound = strs.get(new Random().nextInt(strs.size()));
                    if(canPlayMusic(sound)){
                        return soundNames.get(sound);
                    }
                }
                return null;
            }
            @Override
            public float getVolume(){
                return vol;
            }
        };
    public static boolean autoplay = true;
    public static boolean allowDownload = false;
    private static void addMusic(){
        addSong("Music1", "Five Armies", "https://www.dropbox.com/s/n2nnl7o3qp6pfmu/Five%20Armies.mp3?dl=1", 6090);
    }
    /**
     * Gets a list of all currently playable music.
     * @return a list of sound names that can be played as music at the current time (One will be randomly chosen)
     */
    private static void getPlayableMusic(ArrayList<String> playableMusic){
        if(Core.music){
            playableMusic.add("Music1");
        }
    }
    private static boolean running = false;
    public static HashMap<String, String> soundNames = new HashMap<>();
    public static HashMap<String, Integer> songSizes = new HashMap<>();
    public static HashMap<String, String> songURLs = new HashMap<>();
    public static float vol = 1f;
    /**
     * Disables the sound system.
     * Equivalent to AL.destroy();
     */
    public static void destroy(){
        soundSystem.destroy();
        running = false;
    }
    /**
     * Starts the sound system, music thread, and music downloading thread.
     * Equivalent to AL.destroy();
     */
    public static void create() throws LWJGLException{
        soundNames.clear();
        songURLs.clear();
        addMusic();
        running = true;
        soundSystem = new SoundSystem(10, "/sounds/", ".wav", "music");
        new Thread(() -> { //Music Downloader
            int downloadSize = 0;
            for(String key : songURLs.keySet()){
                File file = new File(soundNames.get(key).replace(".wav", ".mp3"));
                if(!file.exists()){
                    downloadSize+=songSizes.get(key);
                }
            }
            if(downloadSize<=0){
                return;
            }
            Core.gui.open(new MenuMusicDownload(Core.gui, Core.gui.menu, downloadSize));
            while(!allowDownload){
                try{
                    Thread.sleep(100);
                }catch(InterruptedException ex){}
                if(!Core.helper.running){
                    return;
                }
            }
            System.out.println("Starting Music Download...");
            for(String key : songURLs.keySet()){
                if(!running){
                    return;
                }
                String filepath = soundNames.get(key);
                String url = songURLs.get(key);
                File from = new File(filepath);
                if(!from.exists()){
                    System.out.println("Downloading Song "+key+"...");
                    downloadFile(url, from);
                    System.out.println("Song Downloaded: "+key+"...");
                }
            }
            System.out.println("All songs are up to date.");
        }).start();
    }
    static void tick(boolean lastTick){
        if(lastTick){
            destroy();
            return;
        }
        if(soundSystem.getChannel("music").isStopped()&&autoplay){
            enableAutoplay();
        }
        vol = Math.max(0,Math.min(1,vol));
        if(nowPlaying()!=null&&!canPlayMusic(nowPlaying())){
            stopSound("music");
        }
        soundSystem.setMasterVolume(vol);
    }
    /**
     * @return song time in TICKS
     */
    public static int songTimer(){
        return soundSystem.getChannel("music").getPlayheadPosition()/50;
    }
    public static synchronized void playSound(String source, String sound){
        if(source.equals("music")){
            if(!canPlayMusic(sound)) return;
            soundSystem.getChannel(source).fadeTo(60, soundNames.get(sound));
            enableAutoplay();
        }
        soundSystem.getChannel(source).play(soundNames.get(sound));
    }
    @Deprecated
    public static synchronized void playSoundOneChannel(String source, String sound){
        playSound(source, sound);
    }
    public static boolean canPlayMusic(String music){
        ArrayList<String> playable = new ArrayList<>();
        getPlayableMusic(playable);
        return playable.contains(music);
    }
    @Deprecated
    public static synchronized void stopSounds(String source){
        stopSound(source);
    }
    @Deprecated
    public static synchronized void stopSound(String source, int channel){
        stopSound(source);
    }
    public static synchronized void fadeSound(String source, String sound){
        soundSystem.getChannel(source).fadeTo(60, soundNames.get(sound));
    }
    public static synchronized void fadeSound(String source){
        soundSystem.getChannel(source).fadeSkip(60);
    }
    public static synchronized void stopSound(String source){
        soundSystem.getChannel(source).stop();
    }
    public static synchronized boolean isPlaying(String source){
        return soundSystem.getChannel(source).isPlaying();
    }
    @Deprecated
    public static synchronized boolean isPlaying(String source, int channel){
        return isPlaying(source);
    }
    private static File downloadFile(String link, File destinationFile){
        if(destinationFile.exists()||link==null){
            return destinationFile;
        }
        destinationFile.getParentFile().mkdirs();
        try {
            URL url = new URL(link);
            int fileSize;
            URLConnection connection = url.openConnection();
            connection.setDefaultUseCaches(false);
            if ((connection instanceof HttpURLConnection)) {
                ((HttpURLConnection)connection).setRequestMethod("HEAD");
                int code = ((HttpURLConnection)connection).getResponseCode();
                if (code / 100 == 3) {
                    return null;
                }
            }
            fileSize = connection.getContentLength();
            byte[] buffer = new byte[65535];
            int unsuccessfulAttempts = 0;
            int maxUnsuccessfulAttempts = 3;
            boolean downloadFile = true;
            while (downloadFile) {
                downloadFile = false;
                URLConnection urlconnection = url.openConnection();
                if ((urlconnection instanceof HttpURLConnection)) {
                    urlconnection.setRequestProperty("Cache-Control", "no-cache");
                    urlconnection.connect();
                }
                String targetFile = destinationFile.getName();
                FileOutputStream fos;
                int downloadedFileSize;
                try (InputStream inputstream=Main.getRemoteInputStream(targetFile, urlconnection)) {
                    fos=new FileOutputStream(destinationFile);
                    downloadedFileSize=0;
                    int read;
                    while ((read = inputstream.read(buffer)) != -1) {
                        fos.write(buffer, 0, read);
                        downloadedFileSize += read;
                    }
                }
                fos.close();
                if (((urlconnection instanceof HttpURLConnection)) && 
                    ((downloadedFileSize != fileSize) && (fileSize > 0))){
                    unsuccessfulAttempts++;
                    if (unsuccessfulAttempts < maxUnsuccessfulAttempts){
                        downloadFile = true;
                    }else{
                        throw new Exception("failed to download "+targetFile);
                    }
                }
            }
            return destinationFile;
        }catch (Exception ex){
            return null;
        }
    }
    /**
     * Adds a song so it can be played. Songs are downloaded on sound system startup as MP3s and decompressed into .wav files.
     * @param name the name of the song, used when playing it.
     * @param songName The file name, without the extention
     * @param url the URL the song can be downloaded at, as an MP3
     */
    private static void addSong(String name, String songName, String url, int kb){
        soundNames.put(name, Main.getAppdataRoot()+"\\Music\\"+songName+".mp3");
        songURLs.put(name, url);
        songSizes.put(name, kb);
    }
    private static void addSong(String name, String songName){
        soundNames.put(name, "/sounds/music/"+songName+".mp3");
    }
    /**
     * Adds a sound effect so it can be played. Sound effects are not downloaded. They are found in the jarfile under /sounds
     * All sounds should be .wav files
     * @param name the name of the song, used when playing it.
     * @param songName The file name, without the extention.
     */
    private static void addSound(String name, String songName){
        soundNames.put(name, "/sounds/"+songName+".wav");
    }
    static boolean isPlayingMusic(){
        return soundSystem.getChannel("music").isPlaying();
    }
    static void toggleMusic(){
        if(soundSystem.getChannel("music").isPaused()){
            soundSystem.getChannel("music").play();
        }else{
            soundSystem.getChannel("music").pause();
        }
    }
    public static void enableAutoplay(){
        soundSystem.getChannel("music").autoplay(autoplayer);
}
    public static void disableAutoplay(){
        soundSystem.getChannel("music").stop();
    }
    public static String nowPlaying(){
        for(String sound : soundNames.keySet()){
            String path = soundNames.get(sound);
            if(soundSystem.getChannel("music").getCurrentSound()==path){
                return sound;
            }
        }
        return null;
    }
}