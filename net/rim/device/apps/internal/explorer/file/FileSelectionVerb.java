package net.rim.device.apps.internal.explorer.file;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.file.FileSelectionFilter;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;
import net.rim.device.apps.internal.explorer.file.verbs.SelectFolderVerb;
import net.rim.vm.Array;

public final class FileSelectionVerb extends Verb implements SelectionListener, FocusChangeListener, VerbProvider {
   private Object _selected;
   private ExplorePopup _popupExplorer;
   private Object _context;
   private Verb _selectVerb;
   private Verb _selectFolderVerb;
   private int _allowedSelectionType = 1;
   private static final String FILE_SELECTION_VERB_TEXT;
   private static final int SELECT_FILE;
   private static final int SELECT_FOLDER;

   public FileSelectionVerb(Object ctx) {
      this(ctx, ExplorerResources.getResourceBundleFamily(), 40);
      this._selectFolderVerb = new SelectFolderVerb(this, ExplorerResources.getResourceBundleFamily(), 133);
   }

   public FileSelectionVerb(Object ctx, ResourceBundleFamily rbf, int titleKey) {
      super(0);
      this._context = ContextObject.castOrCreate(ctx);
      this._selectVerb = new SelectFolderVerb(this, rbf, titleKey);
   }

   @Override
   public final Object invoke(Object folderParam) {
      String title = null;
      int selectionAttribs = 0;
      Object obj = ContextObject.get(folderParam, -1002650280265073678L);
      if (!(obj instanceof Object)) {
         selectionAttribs = ContextObject.getIntegerData(folderParam, ContextObject.getIntegerData(this._context, 0));
      } else {
         selectionAttribs = ((FileSelectionFilter)obj).getSelectFilter();
      }

      this._allowedSelectionType = 0;
      if ((selectionAttribs & 1024) != 0) {
         this._allowedSelectionType = 2;
      }

      if ((selectionAttribs & 2048) != 0) {
         this._allowedSelectionType |= 1;
      }

      ContextObject context = (ContextObject)this._context;
      title = (String)context.get(3986845832244503196L);
      if (title == null) {
         if ((this._allowedSelectionType & 1) != 0) {
            title = ExplorerResources.getString(40);
         } else {
            title = ExplorerResources.getString(133);
         }
      }

      if (context.getFlag(39)) {
         context.put(-3185095355580406181L, this._selectVerb);
      }

      this._popupExplorer = new ExplorePopup(this, title, folderParam);
      UiApplication.getUiApplication().pushModalScreen(this._popupExplorer);
      return this._selected;
   }

   @Override
   public final String toString() {
      return "Select File";
   }

   @Override
   public final void focusChanged(Field field, int eventType) {
      FileConnectionHolder selectedItem = ((FolderList)field).getSelectedItem();
      if (selectedItem != null) {
         this.selected(selectedItem.getURL());
      } else {
         this.selected(((ExploreManager)this._popupExplorer.getManager()).getCurrentView().getURL());
      }
   }

   @Override
   public final void selected(Object selected) {
      if (selected instanceof Object) {
         String filename = (String)selected;
         boolean isFolder = filename.endsWith("/");
         if (isFolder && (this._allowedSelectionType & 2) != 0 || !isFolder && (this._allowedSelectionType & 1) != 0) {
            this._selected = selected;
            Application app = this._popupExplorer.getApplication();
            if (app != null) {
               app.invokeAndWait(new FileSelectionVerb$1(this));
            }
         }
      }
   }

   @Override
   public final void selected(Object[] selected) {
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      Object verbSupplier = ContextObject.get(this._context, 424670468422402792L);
      Verb defaultVerb = null;
      if (verbSupplier instanceof Object) {
         defaultVerb = ((VerbProvider)verbSupplier).getVerbs(context, verbs);
      }

      Object obj = ContextObject.get(context, 2765042845091913199L);
      if (obj instanceof Object) {
         String filename = (String)obj;
         obj = ContextObject.get(context, 6420606222376351919L);
         if (obj instanceof AliasFileItemField && ((AliasFileItemField)obj).isExecutable()) {
            AliasFileItemField fileItem = (AliasFileItemField)obj;
            if ((this._allowedSelectionType & 1) != 0 && !fileItem.isSelectable()) {
               return null;
            }
         }

         boolean isFolder = filename.endsWith("/");
         if (isFolder && obj instanceof FileItemField) {
            FileItemField fileItem = (FileItemField)obj;
            if (!fileItem.canWrite()) {
               return null;
            }
         }

         if (!isFolder && (this._allowedSelectionType & 1) != 0) {
            Array.resize(verbs, verbs.length + 1);
            verbs[verbs.length - 1] = this._selectVerb;
            if (ContextObject.getFlag(this._context, 5)) {
               defaultVerb = this._selectVerb;
            }
         }

         if ((this._allowedSelectionType & 2) != 0 && (this._selectFolderVerb != null || isFolder)) {
            Array.resize(verbs, verbs.length + 1);
            verbs[verbs.length - 1] = this._selectFolderVerb == null ? this._selectVerb : this._selectFolderVerb;
         }
      }

      return defaultVerb;
   }
}
