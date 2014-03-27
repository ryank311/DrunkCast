package com.king.drunk.cast;

import com.king.drunk.model.entity.Player;

/**
 * Created by king on 3/5/14.
 */
public class CastCommand {

	private CastCommandType commandType;
	private Player player;

	public void playerCommand(CastCommandType commandType, Player player) {
		this.player = player;
		this.commandType = commandType;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
			builder.append(commandType);
			builder.append(":");
				if(player != null) {
					builder.append(player);
				}
		builder.append("}");
		return builder.toString();
	}

}
