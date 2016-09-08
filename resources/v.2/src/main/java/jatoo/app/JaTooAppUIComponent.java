package jatoo.app;

import java.awt.Insets;

import frameworks.spring.SpringComponent;

@SuppressWarnings("serial")
public abstract class JaTooAppUIComponent extends SpringComponent {

	private final Insets marginsGlueGaps = new Insets(0, 0, 0, 0);

	public Insets getMarginsGlueGaps() {
		return marginsGlueGaps;
	}

}
