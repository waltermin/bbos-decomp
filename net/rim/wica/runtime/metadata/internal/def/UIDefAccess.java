package net.rim.wica.runtime.metadata.internal.def;

import net.rim.device.api.util.IntIntHashtable;
import net.rim.wica.runtime.metadata.component.ui.ResourceCollection;
import net.rim.wica.runtime.metadata.component.ui.StyleCollection;

public class UIDefAccess implements ResourceCollection, StyleCollection {
   private ComponentDefStruct _uiDefs;
   private IntIntHashtable _idToDefIndex;
   public static final int TRANSACTION_NONE = 0;
   public static final int TRANSACTION_COMMIT = 1;
   public static final int TRANSACTION_ROLLBACK = 2;

   public boolean hasDefs() {
      return this._uiDefs._defs.length > 0;
   }

   public boolean hasDefinition(int defId) {
      return this._idToDefIndex.containsKey(defId);
   }

   public int getType(int defId) {
      int uiIndex = this._idToDefIndex.get(defId);
      return this._uiDefs._defs[uiIndex + 0];
   }

   public Object getObjectData(int objIndex) {
      return objIndex == -1 ? null : this._uiDefs._objectData[objIndex];
   }

   public int getAccessType(int screenId, int field) {
      return 536870912;
   }

   public int getScreenInit(int screenId) {
      int screenIndex = this._idToDefIndex.get(screenId);
      return this._uiDefs._defs[screenIndex + 9];
   }

   public int getScreenShowEvent(int screenId) {
      int screenIndex = this._idToDefIndex.get(screenId);
      return this._uiDefs._defs[screenIndex + 14];
   }

   public Object getTitle(int screenId) {
      int screenIndex = this._idToDefIndex.get(screenId);
      int titleIndex = this._uiDefs._defs[screenIndex + 2];
      return titleIndex == -1 ? null : this._uiDefs._objectData[titleIndex];
   }

   public boolean isDialog(int screenId) {
      int screenIndex = this._idToDefIndex.get(screenId);
      return (this._uiDefs._defs[screenIndex + 4] & 1) != 0;
   }

   public int getLayout(int screenId) {
      int screenIndex = this._idToDefIndex.get(screenId);
      return this._uiDefs._defs[screenIndex + 5];
   }

   public int getStyleId(int screenId) {
      int screenIndex = this._idToDefIndex.get(screenId);
      return this._uiDefs._defs[screenIndex + 7];
   }

   public int getRefreshMsgArrayId(int screenId) {
      int screenIndex = this._idToDefIndex.get(screenId);
      return this._uiDefs._defs[screenIndex + 8];
   }

   public int getChildrenArrayId(int screenId) {
      int screenIndex = this._idToDefIndex.get(screenId);
      return this._uiDefs._defs[screenIndex + 12];
   }

   public int getTotalControlCount(int screenId) {
      int screenIndex = this._idToDefIndex.get(screenId);
      return this._uiDefs._defs[screenIndex + 11];
   }

   public int getMenuId(int screenId) {
      int screenIndex = this._idToDefIndex.get(screenId);
      return this._uiDefs._defs[screenIndex + 10];
   }

   public int getParamCount(int screenId) {
      int uiIndex = this._idToDefIndex.get(screenId);
      int varRef = this._uiDefs._defs[uiIndex + 3];
      return varRef == -1 ? 0 : this._uiDefs._varData[varRef + 0];
   }

   public int getVarCount(int screenId) {
      int uiIndex = this._idToDefIndex.get(screenId);
      int varRef = this._uiDefs._defs[uiIndex + 13];
      return varRef == -1 ? 0 : this._uiDefs._varData[varRef + 0];
   }

   public int getVarType(int screenId, int var) {
      int uiIndex = this._idToDefIndex.get(screenId);
      int varRef = this._uiDefs._defs[uiIndex + 3];
      int paramCount = varRef == -1 ? 0 : this._uiDefs._varData[varRef + 0];
      if (var >= paramCount) {
         varRef = this._uiDefs._defs[uiIndex + 13];
         var -= paramCount;
      }

      return this._uiDefs._varData[varRef + 1 + var];
   }

