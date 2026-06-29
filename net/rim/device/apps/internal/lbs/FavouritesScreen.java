package net.rim.device.apps.internal.lbs;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.TreeField;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.resources.MessageResources;
import net.rim.device.apps.api.messaging.ui.FolderList;
import net.rim.device.apps.api.messaging.util.SimpleFolder;
import net.rim.device.apps.internal.lbs.resources.LBSResources;
import net.rim.device.internal.ui.IconCollection;

public final class FavouritesScreen extends FolderList {
   private SimpleFolder _root;
   private LocationSyncable _selectedLocation;
   private SimpleFolder _selectedFolder;
   private boolean _showOnlyFolders;
   private Object _favouriteToMove;
   private SimpleFolder _folderBeforeMove;
   int _positionBeforeMove;
   private LBSMenuItem[] _menuItems = new LBSMenuItem[0];
   private static LocationDocumentCollection _locationDocumentCollection = LocationDocumentCollection.getInstance();
   private static boolean _initialized;
   private static IconCollection _icons = IconCollection.get("net_rim_LBS", 5);

   public FavouritesScreen() {
      this(false);
   }

   FavouritesScreen(boolean showOnlyFolders) {
      super(null, LBSResources.getString(77), false, !showOnlyFolders, new FavouritesScreen$FavouritesComparator(null), 1196268651216896L);
      this._showOnlyFolders = showOnlyFolders;
      this.createMenuItems();
      this.setItemIcon(_icons, 3);
      this._root = FavouritesManager.getRootFolder();
      this.setStartFolder(this._root);
      _initialized = false;
      this.run();
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         this.handleDisplayNode();
      }
   }

   private final void handleDisplayNode() {
      super._disp.setFocus();
      _initialized = true;
      int selected = this._showOnlyFolders ? 0 : FavouritesManager.getInstance()._lastSelectedNode;
      if (selected == 0 || selected > super._disp.getNodeCount()) {
         selected = super._disp.getFirstChild(0);
         FavouritesManager.getInstance()._lastSelectedNode = selected;
      }

      super._disp.setCurrentNode(selected);
   }

   @Override
   protected final TreeField makeTreeField() {
      TreeField treeField = new FavouritesScreen$FavouritesTreeField(this, this, 0, super._field);
      treeField.setEmptyString(MessageResources.getString(175), 4);
      treeField.setDefaultExpanded(true);
      return treeField;
   }

   final void add(LBSMenuItem menuItem) {
      Arrays.add(this._menuItems, menuItem);
   }

   final void createMenuItems() {
      int order = 30270;
      this.add(new FavouritesScreen$1(this, 72, order++));
      if (!this._showOnlyFolders) {
         this.add(new FavouritesScreen$2(this, 71, order++));
         this.add(new FavouritesScreen$3(this, 79, order++));
         this.add(new FavouritesScreen$4(this, 307, order++));
      }

      order += 65536;
      this.add(new FavouritesScreen$5(this, 294, order++));
      this.add(new FavouritesScreen$6(this, 305, order++));
      this.add(new FavouritesScreen$7(this, 304, order++));
   }

   @Override
   protected final boolean invokeAction(int action) {
      this.close();
      return true;
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      for (int i = this._menuItems.length - 1; i >= 0; i--) {
         LBSMenuItem menuItem = this._menuItems[i];
         if (menuItem.isVisible()) {
            menu.add(menuItem);
         }
      }

      super.makeMenu(menu, instance);
   }

   private final void moveFavourite() {
      Object item = this.getFocusedItem();
      if (item instanceof LocationSyncable || item instanceof Object && item != this._root) {
         this._favouriteToMove = item;
         this._folderBeforeMove = (SimpleFolder)this.getFolderContainingFocusedItem();
         int currentNode = super._disp.getCurrentNode();
         if (this._favouriteToMove instanceof Object) {
            int folderIDbeforeMove = super._disp.getParent(currentNode);
            this._folderBeforeMove = (SimpleFolder)((SimpleFolder)this._favouriteToMove).getParentFolder();
            if (this._folderBeforeMove == null) {
               this._folderBeforeMove = (SimpleFolder)super._disp.getCookie(folderIDbeforeMove);
               if (this._folderBeforeMove != null) {
                  ((SimpleFolder)this._favouriteToMove).setParentFolder(this._folderBeforeMove);
               }
            }

            for (int child = super._disp.getFirstChild(currentNode); child > -1; child = super._disp.getFirstChild(currentNode)) {
               super._disp.deleteSubtree(child);
            }

            super._disp.setExpanded(currentNode, false);
         }

         this._positionBeforeMove = this.getCurrentBookmarkPositionInFolder();
         super._disp.invalidateNode(currentNode);
      }
   }

   private final void completeMove(boolean cancel) {
      int newPosition = this.getCurrentBookmarkPositionInFolder();
      int currentNode = super._disp.getCurrentNode();
      Folder currentFolder = this.getFolderContainingFocusedItem();
      if (!(this._favouriteToMove instanceof Object)) {
         if (newPosition >= 0) {
            if (!cancel) {
               if (this._folderBeforeMove == currentFolder) {
                  int moveAmount = newPosition - this._positionBeforeMove;
                  this.moveFavouriteUpOrDown(this._favouriteToMove, currentFolder, moveAmount);
               } else {
                  ((WritableSet)this._folderBeforeMove.getContainedItems()).remove(this._favouriteToMove);
                  ((WritableSet)currentFolder.getContainedItems()).add(this._favouriteToMove);
                  int pos = ((ReadableList)currentFolder.getContainedItems()).getIndex(this._favouriteToMove);
                  if (pos > -1) {
                     newPosition -= pos;
                  }

                  this.moveFavouriteUpOrDown(this._favouriteToMove, currentFolder, newPosition);
                  LocationDocumentCollection ldc = LocationDocumentCollection.getInstance();
                  ldc.updateFolderHeirarchy((LocationSyncable)this._favouriteToMove, (SimpleFolder)currentFolder);
               }
            } else {
               this.moveFavouriteUpOrDown(this._favouriteToMove, this._folderBeforeMove, 0);
            }
         }
      } else {
         int folderIDbeforeMove = super._disp.nextNode(0, 0, true);

         while (folderIDbeforeMove > -1 && this._folderBeforeMove != super._disp.getCookie(folderIDbeforeMove)) {
            folderIDbeforeMove = super._disp.nextNode(folderIDbeforeMove, 0, true);
         }

         int nodeToExpand;
         if (cancel) {
            int parentNode = folderIDbeforeMove;
            int nextNode = parentNode;
            super._disp.deleteSubtree(currentNode);
            if (parentNode > 0) {
               super._disp.invalidateNode(parentNode);
            }

            int firstChild = super._disp.getFirstChild(folderIDbeforeMove);
            if (firstChild > 0 && super._disp.getCookie(firstChild) instanceof Object) {
               nextNode = firstChild;

               while (true) {
                  int nextSibling = super._disp.getNextSibling(nextNode);
                  if (nextSibling <= 0 || !(super._disp.getCookie(nextSibling) instanceof Object)) {
                     currentNode = super._disp.addSiblingNode(nextNode, this._favouriteToMove);
                     break;
                  }

                  nextNode = nextSibling;
               }
            } else {
               currentNode = super._disp.addChildNode(nextNode, this._favouriteToMove);
            }

            nodeToExpand = currentNode;
         } else {
            int parentNode = super._disp.getParent(super._disp.getCurrentNode());
            SimpleFolder moveFolder = (SimpleFolder)this._favouriteToMove;
            newPosition = super._disp.getCurrentNode();
            SimpleFolder parent = (SimpleFolder)super._disp.getCookie(parentNode);
            this._folderBeforeMove.removeSubFolder(moveFolder);
            Vector folders = (Vector)(new Object());
            Enumeration e = parent.getSubFolders();

            while (e.hasMoreElements()) {
               SimpleFolder f = (SimpleFolder)e.nextElement();
               folders.addElement(f);
               parent.removeSubFolder(f);
            }

            int count = parentNode;
            boolean folderPlaced = false;

            for (int i = 0; i < folders.size(); i++) {
               if (count == newPosition) {
                  moveFolder.setParentFolder(parent);
                  parent.putFolder(moveFolder);
                  folderPlaced = true;
               }

               SimpleFolder f = (SimpleFolder)folders.elementAt(i);
               f.setParentFolder(parent);
               parent.putFolder(f);
               count++;
            }

            if (!folderPlaced) {
               moveFolder.setParentFolder(parent);
               parent.putFolder(moveFolder);
            }

            nodeToExpand = super._disp.getCurrentNode();
         }

         SimpleFolder folder = (SimpleFolder)super._disp.getCookie(nodeToExpand);
         this.reAddFolders(folder, nodeToExpand);
         super._disp.invalidateNode(nodeToExpand);
         super._disp.setExpanded(nodeToExpand, true);
      }

      this.elementUpdated(currentFolder.getContainedItems(), this._favouriteToMove, this._favouriteToMove);
      TreeField tree = super._disp;

      for (int current = tree.getFirstRoot(); current > 0; current = tree.nextNode(current, 0, true)) {
         if (this._favouriteToMove == tree.getCookie(current)) {
            tree.setCurrentNode(current);
            break;
         }
      }

      this._favouriteToMove = null;
      this._folderBeforeMove = null;
      this._positionBeforeMove = -1;
   }

   private final int reAddFolders(SimpleFolder parent, int node) {
      Enumeration e = parent.getSubFolders();
      int nextNode = node;
      SimpleFolder[] folders = new Object[0];

      while (e.hasMoreElements()) {
         Arrays.add(folders, e.nextElement());
      }

      for (int i = folders.length - 1; i >= 0; i--) {
         nextNode = super._disp.addChildNode(node, folders[i]);
         nextNode = this.reAddFolders(folders[i], nextNode);
      }

      ReadableList list = (ReadableList)parent.getContainedItems();

      for (int i = list.size() - 1; i >= 0; i--) {
         nextNode = super._disp.addChildNode(node, list.getAt(i));
      }

      return nextNode;
   }

   private final void moveFavouriteUpOrDown(Object node, Folder currentFolder, int amount) {
      if (currentFolder != null) {
         Collection collection = currentFolder.getContainedItems();
         synchronized (FolderHierarchies.getLockObject()) {
            WritableSet bookmarksItemsW = (WritableSet)collection;
            ReadableList bookmarksItemsR = (ReadableList)collection;
            int keyToMove = bookmarksItemsR.getIndex(node);
            int numItems = bookmarksItemsR.size();
            int slot = keyToMove + amount;
            if (slot < 0) {
               slot = 0;
            } else if (slot >= numItems) {
               slot = numItems - 1;
            }

            bookmarksItemsW.remove(node);
            Object[] items = new Object[numItems];
            int n = 0;

            for (int i = 0; i < numItems; i++) {
               if (i == slot) {
                  n++;
               }

               if (n < numItems) {
                  items[n] = bookmarksItemsR.getAt(i);
               }

               n++;
            }

            items[slot] = node;
            bookmarksItemsW.removeAll();

            for (int i = 0; i < numItems; i++) {
               bookmarksItemsW.add(items[i]);
            }
         }
      }
   }

   private final int getCurrentBookmarkPositionInFolder() {
      int position = -1;

      for (int currentNode = super._disp.getCurrentNode();
         currentNode > 0 && !(super._disp.getCookie(currentNode) instanceof Object);
         currentNode = super._disp.getPreviousSibling(currentNode)
      ) {
         position++;
      }

      return position;
   }

   private final void deleteFolder() {
      Object item = this.getFocusedItem();
      if (item instanceof Object && item != this._root) {
         SimpleFolder folder = (SimpleFolder)item;
         if (Dialog.ask(3, MessageFormat.format(LBSResources.getString(308), new Object[]{folder.getFriendlyName()}), 4) == 4) {
            FavouritesManager.removeFolder(folder);
            this.removeFolder(folder.getLUID());
            FavouritesManager.getInstance()._lastSelectedNode = 0;
         }
      }
   }

   private final void addFolder() {
      Object item = this.getFocusedItem();
      SimpleFolder parent = null;
      if (!(item instanceof Object)) {
         if (!(item instanceof LocationSyncable)) {
            return;
         }

         parent = (SimpleFolder)this.getFolderContainingFocusedItem();
         if (parent == null) {
            parent = this._root;
         }
      } else {
         parent = (SimpleFolder)item;
      }

      DialogEnterSubFolderName addFolderName = new DialogEnterSubFolderName(parent, "");
      String subFolderName = addFolderName.getName();
      if (subFolderName != null) {
         long luid = -4319711987384773568L | UIDGenerator.getUID();
         SimpleFolder newFolder = new FavouritesSimpleFolder(LBSApplication.UID, luid, subFolderName, parent);
         parent.putFolder(newFolder);
         this.elementAdded(parent.getContainedItems(), newFolder);
         if (!this._showOnlyFolders) {
            this.reset(parent.getContainedItems());
         }

         FavouritesManager.getInstance()._lastSelectedNode = 0;
      }
   }

   private final void renameFolder() {
      Object item = this.getFocusedItem();
      if (item instanceof Object && item != this._root) {
         SimpleFolder folder = (SimpleFolder)item;
         DialogEnterSubFolderName renameFolder = new DialogEnterSubFolderName(folder, folder.getFriendlyName());
         String newName = renameFolder.getName();
         if (newName != null) {
            folder.setFriendlyName(newName);
            this.elementUpdated(null, newName, newName);
            this.invalidate();
            LocationDocumentCollection ldc = LocationDocumentCollection.getInstance();
            ReadableList list = (ReadableList)folder.getContainedItems();

            for (int i = 0; i < list.size(); i++) {
               LocationSyncable loc = (LocationSyncable)list.getAt(i);
               ldc.updateFolderHeirarchy(loc, folder);
            }
         }
      }
   }

   final void delete() {
      this.selectLocation();
      if (this._selectedLocation != null && Dialog.ask(3, LBSResources.getString(80), 4) == 4) {
         Folder folder = this.getFolderContainingFocusedItem();
         _locationDocumentCollection.removeSyncObject(this._selectedLocation);
         this.elementRemoved(folder.getContainedItems(), this._selectedLocation);
         this._selectedLocation = null;
         FavouritesManager.getInstance()._lastSelectedNode = 0;
      }
   }

   @Override
   protected final boolean trackwheelRoll(int amount, int status, int time) {
      if ((status & 1) != 0 && this._favouriteToMove == null && this.getFocusedItem() != null) {
         this.moveFavourite();
      }

      return super.trackwheelRoll(amount, status, time);
   }

   @Override
   public final boolean navigationClick(int status, int time) {
      if (this._favouriteToMove != null) {
         this.completeMove(false);
         return true;
      } else if (this._showOnlyFolders) {
         this.selectFolder();
         this.close();
         return true;
      } else {
         Object item = this.getFocusedItem();
         if (item instanceof LocationSyncable) {
            this.selectLocation();
            this.close();
            return true;
         } else if (item instanceof Object) {
            int node = super._disp.getCurrentNode();
            boolean expanded = !super._disp.getExpanded(node);
            super._disp.setExpanded(node, expanded);
            return true;
         } else {
            return this.trackwheelClick(status, time);
         }
      }
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      this.onMenu(0);
      return true;
   }

   @Override
   protected final boolean keyStatus(int keycode, int time) {
      if (Keypad.key(keycode) == 257 && (Keypad.status(keycode) & 1) == 0 && this._favouriteToMove != null) {
         this.completeMove(false);
         return true;
      } else {
         return super.keyStatus(keycode, time);
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\b':
         case '\u007f':
            if (super._field.getText().equals("")) {
               Object item = this.getFocusedItem();
               if (item instanceof Object && item != this._root) {
                  this.deleteFolder();
                  return true;
               }

               this.delete();
               return true;
            }
         default:
            return super.keyChar(key, status, time);
         case '\n':
            if (this._favouriteToMove == null) {
               this.selectLocation();
               this.selectFolder();
               this.close();
               return true;
            }

            this.completeMove(false);
            return true;
         case '\u001b':
            if (this._favouriteToMove == null) {
               this._selectedLocation = null;
               this._selectedFolder = null;
               this.close();
               return true;
            } else {
               this.completeMove(true);
               return true;
            }
      }
   }

   private final void selectFolder() {
      Object focused = this.getFocusedItem();
      if (focused instanceof Object) {
         this._selectedFolder = (SimpleFolder)focused;
      } else {
         this._selectedFolder = null;
      }
   }

   public final SimpleFolder getSelectedFolder() {
      return this._selectedFolder;
   }

   private final void selectLocation() {
      Object focused = this.getFocusedItem();
      if (focused instanceof LocationSyncable) {
         this._selectedLocation = (LocationSyncable)focused;
         FavouritesManager.getInstance()._lastSelectedNode = super._disp.getCurrentNode();
      } else {
         this._selectedLocation = null;
      }
   }

   public final Location getSelectedLocation() {
      return this._selectedLocation != null ? _locationDocumentCollection.getLocation(this._selectedLocation) : null;
   }
}
