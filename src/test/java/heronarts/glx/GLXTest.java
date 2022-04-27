/**
 * Copyright 2022- Mark C. Slee, Heron Arts LLC
 *
 * This file is part of the LX Studio software library. By using
 * LX, you agree to the terms of the LX Studio Software License
 * and Distribution Agreement, available at: http://lx.studio/license
 *
 * Please note that the LX license is not open-source. The license
 * allows for free, non-commercial use.
 *
 * HERON ARTS MAKES NO WARRANTY, EXPRESS, IMPLIED, STATUTORY, OR
 * OTHERWISE, AND SPECIFICALLY DISCLAIMS ANY WARRANTY OF
 * MERCHANTABILITY, NON-INFRINGEMENT, OR FITNESS FOR A PARTICULAR
 * PURPOSE, WITH RESPECT TO THE SOFTWARE.
 *
 * @author Mark C. Slee <mark@heronarts.com>
 */

package heronarts.glx;

import java.io.IOException;

import heronarts.glx.ui.UI;
import heronarts.glx.ui.UI2dContext;
import heronarts.glx.ui.component.UILabel;

public class GLXTest extends GLX {
  protected GLXTest(Flags flags) throws IOException {
    super(flags);
  }

  @Override
  protected UI buildUI() throws IOException {
    UI ui = super.buildUI();

    System.out.println("buildUI");
    UI2dContext red = (UI2dContext)
      new UI2dContext(ui, 0, 0, 100, 100)
      .setBackgroundColor(0xffff0000);
    new UILabel(0, 0, 100, 14).setLabel("Red").addToContainer(red);
    ui.addLayer(red);

    UI2dContext green = (UI2dContext)
      new UI2dContext(ui, 200, 200, 100, 100)
      .setBackgroundColor(0xff00ff00);
    new UILabel(0, 0, 100, 14).setLabel("Green").addToContainer(green);
    ui.addLayer(green);

    System.out.println("return");
    return ui;
  }

  public static void main(String[] args) {
    try {
      GLX glx = new GLXTest(new Flags());
      glx.run();
    } catch (Exception x) {
      x.printStackTrace();
    }
  }
}
