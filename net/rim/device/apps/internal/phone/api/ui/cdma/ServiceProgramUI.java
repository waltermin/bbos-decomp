package net.rim.device.apps.internal.phone.api.ui.cdma;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.EScreens.EScreenItemInfo;
import net.rim.device.internal.EScreens.EScreenMenuInfo;
import net.rim.device.internal.EScreens.EScreenModel;
import net.rim.device.internal.system.EngineeringDataListener;
import net.rim.device.internal.ui.component.HexEditField;
import net.rim.device.internal.ui.component.IPEditField;
import net.rim.device.internal.ui.component.PhoneNumberEditField;
import net.rim.vm.Array;

public final class ServiceProgramUI extends MainScreen implements ListFieldCallback, EngineeringDataListener {
   private EScreenModel _model;
   private EScreenModel _menuModel;
   private EScreenMenuInfo _menuInfo;
   private byte[] _itemData;
   private ServiceProgramUI$DependentInfo[] _dependencies = new ServiceProgramUI$DependentInfo[0];
   private int[] _screenIdStack;
   private int[] _screenIdCookieStack;
   private int[] _screenItemIndexStack;
   private UiApplication _app = UiApplication.getUiApplication();
   private Font _plainFont;
   private Font _boldFont;
   private ListField _immutables;
   public static final int MENU_CLOSE;
   public static final int MENU_DETAILS;
   public static final int MENU_REFRESH;
   public static final int MENU_COPY_SCREEN;
   public static final int MENU_ACTION;
   private static final int MAX_DATA_LENGTH;

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
         int size = this._model.getData(id, this._itemData);
         graphics.drawText(this._itemData, 0, size, 0, y, 54, width);
      }

      if ((flag & 32) != 0) {
         int yy = y + this._immutables.getRowHeight() - 1;
         graphics.drawLine(0, yy, width, yy);
      }

      if (this.getFont() != currFont) {
         graphics.setFont(currFont);
      }
   }

   @Override
   public final void engDataInitialized() {
   }

   @Override
   public final void engDataChanged() {
      this._model.refresh();
      this.refresh();
   }

   @Override
   public final void engDataLogworthy(int type) {
   }

   @Override
   public final void engResponseMasterReset(int code) {
   }

   @Override
   public final void engOTASPResponse(byte[] response) {
   }

   @Override
   public final void engServiceProgramEvent(int code) {
      boolean reset = (code & 0xFF) == 1;
      code >>= 8;
      switch (code) {
         case 2:
            this.statusShow("Could not read data", true);
            return;
         case 3:
            if (reset) {
               Application.getApplication().invokeLater(new ServiceProgramUI$ResetDialog());
               return;
            }

            UiApplication.getUiApplication().invokeLater(new ServiceProgramUI$StatusRunnable(this, "Write Successful.", true));
            return;
         case 4:
            this.statusShow("Could not write data", false);
            return;
         case 10:
            this.statusShow("A-Key is Invalid", false);
      }
   }

   private final HexEditField makeHexEditField(String label, boolean hasData, byte[] data, int length, int maxLength) {
      return (HexEditField)(hasData ? new Object(label, this._itemData, 0, length, maxLength) : new Object(label, maxLength));
   }

   private final PhoneNumberEditField makePhoneEditField(String label, boolean hasData, byte[] data, int length, int maxLength) {
      return (PhoneNumberEditField)(new Object(label, (String)(hasData ? new Object(data, 0, length) : null), maxLength, 6));
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final ObjectChoiceField makeChoiceEditField(String label, int id, byte[] data, int length) {
      int intData = 0;
      int dependedOn = 0;

      for (int i = 0; i < length; i++) {
         intData <<= 8;
         intData |= data[i] & 255;
      }

      if (intData <= 0) {
         return null;
      }

      String[] choices = new Object[intData];

      for (int i = 0; i < intData; i++) {
         try {
            this._model.setMode(i + 4);
            length = this._model.getData(id, this._itemData);
         } catch (Throwable var21) {
            Dialog.alert(
               ((StringBuffer)(new Object("Unable to get choice option ")))
                  .append(i)
                  .append(", code=")
                  .append(e.getCode())
                  .append(".\n screenId=")
                  .append(this._model.getScreenId())
                  .append(" idCookie=")
                  .append(this._model.getScreenIdCookie())
                  .append(" itemId=")
                  .append(id)
                  .toString()
            );
            continue;
         }

         choices[i] = (String)(new Object(this._itemData, 0, length));
      }

      try {
         this._model.setMode(2);
         length = this._model.getData(id, this._itemData);
      } catch (Throwable var20) {
         Dialog.alert(
            ((StringBuffer)(new Object("Unable to get choice initial index, code=")))
               .append(e.getCode())
               .append(".\n screenId=")
               .append(this._model.getScreenId())
               .append(" idCookie=")
               .append(this._model.getScreenIdCookie())
               .append(" itemId=")
               .append(id)
               .toString()
         );
         return (ObjectChoiceField)(new Object(label, choices));
      }

      intData = 0;

      for (int i = 0; i < length; i++) {
         intData <<= 8;
         intData |= this._itemData[i] & 255;
      }

      if (intData < 0 || intData >= choices.length) {
         intData = 0;
      }

      try {
         this._model.setMode(3);
         length = this._model.getData(id, this._itemData);
      } catch (Throwable var19) {
         Dialog.alert(
            ((StringBuffer)(new Object("Unable to get choice initial index, code=")))
               .append(e.getCode())
               .append(".\n screenId=")
               .append(this._model.getScreenId())
               .append(" idCookie=")
               .append(this._model.getScreenIdCookie())
               .append(" itemId=")
               .append(id)
               .toString()
         );
         return (ObjectChoiceField)(new Object(label, choices));
      }

      if (length > 0) {
         dependedOn = convertToInt(this._itemData, length);
         ServiceProgramUI$DependentInfo info = this.getInfo(dependedOn);
         if (info != null) {
            ((ServiceProgramUI$ChoiceFieldDependentInfo)info).add(id);
         } else {
            this.addDependentInfo(new ServiceProgramUI$ChoiceFieldDependentInfo(dependedOn, id));
         }
      }

      return (ObjectChoiceField)(new Object(label, choices, intData, 1152921504606846976L));
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void popScreen() {
      Screen scr = this._app.getActiveScreen();
      if (scr != this) {
         this._app.popScreen(scr);
      } else {
         while (true) {
            int length = this._screenIdStack.length - 1;
            Array.resize(this._screenIdStack, length);
            Array.resize(this._screenIdCookieStack, length);
            Array.resize(this._screenItemIndexStack, length);
            if (length == 0) {
               this._app.popScreen(scr);
               Application.getApplication().removeRadioListener(this);
               break;
            }

            int screenId = this._screenIdStack[length - 1];
            int idCookie = this._screenIdCookieStack[length - 1];
            int itemIndex = this._screenItemIndexStack[length - 1];

            try {
               this._model.setScreen(screenId, idCookie);
               this.reset();
               this.setItemIndex(itemIndex);
               break;
            } catch (Throwable var8) {
               Dialog.alert(
                  ((StringBuffer)(new Object("Unable to popScreen, code=")))
                     .append(e.getCode())
                     .append(".\n screenId=")
                     .append(screenId)
                     .append(" idCookie=")
                     .append(idCookie)
                     .toString()
               );
               continue;
            }
         }
      }

      if (this._app.getScreenCount() == 0) {
         System.exit(0);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void pushScreen(int screenId, int idCookie) {
      int index = this.getItemIndex();
      if (0 != screenId) {
         this._dependencies = null;
         this._dependencies = new ServiceProgramUI$DependentInfo[0];
         if (this._screenItemIndexStack.length != 0) {
            this._screenItemIndexStack[this._screenItemIndexStack.length - 1] = index;
         }

         Arrays.add(this._screenIdStack, screenId);
         Arrays.add(this._screenIdCookieStack, idCookie);
         Arrays.add(this._screenItemIndexStack, 0);

         try {
            this._model.setScreen(screenId, idCookie);
            this.reset();
         } catch (Throwable var6) {
            Dialog.alert(
               ((StringBuffer)(new Object("Unable to set screen, code=")))
                  .append(e.getCode())
                  .append(".\nscreenId=")
                  .append(screenId)
                  .append(" idCookie=")
                  .append(idCookie)
                  .toString()
            );
            this.popScreen();
            return;
         }
      }
   }

   private final void addDependentInfo(ServiceProgramUI$DependentInfo info) {
      if (this.findIndex(info._dependsOn) == -1) {
         Arrays.add(this._dependencies, info);
      }
   }

   private final ServiceProgramUI$DependentInfo getInfo(int id) {
      int index = this.findIndex(id);
      return index == -1 ? null : this._dependencies[index];
   }

   private final int findIndex(int id) {
      for (int i = 0; i < this._dependencies.length; i++) {
         if (this._dependencies[i]._dependsOn == id) {
            return i;
         }
      }

      return -1;
   }

   public ServiceProgramUI(EScreenModel model) {
      this._model = model;
      this._itemData = new byte[256];
      this._menuModel = (EScreenModel)(new Object(model.getAccessLevel()));
      this._menuInfo = (EScreenMenuInfo)(new Object());
      this._screenIdStack = new int[]{this._model.getScreenId()};
      this._screenIdCookieStack = new int[]{this._model.getScreenIdCookie()};
      this._screenItemIndexStack = new int[]{0, -804651006, 0, 1};
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
      } else {
         Dialog.alert("Unable to find appropriate font. The EScreens are going to look weird.");
         this._plainFont = this.getFont();
      }

      this._boldFont = this._plainFont.derive(1);
      this._immutables = (ListField)(new Object());
      this._immutables.setCallback(this);
      this._immutables.setEmptyString("* No Engineering Items *", 4);
      this._immutables.setSearchable(false);
      this.add(this._immutables);
      this.reset();
      Application.getApplication().addRadioListener(this);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final void makeMenu(Menu menu, int instance) {
      int index = this.getItemIndex();
      this._menuInfo.menuId = 0;

      label184:
      try {
         this._model.getMenuInfo(index, this._menuInfo);
      } catch (Throwable var20) {
         Dialog.alert(
            ((StringBuffer)(new Object("Unable to get menuId, code=")))
               .append(e.getCode())
               .append(".\n screenId=")
               .append(this._model.getScreenId())
               .append(" idCookie=")
               .append(this._model.getScreenIdCookie())
               .append(" itemId=")
               .append(index)
               .toString()
         );
         break label184;
      }

      if (index >= 0) {
         EScreenItemInfo itemInfo = (EScreenItemInfo)(new Object(
            this._model.getItemFlag(index), this._model.getItemId(index), this._model.getItemIdCookie(index)
         ));
         boolean actionOrDetails = false;
         menu.add(new ServiceProgramUI$MyMenuItem(this, "Copy Screen", 3));
         if ((this._model.getItemFlag(index) & 64) != 0) {
            menu.add(new ServiceProgramUI$MyMenuItem(this, "Details", itemInfo, 1));
            actionOrDetails = true;
         } else if ((this._model.getItemFlag(index) & 128) != 0) {
            menu.add(new ServiceProgramUI$MyMenuItem(this, "Execute Action", itemInfo, 4));
            actionOrDetails = true;
         }

         menu.add(new ServiceProgramUI$MyMenuItem(this, "Refresh", 2));
         menu.addSeparator();
         if (this._menuInfo.menuId == 0 && actionOrDetails) {
            return;
         }
      }

      if (this._menuInfo.menuId != 0) {
         int numMenuItems;
         label178:
         try {
            this._menuModel.setScreen(this._menuInfo.menuId, this._menuInfo.idCookie);
            numMenuItems = this._menuModel.getNumItems();
         } catch (Throwable var19) {
            Dialog.alert(
               ((StringBuffer)(new Object("Unable to set menu 'screen', code=")))
                  .append(e.getCode())
                  .append(".\n screenId=")
                  .append(this._menuInfo.menuId)
                  .append(" idCookie=")
                  .append(this._menuInfo.idCookie)
                  .toString()
            );
            numMenuItems = 0;
            break label178;
         }

         for (int i = 0; i < numMenuItems; i++) {
            int length;
            try {
               length = this._menuModel.getData(i, this._itemData);
            } catch (Throwable var18) {
               Dialog.alert(
                  ((StringBuffer)(new Object("Unable to get data for menuItem, code=")))
                     .append(e.getCode())
                     .append(".\n screenId=")
                     .append(this._menuModel.getScreenId())
                     .append(" idCookie=")
                     .append(this._menuModel.getScreenIdCookie())
                     .append(" itemId=")
                     .append(i)
                     .toString()
               );
               continue;
            }

            int type;
            if ((this._menuModel.getItemFlag(i) & 128) != 0) {
               type = 4;
            } else {
               if ((this._menuModel.getItemFlag(i) & 64) == 0) {
                  Dialog.alert(
                     ((StringBuffer)(new Object("Unacceptable menu flags, flags=")))
                        .append(this._menuModel.getItemFlag(i))
                        .append(".\n screenId=")
                        .append(this._menuModel.getScreenId())
                        .append(" idCookie=")
                        .append(this._menuModel.getScreenIdCookie())
                        .append(" itemId=")
                        .append(i)
                        .toString()
                  );
                  continue;
               }

               type = 1;
            }

            menu.add(
               new ServiceProgramUI$MyMenuItem(
                  this,
                  (String)(new Object(this._itemData, 0, length)),
                  (EScreenItemInfo)(new Object(this._menuModel.getItemFlag(i), this._menuModel.getItemId(i), this._menuModel.getItemIdCookie(i))),
                  type
               )
            );
         }

         if (numMenuItems != 0) {
            menu.addSeparator();
         }
      }

      menu.add(new ServiceProgramUI$MyMenuItem(this, "Close", 0));
   }

   @Override
   public final boolean onSavePrompt() {
      int answer = Dialog.ask(3, PhoneResources.getString(6332), -1);
      return answer != -1;
   }

   @Override
   public final boolean isDirty() {
      for (int i = 0; i < this.getFieldCount(); i++) {
         if (this.getField(i).isDirty()) {
            return true;
         }
      }

      return false;
   }

   @Override
   protected final boolean openProductionBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1229935680:
            this._model.keyPressed(this.getItemIndex(), backdoorCode);
            return super.openProductionBackdoor(backdoorCode);
         case 1229935681:
         default:
            Application.getApplication().invokeLater(new ServiceProgramUI$1(this));
            return true;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void addMutables() {
      int num = this._model.getNumUserItems();

      for (int i = 0; i < num; i++) {
         int flag = this._model.getItemFlag(i);
         Field f = null;
         if ((flag & 1) == 0) {
            Dialog.alert(
               ((StringBuffer)(new Object("Expected a mutable item, flag=")))
                  .append(flag)
                  .append(".\n screenId=")
                  .append(this._model.getScreenId())
                  .append(" idCookie=")
                  .append(this._model.getScreenIdCookie())
                  .append(" itemId=")
                  .append(i)
                  .toString()
            );
         } else {
            int length;
            String label;
            try {
               this._model.setMode(0);
               length = this._model.getData(i, this._itemData);
               label = (String)(new Object(this._itemData, 0, length));
            } catch (Throwable var21) {
               Dialog.alert(
                  ((StringBuffer)(new Object("Unable to get mutable label, code=")))
                     .append(e.getCode())
                     .append(".\n screenId=")
                     .append(this._model.getScreenId())
                     .append(" idCookie=")
                     .append(this._model.getScreenIdCookie())
                     .append(" itemId=")
                     .append(i)
                     .toString()
               );
               continue;
            }

            int maxLength = 0;
            if ((flag & 4096) == 0 && (flag & 32) == 0) {
               try {
                  this._model.setMode(2);
                  length = this._model.getData(i, this._itemData);
                  maxLength = convertToInt(this._itemData, length);
               } catch (Throwable var20) {
                  Dialog.alert(
                     ((StringBuffer)(new Object("Unable to get maximum Length, code=")))
                        .append(e.getCode())
                        .append(".\n screenId=")
                        .append(this._model.getScreenId())
                        .append(" idCookie=")
                        .append(this._model.getScreenIdCookie())
                        .append(" itemId=")
                        .append(i)
                        .toString()
                  );
                  continue;
               }
            }

            boolean hasData = (flag & 1024) != 0 || (flag & 4096) != 0;
            if (hasData) {
               try {
                  this._model.setMode(1);
                  length = this._model.getData(i, this._itemData);
               } catch (Throwable var19) {
                  Dialog.alert(
                     ((StringBuffer)(new Object("Unable to get mutable label, code=")))
                        .append(e.getCode())
                        .append(".\n screenId=")
                        .append(this._model.getScreenId())
                        .append(" idCookie=")
                        .append(this._model.getScreenIdCookie())
                        .append(" itemId=")
                        .append(i)
                        .toString()
                  );
                  continue;
               }
            }

            if ((flag & 2) != 0) {
               f = this.makeEditField(label, hasData, this._itemData, length, maxLength, i);
            } else if ((flag & 4) != 0) {
               f = this.makeIPEditField(label, hasData, this._itemData, length, maxLength);
            } else if ((flag & 8) != 0) {
               f = this.makeNumericEditField(label, hasData, this._itemData, length, maxLength);
            } else if ((flag & 16) != 0) {
               f = this.makeHexEditField(label, hasData, this._itemData, length, maxLength);
            } else if ((flag & 2048) != 0) {
               f = this.makePhoneEditField(label, hasData, this._itemData, length, maxLength);
            } else if ((flag & 4096) != 0) {
               f = this.makeChoiceEditField(label, i, this._itemData, length);
               if (f == null) {
                  Dialog.alert(
                     ((StringBuffer)(new Object("Found choice field with no options.\n screenId=")))
                        .append(this._model.getScreenId())
                        .append(" idCookie=")
                        .append(this._model.getScreenIdCookie())
                        .append(" itemId=")
                        .append(i)
                        .toString()
                  );
                  continue;
               }
            } else if ((flag & 32) != 0) {
               f = (Field)(new Object());
            } else {
               Dialog.alert(
                  ((StringBuffer)(new Object("Unknown mutable type. Flags=")))
                     .append(flag)
                     .append(".\n screenId=")
                     .append(this._model.getScreenId())
                     .append(" idCookie=")
                     .append(this._model.getScreenIdCookie())
                     .append(" itemId=")
                     .append(i)
                     .toString()
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
         this.add((Field)(new Object()));
      }

      this.addDependentListeners();
   }

   private final void addDependentListeners() {
      for (int i = 0; i < this._dependencies.length; i++) {
         ServiceProgramUI$DependentInfo var10000 = this._dependencies[i];
         if (!(this._dependencies[i] instanceof ServiceProgramUI$EditFieldDependentInfo)) {
            var10000 = this._dependencies[i];
            if (this._dependencies[i] instanceof ServiceProgramUI$ChoiceFieldDependentInfo) {
               ServiceProgramUI$ChoiceFieldDependentInfo cfdi = (ServiceProgramUI$ChoiceFieldDependentInfo)var10000;
               ChoiceField cf = (ChoiceField)this.getField(cfdi._dependsOn);
               ServiceProgramUI$ChoiceFieldChangeListener li = new ServiceProgramUI$ChoiceFieldChangeListener(cf, cfdi, this);
               cf.setChangeListener(li);
               li.fieldChanged(cf, 0);
            }
         } else {
            ServiceProgramUI$EditFieldDependentInfo efdi = (ServiceProgramUI$EditFieldDependentInfo)var10000;
            EditField f = (EditField)this.getField(efdi._dependsOn);
            f.setChangeListener(new ServiceProgramUI$EditFieldChangeListener(f, efdi, this));
         }
      }
   }

   private static final int convertToInt(byte[] data, int len) {
      int val = 0;

      for (int i = 0; i < len; i++) {
         val <<= 8;
         val |= data[i] & 255;
      }

      return val;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final EditField makeEditField(String label, boolean hasData, byte[] data, int length, int maxLength, int id) {
      int len = 0;
      byte[] itemData = new byte[256];
      int dependedOn = 0;
      char delim = 0;

      label43:
      try {
         this._model.setMode(3);
         len = this._model.getData(id, itemData);
         dependedOn = convertToInt(itemData, len);
         if (len > 0) {
            this._model.setMode(4);
            if (this._model.getData(id, itemData) > 0) {
               delim = (char)itemData[0];
            }
         }
      } catch (Throwable var13) {
         Dialog.alert(
            ((StringBuffer)(new Object("Unable to get dependent id, code=")))
               .append(e.getCode())
               .append(".\n screenId=")
               .append(this._model.getScreenId())
               .append(" idCookie=")
               .append(this._model.getScreenIdCookie())
               .append(" itemId=")
               .append(id)
               .toString()
         );
         break label43;
      }

      if (len > 0) {
         ServiceProgramUI$DependentInfo info = this.getInfo(dependedOn);
         if (info != null) {
            ((ServiceProgramUI$EditFieldDependentInfo)info).add(id, delim);
         } else {
            this.addDependentInfo(new ServiceProgramUI$EditFieldDependentInfo(dependedOn, id, delim));
         }
      }

      return (EditField)(new Object(label, (String)(hasData ? new Object(data, 0, length) : null), maxLength, 0));
   }

   private final IPEditField makeIPEditField(String label, boolean hasData, byte[] data, int length, int maxLength) {
      String value = null;
      if (hasData) {
         StringBuffer strBuf = (StringBuffer)(new Object(16));
         if (length != 4) {
            Dialog.alert(((StringBuffer)(new Object("Bad data length for IPEditField, length="))).append(length).toString());
            return null;
         }

         IPEditField.appendIpAddr(strBuf, data);
         value = strBuf.toString();
      }

      return (IPEditField)(new Object(label, value));
   }

   private final EditField makeNumericEditField(String label, boolean hasData, byte[] data, int length, int maxLength) {
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
               Dialog.alert(((StringBuffer)(new Object("Bad length for numeric edit field, length="))).append(length).toString());
               return null;
         }
      }

      return (EditField)(new Object(label, value, maxLength, 83886080));
   }

   private final void statusShow(String message, boolean exitScreen) {
      UiApplication.getUiApplication().invokeLater(new ServiceProgramUI$StatusRunnable(this, message, exitScreen));
   }
}
