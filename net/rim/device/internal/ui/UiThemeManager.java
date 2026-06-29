package net.rim.device.internal.ui;

import net.rim.device.api.ui.theme.Theme$Factory;
import net.rim.device.api.ui.theme.ThemeManager;

public class UiThemeManager extends ThemeManager {
   public static void add(Theme$Factory factory) {
      ThemeManager.add(factory);
   }
}
