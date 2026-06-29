package net.rim.device.cldc.impl.hrt.editor;

import net.rim.device.api.hrt.DAC;
import net.rim.device.api.hrt.HRUtils;
import net.rim.device.api.hrt.HostRoutingInfo;
import net.rim.device.api.hrt.HostRoutingTable;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.cldc.impl.gcmp.Gcmp;
import net.rim.device.internal.system.RadioInternal;

public final class HRTAppContents extends MainScreen implements GlobalEventListener, ListFieldCallback {
   private int GPRS_REC_TYPE_INDEX = 0;
   private int CDMA_REC_TYPE_INDEX = 1;
   private int IDEN_REC_TYPE_INDEX = 2;
   private int WIFI_REC_TYPE_INDEX = 3;
   private HostRoutingTable _hrt;
   private ListField _list;
   private int _defaultIndex;
   private int _viewMode;
   private StringBuffer _strBuf = (StringBuffer)(new Object(32));
   private TextField _registrationAddressField;
   private TextField _expiryField;
   private ResourceBundleFamily _rb;

   public HRTAppContents(HostRoutingTable hrt, int viewMode) {
      this._hrt = hrt;
      this._rb = HRTAppResources.getResourceBundle();
      this._list = (ListField)(new Object(0));
      this._defaultIndex = hrt.getActiveIndex();
      this._list.setSize(hrt.getNumHris());
      this._list.setCallback(this);
      this._list.setEmptyString(this._rb, 30, 4);
      this._viewMode = viewMode;
      Application.getApplication().addGlobalEventListener(this);
      this.setTitle(this._rb, 1);
      this.add(this._list);
   }

   @Override
   public final void close() {
      Application.getApplication().removeGlobalEventListener(this);
      super.close();
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      int ord = 0;
      if (this.getFieldWithFocus() == this._list) {
         if (this._hrt.getNumHris() != 0) {
            menu.add(new HRTAppContents$HRTMenuItem(this, 14, true, ord++));
            menu.addSeparator();
         }

         if (this._viewMode == 0) {
            menu.add(new HRTAppContents$HRTMenuItem(this, 13, true, ord++));
            if (this._hrt.getNumHris() != 0) {
               menu.add(new HRTAppContents$HRTMenuItem(this, 16, true, ord++));
               menu.add(new HRTAppContents$HRTMenuItem(this, 17, true, ord++));
            }

            menu.addSeparator();
         }
      }

      if (this._hrt == HRUtils.getDefaultHRT()) {
         menu.add(new HRTAppContents$HRTMenuItem(this, 117, false, ord++));
      }

      if (this._registrationAddressField != null) {
         menu.add(new HRTAppContents$HRTMenuItem(this, 116, false, ord++));
      }

      super.makeMenu(menu, instance);
   }

   @Override
   protected final boolean invokeAction(int action) {
      boolean handled = super.invokeAction(action);
      if (!handled) {
         switch (action) {
            case 1:
               if (this._list.getSelectedIndex() != -1) {
                  this.viewItem(false);
               }

               return true;
         }
      }

      return handled;
   }

   @Override
   protected final boolean keyCharUnhandled(char key, int status, int time) {
      if (this._list.getSelectedIndex() != -1) {
         if (key == '\n') {
            this.viewItem(false);
            return true;
         }

         if (key == 127) {
            this.deleteItem();
            return true;
         }
      }

      return super.keyCharUnhandled(key, status, time);
   }

   @Override
   protected final boolean openProductionBackdoor(int backDoor) {
      switch (backDoor) {
         case 1381258315:
            return super.openProductionBackdoor(backDoor);
         case 1381258316:
         default:
            if (this._expiryField == null) {
               this.insert((Field)(new Object()), 0);
               this._expiryField = (TextField)(new Object("Expiry Date: ", this.getExpiryFieldText(), 100, 9007199254740992L));
               Font font = this.getFont();
               this._expiryField.setFont(font.derive(0));
               this.insert(this._expiryField, 0);
               this._expiryField.setFocus();
            }

            return true;
      }
   }

