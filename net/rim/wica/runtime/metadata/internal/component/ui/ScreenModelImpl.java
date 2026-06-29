package net.rim.wica.runtime.metadata.internal.component.ui;

import java.util.Enumeration;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntHashtable;
import net.rim.wica.runtime.metadata.Wiclet;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.component.ui.Command;
import net.rim.wica.runtime.metadata.component.ui.MenuModel;
import net.rim.wica.runtime.metadata.component.ui.ScreenModel;
import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.metadata.component.ui.UIContainer;
import net.rim.wica.runtime.metadata.component.ui.UIControl;
import net.rim.wica.runtime.metadata.internal.WicletEx;
import net.rim.wica.runtime.metadata.internal.component.ui.control.ButtonControlImpl;
import net.rim.wica.runtime.metadata.internal.component.ui.control.CheckboxControlImpl;
import net.rim.wica.runtime.metadata.internal.component.ui.control.ChoiceControlImpl;
import net.rim.wica.runtime.metadata.internal.component.ui.control.EditControlImpl;
import net.rim.wica.runtime.metadata.internal.component.ui.control.ImageControlImpl;
import net.rim.wica.runtime.metadata.internal.component.ui.control.LabelControlImpl;
import net.rim.wica.runtime.metadata.internal.component.ui.control.SeparatorControlImpl;
import net.rim.wica.runtime.metadata.internal.component.ui.control.TableColumnModelImpl;
import net.rim.wica.runtime.metadata.internal.component.ui.control.TableModelImpl;
import net.rim.wica.runtime.metadata.internal.component.ui.control.TextAreaControlImpl;
import net.rim.wica.runtime.metadata.internal.def.Definitions;
import net.rim.wica.runtime.metadata.internal.def.UIDefAccess;
import net.rim.wica.runtime.metadata.internal.handler.UIHandler;
import net.rim.wica.runtime.metadata.internal.transaction.TransactionManager;
import net.rim.wica.runtime.metadata.internal.util.SingleValueHelper;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.ui.ScreenView;
import net.rim.wica.runtime.ui.UiService;
import net.rim.wica.runtime.ui.View;

public class ScreenModelImpl extends UIContainerImpl implements ScreenModel {
   private Wiclet _wiclet;
   private UIDefAccess _defs;
   private Definitions _definitions;
   private long[] _vars;
   private int _paramCount;
   private MenuModelImpl _menu;
   private int _menuId;
   private String _title;
   private int _showEventId;
   private IntHashtable _componentMap;
   private int _transactionId = -1;
   private int _lastTransactionId = -1;
   private UIHandler _uiHandler;
   private UiService _uiService;
   private TransactionManager _transactions;
   private boolean _updating;
   private boolean _showing;
   private boolean _modified;
   private boolean _displayed;
   private int _valid;
   private ControlMappingResolver _mappingResolver;
   private Command _defaultActionCommand;
   public static final int STATE_VALID;
   public static final int STATE_INVALID_UI;
   public static final int STATE_INVALID_UI_FULL;
   static Class class$net$rim$wica$runtime$metadata$internal$handler$UIHandler;
   static Class class$net$rim$wica$runtime$ui$UiService;
   static Class class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager;

   public ControlMappingResolver getMappingResolver() {
      return this._mappingResolver;
   }

