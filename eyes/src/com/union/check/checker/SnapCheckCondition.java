package com.union.check.checker;

import java.util.List;

import com.union.check.obj.BeanProxy;

public interface SnapCheckCondition {
	//返回的结果，供界面展示的时候打印用，snaps的数据会被继续序列化到硬盘，dbs的数据会每次从数据库查询
	public List<BeanProxy> compare(List<BeanProxy> snaps, List<BeanProxy> dbs);
}