   @Override
   protected final boolean openDevelopmentBackdoor(int backdoorCode) {
      String str = null;
      switch (backdoorCode) {
         case 1163087950:
            if (HRUtils.getThunks().toggleSendEFSPN()) {
               str = "Sending EF_SPN field enabled";
            } else {
               str = "Sending EF_SPN field disabled";
            }
            break;
         case 1195593040:
            Gcmp.getInstance().pingNow(-1);
            str = "GCMP Ping sent";
            break;
         case 1196250194:
            if (this._registrationAddressField == null) {
               this.insert((Field)(new Object()), 0);
               this._registrationAddressField = (TextField)(new Object("Registration Address: ", this.getRegistrationAddressFieldText(), 512, 9007207844675584L));
               Font font = this.getFont();
               this._registrationAddressField.setFont(font.derive(0));
               this.insert(this._registrationAddressField, 0);
               this._registrationAddressField.setFocus();
            }
            break;
         case 1380927046:
            RIMGlobalMessagePoster.postGlobalEvent(-1112359896077348406L, 0, 0, "", null);
            str = "All Relay Wi-Fi addresses removed";
            break;
         case 1381191247:
            HRUtils.getThunks().setRegistrationServerPresent(false);
            str = "Registration Server is _NOT_ Available";
            break;
         case 1381191502:
            HRUtils.getThunks().setRegistrationServerPresent(true);
            str = "Registration Server is Available";
            break;
         case 1381192524:
         case 1381253968:
            boolean socket = backdoorCode == 1381253968;
            HRTAppDacDialog d = new HRTAppDacDialog((DAC)(new Object(0, 0)), 2, null);
            if (d.go() == 1) {
               String address = d.getRetObject();
               RIMGlobalMessagePoster.postGlobalEvent(-1112359896077348406L, 1, socket ? 1 : 2, address, null);
               str = ((StringBuffer)(new Object("Relay Wi-Fi address "))).append(address).append(" added for ").append(socket ? "socket" : "ssl").toString();
            }
            break;
         case 1381257030:
            HRUtils.getThunks().enableRequestThread(false);
            str = "HRT Request Thread Disabled!";
            break;
         case 1381257038:
            HRUtils.getThunks().enableRequestThread(true);
            str = "HRT Request Thread Enabled!";
            break;
         case 1396852047:
            if (this._viewMode == 1) {
               this._viewMode = 0;
               str = "View mode set to Read-Write";
            } else {
               str = "View mode was already set to Read-Write";
            }
            break;
         case 1447448398:
            HRUtils.getThunks().useRegistrationVersion(4);
            str = "V4 registration is enabled";
            break;
         case 1448365902:
            HRUtils.getThunks().useRegistrationVersion(3);
            str = "V3 registration is enabled";
            break;
         case 1448496974:
            HRUtils.getThunks().useRegistrationVersion(5);
            str = "V5 registration is enabled";
      }

      if (str != null) {
         Dialog.inform(str);
      }

      return super.openDevelopmentBackdoor(backdoorCode);
   }

   @Override
   protected final void onExposed() {
      if (this._registrationAddressField != null) {
         this._registrationAddressField.setText(this.getRegistrationAddressFieldText());
      }
   }

   private final String getRegistrationAddressFieldText() {
      String result = null;
      HostRoutingInfo hri = HRUtils.getRegistrationHRT().getActiveHri();
      if (hri != null) {
         result = hri.getAddressBase().getAddress();
      }

      if (result == null || result.length() == 0) {
         result = "No registration HRT found";
      }

      return result;
   }

   private final String getExpiryFieldText() {
      return this._hrt.getTtl() > 0 ? ((DateFormat)(new Object("MMM/dd/yyyy HH:mm:ss"))).formatLocal(this._hrt.getTtlExpiry()) : "Never expire";
   }

