package net.rim.device.apps.internal.addressbook.ui;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.component.CollectionListField;
import net.rim.device.api.ui.component.KeywordFilterCollectionListField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.ui.VariableRowHeightProxy;
import net.rim.device.apps.internal.addressbook.BlackBerryAddressBook;
import net.rim.device.apps.internal.addressbook.lookup.ALPConfiguration;
import net.rim.device.apps.internal.addressbook.lookup.ALPManager;
import net.rim.device.apps.internal.addressbook.lookup.Request;
import net.rim.device.apps.internal.addressbook.lookup.RequestModel;
import net.rim.device.apps.internal.addressbook.lookup.Result;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;

public final class AddressBookListField extends KeywordFilterCollectionListField implements ListFieldCallback, CollectionListener {
   private long _sortOrder;
   private ContextObject _paintContext;
   private boolean _useOnceEnabled;
   private boolean _alpEnabled;
   private ALPManager _alpManager;
   private byte _listSeparatorMode;
   private AddressBookListField$ResetListWhileHoldingALPManagerLock _resetListWhileHoldingALPManagerLock = new AddressBookListField$ResetListWhileHoldingALPManagerLock(
      this, null
   );
   private UiApplication _app = UiApplication.getUiApplication();
   private int _themeGeneration;
   private ThemeAttributeSet _themeAttributesLine1;
   private ThemeAttributeSet _themeAttributesLine2;

   public AddressBookListField(boolean enableUseOnce, boolean enableALP, Object initialContext) {
      super(null, null);
      this._useOnceEnabled = enableUseOnce;
      this._alpEnabled = ALPConfiguration.isActive() ? enableALP : false;
      this._alpManager = ALPConfiguration.getManager();
      this.setCallback(this);
      this._sortOrder = AddressBookServices.getAddressBookOptions().getSortOrder();
      this.setTag(AddressBookServices.ADDRESS_BOOK_LIST_PLAIN_TAG);
      this.setListSeparatorAppearance(AddressBookServices.getAddressBookOptions().getListSeparatorAppearance());
      KeywordFilterList view = this.initializeList();
      this.initializeContexts(initialContext);
      view.waitForComplete();
      if (this._alpEnabled) {
         this._alpManager.addCollectionListener(this);
      }

      this.updateExtraRowCount();
      this.setSize(view.size());
   }

   public final long getSortOrder() {
      return this._sortOrder;
   }

   public final void setSortOrder(long sortOrder) {
      this._sortOrder = sortOrder;
      this._paintContext.put(614335798810617774L, new Object(this._sortOrder));
      this.setKeywordFilterList(BlackBerryAddressBook.getAddressBook().getView(this._sortOrder));
   }

   public final void setListSeparatorAppearance(byte val) {
      if (val != this._listSeparatorMode) {
         this._listSeparatorMode = val;
         if (this._listSeparatorMode == 1) {
            this.setTag(AddressBookServices.ADDRESS_BOOK_LIST_STRIPES_TAG);
            return;
         }

         this.setTag(AddressBookServices.ADDRESS_BOOK_LIST_PLAIN_TAG);
      }
   }

   public final boolean isUseOnceIndex(int index) {
      return this._useOnceEnabled && index == 0;
   }

   public final boolean isSearchIndex(int index) {
      if (this.isUseOnceIndex(index)) {
         return false;
      }

      if (!this._alpEnabled) {
         return false;
      }

      int extraRows = this.getExtraRowCount();
      return index < extraRows;
   }

   private final void initializeContexts(Object baseContext) {
      this._paintContext = ContextObject.castOrCreate(baseContext).clone();
      this._paintContext.setFlag(3, 1, 17);
      this._paintContext.setFlag(17, 18, 4);
      this._paintContext.setFlag(128);
      this.updateThemeAttributes();
      this._paintContext.put(614335798810617774L, new Object(this._sortOrder));
      this._paintContext.put(-3906294199383546540L, this);
   }

