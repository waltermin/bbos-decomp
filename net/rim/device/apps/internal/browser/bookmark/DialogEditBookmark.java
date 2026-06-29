package net.rim.device.apps.internal.browser.bookmark;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.page.BrowserPageModel;
import net.rim.device.apps.internal.browser.page.PageModel;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.stack.CacheNode;
import net.rim.device.apps.internal.browser.stack.ModelResult;
import net.rim.device.apps.internal.browser.store.BrowserFolders;
import net.rim.device.apps.internal.browser.ui.BrowserChoiceField;
import net.rim.device.apps.internal.browser.util.FontCache;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.device.internal.i18n.CommonResource;

public final class DialogEditBookmark extends PopupScreen implements FieldChangeListener {
   private BasicEditField _editURL;
   private EditField _editTitle;
   private String _originalURL;
   private PageModel _pageModel;
   private boolean _homePage;
   private DateField _startTimeField;
   private ObjectChoiceField _updateIntervalField;
   private BrowserChoiceField _browserChoiceField;
   private ButtonField _buttonAccept;
   private ButtonField _buttonCancel;
   private boolean _cancelled;

   public DialogEditBookmark(PageModel bookmark) {
      super((Manager)(new Object(1153220571769602048L)), 196608);
      String url = bookmark.getUrl();
      String title = bookmark.getTitle();
      CacheNode cacheNode = BrowserDaemonRegistry.getInstance().getRawDataCache().getCacheNode(url);
      ModelResult modelResult = bookmark.getModelResult();
      boolean availableOffline = cacheNode != null && cacheNode.getAvailableOffline() || modelResult != null && modelResult.getCacheResult() != null;
      this._homePage = bookmark.isHomePage();
      VerticalFieldManager localVfm = (VerticalFieldManager)this.getDelegate();
      this._originalURL = url;
      this._pageModel = bookmark;
      int fontHeight = Font.getDefault().getHeight();
      RichTextField titleField = (RichTextField)(new Object(BrowserResources.getString(149), 36028797018963968L));
      titleField.setFont(FontCache.getInstance().getFont(null, 1, fontHeight));
      localVfm.add(titleField);
      localVfm.add((Field)(new Object(fontHeight >> 1)));
      String urlLabel = BrowserResources.getString(277);
      if (this._homePage) {
         this._editURL = (BasicEditField)(new Object(urlLabel, url, 1000000, 45035996273704960L));
      } else {
         this._editURL = new UrlEditField(urlLabel, url, Integer.MAX_VALUE, 2264924160L, "http://");
      }

      this._editTitle = (EditField)(new Object(CommonResources.getString(2002), title, 1000000, 2147483648L));
      localVfm.add(this._editURL);
      localVfm.add((Field)(new Object(fontHeight >> 1)));
      localVfm.add(this._editTitle);
      localVfm.add((Field)(new Object(fontHeight >> 1)));
      if (availableOffline) {
         localVfm.add((Field)(new Object(BrowserResources.getString(300), 36028797018963968L)));
         localVfm.add((Field)(new Object(fontHeight >> 1)));
      }

      int index = 0;
      int startTime = 28800000;
      byte flags = bookmark.getUpdateFlags();
      if ((flags & 1) != 0) {
         index = DialogAddBookmark.getUpdatePeriodIndex(bookmark.getUpdatePeriod());
         startTime = bookmark.getUpdateStart();
      }

      this._updateIntervalField = (ObjectChoiceField)(new Object(BrowserResources.getString(759), BrowserResources.getStringArray(757), index));
      boolean addAutoSync = !PersistentContent.isEncryptionEnabled() && !ITPolicy.getBoolean(30, 11, false);
      if (addAutoSync) {
         this._updateIntervalField.setChangeListener(this);
         localVfm.add(this._updateIntervalField);
      }

      this._startTimeField = (DateField)(new Object(BrowserResources.getString(758), startTime, 32));
      this._startTimeField.setTimeZone(TimeZone.getTimeZone(DateTimeUtilities.GMT));
      if (index != 0 && addAutoSync) {
         localVfm.add(this._startTimeField);
      }

      String initialConfigUID = BrowserDaemonRegistry.getInstance().getInitialConfigUID();
      String bookmarkConfigUID = initialConfigUID;
      BrowserConfigRecord bookmarkConfig = null;
      if (modelResult.getConfigUID() != null) {
         bookmarkConfig = BrowserConfigRecord.getDecodedConfig(modelResult.getConfigUID(), modelResult.getConfigType(), modelResult.getTransportCID());
      }

      if (bookmarkConfig != null) {
         bookmarkConfigUID = bookmarkConfig.getUid();
      } else if (bookmark instanceof BrowserPageModel) {
         BrowserConfigRecord initialConfig = BrowserConfigRecord.getDecodedConfig(initialConfigUID, BrowserConfigRecord.INVALID_VALUE, null);
         if (initialConfig != null) {
            Folder folder = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID, ((BrowserPageModel)bookmark).getFolderId());
            bookmarkConfigUID = BookmarksFolderList.getConfigUIDForFolder(initialConfig, folder);
         }
      }

