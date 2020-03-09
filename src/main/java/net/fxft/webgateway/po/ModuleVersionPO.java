package net.fxft.webgateway.po;

import net.fxft.common.jdbc.DbId;
import net.fxft.common.jdbc.DbTable;

import java.util.Date;

@DbTable(value="module_version")
public class ModuleVersionPO implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    public static final String F_id = "id";
    public static final String F_ip = "ip";
    public static final String F_workDir = "workDir";
    public static final String F_projectName = "projectName";
    public static final String F_versionTag = "versionTag";
    public static final String F_gitBranch = "gitBranch";
    public static final String F_gitCommitId = "gitCommitId";
    public static final String F_buildTime = "buildTime";
    public static final String F_moduleName = "moduleName";
    public static final String F_jarPath = "jarPath";
    public static final String F_updateTime = "updateTime";

    /**  主键  */
    @DbId
    private Integer id;
    /**  服务器IP  */
    private String ip;
    /**  工作目录  */
    private String workDir;
    /**  工程名称  */
    private String projectName;
    private String versionTag;
    private String gitBranch;
    private String gitCommitId;
    private Date buildTime;
    private String moduleName;
    private String jarPath;
    private Date updateTime;

    public Integer getId(){
        return this.id;
    }

    public void setId(Integer id){
        this.id=id;
    }

    public String getIp(){
        return this.ip;
    }

    public void setIp(String ip){
        this.ip=ip;
    }

    public String getWorkDir(){
        return this.workDir;
    }

    public void setWorkDir(String workDir){
        this.workDir=workDir;
    }

    public String getProjectName(){
        return this.projectName;
    }

    public void setProjectName(String projectName){
        this.projectName=projectName;
    }

    public String getVersionTag(){
        return this.versionTag;
    }

    public void setVersionTag(String versionTag){
        this.versionTag=versionTag;
    }

    public String getGitBranch(){
        return this.gitBranch;
    }

    public void setGitBranch(String gitBranch){
        this.gitBranch=gitBranch;
    }

    public String getGitCommitId(){
        return this.gitCommitId;
    }

    public void setGitCommitId(String gitCommitId){
        this.gitCommitId=gitCommitId;
    }

    public Date getBuildTime(){
        return this.buildTime;
    }

    public void setBuildTime(Date buildTime){
        this.buildTime=buildTime;
    }

    public String getModuleName(){
        return this.moduleName;
    }

    public void setModuleName(String moduleName){
        this.moduleName=moduleName;
    }

    public String getJarPath(){
        return this.jarPath;
    }

    public void setJarPath(String jarPath){
        this.jarPath=jarPath;
    }

    public Date getUpdateTime(){
        return this.updateTime;
    }

    public void setUpdateTime(Date updateTime){
        this.updateTime=updateTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ModuleVersionPO").append(" [");
        sb.append("id=").append(id);
        sb.append(", ip=").append(ip);
        sb.append(", workDir=").append(workDir);
        sb.append(", projectName=").append(projectName);
        sb.append(", versionTag=").append(versionTag);
        sb.append(", gitBranch=").append(gitBranch);
        sb.append(", gitCommitId=").append(gitCommitId);
        sb.append(", buildTime=").append(buildTime);
        sb.append(", moduleName=").append(moduleName);
        sb.append(", jarPath=").append(jarPath);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}
