package net.rim.device.apps.internal.options.items;

import java.util.Vector;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.BackdoorKeyProcessor;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.system.SIMCardEFListener;
import net.rim.device.api.system.SIMCardSecurityListener;
import net.rim.device.api.system.SIMCardStatusListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.MainScreenOptionsListItem;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.SIMCodeDialog;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.SIMCardEfHandler;
import net.rim.device.internal.system.SIMCardEfTask;
import net.rim.device.internal.system.SIMPhoneNumberReader$PhoneNumberList;
import net.rim.device.internal.ui.component.SimplePasswordDialog;

public final class SIMCardOptionsItem
   extends MainScreenOptionsListItem
   implements ListFieldCallback,
   SIMCardStatusListener,
   SIMCardSecurityListener,
   SIMCardEFListener {
   private ListField _simCardItemsField;
   private LabelField _titleField;
   private boolean _displayMEPInfo;
   private int _backdoorChars;
   private int _backdoorKeys;
   private int _mepCategory;
   private int _phoneNumberStartIndex = 0;
   private Vector _lineMap;
   private boolean _updating;
   private static final ResourceBundle _srb = ResourceBundle.getBundle(-1488627819050031640L, "net.rim.device.apps.internal.resource.Security");
   private static Vector _simCardItems;
   private static final int INDENT_SIZE = 10;

   public final boolean processMEPBackdoorCode(int backdoorChars, int backdoorKeys) {
      switch (backdoorChars) {
         case 1296388163:
            switch (backdoorKeys & -256 | backdoorChars & 0xFF) {
               case 1296388144:
                  return false;
               case 1296388145:
               default:
                  this.deactivateMEP(0);
                  return true;
               case 1296388146:
                  this.deactivateMEP(1);
                  return true;
               case 1296388147:
                  this.deactivateMEP(2);
                  return true;
               case 1296388148:
                  this.deactivateMEP(3);
                  return true;
               case 1296388149:
                  this.deactivateMEP(4);
                  return true;
            }
         case 1296388164:
         default:
            this._displayMEPInfo = true;
            this.updateScreenContents();
            return true;
      }
   }

   public final boolean processGSMCertificationBackdoorCode(int backdoorCode) {
      switch (backdoorCode) {
         case 707802154:
            this.changePIN(null, 1);
            return true;
         case 707802410:
            this.forceNewPIN(1);
            return true;
         default:
            return false;
      }
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
   public final void cardInserted() {
   }

   @Override
   public final void cardReady() {
      this.updateScreenContents();
   }

   @Override
   public final void cardUpdated() {
      this.updateScreenContents();
   }

   @Override
   public final void cardInvalid(int reason, int subReason) {
      this.updateScreenContents();
   }

   @Override
   public final void cardFault(int reason) {
   }

   @Override
   public final void smsEFFull() {
   }

   @Override
   public final void responseDeleteSMS(int status, int packetId) {
   }

   @Override
   public final void responseMarkSMSAsRead(int status, int packetId) {
   }

   @Override
   public final void requestSendPIN(int retriesRemaining) {
   }

   @Override
   public final void pinValid() {
   }

   @Override
   public final void requestSendPUK(int retriesRemaining) {
   }

   @Override
   public final void responseEnablePIN(int code, int id, int remainingPINRetries) {
      this.processSIMResponse(code, OptionsResources.getString(1516), OptionsResources.getString(1514));
   }

   @Override
   public final void responseDisablePIN(int code, int id, int remainingPINRetries) {
      this.processSIMResponse(code, OptionsResources.getString(1517), OptionsResources.getString(1515));
   }

   @Override
   public final void responseChangePIN(int code, int id, int remainingPINRetries) {
      this.updateScreenContents();
   }

   @Override
   public final void responseValidatePIN(int code, int id, int remainingPINRetries) {
   }

   @Override
   public final void responseDeactivateMEP(boolean success) {
      if (success) {
         Status.show(_srb.getString(216));
      } else {
         int millisToWait = 2000;

         label25:
         try {
            millisToWait = 10000 * SIMCard.getMEPDeactivateAttempts(this._mepCategory);
         } finally {
            break label25;
         }

         Status.show(_srb.getString(218), Bitmap.getPredefinedBitmap(0), millisToWait, 33554432, false, true, 50);
      }

      this.updateScreenContents();
   }

   @Override
   public final void wtlsKeyWriteComplete(int status) {
   }

   @Override
   public final void responseEFInfo(int code, int id, int fileStatus, int structure, int fileSize, int recordLength, int numRecords) {
   }

   @Override
   public final void responseEFRead(int code, int id, int structure, int length, int recordNumber) {
   }

   @Override
   public final void responseEFWrite(int code, int id, int structure, int recordNumber) {
      this.updateScreenContents();
   }

   @Override
   public final Object get(ListField listField, int index) {
      return _simCardItems.elementAt(index);
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      SIMCardOptionsItem$SIMCardItem item = (SIMCardOptionsItem$SIMCardItem)_simCardItems.elementAt(index);
      int pos = graphics.drawText(item._displayName, item._indent, y, 6, width);
      graphics.drawText(item._value == null ? "" : item._value, pos + item._indent, y, 69, width - (pos + item._indent));
   }

   private final boolean isPIN2OnSIM() {
      return RadioInfo.areWAFsSupported(1);
   }

   @Override
   protected final void addScreenVerbs(VerbToMenu verbToMenu, int instance) {
      super.addScreenVerbs(verbToMenu, instance);

      try {
         if (SIMCard.isValid()) {
            boolean SIMNotBlocked = true;
            if (SIMCard.isPUKRequired(1)) {
               SIMNotBlocked = false;
               verbToMenu.addVerb(new SIMCardOptionsItem$UnblockSIMVerb(this, 1));
            }

            if (SIMCard.isPUKRequired(2)) {
               verbToMenu.addVerb(new SIMCardOptionsItem$UnblockSIMVerb(this, 2));
            }

            if (SIMNotBlocked) {
               if (SIMCard.isSecuritySupported()) {
                  boolean pin2ChangeSupported = InternalServices.getHardwareID() != 67112452;
                  if (SIMCard.isPINEnabled()) {
                     verbToMenu.addVerb(new SIMCardOptionsItem$ChangePINVerb(this, 1));
                     if (pin2ChangeSupported && this.isPIN2OnSIM() && !SIMCard.isPUKRequired(2)) {
                        verbToMenu.addVerb(new SIMCardOptionsItem$ChangePINVerb(this, 2));
                     }

                     SIMCardOptionsItem$DisableSIMSecurityVerb disableSecurityVerb = new SIMCardOptionsItem$DisableSIMSecurityVerb(this);
                     verbToMenu.addVerb(disableSecurityVerb);
                     verbToMenu.setDefaultVerb(disableSecurityVerb);
                  } else {
                     if (pin2ChangeSupported && this.isPIN2OnSIM() && !SIMCard.isPUKRequired(2)) {
                        verbToMenu.addVerb(new SIMCardOptionsItem$ChangePINVerb(this, 2));
                     }

                     SIMCardOptionsItem$EnableSIMSecurityVerb enableSecurityVerb = new SIMCardOptionsItem$EnableSIMSecurityVerb(this);
                     verbToMenu.addVerb(enableSecurityVerb);
                     verbToMenu.setDefaultVerb(enableSecurityVerb);
                  }
               }

               int line = 1;
               int element = this._simCardItemsField.getSelectedIndex() - this._phoneNumberStartIndex - 1;
               if (element >= 0 && element < this._lineMap.size()) {
                  Integer value = (Integer)this._lineMap.elementAt(element);
                  line = value;
               }

               if (line == 1 || line == 2) {
                  VerbFactory[] factories = VerbFactoryRepository.getVerbFactories(-4755272785484829483L);
                  if (factories != null) {
                     ContextObject context = (ContextObject)(new Object());
                     context.putIntegerData(line);

                     for (int i = factories.length - 1; i >= 0; i--) {
                        verbToMenu.addVerbs(factories[i].getVerbs(context));
                     }
                  }
               }
            }
         }
      } finally {
         return;
      }
   }

   private final void createSIMCardItems() {
      _simCardItems = (Vector)(new Object());
      Font currentFont = null;
      if (this._simCardItemsField != null) {
         currentFont = this._simCardItemsField.getFont();
      } else {
         currentFont = Font.getDefault();
      }

      label71:
      try {
         String name = OptionsResources.getString(1501);
         String value = SIMCard.iccidToString(SIMCard.getICCID());
         StringBuffer sb = (StringBuffer)(new Object(name));
         sb.append(' ');
         sb.append(value);
         if (currentFont.getBounds(sb.toString()) < Display.getWidth()) {
            this.addSIMCardItem(name, value, 0);
         } else {
            this.addSIMCardItem(name, null, 0);
            this.addSIMCardItem(null, value, 0);
         }
      } finally {
         break label71;
      }

      if (this._displayMEPInfo) {
         this.addSIMCardItem(OptionsResources.getString(1521), null, 0);
         this.addSIMCardItem(CommonResources.getString(7000), this.getMEPStateString(0), 10);
         this.addSIMCardItem(CommonResources.getString(7001), this.getMEPStateString(1), 10);
         this.addSIMCardItem(CommonResources.getString(7002), this.getMEPStateString(2), 10);
         this.addSIMCardItem(CommonResources.getString(7003), this.getMEPStateString(3), 10);
         this.addSIMCardItem(CommonResources.getString(7004), this.getMEPStateString(4), 10);
      }

      if (this.isValidSIMInserted()) {
         Vector phoneNumbers = (Vector)(new Object());
         this._lineMap = (Vector)(new Object());
         if (RadioInfo.areWAFsSupported(2)) {
            this.getWorldPhonePhoneNumbers(phoneNumbers);
         } else {
            this.getGSMDevicePhoneNumbers(phoneNumbers, currentFont);
         }

         int numPhoneNumbers = phoneNumbers.size();
         this._phoneNumberStartIndex = _simCardItems.size();
         if (numPhoneNumbers == 1) {
            this.addSIMCardItem(OptionsResources.getString(1504), null, 0);
            this.addSIMCardItem((String)phoneNumbers.elementAt(0), null, 10);
         } else {
            if (numPhoneNumbers > 1) {
               this.addSIMCardItem(OptionsResources.getString(1505), null, 0);

               for (int i = 0; i < numPhoneNumbers; i++) {
                  this.addSIMCardItem((String)phoneNumbers.elementAt(i), null, 10);
               }
            }
         }
      }
   }

   private final void getWorldPhonePhoneNumbers(Vector phoneNumbers) {
      Vector phoneNumberList = phoneNumbers;
      SIMPhoneNumberReader$PhoneNumberList phonelist = new SIMCardOptionsItem$1(this, phoneNumberList);
      ((SIMCardEfHandler)(new Object())).startTask((SIMCardEfTask)(new Object(phonelist, 24, -1)), true);
      this._lineMap.addElement(new Object(1));
   }

   private final void getGSMDevicePhoneNumbers(Vector phoneNumbers, Font currentFont) {
      try {
         Phone phone = Phone.getInstance();
         int[] lineIds = phone.getAlternateLines();

         for (int i = 0; i < lineIds.length; i++) {
            if (lineIds[i] <= 2) {
               String number = phone.getAlternateLineNumber(lineIds[i]);
               if (number == null || number.length() == 0) {
                  number = PhoneResources.getString(117);
               }

               String label = phone.getAlternateLineLabel(lineIds[i]);
               if (label != null && label.length() > 0) {
                  label = ((StringBuffer)(new Object(" ("))).append(label).append(")").toString();
                  String numberAndLabel = ((StringBuffer)(new Object())).append(number).append(label).toString();
                  if (currentFont.getBounds(numberAndLabel.toString()) + 10 < Display.getWidth()) {
                     phoneNumbers.addElement(numberAndLabel);
                     this._lineMap.addElement(new Object(i + 1));
                  } else {
                     phoneNumbers.addElement(number);
                     this._lineMap.addElement(new Object(i + 1));
                     phoneNumbers.addElement(label);
                     this._lineMap.addElement(new Object(i + 1));
                  }
               } else {
                  phoneNumbers.addElement(number);
                  this._lineMap.addElement(new Object(i + 1));
               }
            }
         }
      } finally {
         return;
      }
   }

   private final void addSIMCardItem(String displayName, String value, int indent) {
      _simCardItems.addElement(new SIMCardOptionsItem$SIMCardItem(displayName, value, indent));
   }

   private final String getPIN(int id) {
      int retries;
      try {
         retries = SIMCard.getPINRetriesRemaining(id);
      } finally {
         ;
      }

      if (retries == 0) {
         Dialog.alert(OptionsResources.getString(1532));
         return null;
      } else {
         return this.getSIMCode(
            false,
            MessageFormat.format(
               ((StringBuffer)(new Object())).append(_srb.getString(id == 1 ? 214 : 215)).append(_srb.getString(212)).toString(),
               new Object[]{new Object(retries)}
            )
         );
      }
   }

   private final String getSIMCode(boolean puk, String prompt) {
      SIMCodeDialog d = (SIMCodeDialog)(new Object(puk ? 2 : 1));
      d.show(prompt);
      return d.getCloseReason() == -1 ? null : d.getText();
   }

   private final void changePIN(byte[] puk, int pinID) {
      byte[] oldPin = null;
      if (puk == null) {
         String pin = this.getPIN(pinID);
         if (pin == null) {
            return;
         }

         oldPin = pin.getBytes();
      }

      String newPIN = this.getSIMCode(false, _srb.getString(pinID == 1 ? 205 : 206));
      if (newPIN != null) {
         String verifiedPIN = this.getSIMCode(false, _srb.getString(pinID == 1 ? 207 : 208));
         if (verifiedPIN != null) {
            if (newPIN.compareTo(verifiedPIN) != 0) {
               Dialog.alert(_srb.getString(209));
            } else {
               try {
                  SIMCard.requestChangePIN(pinID, puk, oldPin, newPIN.getBytes());
               } finally {
                  String[] parms = new Object[1];
                  if (pinID == 2) {
                     parms[0] = "2";
                  } else {
                     parms[0] = "";
                  }

                  Dialog.alert(MessageFormat.format(OptionsResources.getString(1513), parms));
                  return;
               }
            }
         }
      }
   }

   private final void updateScreenContents() {
      if (!this._updating) {
         this._updating = true;
         if (this._simCardItemsField != null) {
            super._mainScreen.delete(this._simCardItemsField);
            this._simCardItemsField.setCallback(null);
            this._simCardItemsField = null;
         }

         this.addSIMCardItems(super._mainScreen);
         this._titleField.setText(this.getTitle());
         this._updating = false;
      }
   }

   private final void processSIMResponse(int code, String successMsg, String failedMsg) {
      switch (code) {
         case 0:
            Status.show(successMsg);
            break;
         case 6:
            Dialog.alert(_srb.getString(211));
            break;
         default:
            Dialog.alert(failedMsg);
      }

      this.updateScreenContents();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void forceNewPIN(int pinID) {
      if (this.isValidSIMInserted()) {
         boolean var5 = false /* VF: Semaphore variable */;

         int retries;
         try {
            var5 = true;
            retries = SIMCard.getPUKRetriesRemaining(pinID);
            var5 = false;
         } finally {
            if (var5) {
               return;
            }
         }

         String puk = this.getSIMCode(
            true,
            MessageFormat.format(
               ((StringBuffer)(new Object())).append(_srb.getString(pinID == 1 ? 217 : 213)).append(_srb.getString(212)).toString(),
               new Object[]{new Object(retries)}
            )
         );
         if (puk != null) {
            this.changePIN(puk.getBytes(), pinID);
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

   private final String getTitle() {
      StringBuffer titleBuffer = (StringBuffer)(new Object(this.getDisplayName()));
      if (SIMCard.isSecuritySupported()) {
         titleBuffer.append(": ");
         int rc = 1506;

         label35:
         try {
            if (SIMCard.isValid()) {
               if (SIMCard.isPUKRequired(1)) {
                  rc = 1924;
               } else if (SIMCard.isPINEnabled()) {
                  rc = 1507;
               } else {
                  rc = 1508;
               }
            }
         } finally {
            break label35;
         }

         titleBuffer.append(OptionsResources.getString(rc));
      }

      return titleBuffer.toString();
   }

   @Override
   protected final void open() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      this.addSIMCardItems(mainScreen);
      SIMCard.addListener(Application.getApplication(), this);
   }

   private final void addSIMCardItems(MainScreen mainScreen) {
      this.createSIMCardItems();
      if (_simCardItems.size() > 0) {
         ListField simCardItemsField = (ListField)(new Object(_simCardItems.size(), 64));
         simCardItemsField.setCallback(this);
         mainScreen.add(simCardItemsField);
         simCardItemsField.setFocus();
         this._simCardItemsField = simCardItemsField;
      }
   }

   @Override
   public final boolean confirm(Verb verb, Object context) {
      if (!super.confirm(verb, context)) {
         return false;
      }

      SIMCard.removeListener(Application.getApplication(), this);
      return true;
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      this._backdoorChars = BackdoorKeyProcessor.appendKeyDetectingMultitap(
         this._backdoorChars, CharacterUtilities.toUpperCase(Keypad.map(keycode), 1701707776)
      );
      this._backdoorKeys = BackdoorKeyProcessor.appendKeyDetectingMultitap(this._backdoorKeys, Keypad.key(keycode));
      if (!this.processGSMCertificationBackdoorCode(this._backdoorChars) && !this.processMEPBackdoorCode(this._backdoorChars, this._backdoorKeys)) {
         return super.keyDown(keycode, time);
      }

      this._backdoorChars = 0;
      this._backdoorKeys = 0;
      return true;
   }

   @Override
   protected final boolean invokeOptionsAction(int action) {
      switch (action) {
         case 1:
            return true;
         default:
            return false;
      }
   }

   @Override
   protected final Field getTitleField() {
      this._titleField = (LabelField)(new Object(this.getTitle(), 64));
      return this._titleField;
   }

   public SIMCardOptionsItem() {
      super(OptionsResources.getString(1500), new Object(2, 3), -1514481539159318190L);
      ContextObject.put(super._context, 244, "sim_card");
   }

   private final void deactivateMEP(int category) {
      try {
         switch (SIMCard.getMEPState(category)) {
            case 1:
            case 5:
            case 6:
               int resId = 0;
               switch (category) {
                  case -1:
                     break;
                  case 0:
                  default:
                     resId = 7000;
                     break;
                  case 1:
                     resId = 7001;
                     break;
                  case 2:
                     resId = 7002;
                     break;
                  case 3:
                     resId = 7003;
                     break;
                  case 4:
                     resId = 7004;
               }

               int retries = SIMCard.getMEPDeactivateRetriesRemaining(category);
               String prompt = MessageFormat.format(CommonResources.getString(7010), new Object[]{CommonResources.getString(resId), new Object(retries)});
               SimplePasswordDialog d = (SimplePasswordDialog)(new Object(prompt, 1, 16, true, 0));
               d.setRevealPassword(true);
               d.show(prompt);
               if (d.getCloseReason() != -1) {
                  String code = d.getText();
                  this._mepCategory = category;
                  SIMCard.requestDeactivateMEP(category, code.getBytes());
                  return;
               }

               return;
         }
      } finally {
         return;
      }
   }

   private final String getMEPStateString(int category) {
      try {
         switch (SIMCard.getMEPState(category)) {
            case -1:
            case 4:
               return OptionsResources.getString(1531);
            case 0:
            default:
               return OptionsResources.getString(1527);
            case 1:
            case 5:
            case 6:
               return OptionsResources.getString(1528);
            case 2:
               return OptionsResources.getString(1529);
            case 3:
               return OptionsResources.getString(1530);
         }
      } finally {
         return OptionsResources.getString(1531);
      }
   }
}
