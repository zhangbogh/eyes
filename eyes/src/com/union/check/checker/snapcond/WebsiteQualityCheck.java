package com.union.check.checker.snapcond;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.union.check.checker.SnapCheckCondition;
import com.union.check.obj.BeanProxy;

public class WebsiteQualityCheck implements SnapCheckCondition {

	@Override
	public List<BeanProxy> compare(List<BeanProxy> snaps, List<BeanProxy> dbs) {
		List<BeanProxy> result = new ArrayList<BeanProxy>();

		// 将快照数据入hash,快照中保存的是历史上所有quality为2的网站的tcm
		HashSet<Long> set = new HashSet<Long>();
		for (BeanProxy bp : snaps) {
			set.add(bp.getLong("tcm"));
		}

		// 检查目前数据库中的数据状态
		for (BeanProxy bp : dbs) {
			if ((bp.getInt("quality") < 2) && set.contains(bp.getLong("tcm"))) {
				result.add(bp);
			} else if (bp.getInt("quality") == 2
					&& !set.contains(bp.getLong("tcm"))) {
				set.add(bp.getLong("tcm"));
				snaps.add(bp);
			}
		}

		return result;
	}

}
