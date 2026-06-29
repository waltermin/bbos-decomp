package net.rim.device.apps.internal.ribbon.launcher;

import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.ribbon.ApplicationProperties;
import net.rim.device.internal.system.ApplicationManagerInternal;

final class GridApplicationLauncherField extends FlowFieldManager implements ApplicationLauncherField {
   private ApplicationLauncherFieldHelper _helper;
   private boolean _layoutAllowed = true;
   private RibbonIconField _movingField;
   private boolean _movingAppToFolder;
   private int _startingMoveIndex = -1;
   private int _lastMoveFolderIndex = -1;
   private boolean _movingLeft = false;
   private boolean _movingRight = false;
   private Bitmap _backgroundBitmap;
   private XYRect _rect;

   final Bitmap getBackgroundBitmap() {
      return this._backgroundBitmap;
   }

   @Override
   public final void setActiveFolder(InternalApplicationFolder folder) {
      this._helper.setActiveFolder(folder);
   }

   @Override
   public final InternalApplicationFolder getActiveFolder() {
      return this._helper._activeFolder;
   }

   @Override
   public final ApplicationEntry getSelectedApplication() {
      Field field = this.getFieldWithFocus();
      return field != null ? (ApplicationEntry)field.getCookie() : null;
   }

   @Override
   public final ApplicationEntry getMovingApplication() {
      return this._movingField != null ? (ApplicationEntry)this._movingField.getCookie() : null;
   }

   @Override
   public final ApplicationEntry getApplicationByHotKey(char key) {
      return this._helper._hierarchyManager.getApplicationByHotKey(key);
   }

   @Override
   public final synchronized void setFocus(ApplicationEntry application) {
      int fieldCount = this.getFieldCount();
      if (application != null) {
         for (int i = 0; i < fieldCount; i++) {
            Field field = this.getField(i);
            if (field.getCookie() == application) {
               field.setFocus();
               return;
            }
         }

         String uniqueName = application.getPropertiesName();
         if (uniqueName != null) {
            for (int i = 0; i < fieldCount; i++) {
               Field field = this.getField(i);
               Object cookie = field.getCookie();
               if (cookie instanceof ApplicationEntry) {
                  ApplicationEntry entry = (ApplicationEntry)cookie;
                  if (uniqueName.equals(entry.getPropertiesName())) {
                     field.setFocus();
                     return;
                  }
               }
            }
         }
      }

      if (fieldCount > 0) {
         this.resetFocusToTop();
      }
   }

