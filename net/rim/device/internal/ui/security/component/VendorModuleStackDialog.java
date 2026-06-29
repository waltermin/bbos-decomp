package net.rim.device.internal.ui.security.component;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.PopupDialog;
import net.rim.device.internal.ui.container.VerticalIndentFieldManager;
import net.rim.vm.TraceBack;

public class VendorModuleStackDialog extends PopupDialog {
   private static final int INDENT_PIXEL_WIDTH = 12;

   public VendorModuleStackDialog(Manager manager) {
      this(manager, 0);
   }

   public VendorModuleStackDialog(Manager manager, long style) {
      super(manager, style);
      this.setStatusPriority(-2147483643);
   }

   public static void populateVendorApplicationModulesStack(VerticalIndentFieldManager vifm) {
      populateVendorApplicationModulesStack(vifm, null, null);
   }

   protected static void populateVendorApplicationModulesStack(VerticalIndentFieldManager vifm, int[] stackHandles, int[] specialStackHandles) {
      ResourceBundleFamily rb = ResourceBundle.getBundle(3711053710409943671L, "net.rim.device.internal.resource.UI");
      Font boldFont = Font.getDefault();
      boldFont = boldFont.derive(1);
      Font plainFont = Font.getDefault();
      plainFont = plainFont.derive(0);
      vifm.add(new SeparatorField());
      ApplicationDescriptor currentApplicationDescriptor = ApplicationDescriptor.currentApplicationDescriptor();
      int moduleHandle = currentApplicationDescriptor.getModuleHandle();
      String vendorName = CodeModuleManager.getModuleVendor(moduleHandle);
      if (vendorName != null) {
         LabelField vendorNameLabel = new LabelField(rb.getString(28));
         vendorNameLabel.setFont(boldFont);
         vifm.add(vendorNameLabel);
         RichTextField vendorNameField = new RichTextField(vendorName, 9007199254740992L);
         vifm.add(vendorNameField, 12);
         vendorNameField.setFont(plainFont);
      }

      String appName = currentApplicationDescriptor.getName();
      if (appName != null) {
         LabelField appNameLabel = new LabelField(rb.getString(29));
         appNameLabel.setFont(boldFont);
         vifm.add(appNameLabel);
         RichTextField appNameField = new RichTextField(appName, 9007199254740992L);
         vifm.add(appNameField, 12);
         appNameField.setFont(plainFont);
      }

      int[] moduleHandles = stackHandles != null ? stackHandles : TraceBack.getCallingModules();
      if (specialStackHandles == null) {
         specialStackHandles = new int[0];
      }

      String[] callStack;
      if (moduleHandles != null) {
         int length = moduleHandles.length;
         callStack = new String[length];

         for (int i = 0; i < length; i++) {
            callStack[i] = StringUtilities.removeChars(CodeModuleManager.getModuleName(moduleHandles[i]), "̲");
         }
      } else {
         callStack = new String[]{CommonResource.getString(1012)};
         moduleHandles = new int[]{-1, 555417856, 1300392308, 40649801};
      }

      if (callStack != null) {
         LabelField callStackLabel = new LabelField(rb.getString(30));
         callStackLabel.setFont(boldFont);
         vifm.add(callStackLabel);
         int length = callStack.length;
         RichTextField[] callStackFields = new RichTextField[length];

         for (int i = 0; i < length; i++) {
            callStackFields[i] = new RichTextField(callStack[i], 9007199254740992L);
            if (Arrays.getIndex(specialStackHandles, moduleHandles[i]) != -1) {
               callStackFields[i].setFont(boldFont);
            } else {
               callStackFields[i].setFont(plainFont);
            }

            vifm.add(callStackFields[i], 12);
         }
      }
   }
}
