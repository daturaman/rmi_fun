/*
 * <copyright>
 *
 * Copyright (c) 2010-2017 Gresham Technologies plc. All rights reserved.
 *
 * </copyright>
 */
package michael.rmifun;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.UUID;

/**
 * @author mcarter
 */
public interface RmiServer extends Remote {

	UUID getId() throws RemoteException;

	String getMessage() throws RemoteException;

	String get(String key) throws RemoteException;

	void put(String key, String value) throws RemoteException;

	void remove(String key) throws RemoteException;

	void start() throws IOException, AlreadyBoundException;

	boolean started() throws RemoteException;
}
