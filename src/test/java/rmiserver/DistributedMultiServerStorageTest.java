package rmiserver;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Test;

import michael.rmifun.RmiServer;
import michael.rmifun.RmiServerImpl;

/**
 * @author mcarter
 */
public class DistributedMultiServerStorageTest {

	private static final int SERVER_COUNT = 3;
	private RmiServer [] testServers;

	private final List<Map.Entry<String, String>> testEntries;

	{
		testEntries = new ArrayList<>();
		testEntries.add(new AbstractMap.SimpleEntry<>("USA", "Washington DC"));
		testEntries.add(new AbstractMap.SimpleEntry<>("France", "Paris"));
		testEntries.add(new AbstractMap.SimpleEntry<>("China", "Beijing"));
		testEntries.add(new AbstractMap.SimpleEntry<>("Argentina", "Buenos Aires"));
	}

	@Before
	public void setUp() throws IOException, AlreadyBoundException, InterruptedException, NotBoundException {
		RmiServer [] rmiServers = new RmiServer[SERVER_COUNT];
		ExecutorService executorService = Executors.newFixedThreadPool(SERVER_COUNT);

		for(int i = 0; i < SERVER_COUNT; i++){
			rmiServers[i] = new RmiServerImpl();
			final int j = i;
			executorService.execute(() -> {
				try {
					rmiServers[j].start();
				} catch (IOException | AlreadyBoundException e) {
					e.printStackTrace();
				}
			});
		}

		while(!rmiServers[0].started() && !rmiServers[1].started() && !rmiServers[2].started()){
			Thread.sleep(100);
		}

		//Get the "remote" servers we just started from the registry
		testServers = new RmiServer[SERVER_COUNT];
		String [] names = Naming.list("//localhost/RmiServer");
		for (int i = 0; i < names.length; i++) {
			testServers[i] = (RmiServer) Naming.lookup(names[i]);
			for (Map.Entry<String, String> entry : testEntries){
				testServers[i].put(entry.getKey(), entry.getValue());
			}
		}
	}

	@Test
	public void createdEntryIsRetrievableFromAllNodes() throws RemoteException {
		testServers[0].put("UK", "London");
		assertThat(testServers[0].get("UK"), is(equalTo("London")));
		assertThat(testServers[1].get("UK"), is(equalTo("London")));
		assertThat(testServers[2].get("UK"), is(equalTo("London")));
	}

	@Test
	public void deletingEntryFromOneNodeIsProgagatedToAllOtherNodes() throws RemoteException {
		testServers[0].remove("USA");
		assertThat(testServers[0].get("USA"), is(nullValue()));
		assertThat(testServers[1].get("USA"), is(nullValue()));
		assertThat(testServers[2].get("USA"), is(nullValue()));
	}
}

