package net.fxft.webgateway.po;

import java.util.Date;
import net.fxft.common.jdbc.DbTable;
import net.fxft.common.jdbc.DbId;
import net.fxft.common.jdbc.DbColumn;

@DbTable(value="OperationLog", camelToUnderline=false)
public class OperationLog implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    public static final String F_logId = "logId";
    public static final String F_createDate = "createDate";
    public static final String F_deleted = "deleted";
    public static final String F_owner = "owner";
    public static final String F_remark = "remark";
    public static final String F_detail = "detail";
    public static final String F_ip = "ip";
    public static final String F_url = "url";
    public static final String F_userId = "userId";
    public static final String F_userName = "userName";
    public static final String F_companyId = "companyId";
    public static final String F_channel = "channel";
    public static final String F_operationType = "operationType";
    public static final String F_plateNo = "plateNo";

    @DbId
    private Integer logId;
    private Date createDate;
    private boolean deleted;
    private String owner;
    private String remark;
    private String detail;
    private String ip;
    private String url;
    private Integer userId;
    private String userName;
    private Long companyId;
    private Integer channel;
    private String operationType;
    private String plateNo;
    public OperationLog()
    {
        createDate = new Date();
    }
    public Integer getLogId(){
        return this.logId;
    }

    public void setLogId(Integer logId){
        this.logId=logId;
    }

    public Date getCreateDate(){
        return this.createDate;
    }

    public void setCreateDate(Date createDate){
        this.createDate=createDate;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getOwner(){
        return this.owner;
    }

    public void setOwner(String owner){
        this.owner=owner;
    }

    public String getRemark(){
        return this.remark;
    }

    public void setRemark(String remark){
        this.remark=remark;
    }

    public String getDetail(){
        return this.detail;
    }

    public void setDetail(String detail){
        this.detail=detail;
    }

    public String getIp(){
        return this.ip;
    }

    public void setIp(String ip){
        this.ip=ip;
    }

    public String getUrl(){
        return this.url;
    }

    public void setUrl(String url){
        this.url=url;
    }

    public Integer getUserId(){
        return this.userId;
    }

    public void setUserId(Integer userId){
        this.userId=userId;
    }

    public String getUserName(){
        return this.userName;
    }

    public void setUserName(String userName){
        this.userName=userName;
    }

    public Long getCompanyId(){
        return this.companyId;
    }

    public void setCompanyId(Long companyId){
        this.companyId=companyId;
    }

    public Integer getChannel(){
        return this.channel;
    }

    public void setChannel(Integer channel){
        this.channel=channel;
    }

    public String getOperationType(){
        return this.operationType;
    }

    public void setOperationType(String operationType){
        this.operationType=operationType;
    }

    public String getPlateNo(){
        return this.plateNo;
    }

    public void setPlateNo(String plateNo){
        this.plateNo=plateNo;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("OperationLog").append(" [");
        sb.append("logId=").append(logId);
        sb.append(", createDate=").append(createDate);
        sb.append(", deleted=").append(deleted);
        sb.append(", owner=").append(owner);
        sb.append(", remark=").append(remark);
        sb.append(", detail=").append(detail);
        sb.append(", ip=").append(ip);
        sb.append(", url=").append(url);
        sb.append(", userId=").append(userId);
        sb.append(", userName=").append(userName);
        sb.append(", companyId=").append(companyId);
        sb.append(", channel=").append(channel);
        sb.append(", operationType=").append(operationType);
        sb.append(", plateNo=").append(plateNo);
        sb.append("]");
        return sb.toString();
    }
}
