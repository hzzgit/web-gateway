package net.fxft.webgateway.po;

import net.fxft.common.jdbc.DbTable;
import net.fxft.common.jdbc.DbId;

@DbTable(value="SystemConfig", camelToUnderline=false)
public class SystemConfigPO implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    public static final String F_id = "id";
    public static final String F_deleted = "deleted";
    public static final String F_checkValidateCode = "checkValidateCode";

    @DbId
    private Integer id;
    private Integer deleted;
    private String checkValidateCode;

    public Integer getId(){
        return this.id;
    }

    public void setId(Integer id){
        this.id=id;
    }

    public Integer getDeleted(){
        return this.deleted;
    }

    public void setDeleted(Integer deleted){
        this.deleted=deleted;
    }

    public String getCheckValidateCode(){
        return this.checkValidateCode;
    }

    public void setCheckValidateCode(String checkValidateCode){
        this.checkValidateCode=checkValidateCode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SystemConfigPO").append(" [");
        sb.append("id=").append(id);
        sb.append(", deleted=").append(deleted);
        sb.append(", checkValidateCode=").append(checkValidateCode);
        sb.append("]");
        return sb.toString();
    }
}
