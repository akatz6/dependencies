package System;

import static org.junit.Assert.*;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import org.junit.Test;

public class systemDependenciesTest {

	@Test
	public void test_add_depend() throws IOException {
		
		ArrayList<Stack> trackDependencies = new ArrayList<Stack>();
		ArrayList<Stack> trackInstalled = new ArrayList<Stack>();
		HashMap<String, ArrayList> stackPlacement = new HashMap<String, ArrayList>();
		HashMap<String, Integer> count = new HashMap<String, Integer>();
		HashMap<String, Integer> waitingToBeRemoved = new HashMap<String, Integer>();
		HashMap<String, Integer> notExplicitlyAdded = new HashMap<String, Integer>();
		FileWriter myWriter = new FileWriter("output.txt");
		
		Dependencies junit = new Dependencies();
		junit.findCommand("DEPEND   TELNET TCPIP NETCARD", trackDependencies, trackInstalled, stackPlacement, count, waitingToBeRemoved, notExplicitlyAdded, myWriter);
		
		junit.findCommand("DEPEND DNS TCPIP NETCARD", trackDependencies, trackInstalled, stackPlacement, count, waitingToBeRemoved, notExplicitlyAdded, myWriter);
		
		ArrayList expectedVal = new ArrayList();
		expectedVal.add(1);
		assertEquals(stackPlacement.get("DNS"), expectedVal);
		
		expectedVal.clear();
		expectedVal.add(0);
		assertEquals(stackPlacement.get("TELNET"), expectedVal);
		
		expectedVal.add(1);
		assertEquals(stackPlacement.get("TCPIP"), expectedVal);
		assertEquals(stackPlacement.get("NETCARD"), expectedVal);
	}


	@Test
	public void test_remove_component() throws IOException {
		String substring= "TELNET";
		ArrayList<Stack> trackDependencies = new ArrayList<Stack>();
		Stack<String> stack3 = new Stack<String>();
		Stack<String> stack = new Stack<String>();
		trackDependencies.add(stack3);
		ArrayList<Stack> trackInstalled = new ArrayList<Stack>();
		stack.clear();
		stack.add("TELNET");
		stack.add("TCPIP");
		stack.add("NETCARD");
		trackInstalled.add(stack);
		Stack<String> stack2 = new Stack<String>();
		stack2.add("TELNET");
		stack2.add("TCPIP");
		trackInstalled.add(stack2);
		HashMap<String, ArrayList> stackPlacement = new HashMap<String, ArrayList>();
		ArrayList list = new ArrayList();
		stackPlacement.put("BROWSER", list);
		stackPlacement.put("TELNET", list);
		HashMap<String, Integer> count = new HashMap<String, Integer>();
		count.put("BROWSER", 1);
		count.put("TELNET", 0);
		count.put("TCPIP", 4);
		HashMap<String, Integer> waitingToBeRemoved = new HashMap<String, Integer>();
		waitingToBeRemoved.put("NETCARD", 1);
		HashMap<String, Integer> notExplicitlyAdded = new HashMap<String, Integer>();
		notExplicitlyAdded.put("TCPIP", 1);
		notExplicitlyAdded.put("HTML", 1);
		FileWriter myWriter = new FileWriter("output.txt");
		Dependencies junit = new Dependencies();
		
		junit.remove(substring, trackDependencies, trackInstalled, stackPlacement, count, waitingToBeRemoved, notExplicitlyAdded, myWriter);

		assertEquals(count.get("TELNET"), null);
	}
	
