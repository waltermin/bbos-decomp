package net.rim.wica.runtime.provisioning.internal;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.IntVector;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.api.util.ToIntHashtable;
import net.rim.wica.runtime.metadata.internal.def.ComponentDefStruct;
import net.rim.wica.runtime.provisioning.internal.elements.AbstractElement;
import net.rim.wica.runtime.provisioning.internal.elements.ButtonElement;
import net.rim.wica.runtime.provisioning.internal.elements.CheckboxElement;
import net.rim.wica.runtime.provisioning.internal.elements.ColumnElement;
import net.rim.wica.runtime.provisioning.internal.elements.CommonControlElement;
import net.rim.wica.runtime.provisioning.internal.elements.CommonScriptBindingElement;
import net.rim.wica.runtime.provisioning.internal.elements.DataElement;
import net.rim.wica.runtime.provisioning.internal.elements.EditElement;
import net.rim.wica.runtime.provisioning.internal.elements.ImageElement;
import net.rim.wica.runtime.provisioning.internal.elements.LabelElement;
import net.rim.wica.runtime.provisioning.internal.elements.MenuElement;
import net.rim.wica.runtime.provisioning.internal.elements.MenuItemElement;
import net.rim.wica.runtime.provisioning.internal.elements.MessageElement;
import net.rim.wica.runtime.provisioning.internal.elements.MessageElementReference;
import net.rim.wica.runtime.provisioning.internal.elements.MultiChoiceElement;
import net.rim.wica.runtime.provisioning.internal.elements.OnClickElement;
import net.rim.wica.runtime.provisioning.internal.elements.ParamElement;
import net.rim.wica.runtime.provisioning.internal.elements.RegionElement;
import net.rim.wica.runtime.provisioning.internal.elements.RepetitionElement;
import net.rim.wica.runtime.provisioning.internal.elements.ResourceElement;
import net.rim.wica.runtime.provisioning.internal.elements.ScreenElement;
import net.rim.wica.runtime.provisioning.internal.elements.ScriptElement;
import net.rim.wica.runtime.provisioning.internal.elements.SeparatorElement;
import net.rim.wica.runtime.provisioning.internal.elements.SingleChoiceElement;
import net.rim.wica.runtime.provisioning.internal.elements.StyleElement;
import net.rim.wica.runtime.provisioning.internal.elements.TableElement;
import net.rim.wica.runtime.provisioning.internal.elements.TableStyling;
import net.rim.wica.runtime.provisioning.internal.elements.TextAreaElement;
import net.rim.wica.runtime.provisioning.internal.elements.VarElement;
import net.rim.wica.runtime.provisioning.internal.elements.WicletElement;
import net.rim.wica.runtime.util.Util;

public final class ScreenVisitor extends DefinitionVisitorAdapter {
   private EncodingContext _uiDefs;
   private UniqueCodeGenerator _uniqueCodeGenerator;
   private int _globalDefId;
   private WicletElement _wiclet;
   private ToIntHashtable _paramsAndLocalsPos = new ToIntHashtable();
   private Hashtable _paramsAndLocals = new Hashtable();
   private int _screenOffset;
   private ToIntHashtable _childrenArrays = new ToIntHashtable();
   private ScreenVisitor$ReferenceUtil _refUtil = new ScreenVisitor$ReferenceUtil(this, null);
   private boolean _inReferenceContainer;
   private DataElement _referenceContainerContext;

   public ScreenVisitor(UniqueCodeGenerator ucg, int globalDefId, WicletElement wiclet) {
      this._uniqueCodeGenerator = ucg;
      this._uiDefs = new EncodingContext();
      this._globalDefId = globalDefId;
      this._wiclet = wiclet;
   }

   public final ComponentDefStruct getScreenDefs() {
      return this._uiDefs.toComponentDefStruct();
   }

   @Override
   public final boolean visitResourceElement(ResourceElement resource) {
      IntVector defs = this._uiDefs._defs;
      int offset = defs.size();
      defs.setSize(offset + 5);
      defs.setElementAt(12, offset + 0);
      defs.setElementAt(this._uniqueCodeGenerator.generateCode(resource.getName()), offset + 1);
      defs.setElementAt(this.encodeObject(resource.getUrl()), offset + 2);
      defs.setElementAt(this.encodeObject(resource.getMimeType()), offset + 3);
      return false;
   }

