package com.union.check.checker;

import com.union.check.engine.CheckEngine;
import com.union.check.gen.BasicdbCheck;
import com.union.check.obj.DataBase;

public class BasicdbChecker extends AbstractChecker {
	BasicdbCheck node;

	public BasicdbChecker(BasicdbCheck node) {
		super(node);
		this.node = node;
	}

	@Override
	public void check() {
		DataBase db = (DataBase) CheckEngine.getObjById(node.getBdid());
		datas = db.query(node.getSql());
		outputResult();
	}
}
