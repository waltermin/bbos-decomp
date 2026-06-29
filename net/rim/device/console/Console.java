package net.rim.device.console;

import java.util.Vector;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.DeviceInternal;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.system.Radio;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.internal.system.ApplicationManagerInternal;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.ApplicationSwitcher;
import net.rim.device.internal.ui.component.TraceBackDialog;
import net.rim.vm.DebugSupport;

public final class Console extends UiApplication implements KeyListener, ListFieldCallback, GlobalEventListener {
   private Vector _listItems;
   private MainScreen _screen;
   private ListField _listField;
   private LabelField _mainTitleField;
   private ApplicationManager _appManager;
   private ApplicationSwitcher _appSwitchScreen;
   private Console$ApplicationSwitchEnd _appSwitchEnd = new Console$ApplicationSwitchEnd(this);

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void invokeSelected() {
      if (this._listItems.size() != 0) {
         int index = this._listField.getSelectedIndex();
         ApplicationDescriptor ad = (ApplicationDescriptor)this._listItems.elementAt(index);

         try {
            this._appManager.runApplication(ad);
         } catch (Throwable var5) {
            Dialog.inform(ex.getMessage());
            return;
         }
      }
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      int size = this._listItems.size();
      if (size != 0) {
         ApplicationDescriptor ad = (ApplicationDescriptor)this._listItems.elementAt(index);
         graphics.drawText(ad.getName(), 0, y);
      }
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return 0;
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }

   @Override
   public final Object get(ListField listField, int index) {
      ApplicationDescriptor ad = (ApplicationDescriptor)this._listItems.elementAt(index);
      return ad.getName();
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\n':
            this.invokeSelected();
            return true;
         case 'r':
            switch (RadioInfo.getState()) {
               case -1:
                  Status.show("Radio busy");
                  return true;
               case 0:
               default:
                  Status.show("Turning radio on");
                  Radio.requestPowerOn();
                  return true;
               case 1:
                  Status.show("Turning radio off");
                  Radio.requestPowerOff();
                  return true;
               case 2:
                  Status.show("Battery too low for radio");
                  return true;
            }
         default:
            return false;
      }
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyRepeat(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyStatus(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyUp(int keycode, int time) {
      return false;
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 256826950193107649L) {
         if (this._screen != null) {
            this.populateListField();
            return;
         }
      } else if (guid == -4232371946002803201L) {
         if (this._screen != null) {
            this.populateListField();
            return;
         }
      } else {
         if (guid == 7563637690172082503L) {
            if (this._appSwitchScreen == null) {
               this._appSwitchScreen = (ApplicationSwitcher)(new Object(this._appSwitchEnd, 0));
               return;
            }

            this._appSwitchScreen.selectNext(data0);
            return;
         }

         if (guid == 9056933960126321432L && object0 instanceof Object) {
            TraceBackDialog.show(this, (String)object0, object1);
         }
      }
   }

   private final void populateListField() {
      int[] handles = CodeModuleManager.getModuleHandles();
      this._listItems.removeAllElements();

      for (int i = 0; i < handles.length; i++) {
         ApplicationDescriptor[] descriptors = CodeModuleManager.getApplicationDescriptors(handles[i]);
         if (descriptors != null) {
            for (int j = 0; j < descriptors.length; j++) {
               if ((descriptors[j].getFlags() & 2) == 0) {
                  this._listItems.addElement(descriptors[j]);
               }
            }
         }
      }

      this._listField.setSize(this._listItems.size());
      this._mainTitleField.setText(((StringBuffer)(new Object("BlackBerry Platform "))).append(DeviceInfo.getPlatformVersion()).toString());
   }

   private Console() {
      if (!InternalServices.isDateTimeValid()) {
         DeviceInternal.setDateTime(1104537600000L);
      }

      this._appManager = ApplicationManager.getApplicationManager();
      if (!((ApplicationManagerInternal)this._appManager).setConsoleProcess()) {
         throw new Object();
      }

      this._screen = new Console$ConsoleScreen(this);
      this._mainTitleField = (LabelField)(new Object(null, 1152921504606846976L));
      this._screen.setTitle(this._mainTitleField);
      this._listItems = (Vector)(new Object());
      this._listField = (ListField)(new Object());
      this._listField.setSearchable(false);
      this._listField.setCallback(this);
      this._screen.add(this._listField);
      this._screen.addKeyListener(this);
      this.populateListField();
      this.pushScreen(this._screen);
      this.addGlobalEventListener(this);
      if (!DeviceInfo.isSimulator() || DebugSupport.getenv("JvmRadioOff") == null) {
         Radio.requestPowerOn();
      }
   }

   public static final void main(String[] args) {
      new Console().enterEventDispatcher();
   }
}
