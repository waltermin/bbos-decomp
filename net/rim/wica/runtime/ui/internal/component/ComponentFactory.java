package net.rim.wica.runtime.ui.internal.component;

import java.util.Vector;
import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.metadata.component.ui.UIContainer;
import net.rim.wica.runtime.metadata.component.ui.control.ButtonControl;
import net.rim.wica.runtime.metadata.component.ui.control.CheckboxControl;
import net.rim.wica.runtime.metadata.component.ui.control.ChoiceControl;
import net.rim.wica.runtime.metadata.component.ui.control.EditControl;
import net.rim.wica.runtime.metadata.component.ui.control.ImageControl;
import net.rim.wica.runtime.metadata.component.ui.control.LabelControl;
import net.rim.wica.runtime.metadata.component.ui.control.RepetitionControl;
import net.rim.wica.runtime.metadata.component.ui.control.SeparatorControl;
import net.rim.wica.runtime.metadata.component.ui.control.TableModel;
import net.rim.wica.runtime.ui.View;
import net.rim.wica.runtime.ui.internal.ResourceProvider;
import net.rim.wica.transport.handshake.HandshakeMessageHandlerException;

public final class ComponentFactory {
   public static final View getView(ScreenContext context, UIComponent model, int row, long style) {
      int type = model.getType();
      switch (type) {
         case 10:
         case 128:
            return createRegionView(context, (UIContainer)model, row, style);
         case 129:
            return createEditView(context, (EditControl)model, row, style);
         case 130:
            return createChoiceView(context, (ChoiceControl)model, row, style);
         case 131:
            return new WicletButtonField(context, (ButtonControl)model, row, style);
         case 132:
            return new WicletSeparatorField(context, (SeparatorControl)model, row);
         case 133:
            return createMediaView(context, (ImageControl)model, row, style);
         case 134:
            return new WicletLabelField(context, (LabelControl)model, row);
         case 135:
            return new WicletTextEditField(context, (EditControl)model, row, style);
         case 137:
            return new WicletRepetitionManager(context, (RepetitionControl)model, row, style);
         case 139:
            return new WicletCheckboxField(context, (CheckboxControl)model, row, style);
         case 140:
            return new HandshakeMessageHandlerException(context, (TableModel)model, style);
         default:
            return null;
      }
   }

   private static final View createChoiceView(ScreenContext context, ChoiceControl model, int row, long style) {
      int choiceType = model.getChoiceType();
      switch (choiceType) {
         case -1:
            return null;
         case 0:
            return new WicletSingleSelectListField(context, model, row, style);
         case 1:
         case 4:
         default:
            return new WicletCheckboxListField(context, model, row, style);
         case 2:
            return new WicletDropdownListField(context, model, row, style);
         case 3:
            return new WicletRadioButtonListField(context, model, row, style);
      }
   }

   private static final View createEditView(ScreenContext context, EditControl model, int row, long style) {
      int editType = model.getEditType();
      switch (editType) {
         case 3:
         case 4:
         case 11:
            return new WicletDateEditField(context, model, row, style);
         case 10:
            return new WicletDurationEditField(context, model, row, style);
         default:
            return new WicletTextEditField(context, model, row, style);
      }
   }

   private static final View createMediaView(ScreenContext context, ImageControl model, int row, long style) {
      if (model.getResourceId() != -1) {
         String contentType = context.getResourceProvider().getResourceContentType(model.getResourceId());
         if (contentType != null && ResourceProvider.isMediaEngineContent(contentType)) {
            return new WicletMediaField(context, model, row, style);
         }
      } else {
         Object value = model.getValue();
         String uri = null;
         if (!(value instanceof Vector)) {
            uri = (String)value;
         } else {
            uri = (String)((Vector)value).elementAt(row);
         }

         if (uri != null && (uri.endsWith(".pme") || uri.endsWith(".pmb") || uri.endsWith(".PME") || uri.endsWith(".PMB"))) {
            return new WicletMediaField(context, model, row, style);
         }
      }

      return new WicletImageField(context, model, row);
   }

   private static final View createRegionView(ScreenContext context, UIContainer model, int row, long style) {
      int layoutType = model.getLayout();
      switch (layoutType) {
         case 0:
         case 2:
            return new WicletVerticalFieldManager(context, model, row, style);
         case 1:
         default:
            return new WicletFlowFieldManager(context, model, row, style);
         case 3:
            return new WicletGridFieldManager(context, model, row, style);
         case 4:
            return new WicletEdgeFieldManager(context, model, row, style);
      }
   }
}
