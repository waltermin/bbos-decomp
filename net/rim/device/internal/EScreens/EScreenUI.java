package net.rim.device.internal.EScreens;

import net.rim.device.api.system.Clipboard;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.ui.component.HexEditField;
import net.rim.device.internal.ui.component.IPEditField;
import net.rim.device.internal.ui.component.PhoneNumberEditField;

public final class EScreenUI extends MainScreen implements ListFieldCallback {
   private EScreenModel _model;
   private EScreenController _controller;
   private EScreenModel _menuModel;
   private EScreenMenuInfo _menuInfo;
   private byte[] _itemData;
   private Font _plainFont;
   private Font _boldFont;
   private ListField _immutables;
   public static final int MENU_DETAILS = 1;
   public static final int MENU_REFRESH = 2;
   public static final int MENU_COPY_SCREEN = 3;
   public static final int MENU_ACTION = 4;
   private static final int MAX_DATA_LENGTH = 256;

   final void copyScreen() {
      int oldMode = this._model.getMode();
      int numUserItems = this._model.getNumUserItems();
      StringBuffer strBuf = new StringBuffer(128);

      for (int i = 0; i < numUserItems; i++) {
         Field f = this.getField(i);
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
         int i = this.getWidth() / Font.getDefault().getAdvance('-');

         while (--i >= 0) {
            strBuf.append('-');
         }
      }

      for (int i = 0; i < this._model.getNumItems(); i++) {
         if (i != 0) {
            strBuf.append('\n');
         }

         for (int j = 0; j < this._model.getNumModes(); j++) {
            this._model.setMode(j);

            try {
               int length = this._model.getData(i + numUserItems, this._itemData);
               if (j != 0) {
                  strBuf.append(" - ");
               }

               StringUtilities.append(strBuf, this._itemData, 0, length);
            } catch (EScreenException e) {
               strBuf.append('!');
               strBuf.append(e.toString());
               strBuf.append('!');
            }
         }
      }

      Clipboard.getClipboard().put(strBuf.toString());
      this._model.setMode(oldMode);
   }

   public final int setItemIndex(int newIndex) {
      if (newIndex < 0) {
         newIndex = 0;
      }

      if (newIndex < this._model.getNumUserItems()) {
         this.getField(newIndex).setFocus();
         return newIndex;
      }

      newIndex -= this._model.getNumUserItems();
      if (newIndex >= this._immutables.getSize()) {
         newIndex = this._immutables.getSize() - 1;
      }

      if (newIndex >= 0) {
         this._immutables.setFocus();
         this._immutables.setSelectedIndex(newIndex);
      }

      return newIndex;
   }

   public final int getListRowHeight() {
      return this._immutables.getRowHeight();
   }

   public final void refresh() {
      int size = this._model.getNumItems();
      if (this._immutables.getSize() != size) {
         int index = this._immutables.getSelectedIndex();
         this._immutables.setSize(size);
         if (size > 0 && this._immutables.getManager() == null) {
            this.add(this._immutables);
         } else if (size == 0 && this._immutables.getManager() != null) {
            this.delete(this._immutables);
         }

         if (index >= size) {
            index = size - 1;
         }

         if (index >= 0) {
            this._immutables.setSelectedIndex(index);
         }
      }

      this._immutables.invalidate();
   }

   public final void reset() {
      this.deleteRange(0, this.getFieldCount());
      this.addMutables();
      int iSize = this._model.getNumItems();
      this._immutables.setSize(iSize);
      if (iSize > 0) {
         this.add(this._immutables);
      }

      this.setTitle(this._model.getTitle());
   }

   public final int getItemIndex() {
      Field leaf = this.getLeafFieldWithFocus();
      int index = this.getFieldCount() - 1;

      while (index >= 0 && this.getField(index) != leaf) {
         index--;
      }

      if (leaf == this._immutables) {
         index += this._immutables.getSelectedIndex();
         if (this._model.getNumUserItems() != 0) {
            index--;
         }
      }

      return index;
   }

