package com.king.drunk.lib.net;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by king on 2/19/14.
 */
public class NetAction {

	public String connect(BufferedReader input) throws IOException {
		String connectionId = input.readLine();
		return connectionId;
	}

	public String write(BufferedReader input) throws IOException {
		String connectionId = input.readLine();
		return connectionId;
	}
}
