package net.fxft.webgateway.po;

import net.fxft.common.jdbc.DbId;
import net.fxft.common.jdbc.DbTable;

@DbTable(value="userinfo", camelToUnderline=false)
public class UserInfo implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public static String STATE_SUSPEND = "suspend"; //暂停该用户
	public static String STATE_NORMAL = "normal"; //可用状态

	public static final String F_userId = "userId";
	public static final String F_deleted = "deleted";
	public static final String F_owner = "owner";
	public static final String F_loginName = "loginName";
	public static final String F_name = "name";
	public static final String F_userState = "userState";
	public static final String F_userType = "userType";

	@DbId
	private Integer userId;
	private Integer deleted;
	private String owner;
	private String loginName;
	private String password;
	private String name;
	private String userState;
	private String userType;
	private Long createStaff;

	public Integer getUserId(){
		return this.userId;
	}

	public void setUserId(Integer userId){
		this.userId=userId;
	}

	public Integer getDeleted(){
		return this.deleted;
	}

	public void setDeleted(Integer deleted){
		this.deleted=deleted;
	}

	public String getOwner(){
		return this.owner;
	}

	public void setOwner(String owner){
		this.owner=owner;
	}

	public String getLoginName(){
		return this.loginName;
	}

	public void setLoginName(String loginName){
		this.loginName=loginName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName(){
		return this.name;
	}

	public void setName(String name){
		this.name=name;
	}

	public String getUserState(){
		return this.userState;
	}

	public void setUserState(String userState){
		this.userState=userState;
	}

	public String getUserType(){
		return this.userType;
	}

	public void setUserType(String userType){
		this.userType=userType;
	}

	public Long getCreateStaff() {
		return createStaff;
	}

	public void setCreateStaff(Long createStaff) {
		this.createStaff = createStaff;
	}

	public boolean isSuperAdmin() {
		return "admin".equals(getUserType());
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("UserInfo").append(" [");
		sb.append("userId=").append(userId);
		sb.append(", deleted=").append(deleted);
		sb.append(", owner=").append(owner);
		sb.append(", loginName=").append(loginName);
		sb.append(", name=").append(name);
		sb.append(", userState=").append(userState);
		sb.append(", userType=").append(userType);
		sb.append("]");
		return sb.toString();
	}
}
