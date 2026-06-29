package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.internal.lcdui.Lcdui;
import net.rim.device.internal.ui.MIDletApplication;

public class GameCanvas extends Canvas {
   private Image offscreen_buffer;
   public static final int UP_PRESSED;
   public static final int DOWN_PRESSED;
   public static final int LEFT_PRESSED;
   public static final int RIGHT_PRESSED;
   public static final int FIRE_PRESSED;
   public static final int GAME_A_PRESSED;
   public static final int GAME_B_PRESSED;
   public static final int GAME_C_PRESSED;
   public static final int GAME_D_PRESSED;
   private static final int FULLSCREEN_HEIGHT = Display.getHeight();
   private static final int FULLSCREEN_WIDTH = Display.getWidth();

   protected GameCanvas(boolean suppressKeyEvents) {
      this.offscreen_buffer = Image.createImage(FULLSCREEN_WIDTH, FULLSCREEN_HEIGHT);
      synchronized (Application.getEventLock()) {
         Lcdui.setSuppressKeyEvents(suppressKeyEvents);
      }
   }

   protected Graphics getGraphics() {
      return this.offscreen_buffer.getGraphics();
   }

   public int getKeyStates() {
      if (this.isShown()) {
         synchronized (Application.getEventLock()) {
            int result = Lcdui.getCurrentKeyStates() | Lcdui.getKeyDownHistory();
            Lcdui.setKeyDownHistory(0);
            return result;
         }
      } else {
         return 0;
      }
   }

   @Override
   public void paint(Graphics g) {
      g.drawImage(this.offscreen_buffer, 0, 0, 20);
   }

   public void flushGraphics(int x, int y, int width, int height) {
      if (width >= 1 && height >= 1 && this.isShown()) {
         MIDletApplication app = (MIDletApplication)UiApplication.getUiApplication();
         if (app != null) {
            synchronized (Application.getEventLock()) {
               Graphics g = Lcdui.getMIDPGraphics();
               if (g != null) {
                  g.drawImage(this.offscreen_buffer, 0, 0, 20);
                  app.updateDisplay();
               }
            }
         }
      }
   }

   public void flushGraphics() {
      this.flushGraphics(0, 0, this.getWidth(), this.getHeight());
   }
}
