package net.rim.device.cldc.impl.hrt.editor;

import net.rim.device.api.hrt.DAC;
import net.rim.device.api.hrt.DomainNameDAC;
import net.rim.device.api.hrt.GprsHRI;
import net.rim.device.api.hrt.HostRoutingInfo;
import net.rim.device.api.hrt.HostRoutingTable;
import net.rim.device.api.hrt.IPv4UdpDAC;
import net.rim.device.api.hrt.IntDAC;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.GPRSQOSInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.internal.i18n.CommonResource;

final class HRTAppInfoEditUI extends MainScreen implements ListFieldCallback {
   private HostRoutingInfo _hri;
   private int _netType;
   private boolean _isNew;
   private int _retCode;
   private int _viewMode;
   private boolean _dacsDirty;
   String[] _dacStrings;
   private LabelField _screenTitle;
   private AutoTextEditField _nameField;
   private ListField _dacList;
   private EditField _loadSharingField;
   private EditField _npcField;
   private EditField _artField;
   private EditField _pteField;
   private EditField _apnField;
   private EditField _apnUsernameField;
   private EditField _apnPasswordField;
   private ObjectChoiceField _qosPrecedenceField;
   private ObjectChoiceField _qosReliabilityField;
   private ObjectChoiceField _qosDelayField;
   private ObjectChoiceField _qosPeakTPField;
   private ObjectChoiceField _qosMeanTPField;
   public static final int HRI_MOBITEX = 0;
   public static final int HRI_GPRS = 2;
   public static final int HRI_CDMA = 3;
   public static final int HRI_IDEN = 4;
   public static final int HRI_WIFI = 5;
   public static final int RET_SAVE = 0;
   public static final int RET_CLOSE = 1;
   private static final int MENU_ADD_DAC = 0;
   private static final int MENU_EDIT_DAC = 1;
   private static final int MENU_REMOVE_DAC = 2;
   private static final int DV_OKAY = 0;
   private static final int DV_MISSING_DATA = 1;
   private static final int DV_BAD_NPC = 2;
   private static final int DV_BAD_ART = 3;
   private static final int DV_BAD_PTE = 4;
   private static final int[] MENU_STRING_IDS = new int[]{10, 11, 12, -805044219, 1718183726, 10, -805044223, 48, -804651007, 51, -805044141, 944130375};
   private static final int[] MENU_ORDER = new int[]{
      0, 1, 2, -805044213, 775162112, 774909491, 3420721, -805044199, 1699878656, 1918985587, 1226860643, 1867325550
   };

   HRTAppInfoEditUI(HostRoutingTable hrt, HostRoutingInfo theHri, boolean iNew) {
      this._hri = theHri;
      this._isNew = iNew;
      this._retCode = 1;
      this._viewMode = -1;
      this._dacStrings = new Object[0];
      if (theHri instanceof Object) {
         this._netType = 2;
      } else if (theHri instanceof Object) {
         this._netType = 3;
      } else if (theHri instanceof Object) {
         this._netType = 4;
      } else if (theHri instanceof Object) {
         this._netType = 5;
      }

      this._screenTitle = (LabelField)(new Object());
      this.setTitle(this._screenTitle);
      this.initDisplay();
      this.load();
   }

   public final int go(int viewMode) {
      this._retCode = 1;
      this.setViewMode(viewMode);
      this._dacsDirty = false;
      UiApplication.getUiApplication().pushModalScreen(this);
      return this._retCode;
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      if (this.getFieldWithFocus() == this._dacList && this._viewMode == 0) {
         menu.add(new HRTAppInfoEditUI$HRTMenuItem(this, 0));
         if (this._dacStrings.length != 0) {
            menu.add(new HRTAppInfoEditUI$HRTMenuItem(this, 1));
            menu.add(new HRTAppInfoEditUI$HRTMenuItem(this, 2));
         }

         menu.addSeparator();
      }

      if (this.isDirty()) {
         menu.add(new HRTAppInfoEditUI$HRTMenuItem(this, CommonResource.getBundle(), 18));
         menu.addSeparator();
      }
   }

