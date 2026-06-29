package net.rim.device.apps.api.framework.model;

public interface EditableProvider extends RIMModel {
   Object makeReadOnly();

   Object makeReadWrite();

   boolean isReadOnly();
}
