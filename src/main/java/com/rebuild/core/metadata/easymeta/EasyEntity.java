/*
Copyright (c) REBUILD <https://getrebuild.com/> and/or its owners. All rights reserved.

rebuild is dual-licensed under commercial and open source licenses (GPLv3).
See LICENSE and COMMERCIAL in the project root for license information.
*/

package com.rebuild.core.metadata.easymeta;

import cn.devezhao.persist4j.Entity;
import com.alibaba.fastjson.JSON;
import com.rebuild.utils.JSONUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 实体封装
 *
 * @author devezhao
 * @since 2020/11/17
 */
public class EasyEntity extends BaseEasyMeta<Entity> {
    private static final long serialVersionUID = -5487765209824858442L;

    protected EasyEntity(Entity entity) {
        super(entity);
    }

    /**
     * 实体图标
     *
     * @return
     */
    public String getIcon() {
        return StringUtils.defaultIfBlank(getExtraAttr("icon"), "texture");
    }

    /**
     * 具有和业务实体一样的特性（除权限以外（因为无权限字段））
     *
     * @return
     */
    public boolean isPlainEntity() {
        return getExtraAttrs().getBooleanValue("plainEntity");
    }

    @Override
    public JSON toJSON() {
        return JSONUtils.toJSONObject(
                new String[] { "entity", "entityLabel", "icon" },
                new String[] { getName(), getLabel(), getIcon() });
    }
}
