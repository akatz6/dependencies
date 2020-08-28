/*
 *  Aaron Katz
 */

package System;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

public class Dependencies {

	public static void main(String[] args) throws IOException {
		ArrayList<Stack> trackDependencies = new ArrayList<Stack>();
		ArrayList<Stack> trackInstalled = new ArrayList<Stack>();
		HashMap<String, ArrayList> stackPlacement = new HashMap<String, ArrayList>();
		HashMap<String, Integer> count = new HashMap<String, Integer>();
		HashMap<String, Integer> waitingToBeRemoved = new HashMap<String, Integer>();
		HashMap<String, Integer> notExplicitlyAdded = new HashMap<String, Integer>();
		try {
			// TODO create read and write file
			File myObj = new File("input.txt");
			Scanner myReader = new Scanner(myObj);
			FileWriter myWriter = new FileWriter("output.txt");
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				findCommand(data, trackDependencies, trackInstalled, stackPlacement, count, waitingToBeRemoved,
						notExplicitlyAdded, myWriter);
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	private static void findCommand(String data, ArrayList<Stack> trackDependencies, ArrayList<Stack> trackInstalled,
			HashMap<String, ArrayList> stackPlacement, HashMap<String, Integer> count,
			HashMap<String, Integer> waitingToBeRemoved, HashMap<String, Integer> notExplicitlyAdded,
			FileWriter myWriter) throws IOException {
		
		int endOfWord = data.indexOf(" ");
		String command = data;
		if (endOfWord > -1) {
			command = data.substring(0, endOfWord);
		}

		switch (command) {
		case "DEPEND":
			System.out.println(data);
			myWriter.append(data + '\n');
			depend(data.substring(endOfWord, data.length()).trim(), trackDependencies, trackInstalled, stackPlacement,
					myWriter);
			break;
		case "INSTALL":
			System.out.println(data);
			myWriter.append(data + '\n');
			install(data.substring(endOfWord, data.length()).trim(), trackDependencies, trackInstalled, stackPlacement, count,
					notExplicitlyAdded, myWriter);
			break;
		case "REMOVE":
			System.out.println(data);
			myWriter.append(data + '\n');
			remove(data.substring(endOfWord, data.length()).trim(), trackDependencies, trackInstalled, stackPlacement, count,
					waitingToBeRemoved, notExplicitlyAdded, myWriter);
			break;
		case "LIST":
			System.out.println(data);
			myWriter.append(data + '\n');
			print(count, myWriter);
			break;
		case "END":
			System.out.println(data);
			myWriter.append(data + '\n');
			myWriter.close();
			return;
		}
	}

	private static void print(HashMap<String, Integer> count, FileWriter myWriter) {
		// TODO Auto-generated method stub
		Set<String> keys = count.keySet();
		keys.forEach(key -> {
			System.out.println("     " + key);
			try {
				myWriter.append("     " + key + '\n');
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	private static void remove(String substring, ArrayList<Stack> trackDependencies, ArrayList<Stack> trackInstalled,
			HashMap<String, ArrayList> stackPlacement, HashMap<String, Integer> count,
			HashMap<String, Integer> waitingToBeRemoved, HashMap<String, Integer> notExplicitlyAdded,
			FileWriter myWriter) throws IOException {
		if (!count.containsKey(substring)) {
			System.out.println("    " + substring + " is not installed");
			myWriter.append("    " + substring+ " is not installed" + '\n');
			return;
		}
		for (int i = 0; i < trackInstalled.size(); i++) {
			Stack<?> stack = trackInstalled.get(i);
			if (stack.size() > 0 && stack.peek().equals(substring)) {
				String prev = (String) stack.pop();
				if (digOnStack(stack, waitingToBeRemoved, count, prev, notExplicitlyAdded, myWriter)) {
					return;
				}
				int counter = count.get(substring);
				counter--;
				count.put(substring, counter);
			}
		}

		if (count.get(substring) == 0) {
			System.out.println("    Removing " + substring);
			myWriter.append("    Removing " + substring + '\n');
			if (waitingToBeRemoved.containsKey(substring)) {
				waitingToBeRemoved.remove(substring);
			}
			count.remove(substring);
		} else {
			System.out.println("    " + substring + " is still needed");
			myWriter.append("    " + substring + " is still needed" + '\n');
			waitingToBeRemoved.put(substring, 1);
		}
	}

	private static boolean digOnStack(Stack<?> stack, HashMap<String, Integer> waitingToBeRemoved,
			HashMap<String, Integer> count, String prev, HashMap<String, Integer> notExplicitlyAdded,
			FileWriter myWriter) throws IOException {
		Stack<String> digStack = new Stack<String>();
		if (stack.size() > 0 && notExplicitlyAdded.containsKey(stack.peek().toString()) == true
				&& count.get(stack.peek()) == 1) {
			while (stack.size() > 0 && notExplicitlyAdded.containsKey(stack.peek().toString()) == true
					&& count.get(stack.peek()) == 1) {
				if (notExplicitlyAdded.containsKey(stack.peek().toString())) {
					digStack.add(stack.peek().toString());
					notExplicitlyAdded.remove(stack.peek());
					stack.pop();
				} else {
					return false;
				}
			}
			if (count.get(prev) == 1) {
				System.out.println("    Removing " + prev);
				myWriter.append("    Removing " + prev + '\n');
				count.remove(prev);
				while (digStack.size() > 0) {
					if (count.get(digStack.peek()) == 1) {
						System.out.println("    Removing " + digStack.peek());
						myWriter.append("    Removing " + digStack.peek() + '\n');
						count.remove(digStack.pop());
					}
				}
				return true;
			}
		}
		return false;
	}

	private static void install(String substring, ArrayList<Stack> trackDependencies, ArrayList<Stack> trackInstalled,
			HashMap<String, ArrayList> stackPlacement, HashMap<String, Integer> count,
			HashMap<String, Integer> notExplicitlyAdded, FileWriter myWriter) throws IOException {
		if (count.containsKey(substring)) {
			System.out.println("    " + substring + " is already installed");
			myWriter.append("    " + substring + " is already installed" + '\n');
			return;
		}
		
		if (!stackPlacement.containsKey(substring)) {
			System.out.println("    Installing " + substring);
			myWriter.append("    Installing " + substring + '\n');
			count.put(substring, 1);
			return;
		}
		if (stackPlacement.containsKey(substring)) {
			ArrayList<Integer> stackInstalled = stackPlacement.get(substring);
			while (0 < stackInstalled.size()) {
				int i = stackInstalled.remove(0);
				Stack<String> stackDepends = trackDependencies.get(i);
				Stack<String> stackInstall = trackInstalled.get(i);
				if (stackDepends.peek().compareTo(substring) == 0) {
					stackDepends.pop();
					stackInstall.add(substring);
				} else {
					Queue<String> checkInstalled = new LinkedList<>();
					Stack<String> notExplicitlyAddedTemp = new Stack<String>();
					while (0 < stackDepends.size()) {
						String install = stackDepends.pop();
						stackInstall.add(install);
						checkInstalled.add(install);
						notExplicitlyAddedTemp.add(install);
						if (!count.containsKey(install)) {
							System.out.println("    Installing " + install);
							myWriter.append("    Installing " + install + '\n');
							count.put(install, 1);

						} else {
							int counter = count.get(install);
							counter++;
							count.put(install, counter);
						}
					}
					notExplicitlyAddedTemp.pop();
					while (0 < notExplicitlyAddedTemp.size()) {
						notExplicitlyAdded.put(notExplicitlyAddedTemp.pop(), 1);
					}

					installDependents(checkInstalled, stackPlacement, count, trackDependencies, trackInstalled);
					break;
				}
				if (!count.containsKey(substring)) {
					System.out.println("    Installing " + substring);
					myWriter.append("    Installing " + substring + '\n');
					count.put(substring, 1);
				} else {
					int counter = count.get(substring);
					counter++;
					count.put(substring, counter);
				}
				if (stackInstalled.size() < 1) {
					stackPlacement.remove(substring);
				} else {
					stackPlacement.put(substring, stackInstalled);
				}
			}
		} else {
			count.put(substring, 1);

		}
	}

	private static void installDependents(Queue<String> checkInstalled, HashMap<String, ArrayList> stackPlacement,
			HashMap<String, Integer> count, ArrayList<Stack> trackDependencies, ArrayList<Stack> trackInstalled) {
		while (0 < checkInstalled.size()) {
			String str = checkInstalled.remove();
			ArrayList<?> arr = stackPlacement.get(str);
			ArrayList<Integer> foundInStacks = new ArrayList<Integer>();
			for (int i = 0; i < arr.size(); i++) {
				int number = (int) arr.get(i);
				Stack<?> stack = trackDependencies.get(number);
				if (stack.size() > 0 && str.compareTo(stack.peek().toString()) == 0) {
					Stack<String> install = trackInstalled.get(i);
					install.add(str);
					foundInStacks.add(i);
					stack.pop();
				}
			}
			for (int i = 0; i < foundInStacks.size(); i++) {
				int counter = count.get(str);
				counter++;
				count.put(str, counter);
			}
		}

	}

	private static void depend(String str, ArrayList<Stack> trackDependencies, ArrayList<Stack> trackInstalled,
			HashMap<String, ArrayList> stackPlacement, FileWriter myWriter) {

		Stack<String> stackDepends = new Stack<String>();
		Stack<String> stackInstall = new Stack<String>();
		String[] words = str.split(" ");
		for (int i = 0; i < words.length; i++) {
			if (!words[i].isEmpty()) {
				stackDepends.add(words[i]);
			}
		}
		trackDependencies.add(stackDepends);
		trackInstalled.add(stackInstall);
		int placement = trackDependencies.size() - 1;
		for (int i = 0; i < words.length; i++) {
			if (!words[i].isEmpty()) {
				if (!stackPlacement.containsKey(words[i])) {
					ArrayList<Integer> stackInstalled = new ArrayList<Integer>();
					stackInstalled.add(placement);
					stackPlacement.put(words[i], stackInstalled);
				} else {
					ArrayList<Integer> stackInstalled = stackPlacement.get(words[i]);
					stackInstalled.add(placement);
					stackPlacement.put(words[i], stackInstalled);
				}
			}
		}
	}

}
