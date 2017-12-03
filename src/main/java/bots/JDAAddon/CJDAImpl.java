package bots.JDAAddon;

import com.neovisionaries.ws.client.WebSocketFactory;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import okhttp3.OkHttpClient;

public class CJDAImpl extends JDAImpl implements CJDA {

	private Input input;

	public CJDAImpl(AccountType accountType, OkHttpClient.Builder httpClientBuilder, WebSocketFactory wsFactory, boolean autoReconnect, boolean audioEnabled, boolean useShutdownHook, boolean bulkDeleteSplittingEnabled, int corePoolSize, int maxReconnectDelay) {
		super(accountType, httpClientBuilder, wsFactory, autoReconnect, audioEnabled, useShutdownHook, bulkDeleteSplittingEnabled, corePoolSize, maxReconnectDelay);

		new Thread(this::WaitForInput).start();
	}


	private void WaitForInput() {
		while(this.getStatus() != Status.CONNECTED) {
			try {
				Thread.sleep(50L);

			} catch (InterruptedException e) {
				System.err.println("failed to wait for CJDA to log in");
				System.exit(1);
			}
		}

		new Thread(input = new Input(this));
		this.addEventListener(input);
	}


	@Override
	public Input getInput() {
		return input;
	}
}