   @Override
   public final Object get(ListField listField, int index) {
      return "";
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return this.getWidth();
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      int id = index + this._model.getNumUserItems();
      int flag = this._model.getItemFlag(id);
      Font currFont = graphics.getFont();
      if ((flag & 8192) != 0) {
         if (!currFont.isBold() && this._boldFont != null) {
            graphics.setFont(this._boldFont);
         }
      } else if (currFont.isBold() && this._plainFont != null) {
         graphics.setFont(this._plainFont);
      }

      if ((flag & 1) == 0) {
         try {
            int size = this._model.getData(id, this._itemData);
            graphics.drawText(this._itemData, 0, size, 0, y, 54, width);
         } catch (EScreenException e) {
            graphics.drawText(e.toString(), 0, y, 54, width);
         }
      }

      if ((flag & 32) != 0) {
         int yy = y + this._immutables.getRowHeight() - 1;
         graphics.drawLine(0, yy, width, yy);
      }

      if (this.getFont() != currFont) {
         graphics.setFont(currFont);
      }
   }

   private final EditField makeEditField(String label, boolean hasData, byte[] data, int length) {
      return new EditField(label, hasData ? new String(data, 0, length) : null);
   }

   private final HexEditField makeHexEditField(String label, boolean hasData, byte[] data, int length) {
      return hasData ? new HexEditField(label, this._itemData, 0, length) : new HexEditField(label);
   }

   private final IPEditField makeIPEditField(String label, boolean hasData, byte[] data, int length) {
      String value = null;
      if (hasData) {
         StringBuffer strBuf = new StringBuffer(16);
         if (length != 4) {
            Dialog.alert("Bad data length for IPEditField, length=" + length);
            return null;
         }

         IPEditField.appendIpAddr(strBuf, data);
         value = strBuf.toString();
      }

      return new IPEditField(label, value);
   }

   private final EditField makeNumericEditField(String label, boolean hasData, byte[] data, int length) {
      String value = null;
      long lValue = 0;
      int iValue = 0;
      if (hasData) {
         switch (length) {
            case 1:
               value = Integer.toString(data[0]);
               break;
            case 2:
               iValue = data[0] << 8 | data[1] & 255;
               value = Integer.toString(iValue);
               break;
            case 4:
               for (int i = 0; i < 4; i++) {
                  iValue <<= 8;
                  iValue |= data[i] & 255;
               }

               value = Integer.toString(iValue);
               break;
            case 8:
               for (int i = 0; i < 8; i++) {
                  lValue <<= 8;
                  lValue |= data[i] & 255;
               }

               value = Long.toString(lValue);
               break;
            default:
               Dialog.alert("Bad length for numeric edit field, length=" + length);
               return null;
         }
      }

      return new EditField(label, value, Integer.MAX_VALUE, 83886080);
   }

   private final PhoneNumberEditField makePhoneEditField(String label, boolean hasData, byte[] data, int length) {
      return new PhoneNumberEditField(label, hasData ? new String(data, 0, length) : null);
   }

   @Override
   public final void close() {
      this._controller.popScreen();
   }

   public EScreenUI(EScreenModel model, EScreenController controller) {
      this._model = model;
      this._controller = controller;
      this._itemData = new byte[256];
      this._menuModel = new EScreenModel(model.getAccessLevel());
      this._menuInfo = new EScreenMenuInfo();
      int sWidth = Display.getWidth();
      Font font = null;
      String testStr = "From Reset: 18267243t@128tps=39:38::40";

      label56:
      try {
         FontFamily ff = FontFamily.forName(Graphics.isColor() ? "BBMillbank" : "System");
         int[] heights = ff.getHeights();
         Font f = null;

         for (int i = 0; i < heights.length; i++) {
            f = ff.getFont(0, heights[i]);
            if (sWidth < f.getAdvance(testStr)) {
               if (font == null) {
                  font = f;
               }
               break;
            }

            font = f;
         }
      } finally {
         break label56;
      }

      if (font != null) {
         this.setFont(font);
         this._plainFont = font;
         this._boldFont = font.derive(1);
      } else {
         Dialog.alert("Unable to find appropriate font. The EScreens are going to look weird.");
         this._plainFont = this.getFont();
         this._boldFont = this._plainFont;
      }

      this._immutables = new ListField();
      this._immutables.setCallback(this);
      this._immutables.setEmptyString("* No Engineering Items *", 4);
      this._immutables.setSearchable(false);
      this.add(this._immutables);
   }

