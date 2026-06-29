package net.rim.device.apps.api.messaging.messagelist;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.messaging.resources.MessageResources;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.internal.system.InternalServices;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.im.SLControlObject;

public final class MessageListOptionsScreen extends SaveableMainScreenOptionsListItem implements FieldChangeListener {
   private BooleanChoiceField _displayTime;
   private BooleanChoiceField _displayName;
   private ObjectChoiceField _displayMessageCount;
   private VerticalFieldManager _displayMessageOptions;
   private boolean _displayNewMessagIndicatorField;
   private BooleanChoiceField _displayNewMessagIndicator;
   private BooleanChoiceField _confirmDelete;
   private BooleanChoiceField _confirmMarkPriorOpened;
   private BooleanChoiceField _hideFiled;
   private BooleanChoiceField _hideSent;
   private BooleanChoiceField _pinLevel1;
   private BooleanChoiceField _autoMore;
   private BooleanChoiceField _spellCheckBeforeSendEmail;
   private ObjectChoiceField _keepMessagesDuration;
   private ObjectChoiceField _SMSEmailInbox;
   private ObjectChoiceField _messageListLineMode;
   private ObjectChoiceField _listSeparatorAppearance;
   private VerticalFieldManager _autoAttachmentDownloadOptionsMgr;
   private boolean _displayHighSpeedNetworkField;
   private ObjectChoiceField _autoAttachmentDownload;
   private BooleanChoiceField _highSpeedNetwork;
   private MainScreen _screen;
   private MessageListOptions _messageListOptions = MessageListOptions.getOptions();
   private static Tag OPTIONS_SECTION_AREA_TAG = Tag.create("options-section-area");
   private static Tag OPTIONS_SECTION_HEADER_TAG = Tag.create("options-section-header");

   public MessageListOptionsScreen() {
      super(MessageResources.getBundle(), 192);
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      this._screen = mainScreen;
      Manager section = this.createSection(CommonResources.getString(9177));
      this._displayTime = this.createField(MessageResources.getString(27), 1, section);
      this._displayName = this.createField(MessageResources.getString(28), 2, section);
      this.addMessageListLineModeField(section);
      this.initDisplayMessageCountAndDisplayNewMessageIndicatorFields(section);
      if (MessageListOptions.isAutoAttachmentDownloadEnabled()) {
         this.initAutoAttachmentDownloadFields(section);
      }

      this._hideFiled = this.createField(MessageResources.getString(31), 16, section);
      this._hideSent = this.createField(MessageResources.getString(200), 512, section);
      if (ThemeManager.getActiveTheme().getOption("CombinedInboxToggleNotAllowed") == null) {
         this.addSMSEmailInboxField(section);
      }

      this.addListSeparatorAppearanceField(section);
      section = this.createSection(CommonResources.getString(9179));
      if (InternalServices.isPINMessagingSupported()) {
         this._pinLevel1 = this.createField(MessageResources.getString(77), 32, section);
      }

      this._autoMore = this.createField(MessageResources.getString(156), 64, section);
      this._confirmDelete = this.createField(CommonResources.getString(2008), 4, section);
      this._confirmMarkPriorOpened = this.createField(MessageResources.getString(240), 4096, section);
      SLControlObject co = (SLControlObject)InputContext.getInstance().getInputMethodControlObject();
      if (co != null && co.getIMProperties((byte)2) != null) {
         this._spellCheckBeforeSendEmail = this.createField(MessageResources.getString(177), 128, section);
      }

      this.addKeepMessagesDurationField(section);
   }

   protected final Manager createSection(String title) {
      Manager section = new VerticalFieldManager(1153484454560268288L);
      LabelField titleField = new LabelField(title, 1152921504606846976L);
      section.add(titleField);
      section.add(new SeparatorField());
      section.setTag(OPTIONS_SECTION_AREA_TAG);
      titleField.setTag(OPTIONS_SECTION_HEADER_TAG);
      this._screen.add(section);
      return section;
   }

   private final void addKeepMessagesDurationField(Manager manager) {
      short[] durationValues = this._messageListOptions.getKeepMessagesDurationChoices();
      int numDurations = durationValues.length;
      String[] durations = new String[numDurations];
      short currentDuration = this._messageListOptions.getKeepMessagesDuration();
      int initialIndex = 0;
      StringBuffer sb = new StringBuffer();

      for (int i = 0; i < numDurations; i++) {
         short duration = durationValues[i];
         if (duration < 0) {
            durations[i] = CommonResources.getString(9145);
         } else {
            sb.setLength(0);
            if (duration >= 60 && duration % 30 == 0) {
               sb.append(duration / 30);
               sb.append(' ');
               sb.append(CommonResources.getString(9165));
            } else {
               sb.append(duration);
               sb.append(' ');
               sb.append(CommonResources.getString(9144));
            }

            durations[i] = sb.toString();
         }

         if (duration == currentDuration) {
            initialIndex = i;
         }
      }

      this._keepMessagesDuration = new ObjectChoiceField(MessageResources.getString(193), durations, initialIndex);
      this._keepMessagesDuration.setCookie(durationValues);
      manager.add(this._keepMessagesDuration);
   }

