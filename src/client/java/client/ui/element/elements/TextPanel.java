package client.ui.element.elements;

import client.ui.element.Panel;
import client.utils.font.Fonts;
import client.utils.font.TTFFontRenderer;
import client.utils.render.ColorUtils;
import client.utils.render.RenderUtils;
import client.utils.render.easing.Color;
import client.utils.render.easing.Value;
import client.ui.element.ElementManager;

public class TextPanel extends Panel {
	
	public String text;
	public Color color;
	public Value alpha;
	public TextPanel(ElementManager elementManager, Value x, Value y, Value w, Value h, boolean isCollidable, TTFFontRenderer font, String text, Color color) {
		super(elementManager, x, y, w, h, isCollidable);
		this.text = text;
		this.color = color;
		this.alpha = new Value(255, null);
		addValues(this.alpha);
	}

	public TextPanel(ElementManager elementManager, double x, double y, double w, double h, boolean isCollidable, TTFFontRenderer font, String text, Color color) {
		this(elementManager, new Value(x, null), new Value(y, null), new Value(w, null), new Value(h, null), isCollidable, font, text, color);
	}

	public void setAlpha(int alpha) {
		this.alpha.value = alpha;
	}

	public int getAlpha() {
		return (int) alpha.value;
	}

	@Override
	public void draw(int mouseX, int mouseY, float partialTicks) {
		Fonts.font.drawString(text, (int)x.getValue(), (int)y.getValue(), -1);
		super.draw(mouseX, mouseY, partialTicks);
	}
}