   public int getVarIndexByCode(int screenId, int varCode) {
      int uiIndex = this._idToDefIndex.get(screenId);
      int varRef = this._uiDefs._defs[uiIndex + 6];
      if (varRef != -1) {
         int size = this._uiDefs._varData[varRef + 0];
         varRef++;

         for (int i = 0; i < size; i++) {
            if (varCode == this._uiDefs._varData[varRef + i]) {
               return i;
            }
         }
      }

      return -1;
   }

   public int getComponentType(int componentIndex) {
      return this._uiDefs._defs[componentIndex + 0];
   }

   public int getComponentId(int componentIndex) {
      return this._uiDefs._defs[componentIndex + 1];
   }

   public int getComponentBits(int componentIndex) {
      return this._uiDefs._defs[componentIndex + 5];
   }

   public int getComponentStyle(int componentIndex) {
      return this._uiDefs._defs[componentIndex + 4];
   }

   public int getComponentX(int componentIndex) {
      return this._uiDefs._defs[componentIndex + 2];
   }

   public int getComponentY(int componentIndex) {
      return this._uiDefs._defs[componentIndex + 3];
   }

   public int getControlInit(int controlIndex) {
      return this._uiDefs._defs[controlIndex + 7];
   }

   public Object getControlInValue(int controlIndex) {
      int inValueIndex = this._uiDefs._defs[controlIndex + 6];
      return inValueIndex == -1 ? null : this._uiDefs._objectData[inValueIndex];
   }

   public boolean isManadatoryControl(int controlIndex) {
      return (this.getComponentBits(controlIndex) & 1) != 0;
   }

   public boolean isReadOnlyControl(int controlIndex) {
      return (this.getComponentBits(controlIndex) & 536870912) != 0;
   }

   public int getRegionLayout(int regionIndex) {
      return this._uiDefs._defs[regionIndex + 6];
   }

   public int getRegionChildrenArrayId(int regionIndex) {
      return this._uiDefs._defs[regionIndex + 7];
   }

   public int getEditType(int controlIndex) {
      return this._uiDefs._defs[controlIndex + 8];
   }

   public int getEditFocusOutId(int controlIndex) {
      return this._uiDefs._defs[controlIndex + 9];
   }

   public String getEditFormat(int controlIndex) {
      int formatIndex = this._uiDefs._defs[controlIndex + 11];
      return (String)(formatIndex == -1 ? null : this._uiDefs._objectData[formatIndex]);
   }

   public int[] getEditMapping(int controlIndex) {
      int mapIndex = this._uiDefs._defs[controlIndex + 10];
      return mapIndex == -1 ? null : (int[])this._uiDefs._objectData[mapIndex];
   }

   public int getTextAreaFocusOutId(int controlIndex) {
      return this._uiDefs._defs[controlIndex + 8];
   }

   public int getTextAreaVisibleRows(int controlIndex) {
      return this._uiDefs._defs[controlIndex + 10];
   }

   public int[] getTextAreaMapping(int controlIndex) {
      int mapIndex = this._uiDefs._defs[controlIndex + 9];
      return mapIndex == -1 ? null : (int[])this._uiDefs._objectData[mapIndex];
   }

   public int getChoiceType(int controlIndex) {
      return this._uiDefs._defs[controlIndex + 11];
   }

   public int getChoiceChangeId(int controlIndex) {
      return this._uiDefs._defs[controlIndex + 8];
   }

   public String getChoiceFormat(int controlIndex) {
      int formatIndex = this._uiDefs._defs[controlIndex + 10];
      return (String)(formatIndex == -1 ? null : this._uiDefs._objectData[formatIndex]);
   }

   public int[] getChoiceMapping(int controlIndex) {
      int mapIndex = this._uiDefs._defs[controlIndex + 9];
      return mapIndex == -1 ? null : (int[])this._uiDefs._objectData[mapIndex];
   }