   @Override
   protected final boolean openDevelopmentBackdoor(int backdoorCode) {
      if (backdoorCode == 1396852047 && this._viewMode == 1) {
         this.setViewMode(0);
         return true;
      } else {
         return super.openDevelopmentBackdoor(backdoorCode);
      }
   }

   @Override
   public final boolean isDataValid() {
      int dv = this.dataValid();
      if (dv == 1) {
         Status.show(HRTAppResources.getString(21));
         return false;
      } else if (dv == 2) {
         Status.show(HRTAppResources.getString(22));
         return false;
      } else if (dv == 3) {
         Status.show(HRTAppResources.getString(206));
         return false;
      } else if (dv == 4) {
         Status.show(HRTAppResources.getString(209));
         return false;
      } else {
         return true;
      }
   }

   @Override
   public final boolean isDirty() {
      return this._dacsDirty || super.isDirty();
   }

   private final void load() {
      DAC dac = this._hri.getDac();
      this._nameField.setText(this._hri.getName());
      this._npcField.setText(NumberUtilities.toString(this._hri.getNpc(), 16));
      this._artField.setText(NumberUtilities.toString(this._hri.getArt(), 16));
      this._pteField.setText(Integer.toString(this._hri.getPte()));
      this._loadSharingField.setText(Integer.toString(dac.getLoadSharingCode()));
      if (this._netType == 2) {
         GprsHRI ghri = (GprsHRI)this._hri;
         if (ghri.getApn() != null) {
            this._apnField.setText(ghri.getApn());
         }

         this._apnUsernameField.setText(ghri.getApnUsername() != null ? ghri.getApnUsername() : "<null>");
         this._apnPasswordField.setText(ghri.getApnPassword() != null ? ghri.getApnPassword() : "<null>");
         GPRSQOSInfo qos = (GPRSQOSInfo)ghri.getQos();
         this._qosPrecedenceField.setSelectedIndex(qos.getPrecedenceClass());
         this._qosReliabilityField.setSelectedIndex(qos.getReliabilityClass());
         this._qosDelayField.setSelectedIndex(qos.getDelayClass());
         this._qosPeakTPField.setSelectedIndex(qos.getPeakThroughputClass());
         int index;
         if ((index = qos.getMeanThroughputClass()) == 31) {
            index = 19;
         }

         this._qosMeanTPField.setSelectedIndex(index);
      }

      this.loadDacStrings(dac);
      this._dacList.setSize(this._dacStrings.length);
   }

   private final void loadDacStrings(DAC dac) {
      if (dac.getNumCodes() > 0) {
         if (!(dac instanceof Object)) {
            if (!(dac instanceof Object)) {
               if (dac instanceof Object) {
                  DomainNameDAC dnDac = (DomainNameDAC)dac;
                  String[] addrs = dnDac.getAddresses();
                  if (addrs != null) {
                     for (int i = 0; i < addrs.length; i++) {
                        Arrays.add(this._dacStrings, addrs[i]);
                     }
                  }
               }
            } else {
               IPv4UdpDAC ipDac = (IPv4UdpDAC)dac;
               long[] addrs = ipDac.getAddresses();

               for (int i = 0; i < addrs.length; i++) {
                  Arrays.add(this._dacStrings, IPv4UdpDAC.addr2String(addrs[i]));
               }
            }
         } else {
            IntDAC iDac = (IntDAC)dac;
            int[] addrs = iDac.getAddresses();

            for (int i = 0; i < addrs.length; i++) {
               Arrays.add(this._dacStrings, iDac.addr2String(addrs[i]));
            }
         }
      }
   }

