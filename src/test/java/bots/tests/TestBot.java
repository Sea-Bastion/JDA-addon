package bots.tests;

import static bots.JDAAddon.CJDABuilder.*;

import bots.JDAAddon.CJDA;
import bots.JDAAddon.CJDABuilder;
import bots.JDAAddon.Input;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;

public class TestBot {

	private CJDA bot;

	private TestBot(){

		String token = getToken();

		while(bot == null){

			try {
				bot = new CJDABuilder(AccountType.CLIENT).setToken(token).buildBlocking();

			} catch (LoginException e) {
				System.err.println("error logging in to discord: " + e.getMessage());
			} catch (InterruptedException e) {
				System.err.println("could not connect to discord: " + e.getMessage());
			} catch (RateLimitedException e) {
				System.err.println("slow connect to discord: " + e.getMessage());
			}

		}

		Input input = bot.getInput();

		input.send("4");
		input.send("1");
		input.send("ur mum gey");


	}

	public static void main(String args[]){
		new TestBot();
	}

}