   public int getChoiceVisibleRows(int controlIndex) {
      return this._uiDefs._defs[controlIndex + 12];
   }

   public int getButtonResourceId(int controlIndex) {
      return this._uiDefs._defs[controlIndex + 9];
   }

   public int getButtonClickId(int controlIndex) {
      return this._uiDefs._defs[controlIndex + 8];
   }

   public Object getButtonImageInValue(int controlIndex) {
      int imageInValueIndex = this._uiDefs._defs[controlIndex + 10];
      return imageInValueIndex == -1 ? null : this._uiDefs._objectData[imageInValueIndex];
   }

   public int getCheckboxChangeId(int controlIndex) {
      return this._uiDefs._defs[controlIndex + 8];
   }

   public int[] getCheckboxMapping(int controlIndex) {
      int mapIndex = this._uiDefs._defs[controlIndex + 9];
      return mapIndex == -1 ? null : (int[])this._uiDefs._objectData[mapIndex];
   }

   public boolean isWhitespaceSeparator(int controlIndex) {
      return (this.getComponentBits(controlIndex) & 4096) != 0;
   }

   public int getImageResourceId(int controlIndex) {
      return this._uiDefs._defs[controlIndex + 8];
   }

   public int[] getRepetitionMapping(int controlIndex) {
      int mapIndex = this._uiDefs._defs[controlIndex + 8];
      return mapIndex == -1 ? null : (int[])this._uiDefs._objectData[mapIndex];
   }

   public int getRepetitionChangeEventId(int controlIndex) {
      return this._uiDefs._defs[controlIndex + 10];
   }

   public int getRepetitionChildrenArrayId(int controlIndex) {
      return this._uiDefs._defs[controlIndex + 9];
   }

   public boolean isRepetitionCollapsible(int controlIndex) {
      return (this._uiDefs._defs[controlIndex + 5] & 4) != 0;
   }

   public int getRepetitionVisibleRows(int controlIndex) {
      return this._uiDefs._defs[controlIndex + 11];
   }

   public int getEventScript(int eventIndex) {
      return this._uiDefs._varData[eventIndex + 0];
   }

   public int getEventParamOffset(int eventIndex) {
      return eventIndex + 1;
   }

   public int[] getEventParamArray(int eventIndex) {
      return this._uiDefs._varData;
   }

   public int getClickTransitionId(int clickIndex) {
      return this._uiDefs._varData[clickIndex + 0];
   }

   public int getClickTransactionType(int clickIndex) {
      return this._uiDefs._varData[clickIndex + 1];
   }

   public int getClickParamOffset(int clickIndex) {
      return clickIndex + 2;
   }

   public int[] getClickParamArray(int clickIndex) {
      return this._uiDefs._varData;
   }

   public int getMenuItemCount(int menuId) {
      return this._uiDefs._varData[menuId + 1];
   }

   public int getMenuShowEvent(int menuId) {
      return this._uiDefs._varData[menuId + 0];
   }

   public Object getMenuItemValue(int menuId, int itemIndex) {
      int objIndex = this._uiDefs._varData[menuId + 2 + 4 * itemIndex + 0];
      return objIndex == -1 ? null : this._uiDefs._objectData[objIndex];
   }

   public int getMenuItemClickId(int menuId, int itemIndex) {
      return this._uiDefs._varData[menuId + 2 + 4 * itemIndex + 1];
   }

   public int getMenuItemId(int menuId, int itemIndex) {
      return this._uiDefs._varData[menuId + 2 + 4 * itemIndex + 2];
   }

   public int getMenuItemBits(int menuId, int itemIndex) {
      return this._uiDefs._varData[menuId + 2 + 4 * itemIndex + 3];
   }

   public ResourceCollection getResources() {
      return this;
   }

   public String getTableColumnName(int componentIndex) {
      int widthIndex = this._uiDefs._defs[componentIndex + 1];
      return (String)(widthIndex == -1 ? null : this._uiDefs._objectData[widthIndex]);
   }

   public boolean getTableColumnVisible(int componentIndex) {
      return (this._uiDefs._defs[componentIndex + 11] & 2) != 0;
   }

