package client.ui.gui.clickGUI.element;

import client.event.listeners.EventSettingClicked;
import client.features.module.Module;
import client.setting.*;
import client.ui.gui.clickGUI.GuiClickGUI;
import client.ui.theme.Theme;
import client.ui.theme.ThemeManager;
import client.utils.RenderingUtils;
import client.utils.font.Fonts;
import client.utils.render.ColorUtils;
import client.utils.render.RenderUtils;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class Panel {

	public float x, y;
	public Module module;
	public boolean extended;

	public boolean isHover;
	public int currentSetting = -1;
	public int selectedSetting = -1;
	public int hoveredSetting = -1;

	public int lastClick;
	public int lastClickedX;
	public int lastClickedY;

	public Panel(float x, float y, Module module) {
		super();
		this.x = x;
		this.y = y;
		this.module = module;
	}

	public void update(int mouseX, int mouseY) {
		isHover =
				!GuiClickGUI.isCollided&&
						mouseX>x&&
						mouseX<x+100&&
						mouseY>=y&&
						mouseY<y+Fonts.font.getFontHeight()+11;


		if(isHover) {
			GuiClickGUI.isCollided=true;
		}

		if(extended) {
			int i=0;
			int YY=0;

			hoveredSetting = -1;
			currentSetting = -1;

			for(Setting s : module.settings) {
				if(s == null)
					continue;
				if(s.visibility == null || (boolean) s.visibility.get());else continue;

				boolean hover =
						!GuiClickGUI.isCollided&&
								mouseX>x+100&&
								mouseX<x+200&&
								mouseY>=YY+y&&
								mouseY<YY+y+ Fonts.font.getFontHeight()+11;

				if(hover) {
					GuiClickGUI.isCollided=true;
					currentSetting = i;
				}

				i++;
				YY+= (int) (Fonts.font.getFontHeight()+11);
			}
		}
	}

	public void draw(int mouseX, int mouseY, float partialTicks) {
		Theme theme = ThemeManager.getTheme();

		RenderingUtils.drawRect(x-1, y, x+100+(extended?0:1), y+Fonts.font.getFontHeight()+12, theme.dark(3).getRGB());
		RenderingUtils.drawRect(x, y, x+100, y+Fonts.font.getFontHeight()+11, module.isEnable()?theme.dark(3).getRGB():theme.dark(0).getRGB());
		RenderingUtils.drawRect(x, y, x+100, y+Fonts.font.getFontHeight()+11, isHover? ColorUtils.alpha(theme.dark(1), 0xff).getRGB():0);

		Fonts.font.drawString(module.getName(), (int)x+7, (int)y+7, -1);

		if(extended) {
			int i=0;
			int YY=0;

			hoveredSetting=-1;

			for(Setting s : module.settings) {
				if(s == null)
					continue;
				if(s.visibility == null || (boolean) s.visibility.get());else continue;

				boolean hover = currentSetting == i;

				if(selectedSetting == -1 && hover && (glfwGetMouseButton(0, GLFW.GLFW_MOUSE_BUTTON_1) == GLFW_PRESS))
					selectedSetting = i;

				if(i==0) {
					RenderingUtils.drawRect(x+100, YY+y-1, x+201, YY+y+Fonts.font.getFontHeight()+11, theme.dark(3).getRGB());
				}
				RenderingUtils.drawRect(x+100, YY+y, x+201, YY+y+Fonts.font.getFontHeight()+11+(module.settings.size()==module.settings.indexOf(s)?0:1), theme.dark(3).getRGB());
				RenderingUtils.drawRect(x+101, YY+y, x+200, YY+y+Fonts.font.getFontHeight()+11, hover?theme.dark(1).getRGB():theme.dark(0).getRGB());

				if(s instanceof KeyBindSetting) {
					if(hover) hoveredSetting = i;

					KeyBindSetting setting = (KeyBindSetting)s;
				//Fonts.font.drawString(setting.name+": "+(selectedSetting==i?"inputwaiting...": (GLFW.glfwGetKey(0,setting.getKeyCode()))), (int)(100+x+7), (int)(YY+y+7), -1);
				}
				else if(s instanceof ModeSetting) {
					if(hover) hoveredSetting = i;

					ModeSetting setting = (ModeSetting)s;
					Fonts.font.drawString(setting.name+": "+setting.getMode(), (int)(100+x+7), (int)(YY+y+7), -1);
				}
				else if(s instanceof BooleanSetting) {
					if(hover) hoveredSetting = i;

					BooleanSetting setting = (BooleanSetting)s;
					int stwidth = (int) Fonts.font.getStringWidth(setting.name);
					RenderingUtils.drawRect(x+100+stwidth+9.5F, YY+y+3, x+101+stwidth+22, YY+y+Fonts.font.getFontHeight()+11-3, theme.light(0).getRGB());
					RenderingUtils.drawRect(x+100+stwidth+10.5F, YY+y+4, x+100+stwidth+22, YY+y+Fonts.font.getFontHeight()+11-4, setting.isEnable()?theme.light(1).getRGB():0xff000f36);
					Fonts.font.drawString(setting.name, (int)(100+x+7), (int)(YY+y+7), -1);
				}
				else if(s instanceof NumberSetting) {
					if(hover) hoveredSetting = i;

					NumberSetting setting = (NumberSetting)s;
					int stwidth = (int) Fonts.font.getStringWidth(setting.name);
					double inc = setting.value/setting.maximum * 92 + 8;
					if ((glfwGetMouseButton(0, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS) && (hover || hoveredSetting == i)) {
						double mouX = (mouseX-x-100-4)*1.1;
						double d = setting.maximum - setting.minimum;
						setting.setValue(mouX/100*d);
					}
					inc = setting.value/setting.maximum * 92 + 8;
					RenderingUtils.drawRect(x+105, YY+y+Fonts.font.getFontHeight()+5, x+200-4, YY+y+Fonts.font.getFontHeight()+7, theme.light(2).getRGB());
					RenderingUtils.drawRect(x+105, YY+y+Fonts.font.getFontHeight()+5, x+100+inc-4, YY+y+Fonts.font.getFontHeight()+7, theme.light(0).getRGB());
					Fonts.font.drawString(setting.name+" : "+String.valueOf(setting.getValue()), (int)(100+x+7), (int)(YY+y+3), -1);
				}
				else  {
					Fonts.font.drawString(s.name, (int)(100+x+7), (int)(YY+y+7),-1);
				}
				i++;
				YY+= (int) (Fonts.font.getFontHeight()+11);
			}
		}
	}

	public void onKeyDown(int keyCode) {
		if(selectedSetting != -1) {
			Setting s = module.settings.get(selectedSetting);
			if(s instanceof KeyBindSetting) {
				((KeyBindSetting)s).setKeyCode(keyCode);
				selectedSetting=-1;
			}
		}
	}

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if(selectedSetting != -1 && hoveredSetting != -1) {
			Setting s = module.settings.get(selectedSetting);
			if(s instanceof BooleanSetting) {
				if(mouseButton == 1) {
					module.onEvent(new EventSettingClicked(s));
					((BooleanSetting)s).toggle();
					selectedSetting=-1;
				}
			}
			if(s instanceof KeyBindSetting) {
				if(mouseButton == 1) {
					module.onEvent(new EventSettingClicked(s));
					((KeyBindSetting)s).setKeyCode(0);
					selectedSetting=-1;
				}
			}
		}

		lastClick=mouseButton;
	}

	public void mouseReleased(int mouseX, int mouseY, int state) {
		if(hoveredSetting != -1) {
			List<Setting> visibleSettings = new ArrayList<>();
			module.settings.forEach(s -> {
				if (s.visibility == null || (boolean)s.visibility.get()) visibleSettings.add(s);
			});
			Setting cur = visibleSettings.get(currentSetting);
			if (cur == null) {
				System.out.println("wat except in cgui");
				return;
			}
			if(cur instanceof KeyBindSetting) {
				if(state == 0) selectedSetting = hoveredSetting;
				module.onEvent(new EventSettingClicked(module.settings.get(hoveredSetting)));
			}

			if(cur instanceof ModeSetting) {
				((ModeSetting)cur).cycle();
				module.onEvent(new EventSettingClicked(module.settings.get(hoveredSetting)));
			}

			if(cur instanceof BooleanSetting) {
				((BooleanSetting)cur).toggle();
				module.onEvent(new EventSettingClicked(module.settings.get(hoveredSetting)));
			}
		}

		if(!isHover)
			return;
		if(state==0) module.toggle();
		if(state==1) extended=!extended;

		lastClick=-1;
	}
}
