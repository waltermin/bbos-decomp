package net.rim.device.apps.internal.options.items.network;

import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.Confirmation;
import net.rim.device.apps.api.ui.ExitVerb;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.system.NetworkInfo;
import net.rim.device.internal.system.SIMServiceTable;

public final class PrefNetworkListOptions extends AppsMainScreen implements MoveableListFieldCallback, SimCardEfHandlerCallback, Confirmation {
   private int _state;
   private int _flag;
   private SimCardEfHandler _simCardHandler;
   private PrefNetworkList _netList;
   private int _maxListSize;
   private NetworkInfo _netInfo;
   private MoveableListField _listField;
   private PrefNetworkListOptions$StatusScreen _statusScreen;
   private static final int STATE_FAILED;
   private static final int STATE_IDLE;
   private static final int STATE_READ;
   private static final int STATE_WRITE;
   private static final int FLAG_ADD_NETWORK;

   protected final Verb getCloseVerb() {
      return this._netList.isListChanged() ? ExitVerb.createCloseVerb(0, this) : ExitVerb.createCloseVerb(0, null);
   }

   public final void addToList(NetworkInfo netInfo) {
      if (netInfo != null) {
         switch (this._state) {
            case 0:
            default:
               this._flag &= -2;
               this._netInfo = null;
               return;
            case 1:
               if (this._netList != null) {
                  int errorIndex = -1;
                  if (this._netList.isItemInList(netInfo) != -1) {
                     errorIndex = 1894;
                  } else if (this._netList.getListSize() >= this._maxListSize) {
                     errorIndex = 1892;
                  }

                  if (errorIndex != -1) {
                     Status.show(OptionsResources.getString(errorIndex), Bitmap.getPredefinedBitmap(2), 2000);
                     if (UiApplication.getUiApplication().getActiveScreen() == this) {
                        UiApplication.getUiApplication().popScreen(this);
                     }
                  } else {
                     this._netList.add(0, netInfo);
                     this.refresh();
                  }
               }

               this._flag &= -2;
               this._netInfo = null;
            case -1:
               return;
            case 2:
               this._netInfo = netInfo;
               this._flag |= 1;
         }
      }
   }

   @Override
   public final void efHandlerActionFailed(int failureCode) {
      this._statusScreen.close();
      System.err.println("efHandlerActionFailed");
      if (this._state != 2) {
         if (this._state == 3) {
            System.err.println("Warn: efHandlerActionFailed to write");
            this.showSimError(failureCode);
            this._state = 1;
         } else {
            this._statusScreen.close();
         }
      } else {
         if (!DeviceInfo.isSimulator()) {
            this._state = 0;
            if ((this._flag & 1) != 0) {
               this.addToList(this._netInfo);
            }

            this.showSimError(failureCode);
            if (UiApplication.getUiApplication().getActiveScreen() == this) {
               UiApplication.getUiApplication().popScreen(this);
            }
         } else {
            NetworkInfo[] netInfos = new Object[0];
            this._maxListSize = 6;
            this._netList.setList(netInfos);

            for (int i = 0; i < 4; i++) {
               NetworkInfo netInfo = (NetworkInfo)(new Object());
               netInfo.setMcc(1911);
               netInfo.setMnc(512 + i + 1);
               this._netList.add(i, netInfo);
            }

            this._state = 1;
            UiApplication.getUiApplication().pushScreen(this);
         }

         System.err.println("Warn: efHandlerActionFailed to read");
      }

      this.refresh();
   }

   @Override
   public final void efHandlerActionSuccess() {
      this._statusScreen.close();
      if (this._state == 2) {
         NetworkInfo[] netInfos = this._simCardHandler.getNetworkInfos();
         this._maxListSize = this._simCardHandler.getNumRecords();
         if (netInfos != null) {
            this._netList.setList(netInfos);
         }

         this._state = 1;
         this.refresh();
         UiApplication.getUiApplication().pushScreen(this);
         if ((this._flag & 1) != 0) {
            this.addToList(this._netInfo);
            return;
         }
      } else if (this._state == 3) {
         this._state = 1;
         if (UiApplication.getUiApplication().getActiveScreen() == this) {
            UiApplication.getUiApplication().popScreen(this);
         }
      }
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      this.drawListRow(listField, graphics, index, index, y, width);
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int pIndex, int vIndex, int y, int width) {
      NetworkInfo netInfo = this._netList.getItem(pIndex);
      String name = NetworkOptionsUtils.getPredefinedNetworkName(netInfo.getNetworkId());
      String idStr = NetworkOptionsUtils.buildNetIdString(netInfo);
      if (name == null || name.length() == 0) {
         name = OptionsResources.getString(1885);
      }

      String str = ((StringBuffer)(new Object())).append(vIndex + 1).append('.').append(' ').append(name).toString();
      int widthDrawn = graphics.drawText(str, 0, y, 70, width);
      int valueWidth = listField.getFont().getBounds(idStr);
      int drawStyle = 5;
      if (valueWidth > width - widthDrawn) {
         drawStyle = 70;
      }

      graphics.drawText(idStr, widthDrawn, y, drawStyle, width - widthDrawn);
   }

   @Override
   public final Object get(ListField listField, int index) {
      return this._netList.getItem(index);
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return listField.getPreferredWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }

   @Override
   public final void moveFinished(ListField field, int start, int end) {
      this._netList.change(start, end, this._netList.getItem(start));
      this._listField.invalidate();
   }

