/*
Copyright 2018 DEVEZHAO(zhaofang123@gmail.com)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package cn.devezhao.rebuild.web.commons;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;

import cn.devezhao.commons.CalendarUtils;
import cn.devezhao.commons.web.ServletUtils;
import cn.devezhao.persist4j.Record;
import cn.devezhao.persist4j.engine.ID;
import cn.devezhao.rebuild.server.metadata.MetadataHelper;
import cn.devezhao.rebuild.server.service.entitymanage.EasyMeta;
import cn.devezhao.rebuild.utils.AppUtils;

/**
 * @author zhaofang123@gmail.com
 * @since 05/21/2017
 */
public abstract class BaseControll {
	
	public static final int CODE_OK = 0;
	public static final int CODE_FAIL = 1000;
	public static final int CODE_ERROR = 2000;
	
	protected static Log LOG = LogFactory.getLog(BaseControll.class);
	
	/**
	 * @param resp
	 */
	protected void writeSuccess(HttpServletResponse resp) {
		writeSuccess(resp, ObjectUtils.NULL);
	}

	/**
	 * @param resp
	 * @param record
	 */
	protected void writeSuccess(HttpServletResponse resp, Record record) {
		if (record == null) {
			writeFailure(resp, "无法找到记录");
			return;
		}
		
		Map<String, Object> data = new HashMap<String, Object>();
		for (Iterator<String> iter = record.getAvailableFieldIterator(); iter.hasNext(); ) {
			String f = iter.next();
			Object v = record.getObjectValue(f);
			if (v instanceof Date) {
				v = CalendarUtils.getUTCDateTimeFormat().format(v);
			} else if (v instanceof ID) {
				v = v.toString();
			}
			data.put(f, v);
		}
		writeSuccess(resp, data);
	}
	
	/**
	 * @param resp
	 * @param data
	 */
	protected void writeSuccess(HttpServletResponse resp, Object data) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("error_code", CODE_OK);
		map.put("error_msg", "调用成功");
		if (data != null && data != ObjectUtils.NULL) {
			// ID 类型不会 toString ???
			if (Map.class.isAssignableFrom(data.getClass())) {
				@SuppressWarnings("unchecked")
				Map<Object, Object> dataMap = (Map<Object, Object>) data;
				for (Object key : dataMap.keySet()) {
					Object value = dataMap.get(key);
					if (value != null && ID.class.isAssignableFrom(value.getClass())) {
						dataMap.put(key, value.toString());
					} else if (value != null && Date.class.isAssignableFrom(value.getClass())) {
						dataMap.put(key, CalendarUtils.getUTCDateTimeFormat().format(value));
					}
				}
			} else if (Object[][].class.isAssignableFrom(data.getClass())) {
				Object[][] array = (Object[][]) data;
				for (Object[] o : array) {
					for (int i = 0; i < o.length; i++) {
						Object value = o[i];
						if (value != null && ID.class.isAssignableFrom(value.getClass())) {
							o[i] = o[i].toString();
						} else if (value != null && Date.class.isAssignableFrom(value.getClass())) {
							o[i] = CalendarUtils.getUTCDateTimeFormat().format(o[i]);
						}
					}
				}
			} else if (Object[].class.isAssignableFrom(data.getClass())) {
				Object[] o = (Object[]) data;
				for (int i = 0; i < o.length; i++) {
					Object value = o[i];
					if (value != null && ID.class.isAssignableFrom(value.getClass())) {
						o[i] = o[i].toString();
					} else if (value != null && Date.class.isAssignableFrom(value.getClass())) {
						o[i] = CalendarUtils.getUTCDateTimeFormat().format(o[i]);
					}
				}
			}
			map.put("data", data);
		}
		writeJSON(resp, map);
	}
	
	/**
	 * @param resp
	 */
	protected void writeFailure(HttpServletResponse resp) {
		writeFailure(resp, null);
	}
	
	/**
	 * @param resp
	 * @param message
	 */
	protected void writeFailure(HttpServletResponse resp, String message) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("error_code", CODE_FAIL);
		map.put("error_msg", message == null ? "无效请求" : message);
		writeJSON(resp, map);
	}
	
	/**
	 * @param resp
	 * @param json
	 */
	protected void writeJSON(HttpServletResponse resp, Object json) {
		if (json == null) {
			throw new IllegalArgumentException();
		}
		
		String aJSONString = null;
		if (json instanceof String) {
			aJSONString = (String) json;
		} else {
			aJSONString = JSON.toJSONString(json);
		}
		ServletUtils.writeJson(resp, aJSONString);
	}
	
	/**
	 * @param req
	 * @return
	 */
	protected ID getRequestUser(HttpServletRequest req) {
		ID fansId = AppUtils.getRequestUser(req);
		if (fansId == null) {
			throw new BadRequestException("无效请求用户");
		}
		return fansId;
	}
	
	/**
	 * @param req
	 * @param name
	 * @return
	 */
	protected ID getIdParameter(HttpServletRequest req, String name) {
		String v = req.getParameter(name);
		return ID.isId(v) ? ID.valueOf(v) : null;
	}
	
	/**
	 * @param req
	 * @param name
	 * @return
	 */
	protected ID getIdParameterNotNull(HttpServletRequest req, String name) {
		String v = req.getParameter(name);
		if (ID.isId(v)) {
			return ID.valueOf(v);
		}
		throw new BadRequestException("无效ID参数 [" + name + "=" + v + "]");
	}
	
	/**
	 * @param req
	 * @param name
	 * @return
	 */
	protected String getParameter(HttpServletRequest req, String name) {
		return req.getParameter(name);
	}
	
	/**
	 * @param req
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	protected String getParameter(HttpServletRequest req, String name, String defaultValue) {
		return StringUtils.defaultIfBlank(getParameter(req, name), defaultValue);
	}
	
	/**
	 * @param req
	 * @param name
	 * @return
	 */
	protected String getParameterNotNull(HttpServletRequest req, String name) {
		String v = req.getParameter(name);
		if (StringUtils.isEmpty(v)) {
			throw new BadRequestException("无效参数 [" + name + "=" + v + "]");
		}
		return v;
	}
	
	/**
	 * @param req
	 * @param name
	 * @return
	 */
	protected Integer getIntParameter(HttpServletRequest req, String name) {
		String v = req.getParameter(name);
		if (v == null) {
			return null;
		}
		return NumberUtils.toInt(v);
	}
	
	/**
	 * @param req
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	protected Integer getIntParameter(HttpServletRequest req, String name, int defaultValue) {
		Integer v = getIntParameter(req, name);
		return v == null ? (defaultValue) : v;
	}
	
	/**
	 * @param page
	 * @return
	 */
	protected ModelAndView createModelAndView(String page) {
		return createModelAndView(page, null);
	}
	
	/**
	 * @param page
	 * @param entity
	 * @return
	 */
	protected ModelAndView createModelAndView(String page, String entity) {
		ModelAndView mv = new ModelAndView(page);
		PageForward.setPageAttribute(mv);
		
		if (entity != null) {
			EasyMeta entityMeta = new EasyMeta(MetadataHelper.getEntity(entity));
			mv.getModel().put("entityName", entityMeta.getName());
			mv.getModel().put("entityLabel", entityMeta.getLabel());
			mv.getModel().put("icon", entityMeta.getIcon());
		}
		return mv;
	}
}