   protected void createChildren(UIContainer parent, int childrenArrayId, ScreenModelImpl screenModel, boolean add) {
      int count = this._defs.getArraySize(childrenArrayId);
      UIComponent[] children = parent.getChildren();

      for (int i = 0; i < count; i++) {
         int componentIndex = this._defs.getArrayElementAt(childrenArrayId, i);
         switch (this._defs.getComponentType(componentIndex)) {
            case 127:
            case 136:
            case 138:
               break;
            case 128:
            default:
               int childrenArray = this._defs.getRegionChildrenArrayId(componentIndex);
               children[i] = new UIContainerImpl(
                  this._defs.getComponentId(componentIndex),
                  128,
                  parent,
                  this._defs.getComponentStyle(componentIndex),
                  this._defs.getComponentBits(componentIndex),
                  this._defs.getComponentX(componentIndex),
                  this._defs.getComponentY(componentIndex),
                  -1,
                  this._defs.getRegionLayout(componentIndex),
                  this._defs.getArraySize(childrenArray)
               );
               if (add) {
                  screenModel.addControl(children[i]);
               }

               this.createChildren((UIContainer)children[i], childrenArray, screenModel, add);
               break;
            case 129:
               children[i] = new EditControlImpl(
                  this._defs.getComponentId(componentIndex),
                  parent,
                  this._defs.getComponentStyle(componentIndex),
                  this._defs.getComponentBits(componentIndex),
                  this._defs.getComponentX(componentIndex),
                  this._defs.getComponentY(componentIndex),
                  this._defs.getControlInit(componentIndex),
                  this._defs.getControlInValue(componentIndex),
                  this._defs.getEditType(componentIndex),
                  this._defs.getEditFocusOutId(componentIndex),
                  this._defs.getEditMapping(componentIndex),
                  this._defs.getEditFormat(componentIndex)
               );
               if (add) {
                  screenModel.addControl(children[i]);
               }
               break;
            case 130:
               children[i] = new ChoiceControlImpl(
                  this._defs.getComponentId(componentIndex),
                  parent,
                  this._defs.getComponentStyle(componentIndex),
                  this._defs.getComponentBits(componentIndex),
                  this._defs.getComponentX(componentIndex),
                  this._defs.getComponentY(componentIndex),
                  this._defs.getControlInit(componentIndex),
                  this._defs.getControlInValue(componentIndex),
                  this._defs.getChoiceType(componentIndex),
                  this._defs.getChoiceChangeId(componentIndex),
                  this._defs.getChoiceFormat(componentIndex),
                  this._defs.getChoiceMapping(componentIndex),
                  this._defs.getChoiceVisibleRows(componentIndex)
               );
               if (add) {
                  screenModel.addControl(children[i]);
               }
               break;
            case 131:
               children[i] = new ButtonControlImpl(
                  this._defs.getComponentId(componentIndex),
                  parent,
                  this._defs.getComponentStyle(componentIndex),
                  this._defs.getComponentBits(componentIndex),
                  this._defs.getComponentX(componentIndex),
                  this._defs.getComponentY(componentIndex),
                  this._defs.getControlInit(componentIndex),
                  this._defs.getControlInValue(componentIndex),
                  this._defs.getButtonClickId(componentIndex),
                  this._defs.getButtonResourceId(componentIndex),
                  this._defs.getButtonImageInValue(componentIndex)
               );
               if (add) {
                  screenModel.addControl(children[i]);
               }
               break;
            case 132:
               children[i] = new SeparatorControlImpl(
                  this._defs.getComponentId(componentIndex),
                  parent,
                  this._defs.getComponentStyle(componentIndex),
                  this._defs.getComponentBits(componentIndex),
                  this._defs.getComponentX(componentIndex),
                  this._defs.getComponentY(componentIndex),
                  this._defs.getControlInit(componentIndex),
                  this._defs.isWhitespaceSeparator(componentIndex)
               );
               if (add) {
                  screenModel.addControl(children[i]);
               }
               break;
            case 133:
               children[i] = new ImageControlImpl(
                  this._defs.getComponentId(componentIndex),
                  parent,
                  this._defs.getComponentStyle(componentIndex),
                  this._defs.getComponentBits(componentIndex),
                  this._defs.getComponentX(componentIndex),
                  this._defs.getComponentY(componentIndex),
                  this._defs.getControlInit(componentIndex),
                  this._defs.getControlInValue(componentIndex),
                  this._defs.getImageResourceId(componentIndex)
               );
               if (add) {
                  screenModel.addControl(children[i]);
               }
               break;
            case 134:
               children[i] = new LabelControlImpl(
                  this._defs.getComponentId(componentIndex),
                  parent,
                  this._defs.getComponentStyle(componentIndex),
                  this._defs.getComponentBits(componentIndex),
                  this._defs.getComponentX(componentIndex),
                  this._defs.getComponentY(componentIndex),
                  this._defs.getControlInit(componentIndex),
                  this._defs.getControlInValue(componentIndex)
               );
               if (add) {
                  screenModel.addControl(children[i]);
               }
               break;
            case 135:
               children[i] = new TextAreaControlImpl(
                  this._defs.getComponentId(componentIndex),
                  parent,
                  this._defs.getComponentStyle(componentIndex),
                  this._defs.getComponentBits(componentIndex),
                  this._defs.getComponentX(componentIndex),
                  this._defs.getComponentY(componentIndex),
                  this._defs.getControlInit(componentIndex),
                  this._defs.getControlInValue(componentIndex),
                  this._defs.getTextAreaFocusOutId(componentIndex),
                  this._defs.getTextAreaMapping(componentIndex),
                  this._defs.getTextAreaVisibleRows(componentIndex)
               );
               if (add) {
                  screenModel.addControl(children[i]);
               }
               break;
            case 137:
               int childrenArray = this._defs.getRepetitionChildrenArrayId(componentIndex);
               children[i] = new RepetitionControlImpl(
                  this._defs.getComponentId(componentIndex),
                  parent,
                  this._defs.getComponentStyle(componentIndex),
                  this._defs.getComponentBits(componentIndex),
                  this._defs.getComponentX(componentIndex),
                  this._defs.getComponentY(componentIndex),
                  this._defs.getControlInValue(componentIndex),
                  this._defs.getControlInit(componentIndex),
                  this._defs.getRepetitionChangeEventId(componentIndex),
                  this._defs.getRepetitionMapping(componentIndex),
                  this._defs.isRepetitionCollapsible(componentIndex),
                  this._defs.getArraySize(childrenArray),
                  this._defs.getRepetitionVisibleRows(componentIndex)
               );
               if (add) {
                  screenModel.addControl(children[i]);
               }

               this.createChildren((UIContainer)children[i], childrenArray, screenModel, false);
               break;
            case 139:
               children[i] = new CheckboxControlImpl(
                  this._defs.getComponentId(componentIndex),
                  parent,
                  this._defs.getComponentStyle(componentIndex),
                  this._defs.getComponentBits(componentIndex),
                  this._defs.getComponentX(componentIndex),
                  this._defs.getComponentY(componentIndex),
                  this._defs.getControlInit(componentIndex),
                  this._defs.getControlInValue(componentIndex),
                  this._defs.getCheckboxChangeId(componentIndex),
                  this._defs.getCheckboxMapping(componentIndex)
               );
               if (add) {
                  screenModel.addControl(children[i]);
               }
               break;
            case 140:
               int childrenArray = this._defs.getTableChildrenArrayId(componentIndex);
               children[i] = new TableModelImpl(
                  this._defs.getComponentId(componentIndex),
                  parent,
                  this._defs.getComponentStyle(componentIndex),
                  this._defs.getComponentBits(componentIndex),
                  this._defs.getComponentX(componentIndex),
                  this._defs.getComponentY(componentIndex),
                  this._defs.getControlInit(componentIndex),
                  this._defs.getTableChangeEventId(componentIndex),
                  this._defs.getTableFetchMoreEventId(componentIndex),
                  this._defs.getControlInValue(componentIndex),
                  this._defs.getTableMapping(componentIndex),
                  this._defs.getTableHeaderStyle(componentIndex),
                  this._defs.getTableEvenRowStyle(componentIndex),
                  this._defs.getTableOddRowStyle(componentIndex),
                  this._defs.getTableGridlineColor(componentIndex),
                  this._defs.isTableShowGridline(componentIndex),
                  this._defs.isTableShowHeader(componentIndex),
                  this._defs.isTableShowSelector(componentIndex),
                  this._defs.getArraySize(childrenArray),
                  this._defs.getTableVisibleRows(componentIndex)
               );
               if (add) {
                  screenModel.addControl(children[i]);
               }

               this.createChildren((UIContainer)children[i], childrenArray, screenModel, true);
               break;
            case 141:
               TableColumnModelImpl column = new TableColumnModelImpl(
                  parent,
                  this._defs.getTableColumnName(componentIndex),
                  this._defs.getTableColumnTitle(componentIndex),
                  this._defs.getTableColumnInValue(componentIndex),
                  this._defs.getTableColumnFormatType(componentIndex),
                  this._defs.getTableColumnFormat(componentIndex),
                  this._defs.getTableColumnWidth(componentIndex),
                  this._defs.getTableColumnStyle(componentIndex),
                  this._defs.getTableColumnHeaderStyle(componentIndex),
                  this._defs.getTableColumnEvenRowStyle(componentIndex),
                  this._defs.getTableColumnOddRowStyle(componentIndex),
                  this._defs.getTableColumnFrozen(componentIndex),
                  this._defs.getTableColumnVisible(componentIndex)
               );
               children[i] = column;
               if (add && parent instanceof TableModelImpl) {
                  ((TableModelImpl)parent).addColumn(column.getName(), column);
               }
         }
      }
   }

