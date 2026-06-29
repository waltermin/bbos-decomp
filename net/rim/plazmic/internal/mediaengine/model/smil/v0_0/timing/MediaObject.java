package net.rim.plazmic.internal.mediaengine.model.smil.v0_0.timing;

import javax.microedition.media.control.VolumeControl;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeAttributeSet$Writer;
import net.rim.plazmic.internal.mediaengine.event.Event;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.Region;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.contentregistry.ContentRegistry;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.contentregistry.ServiceProvider;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.contentregistry.serviceprovider.PlainTextServiceProvider;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.player.Player;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.player.PlayerListener;

public class MediaObject extends TimingObject implements PlayerListener {
   private String _alt = "";
   private String _uri;
   private String _mimeType;
   private Region _region;
   private ServiceProvider _serviceProvider;
   private Field _uiComponent;
   private Player _player;
   private boolean _realizedSuccessfully;
   private long _playerTime;
   private long _startTime;
   public static final int TYPE_UNKNOWN = 0;
   public static final int TYPE_AUDIO = 1;
   public static final int TYPE_ANIMATED_BITMAP = 2;
   public static final int TYPE_VIDEO = 3;

   public MediaObject(int id, TimeContainer parent) {
      super(id, parent);
   }

   public void setURI(String uri) {
      this._uri = uri;
   }

   public VolumeControl getVolumeControl() {
      return this._player != null ? this._player.getVolumeControl() : null;
   }

   public int getMediaType() {
      if (this._player == null) {
         return 0;
      }

      switch (this._player.getType()) {
         case 0:
            return 0;
         case 1:
         default:
            return 1;
         case 2:
            return 2;
         case 3:
            return 3;
      }
   }

   public void setMimeType(String mimeType) {
      this._mimeType = mimeType;
   }

   public void setAlt(String alt) {
      this._alt = alt;
   }

   public String getAlt() {
      return this._alt;
   }

   public void setRegion(Region region) {
      this._region = region;
   }

   public Field getUIComponent() {
      return this._uiComponent;
   }

   public boolean realizedSuccessfully() {
      return this._realizedSuccessfully;
   }

   public void realize() {
      if (!this._realizedSuccessfully) {
         this._serviceProvider = ContentRegistry.getServiceProvider(this._uri, this._mimeType);
      }

      if (this._serviceProvider != null) {
         this._realizedSuccessfully = true;
      } else if (!this._alt.equals("") && this._region != null) {
         PlainTextServiceProvider sp = new PlainTextServiceProvider();
         sp.createServices(this._alt);
         this._serviceProvider = sp;
      }

      if (this._serviceProvider != null) {
         if (this._region != null) {
            this._uiComponent = (Field)this._serviceProvider.getService("UI_Component");
         }

         this._player = (Player)this._serviceProvider.getService("Player");
      }
   }

   public void close() {
      this._realizedSuccessfully = false;
      this._serviceProvider = null;
      this._uiComponent = null;
      if (this._player != null) {
         this._player.close();
         this._player = null;
      }
   }

   @Override
   public boolean start(Event triggerEvent) {
      if (this._serviceProvider == null && this._uiComponent == null) {
         this.realize();
      }

      boolean started = super.start(triggerEvent);
      if (started) {
         this._startTime = triggerEvent._time;
         if (this._uiComponent != null) {
            if (this._uiComponent.getManager() == null) {
               this._region.add(this._uiComponent);
            }

            int backgroundColor = this._region.getBackgroundColor();
            if (backgroundColor != -1) {
               this.setThemeBackground(backgroundColor);
            }
         }

         if (this._player != null) {
            this._player.setTime(0);
            if (this.isImplicitDuration()) {
               this._player.setPlayerListener(this);
            }

            this._player.start();
         }
      }

      return started;
   }

   private void setThemeBackground(int color) {
      ThemeAttributeSet themeAttributeSet = this._uiComponent.getThemeAttributeSet();
      if (themeAttributeSet == null) {
         themeAttributeSet = (ThemeAttributeSet)(new Object());
      }

      ThemeAttributeSet$Writer themeWriter = themeAttributeSet.getWriterInternal();
      themeWriter.setColor(0, color);
      this._uiComponent.setThemeAttributeSet(themeAttributeSet);
   }

   @Override
   public boolean end(Event triggerEvent) {
      boolean finished = super.end(triggerEvent);
      if (finished) {
         if (this.getFill() == 2) {
            this.removeFill();
         }

         if (this._player != null) {
            this._player.stop();
         }
      }

      return finished;
   }

   @Override
   protected void removeFill() {
      try {
         if (this._uiComponent != null) {
            this._region.delete(this._uiComponent);
            return;
         }
      } finally {
         return;
      }
   }

   public void pause() {
      if (this._player != null) {
         this._playerTime = this._player.getTime();
         this._player.stop();
      }
   }

   public void resume() {
      if (this._player != null) {
         this._player.setTime(this._playerTime);
         this._player.start();
      }
   }

   @Override
   public void restart() {
      super.restart();
      this.pause();
      this._playerTime = 0;
   }

   public boolean isDiscreteMedia() {
      if (this._serviceProvider == null && this._uiComponent == null) {
         this.realize();
      }

      return this._player == null;
   }

   @Override
   public void notifyComplete(long time) {
      this.getInteractor().fireEvent(23, this.getId(), time + this._startTime);
   }
}
