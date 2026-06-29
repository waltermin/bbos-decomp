package net.rim.device.api.ui.accessibility;

public interface AccessibleContext {
   int ACCESSIBLE_STATE_CHANGED;
   int ACCESSIBLE_TEXT_CHANGED;
   int ACCESSIBLE_CARET_CHANGED;
   int ACCESSIBLE_CHILD_CHANGED;
   int ACCESSIBLE_SELECTION_CHANGED;
   int ACCESSIBLE_VALUE_CHANGED;
   int ACCESSIBLE_NAME_CHANGED;
   int ACCESSIBLE_DESCRIPTION_CHANGED;

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