   @Override
   public final boolean visitScreenElement(ScreenElement screen) {
      this._paramsAndLocalsPos.clear();
      this._paramsAndLocals.clear();
      this._childrenArrays.clear();
      this._inReferenceContainer = false;
      this._referenceContainerContext = null;
      IntVector defs = this._uiDefs._defs;
      this._screenOffset = defs.size();
      defs.setSize(this._screenOffset + 15);
      defs.setElementAt(10, this._screenOffset + 0);
      defs.setElementAt(this._uniqueCodeGenerator.generateCode(screen.getName()), this._screenOffset + 1);
      int varRef = this.encodeParams(screen.getParams());
      defs.setElementAt(varRef, this._screenOffset + 3);
      varRef = this.encodeLocals(screen.getVars());
      defs.setElementAt(varRef, this._screenOffset + 13);
      int objRef = this.encodeObjectInValue(screen.getTitle());
      defs.setElementAt(objRef, this._screenOffset + 2);
      defs.setElementAt(screen.isDialog() ? 1 : 0, this._screenOffset + 4);
      defs.setElementAt(this.encodeLayoutType(screen.getLayout()), this._screenOffset + 5);
      varRef = this.encodeParamAndLocalsNames();
      defs.setElementAt(varRef, this._screenOffset + 6);
      StyleElement style = screen.getStyle();
      defs.setElementAt(style == null ? -1 : this._uniqueCodeGenerator.generateCode(style.getName()), this._screenOffset + 7);
      varRef = this.encodeRefreshMsgs(screen.getRefreshMsgs());
      defs.setElementAt(varRef, this._screenOffset + 8);
      varRef = this.encodeEvent(screen.getOninit());
      defs.setElementAt(varRef, this._screenOffset + 9);
      varRef = this.encodeEvent(screen.getOnShow());
      defs.setElementAt(varRef, this._screenOffset + 14);
      varRef = this.encodeMenu(screen.getMenu());
      defs.setElementAt(varRef, this._screenOffset + 10);
      defs.setElementAt(0, this._screenOffset + 11);
      varRef = this._uiDefs._varData.size();
      int childrenCount = screen.getControls().size();
      this._uiDefs._varData.setSize(varRef + childrenCount + 1);
      defs.setElementAt(varRef, this._screenOffset + 12);
      this._childrenArrays.put(screen, varRef);
      return true;
   }

   @Override
   public final boolean visitStyleElement(StyleElement style) {
      IntVector defs = this._uiDefs._defs;
      int offset = defs.size();
      defs.setSize(offset + 8);
      defs.setElementAt(13, offset + 0);
      defs.setElementAt(this._uniqueCodeGenerator.generateCode(style.getName()), offset + 1);
      defs.setElementAt(this.encodeObject(style.getFont()), offset + 2);
      String tmpString = style.getFgColor();
      defs.setElementAt(tmpString != null ? Integer.parseInt(tmpString.substring(1), 16) : -1, offset + 3);
      tmpString = style.getBgColor();
      defs.setElementAt(tmpString != null ? Integer.parseInt(tmpString.substring(1), 16) : -1, offset + 4);
      tmpString = style.getSize();
      defs.setElementAt(tmpString != null ? Integer.parseInt(tmpString) : -1, offset + 5);
      int fontStyle = (style.isBold() ? 1 : 0) | (style.isItalic() ? 2 : 0) | (style.isUnderline() ? 4 : 0);
      defs.setElementAt(fontStyle, offset + 6);
      ResourceElement bgImageRes = style.getBgImage();
      defs.setElementAt(bgImageRes == null ? -1 : this._uniqueCodeGenerator.generateCode(bgImageRes.getName()), offset + 7);
      return false;
   }

   @Override
   public final boolean visitRegionElement(RegionElement element) {
      IntVector defs = this._uiDefs._defs;
      int offset = defs.size();
      this.encodeCommonComponentFields(offset, element, element.getName(), 8, 128, 0);
      defs.setElementAt(this.encodeLayoutType(element.getLayout()), offset + 6);
      int varRef = this._uiDefs._varData.size();
      this._uiDefs._varData.setSize(varRef + element.getNestedElements().size() + 1);
      defs.setElementAt(varRef, offset + 7);
      this._childrenArrays.put(element, varRef);
      return true;
   }