      this._browserChoiceField = new BrowserChoiceField(BrowserResources.getString(819), bookmarkConfigUID);
      if (this._homePage) {
         this._browserChoiceField.setEditable(false);
      }

      localVfm.add(this._browserChoiceField);
      this._buttonAccept = (ButtonField)(new Object(BrowserResources.getString(150), 98304));
      this._buttonCancel = (ButtonField)(new Object(CommonResource.getString(19), 98304));
      this._buttonAccept.setChangeListener(this);
      this._buttonCancel.setChangeListener(this);
      FlowFieldManager fmgr = (FlowFieldManager)(new Object(12884901888L));
      fmgr.add(this._buttonAccept);
      fmgr.add(this._buttonCancel);
      localVfm.add(fmgr);
      this._editURL.setFocus();
      UiApplication.getUiApplication().pushModalScreen(this);
   }

   public final PageModel getEditedNode(ModelResult modelResult, long luid, long timeStamp, long parentFolderID) {
      String urlText = this._editURL.getText();
      String titleText = this._editTitle.getText();
      if (!this._cancelled && urlText.length() != 0 && titleText.length() != 0) {
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

         if (!this._originalURL.equals(urlText)) {
            modelResult = new ModelResult(urlText, 1, null);
            modelResult.setHomePage(this._homePage);
         }

         BrowserConfigRecord browserConfig = this._browserChoiceField.getSelectedBrowser();
         if (browserConfig != null) {
            modelResult.setConfigUID(browserConfig.getUid());
            modelResult.setConfigType(browserConfig.getPropertyAsInt(12));
            modelResult.setTransportCID(browserConfig.getPropertyAsString(3));
         }

         return new BrowserPageModel(
            luid, timeStamp, 4, titleText, modelResult, parentFolderID, this._homePage, this._pageModel.getIconUrl(), updateFlags, updateStart, updatePeriod
         );
      } else {
         return null;
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
            return DialogAddBookmark.validateBookmark(this._editTitle.getText(), this._editURL.getText());
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
   protected final void makeMenu(Menu menu, int context) {
      super.makeMenu(menu, context);
      menu.add(new DialogEditBookmark$1(this, BrowserResources.getString(150), 1, 1));
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (super.keyChar(key, status, time)) {
         return true;
      }

      if (key == '\n' && DialogAddBookmark.validateBookmark(this._editTitle.getText(), this._editURL.getText())) {
         UiApplication.getUiApplication().popScreen(this);
      }

      return false;
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
      } else if (field == this._buttonAccept) {
         if (DialogAddBookmark.validateBookmark(this._editTitle.getText(), this._editURL.getText())) {
            UiApplication.getUiApplication().popScreen(this);
            return;
         }
      } else if (field == this._buttonCancel) {
         this.onClose();
      }
   }
}
