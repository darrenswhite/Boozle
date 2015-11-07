package com.darrenswhite.boozle;

import com.darrenswhite.boozle.game.Action;
import com.darrenswhite.boozle.game.Game;
import com.darrenswhite.boozle.util.Function;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * @author Darren White
 */
public class Application {

	private static final Object[][] ACTIONS = {
			{"BOOZLE!\n", "The current person must keep drinking whilst spinning around for the number of times shown.", createBoozleFunction()},
			{"Boys only", "All boys must drink", null},
			{"Do a dance", "The current person starts with a single dance move. " +
					"Each person must then perform all previous moves and add a new move. " +
					"Continue until someone fails; that person must drink", null},
			{"Down it!", "Finish all of your drink in one go.", null},
			{"Drink", "The current person must drink.", null},
			{"Everyone drink", "Everyone playing must drink.", null},
			{"Girls only", "All girls must drink", null},
			{"Make a rhyme", "The current person says a word. Everyone must say a word that rhymes with the first word. " +
					"Continue until someone fails; that person must drink.", null},
			{"Make a rule", "The current person can add a rule. If someone does abide by any of the rules they must drink", null},
			{"Nominate a friend", "Choose another person to drink.", null},
			{"True or false?", "You must say something about yourself which can be the truth or a lie. " +
					"Everyone must guess if you are telling the truth or not. Everyone who is wrong must drink.", null},
			{"Truth or dare?", "Anyone can ask you truth or dare. You must either tell the truth or do a dare for that turn.", null},
			{"Waterfall!", "The current person must start drinking. Each person starts drinking when " +
					"the person next to them starts drinking. Each person may stop drinking only when the " +
					"person next to them if they want to.", null}
	};
	private static final int[] HEAVYWEIGHT = {10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10};
	private static final int[] LIGHTWEIGHT = {10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10};
	private static final int[] NORMAL = {10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10};
	private static final int[] BOOZLE_KING = {10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10};
	private static final String ASSETS_DIR = "app/src/main/assets/";
	private static Game game;

	@SuppressWarnings("unchecked")
	private static void addAction(Object[] action, int weight) {
		game.add(new Action((String) action[0], (String) action[1], (Function<String, String>) action[2], weight, weight != -1));
	}

	private static Function<String, String> createBoozleFunction() {
		return new Function<String, String>() {

			Random rnd = new Random();

			@Override
			public String apply(String s) {
				return s + nextGaussian(rnd, 1, 11, 5, 3);
			}
		};
	}

	private static void createBoozleking() throws IOException {
		game.clear(true);

		for (int i = 0; i < ACTIONS.length; i++) {
			addAction(ACTIONS[i], BOOZLE_KING[i]);
		}

		game.save(new FileOutputStream(ASSETS_DIR + "boozle_king.dat"));
	}

	private static void createHeavyweight() throws IOException {
		game.clear(true);

		for (int i = 0; i < ACTIONS.length; i++) {
			addAction(ACTIONS[i], HEAVYWEIGHT[i]);
		}

		game.save(new FileOutputStream(ASSETS_DIR + "heavyweight.dat"));
	}

	private static void createLightweight() throws IOException {
		game.clear(true);

		for (int i = 0; i < ACTIONS.length; i++) {
			addAction(ACTIONS[i], LIGHTWEIGHT[i]);
		}

		game.save(new FileOutputStream(ASSETS_DIR + "lightweight.dat"));
	}

	private static void createNormal() throws IOException {
		game.clear(true);

		for (int i = 0; i < ACTIONS.length; i++) {
			addAction(ACTIONS[i], NORMAL[i]);
		}

		game.save(new FileOutputStream(ASSETS_DIR + "normal.dat"));
	}

	public static void main(String[] args) throws IOException {
		game = new Game();

		createBoozleking();
		createHeavyweight();
		createLightweight();
		createNormal();

		game.clear(true);

		try {
			game.load(new FileInputStream(ASSETS_DIR + "normal.dat"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (Action a : game.getActions()) {
			System.out.println(a);
		}
		System.out.println();

		Map<Action, Integer> test = new TreeMap<>();
		double total = 1000000;

		for (int i = 0; i < total; i++) {
			Action next = game.next();
			if (test.containsKey(next)) {
				test.put(next, test.get(next) + 1);
			} else {
				test.put(next, 1);
			}
		}

		for (Map.Entry<Action, Integer> entry : test.entrySet()) {
			System.out.println(entry.getKey().getText().trim() + " = " + entry.getValue() / total * 100);
		}
	}

	public static synchronized int nextGaussian(Random rnd, int min, int max, int mean, int sd) {
		if (min == max) {
			return min;
		}

		int rand;
		do {
			rand = (int) (rnd.nextGaussian() * sd + mean);
		} while (rand < min || rand >= max);

		return rand;
	}
}