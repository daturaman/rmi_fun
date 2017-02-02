package michael.rmifun;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author mcarter
 */
public class RmiServerImpl extends UnicastRemoteObject implements RmiServer {

	private Map<String, String> map = new ConcurrentHashMap<>();
	private boolean started = false;
	private static final String MESSAGE = "Oh shit, I'm, using RMI.";
	private UUID id = UUID.randomUUID();

	public RmiServerImpl() throws RemoteException {
		super(0); // required to avoid the 'rmic' step, see below
	}

	public UUID getId() {
		return id;
	}

	@Override
	public String getMessage() {
		return MESSAGE + ":" + id;
	}

	@Override
	public String get(String key) throws RemoteException {
		return map.get(key);
	}

	@Override
	public void put(String key, String value) throws RemoteException {
		map.put(key, value);
		try {
			for (String name : Naming.list("//localhost/RmiServer")) {
				RmiServer obj = (RmiServer) Naming.lookup(name);
				if(obj.get(key) == null) {
					obj.put(key, value);
				}
			}
		} catch (MalformedURLException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void remove(String key) throws RemoteException {
		map.remove(key);
		try {
			for (String name : Naming.list("//localhost/RmiServer")) {
				RmiServer obj = (RmiServer) Naming.lookup(name);
				if(obj.get(key) != null) {
					obj.remove(key);
				}
			}
		} catch (MalformedURLException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean started() throws RemoteException {
		return started;
	}

	@Override
	public void start() throws IOException, AlreadyBoundException {
		// Start up procedure - look on network for other
		System.out.println("RMI server started");
		try { // special exception handler for registry creation
			LocateRegistry.createRegistry(1099);
			System.out.println("java RMI registry created.");
		} catch (RemoteException e) {
			// do nothing, error means registry already exists
			System.out.println("java RMI registry already exists.");
		}
		// Instantiate RmiServer
		RmiServerImpl obj = new RmiServerImpl();
		// Bind this object instance to the name "RmiServer"
		Naming.bind(String.format("//localhost/RmiServer/%s", obj.getId()), obj);
		System.out.println("PeerServer bound in registry");

		started = true;

		ActionProcessor<String, String> actionProcessor = new ActionProcessor<>(null);
		try (BufferedReader bufIn = new BufferedReader(new InputStreamReader(System.in))) {
			while (true) {
				System.out.println("Ready for input:");
				actionProcessor.process(bufIn.readLine());
			}
		}
	}

	public static void main(String args[]) throws Exception {
		new RmiServerImpl().start();
	}
}