   public final void updateExtraRowCount() {
      int extra = 0;
      if (this._useOnceEnabled) {
         extra++;
      }

      if (this._alpEnabled && !this._paintContext.getFlag(108)) {
         extra += this._alpManager.size();
      }

      super.setExtraRowCount(extra);
   }

   private final KeywordFilterList initializeList() {
      KeywordFilterList view = BlackBerryAddressBook.getAddressBook().getView(this._sortOrder);
      this.setKeywordFilterList(view);
      view.setCriteria(null, this);
      return view;
   }

   @Override
   public final Object getSelectedElement() {
      return this.getElementAtIncludingLookup(this.getSelectedIndex());
   }

   public final Object getElementAtIncludingLookup(int index) {
      this.getExtraRowCount();
      if (this.isUseOnceIndex(index)) {
         return null;
      }

      if (this.isSearchIndex(index)) {
         int searchBase = this._useOnceEnabled ? 1 : 0;

         try {
            Request r = (Request)this._alpManager.getAt(index - searchBase);
            return this._alpManager.createModelFromRequest(r);
         } finally {
            return super.getSelectedElement();
         }
      } else {
         return super.getSelectedElement();
      }
   }

   public static final AddressBookListField getInstance(boolean enableUseOnce, boolean enableALP, Object initialContext) {
      return new AddressBookListField(enableUseOnce, enableALP, initialContext);
   }

   private final void updateThemeAttributes() {
      int themeGeneration = ThemeManager.getGeneration();
      if (themeGeneration != this._themeGeneration) {
         this._themeGeneration = themeGeneration;
         this._themeAttributesLine1 = ThemeManager.getActiveTheme().getAttributeSet(AddressBookServices.TAG_LINE1);
         this._themeAttributesLine2 = ThemeManager.getActiveTheme().getAttributeSet(AddressBookServices.TAG_LINE2);
         if (this._paintContext.getFlag(128)) {
            this.setRowHeight(this.internalGetRowHeight());
         }
      }
   }

   private final int getFontHeight(ThemeAttributeSet tas, int defaultHeight) {
      if (tas != null) {
         Font font = tas.getFont();
         if (font != null) {
            return font.getHeight();
         }
      }

      return defaultHeight;
   }

   private final int internalGetRowHeight() {
      if (this._paintContext.getFlag(128)) {
         int height = Font.getDefault().getHeight();
         int linePadding = this._listSeparatorMode == 2 ? 2 : 0;
         return this.getFontHeight(this._themeAttributesLine1, height) + this.getFontHeight(this._themeAttributesLine2, height) + linePadding;
      } else {
         return super.getRowHeight();
      }
   }

   private final void drawListRowInternal(ListField listField, Graphics graphics, int index, int y, int width) {
      CollectionListField collectionListField = (CollectionListField)listField;
      Object element = collectionListField.getElementAt(index);
      VariableRowHeightProxy.addHeightAdjusterToContext(this._paintContext, listField);
      this.updateThemeAttributes();
      if (element instanceof Object) {
         PaintProvider painter = (PaintProvider)element;
         painter.paint(graphics, 0, y, width, 100, this._paintContext);
      } else if (this.isUseOnceIndex(index)) {
         graphics.drawText(AddressBookResources.getString(304), 0, y, 0, width);
      } else {
         int extraRows = this.getExtraRowCount();
         if (index == extraRows) {
            graphics.drawText(AddressBookResources.getString(305), 0, y, 4, width);
         } else if (this._alpEnabled && !this._paintContext.getFlag(108)) {
            int searchBase = this._useOnceEnabled ? 1 : 0;
            index -= searchBase;
            if (index < this._alpManager.size()) {
               try {
                  Request r = (Request)this._alpManager.getAt(index);
                  if (!(r instanceof Object)) {
                     graphics.drawText(r.toString(), 0, y, 0, width);
                     return;
                  }

                  r.paint(graphics, 0, y, width, 100, this._paintContext);
               } finally {
                  return;
               }
            }
         }
      }
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      this.drawListRowInternal(listField, graphics, index, y, width);
      if (this._listSeparatorMode == 2) {
         int rowHeight = this.getRowHeight(index);
         int graphicsColor = graphics.getColor();
         int graphicsAlpha = graphics.getGlobalAlpha();
         int drawColor = 0;
         graphics.setColor(drawColor);
         graphics.setGlobalAlpha(32);
         graphics.drawLine(0, y + rowHeight - 1, width, y + rowHeight - 1);
         graphics.setColor(graphicsColor);
         graphics.setGlobalAlpha(graphicsAlpha);
      }
   }

