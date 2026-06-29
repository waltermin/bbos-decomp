package net.rim.device.api.ui.accessibility;

public interface AccessibleContext {
   int ACCESSIBLE_STATE_CHANGED = 1;
   int ACCESSIBLE_TEXT_CHANGED = 2;
   int ACCESSIBLE_CARET_CHANGED = 3;
   int ACCESSIBLE_CHILD_CHANGED = 4;
   int ACCESSIBLE_SELECTION_CHANGED = 6;
   int ACCESSIBLE_VALUE_CHANGED = 7;
   int ACCESSIBLE_NAME_CHANGED = 8;
   int ACCESSIBLE_DESCRIPTION_CHANGED = 9;

   String getAccessibleName();

   String getAccessibleDescription();

   AccessibleText getAccessibleText();

   AccessibleValue getAccessibleValue();

   int getAccessibleStateSet();

   boolean isAccessibleStateSet(int var1);

   int getAccessibleRole();

   AccessibleContext getAccessibleParent();

   int getAccessibleChildCount();

   AccessibleContext getAccessibleChildAt(int var1);

   String getAccessibleIconDescription();

   int getAccessibleSelectionCount();

   AccessibleContext getAccessibleSelectionAt(int var1);

   boolean isAccessibleChildSelected(int var1);
}
