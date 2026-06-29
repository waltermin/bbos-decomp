package net.rim.device.apps.api.messaging.util;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.DefaultProvider;
import net.rim.device.apps.api.framework.model.FolderProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.messagelist.RangeActionVerb;
import net.rim.device.apps.api.messaging.ui.FolderList;
import net.rim.device.apps.api.ui.CommonResources;

public class FileMessageVerb extends Verb {
   private RIMModel _model;
   private RIMModel[] _selectedItems;
   private ContextObject _fileMessageContext;

   public FileMessageVerb(int ordering) {
      this(ordering, CommonResources.getResourceBundle(), 9097, null);
   }

   public FileMessageVerb(RIMModel model) {
      this(602416, CommonResources.getResourceBundle(), 9097, model);
   }

   public FileMessageVerb(int ordering, ResourceBundleFamily rb, int rbKey, RIMModel model) {
      super(ordering, rb, rbKey);
      this.setParameters(model);
   }

   public void setParameters(RIMModel model) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setParameters(int[] selectedIndices, Object target) {
      this.setParameters(RangeActionVerb.getSelectedItems(selectedIndices, target), target);
   }

   public void setParameters(RIMModel[] selectedItems, Object target) {
      this._selectedItems = selectedItems;
   }

   public void resetParameters() {
      this._selectedItems = null;
      this._model = null;
   }

   private FolderProvider getFirstFolderProvider() {
      if (this._selectedItems == null) {
         return !(this._model instanceof FolderProvider) ? null : (FolderProvider)this._model;
      }

      int numberOfItems = this._selectedItems.length;

      for (int i = 0; i < numberOfItems; i++) {
         try {
            RIMModel model = this._selectedItems[i];
            if (model instanceof FolderProvider) {
               return (FolderProvider)model;
            }
         } finally {
            ;
         }
      }

      return null;
   }

   public boolean canFile() {
      FolderProvider fp = this.getFirstFolderProvider();
      Folder defaultFolder = null;
      if (fp != null) {
         defaultFolder = this.getDefaultFolder(fp);
      }

      return defaultFolder != null ? defaultFolder.canContainItems() : false;
   }

   @Override
   public Object invoke(Object context) {
      this._fileMessageContext = ContextObject.clone(context);
      this._fileMessageContext.setFlag(46);

      try {
         String title = CommonResources.getString(9098);
         if (this._selectedItems != null) {
            this._model = this.getFirstFolderProvider();
         }

         if (this._model != null) {
            Folder folder = this.getDefaultFolder(this._model);
            if (folder != null) {
               FolderList folderList = new FolderList(folder, title, true);
               folderList.setSelectVerb(new FileMessageVerb$FileTheMessageVerb(this));
               folderList.setContext(this._fileMessageContext);
               folderList.run();
               return null;
            }
         }
      } finally {
         return null;
      }

      return null;
   }

   private Folder getDefaultFolder(RIMModel model) {
      FolderProvider fp = (FolderProvider)model;
      Folder folder = FolderHierarchies.getFolder(fp.getFolderId());
      if (fp instanceof DefaultProvider) {
         folder = (Folder)((DefaultProvider)fp).getDefault(folder, this._fileMessageContext);
      }

      return folder;
   }
}
