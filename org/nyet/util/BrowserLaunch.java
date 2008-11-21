package org.nyet.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import javax.swing.JOptionPane;

public class BrowserLaunch {

    private static final String errMsg =
	"Error attempting to launch web browser";

    public static void openURL(String url) {
	boolean error = true;
	try {
	    Class<?> Desktop = Class.forName("java.awt.Desktop");
	    Method isDesktopSupported =
		Desktop.getDeclaredMethod("isDesktopSupported");
	    if ((Boolean) isDesktopSupported.invoke(null)) {
		java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
		return;
	    }
	} catch (Exception e) {
	}
	fallback(url);
    }

    private static void fallback(String url) {
        String osName = System.getProperty("os.name");
        try {
	    if (osName.startsWith("Mac OS")) {
		Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
		Method openURL = fileMgr.getDeclaredMethod("openURL",
		    new Class[] {String.class});
		openURL.invoke(null, new Object[] {url});
	    } else if (osName.startsWith("Windows"))
		Runtime.getRuntime().exec(
		    "rundll32 url.dll,FileProtocolHandler " + url);
	    else { //assume Unix or Linux
		String[] browsers = {
		    "firefox", "iceweasel", "opera", "konqueror", "epiphany",
		    "mozilla", "netscape" };
		String browser = null;
		for (int i = 0; i <browsers.length && browser == null; i++)
		    if (Runtime.getRuntime().exec(
			new String[] {"which", browsers[i]}).waitFor() == 0)
		    browser = browsers[i];
		if (browser == null)
		    throw new Exception("Could not find web browser");
		else
		    Runtime.getRuntime().exec(new String[] {browser, url});
	    }
        } catch (Exception e) {
	    JOptionPane.showMessageDialog(null,
		errMsg + ":\n" + e.getLocalizedMessage());
	}
    }
}