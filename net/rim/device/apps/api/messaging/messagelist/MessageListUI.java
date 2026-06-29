package net.rim.device.apps.api.messaging.messagelist;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.FilterProgress;
import net.rim.device.api.collection.LoadableCollection;
import net.rim.device.api.collection.LongKeyProviderAdaptor;
import net.rim.device.api.collection.NotificationSuspension;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.component.VariableHeightListField;
import net.rim.device.api.ui.component.VariableHeightListFieldCallback;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.framework.hotkeys.HotKeyCheck;
import net.rim.device.apps.api.framework.hotkeys.HotKeys;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.HotKeyProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.ModelViewListenerRegistry;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.DateSortKeyProviderIndirection;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.resources.MessageResources;
import net.rim.device.apps.api.messaging.util.FileMessageVerb;
import net.rim.device.apps.api.quickcontact.QuickContactList;
import net.rim.device.apps.api.ribbon.RibbonBanner;
import net.rim.device.apps.api.ribbon.indicators.NewMessageEventManager;
import net.rim.device.apps.api.search.BackgroundFilteringCollection;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.framework.ControllerUtilities;
import net.rim.device.apps.internal.api.quincy.QuincyManager;
import net.rim.device.apps.internal.messaging.MessageHotkeys;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.UiInternal;
import net.rim.vm.Array;

