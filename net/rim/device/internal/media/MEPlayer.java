package net.rim.device.internal.media;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.media.Control;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.TimeBase;
import javax.microedition.media.protocol.SourceStream;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.plazmic.internal.mediaengine.MediaModel;
import net.rim.plazmic.internal.mediaengine.util.MEUtilities;
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
         throw new IllegalStateException("Player is CLOSED");
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
         throw new IllegalStateException("Player is CLOSED");
      }
   }

   @Override
   public Control getControl(String controlType) {
      if (this._state == 100) {
         throw new IllegalStateException("Player is Unrealized");
      } else if (controlType.equals("GUIControl")) {
         return this._guiControl;
      } else {
         return controlType.equals("MEDataControl") ? this._dataControl : null;
      }
   }

   @Override
   public Control[] getControls() {
      if (this._state == 100) {
         throw new IllegalStateException("Player is Unrealized");
      } else {
         return this._controls;
      }
   }

   @Override
   public long getDuration() {
      if (this._state == 0) {
         throw new IllegalStateException("Player is CLOSED");
      } else {
         return -1;
      }
   }

   @Override
   public long getMediaTime() {
      if (this._state != 100 && this._state != 0) {
         return this._player.getMediaTime() * 1000;
      } else {
         throw new IllegalStateException("Player is Unrealized");
      }
   }

   @Override
   public int getState() {
      return this._state;
   }

   @Override
   public TimeBase getTimeBase() {
      if (this._state == 100) {
         throw new IllegalStateException("Player is Unrealized");
      } else {
         return Manager.getSystemTimeBase();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void prefetch() throws MediaException {
      if (this._state == 0) {
         throw new IllegalStateException("Player is CLOSED");
      }

      if (this._state != 300 && this._state != 400) {
         label58:
         if (this._url != null) {
            net.rim.plazmic.mediaengine.MediaException e;
            try {
               try {
                  this._player.setMedia(this._manager.createMedia(this._url));
                  break label58;
               } catch (net.rim.plazmic.mediaengine.MediaException var6) {
                  e = var6;
               }
            } catch (Throwable var7) {
               throw new MediaException("Invalid URL - " + e.getMessage());
            }

            throw new MediaException(e.getMessage());
         } else {
            try {
               this._player.setMedia(this._manager.createResource(this._askingType, this._inputStream, null, null));
            } catch (net.rim.plazmic.mediaengine.MediaException e) {
               if (e.getData() instanceof IOException) {
                  throw new MediaException("Invalid input stream or content type - " + e.getMessage());
               }

               throw new MediaException(e.getMessage());
            }
         }

         this._state = 300;
      }
   }

   @Override
   public void realize() {
      if (this._state == 0) {
         throw new IllegalStateException("Player is CLOSED");
      }

      this.prefetch();
   }

   @Override
   public void setTimeBase(TimeBase master) {
      if (this._state == 100) {
         throw new IllegalStateException("Player is Unrealized");
      }
   }

   @Override
   public void start() throws MediaException {
      if (this._state == 0) {
         throw new IllegalStateException("Player is CLOSED");
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
      } catch (net.rim.plazmic.mediaengine.MediaException e) {
         throw new MediaException(e.getMessage());
      }

      this.notifyListeners("started");
      this._state = 400;
   }

   @Override
   public void stop() {
      if (this._state == 0) {
         throw new IllegalStateException("Player is CLOSED");
      }

      this._player.stop();
      this.notifyListeners("stopped");
      this._state = 300;
   }

   @Override
   public void setLoopCount(int count) {
   }

   @Override
   public long setMediaTime(long now) throws MediaException {
      if (this._state != 100 && this._state != 0) {
         if (now < 0) {
            now = 0;
         }

         try {
            this._player.setMediaTime(now / 1000);
         } catch (net.rim.plazmic.mediaengine.MediaException e) {
            throw new MediaException(e.getMessage());
         }

         return this._player.getMediaTime() * 1000;
      } else {
         throw new IllegalStateException("Player is CLOSED");
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
         throw new IllegalArgumentException("Invalid Key");
      }
   }

   @Override
   public Object getKeyValueObject(String key) throws MediaException {
      if (key.equals("locator")) {
         return this._url;
      } else if (key.equals("sourcestreams")) {
         return this._sourceStreams;
      } else if (key.equals("mimetype")) {
         return this._askingType;
      } else {
         throw new MediaException("Invalid Key");
      }
   }

   @Override
   public void setKeyValue(String key, Object value) throws MediaException {
      if (value == null) {
         throw new MediaException("Invalid Key");
      }

      if (key.equals("locator")) {
         this.setSource((String)value);
      } else if (key.equals("sourcestreams")) {
         this.setSource((SourceStream[])value);
      } else if (key.equals("mimetype")) {
         this.setMimeType((String)value);
      } else {
         throw new MediaException("Invalid Key");
      }
   }

   public MEPlayer() {
      this._manager = new MediaManager();
      this._manager.setProperty("Timeout", "2000");
      this._manager.setProperty("Retry", "2000");
      this._guiControl = new GUIControlImpl(this._player.getUI());
      this._dataControl = this;
      this._controls = new Control[2];
      this._controls[0] = this._guiControl;
      this._controls[1] = this._dataControl;
   }

   private void setSource(String locator) {
      this._url = locator;
      this._state = 100;
   }

   private void setSource(SourceStream[] sources) {
      this._sourceStreams = sources;
      this._inputStream = new DataSourceInputStream(this._sourceStreams[0]);
      this._state = 100;
   }

   private void setMimeType(String type) {
      this._askingType = type;
      this._state = 100;
   }
}
