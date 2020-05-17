package elasticsearch.entity;

import java.io.Serializable;
import java.util.List;

public class TUser implements Serializable{
	
    private Integer id;

    private String userName;

    private String realName;

    private Byte sex;

    private String mobile;

    private String email;

    private String note;

/*    
	public TUser(Integer id, String userName) {
		super();
		this.id = id;
		this.userName = userName;
	}
	*/
    

    
  
	@Override
	public String toString() {

		return "TUser [id=" + id + ", userName=" + userName + ", realName="
				+ realName + ", sex=" + sex + ", mobile=" + mobile + ", email="
				+ email + ", note=" + note + ", positionId=" + "]";
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public Byte getSex() {
		return sex;
	}

	public void setSex(Byte sex) {
		this.sex = sex;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public static void main(String[] args) {
		System.out.println(1<<2);
	}

	
	
    
}