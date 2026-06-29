package net.rim.device.apps.internal.phone.api.ui.cdma;

import net.rim.device.api.system.Clipboard;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.EScreens.EScreen;
import net.rim.device.internal.EScreens.EScreenItemInfo;
import net.rim.device.internal.ui.component.IPEditField;

final class ServiceProgramUI$MyMenuItem extends MenuItem {
   int _id;
   EScreenItemInfo _itemInfo;
   private final ServiceProgramUI this$0;

   ServiceProgramUI$MyMenuItem(ServiceProgramUI _1, String label, int id) {
      super(label, 0, 0);
      this.this$0 = _1;
      this._id = id;
   }

   ServiceProgramUI$MyMenuItem(ServiceProgramUI _1, String label, EScreenItemInfo itemInfo, int id) {
      this(_1, label, id);
      this._itemInfo = itemInfo;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      switch (this._id) {
         case 0:
         default:
            this.this$0.popScreen();
            return;
         case 1:
            this.this$0.pushScreen(this._itemInfo.id, this._itemInfo.idCookie);
            return;
         case 2:
            this.this$0.refresh();
            return;
         case 3:
            int oldMode = this.this$0._model.getMode();
            int numUserItems = this.this$0._model.getNumUserItems();
            StringBuffer strBuf = new StringBuffer(128);

            for (int i = 0; i < numUserItems; i++) {
               Field f = this.this$0.getField(i);
               if (i != 0) {
                  strBuf.append('\n');
               }

               if (!(f instanceof BasicEditField)) {
                  if (f instanceof ChoiceField) {
                     ObjectChoiceField ocf = (ObjectChoiceField)f;
                     String data = (String)ocf.getChoice(ocf.getSelectedIndex());
                     strBuf.append(ocf.getLabel());
                     strBuf.append(data);
                  }
               } else {
                  BasicEditField bef = (BasicEditField)f;
                  int length = bef.getTextLength();
                  if (bef.getLabel() != null) {
                     length += bef.getLabel().length();
                  }

                  strBuf.append(bef.getText(0, length));
               }
            }

            if (numUserItems != 0) {
               int i = this.this$0.getWidth() / Font.getDefault().getAdvance('-');

               while (--i >= 0) {
                  strBuf.append('-');
               }
            }

            for (int i = 0; i < this.this$0._model.getNumItems(); i++) {
               if (i != 0) {
                  strBuf.append('\n');
               }

               for (int j = 0; j < this.this$0._model.getNumModes(); j++) {
                  this.this$0._model.setMode(j);
                  int length = this.this$0._model.getData(i + numUserItems, this.this$0._itemData);
                  if (j != 0) {
                     strBuf.append(" - ");
                  }

                  StringUtilities.append(strBuf, this.this$0._itemData, 0, length);
               }
            }

            Clipboard.getClipboard().put(strBuf.toString());
            this.this$0._model.setMode(oldMode);
            return;
         case 4:
            byte[] userData = null;
            StringBuffer strBuf = new StringBuffer(128);
            if ((this._itemInfo.flags & 256) == 0) {
               strBuf.append(this.this$0.getItemIndex()).append('\u0000');
               userData = strBuf.toString().getBytes();
            } else {
               for (int i = 0; i < this.this$0.getFieldCount() - 1; i++) {
                  Field f = this.this$0.getField(i);
                  if (f instanceof IPEditField) {
                     strBuf.append(IPEditField.parseIpAddr(((BasicEditField)f).getText())).append('\u0000');
                  } else if (f instanceof BasicEditField) {
                     strBuf.append(((BasicEditField)f).getText()).append('\u0000');
                  } else if (f instanceof ObjectChoiceField) {
                     strBuf.append(((ObjectChoiceField)f).getSelectedIndex()).append('\u0000');
                  } else if (f instanceof SeparatorField) {
                     strBuf.append('S').append('\u0000');
                  } else if (f instanceof ListField) {
                  }
               }

               userData = strBuf.toString().getBytes();
            }

            try {
               EScreen.doAction(this._itemInfo.id, this._itemInfo.idCookie, userData);
               if ((this._itemInfo.flags & 16384) == 0) {
                  this.this$0._model.refresh();
                  this.this$0.refresh();
                  return;
               } else {
                  this.this$0.popScreen();
               }
            } catch (Throwable var10) {
               String msg;
               if (e.getCode() == -6) {
                  msg = "Bad user data. Please verify and try again.";
               } else {
                  msg = "Unexpected error, code=" + e.getCode() + ".\nactionId=" + this._itemInfo.id + " actionIdCookie=" + this._itemInfo.idCookie;
               }

               Dialog.alert(msg);
               return;
            }
         case -1:
      }
   }
}
