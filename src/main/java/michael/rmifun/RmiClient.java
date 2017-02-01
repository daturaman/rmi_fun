/*
 * <copyright>
 *
 * Copyright (c) 2010-2017 Gresham Technologies plc. All rights reserved.
 *
 * </copyright>
 */
package michael.rmifun;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * @author mcarter
 */
public class RmiClient {
	public static void main(String [] args) throws RemoteException, NotBoundException, MalformedURLException {
		for (String name : Naming.list("//localhost/RmiServer")) {
			RmiServer obj = (RmiServer) Naming.lookup(name);
			System.out.println(obj.getMessage());
		}
	}
}
