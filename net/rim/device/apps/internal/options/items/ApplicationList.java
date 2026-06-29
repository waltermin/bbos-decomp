package net.rim.device.apps.internal.options.items;

import java.util.Hashtable;
import net.rim.device.api.collection.util.KeywordIndexerHelper;
import net.rim.device.api.system.CodeModuleGroup;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.ui.KeywordFilteredScreen;
import net.rim.device.apps.api.ui.ProgressIndicator;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class ApplicationList extends AbstractItemList {
   private Hashtable _indents = (Hashtable)(new Object());
   protected static final int INDENT_SIZE = 5;
   private static Object _loadLock = new Object();
   private static boolean _isProcessing = false;

   public ApplicationList(boolean showAll, KeywordFilteredScreen screen) {
      super(showAll, screen);
   }

   @Override
   protected final void paintElement(Object element, Graphics graphics, int y, int width) {
      ApplicationList$ApplicationListItem moduleGroupListItem = (ApplicationList$ApplicationListItem)element;
      CodeModuleGroup moduleGroup = moduleGroupListItem._group;
      boolean usesDefaults = moduleGroupListItem._usesDefaults;
      String name = getDisplayName(moduleGroup);
      String version = moduleGroup.getVersion();
      if (version == null) {
         version = OptionsResources.getString(1428);
      }

      version = ((StringBuffer)(new Object())).append(' ').append(version).toString();
      Integer indentLevel = (Integer)this._indents.get(element);
      int indentation = 5 * (indentLevel != null ? indentLevel : 0);
      Font f = graphics.getFont();
      if (!usesDefaults) {
         graphics.setFont(f.derive(1, f.getHeight()));
      }

      int versionWidth = graphics.getFont().getBounds(version) + indentation;
      int widthDrawn = graphics.drawText(name, indentation, y, 70, width - versionWidth);
      graphics.drawText(version, widthDrawn, y, 5, width - widthDrawn);
      graphics.setFont(f);
   }

   @Override
   protected final String getEmptyString() {
      return OptionsResources.getString(1438);
   }

   final Hashtable getIndentTable() {
      return this._indents;
   }

   @Override
   public final void refresh() {
      ProgressIndicator indicator = (ProgressIndicator)(new Object(4));
      indicator.setProgressRunnable(new ApplicationList$ApplicationListLoader(this));
      indicator.run();
   }

   @Override
   protected final KeywordIndexerHelper getKeywordIndexer() {
      return new ApplicationList$1(this);
   }

   static final String getDisplayName(CodeModuleGroup group) {
      return group.getFriendlyName();
   }
}