   private final void addSMSEmailInboxField(Manager manager) {
      int initialIndex = this._messageListOptions.getSMSEmailInbox();
      this._SMSEmailInbox = new ObjectChoiceField(MessageResources.getString(222), MessageResources.getStringArray(223), initialIndex);
      manager.add(this._SMSEmailInbox);
   }

   private final void addListSeparatorAppearanceField(Manager manager) {
      int initialIndex = this._messageListOptions.getListSeparatorAppearance();
      this._listSeparatorAppearance = new ObjectChoiceField(MessageResources.getString(238), MessageResources.getStringArray(239), initialIndex);
      manager.add(this._listSeparatorAppearance);
   }

   private final BooleanChoiceField createField(String name, int flag, Manager manager) {
      BooleanChoiceField field = new BooleanChoiceField(name, 0, this._messageListOptions.getFlag(flag));
      if (manager != null) {
         manager.add(field);
      }

      return field;
   }

   @Override
   protected final boolean save() {
      boolean dirtyField = false;
      dirtyField |= this.checkOption(1, this._displayTime);
      dirtyField |= this.checkOption(2, this._displayName);
      dirtyField |= this.checkOption(4, this._confirmDelete);
      dirtyField |= this.checkOption(4096, this._confirmMarkPriorOpened);
      dirtyField |= this.checkOption(16, this._hideFiled);
      dirtyField |= this.checkOption(512, this._hideSent);
      if (this._pinLevel1 != null) {
         dirtyField |= this.checkOption(32, this._pinLevel1);
      }

      dirtyField |= this.checkOption(64, this._autoMore);
      dirtyField |= this.checkOption(128, this._spellCheckBeforeSendEmail);
      dirtyField |= this.checkOption(1024, this._displayNewMessagIndicator);
      short[] keepMessagesDurationChoices = (short[])this._keepMessagesDuration.getCookie();
      short keepMessagesDuration = keepMessagesDurationChoices[this._keepMessagesDuration.getSelectedIndex()];
      if (keepMessagesDuration != this._messageListOptions.getKeepMessagesDuration()) {
         this._messageListOptions.setKeepMessagesDuration(keepMessagesDuration);
         dirtyField = true;
      }

      if (this._SMSEmailInbox != null) {
         short SMSEmailInboxSetting = (short)this._SMSEmailInbox.getSelectedIndex();
         if (SMSEmailInboxSetting != this._messageListOptions.getSMSEmailInbox()) {
            this._messageListOptions.setSMSEmailInbox(SMSEmailInboxSetting);
            ShowMessageApp.postEvent(-8639396151207124460L, 0, 0, new Long(-4696470826620059293L), null);
            dirtyField = true;
         }
      }

      if (this._messageListLineMode != null) {
         int originalValue = this._messageListOptions.getMessageListLineMode();
         int newValue = this._messageListLineMode.getSelectedIndex();
         if (originalValue != newValue) {
            this._messageListOptions.setMessageListLineMode(newValue);
            dirtyField = true;
         }
      }

      if (this._displayMessageCount != null) {
         int selectedIndex = this._displayMessageCount.getSelectedIndex();
         if (selectedIndex != this._messageListOptions.getDisplayMessageCount()) {
            this._messageListOptions.setDisplayMessageCount((short)selectedIndex);
            dirtyField = true;
         }
      }

      if (MessageListOptions.isAutoAttachmentDownloadEnabled()) {
         dirtyField |= this.checkOption(2048, this._highSpeedNetwork);
         if (this._autoAttachmentDownload != null) {
            int selectedIndex = this._autoAttachmentDownload.getSelectedIndex();
            if (selectedIndex != this._messageListOptions.getAutoDownloadAttachments()) {
               this._messageListOptions.setAutoDownloadAttachments((short)selectedIndex);
               dirtyField = true;
            }
         }
      }

      if (this._listSeparatorAppearance != null) {
         int originalValue = this._messageListOptions.getListSeparatorAppearance();
         int newValue = this._listSeparatorAppearance.getSelectedIndex();
         if (originalValue != newValue) {
            this._messageListOptions.setListSeparatorAppearance((short)newValue);
            dirtyField = true;
         }
      }

      if (dirtyField) {
         this._messageListOptions.commit();
      }

      return super.save();
   }

