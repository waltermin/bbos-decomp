package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Audio;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.api.ribbon.indicators.Indicator;
import net.rim.device.apps.api.ribbon.indicators.IndicatorManager;
import net.rim.device.apps.api.ribbon.indicators.TestPoint;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.system.AudioInternal;
import net.rim.device.internal.system.AudioTTYListener;

public final class HACRibbonIndicator implements Indicator, AudioTTYListener, TestPoint {
   private IndicatorManager _indicatorManager;
   private boolean _indicatorOn;
   private StringBuffer _strBuffer = (StringBuffer)(new Object(PhoneResources.getString(6297)));
   static HACRibbonIndicator _instance;
   public static final long HAC_RIBBON_INDICATOR;

   public static final void initialize() {
      if (_instance == null) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         _instance = (HACRibbonIndicator)applicationRegistry.getOrWaitFor(8209130174428269783L);
         if (_instance == null) {
            _instance = new HACRibbonIndicator();
            applicationRegistry.put(8209130174428269783L, _instance);
            Application app = (Application)VoiceServices.getVoiceApplication();
            Audio.addListener(app, _instance);
         }
      }
   }

   public static final HACRibbonIndicator getInstance() {
      if (_instance == null) {
         initialize();
      }

      return _instance;
   }

   public HACRibbonIndicator() {
      this._indicatorManager = IndicatorManager.getInstance();
   }

   public final void updateIndicator() {
      this.updateIndicator(AudioInternal.getHACMode());
   }

   @Override
   public final void responseTTYModeChange(boolean success, int mode) {
   }

   @Override
   public final void responseHACModeChange(boolean success, boolean on) {
      this.updateIndicator(on);
   }

   @Override
   public final void ttyStatusUpdate(boolean used) {
   }

   @Override
   public final void ttyDataAvailable() {
   }

   @Override
   public final void ttyDataReady() {
   }

   @Override
   public final void ttyReadBufferFull() {
   }

   private final void updateIndicator(boolean isHACEnabled) {
      if (isHACEnabled != this._indicatorOn) {
         this._indicatorOn = isHACEnabled;
         if (this._indicatorOn) {
            this._indicatorManager.addIndicator(this);
         } else {
            this._indicatorManager.removeIndicator(this);
         }

         PhoneOptions.getOptions().setHACMode(isHACEnabled ? 1 : 0);
      }
   }

   @Override
   public final int draw(Graphics graphics, int width, int height, int flags) {
      if (this._indicatorOn) {
         boolean iconOnLeft = (flags & 1) != 0;
         return iconOnLeft
            ? graphics.drawText(this._strBuffer, 0, this._strBuffer.length(), 0, 0, 6, width)
            : graphics.drawText(this._strBuffer, 0, this._strBuffer.length(), 0, 0, 5, width);
      } else {
         return 0;
      }
   }

   @Override
   public final int getWidth(Graphics graphics) {
      return graphics.getFont().getBounds(this._strBuffer, 0, this._strBuffer.length());
   }

   @Override
   public final int getHeight(Graphics graphics) {
      return graphics.getFont().getHeight();
   }

   @Override
   public final int getPriority() {
      return 4;
   }

   @Override
   public final String getTypeName() {
      return "hac";
   }

   @Override
   public final void test(Object id, Object value) {
      if (value instanceof Object) {
         this.updateIndicator(value);
      }
   }
}