   @Override
   public final Object get(ListField listField, int index) {
      CollectionListField collectionListField = (CollectionListField)listField;
      return collectionListField.getElementAt(index);
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return this.getSelectedIndex();
   }

   private final void scheduleUpdateExtraRowCountWhileHoldingALPManagerLock() {
      if (this._resetListWhileHoldingALPManagerLock.initiateReset()) {
         this._app.invokeLater(this._resetListWhileHoldingALPManagerLock);
      }
   }

   @Override
   public final void reset(Collection collection) {
      if (collection == this._alpManager) {
         this.scheduleUpdateExtraRowCountWhileHoldingALPManagerLock();
      }

      super.reset(collection);
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (collection == this._alpManager && newElement instanceof Object) {
         Request request = (Request)newElement;
         Result result = request.getResult();
         Object focusedElement = this.getElementWithFocus();
         if (request.needsResolving() && request.isViewable() && focusedElement instanceof Object) {
            RequestModel requestModel = (RequestModel)focusedElement;
            if (requestModel.fetchRequest() == newElement && result != null && result.getIncludedMatches() > 1) {
               RIMGlobalMessagePoster.postGlobalEvent(-6376745458772725637L, 0, 0, requestModel, null);
            }
         }
      }

      super.elementUpdated(collection, oldElement, newElement);
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      if (collection == this._alpManager) {
         this.scheduleUpdateExtraRowCountWhileHoldingALPManagerLock();
      }

      if (element instanceof Object) {
         Request request = (Request)element;
         Object requestModel = this._alpManager.createModelFromRequest(request);
         this.setElementWithFocus(requestModel);
      }

      super.elementAdded(collection, element);
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      if (collection == this._alpManager) {
         this.scheduleUpdateExtraRowCountWhileHoldingALPManagerLock();
      }

      super.elementRemoved(collection, element);
   }

   private final int indexOfRequest(Request request) {
      int index = this._alpManager.getIndex(request);
      if (index == -1) {
         return this.getExtraRowCount();
      }

      if (this._useOnceEnabled) {
         index++;
      }

      return index;
   }

   @Override
   public final void setSize(int count) {
      if (super._list != null) {
         Object elementWithFocus = this.getElementWithFocus();
         if (elementWithFocus instanceof Object) {
            RequestModel requestModel = (RequestModel)elementWithFocus;
            Request request = requestModel.fetchRequest();
            int index = this.indexOfRequest(request);
            this.setSize(count, index, false);
            return;
         }

         int index = elementWithFocus != null ? super._list.getIndex(elementWithFocus) : -1;
         if (index == -1) {
            index = this.getExtraRowCount();
            this.setElementWithFocus(null);
         } else {
            index += this.getExtraRowCount();
         }

         this.setSize(count, index);
      }
   }

   @Override
   public final int processKeyEvent(int event, char key, int keycode, int time) {
      int result = super.processKeyEvent(event, key, keycode, time);
      if ((result & 65536) != 0) {
         this.setElementWithFocus(null);
      }

      return result;
   }

   @Override
   public final AccessibleContext getAccessibleSelectionAt(int index) {
      if (index == 0) {
         Object temp = this.getElementAtIncludingLookup(this.getSelectedIndex());
         if (temp != null) {
            if (!(temp instanceof Object)) {
               return (AccessibleContext)(new Object(temp.toString(), 0, 4));
            }

            return (AccessibleContext)temp;
         }
      }

      return null;
   }
}
