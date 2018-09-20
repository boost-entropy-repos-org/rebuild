package cn.devezhao.rebuild.server.metadata;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;

import cn.devezhao.persist4j.Entity;
import cn.devezhao.persist4j.Record;
import cn.devezhao.persist4j.engine.ID;
import cn.devezhao.persist4j.engine.StandardRecord;
import cn.devezhao.persist4j.record.FieldValueException;
import cn.devezhao.persist4j.record.JsonRecordCreator;
import cn.devezhao.persist4j.record.RecordCreator;

/**
 * @author Zhao Fangfang
 * @version $Id: EntityHelper.java 3407 2017-05-05 10:09:40Z devezhao $
 * @since 1.0, 2013-6-26
 */
public class EntityHelper {
	
	/**
	 * 实体是否具有权限字段
	 * 
	 * @param entity
	 * @return
	 */
	public static boolean hasPrivilegesField(Entity entity) {
		if (entity.containsField(owningUser) && entity.containsField(owningDept)) {
			return true;
		}
		return false;
	}
	
	/**
	 * @param data
	 * @param user
	 * @return
	 */
	public static Record parse(JSONObject data, ID user) {
		JSONObject metadata = data.getJSONObject(JsonRecordCreator.META_FIELD);
		if (metadata == null) {
			throw new FieldValueException("无效实体数据格式(1): " + data.toJSONString());
		}
		String entityName = metadata.getString("entity");
		if (StringUtils.isBlank(entityName)) {
			throw new FieldValueException("无效实体数据格式(2): " + data.toJSONString());
		}
		
		RecordCreator creator = new ExtRecordCreator(MetadataHelper.getEntity(entityName), data, user);
		Record record = creator.create();
		ExtRecordCreator.bindCommonsFieldsValue(record, record.getPrimary() == null);
		return record;
	}

	/**
	 * @param recordId
	 * @param user
	 * @return
	 */
	public static Record forUpdate(ID recordId, ID user) {
		Entity entity = MetadataHelper.getEntity(recordId.getEntityCode());
		Record record = new StandardRecord(entity, user);
		record.setID(entity.getPrimaryField().getName(), recordId);
		ExtRecordCreator.bindCommonsFieldsValue(record, false);
		return record;
	}
	
	/**
	 * @param entityCode
	 * @param user
	 * @return
	 */
	public static Record forNew(int entityCode, ID user) {
		Entity entity = MetadataHelper.getEntity(entityCode);
		Record record = new StandardRecord(entity, user);
		ExtRecordCreator.bindCommonsFieldsValue(record, true);
		return record;
	}
	
	// 公共字段
	
	public static final String createdOn = "createdOn";
	public static final String createdBy = "createdBy";
	public static final String modifiedOn = "modifiedOn";
	public static final String modifiedBy = "modifiedBy";
	public static final String owningUser = "owningUser";
	public static final String owningDept = "owningDept";
	
	// 实体代码

	public static final int User = 1;
	public static final int Department = 2;
	public static final int Role = 3;
	public static final int RolePrivileges = 4;
	public static final int RoleMember = 5;
	
	public static final int MetaEntity = 10;
	public static final int MetaField = 11;
	public static final int PickList = 12;
	public static final int LayoutConfig = 13;
	public static final int FilterConfig = 14;
	
}
