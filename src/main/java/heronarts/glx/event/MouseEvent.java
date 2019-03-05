/**
 * Copyright 2019- Mark C. Slee, Heron Arts LLC
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

package heronarts.glx.event;

import static org.lwjgl.glfw.GLFW.*;

public class MouseEvent extends Event {

  public static final int BUTTON_NONE = -1;
  public static final int BUTTON_1 = GLFW_MOUSE_BUTTON_1;
  public static final int BUTTON_2 = GLFW_MOUSE_BUTTON_2;
  public static final int BUTTON_3 = GLFW_MOUSE_BUTTON_3;
  public static final int BUTTON_4 = GLFW_MOUSE_BUTTON_4;
  public static final int BUTTON_5 = GLFW_MOUSE_BUTTON_5;
  public static final int BUTTON_6 = GLFW_MOUSE_BUTTON_6;
  public static final int BUTTON_7 = GLFW_MOUSE_BUTTON_7;
  public static final int BUTTON_8 = GLFW_MOUSE_BUTTON_8;
  public static final int BUTTON_LEFT = GLFW_MOUSE_BUTTON_LEFT;
  public static final int BUTTON_RIGHT = GLFW_MOUSE_BUTTON_RIGHT;
  public static final int BUTTON_MIDDLE = GLFW_MOUSE_BUTTON_MIDDLE;

  public static enum Action {
    MOVE,
    DRAG,
    PRESS,
    RELEASE,
    SCROLL
  };

  private static Action glfwAction(int action) {
    if (action == GLFW_PRESS) {
      return MouseEvent.Action.PRESS;
    } else if (action == GLFW_RELEASE) {
      return MouseEvent.Action.RELEASE;
    } else {
      throw new IllegalArgumentException("Unknown GLFW mouse action: " + action);
    }
  }

  public final Action action;
  public final int button;
  private int count = 0;
  public final float x;
  public final float y;
  public final float dx;
  public final float dy;

  public MouseEvent(int glfwAction, int button, float x, float y, int modifiers) {
    super(modifiers);
    this.action = glfwAction(glfwAction);
    this.button = button;
    this.x = x;
    this.y = y;
    this.dx = 0;
    this.dy = 0;
  }

  public MouseEvent(Action action, float x, float y, float dx, float dy, int modifiers) {
    super(modifiers);
    this.action = action;
    this.button = -1;
    this.x = x;
    this.y = y;
    this.dx = dx;
    this.dy = dy;
  }

  public int getButton() {
    return this.button;
  }

  public Action getAction() {
    return this.action;
  }

  public double getX() {
    return this.x;
  }

  public double getY() {
    return this.y;
  }

  public MouseEvent setCount(int count) {
    this.count = count;
    return this;
  }

  public int getCount() {
    return this.count;
  }

  @Override
  public String toString() {
    return "MouseEvent action=" + this.action + " button=" + this.button + " x=" + this.x + " y=" + this.y + " dx=" + this.dx + " dy=" + this.dy + " modifiers=" + this.modifiers;
  }
}