   @Override
   protected final boolean invokeAction(int action) {
      switch (action) {
         case 1:
            int index = this.getItemIndex();
            boolean esAction = (this._model.getItemFlag(index) & 128) != 0;
            boolean esDetails = (this._model.getItemFlag(index) & 64) != 0;
            if (index >= 0 && (esAction || esDetails)) {
               if (!Trackball.isSupported()) {
                  this._menuInfo.menuId = 0;

                  try {
                     this._model.getMenuInfo(index, this._menuInfo);
                  } catch (EScreenException var6) {
                  }

                  esDetails &= this._menuInfo.menuId == 0;
               }

               EScreenItemInfo itemInfo = new EScreenItemInfo(this._model.getItemFlag(index), this._model.getItemId(index), this._model.getItemIdCookie(index));
               if (esAction) {
                  this.doAction(itemInfo);
                  return true;
               } else if (esDetails) {
                  this._controller.pushScreen(itemInfo.id, itemInfo.idCookie);
                  return true;
               } else if (this._menuInfo.menuId == 0) {
                  this._controller.pushScreen(itemInfo.id, itemInfo.idCookie);
                  return true;
               }
            }
         default:
            return false;
      }
   }

   private final ObjectChoiceField makeChoiceEditField(String label, int id, byte[] data, int length) {
      int intData = 0;

      for (int i = 0; i < length; i++) {
         intData <<= 8;
         intData |= data[i] & 255;
      }

      if (intData <= 0) {
         return null;
      }

      String[] choices = new String[intData];

      for (int i = 0; i < intData; i++) {
         try {
            this._model.setMode(i + 3);
            length = this._model.getData(id, this._itemData);
         } catch (EScreenException e) {
            Dialog.alert(
               "Unable to get choice option "
                  + i
                  + ", code="
                  + e.getCode()
                  + ".\n screenId="
                  + this._model.getScreenId()
                  + " idCookie="
                  + this._model.getScreenIdCookie()
                  + " itemId="
                  + id
            );
            continue;
         }

         choices[i] = new String(this._itemData, 0, length);
      }

      try {
         this._model.setMode(2);
         length = this._model.getData(id, this._itemData);
      } catch (EScreenException e) {
         Dialog.alert(
            "Unable to get choice initial index, code="
               + e.getCode()
               + ".\n screenId="
               + this._model.getScreenId()
               + " idCookie="
               + this._model.getScreenIdCookie()
               + " itemId="
               + id
         );
         return new ObjectChoiceField(label, choices);
      }

      intData = 0;

      for (int i = 0; i < length; i++) {
         intData <<= 8;
         intData |= this._itemData[i] & 255;
      }

      if (intData < 0 || intData >= choices.length) {
         intData = 0;
      }

      return new ObjectChoiceField(label, choices, intData);
   }

   private final void addMutables() {
      int num = this._model.getNumUserItems();

      for (int i = 0; i < num; i++) {
         int flag = this._model.getItemFlag(i);
         Field f = null;
         if ((flag & 1) == 0) {
            Dialog.alert(
               "Expected a mutable item, flag="
                  + flag
                  + ".\n screenId="
                  + this._model.getScreenId()
                  + " idCookie="
                  + this._model.getScreenIdCookie()
                  + " itemId="
                  + i
            );
         } else {
            int length;
            String label;
            try {
               this._model.setMode(0);
               length = this._model.getData(i, this._itemData);
               label = new String(this._itemData, 0, length);
            } catch (EScreenException e) {
               Dialog.alert(
                  "Unable to get mutable label, code="
                     + e.getCode()
                     + ".\n screenId="
                     + this._model.getScreenId()
                     + " idCookie="
                     + this._model.getScreenIdCookie()
                     + " itemId="
                     + i
               );
               continue;
            }

            boolean hasData = (flag & 1024) != 0 || (flag & 4096) != 0;
            if (hasData) {
               try {
                  this._model.setMode(1);
                  length = this._model.getData(i, this._itemData);
               } catch (EScreenException e) {
                  Dialog.alert(
                     "Unable to get mutable label, code="
                        + e.getCode()
                        + ".\n screenId="
                        + this._model.getScreenId()
                        + " idCookie="
                        + this._model.getScreenIdCookie()
                        + " itemId="
                        + i
                  );
                  continue;
               }
            }

            if ((flag & 2) != 0) {
               f = this.makeEditField(label, hasData, this._itemData, length);
            } else if ((flag & 4) != 0) {
               f = this.makeIPEditField(label, hasData, this._itemData, length);
            } else if ((flag & 8) != 0) {
               f = this.makeNumericEditField(label, hasData, this._itemData, length);
            } else if ((flag & 16) != 0) {
               f = this.makeHexEditField(label, hasData, this._itemData, length);
            } else if ((flag & 2048) != 0) {
               f = this.makePhoneEditField(label, hasData, this._itemData, length);
            } else if ((flag & 4096) != 0) {
               f = this.makeChoiceEditField(label, i, this._itemData, length);
               if (f == null) {
                  Dialog.alert(
                     "Found choice field with no options.\n screenId="
                        + this._model.getScreenId()
                        + " idCookie="
                        + this._model.getScreenIdCookie()
                        + " itemId="
                        + i
                  );
                  continue;
               }
            } else if ((flag & 32) != 0) {
               f = new SeparatorField();
            } else {
               Dialog.alert(
                  "Unknown mutable type. Flags="
                     + flag
                     + ".\n screenId="
                     + this._model.getScreenId()
                     + " idCookie="
                     + this._model.getScreenIdCookie()
                     + " itemId="
                     + i
               );
            }

            if (f != null) {
               if ((flag & 512) != 0) {
                  f.setEditable(false);
               }

               if ((flag & 8192) != 0 && this._boldFont != null) {
                  f.setFont(this._boldFont);
               }

               this.add(f);
            }
         }
      }

      if (num != 0) {
         this.add(new SeparatorField());
      }
   }

