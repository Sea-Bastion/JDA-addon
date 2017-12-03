package bots.JDAAddon;

import java.util.ArrayList;

class NamedList<E> extends ArrayList {

	String name;

	public NamedList(String name, int cap){
		super(cap);
		this.name = name;
	}

	public NamedList(String name){
		super();
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