   @Override
   public final boolean confirm(Verb verb, Object context) {
      boolean result = true;
      if (this._netList.isListChanged()) {
         switch (Dialog.ask(1, CommonResource.getString(10003))) {
            case -2:
            case 0:
               break;
            case -1:
               result = false;
               break;
            case 1:
               new PrefNetworkListOptions$PrefNetworkSaveVerb(this).invoke(context);
               return false;
            case 2:
            default:
               return true;
         }
      }

      return result;
   }

   @Override
   public final void efHandlerActionStarted() {
      if (this._state == 2) {
         this._statusScreen.setStatus(OptionsResources.getString(1913));
      } else {
         if (this._state == 3) {
            this._statusScreen.setStatus(OptionsResources.getString(1912));
         }
      }
   }

   private final boolean isValidSIMInserted() {
      try {
         return SIMCard.isValid();
      } finally {
         ;
      }
   }

   private final void refresh() {
      this._listField.setSize(this._netList.getListSize());
      this._listField.setSelectedIndex(this._netList.getIndex());
      if (this._netList.isListChanged()) {
         this._listField.setDirty(true);
      } else {
         this._listField.setDirty(false);
      }
   }

   private final void showSimError(int failureCode) {
      StringBuffer errStr = (StringBuffer)(new Object(OptionsResources.getString(1888)));
      if (this.isValidSIMInserted()) {
         switch (failureCode) {
            case 2:
            case 5:
               break;
            case 3:
            default:
               errStr.append(":\n").append(OptionsResources.getString(1889));
               break;
            case 4:
               errStr.append(":\n").append(OptionsResources.getString(1891));
               break;
            case 6:
               errStr.append(":\n").append(OptionsResources.getString(1890));
         }
      } else {
         errStr.append(":\n").append(OptionsResources.getString(1506));
      }

      Dialog.alert(errStr.toString());
   }

   @Override
   protected final boolean onSave() {
      this._state = 3;
      this._simCardHandler.write(this._netList.getList());
      return false;
   }

   @Override
   protected final void onExposed() {
      this.refresh();
      super.onExposed();
   }

   public PrefNetworkListOptions() {
      super(2251799813685248L);
      this.setTitle(OptionsResources.getResourceBundle(), 1872);
      this._flag = 0;
      this._netList = new PrefNetworkList(null);
      this._netList.setIndex(-1);
      this._listField = new MoveableListField();
      this._state = 2;
      this._statusScreen = new PrefNetworkListOptions$StatusScreen();
      this._simCardHandler = NetworkOptionsUtils.getPreferredNetworks(this);
      this._listField.setCallback(this);
      this.add(this._listField);
   }

   @Override
   protected final boolean handleEndKey() {
      if (this.getCloseVerb().invoke(null) != null) {
         ApplicationManager.getApplicationManager().requestForegroundForConsole();
         System.exit(0);
      }

      return true;
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (super.keyChar(key, status, time)) {
         return true;
      } else if (key == '\n') {
         this.invokeNewOrAddAction();
         return true;
      } else if (key == 27) {
         this.getCloseVerb().invoke(null);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected final boolean invokeAction(int action) {
      boolean handled = super.invokeAction(action);
      if (!handled) {
         switch (action) {
            case 1:
               this.invokeNewOrAddAction();
               return true;
         }
      }

      return handled;
   }

   private final void invokeNewOrAddAction() {
      int selectedIndex = this._listField.getSelectedIndex();
      this._netList.setIndex(selectedIndex);
      if (selectedIndex != -1) {
         Verb viewVerb = new PrefNetworkListOptions$PrefNetworkViewVerb(selectedIndex, this._netList);
         viewVerb.invoke(null);
      } else {
         Verb addVerb = new PrefNetworkListOptions$PrefNetworkAddVerb(this._listField.getSize(), this._maxListSize, this._netList);
         addVerb.invoke(null);
      }
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      int selectedIndex = this._listField.getSelectedIndex();
      this._netList.setIndex(selectedIndex);
      if (selectedIndex != -1) {
         menu.add(new PrefNetworkListOptions$PrefNetworkAddVerb(selectedIndex, this._maxListSize, this._netList));
         menu.add(new PrefNetworkListOptions$PrefNetworkViewVerb(selectedIndex, this._netList));
         menu.add(new PrefNetworkListOptions$PrefNetworkDeleteVerb(selectedIndex, this._netList));
      } else if (this._state != 2) {
         menu.add(new PrefNetworkListOptions$PrefNetworkAddVerb(this._listField.getSize(), this._maxListSize, this._netList));
      }

      if (this._netList.isListChanged()) {
         menu.add(new PrefNetworkListOptions$PrefNetworkSaveVerb(this));
      }
   }

   @Override
   protected final void verbInvoked(Verb verb, Object context, Object result) {
      if (verb instanceof PrefNetworkListOptions$PrefNetworkDeleteVerb && result != null) {
         this.refresh();
      }
   }

   static final boolean isFeatureSupported() {
      if ((RadioInfo.getSupportedWAFs() & 1) != 0) {
         if (DeviceInfo.isSimulator()) {
            return true;
         }

         if (!SIMServiceTable.isPLMNEnabled()) {
            return false;
         }

         byte[] data = Branding.getData(20736);
         if (data != null && data[0] == 0) {
            return false;
         }

         String platformStr = DeviceInfo.getPlatformVersion();
         int ver = 0;
         if (platformStr != null && platformStr.length() > 3 && Character.isDigit(platformStr.charAt(0)) && Character.isDigit(platformStr.charAt(0))) {
            ver = Integer.parseInt(((StringBuffer)(new Object())).append(platformStr.charAt(0)).append(platformStr.charAt(2)).toString());
         }

         if (ver == 0 || ver >= 18) {
            return true;
         }
      }

      return false;
   }
}
