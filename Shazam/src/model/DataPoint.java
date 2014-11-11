package model;

public class DataPoint {

	private int time;
	private String songId;

	public DataPoint(String songId, int time) {
		this.songId = songId;
		this.time = time;
	}

	public int getTime() {
		return time;
	}

	public String getSongId() {
		return songId;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "["+songId+"|"+time+"]";
	}
}
