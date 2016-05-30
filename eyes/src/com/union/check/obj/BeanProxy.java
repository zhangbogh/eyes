package com.union.check.obj;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class BeanProxy {
	LinkedHashMap<String, Object> container = new LinkedHashMap<String, Object>();

	final int type_int = 0;
	final int type_double = 1;
	final int type_long = 2;
	final int type_string = 3;
	String split = ",";
	String connect = "#";

	public Object getValue(String key) {
		return container.get(key);
	}

	public void setValue(String key, Object value) {
		container.put(key, value);
	}

	public List<String> getCols() {
		List<String> result = new ArrayList<String>();
		if (container.size() > 0) {
			Set<String> keys = container.keySet();
			for (String k : keys) {
				result.add(k);
			}
		}
		return result;
	}

	public int getInt(String key) {
		return Integer.valueOf(container.get(key).toString());
	}

	public double getDouble(String key) {
		return Double.valueOf(container.get(key).toString());
	}

	public long getLong(String key) {
		return (Long) container.get(key);
	}

	public String getString(String key) {
		return container.get(key).toString();
	}

	// 格式：type^key^value,type^key^value
	public String toCsv() {
		Set<String> keys = container.keySet();
		StringBuffer sb = new StringBuffer();
		for (String k : keys) {
			Object v = container.get(k);
			if (v instanceof Integer) {
				sb.append(type_int);
			} else if (v instanceof Double) {
				sb.append(type_double);
			} else if (v instanceof Long) {
				sb.append(type_long);
			} else if (v instanceof String) {
				sb.append(type_string);
			}
			sb.append(connect).append(k).append(connect).append(v)
					.append(split);
		}
		return sb.substring(0, sb.length() - 1);
	}

	public void fromCsv(String s) {
		String[] ss = s.split(split);
		for (String t : ss) {
			String[] ts = t.split(connect);
			int type = Integer.valueOf(ts[0]);
			switch (type) {
			case type_int:
				container.put(ts[1], Integer.valueOf(ts[2]));
				break;
			case type_double:
				container.put(ts[1], Double.valueOf(ts[2]));
				break;
			case type_long:
				container.put(ts[1], Long.valueOf(ts[2]));
				break;
			case type_string:
				container.put(ts[1], ts[2]);
				break;
			default:
				break;
			}
		}
	}
}
