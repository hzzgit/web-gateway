package net.fxft.webgateway.po;

import net.fxft.common.jdbc.DbColumn;
import net.fxft.common.jdbc.DbId;
import net.fxft.common.jdbc.DbTable;

import java.util.Date;

/**
 * 车组/部门类  
 * @author Administrator
 *
 */

@DbTable(value = "department", camelToUnderline = false)
public class Department  {

	@DbColumn(columnName = "depId")
	@DbId
	private long entityId;

	public long getEntityId() {
		return this.entityId;
	}

	public void setEntityId(long id) {
		this.entityId = id;
	}

}
