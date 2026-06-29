package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.theme.Theme$Factory;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.ribbon.Action;
import net.rim.device.apps.api.ribbon.indicators.Indicator;
import net.rim.device.apps.api.ribbon.indicators.IndicatorManager;
import net.rim.device.apps.api.ribbon.indicators.TestPoint;
import net.rim.device.apps.internal.phone.options.PhoneOptions;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.vm.PersistentInteger;

public final class MissedCallIndicator implements Indicator, TestPoint {
   private int _newMissedCallsID = PersistentInteger.getId(-66177064401403627L, 0);
   private MissedCallPopup _missedCallPopup;
   private int _newMissedCallsCount;
   private Action _action;
   private StringBuffer _workBuffer = (StringBuffer)(new Object());
   private static MissedCallIndicator _instance;
   private static final long NEW_MISSED_CALLS_ID;
   public static final long GUID;

   public static final void initialize() {
      if (_instance == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _instance = (MissedCallIndicator)ar.getOrWaitFor(4293092249614032519L);
         if (_instance == null) {
            _instance = new MissedCallIndicator();
            ar.put(4293092249614032519L, _instance);
            IndicatorManager im = IndicatorManager.getInstance();
            if (im != null) {
               im.addIndicator(_instance);
            }
         }
      }
   }

   public static final MissedCallIndicator getInstance() {
      if (_instance == null) {
         initialize();
      }

      return _instance;
   }

   private MissedCallIndicator() {
      this._newMissedCallsCount = PersistentInteger.get(this._newMissedCallsID);
   }

   public final void onMissedCall(boolean userRejected) {
      if (!userRejected) {
         Theme$Factory factory = ThemeManager.getThemeFactory(ThemeManager.getActiveName());
         boolean suppressPopup = factory != null ? factory.getSuppressMissedCallDialog() : false;
         if (!suppressPopup && PhoneOptions.getOptions().getPhoneListViewType() == 3) {
            if (this._missedCallPopup != null) {
               this._missedCallPopup.onMissedCall();
               return;
            }

            this._missedCallPopup = new MissedCallPopup(this);
            this._missedCallPopup.show();
         }
      }
   }

   public final void setAction(Action action) {
      this._action = action;
   }

   protected final synchronized void modifyNewMissedCallsCount(int count, boolean updateIndicators) {
      int prevCount = this._newMissedCallsCount;
      this._newMissedCallsCount += count;
      if (this._newMissedCallsCount < 0) {
         this._newMissedCallsCount = 0;
      }

      this.persistNewMissedCallsCount();
      if (prevCount > 0 && this._newMissedCallsCount == 0) {
         this.disable(false);
      } else if (prevCount == 0 && this._newMissedCallsCount > 0) {
         this.enable();
      } else {
         this._action.update();
      }

      if (updateIndicators) {
         this.updateIndicators();
      }
   }

   private final void updateIndicators() {
      IndicatorManager im = IndicatorManager.getInstance();
      if (im != null) {
         im.updateIndicators();
      }
   }

   private final void persistNewMissedCallsCount() {
      PersistentInteger.set(this._newMissedCallsID, this._newMissedCallsCount);
   }

   public final void enable() {
      if (this._action != null) {
         this._action.set(2, "new");
      }
   }

   private final void disable(boolean updateIndicators) {
      if (this._action != null) {
         this._action.set(2, (String)((Object)null));
      }

      if (updateIndicators) {
         this.updateIndicators();
      }
   }

   public final void disable() {
      this.disable(true);
   }

   public final boolean isEnabled() {
      return this._newMissedCallsCount > 0;
   }

   public final int getNewMissedCallsCount() {
      return this._newMissedCallsCount;
   }

   public final boolean isPopupDisplayed() {
      synchronized (this) {
         return this._missedCallPopup != null;
      }
   }

   final void onPopupClosed() {
      synchronized (this) {
         this._missedCallPopup = null;
      }
   }

   private final int drawIcon(Graphics graphics, int xpos) {
      return PhoneResources.drawIcon(graphics, xpos, 0, 2);
   }

   @Override
   public final int draw(Graphics graphics, int width, int height, int flags) {
      if (!this.isEnabled()) {
         return 0;
      }

      int drawWidth = this.getWidth(graphics);
      this._workBuffer.setLength(0);
      this._workBuffer.append(this.getNewMissedCallsCount());
      int xpos = 0;
      if ((flags & 1) != 0) {
         xpos = width - drawWidth;
      }

      if ((flags & 1) != 0) {
         xpos += this.drawIcon(graphics, xpos);
         xpos += this.getGap(graphics.getFont());
      }

      if ((flags & 4) == 0) {
         xpos += graphics.drawText(this._workBuffer, 0, this._workBuffer.length(), xpos, 0, flags, width);
      }

      if ((flags & 2) != 0) {
         if ((flags & 4) == 0) {
            xpos += this.getGap(graphics.getFont());
         }

         xpos += this.drawIcon(graphics, xpos);
      }

      return drawWidth;
   }

   @Override
   public final int getWidth(Graphics graphics) {
      if (this.isEnabled() && this.getNewMissedCallsCount() >= 1) {
         this._workBuffer.setLength(0);
         this._workBuffer.append(this.getNewMissedCallsCount());
         Font font = graphics.getFont();
         return PhoneResources.getIconWidth(font, 2) + this.getGap(font) + font.getBounds(this._workBuffer, 0, this._workBuffer.length());
      } else {
         return 0;
      }
   }

   @Override
   public final int getHeight(Graphics graphics) {
      return graphics.getFont().getHeight();
   }

   private final int getGap(Font font) {
      return font.getFontFamily().getName().equals("System") ? 0 : font.getHeight() / 8;
   }

   @Override
   public final int getPriority() {
      return 4;
   }

   @Override
   public final String getTypeName() {
      return "missedcall";
   }

   @Override
   public final void test(Object id, Object value) {
      if (value instanceof Object) {
         Boolean b = (Boolean)value;
         if (b) {
            this.enable();
            return;
         }

         this.disable();
      }
   }
}
