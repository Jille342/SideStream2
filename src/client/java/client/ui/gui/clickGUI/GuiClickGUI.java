package client.ui.gui.clickGUI;

import client.features.module.Module;
import client.features.module.ModuleManager;
import client.features.module.render.ClickGUI;
import client.ui.element.ElementManager;
import client.ui.element.Panel;
import client.ui.element.elements.RectPanel;
import client.ui.element.elements.TextPanel;
import client.ui.theme.ThemeManager;
import client.utils.font.Fonts;
import client.utils.render.AnimationUtil;
import client.utils.render.ColorUtils;
import client.utils.render.easing.Color;
import client.utils.render.easing.Value;
import client.Client;
import client.ui.gui.clickGUI.element.Category;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.entity.Entity;

import net.minecraft.text.Text;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static client.Client.mc;
import static org.lwjgl.glfw.GLFW.*;

public class GuiClickGUI extends Screen {

	public static CopyOnWriteArrayList<Category> panels;

	public static boolean isCollided;

	public static ElementManager gui = new ElementManager();
	public static ElementManager MAP_GUI = new ElementManager();
	public static ElementManager SETTING_GUI = new ElementManager();
	public static RectPanel SETTING_PANEL;
	public static RectPanel SETTING_PANEL_RESIZER;

	public static RectPanel currentScreen;

	public static RectPanel menu;

	public static List<Panel> menuElements = new ArrayList<Panel>();

	public static int lastScreen = 0;

	public Value scrolly;

	public GuiClickGUI(int screen) {
		super(Text.of("Screen"));
		//setup map
		MAP_GUI = new ElementManager();
		MAP_GUI.addPanel(new RectPanel(MAP_GUI, 75, 25, 500, 500, ColorUtils.alpha(ThemeManager.getTheme().dark(1), 0xf0), true));

		//Setting GUI
		SETTING_GUI = new ElementManager();
		double heightGround = 300;
		double widthGround = (int) (heightGround * 1.6180339887);
		SETTING_PANEL = new RectPanel(SETTING_GUI, width / 2 - widthGround / 2, height / 2 - heightGround / 2, widthGround, heightGround, ThemeManager.getTheme().dark(0), true);
		float rPos = SETTING_PANEL.x.value + SETTING_PANEL.width.value;
		float dPos = SETTING_PANEL.y.value + SETTING_PANEL.height.value;
		SETTING_PANEL_RESIZER = (RectPanel) new RectPanel(SETTING_GUI, rPos - 10, dPos - 10, 10, 10, ThemeManager.getTheme().light(0), true).setVisible(false);
		SETTING_GUI.addPanel(SETTING_PANEL, SETTING_PANEL_RESIZER);

		//GL11.glScaled(1/size, 1/size, 1);
		GuiClickGUI.gui.panels = new CopyOnWriteArrayList<>();

		String[] els = new String[]{"ClickGUI", "Map", "Satellite Settings", "Player", "Team", "Waypoints"};
		int y = 0;
		int h = 50;

		GuiClickGUI.menu = new RectPanel(gui, 0, 0, 20, 0, ThemeManager.getTheme().dark(0), true);
		GuiClickGUI.currentScreen = new RectPanel(gui, 0, screen * h, 25, h, ThemeManager.getTheme().dark(3), false);

		gui.addPanel(menu);
		gui.addValue(this.scrolly = new Value(0, AnimationUtil.Mode.EASEOUT));
		menu.setEaseType(AnimationUtil.Mode.EASEOUT);

		GuiClickGUI.gui.addPanel(menu);

		for (@SuppressWarnings("unused") String str : els) {
			RectPanel p = new RectPanel(gui, 0, y, 20, h, ThemeManager.getTheme().dark(1), false);
			p.setEaseType(AnimationUtil.Mode.EASEOUT);
			gui.addPanel(p);
			menuElements.add(p);
			y += h;
		}

		gui.addPanel(currentScreen);
		currentScreen.setEaseType(AnimationUtil.Mode.EASEOUT);


		y = 0;
		for (String str : els) {
			TextPanel p = new TextPanel(gui, 54, y + 25, 0, 0, false, Fonts.font, str, new Color(255, 255, 255, 0, AnimationUtil.Mode.EASEOUT));
			p.setEaseType(AnimationUtil.Mode.EASEOUT);
			gui.addPanel(p);
			menuElements.add(p);
			y += h;
		}
	}

