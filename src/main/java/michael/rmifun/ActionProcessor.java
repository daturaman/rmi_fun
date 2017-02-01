/*
 * <copyright>
 *
 * Copyright (c) 2010-2017 Gresham Technologies plc. All rights reserved.
 *
 * </copyright>
 */
package michael.rmifun;

import java.util.concurrent.ConcurrentMap;

/**
 * Interprets string actions entered at command prompt and performs the corresponding action on a provided map.
 * 
 * @author mcarter
 */
class ActionProcessor<K, V> {
	private ConcurrentMap<K, V> map;

	ActionProcessor(ConcurrentMap<K, V> map) {
		this.map = map;
	}

	void process(String action){
		if(action.equals("help")){
			System.out.println("Permitted commands: \n" +
					"put {key} {val} - Stores a new kvp in the map\n" +
					"get {key} - Gets the value associated with {key}\n" +
					"list - Lists all currently stored values\n" );
		}
		else if(action.equals("quit")||action.equals("exit")){
			System.exit(0);
		}
		System.out.println("Your action was: " + action);
	}
}