   protected void addControl(UIComponent component) {
      this._componentMap.put(component.getId(), component);
   }

   public Object getObjectData(int object) {
      return this._defs.getObjectData(object);
   }

   public void handleEvent(int eventType, int eventIndex) {
      if (this._uiHandler != null) {
         if (eventType == 2) {
            if (this._defs.getClickTransactionType(eventIndex) != 2 && !this.checkMandatoryFields()) {
               this._uiService.displayModalDialog(-1, RuntimeResources.getString(62));
               return;
            }

            this.updateData();
            this.handleClick(eventIndex);
            return;
         }

         int scriptId = this._defs.getEventScript(eventIndex);
         if (scriptId != -1) {
            this.updateData();
            int[] paramArray = this._defs.getEventParamArray(eventIndex);
            int offset = this._defs.getEventParamOffset(eventIndex);
            long[] params = null;
            if (paramArray[offset] > 0) {
               params = new long[paramArray[offset]];
               this.resolveParams(paramArray, offset + 1, params);
            }

            this._uiHandler.execute(scriptId, params);
         }
      }
   }

   public boolean isRefreshMsg(int msgId) {
      int arrayId = this._defs.getRefreshMsgArrayId(this.getId());
      if (arrayId != -1) {
         int count = this._defs.getArraySize(arrayId);

         for (int i = 0; i < count; i++) {
            if (msgId == this._defs.getArrayElementAt(arrayId, i)) {
               return true;
            }
         }
      }

      return false;
   }

