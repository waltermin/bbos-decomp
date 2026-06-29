package net.rim.device.apps.internal.explorer.file.verbs;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.explorer.file.SelectionListener;

public final class SelectFolderVerb extends Verb {
   SelectionListener _selector;

   public SelectFolderVerb(SelectionListener selector, ResourceBundleFamily rbf, int titleKey) {
      super(0, rbf, titleKey);
      this._selector = selector;
   }

   @Override
   public final Object invoke(Object parameter) {
      Object selectedItem = parameter;
      if (parameter instanceof ContextObject) {
         selectedItem = ContextObject.get(parameter, 2765042845091913199L);
         if (super._rbKey == 133 && selectedItem instanceof String) {
            String selectedPath = (String)selectedItem;
            if (!selectedPath.endsWith("/")) {
               int end = selectedPath.lastIndexOf(47, selectedPath.length());
               selectedPath = selectedPath.substring(0, end + 1);
               selectedItem = selectedPath;
            }
         }
      }

      this._selector.selected(selectedItem);
      return selectedItem;
   }
}
