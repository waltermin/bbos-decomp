package net.rim.device.apps.internal.browser.plugin.media.field;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.MediaField;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.MathUtilities;
import net.rim.plazmic.internal.mediaengine.service.node.ImageNode;
import net.rim.plazmic.internal.mediaengine.service.node.VisualNode;

public final class SeekablePositionField {
   private boolean _enabled;
   private boolean _seekable;
   private int _position;
   private int _totaltime = -1;
   private int _fivePercentIncrement;
   private int _cacheTime;
   private MediaField _mfield;
   private MediaBrowserField _browserField;
   private int _barWidth;
   private int _sliderWidth;
   private int _halfSliderWidth;
   private int _sliderTrailWidth;
   private int _sliderTrailX;
   private int _sliderX;
   private ImageNode _slider;
   private SeekablePositionField$ResizableVisualNode _streaming;
   private SeekablePositionField$ResizableVisualNode _sliderTrail;
   private double _streamingPercentage;
   private static final EncodedImage ENABLED = ThemeManager.getActiveTheme().getImage("slider_enabled");
   private static final EncodedImage DISABLED = ThemeManager.getActiveTheme().getImage("slider");

   public SeekablePositionField(MediaBrowserField browserField) {
      this._browserField = browserField;
   }

   public final int getTimePosition() {
      return this._seekable ? this._position * 1000 : -1;
   }

   public final boolean isEnabled() {
      return this._enabled;
   }

   public final void toggleEnabled() {
      if (this._seekable && this._totaltime >= 0) {
         this._enabled = !this._enabled;
         this._slider.setImage(this._enabled ? ENABLED : DISABLED);
         if (this._enabled) {
            this._cacheTime = this._position;
         }

         this._mfield.invalidate();
      }
   }

   public final void cancelSeek() {
      this.toggleEnabled();
      this.seekPosition(this._cacheTime);
   }

   private final void seekPosition(int seconds) {
      if (this._totaltime >= 0) {
         if (this._mfield != null) {
            seconds = MathUtilities.clamp(0, seconds, this._totaltime);
            synchronized (this) {
               this._position = seconds;
            }

            int xLoc = this.getXLoc() + this._halfSliderWidth << 16;
            int width = this._sliderTrailWidth - (xLoc - this._sliderTrailX);
            this._sliderTrail.setX(xLoc);
            this._sliderTrail.setWidth(width);
            this._slider.setX((this.getXLoc() + this._sliderX << 16) - this._sliderTrailX);
            this.updateStreamingNode();
            this._mfield.invalidate();
            if (this._enabled && seconds >= 0) {
               this._browserField.updateDurationField(seconds * 1000);
            }
         }
      }
   }

   final void resetAspectRatio() {
      if (this._sliderTrail != null) {
         this._sliderTrail.setAspectRatio(0);
      }

      if (this._streaming != null) {
         this._streaming.setAspectRatio(0);
      }
   }

   public final void setTotalTime(int milliseconds) {
      if (milliseconds >= 1000) {
         this._totaltime = milliseconds / 1000;
         this._fivePercentIncrement = this._totaltime * 5 / 100;
         this._fivePercentIncrement = Math.max(1, this._fivePercentIncrement);
      }
   }

   public final synchronized void updateTime(int milliseconds) {
      if (!this._enabled) {
         this.seekPosition(milliseconds / 1000);
      }
   }

   public final synchronized void updateStreaming(double percentage) {
      if (!this._enabled) {
         this._streamingPercentage = percentage;
         this.updateStreamingNode();
         if (this._mfield != null) {
            this._mfield.invalidate();
         }
      }
   }

   private final void updateStreamingNode() {
      if (this._streaming != null) {
         int xLoc = this.getXLoc() + this._halfSliderWidth << 16;
         int width = Fixed32.mul(this._sliderTrailWidth, Fixed32.tenThouToFP((int)(this._streamingPercentage * 4666723172467343360L)));
         this._streaming.setX(xLoc);
         this._streaming.setWidth(width + this._sliderTrailX - xLoc);
      }
   }

   public final void setSeekable(boolean seekable) {
      this._seekable = seekable;
   }

   public final void setMediaField(MediaField field) {
      this._mfield = field;
   }

   public final void setBarNode(ImageNode bar) {
      this._barWidth = bar.getWidth() >> 16;
   }

   public final void setSliderNode(ImageNode slider) {
      this._slider = slider;
      this._sliderWidth = slider.getWidth() >> 16;
      this._halfSliderWidth = this._sliderWidth >> 1;
      this._sliderX = this._slider.getX() >> 16;
   }

   public final void setSliderTrailNode(VisualNode sliderTrail) {
      this._sliderTrail = new SeekablePositionField$ResizableVisualNode(sliderTrail, null);
      this._sliderTrailWidth = this._sliderTrail.getWidth();
      this._sliderTrailX = sliderTrail.getX();
   }

   public final void setStreamingNode(VisualNode streaming) {
      this._streaming = new SeekablePositionField$ResizableVisualNode(streaming, null);
   }

   private final int getXLoc() {
      return (this._sliderTrailX >> 16) + (this._barWidth - this._sliderWidth) * this._position / this._totaltime;
   }

   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      int amount = 0;
      if ((status & 1073741824) != 0) {
         dx = dy;
      }

      if (dx > 0) {
         amount = 1;
      } else if (dx < 0) {
         amount = -1;
      }

      return this.onRoll(amount, status);
   }

   private final boolean onRoll(int amount, int status) {
      if (this._enabled && this._seekable) {
         int increment = this._fivePercentIncrement;
         boolean altPressed = (status & 1) != 0;
         if (altPressed) {
            increment = Math.min(this._fivePercentIncrement, 5);
         }

         if (amount != 0) {
            this.seekPosition(this._position + amount * increment);
         }

         return true;
      } else {
         return false;
      }
   }
}