   public int getLastTransactionId() {
      return this._lastTransactionId;
   }

   public void display(long[] params) {
      this.reset();
      if (this._vars != null) {
         int paramCount = params != null ? Math.min(this._paramCount, params.length) : 0;

         for (int i = this._vars.length - 1; i >= 0; i--) {
            this.setVarValue(i, i < paramCount ? params[i] : -1);
         }
      }

      this.init();
      this.updateUI();
      this.setDisplayed(true);
   }

   public void setLastTransactionId(int id) {
      this._lastTransactionId = id;
   }

   public int getTransactionId() {
      return this._transactionId;
   }

   public void setTransactionId(int id) {
      this._transactionId = id;
      this._lastTransactionId = -1;
   }

   public int getCodeByName(String name) {
      return this._definitions.getCodeByName(name);
   }

   public void setDisplayed(boolean displayed) {
      this._displayed = displayed;
   }

   public void onModify() {
      this._modified = true;
   }

   @Override
   public int getVarType(int varIndex) {
      return this._defs.getVarType(this.getId(), varIndex);
   }

   @Override
   public long getVarValue(int varIndex) {
      this._vars[varIndex] = this._wiclet.verifyHandle(this._vars[varIndex]);
      return this._vars[varIndex];
   }

   @Override
   public long getVarValue(int varIndex, boolean create) {
      this._vars[varIndex] = this._wiclet.verifyHandle(this._vars[varIndex]);
      if (create && this._vars[varIndex] == -1) {
         int varType = this.getVarType(varIndex);
         DataCollection dc = this._wiclet.getDataCollection(varType);
         if (!dc.getDef().hasKey()) {
            this._vars[varIndex] = this.createLocally(dc);
         }
      }

      return this._vars[varIndex];
   }

   @Override
   public void setVarValue(int varIndex, long value) {
      int varType = this.getVarType(varIndex);
      if (value != -1 && varType != (int)(value >> 32)) {
         throw new Object("Variable or parameter type mismatching.");
      }

      DataCollection dc = this._wiclet.getDataCollection(varType);
      value = this._wiclet.verifyHandle(value);
      if (this._vars[varIndex] != value) {
         dc.addReference(value, false);
         dc.removeReference(this._vars[varIndex], false);
      }

      this._vars[varIndex] = value;
   }

   @Override
   public long getVarValueByName(String name) {
      int code = this.getCodeByName(name);
      int varIndex = this._defs.getVarIndexByCode(this.getId(), code);
      return this.getVarValue(varIndex);
   }

   @Override
   public void invalidateUI(boolean full) {
      if (full) {
         this._valid = 2;
      } else {
         this._valid |= 1;
      }
   }

   @Override
   public boolean isDisplayed() {
      return this._displayed;
   }

   @Override
   public int getVarIndex(String name) {
      return this._defs.getVarIndexByCode(this.getId(), this.getCodeByName(name));
   }

