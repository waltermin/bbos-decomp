package net.rim.device.apps.internal.options.items;

import net.rim.device.api.synchronization.SyncCollectionStatistics;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncManagerStatistics;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.ObjectListField;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.LeftRightFieldManager;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class DatabaseStatisticsScreen extends AppsMainScreen implements ListFieldCallback {
   private StringBuffer _sb = (StringBuffer)(new Object());
   private DatabaseStatisticsScreen$StatisticsListItem[] _statsListItems;
   private int _totalSize;

   DatabaseStatisticsScreen() {
      super(0);
      this.setTitle(OptionsResources.getString(1887));
      this.gatherStatistics();
      this.addStatistics();
   }

   private final void gatherStatistics() {
      SyncManagerStatistics statsManager = (SyncManagerStatistics)SyncManager.getInstance();
      if (statsManager != null) {
         SyncCollectionStatistics[] statsCollections = statsManager.getSyncCollectionStatistics();
         int numStatsCollections = statsCollections.length;
         this._statsListItems = new DatabaseStatisticsScreen$StatisticsListItem[numStatsCollections];

         for (int i = 0; i < numStatsCollections; i++) {
            DatabaseStatisticsScreen$StatisticsListItem statsListItem = new DatabaseStatisticsScreen$StatisticsListItem(null);
            statsListItem._name = statsCollections[i].getSyncName();
            statsListItem._count = statsCollections[i].getSyncObjectCount();
            statsListItem._size = statsCollections[i].getSyncCollectionSize();
            this._statsListItems[i] = statsListItem;
            this._totalSize = this._totalSize + statsListItem._size;
         }

         Arrays.sort(this._statsListItems, new DatabaseStatisticsScreen$StatisticsListItemComparator(null));
      }
   }

   private final void addStatistics() {
      this._sb.setLength(0);
      appendSizeInKB(this._totalSize, this._sb);
      LeftRightFieldManager totalSizeRow = (LeftRightFieldManager)(new Object(
         (Field)(new Object(OptionsResources.getString(1950), 64)), (Field)(new Object(this._sb.toString(), 8589934656L))
      ));
      this.add(totalSizeRow);
      this.add((Field)(new Object()));
      ObjectListField statsList = (ObjectListField)(new Object());
      statsList.set(this._statsListItems);
      statsList.setCallback(this);
      this.add(statsList);
   }

   private static final void appendSizeInKB(int size, StringBuffer sb) {
      size = size * 10 >> 10;
      sb.append(size / 10);
      sb.append('.');
      sb.append(size % 10);
      sb.append('K');
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      menu.add(new DatabaseStatisticsScreen$CopyStatisticsVerb(this));
   }

   @Override
   public final void close() {
      this._statsListItems = null;
      this._sb.setLength(0);
      this._sb = null;
      super.close();
   }

   @Override
   protected final boolean invokeAction(int action) {
      boolean handled = super.invokeAction(action);
      if (!handled) {
         switch (action) {
            case 1:
               return true;
         }
      }

      return handled;
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      DatabaseStatisticsScreen$StatisticsListItem statsListItem = this._statsListItems[index];
      this._sb.setLength(0);
      this._sb.append(statsListItem._count);
      this._sb.append('/');
      appendSizeInKB(statsListItem._size, this._sb);
      int textWidth = graphics.drawText(this._sb, 0, this._sb.length(), 0, y, 69, width);
      graphics.drawText(statsListItem._name, 0, y, 70, width - textWidth);
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public final Object get(ListField listField, int index) {
      return this._statsListItems[index];
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }
}
