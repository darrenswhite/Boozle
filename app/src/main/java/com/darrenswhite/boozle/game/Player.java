package com.darrenswhite.boozle.game;

import java.io.Serializable;

/**
 * @author Darren White
 */
public class Player implements Serializable {

	protected String name;
	protected boolean enabled;

	public Player(String name, boolean enabled) {
		this.name = name;
		this.enabled = enabled;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public String toString() {
		return "Player{name='" + name + "', enabled=" + enabled + '}';
	}
}