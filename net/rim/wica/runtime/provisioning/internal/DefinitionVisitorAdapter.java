package net.rim.wica.runtime.provisioning.internal;

import net.rim.wica.runtime.provisioning.internal.elements.AbstractElement;
import net.rim.wica.runtime.provisioning.internal.elements.AlertElement;
import net.rim.wica.runtime.provisioning.internal.elements.ButtonElement;
import net.rim.wica.runtime.provisioning.internal.elements.CheckboxElement;
import net.rim.wica.runtime.provisioning.internal.elements.ColumnElement;
import net.rim.wica.runtime.provisioning.internal.elements.DataElement;
import net.rim.wica.runtime.provisioning.internal.elements.DependencyElement;
import net.rim.wica.runtime.provisioning.internal.elements.EditElement;
import net.rim.wica.runtime.provisioning.internal.elements.EnumerationElement;
import net.rim.wica.runtime.provisioning.internal.elements.FieldElement;
import net.rim.wica.runtime.provisioning.internal.elements.GlobalElement;
import net.rim.wica.runtime.provisioning.internal.elements.ImageElement;
import net.rim.wica.runtime.provisioning.internal.elements.LabelElement;
import net.rim.wica.runtime.provisioning.internal.elements.MappedFieldElement;
import net.rim.wica.runtime.provisioning.internal.elements.MessageElement;
import net.rim.wica.runtime.provisioning.internal.elements.MultiChoiceElement;
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
import net.rim.wica.runtime.provisioning.internal.elements.TextAreaElement;
import net.rim.wica.runtime.provisioning.internal.elements.VarElement;
import net.rim.wica.runtime.provisioning.internal.elements.WicletElement;

public class DefinitionVisitorAdapter implements DefinitionVisitor {
   private AbstractElement _currentElementVisited;

   @Override
   public boolean visitAlertElement(AlertElement alert) {
      return false;
   }

   @Override
   public boolean visitCheckboxElement(CheckboxElement element) {
      return false;
   }

   @Override
   public boolean visitButtonElement(ButtonElement element) {
      return false;
   }

   @Override
   public boolean visitDataElement(DataElement data) {
      return false;
   }

   @Override
   public boolean visitDependencyElement(DependencyElement dependency) {
      return false;
   }

   @Override
   public boolean visitEditElement(EditElement element) {
      return false;
   }

   @Override
   public boolean visitEnumerationElement(EnumerationElement element) {
      return false;
   }

   @Override
   public boolean visitFieldElement(FieldElement field) {
      return false;
   }

   @Override
   public boolean visitGlobalElement(GlobalElement global) {
      return false;
   }

   @Override
   public boolean visitImageElement(ImageElement element) {
      return false;
   }

   @Override
   public boolean visitLabelElement(LabelElement element) {
      return false;
   }

   @Override
   public boolean visitMappedFieldElement(MappedFieldElement mappedField) {
      return false;
   }

   @Override
   public boolean visitMessageElement(MessageElement message) {
      return false;
   }

   @Override
   public boolean visitMultiChoiceElement(MultiChoiceElement element) {
      return false;
   }

   @Override
   public boolean visitParamElement(ParamElement element) {
      return false;
   }

   @Override
   public boolean visitRegionElement(RegionElement element) {
      return false;
   }

   @Override
   public boolean visitResourceElement(ResourceElement resource) {
      return false;
   }

   @Override
   public boolean visitScreenElement(ScreenElement screen) {
      return false;
   }

   @Override
   public boolean visitScriptElement(ScriptElement script) {
      return false;
   }

   @Override
   public boolean visitSeparatorElement(SeparatorElement element) {
      return false;
   }

   @Override
   public boolean visitSingleChoiceElement(SingleChoiceElement element) {
      return false;
   }

   @Override
   public boolean visitStyleElement(StyleElement style) {
      return false;
   }

   @Override
   public boolean visitTextareaElement(TextAreaElement element) {
      return false;
   }

   @Override
   public boolean visitVarElement(VarElement element) {
      return false;
   }

   @Override
   public boolean visitWicletElement(WicletElement wiclet) {
      return true;
   }

   @Override
   public boolean visitRepetitionElement(RepetitionElement element) {
      return false;
   }

   @Override
   public boolean visitColumnElement(ColumnElement element) {
      return false;
   }

   @Override
   public boolean visitTableElement(TableElement element) {
      return false;
   }

   @Override
   public void setCurrentElementVisited(AbstractElement ae) {
      this._currentElementVisited = ae;
   }

   @Override
   public AbstractElement getCurrentElementVisited() {
      return this._currentElementVisited;
   }
}
