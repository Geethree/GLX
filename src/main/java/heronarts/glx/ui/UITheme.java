/**
 * Copyright 2013- Mark C. Slee, Heron Arts LLC
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

package heronarts.glx.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import heronarts.glx.ui.vg.VGraphics;
import heronarts.lx.LX;

public class UITheme {

  private final List<Color> colors = new ArrayList<Color>();

  public class Color extends UIColor {

    private final String label;

    public Color(String label) {
      super(Theme.DEFAULT.colors.get(label));
      this.label = label;
      colors.add(this);
    }

    private Color set(int argb) {
      this.argb = argb;
      return this;
    }
  }

  public void setTheme(Theme theme) {
    for (Color color : this.colors) {
      color.set(theme.colors.get(color.label));
    }
  }

  public enum Theme {
    DEFAULT("Default",
      "label", "cccccc",

      "deviceBackground", "404040",
      "deviceFocusedBackground", "4c4c4c",
      "deviceSelection", "586658",
      "deviceSelectionText", "e0e0e0",
      "deviceBorder", "292929",

      "paneBackground", "040404",
      "paneInset", "242424",

      "scrollBar", "333333",

      "controlBackground", "222222",
      "controlBorder", "292929",
      "controlDetent", "333333",
      "controlText", "cccccc",
      "controlDisabledText", "707070",
      "controlDisabled", "303030",

      "primary", "669966",
      "secondary", "666699",
      "focus", "669966",
      "attention", "ff3333",
      "cue", "666699",
      "aux", "996666",
      "surface", "666699",
      "recording", "a00044",
      "cursor", "555555",
      "selection", "333333",
      "focusSelection", "393939",
      "error", "ff0000",
      "secondaryListItem", "666666",

      "darkBackground", "191919",
      "darkFocusBackground", "292929",

      "iconDisabled", "505050",
      "iconInactive", "999999",

      "contextBackground", "222222",
      "contextBorder", "000000",
      "contextHighlight", "333333",

      "midiMapping", "33ff0000",
      "modulationSourceMapping", "3300ff00",
      "modulationTargetMapping", "3300cccc"
    ),

    LIGHT("Light");

    public final String name;
    public final Map<String, Integer> colors = new HashMap<String, Integer>();

    private Theme(String name, String ... colors) {
      this.name = name;
      for (int i = 0; i < colors.length; i +=2) {
        String field = colors[i];
        String hex = colors[i+1];
        if (hex.length() == 8) {
          this.colors.put(field, Integer.parseUnsignedInt(hex, 16));
        } else if (hex.length() == 6) {
          this.colors.put(field, Integer.parseUnsignedInt("ff" + hex, 16));
        } else {
          throw new IllegalArgumentException("UITheme color must be 6 or 8 hex digits - " + field);
        }
      }
    }

    @Override
    public String toString() {
      return this.name;
    }
  }

  private final VGraphics.Font deviceFont;
  private final VGraphics.Font labelFont;
  private final VGraphics.Font controlFont;

  public final Color labelColor = new Color("label");

  public final Color deviceBackgroundColor = new Color("deviceBackground");
  public final Color deviceFocusedBackgroundColor = new Color("deviceFocusedBackground");
  public final Color deviceSelectionColor = new Color("deviceSelection");
  public final Color deviceSelectionTextColor = new Color("deviceSelectionText");
  public final Color deviceBorderColor = new Color("deviceBorder");

  public final Color paneBackgroundColor = new Color("paneBackground");
  public final Color paneInsetColor = new Color("paneInset");

  public final Color scrollBarColor = new Color("scrollBar");

  public final Color controlBackgroundColor = new Color("controlBackground");
  public final Color controlBorderColor = new Color("controlBorder");
  public final Color controlDetentColor = new Color("controlDetent");
  public final Color controlTextColor = new Color("controlText");
  public final Color controlDisabledTextColor = new Color("controlDisabledText");
  public final Color controlDisabledColor = new Color("controlDisabled");

  public final Color primaryColor = new Color("primary");
  public final Color secondaryColor = new Color("secondary");
  public final Color focusColor = new Color("focus");
  public final Color attentionColor = new Color("attention");
  public final Color cueColor = new Color("cue");
  public final Color auxColor = new Color("aux");
  public final Color surfaceColor = new Color("surface");
  public final Color recordingColor = new Color("recording");
  public final Color cursorColor = new Color("cursor");
  public final Color selectionColor = new Color("selection");
  public final Color focusSelectionColor = new Color("focusSelection");
  public final Color errorColor = new Color("error");
  public final Color secondaryListItemColor = new Color("secondaryListItem");

  public final Color darkBackgroundColor = new Color("darkBackground");
  public final Color darkFocusBackgroundColor = new Color("darkFocusBackground");

  public final Color iconDisabledColor = new Color("iconDisabled");
  public final Color iconInactiveColor = new Color("iconInactive");

  public final Color contextBackgroundColor = new Color("contextBackground");
  public final Color contextBorderColor = new Color("contextBorder");
  public final Color contextHighlightColor = new Color("contextHighlight");

  public final Color midiMappingColor = new Color("midiMapping");
  public final Color modulationSourceMappingColor = new Color("modulationSourceMapping");
  public final Color modulationTargetMappingColor = new Color("modulationTargetMapping");

  public final VGraphics.Image iconNote;
  public final VGraphics.Image iconTempo;
  public final VGraphics.Image iconControl;
  public final VGraphics.Image iconTrigger;
  public final VGraphics.Image iconTriggerSource;
  public final VGraphics.Image iconLoop;
  public final VGraphics.Image iconMap;
  public final VGraphics.Image iconArm;
  public final VGraphics.Image iconLfo;
  public final VGraphics.Image iconLoad;
  public final VGraphics.Image iconSave;
  public final VGraphics.Image iconSaveAs;
  public final VGraphics.Image iconNew;
  public final VGraphics.Image iconOpen;
  public final VGraphics.Image iconKeyboard;
  public final VGraphics.Image iconPreferences;
  public final VGraphics.Image iconUndo;
  public final VGraphics.Image iconRedo;
  public final VGraphics.Image iconTempoDown;
  public final VGraphics.Image iconTempoUp;
  public final VGraphics.Image iconOscInput;
  public final VGraphics.Image iconOscOutput;
  public final VGraphics.Image iconPatternTransition;
  public final VGraphics.Image iconPatternRotate;
  public final VGraphics.Image iconPlay;
  public final VGraphics.Image iconView;
  public final VGraphics.Image iconEdit;

  UITheme(VGraphics vg) throws IOException {
    this.controlFont = loadFont(vg, "Inter-SemiBold", "Inter-SemiBold.otf");
    this.controlFont.fontSize(10);
    LX.initProfiler.log("GLX: UI: Theme: controlFont");

    this.labelFont = this.deviceFont = loadFont(vg, "Inter-Black", "Inter-Black.otf");
    this.labelFont.fontSize(10);
    LX.initProfiler.log("GLX: UI: Theme: labelFont");

    this.iconNote = loadIcon(vg, "icon-note@2x.png");
    this.iconTempo = loadIcon(vg, "icon-tempo@2x.png");
    this.iconControl = loadIcon(vg, "icon-control@2x.png");
    this.iconTrigger = loadIcon(vg, "icon-trigger@2x.png");
    this.iconTriggerSource = loadIcon(vg, "icon-trigger-source@2x.png");
    this.iconLoop = loadIcon(vg, "icon-loop@2x.png");
    this.iconMap = loadIcon(vg, "icon-map@2x.png");
    this.iconArm = loadIcon(vg, "icon-arm@2x.png");
    this.iconLfo = loadIcon(vg, "icon-lfo@2x.png");
    this.iconLoad = loadIcon(vg, "icon-load@2x.png");
    this.iconSave = loadIcon(vg, "icon-save@2x.png");
    this.iconSaveAs = loadIcon(vg, "icon-save-as@2x.png");
    this.iconNew = loadIcon(vg, "icon-new@2x.png");
    this.iconOpen = loadIcon(vg, "icon-open@2x.png");
    this.iconKeyboard = loadIcon(vg, "icon-keyboard@2x.png");
    this.iconPreferences = loadIcon(vg, "icon-preferences@2x.png");
    this.iconUndo = loadIcon(vg, "icon-undo@2x.png");
    this.iconRedo = loadIcon(vg, "icon-redo@2x.png");
    this.iconTempoDown = loadIcon(vg, "icon-tempo-down@2x.png");
    this.iconTempoUp = loadIcon(vg, "icon-tempo-up@2x.png");
    this.iconOscInput = loadIcon(vg, "icon-osc-input@2x.png");
    this.iconOscOutput = loadIcon(vg, "icon-osc-output@2x.png");
    this.iconPatternTransition = loadIcon(vg, "icon-pattern-transition@2x.png");
    this.iconPatternRotate = loadIcon(vg, "icon-pattern-rotate@2x.png");
    this.iconPlay = loadIcon(vg, "icon-play@2x.png");
    this.iconView = loadIcon(vg, "icon-view@2x.png");
    this.iconEdit = loadIcon(vg, "icon-edit@2x.png");
    LX.initProfiler.log("GLX: UI: Theme: Icons");
  }

  private final List<VGraphics.Font> fonts = new ArrayList<VGraphics.Font>();

  private VGraphics.Font loadFont(VGraphics vg, String name, String filename) throws IOException {
    VGraphics.Font font = vg.loadFont(name, filename);
    this.fonts.add(font);
    return font;
  }

  private final List<VGraphics.Image> icons = new ArrayList<VGraphics.Image>();

  private VGraphics.Image loadIcon(VGraphics vg, String filename) throws IOException {
    VGraphics.Image icon = vg.loadIcon(filename);
    this.icons.add(icon);
    return icon;
  }

  /**
   * Gets the default item font
   *
   * @return The default item font
   */
  public VGraphics.Font getControlFont() {
    return this.controlFont;
  }

  /**
   * Label font
   *
   * @return font
   */
  public VGraphics.Font getLabelFont() {
    return this.labelFont;
  }

  public VGraphics.Font getDeviceFont() {
    return this.deviceFont;
  }

  public void dispose() {
    for (VGraphics.Font font: this.fonts) {
      font.dispose();
    }
    this.fonts.clear();

    for (VGraphics.Image icon : this.icons) {
      icon.dispose();
    }
    this.icons.clear();
  }

}

