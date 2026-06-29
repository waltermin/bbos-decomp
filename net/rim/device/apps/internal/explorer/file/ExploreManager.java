package net.rim.device.apps.internal.explorer.file;

import java.util.Enumeration;
import java.util.Stack;
import javax.microedition.io.InputConnection;
import javax.microedition.io.file.FileSystemListener;
import javax.microedition.io.file.FileSystemRegistry;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.SortedReadableList;
import net.rim.device.api.collection.util.UnsortedReadableList;
import net.rim.device.api.io.MIMEMediaComparator;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.io.MIMETypeProvider;
import net.rim.device.api.io.file.FileSystemJournal;
import net.rim.device.api.io.file.FileSystemJournalEntry;
import net.rim.device.api.io.file.FileSystemJournalListener;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.file.AliasFileEntry;
import net.rim.device.apps.api.framework.file.ExplorerRegistry;
import net.rim.device.apps.api.framework.file.FileSelectionFilter;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.OptionsChangeListener;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.internal.explorer.file.menu.AddFolderMenuItem;
import net.rim.device.apps.internal.explorer.file.menu.DeleteMenuItem;
import net.rim.device.apps.internal.explorer.file.menu.OpenMenuItem;
import net.rim.device.apps.internal.explorer.file.menu.ToggleViewMenuItem;
import net.rim.device.apps.internal.explorer.file.options.ExplorerOptions;
import net.rim.device.apps.internal.explorer.file.render.RenderScreen;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;
import net.rim.device.apps.internal.explorer.file.util.FileItemComparator;
import net.rim.device.apps.internal.explorer.file.verbs.UpNavigationVerb;
import net.rim.device.internal.bluetooth.BluetoothME;
import net.rim.device.internal.io.file.FileUtilities;

