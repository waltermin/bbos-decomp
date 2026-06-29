package net.rim.device.apps.internal.browser.bookmark;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.ui.BrowserChoiceField;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.VerticalSpacerField;
import org.w3c.dom.html2.HTMLLinkElement;

public final class DialogAddRSSBookmark extends PopupScreen implements FieldChangeListener {
   private ObjectChoiceField _folderNameField;
   private DateField _startTimeField;
   private ObjectChoiceField _updateIntervalField;
   private Folder _selectedFolder;
   private BrowserChoiceField _browserChoiceField;
   private ButtonField _buttonAdd;
   private ButtonField _buttonCancel;
   private CheckboxField[] _checkboxes;
   private HTMLLinkElement[] _feeds;
   private BrowserContent _browserContent;

   public DialogAddRSSBookmark(BrowserContent content, HTMLLinkElement[] feeds) {
      super(new VerticalFieldManager(1153220571769602048L), 196608);
      VerticalFieldManager localVfm = (VerticalFieldManager)this.getDelegate();
      this._browserContent = content;
      this._feeds = feeds;
      this._selectedFolder = BookmarksFolderList.getDefaultFolderForSessionConfig();
      String folderName = this._selectedFolder.getFriendlyName();
      this._selectedFolder = BookmarksFolderList.getDefaultFolder();
      localVfm.add(new RichTextField(BrowserResources.getString(751), 36028797018963968L));
      VerticalFieldManager checkboxVfm = new VerticalFieldManager(299067162755072L);
      int size = feeds.length;
      this._checkboxes = new CheckboxField[size];

      for (int i = 0; i < size; i++) {
         this._checkboxes[i] = new CheckboxField(feeds[i].getTitle(), false);
         checkboxVfm.add(this._checkboxes[i]);
      }

      localVfm.add(checkboxVfm);
      int fontHeight = Font.getDefault().getHeight();
      localVfm.add(new VerticalSpacerField(fontHeight >> 1));
      this._folderNameField = new ObjectChoiceField(BrowserResources.getString(297), new String[]{folderName}, 0, 9007199254740992L);
      localVfm.add(this._folderNameField);
      this._updateIntervalField = new ObjectChoiceField(BrowserResources.getString(759), BrowserResources.getStringArray(757));
      if (!PersistentContent.isEncryptionEnabled() && !ITPolicy.getBoolean(30, 11, false)) {
         this._updateIntervalField.setChangeListener(this);
         localVfm.add(this._updateIntervalField);
      }

      this._startTimeField = new DateField(BrowserResources.getString(758), 28800000, 32);
      this._startTimeField.setTimeZone(TimeZone.getTimeZone(DateTimeUtilities.GMT));
      String currentConfigUID = null;
      BrowserSession currentSession = BrowserSession.getCurrentSession();
      if (currentSession != null) {
         currentConfigUID = currentSession.getConfig().getUid();
      }

      this._browserChoiceField = new BrowserChoiceField(BrowserResources.getString(819), currentConfigUID);
      localVfm.add(this._browserChoiceField);
      this._buttonAdd = new ButtonField(CommonResources.getString(9045), 98304);
      this._buttonCancel = new ButtonField(CommonResource.getString(19), 98304);
      this._buttonAdd.setChangeListener(this);
      this._buttonCancel.setChangeListener(this);
      FlowFieldManager fmgr = new FlowFieldManager(12884901888L);
      fmgr.add(this._buttonAdd);
      fmgr.add(this._buttonCancel);
      localVfm.add(fmgr);
   }

   @Override
   protected final boolean onSavePrompt() {
      switch (Dialog.ask(1, CommonResource.getString(10003))) {
         case 0:
            return false;
         case 1:
         default:
            this.onSave();
            return true;
         case 2:
            return true;
      }
   }

   public final void show() {
      UiApplication.getUiApplication().pushModalScreen(this);
   }

   @Override
   protected final void makeMenu(Menu menu, int context) {
      super.makeMenu(menu, context);
      menu.add(new DialogAddRSSBookmark$1(this, CommonResources.getString(9045), 1, 1));
      if (this.getFieldWithFocus() == this._folderNameField) {
         menu.add(new DialogAddRSSBookmark$2(this, BrowserResources.getString(298), 100, 1));
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._updateIntervalField && context != Integer.MIN_VALUE) {
         if (this._updateIntervalField.getSelectedIndex() == 0) {
            if (this._startTimeField.getManager() != null) {
               this.getDelegate().delete(this._startTimeField);
               return;
            }
         } else if (this._startTimeField.getManager() == null) {
            this.getDelegate().insert(this._startTimeField, this._updateIntervalField.getIndex() + 1);
            return;
         }
      } else {
         if (field == this._buttonAdd) {
            this.onSave();
            UiApplication.getUiApplication().popScreen(this);
            return;
         }

         if (field == this._buttonCancel) {
            this.onClose();
         }
      }
   }

   @Override
   public final void save() {
      byte updateFlags = 0;
      int updatePeriod = 0;
      int updateStart = 0;
      int selectedIndex = this._updateIntervalField.getSelectedIndex();
      if (selectedIndex > 0) {
         updateFlags = 1;
         updatePeriod = DialogAddBookmark.getUpdatePeriodValue(selectedIndex);
      }

      if (updatePeriod != 0) {
         Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(DateTimeUtilities.GMT));
         ((CalendarExtensions)cal).setTimeLong(this._startTimeField.getDate());
         updateStart = cal.get(11) * 3600000 + cal.get(12) * 60000;
      }

      int size = this._checkboxes.length;

      for (int i = 0; i < size; i++) {
         if (this._checkboxes[i].getChecked()) {
            String href = this._feeds[i].getHref();
            if (this._browserContent != null) {
               href = this._browserContent.resolveUrl(href);
            }

            Bookmarks.addBookmarkToFolder(
               this._feeds[i].getTitle(),
               href,
               this._selectedFolder,
               false,
               null,
               updateFlags,
               updateStart,
               updatePeriod,
               this._browserChoiceField.getSelectedBrowser()
            );
         }
      }
   }

   static final boolean access$000(DialogAddRSSBookmark x0) {
      return x0.onSave();
   }
}
