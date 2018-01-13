package bots.tests;

import bots.JDAAddon.CJDA;
import bots.JDAAddon.CJDABuilder;
import bots.JDAAddon.Input;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;

import static bots.JDAAddon.CJDABuilder.getToken;

public class TestBot {

	private CJDA bot;

	private TestBot(){

		String token = getToken();

		while(bot == null){

			try {
				bot = new CJDABuilder(AccountType.CLIENT).addMessageHandlers(System.out::println).setToken(token).buildBlocking();

			} catch (LoginException e) {
				System.err.println("error logging in to discord: " + e.getMessage());
			} catch (InterruptedException e) {
				System.err.println("could not connect to discord: " + e.getMessage());
			} catch (RateLimitedException e) {
				System.err.println("slow connect to discord: " + e.getMessage());
			}

		}

		Input input = bot.getInput();

		input.send("/channels");
		input.send("1");
		input.send("17");
		input.send("this was sent from my discord bot");


	}

	public static void main(String args[]){
		new TestBot();
	}

}