   @Override
   public final boolean visitLabelElement(LabelElement element) {
      int offset = this._uiDefs._defs.size();
      this.encodeCommonComponentFields(offset, element, element.getName(), 8, 134, 0);
      this.encodeCommonControlFields(offset, element);
      return false;
   }

   @Override
   public final boolean visitSeparatorElement(SeparatorElement element) {
      int offset = this._uiDefs._defs.size();
      this.encodeCommonComponentFields(offset, element, element.getName(), 8, 132, element.isWhitespace() ? 4096 : 0);
      this.encodeCommonControlFields(offset, element);
      return false;
   }

   @Override
   public final boolean visitEditElement(EditElement element) {
      IntVector defs = this._uiDefs._defs;
      int offset = defs.size();
      int bits = (element.isMandatory() ? 1 : 0) | (element.isReadOnly() ? 536870912 : 268435456);
      this.encodeCommonComponentFields(offset, element, element.getName(), 12, 129, bits);
      this.encodeCommonControlFields(offset, element);
      this.encodeEditType(offset + 8, element.getType());
      int varRef = this.encodeEvent(element.getOnFocusOut());
      defs.setElementAt(varRef, offset + 9);
      int objRef = this.encodeMapping(element.getMapping());
      defs.setElementAt(objRef, offset + 10);
      defs.setElementAt(this.encodeObject(element.getFormat()), offset + 11);
      return false;
   }

   @Override
   public final boolean visitTextareaElement(TextAreaElement element) {
      IntVector defs = this._uiDefs._defs;
      int offset = defs.size();
      int bits = (element.isMandatory() ? 1 : 0) | (element.isReadOnly() ? 536870912 : 268435456);
      this.encodeCommonComponentFields(offset, element, element.getName(), 11, 135, bits);
      this.encodeCommonControlFields(offset, element);
      int varRef = this.encodeEvent(element.getOnFocusOut());
      defs.setElementAt(varRef, offset + 8);
      int objRef = this.encodeMapping(element.getMapping());
      defs.setElementAt(objRef, offset + 9);
      String visibleRows = element.getVisibleRows();
      int rows = visibleRows == null ? 3 : Integer.parseInt(visibleRows);
      defs.setElementAt(rows, offset + 10);
      return false;
   }

   @Override
   public final boolean visitImageElement(ImageElement element) {
      int offset = this._uiDefs._defs.size();
      this.encodeCommonComponentFields(offset, element, element.getName(), 9, 133, 0);
      this.encodeCommonControlFields(offset, element);
      ResourceElement res = element.getResource();
      this._uiDefs._defs.setElementAt(res == null ? -1 : this._uniqueCodeGenerator.generateCode(res.getName()), offset + 8);
      return false;
   }

   @Override
   public final boolean visitSingleChoiceElement(SingleChoiceElement element) {
      IntVector defs = this._uiDefs._defs;
      int offset = defs.size();
      this.encodeCommonComponentFields(offset, element, element.getName(), 13, 130, 0);
      this.encodeCommonControlFields(offset, element);
      int varRef = this.encodeEvent(element.getOnChange());
      defs.setElementAt(varRef, offset + 8);
      int objRef = this.encodeMapping(element.getMapping());
      defs.setElementAt(objRef, offset + 9);
      defs.setElementAt(this.encodeObject(element.getFormat()), offset + 10);
      String choiceType = element.getType();
      int type = 3;
      if (choiceType != null) {
         if (choiceType.equalsIgnoreCase("list")) {
            type = 0;
         } else if (choiceType.equalsIgnoreCase("dropdown")) {
            type = 2;
         }
      }

      defs.setElementAt(type, offset + 11);
      String visibleRows = element.getVisibleRows();
      int rows = visibleRows == null ? Integer.MAX_VALUE : Integer.parseInt(visibleRows);
      defs.setElementAt(rows, offset + 12);
      return false;
   }

