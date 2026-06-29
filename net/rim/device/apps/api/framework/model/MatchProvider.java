package net.rim.device.apps.api.framework.model;

public interface MatchProvider extends RIMModel {
   int NOTAPPLICABLE = -1;
   int NOTMATCHED = 0;
   int MATCHED = 1;

   int match(Object var1);
}
