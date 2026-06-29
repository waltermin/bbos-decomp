package net.rim.device.apps.internal.browser.bookmark;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.page.Page;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.stack.ModelResult;
import net.rim.device.apps.internal.browser.stack.RawDataCache;
import net.rim.device.apps.internal.browser.store.BrowserFolders;
import net.rim.device.apps.internal.browser.ui.BrowserCheckboxField;
import net.rim.device.apps.internal.browser.ui.BrowserChoiceField;
import net.rim.device.apps.internal.browser.ui.BrowserUrlEditField;
import net.rim.device.apps.internal.browser.util.FontCache;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.VerticalSpacerField;

public final class DialogAddBookmark extends PopupScreen implements FieldChangeListener {
   private EditField _editTitle;
   private BrowserUrlEditField _editUrl;
   private ObjectChoiceField _folderNameField;
   private CheckboxField _makeAvailableOffline;
   private Page _page;
   private Folder _selectedFolder;
   private DateField _startTimeField;
   private ObjectChoiceField _updateIntervalField;
   private BrowserChoiceField _browserChoiceField;
   private ButtonField _buttonAdd;
   private ButtonField _buttonCancel;
   private boolean _cancelled;
   static final String STRING_HTTP = "http://";
   static final String STRING_HTTPS = "https://";

   public DialogAddBookmark(Page page) {
      this(page, null, null, null, false, null);
   }

   public DialogAddBookmark(String url, String title, Folder folder, String configUID) {
      this(null, url, title, folder, url == null, configUID);
   }

   private DialogAddBookmark(Page page, String url, String title, Folder folder, boolean urlEditable, String configUID) {
      super(new VerticalFieldManager(1153220571769602048L), 196608);
      VerticalFieldManager localVfm = (VerticalFieldManager)this.getDelegate();
      this._page = page;
      title = title == null ? "" : title;
      url = url == null ? "http://" : url;
      if (this._page != null) {
         title = this._page.getFriendlyTitle();
         url = this._page.getURL();
      }

      this._selectedFolder = folder != null && folder.getLUID() != BrowserFolders.BROWSER_BOOKMARKS_FOLDER_ID
         ? folder
         : BookmarksFolderList.getDefaultFolderForSessionConfig();
      String bookmarkConfigUID = null;
      if (configUID != null) {
         BrowserConfigRecord config = BrowserConfigRecord.getDecodedConfig(configUID, BrowserConfigRecord.INVALID_VALUE, null);
         if (config != null) {
            bookmarkConfigUID = configUID;
            long folderID = BookmarksFolderList.getDefaultFolderIDForConfig(config);
            Folder tempFolder = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID, folderID);
            if (tempFolder != null) {
               this._selectedFolder = tempFolder;
            }
         }
      }

      if (bookmarkConfigUID == null) {
         String initialConfigUID = BrowserDaemonRegistry.getInstance().getInitialConfigUID();
         bookmarkConfigUID = initialConfigUID;
         if (this._page != null) {
            BrowserSession currentSession = BrowserSession.getCurrentSession();
            if (currentSession != null) {
               bookmarkConfigUID = currentSession.getConfig().getUid();
            }
         } else {
            BrowserConfigRecord initialConfig = BrowserConfigRecord.getDecodedConfig(initialConfigUID, BrowserConfigRecord.INVALID_VALUE, null);
            if (initialConfig != null) {
               bookmarkConfigUID = BookmarksFolderList.getConfigUIDForFolder(initialConfig, this._selectedFolder);
            }
         }
      }

      this._makeAvailableOffline = new BrowserCheckboxField(BrowserResources.getString(300), true);
      String folderName = this._selectedFolder.getFriendlyName();
      int fontHeight = Font.getDefault().getHeight();
      RichTextField titleField = new RichTextField(BrowserResources.getString(109), 36028797018963968L);
      titleField.setFont(FontCache.getInstance().getFont(null, 1, fontHeight));
      localVfm.add(titleField);
      localVfm.add(new VerticalSpacerField(fontHeight >> 1));
      this._editTitle = new AutoTextEditField(CommonResources.getString(2002), title, 1000000, 2147483648L);
      localVfm.add(this._editTitle);
      localVfm.add(new VerticalSpacerField(fontHeight >> 1));
      this._editUrl = new BrowserUrlEditField(BrowserResources.getString(277), url, Integer.MAX_VALUE, 2264924160L, url);
      if (urlEditable) {
         localVfm.add(this._editUrl);
         this._editUrl.setCursorPosition(url.length());
         localVfm.add(new VerticalSpacerField(fontHeight >> 1));
      }

      this._folderNameField = new ObjectChoiceField(BrowserResources.getString(297), new String[]{folderName}, 0, 9007199254740992L);
      localVfm.add(this._folderNameField);
      localVfm.add(new VerticalSpacerField(fontHeight >> 1));
      ModelResult modelResult = this._page != null ? this._page.getModelResult() : null;
      if (this._page != null && modelResult != null && modelResult.getCacheResult() != null) {
         localVfm.add(this._makeAvailableOffline);
      }

      this._updateIntervalField = new ObjectChoiceField(BrowserResources.getString(759), BrowserResources.getStringArray(757));
      if (!PersistentContent.isEncryptionEnabled() && !ITPolicy.getBoolean(30, 11, false)) {
         this._updateIntervalField.setChangeListener(this);
         localVfm.add(this._updateIntervalField);
      }

