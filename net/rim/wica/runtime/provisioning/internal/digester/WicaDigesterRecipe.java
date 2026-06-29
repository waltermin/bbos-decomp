package net.rim.wica.runtime.provisioning.internal.digester;

public final class WicaDigesterRecipe implements DigesterRecipe {
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$WicletElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$ResourceElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$DependencyElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$EnumerationElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$GlobalElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$DataElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$FieldElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$MessageElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$MappedFieldElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$AlertElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$NotificationElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$StyleElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$ScreenElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$ParamElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$MenuElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$VarElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$MenuItemElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$OnShowElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$ScriptElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$RegionElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$RepetitionElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$TableElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$ColumnElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$SeparatorElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$LabelElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$EditElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$TextAreaElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$ImageElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$SingleChoiceElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$MultiChoiceElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$CheckboxElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$ButtonElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$OnInitElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$OnChangeElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$OnFocusOutElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$OnClickElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$OnMoreDataElement;

   @Override
   public final Digester prepare(Digester d) {
      d.addObjectCreate(
         "wiclet",
         class$net$rim$wica$runtime$provisioning$internal$elements$WicletElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$WicletElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.WicletElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$WicletElement
      );
      d.addSetAttributesRule("wiclet");
      d.addInvokeElementBodyOnParentRule("wiclet/desc");
      d.addObjectCreate(
         "wiclet/resource",
         class$net$rim$wica$runtime$provisioning$internal$elements$ResourceElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$ResourceElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.ResourceElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$ResourceElement
      );
      d.addSetAttributesRule("wiclet/resource");
      d.addInvokeParentChildRule("wiclet/resource");
      d.addObjectCreate(
         "wiclet/dependency",
         class$net$rim$wica$runtime$provisioning$internal$elements$DependencyElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$DependencyElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.DependencyElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$DependencyElement
      );
      d.addSetAttributesRule("wiclet/dependency");
      d.addInvokeParentChildRule("wiclet/dependency");
      d.addObjectCreate(
         "wiclet/enumeration",
         class$net$rim$wica$runtime$provisioning$internal$elements$EnumerationElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$EnumerationElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.EnumerationElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$EnumerationElement
      );
      d.addSetAttributesRule("wiclet/enumeration");
      d.addInvokeElementBodyOnParentRule("wiclet/enumeration/value");
      d.addInvokeParentChildRule("wiclet/enumeration");
      d.addObjectCreate(
         "wiclet/global",
         class$net$rim$wica$runtime$provisioning$internal$elements$GlobalElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$GlobalElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.GlobalElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$GlobalElement
      );
      d.addSetAttributesRule("wiclet/global");
      d.addInvokeElementBodyOnParentRule("wiclet/global/value");
      d.addInvokeParentChildRule("wiclet/global");
      d.addObjectCreate(
         "wiclet/data",
         class$net$rim$wica$runtime$provisioning$internal$elements$DataElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$DataElement = class$("net.rim.wica.runtime.provisioning.internal.elements.DataElement")
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$DataElement
      );
      d.addSetAttributesRule("wiclet/data");
      d.addInvokeParentChildRule("wiclet/data");
      d.addObjectCreate(
         "field",
         class$net$rim$wica$runtime$provisioning$internal$elements$FieldElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$FieldElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.FieldElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$FieldElement
      );
      d.addSetAttributesRule("field");
      d.addInvokeParentChildRule("field");
      d.addObjectCreate(
         "wiclet/message",
         class$net$rim$wica$runtime$provisioning$internal$elements$MessageElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$MessageElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.MessageElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$MessageElement
      );
      d.addSetAttributesRule("wiclet/message");
      d.addInvokeParentChildRule("wiclet/message");
      d.addObjectCreate(
         "wiclet/message/mappedField",
         class$net$rim$wica$runtime$provisioning$internal$elements$MappedFieldElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$MappedFieldElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.MappedFieldElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$MappedFieldElement
      );
      d.addSetAttributesRule("wiclet/message/mappedField");
      d.addInvokeParentChildRule("wiclet/message/mappedField");
      d.addObjectCreate(
         "wiclet/message/alert",
         class$net$rim$wica$runtime$provisioning$internal$elements$AlertElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$AlertElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.AlertElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$AlertElement
      );
      d.addSetAttributesRule("wiclet/message/alert");
      d.addInvokeParentChildRule("wiclet/message/alert");
      d.addObjectCreate(
         "wiclet/message/notification",
         class$net$rim$wica$runtime$provisioning$internal$elements$NotificationElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$NotificationElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.NotificationElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$NotificationElement
      );
      d.addSetAttributesRule("wiclet/message/notification");
      d.addInvokeParentChildRule("wiclet/message/notification");
      d.addObjectCreate(
         "wiclet/style",
         class$net$rim$wica$runtime$provisioning$internal$elements$StyleElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$StyleElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.StyleElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$StyleElement
      );
      d.addSetAttributesRule("wiclet/style");
      d.addInvokeParentChildRule("wiclet/style");
      d.addObjectCreate(
         "wiclet/screen",
         class$net$rim$wica$runtime$provisioning$internal$elements$ScreenElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$ScreenElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.ScreenElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$ScreenElement
      );
      d.addSetAttributesRule("wiclet/screen");
      d.addInvokeParentChildRule("wiclet/screen");
      d.addObjectCreate(
         "param",
         class$net$rim$wica$runtime$provisioning$internal$elements$ParamElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$ParamElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.ParamElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$ParamElement
      );
      d.addSetAttributesRule("param");
      d.addInvokeParentChildRule("param");
      d.addObjectCreate(
         "wiclet/screen/menu",
         class$net$rim$wica$runtime$provisioning$internal$elements$MenuElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$MenuElement = class$("net.rim.wica.runtime.provisioning.internal.elements.MenuElement")
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$MenuElement
      );
      d.addInvokeParentChildRule("wiclet/screen/menu");
      d.addObjectCreate(
         "wiclet/screen/var",
         class$net$rim$wica$runtime$provisioning$internal$elements$VarElement == null
            ? (class$net$rim$wica$runtime$provisioning$internal$elements$VarElement = class$("net.rim.wica.runtime.provisioning.internal.elements.VarElement"))
            : class$net$rim$wica$runtime$provisioning$internal$elements$VarElement
      );
      d.addInvokeParentChildRule("wiclet/screen/var");
      d.addSetAttributesRule("wiclet/screen/var");
      d.addObjectCreate(
         "wiclet/screen/menu/menuItem",
         class$net$rim$wica$runtime$provisioning$internal$elements$MenuItemElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$MenuItemElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.MenuItemElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$MenuItemElement
      );
      d.addSetAttributesRule("wiclet/screen/menu/menuItem");
      d.addInvokeParentChildRule("wiclet/screen/menu/menuItem");
      d.addObjectCreate(
         "wiclet/screen/menu/item",
         class$net$rim$wica$runtime$provisioning$internal$elements$MenuItemElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$MenuItemElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.MenuItemElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$MenuItemElement
      );
      d.addSetAttributesRule("wiclet/screen/menu/item");
      d.addInvokeParentChildRule("wiclet/screen/menu/item");
      d.addObjectCreate(
         "onShow",
         class$net$rim$wica$runtime$provisioning$internal$elements$OnShowElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$OnShowElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.OnShowElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$OnShowElement
      );
      d.addSetAttributesRule("onShow");
      d.addInvokeParentChildRule("onShow");
      d.addObjectCreate(
         "wiclet/script",
         class$net$rim$wica$runtime$provisioning$internal$elements$ScriptElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$ScriptElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.ScriptElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$ScriptElement
      );
      d.addSetAttributesRule("wiclet/script");
      d.addInvokeParentChildRule("wiclet/script");
      d.addObjectCreate(
         "region",
         class$net$rim$wica$runtime$provisioning$internal$elements$RegionElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$RegionElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.RegionElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$RegionElement
      );
      d.addSetAttributesRule("region");
      d.addInvokeParentChildRule("region");
      d.addObjectCreate(
         "repetition",
         class$net$rim$wica$runtime$provisioning$internal$elements$RepetitionElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$RepetitionElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.RepetitionElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$RepetitionElement
      );
      d.addSetAttributesRule("repetition");
      d.addInvokeParentChildRule("repetition");
      d.addObjectCreate(
         "table",
         class$net$rim$wica$runtime$provisioning$internal$elements$TableElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$TableElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.TableElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$TableElement
      );
      d.addSetAttributesRule("table");
      d.addInvokeParentChildRule("table");
      d.addObjectCreate(
         "column",
         class$net$rim$wica$runtime$provisioning$internal$elements$ColumnElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$ColumnElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.ColumnElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$ColumnElement
      );
      d.addSetAttributesRule("column");
      d.addInvokeParentChildRule("column");
      d.addObjectCreate(
         "separator",
         class$net$rim$wica$runtime$provisioning$internal$elements$SeparatorElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$SeparatorElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.SeparatorElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$SeparatorElement
      );
      d.addSetAttributesRule("separator");
      d.addInvokeParentChildRule("separator");
      d.addObjectCreate(
         "label",
         class$net$rim$wica$runtime$provisioning$internal$elements$LabelElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$LabelElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.LabelElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$LabelElement
      );
      d.addSetAttributesRule("label");
      d.addInvokeParentChildRule("label");
      d.addObjectCreate(
         "edit",
         class$net$rim$wica$runtime$provisioning$internal$elements$EditElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$EditElement = class$("net.rim.wica.runtime.provisioning.internal.elements.EditElement")
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$EditElement
      );
      d.addSetAttributesRule("edit");
      d.addInvokeParentChildRule("edit");
      d.addObjectCreate(
         "textarea",
         class$net$rim$wica$runtime$provisioning$internal$elements$TextAreaElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$TextAreaElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.TextAreaElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$TextAreaElement
      );
      d.addSetAttributesRule("textarea");
      d.addInvokeParentChildRule("textarea");
      d.addObjectCreate(
         "image",
         class$net$rim$wica$runtime$provisioning$internal$elements$ImageElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$ImageElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.ImageElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$ImageElement
      );
      d.addSetAttributesRule("image");
      d.addInvokeParentChildRule("image");
      d.addObjectCreate(
         "singleChoice",
         class$net$rim$wica$runtime$provisioning$internal$elements$SingleChoiceElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$SingleChoiceElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.SingleChoiceElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$SingleChoiceElement
      );
      d.addSetAttributesRule("singleChoice");
      d.addInvokeParentChildRule("singleChoice");
      d.addObjectCreate(
         "multiChoice",
         class$net$rim$wica$runtime$provisioning$internal$elements$MultiChoiceElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$MultiChoiceElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.MultiChoiceElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$MultiChoiceElement
      );
      d.addSetAttributesRule("multiChoice");
      d.addInvokeParentChildRule("multiChoice");
      d.addObjectCreate(
         "checkbox",
         class$net$rim$wica$runtime$provisioning$internal$elements$CheckboxElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$CheckboxElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.CheckboxElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$CheckboxElement
      );
      d.addSetAttributesRule("checkbox");
      d.addInvokeParentChildRule("checkbox");
      d.addObjectCreate(
         "button",
         class$net$rim$wica$runtime$provisioning$internal$elements$ButtonElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$ButtonElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.ButtonElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$ButtonElement
      );
      d.addSetAttributesRule("button");
      d.addInvokeParentChildRule("button");
      d.addObjectCreate(
         "onInit",
         class$net$rim$wica$runtime$provisioning$internal$elements$OnInitElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$OnInitElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.OnInitElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$OnInitElement
      );
      d.addSetAttributesRule("onInit");
      d.addInvokeParentChildRule("onInit");
      d.addObjectCreate(
         "onChange",
         class$net$rim$wica$runtime$provisioning$internal$elements$OnChangeElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$OnChangeElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.OnChangeElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$OnChangeElement
      );
      d.addSetAttributesRule("onChange");
      d.addInvokeParentChildRule("onChange");
      d.addObjectCreate(
         "onFocusOut",
         class$net$rim$wica$runtime$provisioning$internal$elements$OnFocusOutElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$OnFocusOutElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.OnFocusOutElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$OnFocusOutElement
      );
      d.addSetAttributesRule("onFocusOut");
      d.addInvokeParentChildRule("onFocusOut");
      d.addObjectCreate(
         "onClick",
         class$net$rim$wica$runtime$provisioning$internal$elements$OnClickElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$OnClickElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.OnClickElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$OnClickElement
      );
      d.addSetAttributesRule("onClick");
      d.addInvokeParentChildRule("onClick");
      d.addObjectCreate(
         "onMoreData",
         class$net$rim$wica$runtime$provisioning$internal$elements$OnMoreDataElement == null
            ? (
               class$net$rim$wica$runtime$provisioning$internal$elements$OnMoreDataElement = class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.OnMoreDataElement"
               )
            )
            : class$net$rim$wica$runtime$provisioning$internal$elements$OnMoreDataElement
      );
      d.addSetAttributesRule("onMoreData");
      d.addInvokeParentChildRule("onMoreData");
      return d;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
