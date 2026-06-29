package net.rim.device.apps.internal.profiles.tunes;

import java.io.InputStream;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.control.VolumeControl;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.system.EventLogger;
import net.rim.device.apps.internal.profiles.AlertEngine;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.device.internal.media.StreamDataControl;
import net.rim.device.internal.system.AlertPlayer;

final class ContentAlertPlayer implements AlertPlayer {
   private byte[] _data;
   private String _contentType;
   private Player _player = null;
   private boolean _initialized = false;
   private String _tuneLocation;

   ContentAlertPlayer(byte[] data, String contentType) {
      this._data = data;
      this._contentType = contentType;
   }

   ContentAlertPlayer(String fileName) {
      this._contentType = MIMETypeAssociations.getMIMEType(fileName);
      this._tuneLocation = FileUtilities.makeFileURL(fileName);
      if (!FileUtilities.checkFileExists(this._tuneLocation)) {
         EventLogger.logEvent(
            6982943375119825480L, ((StringBuffer)(new Object("ContentAlertPlayer: "))).append(fileName).append(" does not exist.").toString().getBytes(), 5
         );
         throw new Object();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void initializePlayer(int interruptible) {
      if (!this._initialized) {
         synchronized (this) {
            try {
               if (this._data == null) {
                  this._player = Manager.createPlayer(this._tuneLocation);
               } else {
                  this._player = Manager.createPlayer((InputStream)(new Object(this._data)), this._contentType);
               }

               if (this._player != null) {
                  this._player.addPlayerListener(AlertEngine.getInstance());
                  if (this._player instanceof Object) {
                     ((StreamDataControl)this._player).setKeyValue("interrupt_on_user_input", new Object(interruptible));
                     ((StreamDataControl)this._player).setKeyValue("audiosource", new Object(6));
                  }

                  this._player.realize();
                  this._initialized = true;
               }
            } catch (Throwable var7) {
               this._initialized = true;
               this.resetPlayer();
               throw new Object(
                  ((StringBuffer)(new Object("ContentAlertPlayer: initializePlayer ")))
                     .append(this._tuneLocation)
                     .append(" - ")
                     .append(e.toString())
                     .toString()
               );
            }
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void startPlayer(int volume) {
      synchronized (this) {
         if (this._initialized) {
            try {
               this.setVolume(volume);
               this._player.start();
            } catch (Throwable var7) {
               this.resetPlayer();
               throw new Object(
                  ((StringBuffer)(new Object("ContentAlertPlayer: startPlayer "))).append(this._tuneLocation).append(" - ").append(e.toString()).toString()
               );
            }
         }
      }
   }

   private final void setVolume(int volume) {
      VolumeControl vc = (VolumeControl)this._player.getControl("javax.microedition.media.control.VolumeControl");
      if (vc != null) {
         vc.setLevel(volume);
      } else {
         EventLogger.logEvent(
            6982943375119825480L, ((StringBuffer)(new Object("ContentAlertPlayer: can't setVolume for "))).append(this._tuneLocation).toString().getBytes(), 2
         );
      }
   }

   private final void resetPlayer() {
      if (this._initialized) {
         synchronized (this) {
            if (this._player != null) {
               this._player.removePlayerListener(AlertEngine.getInstance());
               this._player.close();
               this._player = null;
            }

            this._initialized = false;
         }
      }
   }

   @Override
   public final void startAlert(int volume, int interruptible) {
      synchronized (this) {
         if (!this._initialized) {
            this.initializePlayer(interruptible);
         }

         if (this._initialized) {
            this.startPlayer(volume);
         }
      }
   }

   @Override
   public final void stopAlert() {
      this.resetPlayer();
   }
}
