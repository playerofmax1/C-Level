package com.clevel.kudu.util;

import com.clevel.kudu.model.LookupList;
import com.clevel.kudu.model.StrLookupList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LookupUtil {
	private static final Logger log = LoggerFactory.getLogger(LookupUtil.class);

	public static <T extends LookupList> T getObjById(List<T> list, long id) {
		for (T t:list) {
			if (t.getId()==id) {
				return t;
			}
		}
		return null;
	}

	public static <T extends StrLookupList> T getObjById(List<T> list, String id) {
		for (T t:list) {
			if (id.trim().equalsIgnoreCase(t.getId())) {
				return t;
			}
		}
		return null;
	}
}