   private final void doAction(EScreenItemInfo itemInfo) {
      byte[] userData = null;
      if ((itemInfo.flags & 256) != 0) {
         StringBuffer strBuf = new StringBuffer(128);

         for (int i = 0; i < this.getFieldCount() - 1; i++) {
            Field f = this.getField(i);
            if (f.isEditable()) {
               if (f instanceof BasicEditField) {
                  strBuf.append(((BasicEditField)f).getText());
               }

               if (f instanceof ObjectChoiceField) {
                  ObjectChoiceField ocf = (ObjectChoiceField)f;
                  strBuf.append(ocf.getSelectedIndex());
               }

               strBuf.append('\u0000');
            }
         }

         userData = strBuf.toString().getBytes();
      }

      try {
         this._controller.doAction(itemInfo.id, itemInfo.idCookie, userData);
         if ((itemInfo.flags & 16384) == 0) {
            this.refresh();
            return;
         }

         this.close();
      } catch (EScreenException e) {
         String msg;
         if (e.getCode() == -6) {
            msg = "Bad user data. Please verify and try again.";
         } else {
            msg = "Unexpected error, code=" + e.getCode() + ".\nactionId=" + itemInfo.id + " actionIdCookie=" + itemInfo.idCookie;
         }

         Dialog.alert(msg);
      }
   }

   @Override
   public final boolean onSavePrompt() {
      return true;
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      int index = this.getItemIndex();
      this._menuInfo.menuId = 0;

      try {
         this._model.getMenuInfo(index, this._menuInfo);
      } catch (EScreenException e) {
         Dialog.alert(
            "Unable to get menuId, code="
               + e.getCode()
               + ".\n screenId="
               + this._model.getScreenId()
               + " idCookie="
               + this._model.getScreenIdCookie()
               + " itemId="
               + index
         );
      }

      if (index >= 0) {
         new EScreenItemInfo(this._model.getItemFlag(index), this._model.getItemId(index), this._model.getItemIdCookie(index));
         boolean actionOrDetails = false;
         menu.add(new EScreenUI$MyMenuItem(this, 3));
         int flags = this._model.getItemFlag(index);
         if ((flags & 64) != 0) {
            menu.add(new EScreenUI$MyMenuItem(this, 1, new EScreenItemInfo(flags, this._model.getItemId(index), this._model.getItemIdCookie(index))));
            actionOrDetails = true;
         } else if ((flags & 128) != 0) {
            menu.add(new EScreenUI$MyMenuItem(this, 4, new EScreenItemInfo(flags, this._model.getItemId(index), this._model.getItemIdCookie(index))));
            actionOrDetails = true;
         }

         menu.add(new EScreenUI$MyMenuItem(this, 2));
         if (this._menuInfo.menuId == 0 && actionOrDetails) {
            return;
         }
      }

      if (this._menuInfo.menuId != 0) {
         int numMenuItems;
         try {
            this._menuModel.setScreen(this._menuInfo.menuId, this._menuInfo.idCookie);
            numMenuItems = this._menuModel.getNumItems();
         } catch (EScreenException e) {
            Dialog.alert("Unable to set menu 'screen', code=" + e.getCode() + ".\n screenId=" + this._menuInfo.menuId + " idCookie=" + this._menuInfo.idCookie);
            numMenuItems = 0;
         }

         for (int i = 0; i < numMenuItems; i++) {
            int length;
            try {
               length = this._menuModel.getData(i, this._itemData);
            } catch (EScreenException e) {
               Dialog.alert(
                  "Unable to get data for menuItem, code="
                     + e.getCode()
                     + ".\n screenId="
                     + this._menuModel.getScreenId()
                     + " idCookie="
                     + this._menuModel.getScreenIdCookie()
                     + " itemId="
                     + i
               );
               continue;
            }

            int type;
            if ((this._menuModel.getItemFlag(i) & 128) != 0) {
               type = 4;
            } else {
               if ((this._menuModel.getItemFlag(i) & 64) == 0) {
                  Dialog.alert(
                     "Unacceptable menu flags, flags="
                        + this._menuModel.getItemFlag(i)
                        + ".\n screenId="
                        + this._menuModel.getScreenId()
                        + " idCookie="
                        + this._menuModel.getScreenIdCookie()
                        + " itemId="
                        + i
                  );
                  continue;
               }

               type = 1;
            }

            menu.add(
               new EScreenUI$MyMenuItem(
                  this,
                  type,
                  new String(this._itemData, 0, length),
                  new EScreenItemInfo(this._menuModel.getItemFlag(i), this._menuModel.getItemId(i), this._menuModel.getItemIdCookie(i))
               )
            );
         }
      }
   }