public final class ExploreManager
   extends VerticalFieldManager
   implements FileSystemJournalListener,
   FileSystemListener,
   OptionsChangeListener,
   ExploreCallback,
   FocusChangeListener {
   private Stack _pathStack = (Stack)(new Object());
   private long _myStoredUSN;
   private FolderList _shortcuts;
   private FolderList _gallery;
   private FileConnectionHolder _view;
   private RootItem _root;
   private LocalizedDirectoryItemField _exploreRoot;
   private FileSelectionFilter _filter;
   private VerbProvider _verbProvider;
   private boolean _showAllMenuItems;
   private ExploreCallback _callback;
   private UiApplication _app;
   private MenuItem _upMenuItem = (MenuItem)(new Object(null, 483584, 0, new UpNavigationVerb(this), null));
   private Object _ctx;
   private ExploreManager$InvalidFileAlertVerb _invalidFileAlertVerb;
   private int _rootView;
   private AliasFileItemField _upAlias;
   private AliasFileItemField[] _globalAlias;
   private MediaFolderListener _mediaFolderListener;
   private ThumbnailFetcher _thumbnailFetcher;
   private ExploreManager$UpdateLayoutInvokeLater _updateLayoutInvokeLater = new ExploreManager$UpdateLayoutInvokeLater(this, null);
   private ExploreManager$OpenDirectoryWorker _openDirTask;
   private static final int BULK_ADD_COUNT = 100;

   public final void cleanup() {
      UiApplication app = UiApplication.getUiApplication();
      app.removeFileSystemListener(this);
      ExplorerOptions.getOptions().removeOptionsChangeListener(this);
   }

   public final FileItemField getSelectedItem() {
      Field field = this.getLeafFieldWithFocus();
      return !(field instanceof FolderList) ? null : ((FolderList)field).getSelectedItem();
   }

   public final void addAliasToList(String path, AliasFileEntry[] entries, FileSelectionFilter filter, UnsortedReadableList fileList) {
      boolean writeableOnly = filter.isSelectWriteable();
      int i = entries.length;

      while (--i >= 0) {
         AliasFileEntry entry = entries[i];
         if (!(entry instanceof AliasFolderEntry)) {
            if (!writeableOnly) {
               FileItemField fileItem = new AliasFileItemField(entry);
               fileItem.setPath(path);
               fileList.elementAdded(null, fileItem);
            }
         } else {
            AliasFolderEntry folderEntry = (AliasFolderEntry)entry;
            if (folderEntry.isActiveInView(path, filter.getMediaType(), writeableOnly)) {
               fileList.elementAdded(null, new AliasFileItemField(entry));
            }
         }
      }
   }

   public final boolean openItem(FileItemField fileItem) {
      if (fileItem != null) {
         if (fileItem instanceof AliasFileItemField) {
            ContextObject.put(this._ctx, 2765042845091913199L, fileItem.getPath());
            ContextObject.put(this._ctx, -1477447097671931650L, this.getScreen());
            ((AliasFileItemField)fileItem).invoke(this._ctx);
            return true;
         } else if (fileItem.isDirectory()) {
            this.pushPath(fileItem);
            return true;
         } else {
            this.openFile(fileItem);
            return true;
         }
      } else {
         return false;
      }
   }

   public final boolean openFileURL(String url) {
      if (url != null && FileUtilities.checkFileExists(url)) {
         this.openFile(new FileItemField(url));
         return true;
      } else {
         return false;
      }
   }

   public final FolderList getFileView() {
      return this._gallery;
   }

   public final void explore(FileSelectionFilter filter) {
      this.pushPath(this._exploreRoot, filter);
   }

   public final boolean popPath() {
      if (this._pathStack.size() == 0) {
         return false;
      }

      ExploreManager$PathStackElement returnPath = (ExploreManager$PathStackElement)this._pathStack.pop();
      this.restoreView(returnPath);
      return true;
   }

   public final void pushPath(FileConnectionHolder path) {
      this.pushPath(path, this._filter);
   }

   public final void pushPath(FileConnectionHolder path, FileSelectionFilter filter) {
      this._myStoredUSN = FileSystemJournal.getNextUSN();
      this._pathStack.push(new ExploreManager$PathStackElement(this._view, this._filter, this._root, this._shortcuts));
      this._filter = filter;
      if (path instanceof RootItem) {
         this._root = (RootItem)path;
      }

      this.setCurrentView(path, filter);
   }

   public final boolean navigateUp(boolean execute) {
      return this.navigateUp(execute, this._pathStack.isEmpty(), this.getCurrentView());
   }

   public final FileConnectionHolder getParent(FileConnectionHolder fileItem) {
      return fileItem instanceof FileItemField ? this._root.getParent((FileItemField)fileItem) : fileItem;
   }

   public final void openFile(FileItemField fileItem) {
      RenderScreen screen = new RenderScreen(this, fileItem, false);
      if (screen != null) {
         UiApplication.getUiApplication().pushScreen(screen);
         screen.finishLoadingFile();
      }
   }

   public final void openStream(InputConnection input) {
      RenderScreen screen = new RenderScreen(this, this.getCurrentView().getURL(), input);
      if (screen != null) {
         UiApplication.getUiApplication().pushScreen(screen);
         screen.finishLoadingFile();
      }
   }

   public final void openSlideShow(FileItemField fileItem) {
      RenderScreen screen = new RenderScreen(this, fileItem, true);
      if (screen != null) {
         this.setFieldWithFocus(this._gallery);
         UiApplication.getUiApplication().pushScreen(screen);
         screen.finishLoadingFile();
      }
   }

   public final FileConnectionHolder getCurrentView() {
      return this._view;
   }

   public final FileSelectionFilter getFilter() {
      return this._filter;
   }

   public final void makeMenuForPath(Menu contextMenu, int instance) {
      if (this._view instanceof FileItemField) {
         this.makeMenu(contextMenu, (FileItemField)this._view, instance);
      }
   }

   public final void addMenuItems(SystemEnabledMenu contextMenu, int instance) {
      this.makeMenu(contextMenu, instance);
   }

   public final void addFileActionMenuItems(Menu param1, FileItemField param2, Field param3, Screen param4, int param5) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 000: aload 2
      // 001: ifnonnull 007
      // 004: goto 237
      // 007: bipush 0
      // 008: istore 6
      // 00a: aconst_null
      // 00b: astore 7
      // 00d: aload 2
      // 00e: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getFileConnection ()Ljavax/microedition/io/file/FileConnection;
      // 011: astore 7
      // 013: aload 7
      // 015: ifnull 026
      // 018: aload 7
      // 01a: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 01f: ifeq 026
      // 022: bipush 1
      // 023: goto 027
      // 026: bipush 0
      // 027: istore 6
      // 029: aload 7
      // 02b: ifnull 069
      // 02e: aload 7
      // 030: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 035: goto 069
      // 038: astore 8
      // 03a: goto 069
      // 03d: astore 8
      // 03f: aload 7
      // 041: ifnull 069
      // 044: aload 7
      // 046: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 04b: goto 069
      // 04e: astore 8
      // 050: goto 069
      // 053: astore 9
      // 055: aload 7
      // 057: ifnull 066
      // 05a: aload 7
      // 05c: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 061: goto 066
      // 064: astore 10
      // 066: aload 9
      // 068: athrow
      // 069: iload 6
      // 06b: ifne 071
      // 06e: goto 237
      // 071: iload 5
      // 073: ifeq 079
      // 076: goto 114
      // 079: aload 2
      // 07a: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.canDelete ()Z
      // 07d: ifne 083
      // 080: goto 0f3
      // 083: aload 1
      // 084: new net/rim/device/apps/internal/explorer/file/menu/DeleteMenuItem
      // 087: dup
      // 088: aload 2
      // 089: aload 4
      // 08b: invokespecial net/rim/device/apps/internal/explorer/file/menu/DeleteMenuItem.<init> (Lnet/rim/device/apps/internal/explorer/file/FileItemField;Lnet/rim/device/api/ui/Screen;)V
      // 08e: invokevirtual net/rim/device/api/ui/component/Menu.add (Lnet/rim/device/api/ui/MenuItem;)V
      // 091: aload 2
      // 092: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.isDirectory ()Z
      // 095: ifne 0d0
      // 098: aload 0
      // 099: getfield net/rim/device/apps/internal/explorer/file/ExploreManager._showAllMenuItems Z
      // 09c: ifeq 0d0
      // 09f: aload 0
      // 0a0: getfield net/rim/device/apps/internal/explorer/file/ExploreManager._ctx Ljava/lang/Object;
      // 0a3: ldc2_w 3941043584844673548
      // 0a6: invokestatic net/rim/device/apps/api/framework/model/ContextObject.get (Ljava/lang/Object;J)Ljava/lang/Object;
      // 0a9: checkcast java/lang/Object
      // 0ac: astore 8
      // 0ae: aload 1
      // 0af: new net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem
      // 0b2: dup
      // 0b3: aload 2
      // 0b4: aload 8
      // 0b6: bipush 1
      // 0b7: aload 4
      // 0b9: invokespecial net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem.<init> (Lnet/rim/device/apps/internal/explorer/file/FileItemField;Ljava/lang/Integer;ZLnet/rim/device/api/ui/Screen;)V
      // 0bc: invokevirtual net/rim/device/api/ui/component/Menu.add (Lnet/rim/device/api/ui/MenuItem;)V
      // 0bf: aload 1
      // 0c0: new net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem
      // 0c3: dup
      // 0c4: aload 2
      // 0c5: aload 8
      // 0c7: bipush 0
      // 0c8: aload 4
      // 0ca: invokespecial net/rim/device/apps/internal/explorer/file/menu/MoveMenuItem.<init> (Lnet/rim/device/apps/internal/explorer/file/FileItemField;Ljava/lang/Integer;ZLnet/rim/device/api/ui/Screen;)V
      // 0cd: invokevirtual net/rim/device/api/ui/component/Menu.add (Lnet/rim/device/api/ui/MenuItem;)V
      // 0d0: new net/rim/device/apps/internal/explorer/file/verbs/RenameFileVerb
      // 0d3: dup
      // 0d4: aconst_null
      // 0d5: aload 4
      // 0d7: invokespecial net/rim/device/apps/internal/explorer/file/verbs/RenameFileVerb.<init> (Ljava/lang/Object;Lnet/rim/device/api/ui/Screen;)V
      // 0da: astore 8
      // 0dc: aload 1
      // 0dd: new java/lang/Object
      // 0e0: dup
      // 0e1: aconst_null
      // 0e2: aload 8
      // 0e4: invokevirtual net/rim/device/apps/api/framework/verb/Verb.getOrdering ()I
      // 0e7: ldc_w 2147483647
      // 0ea: aload 8
      // 0ec: aload 2
      // 0ed: invokespecial net/rim/device/apps/api/ui/VerbMenuItem.<init> (Ljava/lang/String;IILnet/rim/device/apps/api/framework/verb/Verb;Ljava/lang/Object;)V
      // 0f0: invokevirtual net/rim/device/api/ui/component/Menu.add (Lnet/rim/device/api/ui/MenuItem;)V
      // 0f3: aload 2
      // 0f4: dup
      // 0f5: instanceof net/rim/device/apps/internal/explorer/file/AliasFileItemField
      // 0f8: ifne 0ff
      // 0fb: pop
      // 0fc: goto 108
      // 0ff: checkcast net/rim/device/apps/internal/explorer/file/AliasFileItemField
      // 102: invokevirtual net/rim/device/apps/internal/explorer/file/AliasFileItemField.isExecutable ()Z
      // 105: ifne 114
      // 108: aload 1
      // 109: new net/rim/device/apps/internal/explorer/file/menu/PropertiesMenuItem
      // 10c: dup
      // 10d: aload 2
      // 10e: invokespecial net/rim/device/apps/internal/explorer/file/menu/PropertiesMenuItem.<init> (Ljava/lang/Object;)V
      // 111: invokevirtual net/rim/device/api/ui/component/Menu.add (Lnet/rim/device/api/ui/MenuItem;)V
      // 114: aload 0
      // 115: getfield net/rim/device/apps/internal/explorer/file/ExploreManager._showAllMenuItems Z
      // 118: ifne 11e
      // 11b: goto 237
      // 11e: aload 2
      // 11f: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getMediaType ()I
      // 122: bipush 1
      // 123: if_icmpne 13e
      // 126: aload 1
      // 127: new net/rim/device/apps/internal/explorer/file/menu/SetAsHomescreenBackgroundMenuItem
      // 12a: dup
      // 12b: aload 2
      // 12c: aload 3
      // 12d: invokespecial net/rim/device/apps/internal/explorer/file/menu/SetAsHomescreenBackgroundMenuItem.<init> (Lnet/rim/device/apps/internal/explorer/file/FileItemField;Lnet/rim/device/api/ui/Field;)V
      // 130: invokevirtual net/rim/device/api/ui/component/Menu.add (Lnet/rim/device/api/ui/MenuItem;)V
      // 133: aload 1
      // 134: new net/rim/device/apps/internal/explorer/file/menu/ClearHomescreenBackgroundMenuItem
      // 137: dup
      // 138: invokespecial net/rim/device/apps/internal/explorer/file/menu/ClearHomescreenBackgroundMenuItem.<init> ()V
      // 13b: invokevirtual net/rim/device/api/ui/component/Menu.add (Lnet/rim/device/api/ui/MenuItem;)V
      // 13e: invokestatic net/rim/device/apps/internal/explorer/file/ExploreManager.checkBluetoothPolicy ()Z
      // 141: ifeq 168
      // 144: aload 2
      // 145: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.isDirectory ()Z
      // 148: ifne 168
      // 14b: aload 2
      // 14c: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.isDRMForwardLocked ()Z
      // 14f: ifne 168
      // 152: aload 2
      // 153: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.isBuiltIn ()Z
      // 156: ifne 168
      // 159: aload 1
      // 15a: new net/rim/device/apps/internal/explorer/file/menu/SendBluetooth
      // 15d: dup
      // 15e: aload 2
      // 15f: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getURL ()Ljava/lang/String;
      // 162: invokespecial net/rim/device/apps/internal/explorer/file/menu/SendBluetooth.<init> (Ljava/lang/String;)V
      // 165: invokevirtual net/rim/device/api/ui/component/Menu.add (Lnet/rim/device/api/ui/MenuItem;)V
      // 168: iload 5
      // 16a: ifeq 170
      // 16d: goto 237
      // 170: aload 2
      // 171: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.isDRMForwardLocked ()Z
      // 174: ifne 1b2
      // 177: aload 2
      // 178: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.isDirectory ()Z
      // 17b: ifne 1b2
      // 17e: aload 2
      // 17f: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getName ()Ljava/lang/String;
      // 182: invokestatic net/rim/device/api/io/MIMETypeAssociations.getMIMEType (Ljava/lang/String;)Ljava/lang/String;
      // 185: astore 8
      // 187: aload 8
      // 189: aload 2
      // 18a: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getURL ()Ljava/lang/String;
      // 18d: aconst_null
      // 18e: invokestatic net/rim/device/apps/api/framework/registration/MIMEContentVerbRepository.getMenuItems (Ljava/lang/String;Ljava/lang/String;Lnet/rim/device/apps/api/framework/model/ContextObject;)[Lnet/rim/device/api/ui/MenuItem;
      // 191: astore 9
      // 193: aload 9
      // 195: ifnull 1b2
      // 198: bipush 0
      // 199: istore 10
      // 19b: iload 10
      // 19d: aload 9
      // 19f: arraylength
      // 1a0: if_icmpge 1b2
      // 1a3: aload 1
      // 1a4: aload 9
      // 1a6: iload 10
      // 1a8: aaload
      // 1a9: invokevirtual net/rim/device/api/ui/component/Menu.add (Lnet/rim/device/api/ui/MenuItem;)V
      // 1ac: iinc 10 1
      // 1af: goto 19b
      // 1b2: new java/lang/Object
      // 1b5: dup
      // 1b6: invokespecial net/rim/device/apps/api/framework/model/ContextObject.<init> ()V
      // 1b9: astore 8
      // 1bb: aload 1
      // 1bc: dup
      // 1bd: instanceof java/lang/Object
      // 1c0: ifne 1c7
      // 1c3: pop
      // 1c4: goto 1e5
      // 1c7: checkcast java/lang/Object
      // 1ca: invokevirtual net/rim/device/apps/api/ui/SystemEnabledMenu.getContext ()Lnet/rim/device/apps/api/framework/model/ContextObject;
      // 1cd: astore 9
      // 1cf: aload 9
      // 1d1: ifnull 1e5
      // 1d4: aload 9
      // 1d6: bipush 45
      // 1d8: invokevirtual net/rim/device/apps/api/framework/model/ContextObject.getFlag (I)Z
      // 1db: ifeq 1e5
      // 1de: aload 8
      // 1e0: bipush 45
      // 1e2: invokevirtual net/rim/device/apps/api/framework/model/ContextObject.setFlag (I)V
      // 1e5: aload 8
      // 1e7: ldc2_w 2765042845091913199
      // 1ea: aload 2
      // 1eb: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getFullPath ()Ljava/lang/String;
      // 1ee: invokevirtual net/rim/device/api/util/LongHashtable.put (JLjava/lang/Object;)Ljava/lang/Object;
      // 1f1: pop
      // 1f2: aload 0
      // 1f3: getfield net/rim/device/apps/internal/explorer/file/ExploreManager._showAllMenuItems Z
      // 1f6: ifeq 237
      // 1f9: bipush 0
      // 1fa: anewarray 1098
      // 1fd: astore 9
      // 1ff: aload 2
      // 200: aload 8
      // 202: aload 9
      // 204: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getVerbs (Ljava/lang/Object;[Lnet/rim/device/apps/api/framework/verb/Verb;)Lnet/rim/device/apps/api/framework/verb/Verb;
      // 207: pop
      // 208: bipush 0
      // 209: istore 10
      // 20b: iload 10
      // 20d: aload 9
      // 20f: arraylength
      // 210: if_icmpge 237
      // 213: aload 1
      // 214: new java/lang/Object
      // 217: dup
      // 218: aconst_null
      // 219: aload 9
      // 21b: iload 10
      // 21d: aaload
      // 21e: invokevirtual net/rim/device/apps/api/framework/verb/Verb.getOrdering ()I
      // 221: ldc_w 2147483647
      // 224: aload 9
      // 226: iload 10
      // 228: aaload
      // 229: aload 8
      // 22b: invokespecial net/rim/device/apps/api/ui/VerbMenuItem.<init> (Ljava/lang/String;IILnet/rim/device/apps/api/framework/verb/Verb;Ljava/lang/Object;)V
      // 22e: invokevirtual net/rim/device/api/ui/component/Menu.add (Lnet/rim/device/api/ui/MenuItem;)V
      // 231: iinc 10 1
      // 234: goto 20b
      // 237: iload 5
      // 239: ifne 247
      // 23c: aload 1
      // 23d: new net/rim/device/apps/internal/explorer/file/menu/HideMenuItem
      // 240: dup
      // 241: invokespecial net/rim/device/apps/internal/explorer/file/menu/HideMenuItem.<init> ()V
      // 244: invokevirtual net/rim/device/api/ui/component/Menu.add (Lnet/rim/device/api/ui/MenuItem;)V
      // 247: return
      // try (21 -> 23): 24 null
      // try (7 -> 19): 26 null
      // try (29 -> 31): 32 null
      // try (7 -> 19): 34 null
      // try (26 -> 27): 34 null
      // try (37 -> 39): 40 null
      // try (34 -> 35): 34 null
   }

   public final void addThirdPartyMenuItems(Menu contextMenu, long menuItemId, FileItemField selectedItem) {
      String filename = null;
      if (selectedItem != null) {
         filename = selectedItem.getFullPath();
      }

      VerbRepository vr = VerbRepository.getVerbRepository(menuItemId);
      Verb[] verbs = vr.getVerbs(menuItemId);
      if (verbs != null && verbs.length > 0) {
         for (int i = 0; i < verbs.length; i++) {
            MIMEMediaComparator comparator = (MIMEMediaComparator)(new Object());
            if (verbs[i] instanceof Object) {
               String mimeType = selectedItem == null ? null : MIMETypeAssociations.getMIMEType(filename);
               MIMETypeProvider provider = (MIMETypeProvider)verbs[i];
               String menuMimeType = provider.getMIMEType();
               if (menuMimeType == null || mimeType != null && comparator.compare(menuMimeType, mimeType) == 0) {
                  VerbMenuItem vmi = (VerbMenuItem)(new Object(verbs[i], verbs[i].getOrdering()));
                  vmi.setContext(filename);
                  contextMenu.add(vmi);
               }
            } else {
               VerbMenuItem vmi = (VerbMenuItem)(new Object(verbs[i], verbs[i].getOrdering()));
               vmi.setContext(filename);
               contextMenu.add(vmi);
            }
         }
      }
   }

   public final void setCurrentView(FileConnectionHolder path, FileSelectionFilter fileFilter) {
      this.initializeView(path, fileFilter);
      int initialIndex = this._shortcuts.getFirstNonExecutableAliasIndex();
      if (initialIndex >= 0) {
         this._shortcuts.setSelectedIndex(initialIndex);
         this.setFieldWithFocus(this._shortcuts);
      } else if (this._gallery.getFileCount() != 0) {
         this._gallery.setSelectedIndex(0);
         this.setFieldWithFocus(this._gallery);
      } else {
         initialIndex = this._shortcuts.determineInitialIndex();
         this._shortcuts.setSelectedIndex(initialIndex);
         this.setFieldWithFocus(this._shortcuts);
      }
   }

   public final void refreshView() {
      this.setCurrentView(this._view, this._filter);
   }

   @Override
   public final void fileJournalChanged() {
      long nextUSN = FileSystemJournal.getNextUSN();
      if (this._view != null) {
         String currentViewPath = this._view.getURL();
         if (currentViewPath != null && currentViewPath.startsWith("file://")) {
            currentViewPath = currentViewPath.substring(7);

            for (long lookUSN = nextUSN - 1; lookUSN >= this._myStoredUSN; lookUSN -= 1) {
               FileSystemJournalEntry entry = FileSystemJournal.getEntry(lookUSN);
               if (entry == null || this.testUpdateForPath(currentViewPath, entry)) {
                  break;
               }
            }
         }
      }

      this._myStoredUSN = nextUSN;
   }

   @Override
   public final void optionsChanged(int option) {
      ExplorerOptions options = ExplorerOptions.getOptions();
      switch (option) {
         case 3:
         default:
            this._gallery.setNumberOfColumns(options.getNumberOfColumns());
            return;
         case 4:
         case 5:
            if (this._rootView == 1) {
               Comparator comparator = FileItemComparator.getInstance(options.getFilelistSortProperty());
               ReadableList list = this._shortcuts.getList();
               if (list instanceof Object) {
                  ((SortedReadableList)list).setComparator(comparator);
               }

               list = this._gallery.getList();
               if (list instanceof Object) {
                  ((SortedReadableList)list).setComparator(comparator);
               }
            }
         case 2:
      }
   }

   @Override
   public final void rootChanged(int state, String rootName) {
      String rootPath = ((StringBuffer)(new Object("/"))).append(rootName).toString();
      switch (state) {
         case -1:
            break;
         case 0:
         default:
            if (this._root instanceof LocalizedDirectoryItemField) {
               LocalizedDirectoryItemField rootHolder = (LocalizedDirectoryItemField)this._root;
               if (rootHolder.onRootAdded(rootPath) && rootHolder == this._view) {
                  this.setCurrentView(rootHolder, this._filter);
                  return;
               }
            }
            break;
         case 1:
            boolean changingView = false;

            for (int i = 0; i < this._pathStack.size(); i++) {
               ExploreManager$PathStackElement elem = (ExploreManager$PathStackElement)this._pathStack.elementAt(i);
               FileConnectionHolder view = elem._view;
               String path = view.getPath();
               if (path != null && StringUtilities.startsWithIgnoreCase(path, rootPath)) {
                  this._pathStack.setSize(i);
                  this.popPath();
                  changingView = false;
                  break;
               }

               if (view instanceof LocalizedDirectoryItemField) {
                  LocalizedDirectoryItemField lItem = (LocalizedDirectoryItemField)view;
                  lItem.onRootRemoved(rootPath);
                  if (lItem == this._view) {
                     changingView = true;
                  }
               }
            }

            if (!changingView) {
               String path = this._view.getPath();
               if (path != null && StringUtilities.startsWithIgnoreCase(path, rootPath)) {
                  if (this._pathStack.size() > 0) {
                     this.popPath();
                  } else if (this._callback instanceof ExploreScreen) {
                     ExploreScreen screen = (ExploreScreen)this._callback;
                     if (screen.isDisplayed()) {
                        screen.close();
                     }
                  }
               } else if (this._view instanceof LocalizedDirectoryItemField) {
                  ((LocalizedDirectoryItemField)this._view).onRootRemoved(rootPath);
                  changingView = true;
               }
            }

            if (changingView) {
               this.setCurrentView(this._view, this._filter);
            }
      }
   }

   @Override
   public final void pathSet(Object path) {
      if (this._callback != null) {
         this._callback.pathSet(path);
      }
   }

   @Override
   public final void statusOn() {
      if (this._callback != null) {
         this._callback.statusOn();
      }
   }

   @Override
   public final void statusOff() {
      if (this._callback != null) {
         this._callback.statusOff();
      }
   }

   @Override
   public final void currentItemChanged(Field field, FileItemField item) {
      if (this._callback != null) {
         this._callback.currentItemChanged(field, field == this.getFieldWithFocus() ? item : null);
      }
   }

   @Override
   public final void focusChanged(Field field, int eventType) {
      if (eventType == 1 && (field == this._gallery || field == this._shortcuts) && this._callback != null) {
         this.currentItemChanged(field, ((FolderList)field).getSelectedItem());
      }
   }

   private final MenuItem getMenuItem(Integer menuItemId, FileItemField fileItem) {
      MenuItem menuItem = null;
      switch (menuItemId) {
         case 5:
            menuItem = new OpenMenuItem(this, fileItem);
         default:
            return menuItem;
      }
   }

   private static final boolean checkBluetoothPolicy() {
      return !ITPolicy.getBoolean(34, 1, false) && !ITPolicy.getBoolean(34, 14, false) && BluetoothME.isSupported();
   }

   @Override
   protected final void makeMenu(Menu param1, int param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 000: aload 0
      // 001: aload 1
      // 002: iload 2
      // 003: invokespecial net/rim/device/api/ui/Manager.makeMenu (Lnet/rim/device/api/ui/component/Menu;I)V
      // 006: aload 0
      // 007: invokevirtual net/rim/device/apps/internal/explorer/file/ExploreManager.getSelectedItem ()Lnet/rim/device/apps/internal/explorer/file/FileItemField;
      // 00a: astore 3
      // 00b: aload 0
      // 00c: invokevirtual net/rim/device/apps/internal/explorer/file/ExploreManager.getCurrentView ()Lnet/rim/device/apps/internal/explorer/file/FileConnectionHolder;
      // 00f: astore 4
      // 011: aload 3
      // 012: dup
      // 013: instanceof net/rim/device/apps/internal/explorer/file/FileItemField
      // 016: ifne 01d
      // 019: pop
      // 01a: goto 1f5
      // 01d: checkcast net/rim/device/apps/internal/explorer/file/FileItemField
      // 020: astore 5
      // 022: aload 5
      // 024: instanceof net/rim/device/apps/internal/explorer/file/UpAliasFileItemField
      // 027: ifne 042
      // 02a: new net/rim/device/apps/internal/explorer/file/menu/OpenMenuItem
      // 02d: dup
      // 02e: aload 0
      // 02f: aload 5
      // 031: invokespecial net/rim/device/apps/internal/explorer/file/menu/OpenMenuItem.<init> (Lnet/rim/device/apps/internal/explorer/file/ExploreManager;Lnet/rim/device/apps/internal/explorer/file/FileItemField;)V
      // 034: astore 6
      // 036: aload 1
      // 037: aload 6
      // 039: invokevirtual net/rim/device/api/ui/component/Menu.add (Lnet/rim/device/api/ui/MenuItem;)V
      // 03c: aload 1
      // 03d: aload 6
      // 03f: invokevirtual net/rim/device/api/ui/component/Menu.setDefault (Lnet/rim/device/api/ui/MenuItem;)V
      // 042: aload 0
      // 043: aload 1
      // 044: ldc2_w 3504265587951702900
      // 047: aload 5
      // 049: invokevirtual net/rim/device/apps/internal/explorer/file/ExploreManager.addThirdPartyMenuItems (Lnet/rim/device/api/ui/component/Menu;JLnet/rim/device/apps/internal/explorer/file/FileItemField;)V
      // 04c: aload 0
      // 04d: aload 1
      // 04e: ldc2_w -7944553129300517756
      // 051: aload 5
      // 053: invokevirtual net/rim/device/apps/internal/explorer/file/ExploreManager.addThirdPartyMenuItems (Lnet/rim/device/api/ui/component/Menu;JLnet/rim/device/apps/internal/explorer/file/FileItemField;)V
      // 056: aload 0
      // 057: aload 1
      // 058: aload 5
      // 05a: aconst_null
      // 05b: aconst_null
      // 05c: iload 2
      // 05d: invokevirtual net/rim/device/apps/internal/explorer/file/ExploreManager.addFileActionMenuItems (Lnet/rim/device/api/ui/component/Menu;Lnet/rim/device/apps/internal/explorer/file/FileItemField;Lnet/rim/device/api/ui/Field;Lnet/rim/device/api/ui/Screen;I)V
      // 060: aload 0
      // 061: getfield net/rim/device/apps/internal/explorer/file/ExploreManager._showAllMenuItems Z
      // 064: ifne 06a
      // 067: goto 20a
      // 06a: aload 5
      // 06c: astore 6
      // 06e: aload 0
      // 06f: invokevirtual net/rim/device/api/ui/Manager.getLeafFieldWithFocus ()Lnet/rim/device/api/ui/Field;
      // 072: aload 0
      // 073: getfield net/rim/device/apps/internal/explorer/file/ExploreManager._gallery Lnet/rim/device/apps/internal/explorer/file/FolderList;
      // 076: if_acmpeq 091
      // 079: aload 0
      // 07a: getfield net/rim/device/apps/internal/explorer/file/ExploreManager._gallery Lnet/rim/device/apps/internal/explorer/file/FolderList;
      // 07d: invokevirtual net/rim/device/apps/internal/explorer/file/FolderList.getFileCount ()I
      // 080: ifle 08e
      // 083: aload 0
      // 084: getfield net/rim/device/apps/internal/explorer/file/ExploreManager._gallery Lnet/rim/device/apps/internal/explorer/file/FolderList;
      // 087: bipush 0
      // 088: invokevirtual net/rim/device/apps/internal/explorer/file/FolderList.getFile (I)Lnet/rim/device/apps/internal/explorer/file/FileItemField;
      // 08b: goto 08f
      // 08e: aconst_null
      // 08f: astore 6
      // 091: aload 6
      // 093: ifnull 0ab
      // 096: aload 6
      // 098: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.canView ()Z
      // 09b: ifne 0ab
      // 09e: aload 0
      // 09f: getfield net/rim/device/apps/internal/explorer/file/ExploreManager._gallery Lnet/rim/device/apps/internal/explorer/file/FolderList;
      // 0a2: aload 6
      // 0a4: bipush 1
      // 0a5: bipush 0
      // 0a6: invokevirtual net/rim/device/apps/internal/explorer/file/FolderList.getNextViewableFile (Lnet/rim/device/apps/internal/explorer/file/FileItemField;ZZ)Lnet/rim/device/apps/internal/explorer/file/FileItemField;
      // 0a9: astore 6
      // 0ab: aload 6
      // 0ad: ifnull 134
      // 0b0: aload 6
      // 0b2: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getMediaType ()I
      // 0b5: lookupswitch 127 4 1 43 2 86 3 86 7 86
      // 0e0: aload 0
      // 0e1: getfield net/rim/device/apps/internal/explorer/file/ExploreManager._gallery Lnet/rim/device/apps/internal/explorer/file/FolderList;
      // 0e4: invokevirtual net/rim/device/apps/internal/explorer/file/FolderList.getFileCount ()I
      // 0e7: ifle 134
      // 0ea: aload 0
      // 0eb: getfield net/rim/device/apps/internal/explorer/file/ExploreManager._gallery Lnet/rim/device/apps/internal/explorer/file/FolderList;
      // 0ee: aload 6
      // 0f0: bipush 0
      // 0f1: bipush 0
      // 0f2: invokevirtual net/rim/device/apps/internal/explorer/file/FolderList.getNextViewableFile (Lnet/rim/device/apps/internal/explorer/file/FileItemField;ZZ)Lnet/rim/device/apps/internal/explorer/file/FileItemField;
      // 0f5: aload 6
      // 0f7: if_acmpeq 134
      // 0fa: aload 1
      // 0fb: new net/rim/device/apps/internal/explorer/file/menu/SlideShowMenuItem
      // 0fe: dup
      // 0ff: aload 0
      // 100: aload 6
      // 102: invokespecial net/rim/device/apps/internal/explorer/file/menu/SlideShowMenuItem.<init> (Lnet/rim/device/apps/internal/explorer/file/ExploreManager;Lnet/rim/device/apps/internal/explorer/file/FileItemField;)V
      // 105: invokevirtual net/rim/device/api/ui/component/Menu.add (Lnet/rim/device/api/ui/MenuItem;)V
      // 108: goto 134
      // 10b: aload 4
      // 10d: invokeinterface net/rim/device/apps/internal/explorer/file/FileConnectionHolder.getURL ()Ljava/lang/String; 1
      // 112: astore 7
      // 114: aload 1
      // 115: new net/rim/device/apps/internal/explorer/file/menu/PlayAllMenuItem
      // 118: dup
      // 119: aload 7
      // 11b: aload 0
      // 11c: bipush 100
      // 11e: invokespecial net/rim/device/apps/internal/explorer/file/menu/PlayAllMenuItem.<init> (Ljava/lang/String;Lnet/rim/device/apps/internal/explorer/file/ExploreManager;I)V
      // 121: invokevirtual net/rim/device/api/ui/component/Menu.add (Lnet/rim/device/api/ui/MenuItem;)V
      // 124: aload 1
      // 125: new net/rim/device/apps/internal/explorer/file/menu/PlayAllMenuItem
      // 128: dup
      // 129: aload 7
      // 12b: aload 0
      // 12c: bipush 105
      // 12e: invokespecial net/rim/device/apps/internal/explorer/file/menu/PlayAllMenuItem.<init> (Ljava/lang/String;Lnet/rim/device/apps/internal/explorer/file/ExploreManager;I)V
      // 131: invokevirtual net/rim/device/api/ui/component/Menu.add (Lnet/rim/device/api/ui/MenuItem;)V
      // 134: aload 5
      // 136: ifnonnull 13c
      // 139: goto 20a
      // 13c: aload 5
      // 13e: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.isDirectory ()Z
      // 141: ifne 147
      // 144: goto 20a
      // 147: bipush 0
      // 148: istore 7
      // 14a: aconst_null
      // 14b: astore 8
      // 14d: aload 5
      // 14f: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getFileConnection ()Ljavax/microedition/io/file/FileConnection;
      // 152: astore 8
      // 154: aload 8
      // 156: ifnull 1b5
      // 159: aload 8
      // 15b: invokeinterface javax/microedition/io/file/FileConnection.list ()Ljava/util/Enumeration; 1
      // 160: astore 9
      // 162: iload 7
      // 164: ifne 1b5
      // 167: aload 9
      // 169: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 16e: ifeq 1b5
      // 171: aload 9
      // 173: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 178: checkcast java/lang/Object
      // 17b: astore 10
      // 17d: aload 10
      // 17f: invokestatic net/rim/device/api/io/MIMETypeAssociations.getMediaType (Ljava/lang/String;)I
      // 182: tableswitch 26 1 3 -32 26 26
      // 19c: bipush 1
      // 19d: istore 7
      // 19f: aload 1
      // 1a0: new net/rim/device/apps/internal/explorer/file/menu/PlayAllMenuItem
      // 1a3: dup
      // 1a4: aload 5
      // 1a6: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getURL ()Ljava/lang/String;
      // 1a9: aload 0
      // 1aa: bipush 123
      // 1ac: invokespecial net/rim/device/apps/internal/explorer/file/menu/PlayAllMenuItem.<init> (Ljava/lang/String;Lnet/rim/device/apps/internal/explorer/file/ExploreManager;I)V
      // 1af: invokevirtual net/rim/device/api/ui/component/Menu.add (Lnet/rim/device/api/ui/MenuItem;)V
      // 1b2: goto 162
      // 1b5: aload 8
      // 1b7: ifnull 20a
      // 1ba: aload 8
      // 1bc: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1c1: goto 20a
      // 1c4: astore 9
      // 1c6: goto 20a
      // 1c9: astore 9
      // 1cb: aload 8
      // 1cd: ifnull 20a
      // 1d0: aload 8
      // 1d2: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1d7: goto 20a
      // 1da: astore 9
      // 1dc: goto 20a
      // 1df: astore 11
      // 1e1: aload 8
      // 1e3: ifnull 1f2
      // 1e6: aload 8
      // 1e8: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1ed: goto 1f2
      // 1f0: astore 12
      // 1f2: aload 11
      // 1f4: athrow
      // 1f5: aload 4
      // 1f7: astore 3
      // 1f8: aload 0
      // 1f9: aload 1
      // 1fa: ldc2_w 3504265587951702900
      // 1fd: aconst_null
      // 1fe: invokevirtual net/rim/device/apps/internal/explorer/file/ExploreManager.addThirdPartyMenuItems (Lnet/rim/device/api/ui/component/Menu;JLnet/rim/device/apps/internal/explorer/file/FileItemField;)V
      // 201: aload 0
      // 202: aload 1
      // 203: ldc2_w -7944553129300517756
      // 206: aconst_null
      // 207: invokevirtual net/rim/device/apps/internal/explorer/file/ExploreManager.addThirdPartyMenuItems (Lnet/rim/device/api/ui/component/Menu;JLnet/rim/device/apps/internal/explorer/file/FileItemField;)V
      // 20a: invokestatic net/rim/device/apps/internal/explorer/file/ExploreManager.checkBluetoothPolicy ()Z
      // 20d: ifeq 21b
      // 210: aload 1
      // 211: new net/rim/device/apps/internal/explorer/file/menu/ReceiveBluetooth
      // 214: dup
      // 215: invokespecial net/rim/device/apps/internal/explorer/file/menu/ReceiveBluetooth.<init> ()V
      // 218: invokevirtual net/rim/device/api/ui/component/Menu.add (Lnet/rim/device/api/ui/MenuItem;)V
      // 21b: aload 0
      // 21c: getfield net/rim/device/apps/internal/explorer/file/ExploreManager._root Lnet/rim/device/apps/internal/explorer/file/RootItem;
      // 21f: aload 0
      // 220: getfield net/rim/device/apps/internal/explorer/file/ExploreManager._exploreRoot Lnet/rim/device/apps/internal/explorer/file/LocalizedDirectoryItemField;
      // 223: if_acmpeq 23c
      // 226: aload 0
      // 227: getfield net/rim/device/apps/internal/explorer/file/ExploreManager._filter Lnet/rim/device/apps/api/framework/file/FileSelectionFilter;
      // 22a: invokevirtual net/rim/device/apps/api/framework/file/FileSelectionFilter.isSelectFolderOn ()Z
      // 22d: ifeq 23c
      // 230: aload 1
      // 231: new net/rim/device/apps/internal/explorer/file/menu/ExploreMenuItem
      // 234: dup
      // 235: aload 0
      // 236: invokespecial net/rim/device/apps/internal/explorer/file/menu/ExploreMenuItem.<init> (Lnet/rim/device/apps/internal/explorer/file/ExploreManager;)V
      // 239: invokevirtual net/rim/device/api/ui/component/Menu.add (Lnet/rim/device/api/ui/MenuItem;)V
      // 23c: aload 0
      // 23d: aload 1
      // 23e: aload 3
      // 23f: iload 2
      // 240: invokespecial net/rim/device/apps/internal/explorer/file/ExploreManager.makeMenu (Lnet/rim/device/api/ui/component/Menu;Lnet/rim/device/apps/internal/explorer/file/FileConnectionHolder;I)V
      // 243: return
      // try (173 -> 175): 176 null
      // try (139 -> 171): 178 null
      // try (181 -> 183): 184 null
      // try (139 -> 171): 186 null
      // try (178 -> 179): 186 null
      // try (189 -> 191): 192 null
      // try (186 -> 187): 186 null
   }

   private final void makeMenu(Menu contextMenu, FileConnectionHolder selectedItem, int instance) {
      boolean defaultMenu = instance == 0;
      int mediaTypeFilter = this._filter.getMediaType();
      if (selectedItem instanceof FileItemField) {
         FileItemField selectedFileItem = (FileItemField)selectedItem;
         Verb[] verbs = new Object[0];
         ContextObject context = (ContextObject)(new Object());
         if (contextMenu instanceof Object) {
            ContextObject appContext = ((SystemEnabledMenu)contextMenu).getContext();
            if (appContext != null && appContext.getFlag(45)) {
               context.setFlag(45);
            }
         }

         context.put(2765042845091913199L, selectedFileItem.getFullPath());
         Verb defaultVerb = null;
         if (this._verbProvider != null) {
            defaultVerb = this._verbProvider.getVerbs(context, verbs);
         }

         boolean canAddFileToFolder = false;
         FileConnectionHolder currentPath = this.getCurrentView();
         if (currentPath instanceof FileItemField) {
            canAddFileToFolder = ((FileItemField)currentPath).canWrite();
            if (canAddFileToFolder && defaultMenu) {
               contextMenu.add(new AddFolderMenuItem((FileItemField)currentPath));
            }
         }

         if (this.navigateUp(false) && (defaultMenu || selectedItem instanceof UpAliasFileItemField)) {
            contextMenu.add(this._upMenuItem);
         }

         if (this._showAllMenuItems && canAddFileToFolder) {
            if (this._showAllMenuItems && mediaTypeFilter == 2 || mediaTypeFilter == 0) {
               VerbRepository verbRepository = VerbRepository.getVerbRepository(8855791131633157813L);
               if (verbRepository != null) {
                  Verb[] downloadVerbs = verbRepository.getVerbs(-201905085362485851L);
                  if (downloadVerbs != null) {
                     int i = downloadVerbs.length;

                     while (--i >= 0) {
                        Arrays.add(verbs, downloadVerbs[i]);
                     }
                  }
               }
            }

            if (this._showAllMenuItems && mediaTypeFilter == 1 || mediaTypeFilter == 0) {
               VerbRepository verbRepository = VerbRepository.getVerbRepository(8855791131633157813L);
               if (verbRepository != null) {
                  Verb[] downloadVerbs = verbRepository.getVerbs(-2145255983166432762L);
                  if (downloadVerbs != null) {
                     int i = downloadVerbs.length;

                     while (--i >= 0) {
                        Arrays.add(verbs, downloadVerbs[i]);
                     }
                  }
               }
            }
         }

         if (verbs != null && verbs.length > 0) {
            for (int idx = 0; idx < verbs.length; idx++) {
               Verb verb = verbs[idx];
               VerbMenuItem menuItem = (VerbMenuItem)(new Object(null, verb.getOrdering(), Integer.MAX_VALUE, verb, context));
               contextMenu.add(menuItem);
               if (verb == defaultVerb) {
                  contextMenu.setDefault(menuItem);
               }
            }
         }
      }

      int viewMode = this._gallery.getViewMode();

      for (int i = 0; i < 3; i++) {
         if (viewMode != i && (i != 2 || mediaTypeFilter == 2) && defaultMenu) {
            contextMenu.add(new ToggleViewMenuItem(this._gallery, this._rootView, i));
         }
      }
   }

   private final void restoreView(ExploreManager$PathStackElement savedView) {
      this._root = savedView._root;
      this.initializeView(savedView._view, savedView._filter);
      FolderList folderList = savedView._folderList;
      FileConnectionHolder fileItem = savedView._selectedItem;
      if (folderList != null && fileItem != null) {
         folderList.setSelectedItem(fileItem.getName());
         if (this.getFieldWithFocus() != folderList) {
            this.setFieldWithFocus(folderList);
         }
      }

      this._updateLayoutInvokeLater.setSavedView(savedView);
   }

   private final void initializeView(FileConnectionHolder path, FileSelectionFilter fileFilter) {
      if (!(path instanceof FileItemField)) {
         if (path instanceof LocalizedDirectoryItemField) {
            LocalizedDirectoryItemField root = (LocalizedDirectoryItemField)path;
            UnsortedReadableList shortcutItems = (UnsortedReadableList)(new Object());
            shortcutItems.loadFrom(root.getPaths());
            this._shortcuts.setCurrentView(path.getURL(), shortcutItems);
            this._gallery.setCurrentView(path.getURL(), null);
            if (this._pathStack.isEmpty()) {
               this._upAlias = null;
            }
         }
      } else {
         FileItemField fileItem = (FileItemField)path;
         String directory = fileItem.getFullPath();
         int sortProperty = this._rootView == 1 ? ExplorerOptions.getOptions().getFilelistSortProperty() : 1;
         Comparator comparator = FileItemComparator.getInstance(sortProperty);
         SortedReadableList shortcutList = (SortedReadableList)(new Object(comparator));
         SortedReadableList fileList = (SortedReadableList)(new Object(comparator));
         if (this._pathStack.isEmpty() && !fileItem.isRoot() && this._upAlias == null) {
            this._upAlias = new UpAliasFileItemField(this);
         }

         int i = this._globalAlias.length;

         while (--i >= 0) {
            AliasFileItemField alias = this._globalAlias[i];
            if (alias._entry.isActiveInDirectory(directory, fileFilter)) {
               alias.setPath(directory);
               shortcutList.elementAdded(null, alias);
            }
         }

         AliasFileEntry[] entries = ExplorerRegistry.getInstance().getAlias(directory);
         if (entries != null) {
            this.addAliasToList(directory, entries, fileFilter, shortcutList);
         }

         if (fileItem instanceof RootFileItemField) {
            RootFileItemField rootItem = (RootFileItemField)fileItem;
            this.addAliasToList(directory, rootItem.getAliases(), fileFilter, shortcutList);
         }

         if (this._upAlias != null) {
            FileItemField alias = null;
            String viewPath = null;
            boolean addUpAlias = true;
            ExploreManager$PathStackElement element = this.peekStack();
            if (element != null) {
               viewPath = element._view.getPath();
            }

            for (int ix = 0; ix < shortcutList.size(); ix++) {
               Object obj = shortcutList.getAt(ix);
               if (obj instanceof FileItemField) {
                  alias = (FileItemField)obj;
                  if (viewPath != null && alias.getPath().equals(viewPath)) {
                     addUpAlias = false;
                     break;
                  }
               }
            }

            if (addUpAlias && this.navigateUp(false, false, path)) {
               shortcutList.elementAdded(null, this._upAlias);
            }
         }

         this._gallery.setCurrentView(path.getURL(), fileList);
         this._shortcuts.setCurrentView(path.getURL(), shortcutList);
         this.populateDirectory(fileItem, shortcutList, fileList, fileFilter);
      }

      if (this._callback != null) {
         this._callback.pathSet(path);
      }

      this._view = path;
   }

   private final void initializeAliasEntries(int mediaType) {
      ExplorerRegistry registry = ExplorerRegistry.getInstance();
      this._globalAlias = new AliasFileItemField[0];
      AliasFileEntry[] globalEntries = registry.getGlobalAlias(mediaType);
      if (globalEntries != null) {
         int i = globalEntries.length;

         while (--i >= 0) {
            this.addGlobalAlias(globalEntries[i]);
         }
      }

      if (mediaType != 0) {
         AliasFileEntry[] defaultEntries = registry.getGlobalAlias(0);
         if (defaultEntries != null) {
            int i = defaultEntries.length;

            while (--i >= 0) {
               AliasFileEntry entry = defaultEntries[i];
               if (!Arrays.contains(globalEntries, entry)) {
                  this.addGlobalAlias(entry);
               }
            }
         }
      }
   }

   private final void populateDirectory(FileItemField fileItem, SortedReadableList shortcutList, SortedReadableList fileList, FileSelectionFilter filter) {
      String directory = fileItem.getFullPath();
      if (fileItem.isAlias() && directory.equals("/")) {
         Enumeration roots = FileSystemRegistry.listRoots();

         while (roots.hasMoreElements()) {
            String nextRoot = (String)roots.nextElement();
            FileItemField newRoot = new FileItemField(((StringBuffer)(new Object("/"))).append(nextRoot).toString());
            shortcutList.elementAdded(null, newRoot);
         }
      } else {
         if (this._openDirTask != null) {
            this._openDirTask.cancel();
         }

         this._openDirTask = new ExploreManager$OpenDirectoryWorker(this, fileItem, filter, shortcutList, fileList);
         this._openDirTask.run();
      }
   }

   private final boolean match(FileSelectionFilter matchFilter, FileItemField item) {
      FileSelectionFilter filter = matchFilter;
      int mediaType = filter.getMediaType();
      int selectionFilter = filter.getSelectFilter();
      Recognizer recognizer = filter.getRecognizer();
      boolean anyMediaType = mediaType == 0;
      boolean selectOnlyForwardUnlocked = (selectionFilter & 512) != 0;
      return item.isDirectory()
         || (anyMediaType || mediaType == item.getMediaType())
            && filter.isSelectFileOn()
            && (!selectOnlyForwardUnlocked || !item.isDRMForwardLocked())
            && (recognizer == null || recognizer.recognize(item.getDisplayName()));
   }

   @Override
   public final void invalidate() {
      this._gallery.invalidateThumbsToLoad();
      super.invalidate();
   }

   private final Verb getInvalidFileAlertVerb(String errorMsg) {
      if (this._invalidFileAlertVerb == null) {
         this._invalidFileAlertVerb = new ExploreManager$InvalidFileAlertVerb();
      }

      this._invalidFileAlertVerb._fileError = errorMsg;
      return this._invalidFileAlertVerb;
   }

   @Override
   protected final void onDisplay() {
      super.onDisplay();
      this.getScreen().getApplication().addFileSystemJournalListener(this);
   }

   private final boolean testUpdateForPath(String currentViewPath, FileSystemJournalEntry entry) {
      String path = entry.getPath();
      String oldPath = entry.getOldPath();
      if (path != null && oldPath != null) {
         String pathOfPath = FileUtilities.getPath(path);
         String pathOfOldPath = FileUtilities.getPath(oldPath);
         if (!StringUtilities.strEqualIgnoreCase(pathOfOldPath, currentViewPath, 1701707776)
            && !StringUtilities.strEqualIgnoreCase(pathOfPath, currentViewPath, 1701707776)) {
            return false;
         }

         this._app.invokeLater(new ExploreManager$UpdateFolderList(this, entry));
         return true;
      } else {
         return false;
      }
   }

   private final void addGlobalAlias(AliasFileEntry alias) {
      Arrays.add(this._globalAlias, new AliasFileItemField(alias));
   }

   @Override
   protected final void onUndisplay() {
      this.getScreen().getApplication().removeFileSystemJournalListener(this);
      super.onUndisplay();
   }

   public ExploreManager(ExploreCallback callback, Object ctx, boolean useTooltips, long style) {
      super(style);
      Object obj = ContextObject.get(ctx, -1002650280265073678L);
      int mediaType;
      if (obj instanceof Object) {
         this._filter = (FileSelectionFilter)obj;
         mediaType = this._filter.getMediaType();
      } else {
         mediaType = ContextObject.getIntegerData(ctx, 0) & 0xFF;
         obj = ContextObject.get(ctx, -409744358660961448L);
         Recognizer recognizer = null;
         if (obj instanceof Object) {
            recognizer = (Recognizer)obj;
         }

         if (mediaType != 0 || recognizer != null) {
            this._filter = (FileSelectionFilter)(new Object(mediaType, -2147481600, recognizer));
         }
      }

      Integer rootViewInteger = (Integer)ContextObject.get(ctx, 3941043584844673548L);
      if (rootViewInteger != null) {
         this._rootView = rootViewInteger;
      } else {
         Object fn = ContextObject.get(ctx, 2765042845091913199L);
         String filename = null;
         if (fn instanceof Object) {
            filename = (String)fn;
         }

         this._rootView = FileSelectionFilter.getDefaultRootViewForMediaType(mediaType, filename);
      }

      int defaultViewMode = mediaType == 1 ? 0 : 1;
      this._ctx = ContextObject.clone(ctx);
      ExplorerOptions options = ExplorerOptions.getOptions();
      this._callback = callback;
      this._shortcuts = new FolderList(this, 2, 1, false);
      this._gallery = new FolderList(this, defaultViewMode, options.getNumberOfColumns(), useTooltips);
      this._thumbnailFetcher = new ThumbnailFetcher(this._gallery);
      this.add(this._shortcuts);
      this.add((Field)(new Object()));
      this.add(this._gallery);
      this._exploreRoot = new LocalizedDirectoryItemField(0, false);
      this.initializeAliasEntries(mediaType);
      obj = ContextObject.get(ctx, 424670468422402792L);
      if (obj instanceof Object) {
         this._verbProvider = (VerbProvider)obj;
      }

      this._app = UiApplication.getUiApplication();
      if (this._filter == null) {
         this._filter = (FileSelectionFilter)(new Object());
      }

      FileConnectionHolder rootHolder;
      switch (this._rootView) {
         case 0:
            rootHolder = this._exploreRoot;
            this._root = this._exploreRoot;
            break;
         case 5:
            rootHolder = new LocalizedDirectoryItemField(this._rootView, this._filter.isSelectWriteable());
            this._root = (RootItem)rootHolder;
            break;
         default:
            this._mediaFolderListener = new MediaFolderListener(ExplorerResources.getStringArray(12)[this._rootView], mediaType, this._rootView, ctx);
            rootHolder = this._mediaFolderListener.getRootItem();
            this._root = this._mediaFolderListener.getRootItem();
      }

      obj = ContextObject.get(ctx, 2765042845091913199L);
      if (obj == null) {
         this.setCurrentView(rootHolder, this._filter);
      } else {
         String filename = null;
         String path;
         if (!(obj instanceof FileItemField)) {
            path = obj.toString();
            if (path.startsWith("file://")) {
               path = path.substring(7, path.length());
            }

            if (!FileUtilities.isDirectory(path)) {
               filename = FileUtilities.getName(path);
               path = FileUtilities.getPath(path);
            }

            this.setCurrentView(this._root.find(path), this._filter);
         } else {
            FileItemField item = (FileItemField)obj;
            path = item.getPath();
            if (!item.isDirectory()) {
               filename = item.getName();
            }

            this.setCurrentView(item, this._filter);
         }

         if (filename != null) {
            this._gallery.setSelectedItem(filename);
            this.setFieldWithFocus(this._gallery);
            Integer invokeMenuItem = (Integer)ContextObject.get(ctx, -8073278814961745892L);
            if (invokeMenuItem != null) {
               FileItemField fileItem = new FileItemField(((StringBuffer)(new Object())).append(path).append(filename).toString());
               if (fileItem != null && fileItem.canView()) {
                  MenuItem menuItem = this.getMenuItem(invokeMenuItem, fileItem);
                  if (menuItem != null) {
                     this._app.invokeLater(menuItem);
                  }
               }
            }
         }
      }

      this._myStoredUSN = FileSystemJournal.getNextUSN();
      this._showAllMenuItems = ContextObject.getFlag(ctx, 45);
      UiApplication app = UiApplication.getUiApplication();
      app.addFileSystemListener(this);
      options.addOptionsChangeListener(this);
      if (this._mediaFolderListener != null) {
         this._mediaFolderListener.setManager(this);
      }
   }

   private final ExploreManager$PathStackElement peekStack() {
      return this._pathStack.isEmpty() ? null : (ExploreManager$PathStackElement)this._pathStack.peek();
   }

   @Override
   protected final boolean keyChar(char character, int status, int time) {
      switch (character) {
         case '\b':
         case '\u007f':
            FileItemField file = null;
            Field field = this.getLeafFieldWithFocus();
            if (!(field instanceof FolderList)) {
               return false;
            } else {
               file = ((FolderList)field).getSelectedItem();
               if (file != null && file.canDelete() && !file.isAlias()) {
                  DeleteMenuItem deleteMenuItem = new DeleteMenuItem(file, null);
                  deleteMenuItem.run();
                  if (deleteMenuItem.getSuccess()) {
                     UnsortedReadableList containedList = (UnsortedReadableList)((FolderList)field).getList();
                     containedList.elementRemoved(null, file);
                     return true;
                  }
               }

               return false;
            }
         case '\n':
            Field field = this.getLeafFieldWithFocus();
            int instance = 0;
            if (field instanceof FolderList) {
               instance = ((FolderList)field).getSelectedIndex();
            }

            return this.invokeDefaultAction(instance);
         case '\u001b':
            if (this.popPath()) {
               return true;
            }
         default:
            return super.keyChar(character, status, time);
      }
   }

   @Override
   protected final boolean invokeAction(int action) {
      boolean handled = super.invokeAction(action);
      if (!handled) {
         switch (action) {
            case 1:
               Field field = this.getLeafFieldWithFocus();
               FileItemField item = null;
               int instance = 0;
               if (field instanceof FolderList) {
                  FolderList curList = (FolderList)field;
                  instance = curList.getSelectedIndex();
                  item = curList.getSelectedItem();
               }

               if (!(this._callback instanceof ExplorePopup) || !this._filter.isSelectFolderOn() || item != null && !item.isDirectory()) {
                  return this.invokeDefaultAction(instance);
               }

               return false;
         }
      }

      return handled;
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      int k = ExploreHotkeys.map(keycode);
      int folderCount = this._shortcuts.getFileCount();
      int pictureCount = this._gallery.getFileCount();
      switch (k) {
         case 167:
            return super.keyDown(keycode, time);
         case 168:
         default:
            if (folderCount > 0 || pictureCount > 0) {
               if (folderCount > 0) {
                  this._shortcuts.setSelectedIndex(0);
                  if (this.getFieldWithFocus() != this._shortcuts) {
                     this.setFieldWithFocus(this._shortcuts);
                  }
               }

               this._gallery.setSelectedIndex(0);
               this.updateLayout();
            }

            return true;
         case 169:
            if (folderCount > 0 || pictureCount > 0) {
               if (pictureCount > 0) {
                  this._gallery.setSelectedIndex(pictureCount);
                  if (this.getFieldWithFocus() != this._gallery) {
                     this.setFieldWithFocus(this._gallery);
                  }
               } else {
                  this._shortcuts.setSelectedIndex(folderCount);
               }

               this.updateLayout();
            }

            return true;
      }
   }

   private final boolean invokeDefaultAction(int instance) {
      MenuItem menuVerb = this.getScreen().getDefaultMenuItem(instance);
      if (menuVerb != null) {
         menuVerb.run();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final void setFieldWithFocus(Field child) {
      super.setFieldWithFocus(child);
      this.focusChanged(child, 1);
   }

   private final boolean navigateUp(boolean execute, boolean emptyStack, FileConnectionHolder currentPath) {
      boolean success = false;
      if (!(currentPath instanceof RootItem)) {
         if (execute) {
            FileConnectionHolder parentPath = this.getParent(currentPath);
            if (!emptyStack && parentPath.equals(this.peekStack()._view)) {
               ExploreManager$PathStackElement savedView = (ExploreManager$PathStackElement)this._pathStack.pop();
               this.restoreView(savedView);
            } else {
               this.pushPath(parentPath);
            }
         }

         success = true;
      } else if (!emptyStack) {
         if (execute) {
            this.popPath();
         }

         success = true;
      } else if (this._callback instanceof Object) {
         if (execute) {
            ((Screen)this._callback).close();
         }

         success = true;
      }

      return success;
   }

   static final void access$500(ExploreManager x0) {
      x0.updateLayout();
   }

   static final void access$1000(ExploreManager x0) {
      x0.updateLayout();
   }
}
