package net.rim.device.cldc.impl.sb.editor;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceBookSyncCollection;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.IconCollection;

public final class SBAppContents extends MainScreen implements ListFieldCallback, GlobalEventListener {
   private ServiceBook _sb;
   private ListField _list;
   private int _viewMode;
   private ServiceRecord[][][] _records;
   private int _curViewingId = -1;
   private int _numRecords;
   private IconCollection _icons;
   static final int VIEW_MODE_UNKNOWN;
   static final int VIEW_MODE_RW;
   static final int VIEW_MODE_R;
   static final int VIEW_MODE_RO;
   private static final int[] DISPLAY_TYPES = new int[]{1, 0, 6, -804651005, 10, 11, 12, -805044219, 1718183726, 10, -805044223, 48};
   private static final int NUMBER_OF_FONT_SIZES;
   private static final String PROV_SB_CID;

   public SBAppContents(ServiceBook sb, int viewMode) {
      this._sb = sb;
      this._list = (ListField)(new Object(0));
      this._list.setCallback(this);
      this._list.setEmptyString(SBAppResources.getResourceBundle(), 50, 4);
      this._viewMode = viewMode;
      this._records = new Object[DISPLAY_TYPES.length][][];
      this._icons = IconCollection.get("net_rim_ServiceBook", DISPLAY_TYPES.length);
      this.setTitle((Field)(new Object(SBAppResources.getString(1))));
      this.add(this._list);
   }

   public final void go() {
      UiApplication app = UiApplication.getUiApplication();
      app.pushScreen(this);
      app.addGlobalEventListener(this);
      this.resetList();
   }

   @Override
   public final void close() {
      UiApplication.getUiApplication().removeGlobalEventListener(this);
      super.close();
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      boolean bTemp = false;
      int listIndex = this._list.getSelectedIndex();
      ResourceBundle sbRb = SBAppResources.getResourceBundle();
      ResourceBundle cRb = CommonResource.getBundle();
      if (this._numRecords > 0) {
         menu.add(new SBAppContents$MyMenuItem(this, 11, 14, cRb));
         menu.addSeparator();
      }

      ServiceRecord rec = this.getRecordAtIndex(listIndex);
      if (rec != null) {
         if (rec.getType() == 1) {
            menu.add(new SBAppContents$MyMenuItem(this, 14, 101, sbRb));
            menu.add(new SBAppContents$MyMenuItem(this, 15, 102, sbRb));
            menu.addSeparator();
         } else if (!this.isServiceRecUndeletable(rec)) {
            menu.add(new SBAppContents$MyMenuItem(this, 13, 17, cRb));
            bTemp = true;
         }

         if (this._viewMode == 0) {
            menu.add(new SBAppContents$MyMenuItem(this, 12, 16, cRb));
            bTemp = true;
         }
      }

      if (this._viewMode == 0) {
         menu.add(new SBAppContents$MyMenuItem(this, 10, 13, cRb));
         bTemp = true;
      }

      if (this._sb.getNumRecords(2) != 0) {
         menu.add(new SBAppContents$MyMenuItem(this, 16, 106, sbRb));
         bTemp = true;
      }

      if (bTemp) {
         menu.addSeparator();
      }

      super.makeMenu(menu, instance);
   }

   final boolean isServiceRecUndeletable(ServiceRecord rec) {
      return StringUtilities.strEqualIgnoreCase("PROVISIONING", rec.getCid(), 1701707776);
   }

   @Override
   protected final boolean invokeAction(int action) {
      boolean handled = super.invokeAction(action);
      if (!handled) {
         switch (action) {
            case 1:
               int index = this._list.getSelectedIndex();
               if (index != -1) {
                  this.viewRecord(this.getRecordAtIndex(index), false);
                  return true;
               }
         }
      }

      return handled;
   }

   @Override
   protected final boolean keyCharUnhandled(char key, int status, int time) {
      int index = this._list.getSelectedIndex();
      if (index != -1) {
         if (key == '\n') {
            this.viewRecord(this.getRecordAtIndex(index), false);
            return true;
         }

         if (key == 127) {
            ServiceRecord rec = this.getRecordAtIndex(index);
            if (rec.getType() == 1) {
               this._sb.removeRecord(rec);
               return true;
            }

            this.deleteRecord(rec);
            return true;
         }
      }

      return super.keyCharUnhandled(key, status, time);
   }

   @Override
   protected final boolean openDevelopmentBackdoor(int backdoorCode) {
      if (backdoorCode == 1396852047) {
         this._viewMode = 0;
         return true;
      } else {
         return super.openDevelopmentBackdoor(backdoorCode);
      }
   }

