package net.rim.device.apps.internal.browser.plugin.media.field;

import net.rim.device.api.system.Audio;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.MediaField;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.internal.media.MediaOptionsRegistry;
import net.rim.device.internal.media.MediaOptionsUtilities;
import net.rim.plazmic.internal.mediaengine.service.ModelInteractor;
import net.rim.plazmic.internal.mediaengine.service.node.ImageNode;

public final class VolumeField {
   private EncodedImage ENABLED = null;
   private EncodedImage DISABLED = null;
   private int _volumeLevel;
   private MediaBrowserField _changeListener;
   private MediaField _mfield;
   private ModelInteractor _modelBar;
   private ImageNode _volumeHighlight;
   private boolean _enabled;
   private int _additionalCount;
   private long _lastPressTime;
   private static final int MAX_VOLUME = 10;
   private static final String VOLUMESTR = "volume";

   public VolumeField(int initialVolumeLevel) {
      this._volumeLevel = initialVolumeLevel * 10 / 100;
      if (!Trackball.isSupported()) {
         this.ENABLED = ThemeManager.getActiveTheme().getImage("volume_highlight_enabled");
         this.DISABLED = ThemeManager.getActiveTheme().getImage("volume_highlight");
      }
   }

   public final void toggleEnabled() {
      this._enabled = !this._enabled;
      this._volumeHighlight.setImage(this._enabled ? this.ENABLED : this.DISABLED);
      this._mfield.invalidate();
   }

   public final boolean isEnabled() {
      return this._enabled;
   }

   public final int getVolumeLevel() {
      return this._volumeLevel * 100 / 10;
   }

   public final void increment(int amount) {
      this.setVolumeLevel(amount);
   }

   public final void decrement(int amount) {
      this.setVolumeLevel(amount);
   }

   public final void increment() {
      this.setVolumeLevel(this._volumeLevel + 1);
   }

   public final void decrement() {
      this.setVolumeLevel(this._volumeLevel - 1);
   }

   public final void setMediaField(MediaField field) {
      this._mfield = field;
   }

   public final void setModelInteractor(ModelInteractor interactor) {
      this._modelBar = interactor;
      this.setVolumeLevel(this._volumeLevel);
   }

   public final void setChangeListener(MediaBrowserField listener) {
      this._changeListener = listener;
   }

   public final void setVolumeHighlight(ImageNode volumeHighlight) {
      this._volumeHighlight = volumeHighlight;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void setVolumeLevel(int volume) {
      MediaOptionsRegistry instance = MediaOptionsRegistry.getInstance();
      label64:
      if (instance.getBoolean(-4387502259448276168L) || !AudioRouter.getInstance().isVolumeBoostModeSupported() || !Audio.isHeadsetConnected()) {
         volume = MathUtilities.clamp(0, volume, 10);
         synchronized (this) {
            this._volumeLevel = volume;
         }

         this.invalidate();
      } else {
         if (volume > 10) {
            long currentTime = System.currentTimeMillis();
            if (this._additionalCount == 3 && currentTime - this._lastPressTime <= 2000) {
               boolean var9 = false /* VF: Semaphore variable */;

               try {
                  var9 = true;
                  boolean ise = instance.getBoolean(2886183832722201160L);
                  if (!ise) {
                     MediaOptionsUtilities.getInstance().showBoostVolumeWarning();
                     var9 = false;
                  } else {
                     var9 = false;
                  }
               } finally {
                  if (var9) {
                     return;
                  }
               }
            } else if (this._additionalCount >= 0) {
               if (currentTime - this._lastPressTime > 2000) {
                  this._additionalCount = 1;
                  this._lastPressTime = currentTime;
               } else {
                  this._additionalCount++;
               }
            }
         } else {
            this._additionalCount = 0;
         }
         break label64;
      }
   }

   public final void invalidate() {
      if (this._modelBar != null) {
         String number = Integer.toString(this._volumeLevel * 10);
         this._modelBar.trigger(107, this._modelBar.getHandle("volume" + number), null);
      }
   }

   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      int amount = 0;
      if (dy > 0) {
         amount = 1;
      } else if (dy < 0) {
         amount = -1;
      }

      return this.onRoll(amount, status);
   }

   private final boolean onRoll(int amount, int status) {
      if (this._enabled) {
         if (amount < 0) {
            this.increment();
         } else {
            this.decrement();
         }

         if (this._changeListener != null) {
            this._changeListener.internalFieldChanged(this, -1);
         }

         return true;
      } else {
         return false;
      }
   }
}
