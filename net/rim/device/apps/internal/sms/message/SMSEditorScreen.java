package net.rim.device.apps.internal.sms.message;

import java.util.Hashtable;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.HolsterListener;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SMSPacketHeader;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.menu.MenuScreen;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.util.DraftSaveable;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.framework.ModelScreen;
import net.rim.device.apps.internal.sms.SMSModel;
import net.rim.device.apps.internal.sms.SMSPayloadModel;
import net.rim.device.apps.internal.sms.SMSService;
import net.rim.device.apps.internal.sms.resources.SMSResources;
import net.rim.device.apps.internal.sms.ui.SMSEditField;
import net.rim.device.apps.internal.sms.ui.SMSFilter;
import net.rim.device.internal.deviceoptions.SMSOptions;

public final class SMSEditorScreen extends ModelScreen implements FieldChangeListener, DraftSaveable, HolsterListener {
   private SMSModel _model;
   private SMSModel _backupModel;
   private SMSEditField _textField;
   private LabelField _numberOfCharactersField;
   private StringBuffer _numberOfCharactersStringBuffer = new StringBuffer();
   private SMSSendVerb _sendVerb = new SMSSendVerb(this);
   private SMSSaveDraftVerb _saveDraftVerb = new SMSSaveDraftVerb(this);
   private VerticalFieldManager _addressFieldManager;
   private Manager _historyManager;
   boolean _runAgain;
   private boolean _segmented;
   private int _truncatedCharacterCount = 0;
   private Hashtable _addressHashtable;
   private static final boolean DISPLAY_NUMBER_CHARS_REMAINING = false;

   public final Object run() {
      if (!SMSService.canSend()) {
         Dialog.alert(SMSResources.getString(50));
      }

      return super.go();
   }

   protected final void populateScreen(int modelCoding) {
      VerticalFieldManager vfm = new VerticalFieldManager();
      ContextObject addressContextObject = ContextObject.clone(super._context);
      addressContextObject.clearFlag(0);
      addressContextObject.setFlag(9);
      addressContextObject.setFlag(106);
      addressContextObject.setFlag(55);
      addressContextObject.setFlag(1);
      addressContextObject.clearFlag(128);
      ContextObject.remove(addressContextObject, 252);
      this._addressFieldManager = new VerticalFieldManager();
      RIMModel[] addresses = this._model._payload.getAddresses();
      this._addressHashtable = new Hashtable(1);
      int total = addresses.length;

      for (int i = 0; i < total; i++) {
         FieldProvider fieldProvider = (FieldProvider)addresses[i];
         Field field = fieldProvider.getField(ContextObject.clone(addressContextObject));
         if (field != null) {
            this._addressFieldManager.add(field);
            this._addressHashtable.put(field, addresses[i]);
         }
      }

      this._numberOfCharactersField = new LabelField("", 1152921504606846980L);
      this._numberOfCharactersField.setPosition(0);
      vfm.add(new LabelField(SMSResources.getString(2), 6));
      vfm.add(this._numberOfCharactersField);
      this.setTitle(vfm);
      this.add(this._addressFieldManager);
      this.add(new SeparatorField());
      long style = 4503599627374592L;
      if (SMSOptions.getDisableAutoText()) {
         style |= 131072;
      }

      this._textField = new SMSEditField(null, null, this.getMaxNumberOfCharacters(modelCoding), style, modelCoding);
      SMSFilter smsFilter = (SMSFilter)this._textField.getFilter();
      String initialText = this._model._payload.getText();
      int numberOfAddresses = this._model._payload.getAddresses().length;
      if (initialText != null) {
         int maxEncodedLength = this.getMaxNumberOfCharacters() - this.getNumberOfCharactersToReserveForAddresses();
         int encodedLength = smsFilter.getEncodedLength(initialText);
         if (encodedLength > maxEncodedLength) {
            this._truncatedCharacterCount = encodedLength - maxEncodedLength;

            int initialTextLength;
            for (initialTextLength = initialText.length();
               encodedLength > maxEncodedLength && initialTextLength > 0;
               encodedLength -= smsFilter.getEncodedLength(initialText.charAt(initialTextLength))
            ) {
               initialTextLength--;
            }

            initialText = initialText.substring(0, initialTextLength);
         }

         this._textField.setText(initialText);
         if (numberOfAddresses > 0 && addressContextObject.getFlag(13)) {
            this.setDirty(true);
         }
      } else if (numberOfAddresses > 0 && !addressContextObject.getFlag(12)) {
         this.setDirty(true);
      }

      this._textField.setChangeListener(this);
      this.add(this._textField);
      this._textField.setFocus();
      this.updateNumberOfCharactersField(true);
      ContextObject contextObject = ContextObject.clone(super._context);
      contextObject.setFlag(54);
      this._historyManager = new VerticalFieldManager();
      long fromTime = this._model._payload._creationDate;
      boolean includeMessageAtFromTime = false;
      Object selectedItem = contextObject.get(250);
      if (selectedItem instanceof SMSModel) {
         SMSModel smsModel = (SMSModel)selectedItem;
         if (smsModel.getOverallStatus() != Integer.MAX_VALUE) {
            fromTime = ((SMSModel)selectedItem)._payload._creationDate;
            includeMessageAtFromTime = true;
         }
      }

      SMSViewerScreen.includeMessageThread(this._model, this._historyManager, contextObject, fromTime, includeMessageAtFromTime);
      if (this._historyManager.getFieldCount() == 0) {
         this._historyManager = null;
      } else {
         this.add(this._historyManager);
      }
   }

