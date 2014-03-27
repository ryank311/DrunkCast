package com.king.drunk.model.entity;

/**
 * Created by king on 2/18/14.
 */
public class Player {

	private String name;
	private String uuid;
	private int color;

	public Player(String name, String uuid, int color) {
		this.name = name;
		this.uuid = uuid;
		this.color = color;
	}

	public Player(String fromString) {
		name = fromString.substring(6, fromString.indexOf(",uuid:"));
		uuid = fromString.substring(fromString.indexOf(",uuid:") + 6, fromString.indexOf(",color:"));
		color = Integer.valueOf(fromString.substring(fromString.indexOf(",color:") + 7, fromString.length() - 2));
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{name:");
		builder.append(name);
		builder.append(",uuid:");
		builder.append(uuid);
		builder.append(",color:");
		builder.append(color);
		builder.append("}");
		return builder.toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}
}
