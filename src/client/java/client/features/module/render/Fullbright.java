package client.features.module.render;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Fullbright extends Module {
	public Fullbright() {
		super("Fullbright", 310,	Category.RENDER);
	}

	double lastGamma;

    @Override
    public void onEvent(Event<?> e) {
    	if(e instanceof EventUpdate) {
    		mc.options.getGamma().setValue(10E+3);
    	}
    	super.onEvent(e);
    }

	@Override
	public void onEnable() {
		lastGamma = (Double) mc.options.getGamma().getValue();
		super.onEnable();
	}

	@Override
	public void onDisable() {
		mc.options.getGamma().setValue(lastGamma);
		super.onDisable();
	}
}