   @Override
   public final boolean navigationClick(int status, int time) {
      return (status & 1073741824) != 0 && this.invokeAction(1) ? true : super.navigationClick(status, time);
   }

   @Override
   protected final boolean openDevelopmentBackdoor(int backdoorCode) {
      int whichBugdisp = 0;
      String str = null;
      switch (backdoorCode) {
         case 1112885068:
            whichBugdisp = 1;
         case 1112885060:
            try {
               byte[] data = EScreen.getBugDispLog(whichBugdisp);
               str = "No BugDisp log to copy.";
               if (data != null && data.length != 0) {
                  Clipboard.getClipboard().put(new String(data));
                  str = "BugDisp log copied onto clipboard.";
               }
            } catch (EScreenException e) {
               str = "Failed to copy BugDisp log onto clipboard, code=" + e.getCode();
            }
            break;
         case 1381191247:
         case 1381191502:
         case 1381257030:
         case 1381257038:
            str = "Backdoor moved to Options/Host Routing Table";
      }

      if (str != null) {
         Dialog.inform(str);
      }

      return super.openDevelopmentBackdoor(backdoorCode);
   }

   @Override
   protected final boolean openProductionBackdoor(int backdoorCode) {
      this._model.keyPressed(this.getItemIndex(), backdoorCode);
      return super.openProductionBackdoor(backdoorCode);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (this.getLeafFieldWithFocus() != this._immutables && super.keyChar(key, status, time)) {
         return true;
      }

      switch (key) {
         case '\n':
            int index = this.getItemIndex();
            int flags = this._model.getItemFlag(index);
            if (flags != -1) {
               MenuItem menuItem = null;
               if ((flags & 64) != 0) {
                  menuItem = new EScreenUI$MyMenuItem(this, 1, new EScreenItemInfo(flags, this._model.getItemId(index), this._model.getItemIdCookie(index)));
               } else if ((flags & 128) != 0) {
                  menuItem = new EScreenUI$MyMenuItem(this, 4, new EScreenItemInfo(flags, this._model.getItemId(index), this._model.getItemIdCookie(index)));
               }

               if (menuItem != null) {
                  menuItem.run();
               }
            }

            return true;
         case ' ':
            if (this._model.nextMode()) {
               this.refresh();
            }

            return true;
         case 'c':
            this.copyScreen();
            return true;
         case 'r':
            this._controller.refresh(false);
            return true;
         default:
            return super.keyChar(key, status, time);
      }
   }

   private final String getMenuString(int id) {
      switch (id) {
         case 0:
            return "";
         case 1:
            return "Details";
         case 2:
            return "Refresh";
         case 3:
         default:
            return "Copy Screen";
         case 4:
            return "Execute Action";
      }
   }

   private final int getMenuOrdinal(int id) {
      switch (id) {
         case 0:
            return 0;
         case 1:
            return 131072;
         case 2:
            return 262144;
         case 3:
         default:
            return 65536;
         case 4:
            return 196608;
      }
   }

   private final int getMenuPriority(int id) {
      switch (id) {
         case 0:
            return 0;
         case 1:
            return 0;
         case 2:
            return 2;
         case 3:
         default:
            return 1;
         case 4:
            return 0;
      }
   }
}