   final int getNumberOfCharactersUsed() {
      return this._textField.getEncodedLength();
   }

   final int getMaxNumberOfCharacters() {
      return this.getMaxNumberOfCharacters(this._model._payload.getMessageCoding());
   }

   public final int getNumberOfCharactersToReserveForAddresses() {
      SMSFilter sf = (SMSFilter)this._textField.getFilter();
      return sf.getNumberOfCharactersToReserveForAddresses(this._model);
   }

   public final void setBackupModel(SMSModel backupModel) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final void draftSaveOnClose() {
      ContextObject context = new ContextObject();
      ContextObject.setFlag(context, 121);
      this._saveDraftVerb.invoke(context);
   }

   @Override
   public final void inHolster() {
      this.closeEditor();
   }

   @Override
   public final void outOfHolster() {
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._textField) {
         this.updateNumberOfCharactersField(false);
      }
   }

   @Override
   public final Object getModel() {
      this._model.ungroupMessage();
      this._model.setBody(this._textField.getText());
      return this._model;
   }

   private final void updateNumberOfCharactersField(boolean updateMaxSizeOfEditField) {
      int numCharactersUsed = this.getNumberOfCharactersUsed();
      int addressSize = this.getNumberOfCharactersToReserveForAddresses();
      int maxSizeOfEditField = this.getMaxNumberOfCharacters() - addressSize;
      int modelCoding = this._model._payload.getMessageCoding();
      this._numberOfCharactersStringBuffer.setLength(0);
      if (!this._segmented) {
         int lengthRemaining = maxSizeOfEditField - numCharactersUsed;
         this._numberOfCharactersStringBuffer.append(lengthRemaining);
         if (this._segmented) {
            this._numberOfCharactersStringBuffer.append('/');
            this._numberOfCharactersStringBuffer.append(SMSPacketHeader.getSegments(numCharactersUsed, modelCoding));
         }
      } else {
         int segmentCount = SMSPacketHeader.getSegments(numCharactersUsed, modelCoding);
         int maxCharacters = SMSPacketHeader.getCharacters(segmentCount, modelCoding);
         this._numberOfCharactersStringBuffer.append(maxCharacters - numCharactersUsed);
         this._numberOfCharactersStringBuffer.append(" / ");
         this._numberOfCharactersStringBuffer.append(segmentCount);
         this._numberOfCharactersStringBuffer.append(SMSResources.getString(753));
         this._numberOfCharactersStringBuffer.append(SMSPacketHeader.getSegments(maxSizeOfEditField, modelCoding));
      }

      this._numberOfCharactersField.setText(this._numberOfCharactersStringBuffer);
      if (updateMaxSizeOfEditField) {
         this._textField.setMaxSize(maxSizeOfEditField);
      }
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      int numberOfAddresses = this._model._payload.getAddresses().length;
      if (SMSService.canSend()) {
         menu.add(this._sendVerb);
      }

      menu.add(this._saveDraftVerb);
      if (instance != 65536) {
         ContextObject.put(super._context, 250, this.getModel());
         VerbRepository verbRepository = VerbRepository.getVerbRepository(1247995981604341294L);
         menu.add(verbRepository.getVerbs(null));
         verbRepository = VerbRepository.getVerbRepository(4101976187669332923L);
         if (verbRepository != null) {
            menu.add(verbRepository.getVerbs(null));
         }

         if (this._historyManager != null) {
            menu.add(new SMSEditorScreen$RemoveHistoryVerb(this));
         }

         if (SMSService.isSMSToMultipleRecipientsSupported() && numberOfAddresses < 10) {
            Verb addressVerb = new SMSEditAddressVerb(this, 0, null);
            menu.add(addressVerb);
         }
      }

      if (this.getFieldWithFocus() == this._addressFieldManager) {
         RIMModel selectedAddress = (RIMModel)this._addressHashtable.get(this._addressFieldManager.getFieldWithFocus());
         if (instance != 65536 && SMSService.isSMSToMultipleRecipientsSupported() && numberOfAddresses > 1) {
            Verb addressVerb = new SMSEditAddressVerb(this, 2, selectedAddress);
            menu.add(addressVerb);
         }

         Verb addressVerb = new SMSEditAddressVerb(this, 1, selectedAddress);
         menu.add(addressVerb);
         menu.setDefault(addressVerb);
      } else if (SMSService.canSend()) {
         menu.setDefault(this._sendVerb);
      } else {
         menu.setDefault(this._saveDraftVerb);
      }
   }

   @Override
   public final void save() {
      this._saveDraftVerb.invoke(super._context);
   }

   @Override
   protected final boolean onSavePrompt() {
      int answer = Dialog.ask(1);
      if (answer == 1) {
         return this.onSave();
      }

      if (answer == -1) {
         return false;
      }

      if (answer == 2 && this._backupModel != null) {
         this._model._payload = this._backupModel._payload;
      }

      return true;
   }

   @Override
   protected final Object invokeVerb(Verb verb, Object context) {
      ContextObject contextObject = ContextObject.clone(context);
      contextObject.setFlag(55);
      contextObject.setFlag(40);
      return super.invokeVerb(verb, contextObject);
   }

   static final Object runEditor(Object context, SMSModel message) {
      ContextObject contextObject = ContextObject.castOrCreate(context);
      SMSMessageModel backupModel = new SMSMessageModel(message);
      boolean editable = contextObject.getFlag(0);
      if (!editable) {
         contextObject.setFlag(0);
      }

      boolean editorWasDirty = false;
      SMSEditorScreen editor = null;

      Object retFromEditor;
      do {
         editorWasDirty = editor != null && editor.isDirty();
         editor = new SMSEditorScreen(contextObject);
         editor.setModel(message);
         editor.setBackupModel(backupModel);
         if (editorWasDirty) {
            editor.setDirty(true);
         }

         retFromEditor = editor.run();
      } while (retFromEditor instanceof ContextObject && editor._runAgain);

      if (!editable) {
         contextObject.clearFlag(0);
      }

      return retFromEditor;
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      if (attached) {
         super.onUiEngineAttached(attached);
         super._application.addHolsterListener(this);
         if (this._truncatedCharacterCount > 0) {
            Status.show(MessageFormat.format(SMSResources.getString(417), new String[]{Integer.toString(this._truncatedCharacterCount)}));
            this._truncatedCharacterCount = 0;
            return;
         }
      } else {
         this._model = null;
         this._backupModel = null;
         super._application.removeHolsterListener(this);
         super.onUiEngineAttached(attached);
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 7884295420352689779L) {
         this.close();
      }

      if (guid == -8040378802380461050L) {
         this.checkIfLocaleAndEncodingMatch();
      } else if (guid == -7131874474196788121L && PersistentContent.isEncryptionEnabled()) {
         this.closeEditor();
      }

      super.eventOccurred(guid, data0, data1, object0, object1);
   }

   private final synchronized void closeEditor() {
      Screen activeScreen = UiApplication.getUiApplication().getActiveScreen();
      if (activeScreen instanceof MenuScreen) {
         UiApplication.getUiApplication().popScreen(activeScreen);
         activeScreen = UiApplication.getUiApplication().getActiveScreen();
      }

      if (activeScreen == this) {
         this.draftSaveOnClose();
         UiApplication.getUiApplication().popScreen(this);
      }
   }

   private final int getMaxNumberOfCharacters(int modelCoding) {
      return this._segmented ? SMSPacketHeader.getCharacters(6, modelCoding) : SMSPacketHeader.getCharacters(1, modelCoding);
   }

   @Override
   public final void setModel(Object model) {
      super.setModel(model);
      this._model = (SMSModel)model;
      this.checkIfLocaleAndEncodingMatch();
      int modelCoding = this._model._payload.getMessageCoding();
      this.deleteAll();
      this.populateScreen(modelCoding);
      this.setSecurityServiceColours(false);
   }

   private final void checkIfLocaleAndEncodingMatch() {
      Locale selectedLocale = Locale.getDefaultInputForSystem();
      boolean changeTheEncoding = false;
      int currentEncoding = this._model._payload.getMessageCoding();
      int newEncoding = currentEncoding;
      int defaultEncoding = SMSPayloadModel.getPreferredMessageCoding();
      int internationalEncoding = 2;
      if ((RadioInfo.getSupportedWAFs() & 2) != 0) {
         internationalEncoding = 5;
      }

      if (!Locale.isLatinOneCharacterSetLocale(selectedLocale)) {
         if (currentEncoding != internationalEncoding) {
            newEncoding = internationalEncoding;
            changeTheEncoding = true;
         }
      } else if (currentEncoding == internationalEncoding && currentEncoding != defaultEncoding && this.getNumberOfCharactersUsed() == 0) {
         newEncoding = defaultEncoding;
         changeTheEncoding = true;
      }

      if (changeTheEncoding) {
         String text = this._model._payload.getText();
         this._model._payload.setByteField(0, newEncoding);
         if (text != null) {
            this._model._payload.setText(text);
         }

         if (this._textField != null) {
            this._textField.updateTextFilter(newEncoding);
         }
      }
   }

   private final void removeHistory() {
      if (this._historyManager != null) {
         this.delete(this._historyManager);
         this._historyManager = null;
         this._model.setFlags(32);
      }
   }

   private SMSEditorScreen(Object context) {
      super(0, null, context);
      this._segmented = SMSPacketHeader.isSegmentationSupported();
      if (super._context instanceof ContextObject) {
         ContextObject contextObject = (ContextObject)super._context;
         contextObject.setFlag(96);
         contextObject.put(244, new Integer(26407));
      }
   }
}
