package bots.JDAAddon;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;
import java.util.*;

public class Input extends ListenerAdapter implements Runnable {

	protected JDA bot;
	protected MessageChannel SelectedChannel;
	private StringBuilder msg = new StringBuilder();
	private boolean intaking = false;

	//---------------------------------init---------------------------------
	Input(JDA bot){
		this.bot = bot;
	}

	//---------------------------------run---------------------------------
	public void run(){
		SelectedChannel = SelectChannel();

		while (true) {
			String msg = input();

			if (msg.equals("/disconnect") || msg.equals("/exit")){
				bot.shutdown();
				break;

			}else if (msg.equals("/channels")) {
				SelectedChannel = null;
				cls();
				SelectedChannel = SelectChannel();

			}else if (msg.length() > 5 && msg.substring(0,5).equals("/read")) {

				try {
					read(Integer.parseInt(msg.substring(6)), SelectedChannel);

				} catch (NumberFormatException e) {
					System.err.println("please enter valid number");
				}

			}else if(msg.equals("/clear")){
				cls();

			}else if(!msg.isEmpty()) SelectedChannel.sendMessage(msg).queue();

		}

	}

	//---------------------------------Select from list---------------------------------
	private <T> T Select(List<T> list){
		T Selected = null;
		boolean loop = true;

		while(loop){
			for (int i = 0; i < list.size(); i++) {
				System.out.println(String.valueOf(i+1) + ": " + list.get(i));
			}

			try{
				Selected = list.get(Integer.parseInt(input()) - 1);
				loop = false;

			}catch (InputMismatchException e) {
				System.out.println("please inputis an int");

			}catch (IndexOutOfBoundsException e) {
				System.out.println("please inputis valid channel\n");
			}
		}

		return Selected;

	}

	//---------------------------------select channel---------------------------------
	private MessageChannel SelectChannel(){
		List<NamedList<MessageChannel>> Servers = new ArrayList<>();
		//TODO show unread messages

		NamedList<MessageChannel> PMs = new NamedList<>("PMs");
		PMs.addAll(bot.getPrivateChannels());
		Servers.add(PMs);

		for(Guild i: bot.getGuilds()){

			NamedList<MessageChannel> guild = new NamedList<>(i.getName());

			guild.addAll(i.getTextChannels());

			Servers.add(guild);
		}

		MessageChannel selected = Select(Select(Servers));
		cls();

		read(25, selected);

		return selected;
	}

	//---------------------------------read msgs---------------------------------
	private void read(int msgNum, MessageChannel SelectedChannel){
		List<Message> history = SelectedChannel.getHistory().retrievePast(msgNum).complete();
		history = history.subList(0, history.size());
		Collections.reverse(history);

		for (Message i: history){
			System.out.println(i.getAuthor().getName() + ": " + i.getContent());
		}

	}

	//---------------------------------clear screen---------------------------------
	private void cls() {
		for(int i = 0; i < 100; i++){
			System.out.println();
		}
	}

	MessageChannel getSelectedChannel() {
		return SelectedChannel;
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {

		//TODO add custom message output

		Message msg = event.getMessage();

		if(SelectedChannel != null && msg.getChannel().equals(SelectedChannel)){
			System.out.println(msg.getAuthor().getName() + ": " + msg.getContent());
		}
	}
	
	//TODO fix input and send messages

	private String input() {
		synchronized (msg) {
			try {
				System.out.println("intaking and waiting");
				intaking = true;
				msg.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		return msg.toString();
	}


	public void send(String message){
		System.out.println("send wait started");

		try {
			while (!intaking) {
				Thread.sleep(50L);
			}

		}catch (InterruptedException e){
			System.err.println("wait for intake interupted: " + e.getMessage());
			System.exit(1);
		}


		System.out.println("send wait ended");

		intaking = false;

		synchronized (msg) {
			msg.delete(0,msg.length());
			msg.append(message);
			msg.notifyAll();
		}
	}
}
