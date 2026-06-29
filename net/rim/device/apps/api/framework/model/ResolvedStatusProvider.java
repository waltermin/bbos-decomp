package net.rim.device.apps.api.framework.model;

public interface ResolvedStatusProvider extends RIMModel {
   boolean isResolved();

   Object getResolvedItem();

   Object getResolvedSubItem();
}
