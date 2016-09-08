package jatoo.app;

import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("serial")
public class JaTooAppContentPaneImpl extends JaTooAppContentPane {

	@Autowired
	private JaTooApp app;

	@Override
	protected void initContentPane() throws Throwable {
		new Thread() {
			public void run() {

				sleepThread();
				
				app.showTrayMessage("1 ncerc sa configurez un system de test pentru Migros. POS", "1 mesPOS-ul face restart la linia din system.log:\n“lisysld: System Shutdown requested by 00000 Loc 011”sage");
				sleepThread();
				app.showTrayInformationMessage("22 ncerc sa configurez un system de test pentru Migros.", "22 mesPOS-ul face restart la .log:\n“lisysld: requested by 00000 Loc 011”sage");
				sleepThread();
				app.showTrayWarningMessage("333 ncerc sa configurez un system de test pentru Migros. POS", "333 mesPOS-ul face restart la linia din system.log:\n“lisysld: System Shutdown requested by 00000 Loc 011”sage");
				sleepThread();
				app.showTrayErrorMessage("4444 ncerc sa configurez un system de test pentru Migros.", "4444 mesPOS-ul face restart la .log:\n“lisysld: requested by 00000 Loc 011”sage");
			}
		}.start();
	}

	private void sleepThread(){
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
	}
	
}