   @Override
   public final boolean visitMultiChoiceElement(MultiChoiceElement element) {
      IntVector defs = this._uiDefs._defs;
      int offset = defs.size();
      int bits = element.isMandatory() ? 1 : 0;
      this.encodeCommonComponentFields(offset, element, element.getName(), 13, 130, bits);
      this.encodeCommonControlFields(offset, element);
      int varRef = this.encodeEvent(element.getOnChange());
      defs.setElementAt(varRef, offset + 8);
      int objRef = this.encodeMapping(element.getMapping());
      defs.setElementAt(objRef, offset + 9);
      defs.setElementAt(this.encodeObject(element.getFormat()), offset + 10);
      String choiceType = element.getType();
      int type = 4;
      if (choiceType != null && choiceType.equalsIgnoreCase("list")) {
         type = 1;
      }

      defs.setElementAt(type, offset + 11);
      String visibleRows = element.getVisibleRows();
      int rows = visibleRows == null ? Integer.MAX_VALUE : Integer.parseInt(visibleRows);
      defs.setElementAt(rows, offset + 12);
      return false;
   }

   @Override
   public final boolean visitCheckboxElement(CheckboxElement element) {
      IntVector defs = this._uiDefs._defs;
      int offset = defs.size();
      int bits = element.isReadOnly() ? 536870912 : 268435456;
      this.encodeCommonComponentFields(offset, element, element.getName(), 12, 139, bits);
      this.encodeCommonControlFields(offset, element);
      int varRef = this.encodeEvent(element.getOnChange());
      defs.setElementAt(varRef, offset + 8);
      int objRef = this.encodeMapping(element.getMapping());
      defs.setElementAt(objRef, offset + 9);
      return false;
   }

   @Override
   public final boolean visitButtonElement(ButtonElement element) {
      IntVector defs = this._uiDefs._defs;
      int offset = defs.size();
      this.encodeCommonComponentFields(offset, element, element.getName(), 11, 131, 0);
      this.encodeCommonControlFields(offset, element);
      int varRef = this.encodeOnClick(element.getOnClick());
      defs.setElementAt(varRef, offset + 8);
      ResourceElement res = element.getResource();
      this._uiDefs._defs.setElementAt(res == null ? -1 : this._uniqueCodeGenerator.generateCode(res.getName()), offset + 9);
      this._uiDefs._defs.setElementAt(this.encodeObjectInValue(element.getImageValue()), offset + 10);
      return false;
   }

   @Override
   public final boolean visitRepetitionElement(RepetitionElement element) {
      IntVector defs = this._uiDefs._defs;
      int offset = defs.size();
      int bits = 0;
      if (element.isCollapsible()) {
         bits |= 4;
      }

      this.encodeCommonComponentFields(offset, element, element.getName(), 12, 137, bits);
      defs.setElementAt(this.encodeObjectInValue(element.getInValue()), offset + 6);
      this._referenceContainerContext = this._refUtil.getLastContext();
      int varRef = this.encodeEvent(element.getOnInit());
      defs.setElementAt(varRef, offset + 7);
      varRef = this.encodeEvent(element.getOnChange());
      defs.setElementAt(varRef, offset + 10);
      int objRef = this.encodeMapping(element.getMapping());
      this._uiDefs._defs.setElementAt(objRef, offset + 8);
      varRef = this._uiDefs._varData.size();
      this._uiDefs._varData.setSize(varRef + element.getNestedElements().size() + 1);
      this._uiDefs._defs.setElementAt(varRef, offset + 9);
      this._childrenArrays.put(element, varRef);
      this._inReferenceContainer = true;
      defs.setElementAt(element.getVisibleRows(), offset + 11);
      return true;
   }

   @Override
   public final boolean visitColumnElement(ColumnElement element) {
      IntVector defs = this._uiDefs._defs;
      int offset = defs.size();
      defs.setSize(offset + 12);
      defs.setElementAt(141, offset + 0);
      defs.setElementAt(this.encodeObject(element.getName()), offset + 1);
      defs.setElementAt(this.encodeObjectInValue(element.getTitle()), offset + 2);
      defs.setElementAt(this.encodeObjectInValue(element.getInValue()), offset + 3);
      this.encodeEditType(offset + 4, element.getType());
      defs.setElementAt(this.encodeObject(element.getFormat()), offset + 5);
      defs.setElementAt(this.encodeObject(element.getWidth()), offset + 6);
      StyleElement style = element.getStyle();
      defs.setElementAt(style == null ? -1 : this._uniqueCodeGenerator.generateCode(style.getName()), offset + 7);
      this.encodeTableStyles(element, offset + 8, offset + 10, offset + 9);
      this.encodeTableColumnBits(offset, element);
      int varRef = this._childrenArrays.get(element.getParent());
      int currentCount = this._uiDefs._varData.elementAt(varRef) + 1;
      this._uiDefs._varData.setElementAt(currentCount, varRef + 0);
      this._uiDefs._varData.setElementAt(offset, varRef + 1 + currentCount - 1);
      return true;
   }