   @Override
   public final synchronized void loadApplications() {
      InternalApplicationFolder activeFolder = this.getActiveFolder();
      if (activeFolder != null) {
         Field newFocus = null;
         this.setLayoutAllowed(false);
         synchronized (activeFolder) {
            ApplicationEntry[] applications = activeFolder.getApplications();
            ApplicationEntry focusedApp = this.getSelectedApplication();
            int fldCount = this.getFieldCount();
            int appCount = applications.length;
            int fldIndex = 0;
            int appIndex = 0;
            int applicationIconWidth = this._helper.getApplicationIconWidth();
            int applicationIconHeight = this._helper.getApplicationIconHeight();
            boolean grabNext = false;
            boolean showHiddenApps = this._helper._showHiddenApps;
            ApplicationManagerInternal appManager = (ApplicationManagerInternal)ApplicationManager.getApplicationManager();
            appManager.resetAppDescriptorOverrides();

            while (fldIndex < fldCount && appIndex < appCount) {
               Field fldField = this.getField(fldIndex);
               ApplicationEntry fldEntry = (ApplicationEntry)fldField.getCookie();
               ApplicationEntry appEntry = applications[appIndex];
               InternalApplicationHierarchy activeHierarchy = HierarchyManager.getInstance().getActiveHierarchy();
               if (activeHierarchy != null) {
                  ApplicationProperties properties = activeHierarchy.getApplicationProperties(appEntry.getPropertiesName());
                  if (properties == null) {
                     properties = activeHierarchy.createDefaultProperties(appEntry.getPropertiesName());
                  }

                  if (properties.getBundleName() != null) {
                     String propertiesName = appEntry.getPropertiesName();
                     int dot = propertiesName.indexOf(".");
                     if (dot != -1) {
                        propertiesName = propertiesName.substring(0, dot);
                     }

                     appManager.updateAppDescriptor(propertiesName, properties.getBundleName(), properties.getResourceId());
                  }
               }

               Field appField = appEntry.getRibbonIcon();
               if (!showHiddenApps && !appEntry.isVisible()) {
                  if (appEntry == focusedApp) {
                     grabNext = true;
                  }

                  appIndex++;
               } else if (fldEntry == appEntry) {
                  if (fldField != appField) {
                     this.delete(fldField);
                     this.insertApplication(appEntry, fldIndex, applicationIconWidth, applicationIconHeight);
                  }

                  fldIndex++;
                  appIndex++;
                  if (grabNext) {
                     newFocus = appEntry.getRibbonIcon();
                     grabNext = false;
                  }
               } else {
                  int i = appIndex + 1;

                  while (i < appCount && (applications[i] != fldEntry || !showHiddenApps && !applications[i].isVisible())) {
                     i++;
                  }

                  if (i == appCount) {
                     this.delete(fldField);
                     fldCount--;
                  }

                  if (fldEntry == focusedApp) {
                     grabNext = true;
                  }

                  boolean reselect = false;
                  Manager appEntryManager = appField.getManager();
                  if (appEntryManager != null) {
                     appEntryManager.delete(appField);
                     if (appEntryManager == this) {
                        fldCount--;
                     }

                     if (focusedApp != null && focusedApp.getRibbonIcon() == appField) {
                        reselect = true;
                     }
                  }

                  if (this.insertApplication(appEntry, fldIndex, applicationIconWidth, applicationIconHeight)) {
                     fldIndex++;
                     fldCount++;
                     if (reselect) {
                        newFocus = appEntry.getRibbonIcon();
                     }

                     if (grabNext && appEntry.isVisible()) {
                        newFocus = appEntry.getRibbonIcon();
                        grabNext = false;
                     }
                  }

                  appIndex++;
               }
            }

            while (fldIndex < fldCount) {
               this.delete(this.getField(fldIndex));
               fldCount--;
            }

            for (; appIndex < appCount; appIndex++) {
               ApplicationEntry application = applications[appIndex];
               if ((showHiddenApps || application.isVisible()) && this.insertApplication(application, fldIndex, applicationIconWidth, applicationIconHeight)) {
                  if (grabNext) {
                     newFocus = application.getRibbonIcon();
                     grabNext = false;
                  }

                  fldIndex++;
               }
            }

            if (activeFolder.getName().length() > 0) {
               InternalApplicationFolder parent = this._helper.getParentFolder();
               if (parent == null) {
                  parent = this._helper._hierarchyManager.getFolder("");
               }

               String launchURL = FolderEntryPointDescriptor.createLaunchURL(parent);
               ApplicationEntry parentEntry = UpFolderApplicationEntry.createUpFolder(launchURL);
               this.insertApplication(parentEntry, 0, applicationIconWidth, applicationIconHeight);
               this._lastMoveFolderIndex = -1;
            }
         }

         this.setLayoutAllowed(true);
         if (newFocus != null) {
            newFocus.setFocus();
         }
      }
   }

   @Override
   public final void clearFolderStack() {
      this._helper.clearFolderStack();
   }

   @Override
   public final boolean isRootFolder() {
      return this._helper.isRootFolder();
   }

   @Override
   public final boolean goToFolder(String folderName) {
      return this._helper.goToFolder(folderName);
   }

   @Override
   public final boolean popFolder() {
      return this._helper.popFolder();
   }

   @Override
   public final boolean hidingIconsAllowed() {
      return this._helper.hidingIconsAllowed();
   }

   @Override
   public final boolean hasHiddenApplications() {
      return this._helper.hasHiddenApplications();
   }

   @Override
   public final void setBackgroundBitmap(Bitmap bitmap) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final void setShowHiddenApps(boolean showHiddenApps) {
      this._helper._showHiddenApps = showHiddenApps;
   }

   @Override
   public final void resetFocusToTop() {
      if (this.getFieldCount() > 0) {
         this.getField(0).setFocus();
      }
   }

   @Override
   public final void deleteAllApplications() {
      this.deleteAll();
   }

   @Override
   public final boolean moveApplicationInProgress() {
      return this._movingField != null;
   }

