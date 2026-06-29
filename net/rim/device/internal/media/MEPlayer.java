package net.rim.device.internal.media;

import java.io.InputStream;
import javax.microedition.media.Control;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.TimeBase;
import javax.microedition.media.protocol.SourceStream;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.plazmic.internal.mediaengine.MediaModel;
import net.rim.plazmic.internal.mediaengine.util.MEUtilities;
import net.rim.plazmic.mediaengine.MediaException;
import net.rim.plazmic.mediaengine.MediaManager;

class MEPlayer implements Player, StreamDataControl {
   private MediaManager _manager;
   private net.rim.plazmic.mediaengine.MediaPlayer _player = new net.rim.plazmic.mediaengine.MediaPlayer();
   private Control _guiControl;
   private Control _dataControl;
   private Control[] _controls;
   private Object[] _listeners;
   private String _url;
   private String _askingType;
   private InputStream _inputStream;
   private SourceStream[] _sourceStreams;
   private int _state;
   private static final String PLAYER_CLOSED = "Player is CLOSED";
   private static final String PLAYER_UNREALIZED = "Player is Unrealized";
   private static final String INVALID_KEY = "Invalid Key";

   protected void notifyListeners(String event) {
      if (this._listeners != null) {
         for (int lv = this._listeners.length - 1; lv >= 0; lv--) {
            try {
               ((PlayerListener)this._listeners[lv]).playerUpdate(this, event, null);
            } finally {
               continue;
            }
         }
      }
   }

   @Override
   public void addPlayerListener(PlayerListener playerListener) {
      this._listeners = ListenerUtilities.addListener(this._listeners, playerListener);
   }

   @Override
   public void removePlayerListener(PlayerListener playerListener) {
      this._listeners = ListenerUtilities.removeListener(this._listeners, playerListener);
   }

   @Override
   public void close() {
      if (this._state != 0) {
         this._player.close();
         this.notifyListeners("closed");
         this._state = 0;
      }
   }

   @Override
   public void deallocate() {
      if (this._state == 0) {
         throw new Object("Player is CLOSED");
      }

      if (this._state == 400) {
         label34:
         try {
            this.stop();
         } finally {
            break label34;
         }
      } else if (this._state == 200 || this._state == 100) {
         return;
      }

      this._player.close();
      this._state = 100;
   }

   @Override
   public String getContentType() {
      if (this._state != 100 && this._state != 0) {
         MediaModel media = MEUtilities.getMediaModel(this._player.getMedia());
         return media != null ? media.getContentType() : "";
      } else {
         throw new Object("Player is CLOSED");
      }
   }

   @Override
   public Control getControl(String controlType) {
      if (this._state == 100) {
         throw new Object("Player is Unrealized");
      } else if (controlType.equals("GUIControl")) {
         return this._guiControl;
      } else {
         return controlType.equals("MEDataControl") ? this._dataControl : null;
      }
   }

   @Override
   public Control[] getControls() {
      if (this._state == 100) {
         throw new Object("Player is Unrealized");
      } else {
         return this._controls;
      }
   }

   @Override
   public long getDuration() {
      if (this._state == 0) {
         throw new Object("Player is CLOSED");
      } else {
         return -1;
      }
   }

   @Override
   public long getMediaTime() {
      if (this._state != 100 && this._state != 0) {
         return this._player.getMediaTime() * 1000;
      } else {
         throw new Object("Player is Unrealized");
      }
   }

   @Override
   public int getState() {
      return this._state;
   }

