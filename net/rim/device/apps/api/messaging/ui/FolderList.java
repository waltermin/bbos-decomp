package net.rim.device.apps.api.messaging.ui;

import java.util.Enumeration;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.TreeField;
import net.rim.device.api.ui.component.TreeFieldCallback;
import net.rim.device.api.ui.component.TreeFieldCallback2;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.util.BitSet;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.ConditionalVerb;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.resources.MessageResources;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.ExitVerb;
import net.rim.device.apps.api.ui.FolderIcons;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.internal.ui.IconCollection;
import net.rim.device.internal.ui.RichText;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public class FolderList extends AppsMainScreen implements TreeFieldCallback, TreeFieldCallback2, CollectionListener {
   private Object _startItem;
   private Folder _rootFolder;
   private SelectFolderVerb _selectVerb;
   private Verb _deleteVerb;
   private Verb _createVerb;
   private Verb _renameVerb;
   private Comparator _comparator;
   private Folder[] _hierarchies;
   private boolean _omitTopLevelFolders;
   private TreeField _treeField;
   private boolean _initialized;
   private WeakReference _collectionListener;
   private boolean _shouldMakeSelectVerbDefaultInitially;
   private boolean _initiallyOnDefaultFolder = true;
   private String[] _query;
   protected TreeField _disp;
   protected FolderList$SearchFolderField _field;
   private byte[] _nodeIndex;
   private FolderList$SearchEngine _engine;
   private FolderList$SearchThread _searchThread;
   private boolean _startThread;
   private Object _lockObj;
   private boolean _interrupted;
   private int _searchCount;
   private int[] _wordOffsets = new int[64];
   private int[] _queryWordsStartOffsets = new int[64];
   private int[] _queryWordsEndOffsets = new int[64];
   private FolderList$Updater _updater;
   private boolean _keepFocus;
   private boolean _showContainedItems;
   private IconCollection _icons;
   private int _iconIndex;
   private UiApplication _application;
   private int _maxFolderWidth;
   private boolean _inFolderWidthCalculation;
   private FolderList$DataSearchRepositoryImpl _inputMethodSearchDelegate = new FolderList$DataSearchRepositoryImpl(this, null);
   private StringBuffer _getQueryBuffer = (StringBuffer)(new Object());
   private static final long DEFAULT_STYLE;
   private static final int FL_REFLAST_LAST_IDX;
   private static final int FL_REFLAST_CURR_IDX;
   private static final int FL_REFLAST_SIZE;
   private static final byte UNMARK;
   private static final byte MARK;
   private static final byte EXT_MARK;
   private static final int HORIZONTAL_SCROLL_INCR = Display.getWidth() >> 2;

   public BasicEditField getSearchField() {
      return this._field;
   }

   public void setStartFolder(Folder startFolder) {
      this._startItem = startFolder;
      this._rootFolder = startFolder.getBaseFolder();
   }

   public void setItemIcon(IconCollection icons, int iconIndex) {
      this._iconIndex = iconIndex;
      this._icons = icons;
   }

   public String getLabel(Object cookie) {
      if (!(cookie instanceof Folder)) {
         return cookie != null ? cookie.toString() : null;
      } else {
         return ((Folder)cookie).getFriendlyName();
      }
   }

   public void removeFolder(long folderLUID) {
      if (this._application.isEventThread()) {
         this.handleRemoveFolder(folderLUID);
      } else {
         this._application.invokeLater(new FolderList$5(this, folderLUID));
      }
   }

   public void setHierarchies(Folder[] hierarchies) {
      this.setHierarchies(hierarchies, false);
   }

   public void setHierarchies(Folder[] hierarchies, boolean omitTopLevelFolders) {
      this._hierarchies = hierarchies;
      this._omitTopLevelFolders = true;
   }

   public void setSelectVerb(SelectFolderVerb selectVerb) {
      if (selectVerb != null) {
         selectVerb.clearSelection();
      }

      this._selectVerb = selectVerb;
   }

   public void setDeleteVerb(Verb deleteVerb) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setCreateVerb(Verb createVerb) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setRenameVerb(Verb renameVerb) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public Object getFocusedItem() {
      int node = this._disp.getCurrentNode();
      return node != -1 ? this._disp.getCookie(node) : null;
   }

   public Folder getFocusedFolder() {
      Object cookie = this.getFocusedItem();
      return !(cookie instanceof Folder) ? null : (Folder)cookie;
   }

   public Folder getFolderContainingFocusedItem() {
      int node = this._disp.getCurrentNode();
      if (node != -1) {
         do {
            Object cookie = this._disp.getCookie(node);
            if (cookie instanceof Folder) {
               return (Folder)cookie;
            }

            node = this._disp.getParent(node);
         } while (node != 0);
      }

      return null;
   }

   protected TreeField makeTreeField() {
      TreeField treeField = new FolderList$CustomTreeField(this, 0, this._field);
      treeField.setEmptyString(MessageResources.getString(175), 4);
      treeField.setDefaultExpanded(false);
      return treeField;
   }

   public void run() {
      this.initialize((Folder)this._startItem);
      this._nodeIndex = new byte[this._treeField.getNodeCount() + 1];
      synchronized (this._lockObj) {
         for (int c = this._nodeIndex.length - 1; c >= 0; c--) {
            this._nodeIndex[c] = 2;
         }

         this.constructTree();
         this.updateFocus();
      }

      this._searchThread.start();
      this._application.pushModalScreen(this);
      this._searchThread.stop();
   }

   protected boolean isVisible(Folder folder) {
      return folder.isVisible(this.getContext());
   }

   protected void cancel() {
      this._application.popScreen(this);
   }

   protected String getQuery() {
      for (int i = 0; i < this._query.length; i++) {
         if (this._getQueryBuffer.length() != 0) {
            this._getQueryBuffer.append('\n');
         }

         this._getQueryBuffer.append(this._query[i]);
      }

      String result = this._getQueryBuffer.toString();
      this._getQueryBuffer.setLength(0);
      return result;
   }

   protected void runSearch(String newPattern) {
      throw new RuntimeException("cod2jar: invokevirtual: receiver not in world");
   }

   public void clearTree() {
      for (int root = this._disp.getFirstRoot(); root > 0; root = this._disp.getFirstRoot()) {
         this._disp.deleteSubtree(root);
      }

      this._disp.setDefaultExpanded(false);
   }

   protected int addNode(TreeField treeField, Collection collection, Object nodeCookie) {
      synchronized (FolderHierarchies.getLockObject()) {
         synchronized (this._lockObj) {
            for (int current = treeField.getFirstRoot(); current > 0; current = treeField.nextNode(current, 0, true)) {
               Object currentNode = treeField.getCookie(current);
               if (currentNode instanceof Folder) {
                  Folder folder = (Folder)currentNode;
                  if (folder.getContainedItems() == collection) {
                     return this.insert(treeField, nodeCookie, current, -1);
                  }
               }
            }

            if (this._omitTopLevelFolders && this._hierarchies != null) {
               for (int i = this._hierarchies.length - 1; i >= 0; i--) {
                  Folder folder = this._hierarchies[i];
                  if (folder != null && folder.getContainedItems() == collection) {
                     return this.insert(treeField, nodeCookie, 0, -1);
                  }
               }
            }
         }

         return -1;
      }
   }

   protected void treeConstructed() {
   }

   protected void updateFocusWithoutQuery() {
      int current = this._disp.getFirstRoot();

      for (this._keepFocus = false; current > 0; current = this._disp.nextNode(current, 0, true)) {
         if (this._startItem == this._disp.getCookie(current)) {
            expandToFolder(this._disp, current);
            this._disp.setCurrentNode(current);
            return;
         }
      }

      current = this._disp.getFirstRoot();
      if (current > 0) {
         expandToFolder(this._disp, current);
         this._disp.setCurrentNode(current);
      }
   }

   protected void focusStartItemIfVisible() {
      int current = this._disp.getFirstRoot();

      for (this._keepFocus = false; current > 0; current = this._disp.nextNode(current, 0, true)) {
         if (this._startItem == this._disp.getCookie(current)) {
            if (this._disp.getVisible(current)) {
               this._disp.setCurrentNode(current);
            }

            return;
         }
      }

      current = this._disp.getFirstRoot();
      if (current > 0) {
         this._disp.setCurrentNode(current);
      }
   }

   protected void updateFocusWithQuery() {
      int current = this._disp.getFirstRoot();
      String[] queryWords = this._query;
      int firstMatch = -1;
      int focusMatch = -1;
      if (this._keepFocus) {
         for (this._keepFocus = false; current > 0; current = this._disp.nextNode(current, 0, true)) {
            Object currentObj = this._disp.getCookie(current);
            String label = this.getLabel(currentObj);
            if (this.match(label, queryWords, null)) {
               expandToFolder(this._disp, current);
               if (firstMatch == -1) {
                  firstMatch = current;
               }
            }

            if (this._startItem == currentObj) {
               if (!this._disp.getVisible(current)) {
                  expandToFolder(this._disp, current);
               }

               if (focusMatch == -1) {
                  focusMatch = current;
               }
            }
         }

         if (focusMatch > 0) {
            this._disp.setCurrentNode(focusMatch);
            return;
         }

         if (firstMatch > 0) {
            this._disp.setCurrentNode(firstMatch);
            return;
         }
      } else {
         for (; current > 0; current = this._disp.nextNode(current, 0, true)) {
            String label = this.getLabel(this._disp.getCookie(current));
            if (this.match(label, queryWords, null)) {
               expandToFolder(this._disp, current);
               if (firstMatch == -1) {
                  firstMatch = current;
               }
            }
         }

         if (firstMatch > 0) {
            this._disp.setCurrentNode(firstMatch);
         }
      }
   }

   @Override
   public void reset(Collection collection) {
      if (this._application.isEventThread()) {
         this.handleReset(collection);
      } else {
         this._application.invokeLater(new FolderList$1(this, collection));
      }
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
      if (this._application.isEventThread()) {
         this.handleElementAdded(collection, element);
      } else {
         this._application.invokeLater(new FolderList$2(this, collection, element));
      }
   }

   @Override
   public void drawTreeItem(TreeField treeField, Graphics graphics, int node, int y, int width, int indent) {
      int x = 0;
      Object item = treeField.getCookie(node);
      IconCollection icons = null;
      int iconIndex = 0;
      if (item instanceof Folder) {
         icons = FolderIcons.ICONS;
         iconIndex = 0;
      } else if (item != null && this._icons != null) {
         icons = this._icons;
         iconIndex = this._iconIndex;
      }

      String label = this.getLabel(item);
      if (label != null) {
         y = treeField.getAdjustedY(this.getFont(), label, y);
      }

      if (icons != null) {
         int height = treeField.getRowHeight();
         int iconWidth = icons.getWidth(width, height);
         icons.paint(graphics, x + indent, y, iconWidth, height, iconIndex);
         x += iconWidth + 2;
      }

      if (label != null) {
         if (this._inFolderWidthCalculation) {
            int len = this.getFont().getBounds(label) + indent + x;
            if (len > this._maxFolderWidth) {
               this._maxFolderWidth = len;
            }
         }

         RichText.drawTextWithEllipses(
            graphics,
            label,
            x + indent,
            y,
            Display.getWidth() - x - indent + treeField.getManager().getHorizontalScroll(),
            RichText.getParagraphOrdering(this.getStyle()),
            0
         );
      }
   }

   @Override
   public void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (this._application.isEventThread()) {
         this.handleElementUpdated(collection, oldElement, newElement);
      } else {
         this._application.invokeLater(new FolderList$3(this, collection, oldElement, newElement));
      }
   }

   @Override
   public void elementRemoved(Collection collection, Object element) {
      if (this._application.isEventThread()) {
         this.handleElementRemoved(collection, element);
      } else {
         this._application.invokeLater(new FolderList$4(this, collection, element));
      }
   }

   @Override
   public int getWidth(TreeField treeField, int node) {
      int x = 0;
      int size = this.getFont().getHeight();
      Object cookie = treeField.getCookie(node);
      if (cookie instanceof Folder) {
         x += FolderIcons.ICONS.getWidth(size, size) + 2;
      } else if (cookie != null && this._icons != null) {
         x += this._icons.getWidth(size, size) + 2;
      }

      String label = this.getLabel(cookie);
      if (label != null) {
         x += treeField.getFont().getBounds(label);
      }

      return x;
   }

   public FolderList(Folder startFolder, String title, boolean shouldMakeSelectVerbDefaultInitially) {
      this(startFolder, title, shouldMakeSelectVerbDefaultInitially, false, null, 1196268651020288L);
   }

   private void setQuery(String newPattern) {
      if (newPattern != null && newPattern.length() != 0) {
         Array.resize(this._query, 1);
         this._query[0] = newPattern;
      } else {
         this.resetQuery();
      }
   }

   public FolderList(
      Folder startFolder, String title, boolean shouldMakeSelectVerbDefaultInitially, boolean showContainedItems, Comparator comparator, long style
   ) {
      super(style);
      this._application = (UiApplication)Application.getApplication();
      this.setVerticalQuantization(-1);
      this.setDefaultClose(false);
      this._lockObj = new Object();
      this._engine = new FolderList$SearchEngine(this);
      this._searchThread = new FolderList$SearchThread(this);
      this._updater = new FolderList$Updater(this);
      if (startFolder != null) {
         this.setStartFolder(startFolder);
      }

      this._collectionListener = (WeakReference)(new Object(this));
      this._query = new Object[0];
      this._field = new FolderList$SearchFolderField(this, ((StringBuffer)(new Object())).append(title).append(": ").toString());
      this.setTitle(this._field);
      this._treeField = new FolderList$CustomTreeField(this, 0, this._field);
      this._treeField.setDefaultExpanded(false);
      this.setContext((ContextObject)(new Object()));
      this._shouldMakeSelectVerbDefaultInitially = shouldMakeSelectVerbDefaultInitially;
      this._showContainedItems = showContainedItems;
      this._comparator = comparator;
   }

   @Override
   public int processKeyEvent(int event, char key, int keycode, int time) {
      int res;
      if (!this.isStyle(1125899906842624L)) {
         res = this._field.processKeyEvent(event, key, keycode, time);
      } else {
         Manager scrollManager = this._disp.getManager();
         int currentHorizontalScroll = scrollManager.getHorizontalScroll();
         if (Keypad.map(keycode) == 131) {
            if (event == 513 && currentHorizontalScroll > 0) {
               if (currentHorizontalScroll - HORIZONTAL_SCROLL_INCR < 0) {
                  currentHorizontalScroll = HORIZONTAL_SCROLL_INCR;
               }

               scrollManager.setHorizontalScroll(currentHorizontalScroll - HORIZONTAL_SCROLL_INCR);
               scrollManager.invalidate();
            }

            res = 65536;
         } else if (Keypad.map(keycode) == 132) {
            if (event == 513) {
               if (this._maxFolderWidth == 0) {
                  this._inFolderWidthCalculation = true;
                  this.paint(this.getGraphics());
                  this._inFolderWidthCalculation = false;
               }

               if (this._maxFolderWidth - currentHorizontalScroll > Display.getWidth()) {
                  if (this._maxFolderWidth - currentHorizontalScroll - HORIZONTAL_SCROLL_INCR < Display.getWidth()) {
                     currentHorizontalScroll = this._maxFolderWidth - Display.getWidth() - HORIZONTAL_SCROLL_INCR;
                  }

                  scrollManager.setHorizontalScroll(currentHorizontalScroll + HORIZONTAL_SCROLL_INCR);
                  scrollManager.invalidate();
               }
            }

            res = 65536;
         } else {
            this._maxFolderWidth = 0;
            if (currentHorizontalScroll > 0) {
               scrollManager.setHorizontalScroll(0);
               scrollManager.invalidate();
            }

            res = this._field.processKeyEvent(event, key, keycode, time);
         }
      }

      if ((res & 65536) == 0) {
         res = super.processKeyEvent(event, key, keycode, time);
      }

      return res;
   }

   private void resetQuery() {
      Array.resize(this._query, 0);
   }

   private boolean canSelectFolder(Folder folder) {
      if (folder == null || this._selectVerb == null || !hasParent(folder)) {
         return false;
      }

      if (!(this._selectVerb instanceof Object)) {
         return true;
      }

      ConditionalVerb conditionalVerb = (ConditionalVerb)this._selectVerb;
      ContextObject context = (ContextObject)(new Object());
      context.put(-1219344331000926502L, folder);
      return conditionalVerb.canInvoke(context);
   }

   private void selectFolder(Folder folder) {
      ContextObject context = this.getContext();
      context.put(-1219344331000926502L, folder);
      this._selectVerb.invoke(context);
      this._field.setText("");
      this.resetQuery();
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      Folder f = this.getFocusedFolder();
      synchronized (this._lockObj) {
         switch (key) {
            case '\b':
               if (this._query.length > 0) {
                  this._field.keyChar(key, status, time);
                  break;
               }
            case '\u007f':
               if (this._deleteVerb != null && hasParent(f) && f != this._rootFolder) {
                  this._deleteVerb.invoke(this.getFocusedFolder());
                  return true;
               }
               break;
            case '\n':
               if (this.canSelectFolder(f)) {
                  this.selectFolder(f);
               }

               return true;
            case '\u001b':
               if (this._query.length <= 0) {
                  this.cancel();
                  return true;
               }

               this._field.setText("");
               this.resetQuery();
               this._engine.startSearch();
               break;
            case ' ':
               if (this._query.length == 0) {
                  return super.keyChar(key, status, time);
               } else {
                  this._field.keyChar(key, status, time);
                  return true;
               }
            default:
               if (key == 17) {
                  return false;
               }

               this._field.keyChar(key, status, time);
         }

         return true;
      }
   }

   @Override
   protected boolean keyControl(char ch, int status, int time) {
      return super.keyControl(ch, status, time) ? true : this._field.keyControl(ch, status, time);
   }

   private void initialize(Folder startFolder) {
      if (!this._initialized) {
         this._initialized = true;
         this._disp = this.makeTreeField();
         if (this.isStyle(1125899906842624L)) {
            HorizontalFieldManager horizontalScroller = (HorizontalFieldManager)(new Object(1125899906842624L));
            horizontalScroller.add(this._disp);
            this.add(horizontalScroller);
         } else {
            this.add(this._disp);
         }

         if (this._hierarchies == null) {
            while (true) {
               Folder parent = startFolder.getParentFolder();
               if (parent == null) {
                  this.initialize(startFolder, 0, false);
                  return;
               }

               startFolder = parent;
            }
         } else {
            Folder[] hierarchies = this._hierarchies;
            int num = hierarchies.length;

            for (int i = 0; i < num; i++) {
               this.initialize(hierarchies[i], 0, this._omitTopLevelFolders);
            }
         }
      }
   }

   static boolean hasParent(Folder f) {
      return f != null && f.getParentFolder() != null;
   }

   private void constructTree() {
      this.constructSubTree(0, 0, false);
      this.treeConstructed();
   }

   private boolean initialize(Folder folder, int onto, boolean justAddChildren) {
      int[] refLast = new int[]{-1, -1};
      boolean ret = this.initialize(folder, onto, refLast, justAddChildren);
      int currNode = refLast[1];
      if (currNode > 0) {
         expandToFolder(this._treeField, currNode);
         this._treeField.setCurrentNode(currNode);
      }

      return ret;
   }

   private int constructSubTree(int subtreeNodeId, int anchorNodeId, boolean addSubtreeAsSibling) {
      if (this._interrupted) {
         return -1;
      }

      int newSubtreeNodeId = -1;
      if (this._nodeIndex[subtreeNodeId] == 2) {
         if (subtreeNodeId == 0) {
            newSubtreeNodeId = 0;
         } else if (addSubtreeAsSibling) {
            newSubtreeNodeId = this._disp.addSiblingNode(anchorNodeId, this._treeField.getCookie(subtreeNodeId));
         } else {
            newSubtreeNodeId = this._disp.addChildNode(anchorNodeId, this._treeField.getCookie(subtreeNodeId));
         }

         int childNodeId = this._treeField.getFirstChild(subtreeNodeId);
         if (childNodeId != -1) {
            subtreeNodeId = newSubtreeNodeId;
            addSubtreeAsSibling = false;

            int siblingNodeId;
            do {
               siblingNodeId = this._treeField.getNextSibling(childNodeId);
               int addedSubtreeNodeId = this.constructSubTree(childNodeId, subtreeNodeId, addSubtreeAsSibling);
               if (addedSubtreeNodeId != -1) {
                  subtreeNodeId = addedSubtreeNodeId;
                  addSubtreeAsSibling = true;
               }

               childNodeId = siblingNodeId;
            } while (siblingNodeId != -1);
         } else {
            this._disp.setExpanded(newSubtreeNodeId, true);
         }
      }

      return newSubtreeNodeId;
   }

   private boolean initialize(Folder folder, int onto, int[] refLast, boolean justAddChildren) {
      boolean expand = false;
      if (!this.isVisible(folder)) {
         return false;
      }

      if (!justAddChildren) {
         onto = this.insert(this._treeField, folder, onto, refLast[0]);
      }

      if (this._showContainedItems) {
         this.addContainedItems(this._treeField, folder, onto, true);
      }

      if (folder == this._startItem) {
         refLast[1] = onto;
         expand = true;
         if (this._showContainedItems) {
            int firstChild = this._treeField.getFirstChild(onto);
            if (firstChild >= 0) {
               this._startItem = this._treeField.getCookie(firstChild);
            }
         }
      }

      if (folder.containsSubFolders()) {
         Enumeration enumeration = folder.getSubFolders();
         refLast[0] = -1;

         while (enumeration.hasMoreElements()) {
            if (this.initialize((Folder)enumeration.nextElement(), onto, refLast, false)) {
               expand = true;
            }
         }
      }

      refLast[0] = onto;
      return expand;
   }

   public FolderList(Folder startFolder, String title) {
      this(startFolder, title, false);
   }

   private int insert(TreeField treeField, Object object, int onto, int last) {
      int child = treeField.getFirstChild(onto);
      if (child == -1) {
         return treeField.addChildNode(onto, object);
      }

      if (last != -1 && this.compare(object, treeField.getCookie(last)) >= 0) {
         child = last;
      } else if (this.compare(object, treeField.getCookie(child)) <= 0) {
         return treeField.addChildNode(onto, object);
      }

      while (true) {
         int next = treeField.getNextSibling(child);
         if (next == -1 || this.compare(object, treeField.getCookie(next)) <= 0) {
            return treeField.addSiblingNode(child, object);
         }

         child = next;
      }
   }

   private void updateFocus() {
      synchronized (this._lockObj) {
         if (this._query.length == 0) {
            this.updateFocusWithoutQuery();
         } else {
            this.updateFocusWithQuery();
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private boolean match(String src, String[] keyWords, BitSet keyWordsMatchSet) {
      if (keyWords.length == 0) {
         return true;
      }

      boolean result = false;
      int wordCount = 0;
      boolean var8 = false /* VF: Semaphore variable */;

      label45:
      try {
         var8 = true;
         wordCount = StringUtilities.stringToWords(src, this._wordOffsets, 0);
         var8 = false;
      } finally {
         if (var8) {
            Array.resize(this._wordOffsets, (src.length() >> 1) + 1);
            wordCount = StringUtilities.stringToWords(src, this._wordOffsets, 0);
            break label45;
         }
      }

      for (int i = keyWords.length - 1; i >= 0; i--) {
         if (this.match(src, wordCount, this._wordOffsets, keyWords[i])) {
            result = true;
            if (keyWordsMatchSet != null) {
               keyWordsMatchSet.set(i);
            }
         }
      }

      return result;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private boolean match(String src, int srcWordsCount, int[] srcWordsOffsets, String queryWords) {
      int queryWordsCount = 0;
      boolean var9 = false /* VF: Semaphore variable */;

      label60:
      try {
         var9 = true;
         queryWordsCount = StringUtilities.stringToWordsOrKeywords(queryWords, this._queryWordsStartOffsets, this._queryWordsEndOffsets, 0, false);
         var9 = false;
      } finally {
         if (var9) {
            Array.resize(this._queryWordsStartOffsets, (queryWords.length() >> 1) + 1);
            Array.resize(this._queryWordsEndOffsets, (queryWords.length() >> 1) + 1);
            queryWordsCount = StringUtilities.stringToWordsOrKeywords(queryWords, this._queryWordsStartOffsets, this._queryWordsEndOffsets, 0, false);
            break label60;
         }
      }

      for (int i = queryWordsCount - 1; i >= 0; i--) {
         int j = srcWordsCount - 1;

         while (
            j >= 0
               && !startsWithIgnoreCase(
                  src, srcWordsOffsets[j], queryWords, this._queryWordsStartOffsets[i], this._queryWordsEndOffsets[i] - this._queryWordsStartOffsets[i]
               )
         ) {
            j--;
         }

         if (j < 0) {
            return false;
         }
      }

      return true;
   }

   private static boolean startsWithIgnoreCase(String string, int index, String prefix, int offsetInPrefix, int prefixLength) {
      if (string != null && prefix != null) {
         if (string.length() - index < prefixLength) {
            return false;
         }

         for (int i = 0; i < prefixLength; i++) {
            char c1 = string.charAt(index + i);
            char c2 = prefix.charAt(offsetInPrefix + i);
            if (c1 != c2) {
               c1 = Character.toUpperCase(c1);
               c2 = Character.toUpperCase(c2);
               if (c1 != c2) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private void addContainedItems(TreeField treeField, Folder folder, int onto, boolean addListener) {
      Collection collection = folder.getContainedItems();
      if (collection instanceof Object) {
         ReadableList list = (ReadableList)collection;

         for (int i = list.size() - 1; i >= 0; i--) {
            int newNode = this.insert(treeField, list.getAt(i), onto, -1);
            if (newNode >= 0 && this._nodeIndex != null && treeField == this._treeField) {
               if (newNode >= this._nodeIndex.length) {
                  Array.resize(this._nodeIndex, newNode + 1);
               }

               this._nodeIndex[newNode] = 2;
            }
         }

         if (addListener && collection instanceof Object) {
            ((CollectionEventSource)collection).addCollectionListener(this._collectionListener);
         }
      }
   }

   private void handleReset(Collection collection) {
      synchronized (FolderHierarchies.getLockObject()) {
         synchronized (this._lockObj) {
            this.removeContainedItems(this._treeField, collection);
            this.removeContainedItems(this._disp, collection);
            if (collection instanceof Object) {
               ReadableList list = (ReadableList)collection;

               for (int i = list.size() - 1; i >= 0; i--) {
                  this.elementAdded(collection, list.getAt(i));
               }
            }

            if (this._query.length > 0) {
               this._keepFocus = true;
               this._searchThread.interruptSearch();
               this._startThread = true;
               this._searchThread.wakeup();
            }
         }
      }
   }

   private void removeContainedItems(TreeField treeField, Collection collection) {
      for (int current = treeField.getFirstRoot(); current > 0; current = treeField.nextNode(current, 0, true)) {
         Object currentNode = treeField.getCookie(current);
         if (currentNode instanceof Folder) {
            Folder folder = (Folder)currentNode;
            if (folder.getContainedItems() == collection) {
               deleteNonFolderChildren(treeField, current);
               return;
            }
         }
      }

      if (this._omitTopLevelFolders && this._hierarchies != null) {
         for (int i = this._hierarchies.length - 1; i >= 0; i--) {
            Folder folder = this._hierarchies[i];
            if (folder != null && folder.getContainedItems() == collection) {
               deleteNonFolderChildren(treeField, 0);
               return;
            }
         }
      }
   }

   private static void deleteNonFolderChildren(TreeField treeField, int node) {
      if (node > 0) {
         node = treeField.getFirstChild(node);
      } else if (node == 0) {
         node = treeField.getFirstRoot();
      }

      while (node > 0) {
         int next = treeField.getNextSibling(node);
         if (!(treeField.getCookie(node) instanceof Folder)) {
            treeField.deleteSubtree(node);
         }

         node = next;
      }
   }

   private int compare(Object obj1, Object obj2) {
      if (this._comparator != null) {
         return this._comparator.compare(obj1, obj2);
      } else {
         return obj1 instanceof Folder && obj2 instanceof Folder
            ? StringUtilities.compareToIgnoreCase(((Folder)obj1).getFriendlyName(), ((Folder)obj2).getFriendlyName())
            : -1;
      }
   }

   private void handleElementAdded(Collection collection, Object element) {
      synchronized (FolderHierarchies.getLockObject()) {
         synchronized (this._lockObj) {
            int newNode = this.addNode(this._treeField, collection, element);
            if (newNode >= 0) {
               if (this._nodeIndex != null) {
                  if (newNode >= this._nodeIndex.length) {
                     Array.resize(this._nodeIndex, newNode + 1);
                  }

                  this._nodeIndex[newNode] = 2;
               }

               int newDisplayNode = this.addNode(this._disp, collection, element);
               if (element instanceof Folder && this._showContainedItems) {
                  this.addContainedItems(this._treeField, (Folder)element, newNode, true);
                  this.addContainedItems(this._disp, (Folder)element, newDisplayNode, false);
               }

               this._startItem = element;
               if (this._query.length > 0) {
                  this._keepFocus = true;
                  this._searchThread.interruptSearch();
                  this._startThread = true;
                  this._searchThread.wakeup();
               } else if (newDisplayNode >= 0) {
                  expandToFolder(this._disp, newDisplayNode);
                  this._disp.setCurrentNode(newDisplayNode);
               }
            }
         }
      }
   }

   private static void expandToFolder(TreeField treeField, int currNode) {
      if (currNode != 0) {
         int parent = treeField.getParent(currNode);
         if (parent != 0) {
            expandToFolder(treeField, parent);
         }

         treeField.setExpanded(currNode, true);
      }
   }

   @Override
   protected boolean invokeAction(int action) {
      boolean handled = super.invokeAction(action);
      if (!handled) {
         switch (action) {
            case 1:
               int id = this._disp.getCurrentNode();
               Folder focusedFolder = this.getFocusedFolder();
               if (focusedFolder.containsSubFolders()) {
                  if (this._selectVerb != null && hasParent(focusedFolder)) {
                     return false;
                  }

                  this._disp.setExpanded(id, !this._disp.getExpanded(id));
                  this.invalidate();
                  return true;
               }

               if (this.canSelectFolder(focusedFolder)) {
                  this.selectFolder(focusedFolder);
               }

               return true;
         }
      }

      return handled;
   }

   private void handleElementUpdated(Collection collection, Object oldElement, Object newElement) {
      synchronized (this._lockObj) {
         this.updateNode(this._treeField, oldElement, newElement);
         this.updateNode(this._disp, oldElement, newElement);
         this._startItem = newElement;
         if (this._query.length > 0) {
            this._keepFocus = true;
            this._searchThread.interruptSearch();
            this._startThread = true;
            this._searchThread.wakeup();
         }
      }
   }

   private void updateNode(TreeField treeField, Object oldCookie, Object newCookie) {
      for (int current = treeField.getFirstRoot(); current > 0; current = treeField.nextNode(current, 0, true)) {
         if (oldCookie == treeField.getCookie(current)) {
            treeField.setCookie(current, newCookie);
            treeField.invalidateNode(current);
            return;
         }
      }
   }

   @Override
   protected void makeMenu(SystemEnabledMenu menu, int instance) {
      Folder focusedFolder = this.getFocusedFolder();
      if (focusedFolder != null) {
         this.getContext().put(-1219344331000926502L, focusedFolder);
      } else {
         this.getContext().remove(-1219344331000926502L);
      }

      if (instance == 0) {
         if (this._createVerb != null && hasParent(focusedFolder)) {
            menu.add(this._createVerb);
         }

         if (this._deleteVerb != null && hasParent(focusedFolder) && focusedFolder != this._rootFolder) {
            menu.add(this._deleteVerb);
         }

         if (this._renameVerb != null && hasParent(focusedFolder) && focusedFolder != this._rootFolder) {
            menu.add(this._renameVerb);
         }

         menu.add(ExitVerb.createCancelVerb(0, null));
      }

      if (this.canSelectFolder(focusedFolder)) {
         int priority = Integer.MAX_VALUE;
         if (this._shouldMakeSelectVerbDefaultInitially) {
            if (this._initiallyOnDefaultFolder && this._startItem == focusedFolder || focusedFolder != null && !focusedFolder.containsSubFolders()) {
               priority = 0;
            }

            this._initiallyOnDefaultFolder = false;
         }

         menu.add(this._selectVerb, priority);
      }
   }

   private void handleElementRemoved(Collection collection, Object element) {
      synchronized (this._lockObj) {
         removeNode(this._treeField, element);
         removeNode(this._disp, element);
         if (this._query.length > 0) {
            this._keepFocus = true;
            this._searchThread.interruptSearch();
            this._startThread = true;
            this._searchThread.wakeup();
         }
      }
   }

   private static void removeNode(TreeField treeField, Object nodeCookie) {
      for (int current = treeField.getFirstRoot(); current > 0; current = treeField.nextNode(current, 0, true)) {
         if (nodeCookie == treeField.getCookie(current)) {
            int parentNode = treeField.getParent(current);
            treeField.deleteSubtree(current);
            if (parentNode > 0) {
               treeField.invalidateNode(parentNode);
               return;
            }
            break;
         }
      }
   }

   public FolderList(Folder startFolder) {
      this(startFolder, MessageResources.getString(5));
   }

   private void handleRemoveFolder(long folderLUID) {
      synchronized (this._lockObj) {
         removeFolder(this._treeField, folderLUID);
         removeFolder(this._disp, folderLUID);
         if (this._query.length > 0) {
            this._keepFocus = true;
            this._searchThread.interruptSearch();
            this._startThread = true;
            this._searchThread.wakeup();
         }
      }
   }

   private static void removeFolder(TreeField treeField, long folderLUID) {
      for (int current = treeField.getFirstRoot(); current > 0; current = treeField.nextNode(current, 0, true)) {
         Object nodeCookie = treeField.getCookie(current);
         if (nodeCookie instanceof Folder) {
            Folder folder = (Folder)nodeCookie;
            if (folder.getLUID() == folderLUID) {
               int parentNode = treeField.getParent(current);
               treeField.deleteSubtree(current);
               if (parentNode > 0) {
                  treeField.invalidateNode(parentNode);
                  return;
               }
               break;
            }
         }
      }
   }

   static int access$1010(FolderList x0) {
      return x0._searchCount--;
   }

   static int access$1008(FolderList x0) {
      return x0._searchCount++;
   }
}