   public StyleCollection getStyles() {
      return this;
   }

   public boolean getTableColumnFrozen(int componentIndex) {
      return (this._uiDefs._defs[componentIndex + 11] & 1) != 0;
   }

   public int getTableColumnOddRowStyle(int componentIndex) {
      return this._uiDefs._defs[componentIndex + 10];
   }

   public int getTableColumnEvenRowStyle(int componentIndex) {
      return this._uiDefs._defs[componentIndex + 9];
   }

   public int getArraySize(int index) {
      return index == -1 ? 0 : this._uiDefs._varData[index + 0];
   }

   public int getArrayElementAt(int array, int offset) {
      return this._uiDefs._varData[array + 1 + offset];
   }

   public ComponentDefStruct getUiDefs() {
      return this._uiDefs;
   }

   public int getTableChildrenArrayId(int controlIndex) {
      return this._uiDefs._defs[controlIndex + 17];
   }

   public int getTableChangeEventId(int controlIndex) {
      return this._uiDefs._defs[controlIndex + 15];
   }

   public int getTableFetchMoreEventId(int controlIndex) {
      return this._uiDefs._defs[controlIndex + 16];
   }

   public int getTableHeaderStyle(int componentIndex) {
      return this._uiDefs._defs[componentIndex + 9];
   }

   public int getTableEvenRowStyle(int componentIndex) {
      return this._uiDefs._defs[componentIndex + 10];
   }

   public int getTableOddRowStyle(int componentIndex) {
      return this._uiDefs._defs[componentIndex + 11];
   }

   public String getTableGridlineColor(int componentIndex) {
      int gridlineIndex = this._uiDefs._defs[componentIndex + 12];
      return (String)(gridlineIndex == -1 ? null : this._uiDefs._objectData[gridlineIndex]);
   }

   public boolean isTableShowGridline(int componentIndex) {
      return (this._uiDefs._defs[componentIndex + 14] & 1) != 0;
   }

   public boolean isTableShowHeader(int componentIndex) {
      return (this._uiDefs._defs[componentIndex + 14] & 4) != 0;
   }

   public boolean isTableShowSelector(int componentIndex) {
      return (this._uiDefs._defs[componentIndex + 14] & 2) != 0;
   }

   public int getTableVisibleRows(int componentIndex) {
      return this._uiDefs._defs[componentIndex + 13];
   }

   public int[] getTableMapping(int componentIndex) {
      int mapIndex = this._uiDefs._defs[componentIndex + 8];
      return mapIndex == -1 ? null : (int[])this._uiDefs._objectData[mapIndex];
   }

   public Object getTableColumnTitle(int componentIndex) {
      int titleIndex = this._uiDefs._defs[componentIndex + 2];
      return titleIndex == -1 ? null : this._uiDefs._objectData[titleIndex];
   }

   public Object getTableColumnInValue(int componentIndex) {
      int inValueIndex = this._uiDefs._defs[componentIndex + 3];
      return inValueIndex == -1 ? null : this._uiDefs._objectData[inValueIndex];
   }

   public int getTableColumnFormatType(int componentIndex) {
      return this._uiDefs._defs[componentIndex + 5];
   }

   public String getTableColumnFormat(int componentIndex) {
      int formatIndex = this._uiDefs._defs[componentIndex + 5];
      return (String)(formatIndex == -1 ? null : this._uiDefs._objectData[formatIndex]);
   }

   public String getTableColumnWidth(int componentIndex) {
      int widthIndex = this._uiDefs._defs[componentIndex + 6];
      return (String)(widthIndex == -1 ? null : this._uiDefs._objectData[widthIndex]);
   }

   public int getTableColumnStyle(int componentIndex) {
      return this._uiDefs._defs[componentIndex + 7];
   }

   public int getTableColumnHeaderStyle(int componentIndex) {
      return this._uiDefs._defs[componentIndex + 8];
   }

