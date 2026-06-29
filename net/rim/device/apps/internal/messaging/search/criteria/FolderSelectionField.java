package net.rim.device.apps.internal.messaging.search.criteria;

import java.util.Enumeration;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.text.TextRect;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.ui.FolderList;
import net.rim.device.apps.api.messaging.ui.SelectFolderVerb;
import net.rim.device.apps.internal.blackberryemail.folder.FolderUtil;
import net.rim.device.apps.internal.messaging.search.resources.SearchResources;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.vm.Array;

public final class FolderSelectionField extends Field {
   private TextRect _label = new TextRect(this);
   private String _folderName;
   private Folder _folder;
   private int _selectedWidth;
   private boolean _isLabelOwnLine;
   private MenuItem _clearFieldItem = new FolderSelectionField$2(
      this, ResourceBundle.getBundle(570792712302434978L, "net.rim.device.apps.internal.resource.Search"), 38, 75900, 10
   );
   private static final int PADDING = 1;
   private static Tag TAG = Tag.create("choice");
   private static Tag TAG_LABEL = Tag.create("label");
   private static String CMIME_CONTENT = "CMIME";
   private static MenuItem _changeOptionItem = new FolderSelectionField$1(CommonResource.getBundle(), 1, 50200, 10);

   public static final FolderList getSelectionList(Folder folder, SelectFolderVerb selectionVerb) {
      FolderList folderList = new FolderList(folder);
      folderList.setContext(new ContextObject(22));
      folderList.setHierarchies(getSearchableHierarchies());
      folderList.setSelectVerb(selectionVerb);
      return folderList;
   }

   private final Folder getDefaultFolder() {
      Folder defaultFolder = null;
      return (defaultFolder = FolderUtil.getActiveFolderHierarchy(CMIME_CONTENT)) == null ? null : defaultFolder.getBaseFolder();
   }

   public FolderSelectionField(String label, String folderName, Folder folder) {
      super(18014398509481984L);
      this.setTag(TAG);
      this._label.setTag(TAG_LABEL);
      this._label.setText(label);
      this._label.setText(label);
      if (folderName != null) {
         this._folderName = folderName;
      } else {
         this._folderName = SearchResources.getString(38);
      }

      this._folder = folder;
      if (this._folder == null) {
         this._folder = this.getDefaultFolder();
      }

      this._isLabelOwnLine = ThemeManager.getActiveTheme().isLabelOnOwnLine();
   }

   public static final Folder[] getSearchableHierarchies() {
      Folder[] result = new Folder[16];
      int count = 0;
      Enumeration enumeration = FolderHierarchies.getFolderHierarchies();
      ContextObject context = new ContextObject(22);

      while (enumeration.hasMoreElements()) {
         Folder f = (Folder)enumeration.nextElement();
         if (f instanceof Folder && f.isVisible(context)) {
            if (count >= result.length) {
               Array.resize(result, count + 16);
            }

            result[count++] = f;
         }
      }

      Array.resize(result, count);
      return result;
   }

   @Override
   protected final void layout(int width, int heigth) {
      this.setExtent(width, this.getPreferredHeight());
      this._label.setPosition(0, 0);
      this._label.layout(width, this.getFont().getHeight());
   }

   @Override
   protected final void paint(Graphics g) {
      int labelWidth = this._label.getWidth();
      this._label.paintSelf(g);
      int x = labelWidth + 1;
      int y = 0;
      int width = this.getWidth() - labelWidth - 1;
      int style = 69;
      if (this._isLabelOwnLine) {
         x = 0;
         y = this.getFont().getHeight();
         width = this.getContentWidth();
         style = 70;
      }

      this._selectedWidth = g.drawText(this._folderName, x, y, style, width);
   }

   @Override
   public final void getFocusRect(XYRect rect) {
      if (this._isLabelOwnLine) {
         int height = this.getFont().getHeight();
         rect.set(0, height, this._selectedWidth, height);
      } else {
         rect.set(this.getWidth() - this._selectedWidth - 1, 0, this._selectedWidth + 1, this.getHeight());
      }
   }

   @Override
   public final int moveFocus(int amount, int status, int time) {
      if ((status & 1) != 0) {
         getSelectionList(this._folder, new LocalSelectFolderVerb(this)).run();
         this.fieldChangeNotify(0);
         this.invalidate();
         return 0;
      } else {
         return amount;
      }
   }

   @Override
   protected final void makeContextMenu(ContextMenu contextMenu) {
      super.makeContextMenu(contextMenu);
      contextMenu.addItem(_changeOptionItem);
      contextMenu.addItem(this._clearFieldItem);
   }

   public final long getFolderId() {
      return this._folder != null && this._folder != this.getDefaultFolder() ? this._folder.getLUID() : 0;
   }

   @Override
   public final void fieldChangeNotify(int context) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   public final void setSelectedFolder(Folder f) {
      this._folder = f;
      if (this._folder != null) {
         this._folderName = this._folder.getFriendlyName();
      } else {
         this._folder = this.getDefaultFolder();
         this._folderName = SearchResources.getString(38);
      }

      this.fieldChangeNotify(0);
      this.invalidate();
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == ' ') {
         getSelectionList(this._folder, new LocalSelectFolderVerb(this)).run();
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected final boolean invokeAction(int action) {
      switch (action) {
         case 1:
            getSelectionList(this._folder, new LocalSelectFolderVerb(this)).run();
            return true;
         default:
            return false;
      }
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      Theme theme = ThemeManager.getActiveTheme();
      this._label.applyTheme();
      this._isLabelOwnLine = theme.isLabelOnOwnLine();
   }

   @Override
   public final int getPreferredWidth() {
      ThemeAttributeSet tas = ThemeManager.getActiveTheme().getAttributeSet(TAG_LABEL);
      Font f = null;
      if (tas != null) {
         f = tas.getFont();
      }

      if (f == null) {
         f = this.getFont();
      }

      int widthLabel = this._label.getText() != null && !this._label.getText().equals("") ? f.getBounds((String)this._label.getText()) : 0;
      int widthFolder = this.getFont().getBounds(this._folderName);
      int width = widthLabel + 1 + widthFolder;
      this._selectedWidth = this.getContentWidth() - widthLabel - 1;
      if (this._isLabelOwnLine) {
         width = Math.max(widthLabel, widthFolder);
         this._selectedWidth = widthFolder;
      }

      return width;
   }

   @Override
   public final int getPreferredHeight() {
      int var10000 = this.getFont().getHeight();
      return this._isLabelOwnLine && this._label.getText() != null && !this._label.getText().equals("") ? var10000 << 1 : var10000 << 0;
   }
}