   @Override
   public final boolean visitTableElement(TableElement element) {
      IntVector defs = this._uiDefs._defs;
      int offset = defs.size();
      int bits = 0;
      this.encodeCommonComponentFields(offset, element, element.getName(), 18, 140, bits);
      defs.setElementAt(this.encodeObjectInValue(element.getInValue()), offset + 6);
      this._referenceContainerContext = this._refUtil.getLastContext();
      int varRef = this.encodeEvent(element.getOnInit());
      defs.setElementAt(varRef, offset + 7);
      varRef = this.encodeEvent(element.getOnChange());
      defs.setElementAt(varRef, offset + 15);
      varRef = this.encodeEvent(element.getOnMoreData());
      defs.setElementAt(varRef, offset + 16);
      int objRef = this.encodeMapping(element.getMapping());
      this._uiDefs._defs.setElementAt(objRef, offset + 8);
      this.encodeTableStyles(element, offset + 9, offset + 11, offset + 10);
      defs.setElementAt(this.encodeObject(element.getGridlineColor()), offset + 12);
      defs.setElementAt(element.getVisibleRows(), offset + 13);
      this.encodeTableBits(offset, element);
      varRef = this._uiDefs._varData.size();
      this._uiDefs._varData.setSize(varRef + element.getNestedElements().size() + 1);
      this._uiDefs._defs.setElementAt(varRef, offset + 17);
      this._childrenArrays.put(element, varRef);
      this._inReferenceContainer = true;
      return true;
   }

   private final int blindIndexOf(String s, int startPos, char ch) {
      int index = s.indexOf(ch, startPos);
      return index == -1 ? s.length() : index;
   }

   private final int encodeObjectInValue(String expr) {
      if (expr == null) {
         return -1;
      }

      if (expr.indexOf(64) == -1) {
         this._uiDefs._objectData.addElement(expr);
         return this._uiDefs._objectData.size() - 1;
      }

      Vector vector = new Vector();
      int len = expr.length();
      int lastPos = 0;

      while (true) {
         String sub = "";

         while (lastPos < len) {
            int indexOfNextAt = this.blindIndexOf(expr, lastPos, '@');
            sub = sub + expr.substring(lastPos, indexOfNextAt);
            lastPos = indexOfNextAt == len ? indexOfNextAt : indexOfNextAt + 1;
            if (lastPos >= len || expr.charAt(lastPos) != '@') {
               break;
            }

            sub = sub + '@';
            lastPos++;
         }

         if (sub != null && sub.length() > 0) {
            vector.addElement(sub);
         }

         int refStart = lastPos;

         while (true) {
            if (lastPos < len) {
               char refCh = expr.charAt(lastPos);
               if (refCh == '[') {
                  if (lastPos == len - 1 || (lastPos = this.blindIndexOf(expr, lastPos + 1, ']') + 1) > len) {
                     throw new RuntimeException("Encountered where filter without closing ]");
                  }
                  continue;
               }

               if (refCh != '@' && (CharacterUtilities.isLetter(refCh) || CharacterUtilities.isDigit(refCh) || refCh == '.' || refCh == '_' || refCh == '*')) {
                  lastPos++;
                  continue;
               }
            }

            sub = expr.substring(refStart, lastPos);
            if (sub.length() > 0) {
               vector.addElement(this._refUtil.breakDownInValueReference(sub));
            }

            if (lastPos >= len) {
               if (vector.size() == 1) {
                  this._uiDefs._objectData.addElement(vector.elementAt(0));
               } else {
                  Object[] array = new Object[vector.size()];
                  vector.copyInto(array);
                  this._uiDefs._objectData.addElement(array);
               }

               return this._uiDefs._objectData.size() - 1;
            }
            break;
         }
      }
   }

