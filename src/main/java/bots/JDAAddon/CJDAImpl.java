package bots.JDAAddon;

import com.neovisionaries.ws.client.WebSocketFactory;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import okhttp3.OkHttpClient;

import java.util.List;

public class CJDAImpl extends JDAImpl implements CJDA {

	private Input input;
	private boolean InputReady = false;
	private List<MessageHandler> MsgHandlers;

	public CJDAImpl(AccountType accountType, OkHttpClient.Builder httpClientBuilder, WebSocketFactory wsFactory, boolean autoReconnect, boolean audioEnabled, boolean useShutdownHook, boolean bulkDeleteSplittingEnabled, int corePoolSize, int maxReconnectDelay, List<MessageHandler> MsgHandlers) {
		super(accountType, httpClientBuilder, wsFactory, autoReconnect, audioEnabled, useShutdownHook, bulkDeleteSplittingEnabled, corePoolSize, maxReconnectDelay);

		this.MsgHandlers = MsgHandlers;
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

		new Thread(input = new Input(this, MsgHandlers)).start();
		this.addEventListener(input);
		InputReady = true;
	}


	@Override
	public Input getInput() throws NullPointerException {

		if(input == null) throw new NullPointerException("Bot not finished loggin in");

		return input;
	}

	@Override
	public boolean InputReady() {
		return InputReady;
	}
}