   @Override
   public final void beginMoveApplication() {
      Field field = this.getFieldWithFocus();
      if (field instanceof RibbonIconField) {
         this._movingField = (RibbonIconField)field;
         this._startingMoveIndex = this._movingField.getIndex();
         this._movingField.setUseMovingFocus(true);
         this._movingAppToFolder = false;
         RibbonIconField.setFocusIconField(this._movingField);
         this.updateMovingApplicationPosition();
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void updateMovingApplicationPosition() {
      Field targetField = this.getFieldWithFocus();
      int targetIndex = targetField.getIndex();
      ApplicationEntry targetEntry = (ApplicationEntry)targetField.getCookie();
      boolean targetIsFolder = targetEntry.getDescriptor() instanceof FolderEntryPointDescriptor;
      int movingIndex = this._movingField.getIndex();
      ApplicationEntry movingEntry = (ApplicationEntry)this._movingField.getCookie();
      boolean movingIsFolder = movingEntry.getDescriptor() instanceof FolderEntryPointDescriptor;
      if (this.getFieldCount() != 1 || !targetIsFolder || !movingIsFolder) {
         boolean lastTargetWasFolder = this._movingAppToFolder;
         InternalApplicationFolder activeFolder = this.getActiveFolder();
         if (!movingIsFolder && this._lastMoveFolderIndex == targetIndex && this._movingRight
            || activeFolder.getName().length() > 0 && targetIndex == 0 && this._movingRight) {
            boolean var12 = false /* VF: Semaphore variable */;

            label152:
            try {
               var12 = true;
               this.getField(targetIndex + 1);
               var12 = false;
            } finally {
               if (var12) {
                  targetIndex++;
                  break label152;
               }
            }
         }

         if ((activeFolder.getName().length() <= 0 || targetIndex != 0 || this._lastMoveFolderIndex != targetIndex)
            && movingIndex != targetIndex
            && (this._lastMoveFolderIndex != targetIndex || this._movingLeft || movingIndex != -1)) {
            if (movingIndex != -1) {
               this.delete(this._movingField);
            }

            if (!movingIsFolder && targetIsFolder && !lastTargetWasFolder
               || activeFolder.getName().length() > 0 && targetIndex == 0 && this._lastMoveFolderIndex != 1) {
               if (movingIndex > targetIndex) {
                  this._lastMoveFolderIndex = targetIndex;
               } else {
                  this._lastMoveFolderIndex = targetIndex - 1;
               }

               if (activeFolder.getName().length() > 0 && targetIndex == 0) {
                  this._lastMoveFolderIndex = -1;
               }

               this._movingAppToFolder = true;
            } else {
               this._movingAppToFolder = false;
               if (this._lastMoveFolderIndex > targetIndex) {
                  targetIndex++;
               }

               this._lastMoveFolderIndex = -1;
               this.insert(this._movingField, targetIndex);
               this._movingField.setFocus();
            }

            this.invalidate();
         }
      }
   }

   @Override
   public final void completeMoveApplication(boolean doMove) {
      if (!doMove) {
         if (this._movingField.getIndex() != -1) {
            this.delete(this._movingField);
         }

         this.insert(this._movingField, this._startingMoveIndex);
         this._movingField.setFocus();
      } else if (this._movingAppToFolder) {
         Field focusedField = this.getFieldWithFocus();
         ApplicationEntry targetEntry = (ApplicationEntry)focusedField.getCookie();
         FolderEntryPointDescriptor folderDescriptor = (FolderEntryPointDescriptor)targetEntry.getDescriptor();
         String folder = folderDescriptor.getDefaultDescription();
         if (FolderEntryPointDescriptor.isUpFolder(targetEntry)) {
            folder = folderDescriptor.getFolderName();
         }

         this._helper._hierarchyManager.moveToFolder((ApplicationEntry)this._movingField.getCookie(), folder);
         this._movingAppToFolder = false;
         InternalApplicationFolder parentFolder = this._helper.getParentFolder();
         if (parentFolder == null) {
            parentFolder = this._helper._hierarchyManager.getFolder("");
         }

         parentFolder.setLastActiveApplication(targetEntry);
         this.setFocus(targetEntry);
      } else {
         Field focusedField = this.getFieldWithFocus();
         int index = focusedField.getIndex();
         if (this._movingField.getIndex() != -1) {
            this.delete(this._movingField);
         }

         this.insert(this._movingField, index);
         this._helper._hierarchyManager.moveApplication((ApplicationEntry)this._movingField.getCookie(), this._startingMoveIndex, index);
         this._movingField.setFocus();
      }

      RibbonIconField.setFocusIconField(null);
      this._movingField.setUseMovingFocus(false);
      this._movingField = null;
      this._startingMoveIndex = -1;
      this.invalidate();
   }

   @Override
   public final ApplicationEntry getApplicationAt(int index) {
      return this.getFieldCount() > 0 ? (ApplicationEntry)this.getField(index).getCookie() : null;
   }

   @Override
   public final void setHotKeysDisabled(boolean disabled) {
   }

   @Override
   public final synchronized void refreshApplication(ApplicationEntry application) {
   }

   @Override
   protected final boolean isScrollCopyable() {
      return false;
   }

   @Override
   protected final boolean incrementalLayout(int index, int added, int deleted) {
      this.sublayout(this.getWidth(), this.getHeight());
      return true;
   }

   @Override
   protected final void sublayout(int width, int height) {
      if (this._layoutAllowed) {
         InternalApplicationFolder activeFolder = this.getActiveFolder();
         if (activeFolder != null) {
            synchronized (activeFolder) {
               ApplicationEntry[] applications = activeFolder.getApplications();
               int appCount = applications.length;
               int appIndex = 0;
               int applicationIconWidth = this._helper.getApplicationIconWidth(width);
               int applicationIconHeight = this._helper.getApplicationIconHeight();
               this.setVerticalQuantization(applicationIconHeight);

               for (; appIndex < appCount; appIndex++) {
                  ApplicationEntry appEntry = applications[appIndex];
                  RibbonIconField iconField = appEntry.getRibbonIcon();
                  if (iconField != null) {
                     iconField.setSize(applicationIconWidth, applicationIconHeight);
                  }
               }
            }
         }
      }

      super.sublayout(width, height);
   }

   @Override
   protected final boolean trackwheelRoll(int dy, int status, int time) {
      this._movingLeft = dy < 0;
      this._movingRight = dy > 0;
      return super.trackwheelRoll(dy, status, time);
   }

   @Override
   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      this._movingLeft = dx < 0;
      this._movingRight = dx > 0;
      return super.navigationMovement(dx, dy, status, time);
   }

   @Override
   protected final void subpaint(Graphics graphics) {
      if (this._backgroundBitmap != null) {
         int xTrans = graphics.getTranslateX();
         int yTrans = graphics.getTranslateY();
         graphics.getAbsoluteClippingRect(this._rect);
         int offset = Display.getHeight() - this.getScreen().getHeight() + this.getExtent().y - (graphics.getBackgroundOffsetY() + this.getVerticalScroll());
         graphics.drawBitmap(
            this._rect.x - xTrans, this._rect.y - yTrans, this._rect.width, this._rect.height, this._backgroundBitmap, this._rect.x, this._rect.y + offset
         );
      }

      if (this.getFieldCount() > 0) {
         super.subpaint(graphics);
      }
   }

   private final void setLayoutAllowed(boolean state) {
      this._layoutAllowed = state;
      if (state && this.isValidLayout()) {
         this.layout(this.getWidth(), this.getHeight());
         this.invalidate();
      }
   }

   private final boolean insertApplication(ApplicationEntry application, int index, int width, int height) {
      try {
         RibbonIconField iconField = application.getRibbonIcon();
         if (iconField != null && iconField.getManager() == null) {
            iconField.setSize(width, height);
            this.insert(iconField, index);
            return true;
         }
      } finally {
         return false;
      }

      return false;
   }

   GridApplicationLauncherField(HierarchyManager hierarchyManager) {
      super(3459063580983296000L);
      this._helper = new ApplicationLauncherFieldHelper(this, hierarchyManager);
      this._rect = (XYRect)(new Object());
      this.setTag(Tag.create("client"));
      this.setId("homescreen");
   }

   @Override
   public final String getAccessibleName() {
      Field field = this.getFieldWithFocus();
      return field != null ? field.getAccessibleName() : null;
   }

   @Override
   public final AccessibleContext getAccessibleSelectionAt(int index) {
      return this.getFieldWithFocus();
   }

   @Override
   public final int getAccessibleChildCount() {
      return this.getFieldCount();
   }
}