	public static void loadModules() {
		panels = new CopyOnWriteArrayList<>();

		int x = 0;
		for (Module.Category c : Module.Category.values()) {
			List<Module> mods = ModuleManager.getModulesbyCategory(c);

			Collections.sort(mods, new Comparator<Module>() {
				public int compare(Module one, Module other) {
					return one.name.compareTo(other.name);
				}
			});

			if (mods.isEmpty())
				continue;
			x += 120;
			panels.add(new Category(c.name, x, 50, mods));
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		Window window = mc.getWindow();
		if (((ClickGUI) ModuleManager.getModulebyClass(ClickGUI.class)).autoGuiScale.enable) {
			float size = (float) window.getWidth() / 2048;
			if (mc.options.getGuiScale().getValue() == 3) {
				size /= 1.5;
			}
			mouseX /= size;
			mouseY /= size;
			height /= size;
			width /= size;
			context.getMatrices().scale(size,size,1);
		}
		context.getMatrices().translate(0, scrolly.value, 0);
		mouseY -= scrolly.value;
		Client.themeManager.setTheme(((ClickGUI) ModuleManager.getModulebyClass(ClickGUI.class)).theme.getMode());

		isCollided = false;

		menu.setColor(ThemeManager.getTheme().dark(0));
		menu.height.easeTo = height;

		//gui.updateCollision(mouseX, mouseY);

		menu.width.easeTo(menu.isHover() ? 150 : 50, 100, true);

		currentScreen.y.easeTo(mouseX < menu.width.value ? (int) (mouseY / 50) * 50 : currentScreen.y.value, 50, true);
		currentScreen.width = menu.width;
		for (Panel p : menuElements) {
			p.width = menu.width;
			if (p instanceof TextPanel) {
				TextPanel panel = (TextPanel) p;
				panel.alpha.easeTo(menu.isHover() ? 255 : 0, 50, true);
			}
		}
		int screenIndex = (int) ((currentScreen.y.value + 25) / 50);
		isCollided = gui.isCollided;
		switch (screenIndex) {
			case 0:
				List<Category> drawPanel = (List<Category>) panels.clone();
				for (Category c : drawPanel) {
					c.update(mouseX, mouseY);
				}
				Collections.reverse(drawPanel);
				for (Category c : drawPanel) {
					c.draw(mouseX, mouseY, delta);
				}
				Collections.reverse(drawPanel);
				Collections.reverse(drawPanel);
				break;
			case 1:
				RectPanel mapGround = (RectPanel) MAP_GUI.getPanels().get(0);
				mapGround.color.easeTo(255, 255, 255, 0, 50, true);
				int size = 4;
				for (int x = 0; x < ((int) ((width - 100) / size)); x++) {
					for (int y = 0; y < (int) ((height - 50) / size); y++) {
						if (mc.world.getHeight() > mc.player.getY()) ;
						else continue;
						java.awt.Color color = ThemeManager.getTheme().light(0);
						color = ColorUtils.alpha(color, 0x80);
						new RectPanel(MAP_GUI, 75 + x * size, 25 + y * size, size, size, color, false).draw(mouseX, mouseY, delta);
					}
				}
				assert mc.world != null;
				for (Entity ent : mc.world.getEntities()) {
					if (ent == mc.player) continue;
					assert mc.player != null;
					int dx = (int) (ent.getX() - mc.player.getX()) + ((int) ((width - 100) / size)) / 2, dz = (int) (ent.getZ() - mc.player.getZ()) + ((int) ((height - 50) / size)) / 2;
					new RectPanel(MAP_GUI, 75 + dx * size, 25 + dz * size, size, size, ThemeManager.getTheme().dark(0), false).draw(mouseX, mouseY, delta);
				}
				mapGround.width.easeTo(((int) ((width - 100) / size) * size), 50, true);
				mapGround.height.easeTo(((int) ((height - 50) / size) * size), 50, true);
				MAP_GUI.updateEasing();
				MAP_GUI.updateCollision(mouseX, mouseY);
				MAP_GUI.draw(mouseX, mouseY, delta);
				break;
			case 2:
				float rPos = SETTING_PANEL.x.value + SETTING_PANEL.width.value;
				float dPos = SETTING_PANEL.y.value + SETTING_PANEL.height.value;
				SETTING_PANEL.x.easeTo(width / 2 - SETTING_PANEL.width.value / 2, 1, false);
				SETTING_PANEL.y.easeTo(height / 2 - SETTING_PANEL.height.value / 2, 1, false);
				if (glfwGetMouseButton(0, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS && (SETTING_GUI.clickedPanel(SETTING_PANEL_RESIZER))) {
					SETTING_PANEL.width.easeTo(SETTING_PANEL.width.value, 1, false);
					SETTING_PANEL.height.easeTo(SETTING_PANEL.height.value, 1, false);
					if (SETTING_PANEL.width.easeTo < 200) {
						SETTING_PANEL.width.easeTo(200, 1, true);
					}
					if (SETTING_PANEL.height.easeTo < 200) {
						SETTING_PANEL.height.easeTo(200, 1, true);
					}
				}
				SETTING_GUI.updateCollision(mouseX, mouseY);
				SETTING_GUI.updateEasing();
				SETTING_GUI.draw(mouseX, mouseY, delta);
				//SETTING_PANEL_RESIZER = new RectPanel(SETTING_GUI, rPos-10, dPos-10, 10, 10, ThemeManager.getTheme().light(0), true);
				rPos = SETTING_PANEL.x.value + SETTING_PANEL.width.value;
				dPos = SETTING_PANEL.y.value + SETTING_PANEL.height.value;
				SETTING_PANEL_RESIZER.x = new Value(rPos - 10, null);
				SETTING_PANEL_RESIZER.y = new Value(dPos - 10, null);
				SETTING_PANEL_RESIZER.draw(mouseX, mouseY, delta);
				new RectPanel(SETTING_GUI, SETTING_PANEL.x.value, SETTING_PANEL.y.value, 75, SETTING_PANEL.height.value, ThemeManager.getTheme().dark(1), true).draw(mouseX, mouseY, delta);
				new RectPanel(SETTING_GUI, SETTING_PANEL.x.value, SETTING_PANEL.y.value, 75, 40, ThemeManager.getTheme().dark(2), true).draw(mouseX, mouseY, delta);
		}

		lastScreen = screenIndex;
		/*for(int i=0; i<panels.size(); i++) {
			panels.get(i).update(mouseX, mouseY);
		}

		for(int i=1; i<=panels.size(); i++) {
			panels.get(panels.size()-i).draw(mouseX, mouseY, partialTicks);
		}*/
		gui.updateEasing();
		//gui.draw(mouseX, mouseY, partialTicks);
	}

	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		for (Category p : panels) {
			p.onKeyDown(keyCode);
		}
		super.keyPressed(keyCode, scanCode, modifiers);
		return false;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		SETTING_GUI.onMousePressed((int) mouseX, (int) mouseY, button);
		for (Category p : panels) {
			p.mouseClicked((int)mouseX, (int)mouseY, button);
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		SETTING_GUI.onMouseReleased((int) mouseX, (int) mouseY);
		for(Category p : panels) {
			p.mouseReleased((int)mouseX, (int)mouseY, button);
		}
		return  super.mouseReleased(mouseX, mouseY, button);
	}
	public void onClose() {
		ModuleManager.toggle(ClickGUI.class);
	}

}
