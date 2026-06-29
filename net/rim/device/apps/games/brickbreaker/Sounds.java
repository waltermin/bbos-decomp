package net.rim.device.apps.games.brickbreaker;

import java.io.ByteArrayInputStream;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.control.VolumeControl;
import net.rim.device.api.media.control.AudioPathControl;
import net.rim.device.api.system.Alert;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.resources.Resource;
import net.rim.device.resources.Resource$Internal;

final class Sounds {
   private Player[] _players = new Player[8];
   private AudioPathControl _audioPathControl;
   public static final int POPPILL = 0;
   public static final int EATPILL = 1;
   public static final int LASER = 2;
   public static final int BOMB = 3;
   public static final int BRICKDESTROY = 4;
   public static final int BRICKHIT = 5;
   public static final int CEILING = 6;
   public static final int PADDLE = 7;
   private static Sounds _sounds;
   private static final short[] _paddle = new short[]{500, 70, 256, 6502};
   private static final short[] _laser = new short[]{600, 50, 650, 50, 550, 50, 2, -12280, 500, 70, 256, 6502};
   private static final short[] _hit = new short[]{850, 40, 900, 40, 950, 40, 6, -12280, 600, 50, 650, 50};

   private Sounds() {
   }

   public static final Sounds getSounds() {
      if (_sounds == null) {
         _sounds = new Sounds();
      }

      return _sounds;
   }

   public final void updatePlayerVolumes() {
      int vol = Options.getVolume();

      for (int i = 0; i < this._players.length; i++) {
         if (this._players[i] != null) {
            VolumeControl vc = (VolumeControl)this._players[i].getControl("VolumeControl");
            if (vc != null) {
               vc.setLevel(vol);
            }
         }
      }
   }

   public final void setProperties(String codfilename, String series) {
      if (codfilename != null && series != null) {
         Resource resource = null;
         resource = Resource$Internal.getResourceClass(codfilename);
         if (resource != null) {
            byte[] data = resource.getResource(series + ".properties");
            if (data != null) {
               StringTokenizer st = new StringTokenizer(new String(data), "=,\t\n\r\f");

               while (st.hasMoreTokens()) {
                  String next = st.nextToken().trim();
                  String filename = st.nextToken().trim();
                  String mime = st.nextToken().trim();
                  byte[] filedata = resource.getResource(filename);
                  int index = -1;
                  if ("poppill".equals(next)) {
                     index = 0;
                  }

                  if ("eatpill".equals(next)) {
                     index = 1;
                  }

                  if ("laser".equals(next)) {
                     index = 2;
                  }

                  if ("bomb".equals(next)) {
                     index = 3;
                  }

                  if ("brickdestroy".equals(next)) {
                     index = 4;
                  }

                  if ("ceiling".equals(next)) {
                     index = 6;
                  }

                  if ("paddle".equals(next)) {
                     index = 7;
                  }

                  if ("brickhit".equals(next)) {
                     index = 5;
                  }

                  if (index >= 0) {
                     try {
                        this._players[index] = Manager.createPlayer(new ByteArrayInputStream(filedata), mime);
                        this._players[index].prefetch();
                        if (this._audioPathControl == null) {
                           this._audioPathControl = (AudioPathControl)this._players[index].getControl("net.rim.device.api.media.control.AudioPathControl");
                        }
                     } finally {
                        continue;
                     }
                  }
               }

               this.updatePlayerVolumes();
            }
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void play(int sound) {
      Player p = this._players[sound];
      if (p != null) {
         try {
            p.stop();
            p.setMediaTime(0);
            p.start();
         } catch (Throwable var5) {
            System.out.println("BrickBreaker sound: " + any.toString());
            return;
         }
      } else {
         if (Alert.isBuzzerSupported()) {
            switch (sound) {
               case 1:
               case 3:
                  break;
               case 2:
                  Alert.startBuzzer(_laser, Options.getVolume());
                  return;
               case 4:
               case 5:
               case 6:
                  Alert.startBuzzer(_hit, Options.getVolume());
                  break;
               case 7:
               default:
                  Alert.startBuzzer(_paddle, Options.getVolume());
                  return;
            }
         }
      }
   }

   public final void gamePaused(boolean paused) {
      if (Options.sounds && this._audioPathControl != null) {
         this._audioPathControl.forceActive(!paused);
      }
   }
}