   @Override
   public TimeBase getTimeBase() {
      if (this._state == 100) {
         throw new Object("Player is Unrealized");
      } else {
         return Manager.getSystemTimeBase();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void prefetch() {
      if (this._state == 0) {
         throw new Object("Player is CLOSED");
      }

      if (this._state != 300 && this._state != 400) {
         label58:
         if (this._url != null) {
            MediaException e;
            try {
               try {
                  this._player.setMedia(this._manager.createMedia(this._url));
                  break label58;
               } catch (MediaException var6) {
                  e = var6;
               }
            } catch (Throwable var7) {
               throw new Object(((StringBuffer)(new Object("Invalid URL - "))).append(e.getMessage()).toString());
            }

            throw new Object(e.getMessage());
         } else {
            try {
               this._player.setMedia(this._manager.createResource(this._askingType, this._inputStream, null, null));
            } catch (MediaException e) {
               if (e.getData() instanceof Object) {
                  throw new Object(((StringBuffer)(new Object("Invalid input stream or content type - "))).append(e.getMessage()).toString());
               }

               throw new Object(e.getMessage());
            }
         }

         this._state = 300;
      }
   }

   @Override
   public void realize() {
      if (this._state == 0) {
         throw new Object("Player is CLOSED");
      }

      this.prefetch();
   }

   @Override
   public void setTimeBase(TimeBase master) {
      if (this._state == 100) {
         throw new Object("Player is Unrealized");
      }
   }

   @Override
   public void start() {
      if (this._state == 0) {
         throw new Object("Player is CLOSED");
      }

      if (this._state != 100 && this._state != 200) {
         if (this._state == 400) {
            return;
         }
      } else {
         this.prefetch();
      }

      try {
         this._player.start();
      } catch (MediaException e) {
         throw new Object(e.getMessage());
      }

      this.notifyListeners("started");
      this._state = 400;
   }

   @Override
   public void stop() {
      if (this._state == 0) {
         throw new Object("Player is CLOSED");
      }

      this._player.stop();
      this.notifyListeners("stopped");
      this._state = 300;
   }

   @Override
   public void setLoopCount(int count) {
   }

   @Override
   public long setMediaTime(long now) {
      if (this._state != 100 && this._state != 0) {
         if (now < 0) {
            now = 0;
         }

         try {
            this._player.setMediaTime(now / 1000);
         } catch (MediaException e) {
            throw new Object(e.getMessage());
         }

         return this._player.getMediaTime() * 1000;
      } else {
         throw new Object("Player is CLOSED");
      }
   }

   @Override
   public String[] getKeys() {
      return new String[]{"author", "copyright", "date", "title", "locator", "mimetype"};
   }

   @Override
   public String getKeyValue(String key) {
      if (key.equals("locator")) {
         return this._url;
      } else if (key.equals("mimetype")) {
         return this._askingType;
      } else {
         throw new Object("Invalid Key");
      }
   }

   @Override
   public Object getKeyValueObject(String key) {
      if (key.equals("locator")) {
         return this._url;
      } else if (key.equals("sourcestreams")) {
         return this._sourceStreams;
      } else if (key.equals("mimetype")) {
         return this._askingType;
      } else {
         throw new Object("Invalid Key");
      }
   }

   @Override
   public void setKeyValue(String key, Object value) {
      if (value == null) {
         throw new Object("Invalid Key");
      }

      if (key.equals("locator")) {
         this.setSource((String)value);
      } else if (key.equals("sourcestreams")) {
         this.setSource((Object[])value);
      } else if (key.equals("mimetype")) {
         this.setMimeType((String)value);
      } else {
         throw new Object("Invalid Key");
      }
   }

   public MEPlayer() {
      this._manager = new MediaManager();
      this._manager.setProperty("Timeout", "2000");
      this._manager.setProperty("Retry", "2000");
      this._guiControl = (Control)(new Object(this._player.getUI()));
      this._dataControl = this;
      this._controls = new Object[2];
      this._controls[0] = this._guiControl;
      this._controls[1] = this._dataControl;
   }

   private void setSource(String locator) {
      this._url = locator;
      this._state = 100;
   }

   private void setSource(SourceStream[] sources) {
      this._sourceStreams = sources;
      this._inputStream = (InputStream)(new Object(this._sourceStreams[0]));
      this._state = 100;
   }

   private void setMimeType(String type) {
      this._askingType = type;
      this._state = 100;
   }
}