   @Override
   public final void save() {
      DAC dac = this._hri.getDac();
      this._hri.setName(this._nameField.getText());
      this._hri.setNpc(Long.parseLong(this._npcField.getText(), 16));
      this._hri.setArt(Integer.parseInt(this._artField.getText(), 16));
      this._hri.setPte(Integer.parseInt(this._pteField.getText()));
      dac.setLoadSharingCode(this.parseInt(this._loadSharingField.getText()));
      switch (this._netType) {
         case 2:
            GprsHRI ghri = (GprsHRI)this._hri;
            ghri.setApn(this._apnField.getText());
            ghri.setApnUsername(!this._apnUsernameField.getText().equals("<null>") ? this._apnUsernameField.getText() : null);
            ghri.setApnPassword(!this._apnPasswordField.getText().equals("<null>") ? this._apnPasswordField.getText() : null);
            int index = this._qosMeanTPField.getSelectedIndex();
            if (index == 19) {
               index = 31;
            }

            GPRSQOSInfo qos = (GPRSQOSInfo)(new Object(
               this._qosPrecedenceField.getSelectedIndex(),
               this._qosReliabilityField.getSelectedIndex(),
               this._qosDelayField.getSelectedIndex(),
               this._qosPeakTPField.getSelectedIndex(),
               index
            ));
            ghri.setQos(qos);
         default:
            this.saveDacStrings(dac);
            this._retCode = 0;
      }
   }

   private final void saveDacStrings(DAC dac) {
      if (!(dac instanceof Object)) {
         if (!(dac instanceof Object)) {
            if (dac instanceof Object) {
               DomainNameDAC dnDac = (DomainNameDAC)dac;
               StringBuffer addrBuf = (StringBuffer)(new Object());

               for (int i = 0; i < this._dacStrings.length; i++) {
                  addrBuf.append(this._dacStrings[i]);
                  if (i != this._dacStrings.length - 1) {
                     addrBuf.append(',');
                  }
               }

               dnDac.setAddresses(addrBuf.toString());
            }
         } else {
            IPv4UdpDAC ipDac = (IPv4UdpDAC)dac;
            long[] addrs = null;
            if (this._dacStrings.length != 0) {
               addrs = new long[this._dacStrings.length];

               for (int i = addrs.length - 1; i >= 0; i--) {
                  addrs[i] = IPv4UdpDAC.string2Addr(this._dacStrings[i]);
               }
            }

            ipDac.setAddresses(addrs);
         }
      } else {
         IntDAC iDac = (IntDAC)dac;
         int[] addrs = null;
         if (this._dacStrings.length != 0) {
            addrs = new int[this._dacStrings.length];

            for (int i = addrs.length - 1; i >= 0; i--) {
               addrs[i] = iDac.string2Addr(this._dacStrings[i]);
            }
         }

         iDac.setAddresses(addrs);
      }
   }

   private final int parseInt(String str) {
      if (str.startsWith("0x")) {
         return Integer.parseInt(str.substring(2), 16);
      } else {
         return str.startsWith("0b") ? Integer.parseInt(str.substring(2), 2) : Integer.parseInt(str, 10);
      }
   }

   private final int dataValid() {
      long npc = -1;
      if (this._nameField.getTextLength() != 0
         && this._npcField.getTextLength() != 0
         && this._artField.getTextLength() != 0
         && this._pteField.getTextLength() != 0
         && (!(this._hri instanceof Object) || this._dacStrings.length != 0)
         && (this._hri instanceof Object || this._dacStrings.length != 0)) {
         try {
            npc = Long.parseLong(this._npcField.getText(), 16);
         } finally {
            ;
         }

         if (npc == 0) {
            return 2;
         }

         int art;
         try {
            art = Integer.parseInt(this._artField.getText(), 16);
         } finally {
            ;
         }

         int pte;
         try {
            pte = Integer.parseInt(this._pteField.getText());
         } finally {
            ;
         }

         long baseNpc = npc & 252;
         switch (this._netType) {
            case 1:
               break;
            case 2:
            default:
               if (this._apnField.getTextLength() == 0) {
                  return 1;
               }

               if (baseNpc != 48) {
                  return 2;
               }

               if ((art & 7) == 0) {
                  return 3;
               }

               if (pte != 1) {
                  return 4;
               }
               break;
            case 3:
               if (baseNpc != 64) {
                  return 2;
               }

               if ((art & 16) == 0) {
                  return 3;
               }

               if (pte != 1) {
                  return 4;
               }
               break;
            case 4:
               if (baseNpc != 80) {
                  return 2;
               }

               if ((art & 32) == 0) {
                  return 3;
               }

               if (pte != 1) {
                  return 4;
               }
               break;
            case 5:
               if (baseNpc != 96) {
                  return 2;
               }

               if ((art & 8) == 0) {
                  return 3;
               }

               if (pte != 2 && pte != 3 && pte != 4 && pte != 5 && pte != 6) {
                  return 4;
               }
         }

         return 0;
      } else {
         return 1;
      }
   }