   private final boolean checkOption(int flag, BooleanChoiceField field) {
      if (field != null) {
         boolean choice = field.isAffirmative();
         if (this._messageListOptions.getFlag(flag) != choice) {
            this._messageListOptions.setFlag(flag, choice);
            return true;
         }
      }

      return false;
   }

   private final void addMessageListLineModeField(Manager manager) {
      String label = MessageResources.getString(220);
      int initIndex = this._messageListOptions.getMessageListLineMode();
      this._messageListLineMode = new ObjectChoiceField(label, MessageResources.getStringArray(221), initIndex);
      manager.add(this._messageListLineMode);
   }

   private final void initDisplayMessageCountAndDisplayNewMessageIndicatorFields(Manager manager) {
      this._displayMessageOptions = new VerticalFieldManager();
      manager.add(this._displayMessageOptions);
      String label = MessageResources.getString(215);
      short initIndex = this._messageListOptions.getDisplayMessageCount();
      this._displayMessageCount = new ObjectChoiceField(label, MessageResources.getStringArray(216), initIndex);
      this._displayMessageOptions.add(this._displayMessageCount);
      this._displayNewMessagIndicatorField = this.showDisplayNewMessageIndicator();
      this._displayNewMessagIndicator = this.createField(MessageResources.getString(217), 1024, null);
      this._displayNewMessagIndicator.setAffirmative(this._messageListOptions.getDisplayNewMessageIndicator());
      if (this._displayNewMessagIndicatorField) {
         this._displayMessageOptions.add(this._displayNewMessagIndicator);
      }

      this._displayMessageCount.setChangeListener(this);
   }

   private final boolean showDisplayNewMessageIndicator() {
      return this._displayMessageCount.getSelectedIndex() == 1;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field != null) {
         if (field == this._displayMessageCount) {
            this.processDisplayMessageCount(field, context);
         } else {
            if (field == this._autoAttachmentDownload) {
               this.processAutoAttachmentDownload(field, context);
            }
         }
      }
   }

   private final void processDisplayMessageCount(Field field, int context) {
      boolean toggled = this.checkOption(1024, this._displayNewMessagIndicator);
      boolean lastSavedNewMessageIndicator = this._messageListOptions.getDisplayNewMessageIndicator();
      if (this.showDisplayNewMessageIndicator()) {
         if (toggled) {
            this._displayNewMessagIndicator.setAffirmative(!lastSavedNewMessageIndicator);
         } else {
            this._displayNewMessagIndicator.setAffirmative(lastSavedNewMessageIndicator);
         }

         this._displayMessageOptions.add(this._displayNewMessagIndicator);
         this._displayNewMessagIndicatorField = true;
      } else {
         this._displayNewMessagIndicator.setAffirmative(true);
         if (this._displayNewMessagIndicatorField) {
            this._displayMessageOptions.delete(this._displayNewMessagIndicator);
         }

         this._displayNewMessagIndicatorField = false;
      }
   }

   private final void initAutoAttachmentDownloadFields(Manager manager) {
      this._autoAttachmentDownloadOptionsMgr = new VerticalFieldManager();
      manager.add(this._autoAttachmentDownloadOptionsMgr);
      short initValue = this._messageListOptions.getAutoDownloadAttachments();
      this._autoAttachmentDownload = new ObjectChoiceField(MessageResources.getString(231), MessageResources.getStringArray(233), initValue);
      this._autoAttachmentDownload.setChangeListener(this);
      this._autoAttachmentDownloadOptionsMgr.add(this._autoAttachmentDownload);
      this._displayHighSpeedNetworkField = this.displayHighSpeedNetworkField();
      this._highSpeedNetwork = this.createField(MessageResources.getString(232), 2048, null);
      if (this._displayHighSpeedNetworkField) {
         this._autoAttachmentDownloadOptionsMgr.add(this._highSpeedNetwork);
      }
   }

   private final boolean displayHighSpeedNetworkField() {
      int selectedIndex = this._autoAttachmentDownload.getSelectedIndex();
      return selectedIndex == 1 || selectedIndex == 2;
   }

   private final void processAutoAttachmentDownload(Field field, int context) {
      if (this.displayHighSpeedNetworkField()) {
         if (!this._displayHighSpeedNetworkField) {
            this._autoAttachmentDownloadOptionsMgr.add(this._highSpeedNetwork);
            this._displayHighSpeedNetworkField = true;
            return;
         }
      } else if (this._displayHighSpeedNetworkField) {
         this._autoAttachmentDownloadOptionsMgr.delete(this._highSpeedNetwork);
         this._displayHighSpeedNetworkField = false;
      }
   }
}