   private final int encodeParams(Vector params) {
      if (params != null && params.size() != 0) {
         IntVector vars = this._uiDefs._varData;
         int startIndex = vars.size();
         vars.addElement(params.size());
         Enumeration paramEnum = params.elements();

         while (paramEnum.hasMoreElements()) {
            ParamElement param = (ParamElement)paramEnum.nextElement();
            this._paramsAndLocalsPos.put(param.getName(), this._paramsAndLocalsPos.size());
            this._paramsAndLocals.put(param.getName(), param);
            vars.addElement(this._uniqueCodeGenerator.generateCode(param.getComponent().getName()));
         }

         return startIndex;
      } else {
         return -1;
      }
   }

   private final int encodeLocals(Vector localVars) {
      if (localVars != null && localVars.size() != 0) {
         IntVector vars = this._uiDefs._varData;
         int startIndex = vars.size();
         vars.addElement(localVars.size());
         Enumeration varsEnum = localVars.elements();

         while (varsEnum.hasMoreElements()) {
            VarElement localVar = (VarElement)varsEnum.nextElement();
            this._paramsAndLocalsPos.put(localVar.getName(), this._paramsAndLocalsPos.size());
            this._paramsAndLocals.put(localVar.getName(), localVar);
            vars.addElement(this._uniqueCodeGenerator.generateCode(localVar.getComponent().getName()));
         }

         return startIndex;
      } else {
         return -1;
      }
   }

   private final int encodeLayoutType(String layout) {
      if (layout.equalsIgnoreCase("grid")) {
         return 3;
      } else if (layout.equalsIgnoreCase("flow")) {
         return 1;
      } else {
         return layout.equalsIgnoreCase("edge") ? 4 : 2;
      }
   }

   private final int encodeRefreshMsgs(Vector refreshMsgs) {
      if (refreshMsgs != null && refreshMsgs.size() != 0) {
         IntVector varData = this._uiDefs._varData;
         int startOffset = varData.size();
         varData.setSize(startOffset + 1 + refreshMsgs.size());
         varData.setElementAt(refreshMsgs.size(), startOffset + 0);
         Enumeration msgEnum = refreshMsgs.elements();

         for (int index = startOffset + 1; msgEnum.hasMoreElements(); index++) {
            MessageElementReference msgRef = (MessageElementReference)msgEnum.nextElement();
            int code = this._uniqueCodeGenerator.generateCode(((MessageElement)msgRef.resolve()).getName());
            varData.setElementAt(code, index);
         }

         return startOffset;
      } else {
         return -1;
      }
   }

   private final int encodeEvent(CommonScriptBindingElement event) {
      if (event == null) {
         return -1;
      }

      IntVector varData = this._uiDefs._varData;
      int startOffset = varData.size();
      varData.setSize(startOffset + 2);
      varData.setElementAt(this._uniqueCodeGenerator.generateCode(event.getScript().getName()), startOffset + 0);
      varData.setElementAt(0, startOffset + 1);
      this.breakDownAndEncodeParams(event.getParams(), varData, startOffset + 1);
      return startOffset;
   }

   private final int encodeMenu(MenuElement menu) {
      if (menu == null) {
         return -1;
      }

      IntVector varData = this._uiDefs._varData;
      int startOffset = varData.size();
      int itemStartOffset = startOffset + 2;
      Vector items = menu.getItems();
      int itemCount = items.size();
      varData.setSize(itemStartOffset + itemCount * 4);
      int varRef = this.encodeEvent(menu.getOnShow());
      varData.setElementAt(varRef, startOffset + 0);
      varData.setElementAt(itemCount, startOffset + 1);
      Enumeration itemEnum = items.elements();

      for (int currentItemOffset = itemStartOffset; itemEnum.hasMoreElements(); currentItemOffset += 4) {
         MenuItemElement item = (MenuItemElement)itemEnum.nextElement();
         int objRef = this.encodeObjectInValue(item.getInValue());
         varData.setElementAt(objRef, currentItemOffset + 0);
         varRef = this.encodeOnClick(item.getOnClick());
         varData.setElementAt(varRef, currentItemOffset + 1);
         varData.setElementAt(this._uniqueCodeGenerator.generateCode(item.getName()), currentItemOffset + 2);
         varData.setElementAt(item.getVisible() ? 2 : 0, currentItemOffset + 3);
      }

      return startOffset;
   }