	@Test
	public void test_remove_none_explicitly_added() throws IOException {
		Stack stack = new Stack();
		stack.add("TCPIP");
		stack.add("HTML");
		HashMap<String, Integer> waitingToBeRemoved = new HashMap<String, Integer>();
		waitingToBeRemoved.put("TCPIP", 1);
		waitingToBeRemoved.put("NETCARD", 1);
		HashMap<String, Integer> count = new HashMap<String, Integer>();
		count.put("BROWSER", 1);
		count.put("foo", 1);
		count.put("TCPIP", 1);
		count.put("HTML", 1);
		count.put("NETCARD", 3);
		String prev = "BROWSER";
		FileWriter myWriter = new FileWriter("output.txt");
		HashMap<String, Integer> notExplicitlyAdded = new HashMap<String, Integer>();
		notExplicitlyAdded.put("TCPIP", 1);
		notExplicitlyAdded.put("HTML", 1);
		Dependencies junit = new Dependencies();
		junit.removeNotExplicitlyAdded(stack, waitingToBeRemoved, count, prev, notExplicitlyAdded, myWriter);
		assertEquals(notExplicitlyAdded.size(), 0);
	}
	@Test
	public void test_install_dependencies() throws IOException {
		Dependencies junit = new Dependencies();
		Queue<String> checkInstalled = new LinkedList<>();
		checkInstalled.add("TCPIP");
		checkInstalled.add("TELNET");
		HashMap<String, Integer> count = new HashMap<String, Integer>();
		count.put("TELNET", 1);
		count.put("TCPIP", 1);
		count.put("NETCARD", 3);
		HashMap<String, ArrayList> stackPlacement = new HashMap<String, ArrayList>();
		ArrayList<Integer> arrList =   new ArrayList<Integer>();
		arrList.add(3);
		stackPlacement.put("BROWSER", arrList);
		ArrayList<Integer> arrList2 =   new ArrayList<Integer>();
		stackPlacement.put("TELNET", arrList2);
		ArrayList<Integer> arrList3 =   new ArrayList<Integer>();
		arrList3.add(0);
		arrList3.add(1);
		arrList3.add(2);
		arrList3.add(3);
		stackPlacement.put("TCPIP", arrList3);
		ArrayList<Integer> arrList4 =   new ArrayList<Integer>();
		arrList4.add(2);
		stackPlacement.put("HTML", arrList);
		stackPlacement.put("DNS", arrList4);
		ArrayList<Stack> trackDependencies = new ArrayList<Stack>();
		Stack stack = new Stack();
		trackDependencies.add(stack);
		Stack stack1 = new Stack();
		stack1.add("TCPIP");
		trackDependencies.add(stack1);
		Stack stack2 = new Stack();
		stack2.add("TCPIP");
		stack2.add("DNS");
		trackDependencies.add(stack2);
		Stack stack3 = new Stack();
		stack3.add("BROWSER");
		stack3.add("TCPIP");
		stack3.add("HTML");
		trackDependencies.add(stack3);
		ArrayList<Stack> trackInstalled = new ArrayList<Stack>();
		Stack stack4 = new Stack();
		stack4.add("NETCARD");
		stack4.add("TCPIP");
		stack4.add("TELNET");
		trackInstalled.add(stack4);
		Stack stack5 = new Stack();
		stack5.add("NETCARD");
		trackInstalled.add(stack5);
		trackInstalled.add(stack5);
		Stack stack6 = new Stack();
		trackInstalled.add(stack6);
		junit.installDependents(checkInstalled, stackPlacement, count, trackDependencies, trackInstalled);
		assertEquals(checkInstalled.size(), 0);
	}
	@Test
	public void test_count_hash_map() throws IOException {
		HashMap<String, Integer> count = new HashMap<String, Integer>();
		Dependencies junit = new Dependencies();
		FileWriter myWriter = new FileWriter("output.txt");
		junit.addToCountMap(count, "TELNET", myWriter);
		assertEquals(count.size(), 1);
		int one = count.get("TELNET");
		assertEquals(one, 1);
		junit.addToCountMap(count, "TELNET", myWriter);
		int two = count.get("TELNET");
		assertEquals(two, 2);
	}
	@Test
	public void test_install() throws IOException {
		ArrayList<Stack> trackDependencies = new ArrayList<Stack>();
		Stack stack1 = new Stack();
		stack1.add("TELNET");
		stack1.add("TCPIP");
		stack1.add("NETCARD");
		trackDependencies.add(stack1);
		Stack stack2 = new Stack();
		stack2.add("TCPIP");
		stack2.add("NETCARD");
		trackDependencies.add(stack2);
		Stack stack4 = new Stack();
		stack4.add("DNS");
		stack4.add("TCPIP");
		stack4.add("NETCARD");
		trackDependencies.add(stack4);
		Stack stack3 = new Stack();
		stack3.add("BROWSER");
		stack3.add("TCPIP");
		stack3.add("HTML");
		trackDependencies.add(stack3);
		ArrayList<Stack> trackInstalled = new ArrayList<Stack>();
		Stack stack5 = new Stack();
		trackInstalled.add(stack5);
		trackInstalled.add(stack5);
		trackInstalled.add(stack5);
		trackInstalled.add(stack5);
	
		HashMap<String, ArrayList> stackPlacement = new HashMap<String, ArrayList>();
		ArrayList list = new ArrayList();
		list.add(3);
		ArrayList list1 = new ArrayList();
		list1.add(0);
		stackPlacement.put("BROWSER", list);
		stackPlacement.put("TELNET", list1);
		ArrayList list2 = new ArrayList();
		list2.add(0);
		list2.add(1);
		list2.add(2);
		list2.add(3);
		stackPlacement.put("TCPIP", list2);
		ArrayList list3 = new ArrayList();
		list3.add(2);
		stackPlacement.put("DNS", list3);
		stackPlacement.put("HTML", list);
		ArrayList list4 = new ArrayList();
		list4.add(0);
		list4.add(1);
		list4.add(2);
		stackPlacement.put("NETCARD", list4);
		HashMap<String, Integer> count = new HashMap<String, Integer>();
		FileWriter myWriter = new FileWriter("output.txt");
		HashMap<String, Integer> notExplicitlyAdded = new HashMap<String, Integer>();
		Dependencies junit = new Dependencies();
		junit.install("NETCARD", trackDependencies, trackInstalled, stackPlacement, count, notExplicitlyAdded, myWriter);
		int three = count.get("NETCARD");
		assertEquals(three, 3);
		Stack stackTrack =trackDependencies.get(0);
		assertEquals(stackTrack.size(), 2);
	}
}
