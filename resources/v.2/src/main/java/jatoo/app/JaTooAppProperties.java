package jatoo.app;

import jatoo.properties.FileProperties;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;

@SuppressWarnings("serial")
public class JaTooAppProperties extends FileProperties {

	public JaTooAppProperties(File file) {
		super(file);
	}

	public boolean isOpened() {
		return getPropertyAsBoolean("opened", true);
	}

	public void setOpened(boolean opened) {
		setProperty("opened", opened);
	}

	public Point getLocation(Point defaultLocation) {
		return getPropertyAsPoint("location", defaultLocation);
	}

	public void setLocation(Point location) {
		setProperty("location", location);
	}

	public Dimension getSize(Dimension defaultSize) {
		return getPropertyAsDimension("size", defaultSize);
	}

	public void setSize(Dimension size) {
		setProperty("size", size);
	}

	public boolean isAlwaysOnTop() {
		return getPropertyAsBoolean("alwaysOnTop", false);
	}

	public void setAlwaysOnTop(boolean alwaysOnTop) {
		setProperty("alwaysOnTop", alwaysOnTop);
	}

	public int getTransparency(int defaultTransparency) {
		return getPropertyAsInt("transparency", defaultTransparency);
	}

	public void setTransparency(int transparency) {
		setProperty("transparency", transparency);
	}

	@SuppressWarnings("unchecked")
	public Class<? extends JaTooAppUIComponent> getUIComponentClass(Class<? extends JaTooAppUIComponent> defaultContentPaneClass) {
		return (Class<? extends JaTooAppUIComponent>) getPropertyAsClass("uiComponentClass", defaultContentPaneClass);
	}

	public void setUIComponentClass(Class<? extends JaTooAppUIComponent> uiComponentClass) {
		setProperty("uiComponentClass", uiComponentClass);
	}

}
