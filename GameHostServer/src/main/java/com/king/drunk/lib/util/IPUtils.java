package com.king.drunk.lib.util;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Collections;
import java.util.List;

/**
 * Created by king on 2/19/14.
 */
public class IPUtils {
	public static String getIPAddress(boolean useIPv4) {
		try {
			List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
				for (InetAddress addr : addrs) {
					if (!addr.isLoopbackAddress()) {
						String sAddr = addr.getHostAddress().toUpperCase();
						boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
						if (useIPv4) {
							if (isIPv4)
								return sAddr;
						} else {
							if (!isIPv4) {
								int delim = sAddr.indexOf('%'); // drop ip6 port suffix
								return delim<0 ? sAddr : sAddr.substring(0, delim);
							}
						}
					}
				}
			}
		} catch (Exception ex) { } // for now eat exceptions
		return "";
	}

	/**
	 * Method to get the ip address of a server on the network.
	 * @return
	 */
	public static String scanForGameServer(int port) {
		String subnet = getSubnet();
		for(int i = 0; i < 256; i++) {
			Socket client = null;
			try {
				client = new Socket();
				InetSocketAddress address = new InetSocketAddress(subnet + i,  port);
				client.connect(address, 20);
				if(client.isConnected()) {
					return subnet + i;
				}
			} catch (IOException e) {
				//Ignore
			} finally {
				if(client != null) {
					try {
						client.close();
					} catch (IOException e) {
						//ignore
					}
				}
			}
		}
		return null;
	}

	/**
	 * Helper method to get subnet for scanning
	 * @return
	 */
	public static String getSubnet() {
		String localIp = IPUtils.getIPAddress(true);
		String subnet = localIp.substring(0, localIp.lastIndexOf(".") + 1);
		return subnet;
	}
}
