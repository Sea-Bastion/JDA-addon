package bots.tests;

import bots.JDAAddon.CJDA;
import bots.JDAAddon.CJDABuilder;
import bots.JDAAddon.Input;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;

public class TestBot {

	private CJDA bot;
	private String token = "mfa.W6B7F0VQFXomciEritB6hSYxmOwP1jF0fHe9kHB4RQPayytJkxcaR48pUymS_b8o9kQ3u4VeiDU6GUHXKBcv";
	private Input input;

	private TestBot(){

		while(bot == null){

			try {
				bot = new CJDABuilder(AccountType.CLIENT).setToken(token).buildBlocking();

			} catch (LoginException e) {
				System.err.println("error logging in to discord");
			} catch (InterruptedException e) {
				System.err.println("could not connect to discord");
			} catch (RateLimitedException e) {
				System.err.println("slow connect to discord");
			}

		}

		input = bot.getInput();

		input.send("4");


	}

	public static void main(String args[]){
		new TestBot();
	}

}