   private final void add() {
      int primaryWAF = RadioInternal.getPrimaryWAF();
      int defaultTypeIndex;
      if (primaryWAF == 1) {
         defaultTypeIndex = this.GPRS_REC_TYPE_INDEX;
      } else if (primaryWAF == 2) {
         defaultTypeIndex = this.CDMA_REC_TYPE_INDEX;
      } else if (primaryWAF == 8) {
         defaultTypeIndex = this.IDEN_REC_TYPE_INDEX;
      } else if (primaryWAF == 4) {
         defaultTypeIndex = this.WIFI_REC_TYPE_INDEX;
      } else {
         defaultTypeIndex = this.GPRS_REC_TYPE_INDEX;
      }

      int netType = Dialog.ask(this._rb.getString(23), this._rb.getStringArray(24), defaultTypeIndex) + 1;
      if (netType > 0) {
         netType += 2;
         HostRoutingInfo hri = HRUtils.newHriByNetType(netType);
         HRTAppInfoEditUI editUI = new HRTAppInfoEditUI(this._hrt, hri, true);
         if (editUI.go(0) == 0) {
            this._hrt.addHri(hri);
            this._defaultIndex = this._hrt.getActiveIndex();
            this._list.setSize(this._hrt.getNumHris());
         }
      }
   }

   private final void deleteItem() {
      int index = this._list.getSelectedIndex();
      if (Dialog.ask(2, HRTAppResources.getString(20)) == 3) {
         this._hrt.removeHri(index);
         this._defaultIndex = this._hrt.getActiveIndex();
         this._list.setSize(this._hrt.getNumHris());
      }
   }

   private final void viewItem(boolean edit) {
      HostRoutingInfo hri = this._hrt.getHris()[this._list.getSelectedIndex()];
      HostRoutingInfo oldWiFiHRI = null;
      if ((hri.getArt() & 8) != 0) {
         oldWiFiHRI = hri.clone();
      }

      HRTAppInfoEditUI editUI = new HRTAppInfoEditUI(this._hrt, hri, false);
      int viewMode;
      if (!edit) {
         viewMode = this._viewMode == 2 ? this._viewMode : 1;
      } else {
         viewMode = 0;
      }

      if (editUI.go(viewMode) == 0) {
         this._hrt.commit();
         if (oldWiFiHRI != null) {
            RIMGlobalMessagePoster.postGlobalEvent(8951540267497860657L, 0, 0, oldWiFiHRI, hri);
         }

         this._defaultIndex = this._hrt.getActiveIndex();
         this._list.invalidate();
      }
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      if (index < this._hrt.getNumHris()) {
         HostRoutingInfo hri = this._hrt.getHris()[index];
         Font font = this.getFont();
         font = font.derive(index == this._defaultIndex ? 1 : 0);
         graphics.setFont(font);
         this._strBuf.setLength(0);
         this._strBuf.append(hri.getName());
         this._strBuf.append(' ');
         this._strBuf.append('[');
         NumberUtilities.appendNumber(this._strBuf, hri.getNpc(), 16);
         this._strBuf.append(']');
         graphics.drawText(this._strBuf, 0, this._strBuf.length(), 0, y, 64, width);
      }
   }

   @Override
   public final int getPreferredWidth(ListField field) {
      return 16;
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }

   @Override
   public final Object get(ListField listField, int index) {
      HostRoutingInfo hri = this._hrt.getHris()[index];
      return hri.getName();
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -3864212166794284297L || guid == -2686242019764477137L || guid == -6531073315810526672L || guid == 2200641410611652722L) {
         this._defaultIndex = this._hrt.getActiveIndex();
         this._list.setSize(this._hrt.getNumHris());
         this._list.invalidate();
         if (this._expiryField != null) {
            this._expiryField.setText(this.getExpiryFieldText());
         }
      }
   }
}