   private final void setViewMode(int viewMode) {
      if (viewMode != this._viewMode) {
         this._viewMode = viewMode;
         boolean mode = false;
         if (this._viewMode == 0) {
            mode = true;
            if (this._netType == 2) {
               this.insert(this._apnUsernameField, 3);
               this.insert(this._apnPasswordField, 4);
            }
         }

         for (int i = this.getFieldCount() - 1; i >= 0; i--) {
            Field f = this.getField(i);
            f.setEditable(mode);
         }

         int titleId = this._isNew ? 2 : (this._viewMode == 0 ? 3 : 205);
         this._screenTitle.setText(this.pickString(titleId));
      }
   }

   private final EditField addEditField(int titleId, String contents) {
      EditField ef = (EditField)(new Object(HRTAppResources.getString(titleId), contents));
      this.add(ef);
      return ef;
   }

   private final ObjectChoiceField addObjectChoiceField(int titleId, int stringsId) {
      ResourceBundle rb = HRTAppResources.getResourceBundle();
      ObjectChoiceField ocf = (ObjectChoiceField)(new Object(rb.getString(titleId), CommonResource.getStringArray(stringsId)));
      this.add(ocf);
      return ocf;
   }

   private final String pickString(int resId) {
      return HRTAppResources.getResourceBundle().getStringArray(resId)[this._netType - 2];
   }

   private final void initDisplay() {
      this._nameField = (AutoTextEditField)(new Object(HRTAppResources.getString(102), null));
      this.add(this._nameField);
      this._npcField = this.addEditField(101, null);
      this._artField = this.addEditField(207, null);
      if (this._netType == 2) {
         this._apnField = this.addEditField(110, null);
         this._apnUsernameField = (EditField)(new Object("Username: ", null));
         this._apnPasswordField = (EditField)(new Object("Password: ", null));
      }

      this.add((Field)(new Object(this.pickString(40))));
      this._dacList = (ListField)(new Object(0));
      this._dacList.setCallback(this);
      this._dacList.setEmptyString(this.pickString(31), 4);
      this.add(this._dacList);
      this.add((Field)(new Object()));
      this._loadSharingField = (EditField)(new Object(HRTAppResources.getString(103), null, 10, 16777216));
      this.add(this._loadSharingField);
      this._pteField = this.addEditField(208, null);
      if (this._netType == 2) {
         this.add((Field)(new Object(CommonResource.getString(306))));
         this._qosPrecedenceField = this.addObjectChoiceField(111, 301);
         this._qosReliabilityField = this.addObjectChoiceField(112, 302);
         this._qosDelayField = this.addObjectChoiceField(113, 303);
         this._qosPeakTPField = this.addObjectChoiceField(114, 304);
         this._qosMeanTPField = this.addObjectChoiceField(115, 305);
      }
   }

   @Override
   public final Object get(ListField listField, int index) {
      return this._dacStrings[index];
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      graphics.drawText(this._dacStrings[index], 0, y, 4, width);
   }

   @Override
   public final int getPreferredWidth(ListField field) {
      return this.getWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }

   static final boolean access$700(HRTAppInfoEditUI x0) {
      return x0.onSave();
   }
}