   private final int encodeOnClick(OnClickElement onClick) {
      if (onClick == null) {
         return -1;
      }

      IntVector varData = this._uiDefs._varData;
      int startOffset = varData.size();
      varData.setSize(startOffset + 3);
      if (onClick.hasTransition()) {
         AbstractElement trans = onClick.getTransition();
         if (trans instanceof ScriptElement) {
            varData.setElementAt(this._uniqueCodeGenerator.generateCode(((ScriptElement)trans).getName()), startOffset + 0);
         } else {
            varData.setElementAt(this._uniqueCodeGenerator.generateCode(((ScreenElement)trans).getName()), startOffset + 0);
         }
      } else {
         varData.setElementAt(-1, startOffset + 0);
      }

      String transactionType = onClick.getTransaction();
      if (transactionType.equalsIgnoreCase("commit")) {
         varData.setElementAt(1, startOffset + 1);
      } else if (transactionType.equalsIgnoreCase("rollback")) {
         varData.setElementAt(2, startOffset + 1);
      } else {
         varData.setElementAt(0, startOffset + 1);
      }

      varData.setElementAt(0, startOffset + 2);
      this.breakDownAndEncodeParams(onClick.getParams(), varData, startOffset + 2);
      return startOffset;
   }

   private final void breakDownAndEncodeParams(String params, IntVector varData, int startOffset) {
      if (params != null) {
         for (String param : Util.splitAndTrim(params, ',')) {
            if (param.length() == 0) {
               varData.addElement(2);
               varData.addElement(-1);
               varData.addElement(-1);
            } else {
               this._refUtil.breakDownReference(param, varData, null);
            }
         }

         byte length;
         varData.setElementAt(length, startOffset);
      }
   }

   private final void encodeCommonComponentFields(int componentOffset, CommonControlElement element, String name, int reserveSize, int componentType, int bits) {
      IntVector defs = this._uiDefs._defs;
      defs.setSize(componentOffset + reserveSize);
      defs.setElementAt(componentType, componentOffset + 0);
      defs.setElementAt(name == null ? -1 : this._uniqueCodeGenerator.generateCode(name), componentOffset + 1);
      String placement = element.getPlacement();
      int x;
      int y;
      if (placement != null) {
         StringTokenizer tokenizer = new StringTokenizer(element.getPlacement(), ' ');
         String sub = tokenizer.nextToken();
         x = sub != null && sub.length() != 0 ? Integer.parseInt(sub) : -1;
         sub = tokenizer.nextToken();
         y = sub != null && sub.length() != 0 ? Integer.parseInt(sub) : -1;
      } else {
         x = -1;
         y = -1;
      }

      defs.setElementAt(x, componentOffset + 2);
      defs.setElementAt(y, componentOffset + 3);
      StyleElement style = element.getStyle();
      defs.setElementAt(style == null ? -1 : this._uniqueCodeGenerator.generateCode(style.getName()), componentOffset + 4);
      if (element.getVisible()) {
         bits |= 2;
      } else {
         bits &= -3;
      }

      defs.setElementAt(bits, componentOffset + 5);
      int varRef = this._childrenArrays.get(element.getParent());
      int currentCount = this._uiDefs._varData.elementAt(varRef) + 1;
      this._uiDefs._varData.setElementAt(currentCount, varRef + 0);
      this._uiDefs._varData.setElementAt(componentOffset, varRef + 1 + currentCount - 1);
      if (!this._inReferenceContainer) {
         defs.setElementAt(defs.elementAt(this._screenOffset + 11) + 1, this._screenOffset + 11);
      }
   }

   private final void encodeCommonControlFields(int controlOffset, CommonControlElement element) {
      IntVector defs = this._uiDefs._defs;
      defs.setElementAt(this.encodeObjectInValue(element.getInValue()), controlOffset + 6);
      int varRef = this.encodeEvent(element.getOnInit());
      defs.setElementAt(varRef, controlOffset + 7);
   }

