package net.rim.device.apps.internal.phone;

import net.rim.device.api.system.Display;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.api.ribbon.indicators.VoicemailIconManager;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.options.CallForwardRibbonIndicator;
import net.rim.device.apps.internal.phone.options.SSManager;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.ui.IconCollection;

final class PhoneStatusField extends ObjectChoiceField implements RadioStatusListener, PhoneEventListener, GlobalEventListener, FieldChangeListener {
   private int[] _lineIds;
   private int _oldIndex;
   private boolean _setDeviceNumberOnUnfocus = false;
   private static final int PHONE_STATUS_FONT_SIZE = 8;
   private static final String PHONE_STATUS_FONT = "BBMillbank";

   final void setDevicePhoneNumber(int lineId) {
      if (this.isFocus()) {
         this._setDeviceNumberOnUnfocus = true;
      } else {
         int defaultIndex = 0;
         this._lineIds = PhoneUtilities.getAvailableLineIds();
         String[] choices = new Object[this._lineIds.length];
         if (this._lineIds.length > 1) {
            for (int i = this._lineIds.length - 1; i >= 0; i--) {
               String number = PhoneUtilities.getLineNumber(this._lineIds[i], true);
               choices[i] = ((StringBuffer)(new Object())).append(PhoneUtilities.getLineDescription(this._lineIds[i])).append(": ").append(number).toString();
               if (this._lineIds[i] == lineId) {
                  defaultIndex = i;
               }
            }
         } else if (this._lineIds.length == 1) {
            String number = PhoneUtilities.getLineNumber(this._lineIds[0], true);
            if (number == null) {
               number = PhoneResources.getString(117);
            }

            choices[0] = ((StringBuffer)(new Object())).append(PhoneResources.getString(168)).append(": ").append(number).toString();
         }

         this.setChoices(choices);
         this.setSelectedIndex(defaultIndex);
         this._oldIndex = defaultIndex;
      }
   }

   final void addToMenu(SystemEnabledMenu menu, int instance, boolean systemLocked) {
      MenuItem menuItem = menu.getDefault();
      if (menuItem != null) {
         menuItem.setPriority(611152);
      }

      menu.add(new PhoneStatusField$EditLabelsVerb());
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (context != Integer.MIN_VALUE
         && field == this
         && this._oldIndex != this.getSelectedIndex()
         && PhoneUtilities.setCurrentLine(this._lineIds[this.getSelectedIndex()])) {
         this._oldIndex = this.getSelectedIndex();
      }
   }

   @Override
   public final void signalLevel(int level) {
   }

   @Override
   public final void networkStarted(int networkId, int service) {
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void radioTurnedOff() {
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
      this.setDevicePhoneNumber(PhoneUtilities.getCurrentLineId());
      this.invalidate();
   }

   @Override
   public final void phoneEventNotify(int eventId, int param1, Object param2) {
      switch (eventId) {
         case 150120:
         default:
            this.invalidate();
         case 150119:
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -8040378802380461050L) {
         this.setOptionsMenuText(PhoneResources.getString(6321));
         this.setDevicePhoneNumber(PhoneUtilities.getCurrentLineId());
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   PhoneStatusField() {
      super(null, null, -1, 1152921508901814272L);
      this._lineIds = PhoneUtilities.getAllLineIds();
      this.setDevicePhoneNumber(PhoneUtilities.getCurrentLineId());
      this.setId("phonestatusfield");
      this.setTag(Tag.create("list"));
      if (RadioInfo.getNetworkType() == 4) {
         VoiceServices.addRadioStatusListener(new WeakRadioStatusListener(this));
      }

      if (this._lineIds.length > 1) {
         VoiceServices.addPhoneEventListener((PhoneEventListener)(new Object(this)));
      }

      int fontStyle = PhoneUtilities.smallScreen() ? 0 : 1;
      boolean var4 = false /* VF: Semaphore variable */;

      label36:
      try {
         var4 = true;
         FontFamily e = FontFamily.forName("BBMillbank");
         this.setFont(e.getFont(fontStyle, 8, 3));
         var4 = false;
      } finally {
         if (var4) {
            this.setFont(Font.getDefault().derive(fontStyle, 8, 3));
            break label36;
         }
      }

      this.setOptionsMenuText(PhoneResources.getString(6321));
      this.setChangeListener(this);
   }

   @Override
   protected final boolean keyUp(int keycode, int time) {
      return Keypad.key(keycode) != 27;
   }

   @Override
   protected final void onFocus(int direction) {
      this._oldIndex = this.getSelectedIndex();
      super.onFocus(direction);
   }

   @Override
   protected final void onUnfocus() {
      super.onUnfocus();
      if (this._setDeviceNumberOnUnfocus) {
         this.setDevicePhoneNumber(PhoneUtilities.getCurrentLineId());
         this._setDeviceNumberOnUnfocus = false;
      }
   }

   private final void correctFont() {
      Font font = Font.getDefault();
      int maxFontSize = this.getMaxFontSize();
      if (font.getHeight(3) > maxFontSize) {
         font = font.derive(font.getStyle(), maxFontSize, 3);
      } else {
         font = font.derive(font.getStyle(), font.getHeight());
      }

      this.setFont(font);
   }

   @Override
   protected final void applyFont() {
      this.correctFont();
      super.applyFont();
   }

   private final int getMaxFontSize() {
      return PhoneUtilities.isCharm240x260Screen() ? 8 : 10;
   }

   @Override
   protected final int getWidthOfChoice(int index) {
      return Display.getWidth() - 2;
   }

   @Override
   protected final void drawChoice(int index, Graphics graphics, int x, int y, int flags, int width) {
      if (this._lineIds.length > 1) {
         if (SSManager.isCallForwardUnconditionalActive(this._lineIds[index])) {
            width -= CallForwardRibbonIndicator.getInstance().getWidth(graphics);
            PhoneResources.drawIcon(graphics, width, y, 13);
         }

         if (VoicemailIconManager.getInstance().isIndicatorOn(this._lineIds[index])) {
            IconCollection ic = VoicemailIconManager.getIconCollection();
            width -= ic.getWidth(graphics.getFont());
            ic.paint(graphics, width, y, 0);
         }
      }

      super.drawChoice(index, graphics, x, y, flags, width);
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      switch (Keypad.key(keycode)) {
         case 32:
         case 4098:
            return super.keyDown(keycode, time);
         default:
            return false;
      }
   }
}