   @Override
   public MenuModel getMenuOnShow() {
      if (this._menu != null) {
         this.updateData();
         this._menu.updateUI();
      }

      return this._menu;
   }

   @Override
   public MenuModel getMenu() {
      return this._menu;
   }

   @Override
   public boolean isDialog() {
      return this._defs.isDialog(this.getId());
   }

   @Override
   public String getTitle() {
      return this._title;
   }

   @Override
   public Wiclet getWiclet() {
      return this._wiclet;
   }

   @Override
   public boolean performDefaultAction() {
      if (this._defaultActionCommand != null) {
         this._defaultActionCommand.onClick();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void setDefaultAction(Command command) {
      this._defaultActionCommand = command;
   }

   @Override
   public Command getDefaultAction() {
      return this._defaultActionCommand;
   }

   @Override
   public UIComponent getCurrentFocus() {
      return this.isDisplayed() ? ((ScreenView)this.getView()).getCurrentFocus().getModel() : null;
   }

   @Override
   public UIComponent getComponent(String name) {
      return (UIComponent)this._componentMap.get(this.getCodeByName(name));
   }

   @Override
   public void clean() {
      super.clean();
      this.setTransactionId(-1);
      this.reset();
   }

   @Override
   public boolean isVisible() {
      return true;
   }

   private void handleClick(int clickIndex) {
      if (this._uiHandler != null) {
         int transactionType = this._defs.getClickTransactionType(clickIndex);
         boolean isRollback = transactionType == 2;
         int transitionId = this._defs.getClickTransitionId(clickIndex);
         if (transactionType != 0
            && (transitionId == -1 || this._wiclet.getDefType(transitionId) == 10 || this._wiclet.getDefType(transitionId) == 11 && isRollback)) {
            this._uiHandler.completeTransaction(isRollback);
         }

         if (transitionId != -1) {
            int[] paramArray = this._defs.getClickParamArray(clickIndex);
            int offset = this._defs.getClickParamOffset(clickIndex);
            long[] params = null;
            if (paramArray[offset] > 0) {
               params = new long[paramArray[offset]];
               this.resolveParams(paramArray, offset + 1, params);
            }

            this._uiHandler.execute(transitionId, params);
            if (transactionType == 1 && this._wiclet.getDefType(transitionId) == 11) {
               this._uiHandler.completeTransaction(isRollback);
            }
         }
      }
   }

   private void resolveParams(int[] array, int offset, long[] results) {
      int paramCount = results.length;

      for (int i = 0; i < paramCount; i++) {
         results[i] = this.resolveParam(array, offset);
         offset += array[offset] + 1;
      }
   }

   private long resolveParam(int[] array, int offset) {
      int depth = array[offset];
      int index = offset + 1;
      if (array[index] == -1) {
         if (array[index + 1] == -1) {
            return -1;
         }

         long handle = this.getVarValue(array[index + 1]);
         return handle != -1 && depth > 2 ? ((WicletEx)this._wiclet).getRef(handle, array, index + 2, index + depth) : handle;
      } else {
         long handle = (long)array[index] << 32;
         return handle != -1 ? ((WicletEx)this._wiclet).getRef(handle, array, index + 1, index + depth) : -1;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private long createLocally(DataCollection dc) {
      if (this._transactions != null) {
         boolean ignore = this._transactions.getIgnoreTransactions();
         this._transactions.setIgnoreTransactions(true);
         boolean var9 = false /* VF: Semaphore variable */;

         long var5;
         try {
            var9 = true;
            long handle = dc.create();
            dc.addReference(handle, false);
            var5 = handle;
            var9 = false;
         } finally {
            if (var9) {
               this._transactions.setIgnoreTransactions(ignore);
            }
         }

         this._transactions.setIgnoreTransactions(ignore);
         return var5;
      } else {
         long handle = dc.create();
         dc.addReference(handle, false);
         return handle;
      }
   }

   private boolean checkMandatoryFields() {
      boolean allSatisfied = true;
      Enumeration e = this._componentMap.elements();

      while (e.hasMoreElements()) {
         UIComponent currentControl = (UIComponent)e.nextElement();
         if (currentControl instanceof UIControl) {
            UIControl control = (UIControlImpl)currentControl;
            if (control.isMandatory() && control.isVisible() && !control.isMandatorySatisfied()) {
               return false;
            }
         }
      }

      return allSatisfied;
   }

   @Override
   protected void reset() {
      super.reset();
      this._valid = 2;
      this._modified = false;
      this.setDisplayed(false);
   }

   private void buildMenu() {
      if (this._menuId != -1) {
         this._menu = new MenuModelImpl(this._menuId, 136, this, this._defs.getMenuShowEvent(this._menuId));
         int menuCount = this._defs.getMenuItemCount(this._menuId);
         this._menu.setMenuItemCount(menuCount);

         for (int i = 0; i < menuCount; i++) {
            this._menu
               .setMenuItem(
                  i,
                  this._defs.getMenuItemId(this._menuId, i),
                  this._defs.getMenuItemValue(this._menuId, i),
                  this._defs.getMenuItemClickId(this._menuId, i),
                  (this._defs.getMenuItemBits(this._menuId, i) & 2) != 0
               );
         }
      }
   }

   private void buildScreen() {
      int children = this._defs.getChildrenArrayId(this.getId());
      this.createChildren(this, children, this, true);
      this._menuId = this._defs.getMenuId(this.getId());
      this.buildMenu();
      this._showEventId = this._defs.getScreenShowEvent(this.getId());
   }

   @Override
   public void updateUI() {
      if ((this._valid & 2) != 0) {
         if (this._updating) {
            return;
         }

         this._updating = true;
         Object titleValue = SingleValueHelper.resolveInValue(this, this._defs.getTitle(this.getId()), 3);
         this._title = titleValue == null ? null : titleValue.toString();
         this._mappingResolver.updateRootControlUI();
         super.updateUI();
         this._valid = 0;
         this._modified = false;
         this._updating = false;
         if (!this._showing && this._showEventId != -1) {
            this._showing = true;
            this.handleEvent(2147483646, this._showEventId);
            this._showing = false;
         }

         if (this.getView() != null) {
            ((View)this.getView()).update(0);
            return;
         }
      } else if (this.getView() != null && (this._valid & 1) != 0) {
         this._valid = 0;
         if (!this._showing) {
            ((View)this.getView()).update(0);
         }
      }
   }

   @Override
   public void updateData() {
      if (this._modified) {
         this._mappingResolver.updateRootControlData();
         super.updateData();
         this._modified = false;
      }
   }

   public ScreenModelImpl(int id, Wiclet wiclet, Definitions definitions) {
      super(
         id,
         10,
         null,
         definitions.getUIDefs().getStyleId(id),
         0,
         0,
         0,
         definitions.getUIDefs().getScreenInit(id),
         definitions.getUIDefs().getLayout(id),
         definitions.getUIDefs().getArraySize(definitions.getUIDefs().getChildrenArrayId(id))
      );
      this._wiclet = wiclet;
      this._uiHandler = (UIHandler)((WicletEx)wiclet)
         .getRuntime()
         .getService(
            class$net$rim$wica$runtime$metadata$internal$handler$UIHandler == null
               ? (class$net$rim$wica$runtime$metadata$internal$handler$UIHandler = class$("net.rim.wica.runtime.metadata.internal.handler.UIHandler"))
               : class$net$rim$wica$runtime$metadata$internal$handler$UIHandler
         );
      this._uiService = (UiService)((WicletEx)wiclet)
         .getRuntime()
         .getService(
            class$net$rim$wica$runtime$ui$UiService == null
               ? (class$net$rim$wica$runtime$ui$UiService = class$("net.rim.wica.runtime.ui.UiService"))
               : class$net$rim$wica$runtime$ui$UiService
         );
      this._definitions = definitions;
      this._defs = definitions.getUIDefs();
      this._paramCount = this._defs.getParamCount(id);
      int varCount = this._defs.getVarCount(id) + this._paramCount;
      if (varCount > 0) {
         this._vars = new long[varCount];
         Arrays.fill(this._vars, -1);
         this._transactions = (TransactionManager)((WicletEx)wiclet)
            .getRuntime()
            .getService(
               class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager == null
                  ? (
                     class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager = class$(
                        "net.rim.wica.runtime.metadata.internal.transaction.TransactionManager"
                     )
                  )
                  : class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager
            );
      }

      int totalControls = this._defs.getTotalControlCount(id);
      this._mappingResolver = new ControlMappingResolver();
      this._componentMap = (IntHashtable)(new Object(totalControls));
      this.buildScreen();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
