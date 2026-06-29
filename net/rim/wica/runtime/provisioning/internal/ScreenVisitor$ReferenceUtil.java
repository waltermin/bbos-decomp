package net.rim.wica.runtime.provisioning.internal;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.util.IntVector;
import net.rim.wica.runtime.provisioning.internal.elements.DataElement;
import net.rim.wica.runtime.provisioning.internal.elements.EnumerationElement;
import net.rim.wica.runtime.provisioning.internal.elements.FieldElement;
import net.rim.wica.runtime.provisioning.internal.elements.GlobalElement;
import net.rim.wica.runtime.provisioning.internal.elements.ParamElement;
import net.rim.wica.runtime.util.Util;

class ScreenVisitor$ReferenceUtil {
   private int _lastEnumDefId;
   private DataElement _lastContext;
   private final ScreenVisitor this$0;

   private ScreenVisitor$ReferenceUtil(ScreenVisitor this$0) {
      this.this$0 = this$0;
   }

   private int[] breakDownInValueReference(String reference) {
      IntVector vector = new IntVector();
      vector.addElement(0);
      int filterStart = reference.indexOf(91);
      int resType;
      if (filterStart != -1) {
         String sub = reference.substring(0, filterStart);
         resType = this.breakDownReference(sub, vector, null);
         int filterEnd = reference.indexOf(93, filterStart + 1);
         if (filterStart + 1 >= filterEnd - 1) {
            vector.addElement(-1);
         } else {
            vector.addElement(this.this$0._uiDefs._objectData.size());
            this.this$0._uiDefs._objectData.addElement(reference.substring(filterStart + 1, filterEnd).trim());
         }

         if (filterEnd < reference.length() - 2) {
            sub = reference.substring(filterEnd + 2);
            resType = this.breakDownReference(sub, vector, this._lastContext);
         }
      } else {
         resType = this.breakDownReference(reference, vector, null);
      }

      vector.setElementAt(resType, 0);
      if (this._lastEnumDefId != -1) {
         vector.insertElementAt(this._lastEnumDefId, 1);
      }

      int[] array = new int[vector.size()];
      vector.copyInto(array);
      return array;
   }

   private int breakDownReference(String reference, IntVector vector, DataElement contextData) {
      int startPos = vector.size();
      vector.addElement(0);
      int resultType = this.breakDownReferenceCommon(reference, vector, contextData);
      vector.setElementAt(vector.size() - startPos - 1, startPos);
      return resultType;
   }

   private int breakDownReferenceCommon(String reference, IntVector vector, DataElement contextData) {
      int resultType = -1;
      boolean isArray = false;
      this._lastEnumDefId = -1;
      this._lastContext = null;
      DataElement resolvedElement = null;
      String[] tokens = Util.split(reference, '.');
      int currentToken = 0;
      if (contextData != null) {
         resolvedElement = contextData;
      } else {
         String sub = tokens[currentToken++];
         if (this.this$0._wiclet.getStandardComponentResolver().isStandardComponent(sub)) {
            this.this$0._wiclet.getStandardComponentResolver().buildStandardComponents();
         }

         if (this.this$0._paramsAndLocalsPos.containsKey(sub)) {
            vector.addElement(-1);
            vector.addElement(this.this$0._paramsAndLocalsPos.get(sub));
            resolvedElement = ((ParamElement)this.this$0._paramsAndLocals.get(sub)).getComponent();
         } else if (!sub.equals("*") && !sub.equals("table")) {
            if (this.this$0._wiclet.getGlobalElement(sub) == null) {
               if (this.this$0._wiclet.getEnumerationElement(sub) != null) {
                  vector.addElement(this.this$0._uniqueCodeGenerator.generateCode(sub));
                  resolvedElement = null;
                  resultType = 5;
               } else {
                  vector.addElement(this.this$0._uniqueCodeGenerator.generateCode(sub));
                  resolvedElement = this.this$0._wiclet.getDataElement(sub);
               }
            } else {
               vector.addElement(this.this$0._globalDefId);
               Enumeration globalsEnum = this.this$0._wiclet.getGlobalElements().elements();
               GlobalElement ge = null;

               int i;
               for (i = 0; globalsEnum.hasMoreElements(); i++) {
                  ge = (GlobalElement)globalsEnum.nextElement();
                  if (ge.getName().equals(sub)) {
                     break;
                  }

                  ge = null;
               }

               vector.addElement(i);
               boolean isEnum = ge.getType().equals("enumeration");
               if (ge.hasComponent() && !isEnum) {
                  resolvedElement = (DataElement)ge.getComponent();
                  isArray = ge.isArray();
               } else {
                  if (isEnum) {
                     this._lastEnumDefId = this.this$0._uniqueCodeGenerator.generateCode(((EnumerationElement)ge.getComponent()).getName());
                  }

                  resolvedElement = null;
                  resultType = ProvisioningHelper.encodeType(ge.getType(), ge.isArray());
               }
            }
         } else {
            if (!this.this$0._inReferenceContainer) {
               throw new RuntimeException("Reference to '*' in a control that is not part of a reference container(i.e. repetition or table)");
            }

            if (this.this$0._referenceContainerContext == null) {
               throw new RuntimeException("Reference to '*' in a reference container(i.e. repetition or table) that has no proper inValue specified");
            }

            vector.addElement(-1);
            vector.addElement(-1);
            if (this.this$0._referenceContainerContext != null) {
               resolvedElement = this.this$0._referenceContainerContext;
            }
         }
      }

      if (resolvedElement != null) {
         FieldElement field = null;
         int fieldCount;
         if (currentToken < tokens.length) {
            Vector fields = new Vector();
            resolvedElement.resolvePath(tokens, fields, currentToken);
            DataElement dcontainer = resolvedElement;
            fieldCount = fields.size();

            for (int i = 0; i < fieldCount; i++) {
               field = (FieldElement)fields.elementAt(i);
               int fieldIndex = dcontainer.getFieldIndex(field.getName());
               vector.addElement(fieldIndex);
               dcontainer = field.hasComponent() && !field.getType().equals("enumeration") ? (DataElement)field.getComponent() : null;
            }
         } else {
            fieldCount = 0;
         }

         if (fieldCount > 0) {
            if (field.hasComponent()) {
               if (field.getType().equals("enumeration")) {
                  this._lastEnumDefId = this.this$0._uniqueCodeGenerator.generateCode(((EnumerationElement)field.getComponent()).getName());
               } else {
                  this._lastContext = (DataElement)field.getComponent();
               }
            }

            resultType = ProvisioningHelper.encodeType(field.getType(), field.isArray());
         } else {
            this._lastContext = resolvedElement;
            resultType = isArray ? 32774 : 6;
         }
      }

      return resultType;
   }

   public DataElement getLastContext() {
      return this._lastContext;
   }

   ScreenVisitor$ReferenceUtil(ScreenVisitor x0, ScreenVisitor$1 x1) {
      this(x0);
   }
}
