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

public interface DefinitionVisitor {
   void setCurrentElementVisited(AbstractElement var1);

   AbstractElement getCurrentElementVisited();

   boolean visitAlertElement(AlertElement var1);

   boolean visitDataElement(DataElement var1);

   boolean visitDependencyElement(DependencyElement var1);

   boolean visitFieldElement(FieldElement var1);

   boolean visitGlobalElement(GlobalElement var1);

   boolean visitMessageElement(MessageElement var1);

   boolean visitMappedFieldElement(MappedFieldElement var1);

   boolean visitParamElement(ParamElement var1);

   boolean visitResourceElement(ResourceElement var1);

   boolean visitScreenElement(ScreenElement var1);

   boolean visitScriptElement(ScriptElement var1);

   boolean visitStyleElement(StyleElement var1);

   boolean visitWicletElement(WicletElement var1);

   boolean visitEnumerationElement(EnumerationElement var1);

   boolean visitVarElement(VarElement var1);

   boolean visitRegionElement(RegionElement var1);

   boolean visitLabelElement(LabelElement var1);

   boolean visitSeparatorElement(SeparatorElement var1);

   boolean visitEditElement(EditElement var1);

   boolean visitTextareaElement(TextAreaElement var1);

   boolean visitImageElement(ImageElement var1);

   boolean visitSingleChoiceElement(SingleChoiceElement var1);

   boolean visitMultiChoiceElement(MultiChoiceElement var1);

   boolean visitCheckboxElement(CheckboxElement var1);

   boolean visitButtonElement(ButtonElement var1);

   boolean visitRepetitionElement(RepetitionElement var1);

   boolean visitTableElement(TableElement var1);

   boolean visitColumnElement(ColumnElement var1);
}
