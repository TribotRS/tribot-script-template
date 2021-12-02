package scripts;

import org.tribot.script.sdk.Log;
import org.tribot.script.sdk.script.TribotScript;
import org.tribot.script.sdk.script.TribotScriptManifest;

import java.io.IOException;

@TribotScriptManifest(name = "MyScript", author = "Me", category = "Template", description = "My example script")
public class MyScript implements TribotScript {

	@Override
	public void execute(final String args) {
		// Example: Call our shared library class
		SampleHelper.getHello();
		try {
			// Example: Load a resource
			byte[] resourceContents = getClass().getClassLoader().getResourceAsStream("scripts/my-resource.txt").readAllBytes();
			Log.log(new String(resourceContents));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
