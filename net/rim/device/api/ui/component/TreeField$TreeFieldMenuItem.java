package net.rim.device.api.ui.component;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.internal.i18n.CommonResource;

class TreeField$TreeFieldMenuItem extends MenuItem {
   private boolean _expand;

   public TreeField$TreeFieldMenuItem(int id, boolean expand) {
      super(CommonResource.getBundle(), id, 65552, 10);
      this._expand = expand;
   }

   @Override
   public void run() {
      TreeField t = (TreeField)this.getTarget();
      int node = t.getCurrentNode();
      t.setExpanded(node, this._expand);
      if (this._expand) {
         t.showDescendants(node);
      }
   }
}