   @Override
   protected final boolean openProductionBackdoor(int backdoorCode) {
      if (backdoorCode == 1396852034) {
         ServiceBookSyncCollection sbSyncCollection = ServiceBookSyncCollection.getInstance();
         if (sbSyncCollection != null) {
            sbSyncCollection.enableLegacyBackupRestore(true, true);
            Dialog.alert("Legacy SB Restore Enabled.");
            return true;
         } else {
            Dialog.alert("SB Unavailable.");
            return true;
         }
      } else if (backdoorCode == 1396851778) {
         ServiceBookSyncCollection sbSyncCollection = ServiceBookSyncCollection.getInstance();
         if (sbSyncCollection != null) {
            sbSyncCollection.enableLegacyBackupRestore(false, true);
            Dialog.alert("Legacy SB Restore Disabled.");
            return true;
         } else {
            Dialog.alert("SB Unavailable.");
            return true;
         }
      } else {
         return super.openProductionBackdoor(backdoorCode);
      }
   }

   private final ServiceRecord getRecordAtIndex(int index) {
      ServiceRecord rec = null;

      for (int i = 0; i < this._records.length; i++) {
         if (index < 0) {
            return rec;
         }

         if (index < this._records[i].length) {
            return this._records[i][index];
         }

         index -= this._records[i].length;
      }

      return rec;
   }

   private final void resetList() {
      int index = this._list.getSelectedIndex();
      this._numRecords = 0;

      for (int i = 0; i < DISPLAY_TYPES.length; i++) {
         this._records[i] = this._sb.findRecordsByType(DISPLAY_TYPES[i]);

         for (int j = 0; j < this._records[i].length; j++) {
            if (this._records[i][j].isInvisible()) {
               Arrays.removeAt(this._records[i], j);
               j--;
            }
         }

         this._numRecords = this._numRecords + this._records[i].length;
      }

      this._list.setSize(this._numRecords);
      if (index < this._numRecords) {
         this._list.setSelectedIndex(index);
      }
   }

   private final void viewRecord(ServiceRecord rec, boolean edit) {
      if (rec != null) {
         this._curViewingId = rec.getId();
         SBAppEditRecordUI editUI = new SBAppEditRecordUI(this._sb, rec, false);
         editUI.go(edit ? 0 : 1, this._viewMode == 0);
      }
   }

   private final void deleteRecord(ServiceRecord rec) {
      if (rec != null) {
         int result = Dialog.ask(2, SBAppResources.getString(10));
         if (result != -1) {
            if (this._sb.getNumRecords(2) != 0) {
               ServiceRecord[] records = this._sb.findRecordsByType(2);

               for (int i = records.length - 1; i >= 0; i--) {
                  this._sb.removeRecord(records[i]);
               }
            }

            if (rec.getType() != 1) {
               rec.setType(2);
               this._sb.commit();
               this.resetList();
            }
         }
      }
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      ServiceRecord rec = this.getRecordAtIndex(index);
      String name = rec.getName();
      String cid = rec.getCid();
      int recType = rec.getType();
      Font baseFont = this.getFont();
      Font font = baseFont;
      if (recType == 1) {
         font = font.derive(1);
         graphics.setFont(font);
      }

      int fontHeight = font.getHeight();
      int iconWidth = this._icons.getWidth(fontHeight + 3, fontHeight);

      for (int i = 0; i < DISPLAY_TYPES.length; i++) {
         if (recType == DISPLAY_TYPES[i]) {
            this._icons.paint(graphics, 0, y, iconWidth, fontHeight, i);
            break;
         }
      }

      graphics.drawText(
         ((StringBuffer)(new Object())).append(name).append(" [").append(cid).append(']').toString(), iconWidth + 1, y, 64, width - (iconWidth + 1)
      );
      graphics.setFont(baseFont);
   }

   @Override
   public final int getPreferredWidth(ListField field) {
      return this.getWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }

   @Override
   public final Object get(ListField listField, int index) {
      ServiceRecord rec = this.getRecordAtIndex(index);
      return rec != null ? rec.getName() : null;
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      UiApplication app = UiApplication.getUiApplication();
      if (app.getActiveScreen() != this && data0 == this._curViewingId) {
         int index = -1;
         if (guid == 2522898683889177438L) {
            index = 14;
         } else if (guid == 8288627527798139133L) {
            index = 15;
         }

         if (index != -1) {
            Dialog.alert(SBAppResources.getString(index));

            for (Screen screen = app.getActiveScreen(); app.getActiveScreen() != this; screen = app.getActiveScreen()) {
               app.popScreen(screen);
            }
         }
      }

      this.resetList();
   }
}
