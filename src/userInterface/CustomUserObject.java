package userInterface;

public class CustomUserObject {
	private String Id = null;
	private String Title = null;

	public CustomUserObject(String id, String title) {
		this.Id = id;
		this.Title = title;
	}

	public CustomUserObject() {

	}

	public String getId() {
		return this.Id;
	}

	public String getTitle() {
		return this.Title;
	}

	public void setId(String id) {
		this.Id = id;
	}

	public void setTitle(String title) {
		this.Title = title;
	}

	public String toString() {
		return this.Title;
	}
}