      this._startTimeField = new DateField(BrowserResources.getString(758), 28800000, 32);
      this._startTimeField.setTimeZone(TimeZone.getTimeZone(DateTimeUtilities.GMT));
      this._browserChoiceField = new BrowserChoiceField(BrowserResources.getString(819), bookmarkConfigUID);
      localVfm.add(this._browserChoiceField);
      this._buttonAdd = new ButtonField(CommonResources.getString(9045), 98304);
      this._buttonCancel = new ButtonField(CommonResource.getString(19), 98304);
      this._buttonAdd.setChangeListener(this);
      this._buttonCancel.setChangeListener(this);
      FlowFieldManager fmgr = new FlowFieldManager(12884901888L);
      fmgr.add(this._buttonAdd);
      fmgr.add(this._buttonCancel);
      localVfm.add(fmgr);
      this._editTitle.setFocus();
      UiApplication.getUiApplication().pushModalScreen(this);
   }

   public final boolean getUserInput() {
      if (this._cancelled) {
         return true;
      }

      this._editTitle.setText(this._editTitle.getText().trim());
      byte updateFlags = 0;
      int updatePeriod = 0;
      int updateStart = 0;
      int selectedIndex = this._updateIntervalField.getSelectedIndex();
      if (selectedIndex > 0) {
         updateFlags = 1;
         updatePeriod = getUpdatePeriodValue(selectedIndex);
      }

      if (updatePeriod != 0) {
         Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(DateTimeUtilities.GMT));
         ((CalendarExtensions)cal).setTimeLong(this._startTimeField.getDate());
         updateStart = cal.get(11) * 3600000 + cal.get(12) * 60000;
      }

      if (this._page == null) {
         if (this._editUrl.getText().length() != 0) {
            Bookmarks.addBookmarkToFolder(
               this._editTitle.getText(),
               this._editUrl.getText(),
               this._selectedFolder,
               false,
               null,
               updateFlags,
               updateStart,
               updatePeriod,
               this._browserChoiceField.getSelectedBrowser()
            );
            return true;
         } else {
            Status.show(BrowserResources.getString(165));
            return true;
         }
      } else {
         ModelResult modelResult = this._page.getModelResult();
         String iconUrl = null;
         if (this._page.getBrowserContent() != null) {
            iconUrl = this._page.getBrowserContent().getIconUrl();
         }

         if (modelResult != null) {
            if (this._makeAvailableOffline.getChecked() && modelResult.getCacheResult() != null) {
               RawDataCache cache = BrowserDaemonRegistry.getInstance().getRawDataCache();
               cache.put(modelResult.getURL(), modelResult.getCacheResult(), true, true);
               cache.commit();
            }

            Bookmarks.addBookmarkToFolder(
               this._editTitle.getText(),
               modelResult.getURL(),
               this._selectedFolder,
               false,
               iconUrl,
               updateFlags,
               updateStart,
               updatePeriod,
               this._browserChoiceField.getSelectedBrowser()
            );
            return true;
         } else {
            Status.show(BrowserResources.getString(165));
            return true;
         }
      }
   }

   static final int getUpdatePeriodIndex(int updatePeriod) {
      switch (updatePeriod) {
         case 3600:
            return 5;
         case 14400:
            return 4;
         case 28800:
            return 3;
         case 43200:
            return 2;
         case 86400:
            return 1;
         default:
            return 0;
      }
   }

   static final int getUpdatePeriodValue(int updatePeriodIndex) {
      switch (updatePeriodIndex) {
         case 0:
            return 0;
         case 1:
         default:
            return 86400;
         case 2:
            return 43200;
         case 3:
            return 28800;
         case 4:
            return 14400;
         case 5:
            return 3600;
      }
   }

   @Override
   protected final boolean onSavePrompt() {
      switch (Dialog.ask(1, CommonResource.getString(10003))) {
         case 0:
            this._cancelled = false;
            return false;
         case 1:
         default:
            this._cancelled = false;
            return validateBookmark(this._editTitle.getText(), this._editUrl.getText());
         case 2:
            return true;
      }
   }

   @Override
   public final boolean onClose() {
      this._cancelled = true;
      return super.onClose();
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
      } else if (field == this._buttonAdd) {
         if (validateBookmark(this._editTitle.getText(), this._editUrl.getText())) {
            UiApplication.getUiApplication().popScreen(this);
            return;
         }
      } else if (field == this._buttonCancel) {
         this.onClose();
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (super.keyChar(key, status, time)) {
         return true;
      }

      if (key == '\n' && validateBookmark(this._editTitle.getText(), this._editUrl.getText())) {
         UiApplication.getUiApplication().popScreen(this);
      }

      return false;
   }

   @Override
   protected final void makeMenu(Menu menu, int context) {
      super.makeMenu(menu, context);
      menu.add(new DialogAddBookmark$1(this, CommonResources.getString(9045), 1, 1));
      if (this.getFieldWithFocus() == this._folderNameField) {
         menu.add(new DialogAddBookmark$2(this, BrowserResources.getString(298), 100, 1));
      }
   }

   static final boolean validateBookmark(String title, String url) {
      title = title.trim();
      if (title.length() == 0) {
         Dialog.alert(BrowserResources.getString(523));
         return false;
      } else if (url.length() == 0) {
         Dialog.alert(BrowserResources.getString(665));
         return false;
      } else {
         return true;
      }
   }
}
