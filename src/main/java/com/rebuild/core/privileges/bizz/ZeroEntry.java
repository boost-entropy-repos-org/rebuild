/*
Copyright (c) REBUILD <https://getrebuild.com/> and/or its owners. All rights reserved.

rebuild is dual-licensed under commercial and open source licenses (GPLv3).
See LICENSE and COMMERCIAL in the project root for license information.
*/

package com.rebuild.core.privileges.bizz;

/**
 * 扩权限项
 *
 * @author devezhao zhaofang123@gmail.com
 * @since 2019/04/13
 */
public enum ZeroEntry {

    /**
     * 允许登录
     */
    AllowLogin(true),
    /**
     * 允许批量修改
     */
    AllowBatchUpdate(false),
    /**
     * 允许导入
     */
    AllowDataImport(false),
    /**
     * 允许导出
     */
    AllowDataExport(false),
    /**
     * 允许自定义导航菜单
     */
    AllowCustomNav(true),
    /**
     * 允许自定义列表显示列
     */
    AllowCustomDataList(true),
    /**
     * 允许自定义图表
     */
    AllowCustomChart(true),

    // NOTE 对于新增权限都应该设置默认值为 true 以保持老版本兼容

    ;

    private boolean defaultVal;

    ZeroEntry(boolean defaultVal) {
        this.defaultVal = defaultVal;
    }

    /**
     * 默认值
     *
     * @return
     */
    public boolean getDefaultVal() {
        return defaultVal;
    }
}
