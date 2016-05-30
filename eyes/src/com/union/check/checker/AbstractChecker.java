package com.union.check.checker;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.union.check.engine.CheckEngine;
import com.union.check.exception.ShowNameNotMatchException;
import com.union.check.gen.Basenode;
import com.union.check.obj.BeanProxy;

public abstract class AbstractChecker implements Checker {
	List<BeanProxy> datas;
	Basenode bnode;
	Comparator<BeanProxy> comp;

	public AbstractChecker(Basenode node) {
		this.bnode = node;
		this.comp = new Comparator<BeanProxy>() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public int compare(BeanProxy o1, BeanProxy o2) {
				List<String> l = o1.getCols();
				for (String s : l) {
					Comparable s1 = (Comparable) o1.getValue(s);
					Comparable s2 = (Comparable) o1.getValue(s);
					int v = s1.compareTo(s2);
					if (v != 0) {
						return v;
					}
				}
				return 0;
			}
		};

	}

	protected void outputResult() {
		if (datas.size() > 0) {
			CheckEngine.addMail(bnode.getMailid(), toHtml());
		} else {
			CheckEngine.addOks(getName());
		}
	}

	public String getName() {
		return bnode.getName();
	}

	private String toHtml() {
		BeanProxy bp = datas.get(0);
		String[] shows = bnode.getShownames().split(",");
		List<String> cols = bp.getCols();
		if (shows.length != cols.size()) {
			throw new ShowNameNotMatchException();
		}

		StringBuffer sb = new StringBuffer(1000);
		sb.append("<br/><table>");
		sb.append("<tr><th colspan=\"" + cols.size() + "\">")
				.append(bnode.getId()).append(":").append(getName())
				.append("</th></tr>");
		sb.append("<tr><th colspan=\"" + cols.size() + "\">")
				.append(bnode.getError()).append("</th></tr>");
		sb.append("<tr>");
		for (String s : shows) {
			sb.append("<th>").append(s).append("</th>");
		}
		sb.append("</tr>");
		for (BeanProxy t : datas) {
			sb.append("<tr>");
			for (String s : cols) {
				sb.append("<td>")
						.append(t.getValue(s) == null ? "" : t.getValue(s)
								.toString()).append("</td>");
			}
			sb.append("</tr>");
		}
		sb.append("</table>");

		return sb.toString();
	}

	protected void sortAll() {
		Collections.sort(datas, comp);
	}
}
