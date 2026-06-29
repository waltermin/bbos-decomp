package net.rim.wica.common.metadata.component;

public interface DataComponentDef extends ComponentDef {
   boolean hasKey();

   int[] getKeyFields();

   boolean isKeyField(int var1);

   boolean isPersistable(int var1);

   boolean isBuiltinComponent();
}