public final class MessageListUI
   extends AppsMainScreen
   implements VariableHeightListFieldCallback,
   CollectionListener,
   LoadableCollection,
   VerbFactory,
   HotKeyCheck,
   FocusChangeListener,
   GlobalEventListener,
   NotificationSuspension {
   private MessageBulkMarkOldManager _messageBulkMarkOldManager;
   private VariableHeightListField _listField;
   private Object _items;
   private DateSortedSeparatedMessageArray _sortedSeparatedItems;
   private SelectedIndexManager _selectedIndexManager = new SelectedIndexManager();
   private ContextObject _context;
   private Object _result;
   private int _exitAction;
   private Application _creationApp;
   private MessageListUI$OpenItemVerb _previousItemVerb = new MessageListUI$OpenItemVerb(this, 16863312, 1600);
   private MessageListUI$OpenItemVerb _nextItemVerb = new MessageListUI$OpenItemVerb(this, 16863488, 1500);
   private MessageListUI$OpenItemVerb _nextUnreadItemVerb = new MessageListUI$OpenItemVerb(this, 16863744, 1550);
   private DeleteSingleItemVerb _deleteSingleItemVerb = new DeleteSingleItemVerb(611472, 1000);
   private MessageListUI$OpenItemVerb _previousThreadItemVerb = new MessageListUI$OpenItemVerb(this, 16863760, -1);
   private MessageListUI$OpenItemVerb _nextThreadItemVerb = new MessageListUI$OpenItemVerb(this, 16863776, -1);
   private DeleteMultipleItemsVerb _deleteMultiselectedVerb = new DeleteMultipleItemsVerb(602517, 0, 1102);
   private FileMessageVerb _fileMessageMultiselectedVerb = new FileMessageVerb(602496, CommonResources.getResourceBundle(), 1358, null);
   private RangeActionVerb _markOpenedMultiselectedVerb = new RangeActionVerb(602501, 5803508244060051872L, false, -1, null, 0, 0, 1352);
   private RangeActionVerb _markUnopenedMultiselectedVerb = new RangeActionVerb(602503, -8629311385729242560L, false, -1, null, 0, 0, 1353);
   private RangeActionVerb _saveMultiselectedVerb = new RangeActionVerb(602512, -8570780006855731756L, false, -1, null, 0, 0, 1354);
   private MessageListUI$PickVerb _pickVerb = new MessageListUI$PickVerb(this);
   private MessageListUI$UpdateRunnable _updateRunnable = new MessageListUI$UpdateRunnable(this);
   private MessageListUI$MessageListUIInvokeLaterRunnable _invokeLaterRunnable = new MessageListUI$MessageListUIInvokeLaterRunnable(null);
   private Verb[] _verbs = new Object[0];
   private MessageListColumnPainter _columnPainter;
   private Verb _leaveScreenVerb = new MessageListUI$LeaveScreenVerb(this);
   private Field _statusBanner;
   private String _title;
   private boolean _ignoreKeyUp = true;
   private boolean _ignoreKeyRepeat = true;
   private int _lastKeyDownKeyCode = 0;
   private DateSeparator _topDateSeparator;
   private int _addressCardUID;
   private static final Tag STRIPED_MESSAGE_LIST_TAG = Tag.create("messagelist");
   private static final Tag PLAIN_MESSAGE_LIST_TAG = Tag.create("messagelist-plain");
   static final boolean STICKY_DATE_SEPARATOR = false;
   public static final long MESSAGE_LIST_FACTORY_REPOSITORY_GUID = 2729258854446987021L;
   private static boolean _isReducedKeyboard = InternalServices.isReducedFormFactor();
   public static final int EXIT_POP = 1;
   public static final int EXIT_TO_BACKGROUND = 2;
   public static final int EXIT_SEARCH = 3;
   public static final int EXIT_TO_RIBBON_AFTER_SEARCH = 4;
   private static LongKeyProviderAdaptor _longKeyProviderAdaptor = new DateSortKeyProviderIndirection();

   public final void setAddressCardUID(int addressCardUID) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final long getMessageListMergeCollectionID() {
      Long mergeCollectionIdLong = (Long)ContextObject.get(this._context, 8505215491443778230L);
      return mergeCollectionIdLong != null ? mergeCollectionIdLong : 0;
   }

   public final int getExitAction() {
      return this._exitAction;
   }

   public final void setExitAction(int exitAction) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   protected final void runOnCorrectEventThread(Runnable runnable) {
      synchronized (this._invokeLaterRunnable) {
         this._invokeLaterRunnable.setRunnable(runnable);
      }

      if (safeGetApplication() == this._creationApp && Application.isEventDispatchThread()) {
         runnable.run();
      } else {
         synchronized (this._invokeLaterRunnable) {
            if (this._invokeLaterRunnable.doneProcessing()) {
               this._invokeLaterRunnable.resetDoneProcessing();
               this._creationApp.invokeLater(this._invokeLaterRunnable);
            } else {
               this._invokeLaterRunnable.resetDoneProcessing();
            }
         }
      }
   }

   public final void updateColumnInfo() {
      if (this._listField != null) {
         Font listFieldFont = this._listField.getFont();
         this._columnPainter.updateColumnInformation(listFieldFont);
         this._listField.invalidate();
      }
   }

   protected final void updateRibbonTitle() {
      if (this._title != null) {
         ReadableList sublist = this._sortedSeparatedItems;
         if (sublist instanceof Object) {
            FilterProgress fp = (FilterProgress)sublist;
            if (fp.getProgress() == 100) {
               if (this._statusBanner != null) {
                  RibbonBanner ribbonBar = RibbonBanner.getInstance();
                  ribbonBar.setStatusBannerTitle(this._statusBanner, this._title);
                  if (this._exitAction == 3 || this._exitAction == 4) {
                     this.setExitAction(this._exitAction == 3 ? 1 : 2);
                  }
               }

               this._title = null;
            }
         }
      }
   }

   protected final void updateView() {
      this.updateRibbonTitle();
      this._updateRunnable.setNumEntries(this._sortedSeparatedItems.size());
      this.runOnCorrectEventThread(this._updateRunnable);
   }

   protected final void updateElement(Object itemThatHasChanged) {
      this.updateRibbonTitle();
      int visibleRow = this.isVisible(itemThatHasChanged);
      if (visibleRow >= 0) {
         this._updateRunnable.setItemIndex(visibleRow);
         this.runOnCorrectEventThread(this._updateRunnable);
      }
   }

   public final void loadFrom(Object collection, boolean optionsChanged) {
      synchronized (FolderHierarchies.getLockObject()) {
         Object oldSelectedObject = this._selectedIndexManager.getSelectedObject();
         if (this._sortedSeparatedItems != null) {
            this._sortedSeparatedItems.removeCollectionListener(this);
            ((CollectionEventSource)this._items).removeCollectionListener(this._sortedSeparatedItems);
         }

         this._items = collection;
         this._sortedSeparatedItems = new DateSortedSeparatedMessageArray(102, _longKeyProviderAdaptor);
         this._sortedSeparatedItems.addCollectionListener(new Object(this));
         ((CollectionEventSource)this._items).addCollectionListener(new Object(this._sortedSeparatedItems));
         this._sortedSeparatedItems.loadFrom(this._items);
         if (this._listField != null) {
            this.delete(this._listField);
         }

         this._listField = new MessageListVariableHeightListField(this, this._sortedSeparatedItems.size(), 2);
         this._listField.setId("message");
         this._listField.setCallback(this);
         this._listField.setFocusListener(this);
         int listSeparatorAppearance = MessageListOptions.getOptions().getListSeparatorAppearance();
         this._listField.setTag(listSeparatorAppearance == 1 ? STRIPED_MESSAGE_LIST_TAG : PLAIN_MESSAGE_LIST_TAG);
         if (optionsChanged) {
            this.updateColumnInfo();
         }

         this.getMainManager().setVerticalQuantization(this._columnPainter.getVerticalQuantization());
         this.add(this._listField);
         this._selectedIndexManager.setParameters(this._listField, this._sortedSeparatedItems);
         if (oldSelectedObject != null) {
            long targetDate = _longKeyProviderAdaptor.getLongKey(oldSelectedObject);
            int newIndex = this._sortedSeparatedItems.getIndex(targetDate, false);
            if (newIndex >= 0) {
               this._selectedIndexManager.setSelectedIndex(newIndex);
            }
         }

         this.updateRibbonTitle();
         this.markNewMessages();
      }
   }

   public final void updateLastSeenTimeStamp() {
      this._messageBulkMarkOldManager.markLastSeenTimeStamp();
   }

   public final void initiateBulkMarkOldOperation() {
      this._messageBulkMarkOldManager.perform();
   }

   public final void initiateRemoveFlagsOperation() {
      if (this._addressCardUID > 0) {
         NewMessageEventManager.removeFlags(this._addressCardUID);
      } else {
         NewMessageEventManager.purgeFlags();
      }
   }

   protected final int isVisible(Object item) {
      if (this._listField != null && this._listField.getManager() != null) {
         int firstVisibleItem = getFirstVisibleRow(this._listField);
         int lastVisibleItem = getLastVisibleRow(this._listField);
         synchronized (FolderHierarchies.getLockObject()) {
            int size = this._sortedSeparatedItems.size();
            if (lastVisibleItem >= size) {
               lastVisibleItem = size - 1;
            }

            for (int i = firstVisibleItem; i <= lastVisibleItem; i++) {
               if (this._sortedSeparatedItems.getAt(i) == item) {
                  return i;
               }
            }

            return -1;
         }
      } else {
         this.logBadState(this._listField == null ? "null field" : "null manager");
         return -1;
      }
   }

   public final Object run(Object context) {
      this._context = ContextObject.castOrCreate(context);
      this._context.put(-2846768035584909703L, this);
      this._context.put(-7922982350060060892L, this);
      this._context.put(244, "messages_index");
      UiApplication.getUiApplication().pushScreen(this);
      return this._result;
   }

   final Object getItem(int index) {
      Object item = null;
      synchronized (FolderHierarchies.getLockObject()) {
         if (index < this._sortedSeparatedItems.size()) {
            try {
               item = this._sortedSeparatedItems.getAt(index);
            } finally {
               return item;
            }
         }

         return item;
      }
   }

   final boolean overrideWithDateSeparator(VariableHeightListField listField, int index, Object item) {
      return false;
   }

   @Override
   public final void drawListRow(VariableHeightListField listField, Graphics graphics, int index, int y, int width) {
      Object item = this.getItem(listField, index);
      if (item != null) {
         this._columnPainter.drawRow(listField, graphics, y, width, item);
      }
   }

   @Override
   public final int getRowHeight(VariableHeightListField listField, int index) {
      Object item = this.getItem(listField, index);
      return item != null ? this._columnPainter.getRowHeight(item) : 0;
   }

   @Override
   public final Object get(VariableHeightListField listField, int index) {
      return this.getItem(listField, index);
   }

   @Override
   public final int indexOfList(VariableHeightListField listField, String prefix, int start) {
      return -1;
   }

   @Override
   public final int getPreferredWidth(VariableHeightListField listField) {
      return 10;
   }

   @Override
   public final void suspendNotification(Object context) {
      if (this._sortedSeparatedItems instanceof Object) {
         this._sortedSeparatedItems.suspendNotification(context);
      }
   }

   @Override
   public final void resumeNotification(Object context) {
      if (this._sortedSeparatedItems instanceof Object) {
         this._sortedSeparatedItems.resumeNotification(context);
      }
   }

   @Override
   public final void reset(Collection collection) {
      this._selectedIndexManager.updateSelectedIndex();
      this.markNewMessages();
      this.updateView();
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      this._selectedIndexManager.updateSelectedIndex();
      this._messageBulkMarkOldManager.addMessage(element);
      this.updateView();
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (oldElement == newElement) {
         this.updateElement(newElement);
      } else {
         this.updateView();
      }
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      this._selectedIndexManager.elementRemoved(element);
      this._selectedIndexManager.updateSelectedIndex();
      this._messageBulkMarkOldManager.removeMessage(element);
      this.updateView();
   }

   @Override
   public final void focusChanged(Field field, int action) {
      this._selectedIndexManager.focusChanged(field, action);
   }

   @Override
   public final void loadFrom(Object collection) {
      this.loadFrom(collection, false);
   }

   @Override
   public final Verb[] getVerbs(Object context) {
      Array.resize(this._verbs, 4);
      int numberOfVerbsNeeded = 0;
      RIMModel model = null;
      RIMModel modelCurrentlyOperatedOn = (RIMModel)ContextObject.get(context, -321822713458159100L);
      boolean scanIncludingCurrentSelection = ContextObject.getFlag(context, 100);
      boolean nextItemInMenu = false;
      boolean prevItemInMenu = false;
      int index = this.checkFolderForItem(635678369939227345L, true, false, false, scanIncludingCurrentSelection, modelCurrentlyOperatedOn);
      if (index != -1) {
         this._nextItemVerb.setItem(index, this._context);
         this._verbs[numberOfVerbsNeeded++] = this._nextItemVerb;
         nextItemInMenu = true;
      }

      index = this.checkFolderForItem(635678369939227345L, false, false, false, scanIncludingCurrentSelection, modelCurrentlyOperatedOn);
      if (index != -1) {
         this._previousItemVerb.setItem(index, this._context);
         this._verbs[numberOfVerbsNeeded++] = this._previousItemVerb;
         prevItemInMenu = true;
      }

      if (nextItemInMenu || prevItemInMenu) {
         this._nextUnreadItemVerb.setItem(-1, this._context);
         this._verbs[numberOfVerbsNeeded++] = this._nextUnreadItemVerb;
      }

      RIMModel var10 = this.getSelectedItem();
      if (modelCurrentlyOperatedOn != null) {
         var10 = modelCurrentlyOperatedOn;
      }

      this._deleteSingleItemVerb.setParameters(var10, this._context);
      this._verbs[numberOfVerbsNeeded++] = this._deleteSingleItemVerb;
      Array.resize(this._verbs, numberOfVerbsNeeded);
      return this._verbs;
   }

   @Override
   public final boolean invokeHotkey(int hotkeyID, Object context) {
      RIMModel modelCurrentlyOperatedOn = (RIMModel)ContextObject.get(context, -321822713458159100L);
      boolean scanIncludingCurrentSelection = ContextObject.getFlag(context, 100);
      MessageListUI$OpenItemVerb item_verb;
      int index;
      switch (hotkeyID) {
         case 143:
            item_verb = this._nextItemVerb;
            index = this.checkFolderForItem(635678369939227345L, true, false, false, scanIncludingCurrentSelection, modelCurrentlyOperatedOn);
            break;
         case 144:
            item_verb = this._previousItemVerb;
            index = this.checkFolderForItem(635678369939227345L, false, false, false, scanIncludingCurrentSelection, modelCurrentlyOperatedOn);
            break;
         case 146:
            item_verb = this._nextUnreadItemVerb;
            index = this.checkItemIndex(278390328807340479L, true, scanIncludingCurrentSelection, modelCurrentlyOperatedOn);
            break;
         case 185:
            item_verb = this._nextThreadItemVerb;
            index = this.checkItemIndex(-2415955221176628574L, true, scanIncludingCurrentSelection, modelCurrentlyOperatedOn);
            break;
         case 186:
            item_verb = this._previousThreadItemVerb;
            index = this.checkItemIndex(-2415955221176628574L, false, scanIncludingCurrentSelection, modelCurrentlyOperatedOn);
            break;
         default:
            return false;
      }

      if (index != -1) {
         item_verb.setItem(index, this._context);
         item_verb.invoke(this._context);
         return true;
      }

      if (index == -1 && hotkeyID != 185 && hotkeyID != 186) {
         UiApplication uiApp = UiApplication.getUiApplication();
         uiApp.popScreen(uiApp.getActiveScreen());
      }

      return true;
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -4394903006263251010L) {
         this.invalidate();
      } else {
         if (guid != -4363772166259176210L) {
            if (guid == -1152649409323516530L && object0 instanceof ShowMessageAppVerb) {
               ShowMessageAppVerb verbToInvoke = (ShowMessageAppVerb)object0;
               Object contextParameter = object1;
               this._creationApp.invokeLater(new MessageListUI$2(this, verbToInvoke, contextParameter));
            }
         } else if (object0 instanceof Object) {
            RIMModel modelToOpen = (RIMModel)object0;
            if (ModelViewListenerRegistry.isViewerUp(0, modelToOpen, this._context)) {
               return;
            }

            ContextObject contextObject = ContextObject.clone(this._context);
            if (object1 instanceof Object) {
               ContextObject co = (ContextObject)object1;
               Object selectedItem = ContextObject.get(co, 250);
               if (selectedItem != null) {
                  ContextObject.put(contextObject, 250, selectedItem);
               }
            }

            contextObject.put(-321822713458159100L, modelToOpen);
            if (ContextObject.getFlag(object1, 64)) {
               contextObject.setFlag(64);
            }

            if (modelToOpen instanceof Object) {
               ActionProvider actionProvider = (ActionProvider)modelToOpen;
               this._creationApp.invokeLater(new MessageListUI$1(this, modelToOpen));
               actionProvider.perform(6099736323056465049L, contextObject);
               return;
            }
         }
      }
   }

   @Override
   protected final void verbInvoked(Verb verb, Object context, Object result) {
   }

   private final Object getItem(VariableHeightListField listField, int index) {
      Object item = this.getItem(index);
      if (item != null && this.overrideWithDateSeparator(listField, index, item)) {
         long date = _longKeyProviderAdaptor.getLongKey(item);
         this._topDateSeparator.setShowArrow(true);
         this._topDateSeparator.setDate(date);
         item = this._topDateSeparator;
      }

      return item;
   }

   private final Object getSelectedItem() {
      Object object = null;
      synchronized (FolderHierarchies.getLockObject()) {
         int index = this._selectedIndexManager.getSelectedIndex();
         if (index != -1) {
            object = this.getItem(this._listField, index);
         }

         return object;
      }
   }

   private final Verb makeMultiSelectContextMenu(SystemEnabledMenu menu, int[] selectedIndices) {
      this._context.put(-4502157452528129690L, selectedIndices);
      VerbRepository verbRepository = VerbRepository.getVerbRepository(-2776786416652883258L);
      menu.add(verbRepository.getVerbs(null));
      RIMModel[] selectedItems = RangeActionVerb.getSelectedItems(selectedIndices, this._sortedSeparatedItems);
      this._context.put(-2364888284414893834L, selectedIndices);
      this._deleteMultiselectedVerb.setParameters(selectedItems, this._sortedSeparatedItems, this._context);
      menu.add(this._deleteMultiselectedVerb);
      Verb defaultVerb = this._deleteMultiselectedVerb;
      this._fileMessageMultiselectedVerb.setParameters(selectedItems, this._sortedSeparatedItems);
      if (this._fileMessageMultiselectedVerb.canFile()) {
         menu.add(this._fileMessageMultiselectedVerb);
         defaultVerb = this._fileMessageMultiselectedVerb;
      }

      if (RangeActionVerb.performMultiSelectAction(selectedItems, -5544992959212130441L, this._context)) {
         this._markOpenedMultiselectedVerb.setParameters(selectedItems, this._sortedSeparatedItems, this._context);
         menu.add(this._markOpenedMultiselectedVerb);
      }

      if (RangeActionVerb.performMultiSelectAction(selectedItems, 477896226347912237L, this._context)) {
         this._markUnopenedMultiselectedVerb.setParameters(selectedItems, this._sortedSeparatedItems, this._context);
         menu.add(this._markUnopenedMultiselectedVerb);
      }

      if (RangeActionVerb.performMultiSelectAction(selectedItems, -1042102706756508802L, this._context)) {
         this._saveMultiselectedVerb.setParameters(selectedItems, this._sortedSeparatedItems, this._context);
         menu.add(this._saveMultiselectedVerb);
      }

      return defaultVerb;
   }

   @Override
   public final void onExposed() {
      this._ignoreKeyUp = true;
      this._ignoreKeyRepeat = true;
   }

   private final void markNewMessages() {
      int size = this._sortedSeparatedItems.size();
      if (size >= 1) {
         for (int i = 0; i < size; i++) {
            Object currentObj = this._sortedSeparatedItems.getAt(i);
            if (currentObj != null && !(currentObj instanceof NoMessagesBar) && !(currentObj instanceof DateSeparator)) {
               this._messageBulkMarkOldManager.addMessage(currentObj);
            }
         }
      }
   }

   @Override
   protected final void applyFont() {
      super.applyFont();
      this.updateColumnInfo();
      this.getMainManager().setVerticalQuantization(this._columnPainter.getVerticalQuantization());
   }

   private final int checkFolderForItem(
      long action, boolean upward, boolean startFromBottom, boolean startFromTop, boolean scanIncludingCurrentSelection, RIMModel modelToStartFrom
   ) {
      boolean foundOne = false;
      int itemToCheck = 0;
      synchronized (FolderHierarchies.getLockObject()) {
         int listFieldSize = this._listField.getSize();
         if (startFromBottom) {
            itemToCheck = listFieldSize;
            foundOne = true;
         } else if (startFromTop) {
            itemToCheck = 0;
            foundOne = true;
         } else if (modelToStartFrom != null) {
            itemToCheck = this._sortedSeparatedItems.getIndex(modelToStartFrom);
            if (itemToCheck >= 0) {
               foundOne = true;
            }
         }

         if (!foundOne) {
            itemToCheck = this._selectedIndexManager.getSelectedIndex();
            if (scanIncludingCurrentSelection && modelToStartFrom != null) {
               if (!upward && itemToCheck > 0 && itemToCheck < listFieldSize - 1) {
                  itemToCheck--;
               } else if (upward && itemToCheck == listFieldSize - 1) {
                  itemToCheck++;
               }
            }
         }

         foundOne = false;
         int size = this._sortedSeparatedItems.size();

         do {
            if (upward) {
               itemToCheck--;
            } else {
               itemToCheck++;
            }

            if (itemToCheck <= 0 || itemToCheck >= size) {
               break;
            }

            RIMModel model = (RIMModel)this._sortedSeparatedItems.getAt(itemToCheck);
            if (model instanceof Object) {
               ActionProvider actionProvider = (ActionProvider)model;
               ContextObject context = null;
               if (action == -2415955221176628574L && modelToStartFrom != null) {
                  context = (ContextObject)(new Object());
                  ContextObject.put(context, -321822713458159100L, modelToStartFrom);
               }

               foundOne = actionProvider.perform(action, context);
            }
         } while (!foundOne);
      }

      return foundOne ? itemToCheck : -1;
   }

   @Override
   protected final boolean keyRepeat(int keycode, int time) {
      if (this._ignoreKeyRepeat) {
         return true;
      } else {
         char key = Keypad.map(keycode);
         if (key == ' ') {
            return true;
         } else {
            return Phone.getInstance().isActive() && RadioInfo.getNetworkType() != 4 ? true : super.keyRepeat(keycode, time);
         }
      }
   }

   @Override
   protected final boolean keyControl(char ch, int status, int time) {
      switch (ch) {
         case '\u0082':
            return super.keyControl(ch, status, time);
         case '\u0083':
         case '\u0084':
         default:
            return true;
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (((UiApplication)this._creationApp).isPaintingSuspended()) {
         return false;
      }

      if (super.keyChar(key, status, time)) {
         return true;
      }

      if (key == Keypad.map(this._lastKeyDownKeyCode) && key != ' ') {
         QuickContactList.getInstance();
         if (QuickContactList.isValidQuickContactKey(this._lastKeyDownKeyCode)) {
            return true;
         }
      }

      switch (key) {
         case '\u001b':
            this._leaveScreenVerb.invoke(null);
            return true;
         case ' ':
            int direction = (status & 2) == 0 ? 1 : -1;
            this.dispatchTrackwheelEvent(519, direction, 1, time);
            return true;
         default:
            return false;
      }
   }

   @Override
   protected final boolean keyCharUnhandled(char key, int status, int time) {
      return key == '\n' ? true : super.keyCharUnhandled(key, status, time);
   }

   private static final Verb checkForALTSearchKey(int key, int status) {
      char searchKeyChar = UiInternal.map(key, status & -2);
      if (_isReducedKeyboard) {
         searchKeyChar = (char)key;
      }

      searchKeyChar = Character.toUpperCase(searchKeyChar);
      return HotKeys.getVerb(7, searchKeyChar);
   }

   @Override
   protected final boolean handleSendKey() {
      Object model = this.getSelectedItem();
      return ControllerUtilities.invokeSendKeyVerb(model, this._context);
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      if (Keypad.key(keycode) == 21 && ControllerUtilities.invokeApplicationKeyVerb(this.getSelectedItem())) {
         return true;
      }

      this._lastKeyDownKeyCode = keycode;
      if (this._ignoreKeyUp) {
         this._ignoreKeyUp = false;
      }

      if (this._ignoreKeyRepeat) {
         this._ignoreKeyRepeat = false;
      }

      return ((UiApplication)this._creationApp).isPaintingSuspended() ? false : super.keyDown(keycode, time);
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         this._ignoreKeyUp = true;
         this._ignoreKeyRepeat = true;
      } else {
         this.haltFiltering();
         this.unregisterStatusBanner();
      }
   }

   @Override
   protected final void onObscured() {
      this._ignoreKeyUp = true;
      this._ignoreKeyRepeat = true;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final boolean keyUp(int keycode, int time) {
      if (this._ignoreKeyUp) {
         return true;
      }

      this._lastKeyDownKeyCode = 0;
      Object savedSelectedItem = this._context.get(250);
      boolean var20 = false /* VF: Semaphore variable */;

      int hotk;
      label358: {
         label357: {
            int index;
            label356: {
               int k;
               label355: {
                  label354: {
                     label353: {
                        label352: {
                           label351: {
                              label350: {
                                 label349: {
                                    label348: {
                                       label347: {
                                          label346: {
                                             boolean var17;
                                             label345: {
                                                label344: {
                                                   label343: {
                                                      label342: {
                                                         label341: {
                                                            label340: {
                                                               label339: {
                                                                  label338: {
                                                                     label337: {
                                                                        label336: {
                                                                           label335: {
                                                                              try {
                                                                                 var20 = true;
                                                                                 char keyPress = UiInternal.map(keycode);
                                                                                 int status = Keypad.status(keycode);
                                                                                 int key = Keypad.key(keycode);
                                                                                 RIMModel selectedModel = null;
                                                                                 Object selectedObject = null;
                                                                                 Verb verb = null;
                                                                                 if (Keypad.getAltedChar(keyPress) == 127) {
                                                                                    keyPress = 127;
                                                                                 }

                                                                                 boolean isAlted = (status & 1) != 0;
                                                                                 int[] selectedIndices = this._listField.getSelection();
                                                                                 if (selectedIndices.length > 1) {
                                                                                    this._context.put(-4502157452528129690L, selectedIndices);
                                                                                    switch (keyPress) {
                                                                                       case '\u007f':
                                                                                          this._deleteMultiselectedVerb
                                                                                             .setParameters(
                                                                                                selectedIndices, this._sortedSeparatedItems, this._context
                                                                                             );
                                                                                          this._result = this._deleteMultiselectedVerb.invoke(this._context);
                                                                                          k = 1;
                                                                                          var20 = false;
                                                                                          break label344;
                                                                                    }

                                                                                    k = MessageHotkeys.map(keycode);
                                                                                    index = -1;
                                                                                    switch (k) {
                                                                                       case 141:
                                                                                          this._selectedIndexManager.setSelectedIndex(0);
                                                                                          hotk = 1;
                                                                                          var20 = false;
                                                                                          break label343;
                                                                                       case 142:
                                                                                          this._selectedIndexManager
                                                                                             .setSelectedIndex(this._listField.getSize() - 1);
                                                                                          hotk = 1;
                                                                                          var20 = false;
                                                                                          break label342;
                                                                                       case 143:
                                                                                          this._selectedIndexManager
                                                                                             .setSelectedIndex(
                                                                                                this._sortedSeparatedItems
                                                                                                   .getNextDateIndex(
                                                                                                      this._selectedIndexManager.getSelectedIndex()
                                                                                                   )
                                                                                             );
                                                                                          hotk = 1;
                                                                                          var20 = false;
                                                                                          break label341;
                                                                                       case 144:
                                                                                          this._selectedIndexManager
                                                                                             .setSelectedIndex(
                                                                                                this._sortedSeparatedItems
                                                                                                   .getPreviousDateIndex(
                                                                                                      this._selectedIndexManager.getSelectedIndex()
                                                                                                   )
                                                                                             );
                                                                                          hotk = 1;
                                                                                          var20 = false;
                                                                                          break label338;
                                                                                       case 146:
                                                                                          this.checkItemIndex(278390328807340479L, true, false, null);
                                                                                          hotk = 1;
                                                                                          var20 = false;
                                                                                          break label340;
                                                                                       case 151:
                                                                                          this.checkItemIndex(-4201671119995560115L, true, false, null);
                                                                                          hotk = 1;
                                                                                          var20 = false;
                                                                                          break label339;
                                                                                       case 153:
                                                                                          this._fileMessageMultiselectedVerb
                                                                                             .setParameters(selectedIndices, this._sortedSeparatedItems);
                                                                                          if (!this._fileMessageMultiselectedVerb.canFile()) {
                                                                                             hotk = 0;
                                                                                             var20 = false;
                                                                                             break label337;
                                                                                          }

                                                                                          this._result = this._fileMessageMultiselectedVerb
                                                                                             .invoke(this._context);
                                                                                          hotk = 1;
                                                                                          var20 = false;
                                                                                          break label336;
                                                                                    }

                                                                                    if ((status & 1) != 0) {
                                                                                       verb = checkForALTSearchKey(key, status);
                                                                                    }

                                                                                    if (verb == null) {
                                                                                       verb = HotKeys.getVerb(1, keyPress);
                                                                                    }

                                                                                    if (verb != null) {
                                                                                       this._result = verb.invoke(this._context);
                                                                                       hotk = 1;
                                                                                       var20 = false;
                                                                                       break label335;
                                                                                    }

                                                                                    var20 = false;
                                                                                 } else {
                                                                                    selectedObject = this.getSelectedItem();
                                                                                    if (selectedObject instanceof Object) {
                                                                                       selectedModel = selectedObject;
                                                                                       this._context.put(250, selectedModel);
                                                                                    }

                                                                                    if (_isReducedKeyboard && !isAlted) {
                                                                                       k = 0;
                                                                                       index = 0;
                                                                                       switch (Keypad.key(keycode)) {
                                                                                          case 66:
                                                                                             k = 1;
                                                                                             break;
                                                                                          case 71:
                                                                                             this.invokeDefaultMenuItem(0);
                                                                                             hotk = 1;
                                                                                             var20 = false;
                                                                                             break label358;
                                                                                          case 77:
                                                                                             k = 1;
                                                                                             index = 1;
                                                                                             break;
                                                                                          case 84:
                                                                                             k = -1;
                                                                                             break;
                                                                                          case 85:
                                                                                             k = -1;
                                                                                             index = 1;
                                                                                       }

                                                                                       if (k != 0) {
                                                                                          this.dispatchTrackwheelEvent(519, k, index, time);
                                                                                          hotk = 1;
                                                                                          var20 = false;
                                                                                          break label357;
                                                                                       }
                                                                                    }

                                                                                    switch (keyPress) {
                                                                                       case '\n':
                                                                                          this.invokeDefaultMenuItem(0);
                                                                                          k = 1;
                                                                                          var20 = false;
                                                                                          break label355;
                                                                                       case '\u007f':
                                                                                          if (selectedModel != null) {
                                                                                             boolean dateSelected = selectedModel instanceof DateSeparator;
                                                                                             if (!dateSelected && !(selectedModel instanceof NoMessagesBar)) {
                                                                                                this._deleteSingleItemVerb
                                                                                                   .setParameters(selectedModel, this._context);
                                                                                                this._deleteSingleItemVerb.invoke(this._context);
                                                                                                index = 1;
                                                                                                var20 = false;
                                                                                                break label356;
                                                                                             }
                                                                                          }
                                                                                    }

                                                                                    k = MessageHotkeys.map(keycode);
                                                                                    index = -1;
                                                                                    switch (k) {
                                                                                       case 141:
                                                                                          this._selectedIndexManager.setSelectedIndex(0);
                                                                                          hotk = 1;
                                                                                          var20 = false;
                                                                                          break label354;
                                                                                       case 142:
                                                                                          this._selectedIndexManager
                                                                                             .setSelectedIndex(this._listField.getSize() - 1);
                                                                                          hotk = 1;
                                                                                          var20 = false;
                                                                                          break label353;
                                                                                       case 143:
                                                                                          this._selectedIndexManager
                                                                                             .setSelectedIndex(
                                                                                                this._sortedSeparatedItems
                                                                                                   .getNextDateIndex(
                                                                                                      this._selectedIndexManager.getSelectedIndex()
                                                                                                   ),
                                                                                                true
                                                                                             );
                                                                                          hotk = 1;
                                                                                          var20 = false;
                                                                                          break label352;
                                                                                       case 144:
                                                                                          this._selectedIndexManager
                                                                                             .setSelectedIndex(
                                                                                                this._sortedSeparatedItems
                                                                                                   .getPreviousDateIndex(
                                                                                                      this._selectedIndexManager.getSelectedIndex()
                                                                                                   ),
                                                                                                true
                                                                                             );
                                                                                          hotk = 1;
                                                                                          var20 = false;
                                                                                          break label349;
                                                                                       case 146:
                                                                                          this.checkItemIndex(278390328807340479L, true, false, null);
                                                                                          hotk = 1;
                                                                                          var20 = false;
                                                                                          break label351;
                                                                                       case 151:
                                                                                          this.checkItemIndex(-4201671119995560115L, true, false, null);
                                                                                          hotk = 1;
                                                                                          var20 = false;
                                                                                          break label350;
                                                                                       case 185:
                                                                                          this.checkItemIndex(
                                                                                             -2415955221176628574L, true, false, (RIMModel)selectedModel
                                                                                          );
                                                                                          hotk = 1;
                                                                                          var20 = false;
                                                                                          break label348;
                                                                                       case 186:
                                                                                          this.checkItemIndex(
                                                                                             -2415955221176628574L, false, false, (RIMModel)selectedModel
                                                                                          );
                                                                                          hotk = 1;
                                                                                          var20 = false;
                                                                                          break label347;
                                                                                    }

                                                                                    if (isAlted) {
                                                                                       verb = checkForALTSearchKey(key, status);
                                                                                    }

                                                                                    if (verb == null) {
                                                                                       verb = HotKeys.getVerb(1, (char)(_isReducedKeyboard ? key : keyPress));
                                                                                    }

                                                                                    if (verb != null) {
                                                                                       this._result = verb.invoke(this._context);
                                                                                       hotk = 1;
                                                                                       var20 = false;
                                                                                       break label346;
                                                                                    }

                                                                                    if (selectedModel == null) {
                                                                                       var20 = false;
                                                                                    } else {
                                                                                       hotk = MessageHotkeys.map(keycode);
                                                                                       if (hotk == 0) {
                                                                                          var20 = false;
                                                                                       } else if (hotk == 65535) {
                                                                                          var20 = false;
                                                                                       } else {
                                                                                          if (selectedModel instanceof Object) {
                                                                                             HotKeyProvider h = (HotKeyProvider)selectedModel;
                                                                                             this._result = h.invokeHotkey(this._context, hotk);
                                                                                             var17 = this._result != null;
                                                                                             var20 = false;
                                                                                             break label345;
                                                                                          }

                                                                                          var20 = false;
                                                                                       }
                                                                                    }
                                                                                 }
                                                                              } finally {
                                                                                 if (var20) {
                                                                                    this.endSelect(savedSelectedItem);
                                                                                 }
                                                                              }

                                                                              this.endSelect(savedSelectedItem);
                                                                              return false;
                                                                           }

                                                                           this.endSelect(savedSelectedItem);
                                                                           return (boolean)hotk;
                                                                        }

                                                                        this.endSelect(savedSelectedItem);
                                                                        return (boolean)hotk;
                                                                     }

                                                                     this.endSelect(savedSelectedItem);
                                                                     return (boolean)hotk;
                                                                  }

                                                                  this.endSelect(savedSelectedItem);
                                                                  return (boolean)hotk;
                                                               }

                                                               this.endSelect(savedSelectedItem);
                                                               return (boolean)hotk;
                                                            }

                                                            this.endSelect(savedSelectedItem);
                                                            return (boolean)hotk;
                                                         }

                                                         this.endSelect(savedSelectedItem);
                                                         return (boolean)hotk;
                                                      }

                                                      this.endSelect(savedSelectedItem);
                                                      return (boolean)hotk;
                                                   }

                                                   this.endSelect(savedSelectedItem);
                                                   return (boolean)hotk;
                                                }

                                                this.endSelect(savedSelectedItem);
                                                return (boolean)k;
                                             }

                                             this.endSelect(savedSelectedItem);
                                             return var17;
                                          }

                                          this.endSelect(savedSelectedItem);
                                          return (boolean)hotk;
                                       }

                                       this.endSelect(savedSelectedItem);
                                       return (boolean)hotk;
                                    }

                                    this.endSelect(savedSelectedItem);
                                    return (boolean)hotk;
                                 }

                                 this.endSelect(savedSelectedItem);
                                 return (boolean)hotk;
                              }

                              this.endSelect(savedSelectedItem);
                              return (boolean)hotk;
                           }

                           this.endSelect(savedSelectedItem);
                           return (boolean)hotk;
                        }

                        this.endSelect(savedSelectedItem);
                        return (boolean)hotk;
                     }

                     this.endSelect(savedSelectedItem);
                     return (boolean)hotk;
                  }

                  this.endSelect(savedSelectedItem);
                  return (boolean)hotk;
               }

               this.endSelect(savedSelectedItem);
               return (boolean)k;
            }

            this.endSelect(savedSelectedItem);
            return (boolean)index;
         }

         this.endSelect(savedSelectedItem);
         return (boolean)hotk;
      }

      this.endSelect(savedSelectedItem);
      return (boolean)hotk;
   }

   @Override
   protected final boolean invokeAction(int action) {
      return !(this.getSelectedItem() instanceof DateSeparator) && this._listField.getSelection().length <= 1 ? this.invokeDefaultMenuItem(0) : false;
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      RIMModel selectedModel = null;
      Object selectedElement = null;
      Verb defaultVerb = null;
      boolean oldBackgroundFlag = this._context.getFlag(96);
      this._context.setFlag(96);
      int[] selectedIndices = this._listField.getSelection();
      if (instance == 65536) {
         if (selectedIndices.length <= 1) {
            selectedElement = this.getSelectedItem();
            if (selectedElement instanceof DateSeparator) {
               this._context.put(250, selectedElement);
               Verb[] verbs = new Object[0];
               ((DateSeparator)selectedElement)
                  .getContextMenuVerbs(verbs, this._selectedIndexManager.getSelectedIndex(), this._sortedSeparatedItems, this._context);
               menu.add(verbs);
            }
         } else {
            defaultVerb = this.makeMultiSelectContextMenu(menu, selectedIndices);
         }
      } else {
         if (selectedIndices.length > 1) {
            defaultVerb = this.makeMultiSelectContextMenu(menu, selectedIndices);
         } else {
            selectedElement = this.getSelectedItem();
            if (selectedElement instanceof Object) {
               selectedModel = (RIMModel)selectedElement;
               this._context.put(250, selectedElement);
               if (selectedModel instanceof Object) {
                  Array.resize(this._verbs, 0);
                  VerbProvider provider = (VerbProvider)selectedModel;
                  defaultVerb = provider.getVerbs(this._context, this._verbs);
                  menu.add(this._verbs);
                  Array.resize(this._verbs, 0);
               }

               this._context.put(424670468422402792L, selectedElement);
               if (ContextObject.getFlag(this._context, 5) && !(selectedModel instanceof DateSeparator) && !(selectedModel instanceof NoMessagesBar)) {
                  this._pickVerb.setParameters(selectedModel);
                  menu.add(this._pickVerb);
               }

               boolean dateSelected = selectedModel instanceof DateSeparator;
               if (!dateSelected && !(selectedModel instanceof NoMessagesBar)) {
                  this._deleteSingleItemVerb.setParameters(selectedModel, this._context);
                  menu.add(this._deleteSingleItemVerb);
               }

               if (dateSelected) {
                  Verb[] verbs = new Object[0];
                  ((DateSeparator)selectedModel)
                     .getDefaultMenuVerbs(verbs, this._selectedIndexManager.getSelectedIndex(), this._sortedSeparatedItems, this._context);
                  menu.add(verbs);
               }
            }
         }

         if (ContextObject.getFlag(this._context, 5)) {
            menu.add(this._leaveScreenVerb);
         } else {
            VerbRepository verbRepository = VerbRepository.getVerbRepository(-7881764549058890736L);
            menu.add(verbRepository.getVerbs(null));
            verbRepository = VerbRepository.getVerbRepository(-2204303273264560528L);
            menu.add(verbRepository.getVerbs(null));
            if (!this._context.getFlag(52)) {
               menu.add(new MessageListUI$ViewSavedMessagesVerb());
            }

            menu.add(this._leaveScreenVerb);
            verbRepository = VerbRepository.getVerbRepository(-1585975315762883881L);
            menu.add(verbRepository.getVerbs(null));
         }

         VerbFactory[] verbFactories = VerbFactoryRepository.getVerbFactories(2729258854446987021L);
         if (verbFactories != null) {
            for (int i = verbFactories.length - 1; i >= 0; i--) {
               menu.add(verbFactories[i].getVerbs(this._context));
            }
         }
      }

      if (defaultVerb == null) {
         int ordinalToStartAt = 1245184;
         boolean exactMatchRequired = false;
         Long mergeCollectionIdLong = (Long)ContextObject.get(this._context, 8505215491443778230L);
         if (mergeCollectionIdLong != null) {
            long mergeCollectionId = mergeCollectionIdLong;
            if (mergeCollectionId == -7118119043524803584L || mergeCollectionId == -4696470826620059293L) {
               ordinalToStartAt = 1266944;
               exactMatchRequired = true;
            } else if (mergeCollectionId == -942103673428357213L) {
               ordinalToStartAt = 1267024;
               exactMatchRequired = true;
            }
         }

         defaultVerb = menu.getFirstVerbAt(ordinalToStartAt);
         if (defaultVerb != null) {
            int ordinalFound = defaultVerb.getOrdering();
            if (exactMatchRequired && ordinalFound != ordinalToStartAt || ordinalFound > 1284505) {
               defaultVerb = null;
            }
         }
      }

      menu.setDefault(defaultVerb);
      menu.coalesce(-3072555018635390988L, null);
      if (!oldBackgroundFlag) {
         this._context.clearFlag(96);
      }
   }

   @Override
   protected final void onFocusNotify(boolean focus) {
      super.onFocusNotify(focus);
      if (Ui.isTTSEnabled()) {
         if (focus) {
            this._selectedIndexManager.addAccessibleState(2);
         } else {
            this._selectedIndexManager.removeAccessibleState(2);
            this._selectedIndexManager.addAccessibleState(1);
         }

         this.accessibleEventOccurred(6, new Object(1), new Object(2), this._selectedIndexManager);
      }
   }

   private final int checkItemIndex(long action, boolean upward, boolean scanIncludingCurrentSelection, RIMModel modelToStartFrom) {
      String warning = null;
      int index = this.checkFolderForItem(action, upward, false, false, scanIncludingCurrentSelection, modelToStartFrom);
      if (index != -1) {
         UiApplication app = UiApplication.getUiApplication();
         Object item = this.getItem(index);
         boolean restorePainting = false;
         if (!(item instanceof DateSeparator) && index <= this._listField.getTopRow()) {
            app.suspendPainting(true);
            this._selectedIndexManager.setSelectedIndex(index - 1);
            restorePainting = true;
         }

         this._selectedIndexManager.setSelectedIndex(index);
         if (restorePainting) {
            this._listField.invalidate();
            app.suspendPainting(false);
         }
      } else {
         index = this.checkFolderForItem(action, upward, upward, !upward, scanIncludingCurrentSelection, modelToStartFrom);
         if (index == -1) {
            if (action == 278390328807340479L) {
               warning = MessageResources.getString(115);
            } else if (action == -4201671119995560115L) {
               warning = MessageResources.getString(118);
            } else if (action == -2415955221176628574L) {
               warning = MessageResources.getString(190);
            }
         } else {
            if (index == this._selectedIndexManager.getSelectedIndex()) {
               if (action == 278390328807340479L) {
                  warning = MessageResources.getString(117);
               } else if (action == -4201671119995560115L) {
                  warning = MessageResources.getString(120);
               } else if (action == -2415955221176628574L) {
                  warning = MessageResources.getString(187);
                  index = -1;
               }
            } else if (action == 278390328807340479L) {
               warning = MessageResources.getString(116);
            } else if (action == -4201671119995560115L) {
               warning = MessageResources.getString(119);
            } else if (action == -2415955221176628574L && upward) {
               warning = MessageResources.getString(188);
            } else if (action == -2415955221176628574L && !upward) {
               warning = MessageResources.getString(189);
            }

            if (index != -1) {
               this._selectedIndexManager.setSelectedIndex(index);
            }
         }
      }

      if (warning != null) {
         Status.show(warning, 1000);
      }

      return index;
   }

   private final void endSelect(Object savedSelectedItem) {
      if (savedSelectedItem != null) {
         this._context.put(250, savedSelectedItem);
      } else {
         this._context.remove(250);
      }

      this._context.remove(-4502157452528129690L);
      this._context.remove(-2364888284414893834L);
      this._deleteSingleItemVerb.resetParameters();
      this._deleteMultiselectedVerb.resetParameters();
      this._fileMessageMultiselectedVerb.resetParameters();
      this._markOpenedMultiselectedVerb.resetParameters();
      this._markUnopenedMultiselectedVerb.resetParameters();
      this._saveMultiselectedVerb.resetParameters();
      this._pickVerb.resetParameters();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final boolean onMenu(int instance) {
      if (((UiApplication)this._creationApp).isPaintingSuspended()) {
         return false;
      }

      Object savedSelectedItem = this._context.get(250);
      boolean var5 = false /* VF: Semaphore variable */;

      try {
         var5 = true;
         super.onMenu(instance);
         var5 = false;
      } finally {
         if (var5) {
            this.endSelect(savedSelectedItem);
         }
      }

      this.endSelect(savedSelectedItem);
      return true;
   }

   @Override
   protected final void paint(Graphics graphics) {
      this._columnPainter.updateTimeFormat();
      super.paint(graphics);
   }

   private final void unregisterStatusBanner() {
      if (this._statusBanner != null && this._items instanceof Object) {
         RibbonBanner ribbonBar = RibbonBanner.getInstance();
         ribbonBar.unregisterBanner(this._statusBanner);
         this._statusBanner = null;
      }
   }

   private final void haltFiltering() {
      boolean cleanup = true;
      if (this._exitAction == 3 || this._exitAction == 4) {
         this.setExitAction(this._exitAction == 3 ? 1 : 2);
         cleanup = false;
         ReadableList sublist = this._sortedSeparatedItems;
         if (sublist instanceof Object) {
            FilterProgress fp = (FilterProgress)sublist;
            if (fp.getProgress() < 100) {
               RibbonBanner ribbonBar = RibbonBanner.getInstance();
               ribbonBar.setStatusBannerTitle(this._statusBanner, MessageResources.getString(191));
            }
         }
      }

      if (this._items instanceof Object) {
         ((BackgroundFilteringCollection)this._items).haltFiltering(cleanup);
      }
   }

   @Override
   protected final ContextObject getContext() {
      return this._context;
   }

   @Override
   public final void setTitle(String title) {
      this._title = title;
      this.updateRibbonTitle();
   }

   private final void logBadState(String message) {
      try {
         throw new Object(((StringBuffer)(new Object("MessageListUI Forced Stack Trace: "))).append(message).toString());
      } finally {
         QuincyManager.sendUncaughtException("MessageListUI");
         return;
      }
   }

   private static final Application safeGetApplication() {
      try {
         return Application.getApplication();
      } finally {
         ;
      }
   }

   public MessageListUI(String title, String in_progress_title, int exitAction) {
      super(562952100970496L);
      this.setId("message-list");
      this.setDefaultClose(false);
      this._exitAction = exitAction;
      String initial_title = title;
      if (in_progress_title != null) {
         initial_title = in_progress_title;
         this._title = title;
      }

      this._topDateSeparator = new DateSeparator(0);
      this._topDateSeparator = this._topDateSeparator;
      RibbonBanner ribbonBar = RibbonBanner.getInstance();
      if (ribbonBar != null) {
         this._statusBanner = ribbonBar.getStatusBanner(initial_title, 11);
         this.setTitle(this._statusBanner);
         this._statusBanner.getManager().setTag(null);
      }

      this.getMainManager().setVerticalQuantization(-1);
      this._columnPainter = new MessageListColumnPainter();
      this._creationApp = Application.getApplication();
      this._messageBulkMarkOldManager = new MessageBulkMarkOldManager();
      Proxy.getInstance().addRealtimeClockListener(this._messageBulkMarkOldManager);
      this.setSupportClickAndHoldKeyEvents(true);
   }

   static final int getFirstVisibleRow(VariableHeightListField list) {
      return list.getFirstVisibleRow();
   }

   static final int getLastVisibleRow(VariableHeightListField list) {
      return list.getLastVisibleRow();
   }
}