   private final void encodeEditType(int offset, String editType) {
      IntVector defs = this._uiDefs._defs;
      int type = 0;
      if (editType.equalsIgnoreCase("number")) {
         type = 1;
      } else if (editType.equalsIgnoreCase("currency")) {
         type = 2;
      } else if (editType.equalsIgnoreCase("date")) {
         type = 3;
      } else if (editType.equalsIgnoreCase("time")) {
         type = 4;
      } else if (editType.equalsIgnoreCase("dateTime")) {
         type = 11;
      } else if (editType.equalsIgnoreCase("percentage")) {
         type = 5;
      } else if (editType.equalsIgnoreCase("URL")) {
         type = 6;
      } else if (editType.equalsIgnoreCase("password")) {
         type = 7;
      } else if (editType.equalsIgnoreCase("phone")) {
         type = 8;
      } else if (editType.equalsIgnoreCase("email")) {
         type = 9;
      } else if (editType.equalsIgnoreCase("duration")) {
         type = 10;
      }

      defs.setElementAt(type, offset);
   }

   private final void encodeTableStyles(TableStyling tableStylingElement, int headerOffset, int oddRowOffset, int evenRowOffset) {
      IntVector defs = this._uiDefs._defs;
      StyleElement style = tableStylingElement.getHeaderStyle();
      defs.setElementAt(style == null ? -1 : this._uniqueCodeGenerator.generateCode(style.getName()), headerOffset);
      style = tableStylingElement.getOddRowStyle();
      defs.setElementAt(style == null ? -1 : this._uniqueCodeGenerator.generateCode(style.getName()), oddRowOffset);
      style = tableStylingElement.getEvenRowStyle();
      defs.setElementAt(style == null ? -1 : this._uniqueCodeGenerator.generateCode(style.getName()), evenRowOffset);
   }

   private final void encodeTableBits(int offset, TableElement element) {
      IntVector defs = this._uiDefs._defs;
      int bits = 0;
      if (element.isShowGridline()) {
         bits |= 1;
      }

      if (element.isShowRowSelector()) {
         bits |= 2;
      }

      if (element.isShowHeader()) {
         bits |= 4;
      }

      defs.setElementAt(bits, offset + 14);
   }

   private final void encodeTableColumnBits(int offset, ColumnElement element) {
      IntVector defs = this._uiDefs._defs;
      int bits = 0;
      if (element.isFrozen()) {
         bits |= 1;
      }

      if (element.isVisible()) {
         bits |= 2;
      }

      defs.setElementAt(bits, offset + 11);
   }

   private final int encodeObject(Object obj) {
      if (obj == null) {
         return -1;
      }

      int objRef = this._uiDefs._objectData.size();
      this._uiDefs._objectData.addElement(obj);
      return objRef;
   }

   private final int encodeParamAndLocalsNames() {
      IntVector varData = this._uiDefs._varData;
      int startOffset = varData.size();
      int elementStartOffset = startOffset + 1;
      int count = this._paramsAndLocalsPos.size();
      if (count == 0) {
         return -1;
      }

      varData.setSize(elementStartOffset + count);
      varData.setElementAt(count, startOffset + 0);
      Enumeration keysEnum = this._paramsAndLocalsPos.keys();

      while (keysEnum.hasMoreElements()) {
         String key = (String)keysEnum.nextElement();
         varData.setElementAt(this._uniqueCodeGenerator.generateCode(key), elementStartOffset + this._paramsAndLocalsPos.get(key));
      }

      return startOffset;
   }

   private final int encodeMapping(String reference) {
      if (reference != null && !this._inReferenceContainer) {
         Vector objectData = this._uiDefs._objectData;
         IntVector vector = new IntVector();
         this._refUtil.breakDownReferenceCommon(reference, vector, null);
         int[] array = new int[vector.size()];
         vector.copyInto(array);
         objectData.addElement(array);
         return objectData.size() - 1;
      } else {
         return -1;
      }
   }

   @Override
   public final void setCurrentElementVisited(AbstractElement ae) {
      super.setCurrentElementVisited(ae);
      if (this._inReferenceContainer) {
         AbstractElement e = ae;

         do {
            e = e.getParent();
         } while (!(e instanceof RepetitionElement) && !(e instanceof TableElement) && e != null);

         if (e == null) {
            this._inReferenceContainer = false;
            this._referenceContainerContext = null;
         }
      }
   }
}