   @Override
   public String getStringProperty(int styleDefId, int property) {
      int styleIndex = this._idToDefIndex.get(styleDefId);
      switch (property) {
         case 2:
            int fontIndex = this._uiDefs._defs[styleIndex + 2];
            if (fontIndex == -1) {
               return null;
            }

            return (String)this._uiDefs._objectData[fontIndex];
         default:
            throw new Object("Invalid style property");
      }
   }

   @Override
   public int getIntProperty(int styleDefId, int property) {
      int styleIndex = this._idToDefIndex.get(styleDefId);
      switch (property) {
         case 0:
            return this._uiDefs._defs[styleIndex + 4];
         case 1:
            return this._uiDefs._defs[styleIndex + 3];
         case 3:
            return this._uiDefs._defs[styleIndex + 5];
         case 8:
            return this._uiDefs._defs[styleIndex + 7];
         default:
            throw new Object("Invalid style property");
      }
   }

   @Override
   public boolean hasProperty(int styleDefId, int property) {
      int styleIndex = this._idToDefIndex.get(styleDefId);
      switch (property) {
         case -1:
         case 6:
            throw new Object("Invalid style property");
         case 0:
         default:
            if (this._uiDefs._defs[styleIndex + 4] != -1) {
               return true;
            }

            return false;
         case 1:
            if (this._uiDefs._defs[styleIndex + 3] != -1) {
               return true;
            }

            return false;
         case 2:
            if (this._uiDefs._defs[styleIndex + 2] != -1) {
               return true;
            }

            return false;
         case 3:
            if (this._uiDefs._defs[styleIndex + 5] != -1) {
               return true;
            }

            return false;
         case 4:
            if ((this._uiDefs._defs[styleIndex + 6] & 1) != 0) {
               return true;
            }

            return false;
         case 5:
            if ((this._uiDefs._defs[styleIndex + 6] & 2) != 0) {
               return true;
            }

            return false;
         case 7:
            if ((this._uiDefs._defs[styleIndex + 6] & 4) != 0) {
               return true;
            }

            return false;
         case 8:
            return this._uiDefs._defs[styleIndex + 7] != -1;
      }
   }

   @Override
   public String getResourceContentType(int resourceDefId) {
      int resIndex = this._idToDefIndex.get(resourceDefId);
      int objIndex = this._uiDefs._defs[resIndex + 3];
      return (String)(objIndex == -1 ? null : this._uiDefs._objectData[objIndex]);
   }

   @Override
   public String getResourceURI(int resourceDefId) {
      int resIndex = this._idToDefIndex.get(resourceDefId);
      int objIndex = this._uiDefs._defs[resIndex + 2];
      return (String)this._uiDefs._objectData[objIndex];
   }

   public UIDefAccess(ComponentDefStruct uiDefs) {
      this._uiDefs = uiDefs;
      int size = this._uiDefs._defs.length;
      this._idToDefIndex = (IntIntHashtable)(new Object());
      int i = 0;

      while (i < size) {
         int type = this._uiDefs._defs[i + 0];
         if ((type & 128) != 0) {
            switch (type) {
               case 127:
               case 136:
               case 138:
                  throw new Object("Illegal control definition in ui defs");
               case 128:
               default:
                  i += 8;
                  break;
               case 129:
                  i += 12;
                  break;
               case 130:
                  i += 13;
                  break;
               case 131:
                  i += 11;
                  break;
               case 132:
                  i += 8;
                  break;
               case 133:
                  i += 9;
                  break;
               case 134:
                  i += 8;
                  break;
               case 135:
                  i += 11;
                  break;
               case 137:
                  i += 12;
                  break;
               case 139:
                  i += 12;
                  break;
               case 140:
                  i += 18;
                  break;
               case 141:
                  i += 12;
            }
         } else {
            this._idToDefIndex.put(this._uiDefs._defs[i + 1], i);
            switch (type) {
               case 9:
               case 11:
                  throw new Object("Illegal definition in ui defs");
               case 10:
               default:
                  i += 15;
                  break;
               case 12:
                  i += 5;
                  break;
               case 13:
                  i += 8;
            }
         }
      }
   }
}
