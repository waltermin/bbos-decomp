package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Audio;
import net.rim.device.api.system.Phone;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.api.ribbon.indicators.Indicator;
import net.rim.device.apps.api.ribbon.indicators.IndicatorManager;
import net.rim.device.apps.api.ribbon.indicators.TestPoint;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.system.AudioTTYListener;
import net.rim.device.internal.system.TTY;

public final class TTYRibbonIndicator implements Indicator, AudioTTYListener, TestPoint {
   private IndicatorManager _indicatorManager;
   private boolean _indicatorOn;
   private int _TTYUses = 0;
   private StringBuffer _strBuffer = (StringBuffer)(new Object(PhoneResources.getString(6025)));
   static TTYRibbonIndicator _instance;
   private static final int MAX_TTY_USES;
   public static final long TTY_RIBBON_INDICATOR;

   public static final void initialize() {
      if (_instance == null) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         _instance = (TTYRibbonIndicator)applicationRegistry.getOrWaitFor(-9082444837343367946L);
         if (_instance == null) {
            _instance = new TTYRibbonIndicator();
            applicationRegistry.put(-9082444837343367946L, _instance);
            Application app = (Application)VoiceServices.getVoiceApplication();
            Audio.addListener(app, _instance);
         }
      }
   }

   public static final TTYRibbonIndicator getInstance() {
      if (_instance == null) {
         initialize();
      }

      return _instance;
   }

   public TTYRibbonIndicator() {
      this._indicatorManager = IndicatorManager.getInstance();
   }

   public final void updateIndicator() {
      this.updateIndicator(TTY.getMode());
   }

   @Override
   public final void responseTTYModeChange(boolean success, int mode) {
      if (success) {
         this.updateIndicator(mode);
      } else {
         this.updateIndicator();
      }
   }

   @Override
   public final void responseHACModeChange(boolean success, boolean on) {
   }

   @Override
   public final void ttyStatusUpdate(boolean used) {
      if (this._indicatorOn) {
         if (used) {
            this._TTYUses = 0;
         } else {
            this._TTYUses++;
            System.out.println(((StringBuffer)(new Object("Status update: used - "))).append(used).append(" uses: ").append(this._TTYUses).toString());
            if (this._TTYUses >= 5 && !Phone.getInstance().isActive()) {
               if (Dialog.ask(3, PhoneResources.getString(6289), -1) == 4) {
                  TTY.requestModeChange(3);
               }

               this._TTYUses = 0;
            }
         }
      }
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

   private final void updateIndicator(int mode) {
      PhoneOptions.getOptions().setTTYMode(mode);
      this.updateIndicator(mode != 3);
   }

   private final void updateIndicator(boolean isTTYEnabled) {
      this._strBuffer.delete(0, this._strBuffer.length());
      int mode = TTY.getMode();
      if (mode == 0) {
         this._strBuffer.append(PhoneResources.getString(6025));
      } else if (mode == 2) {
         this._strBuffer.append("HCO");
      } else if (mode == 1) {
         this._strBuffer.append("VCO");
      }

      if (isTTYEnabled != this._indicatorOn) {
         this._indicatorOn = isTTYEnabled;
         if (this._indicatorOn) {
            this._indicatorManager.addIndicator(this);
            this._TTYUses = 0;
            return;
         }

         this._indicatorManager.removeIndicator(this);
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
      return "tty";
   }

   @Override
   public final void test(Object id, Object value) {
      if (value instanceof Object) {
         this.updateIndicator(value);
      }
   }
}
