package net.rim.wica.runtime.provisioning.internal;

import net.rim.wica.runtime.provisioning.internal.digester.Digester;
import net.rim.wica.runtime.provisioning.internal.digester.DigesterRecipe;

class StandardComponentResolver$StandardComponentsRecipe implements DigesterRecipe {
   private StandardComponentResolver$StandardComponentsRecipe() {
   }

   @Override
   public Digester prepare(Digester d) {
      d.addObjectCreate(
         "standardcomponents/data",
         StandardComponentResolver.class$net$rim$wica$runtime$provisioning$internal$elements$DataElement == null
            ? (
               StandardComponentResolver.class$net$rim$wica$runtime$provisioning$internal$elements$DataElement = StandardComponentResolver.class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.DataElement"
               )
            )
            : StandardComponentResolver.class$net$rim$wica$runtime$provisioning$internal$elements$DataElement
      );
      d.addSetAttributesRule("standardcomponents/data");
      d.addInvokeParentChildRule("standardcomponents/data");
      d.addObjectCreate(
         "standardcomponents/data/field",
         StandardComponentResolver.class$net$rim$wica$runtime$provisioning$internal$elements$FieldElement == null
            ? (
               StandardComponentResolver.class$net$rim$wica$runtime$provisioning$internal$elements$FieldElement = StandardComponentResolver.class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.FieldElement"
               )
            )
            : StandardComponentResolver.class$net$rim$wica$runtime$provisioning$internal$elements$FieldElement
      );
      d.addSetAttributesRule("standardcomponents/data/field");
      d.addInvokeParentChildRule("standardcomponents/data/field");
      d.addObjectCreate(
         "standardcomponents/enumeration",
         StandardComponentResolver.class$net$rim$wica$runtime$provisioning$internal$elements$EnumerationElement == null
            ? (
               StandardComponentResolver.class$net$rim$wica$runtime$provisioning$internal$elements$EnumerationElement = StandardComponentResolver.class$(
                  "net.rim.wica.runtime.provisioning.internal.elements.EnumerationElement"
               )
            )
            : StandardComponentResolver.class$net$rim$wica$runtime$provisioning$internal$elements$EnumerationElement
      );
      d.addSetAttributesRule("standardcomponents/enumeration");
      d.addInvokeElementBodyOnParentRule("standardcomponents/enumeration/value");
      d.addInvokeParentChildRule("standardcomponents/enumeration");
      return d;
   }

   StandardComponentResolver$StandardComponentsRecipe(StandardComponentResolver$1 x0) {
      this();
   }
}
