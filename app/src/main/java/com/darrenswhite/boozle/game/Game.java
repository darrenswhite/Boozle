package com.darrenswhite.boozle.game;

import android.content.Context;
import com.darrenswhite.boozle.util.FixedQueue;
import com.darrenswhite.boozle.util.Function;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Darren White
 */
public class Game implements Serializable {

	private final List<Action> actions = new ArrayList<>();
	private final Random rnd = new Random();
	private final FixedQueue<Action> stack = new FixedQueue<>(5);
	private final List<Player> players = new ArrayList<>();
	private int total = 0;
	private int playerIndex = 0;

	public void add(Player p) {
		if (players.contains(p)) {
			throw new IllegalArgumentException("Duplicate player");
		}

		players.add(p);
	}

	public void add(Action a) {
		if (actions.contains(a)) {
			throw new IllegalArgumentException("Duplicate action");
		}
		if (a.weight < 0) {
			throw new IllegalArgumentException("Weight must be greater than 0");
		}

		if (a.enabled) {
			total += a.weight;
			a.position = total;
		} else {
			a.position = -1;
		}

		actions.add(a);

		recalculate();
	}

	public void clear(boolean clearPlayers) {
		total = 0;
		actions.clear();

		if (clearPlayers) {
			players.clear();
		}
	}

	public int enabledActions() {
		int enabled = 0;

		for (Action a : actions) {
			if (a.isEnabled()) {
				enabled++;
			}
		}

		return enabled;
	}

	public int enabledPlayers() {
		int enabled = 0;

		for (Player p : players) {
			if (p.isEnabled()) {
				enabled++;
			}
		}

		return enabled;
	}

	public List<Action> getActions() {
		return actions;
	}

	public Action getCurrent() {
		return stack.poll();
	}

	public List<Player> getPlayers() {
		return players;
	}

	@SuppressWarnings("unchecked")
	public void load(InputStream is) throws Exception {
		try (ObjectInputStream in = new ObjectInputStream(is)) {

			clear(true);

			int len = in.readInt();
			while (len-- > 0) {
				add(new Action(in.readUTF(), in.readUTF(), (Function<String, String>) in.readObject(), in.readInt(), in.readBoolean()));
			}

			len = in.readInt();
			while (len-- > 0) {
				add(new Player(in.readUTF(), in.readBoolean()));
			}
		}
	}

	public Action next() {
		if (total <= 0) {
			return null;
		}

		double r = rnd.nextInt(total);

		for (Action a : actions) {
			if (a.enabled && a.weight > 0 && a.position != -1 && r < a.position) {
				if (enabledActions() > 5 && (stack.count(a) > 1 || a.equals(stack.poll()))) {
					return next();
				}

				if (enabledPlayers() > 0) {
					while (!(a.player = players.get(playerIndex++ % players.size())).enabled) {
					}
				} else {
					a.player = null;
				}

				stack.add(a);

				return a;
			}
		}

		return null;
	}

	private void recalculate() {
		total = 0;

		for (Action a : actions) {
			if (a.enabled && a.weight > 0) {
				total += a.weight;
				a.position = total;
			} else {
				a.position = -1;
			}
		}
	}

	public void remove(Action a) {
		if (!actions.contains(a)) {
			throw new IllegalStateException();
		}

		actions.remove(a);

		recalculate();
	}

	public void remove(Player p) {
		if (!players.contains(p)) {
			throw new IllegalStateException();
		}

		players.remove(p);
	}

	public void save(OutputStream os) throws IOException {
		try (ObjectOutputStream out = new ObjectOutputStream(os)) {

			out.writeInt(actions.size());

			for (Action a : actions) {
				out.writeUTF(a.text);
				out.writeUTF(a.description);
				out.writeObject(a.function);
				out.writeInt(a.weight);
				out.writeBoolean(a.enabled);
			}

			out.writeInt(players.size());

			for (Player p : players) {
				out.writeUTF(p.name);
				out.writeBoolean(p.enabled);
			}

			out.flush();
		}
	}

	public void saveCustom(Context c) throws IOException {
		save(new FileOutputStream(new File(c.getDir("boozle", Context.MODE_PRIVATE), "custom.dat")));
	}

	public void setEnabled(Action a, boolean enabled) {
		if (!actions.contains(a)) {
			throw new IllegalArgumentException();
		}

		a.enabled = enabled;

		recalculate();
	}

	public void setEnabled(Player p, boolean enabled) {
		if (!players.contains(p)) {
			throw new IllegalArgumentException();
		}

		p.enabled = enabled;
	}

	public void setWeight(Action action, int weight) {
		if (!actions.contains(action)) {
			throw new IllegalArgumentException();
		}

		action.weight = weight;

		recalculate();
	}

	public int size() {
		return actions.size();
	}

	@Override
	public String toString() {
		return "Game{actions=" + actions + '}';
	}

	public int total() {
		return total;
	}
}