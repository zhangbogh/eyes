package com.union.check.checker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.union.check.engine.CheckEngine;
import com.union.check.exception.FileIoException;
import com.union.check.gen.SnapdbCheck;
import com.union.check.obj.BeanProxy;
import com.union.check.obj.DataBase;

public class SnapdbChecker extends AbstractChecker {
	private SnapdbCheck node;

	public SnapdbChecker(SnapdbCheck node) {
		super(node);
		this.node = node;
	}

	@Override
	public void check() {
		try {
			Class<?> clz = Class.forName(node.getCheckconditionclass());
			SnapCheckCondition scc = (SnapCheckCondition) clz.newInstance();

			File f = new File(node.getFilepath());
			if (!f.exists()) {
				f.createNewFile();
			}

			// 从文件恢复snap
			List<BeanProxy> snaps = readListFromFile(f);
			DataBase db = (DataBase) CheckEngine.getObjById(node.getBdid());
			List<BeanProxy> dbs = db.query(node.getSql());
			datas = scc.compare(snaps, dbs);

			// snaps会重新落回文件
			writeListToFile(f, snaps);

			outputResult();
		} catch (Exception e) {
			e.printStackTrace();
			throw new FileIoException();
		}
	}

	private void writeListToFile(File f, List<BeanProxy> snaps) {
		BufferedWriter bw = null;
		try {
			if (f.exists()) {
				f.delete();
				f.createNewFile();
			}

			bw = new BufferedWriter(new FileWriter(f));
			for (BeanProxy bp : snaps) {
				bw.write(bp.toCsv());
				bw.write("\n");
			}
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
			e.printStackTrace();
			throw new FileIoException();
		} finally {
			if (bw != null)
				try {
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

	}

	private List<BeanProxy> readListFromFile(File f) {
		List<BeanProxy> result = new ArrayList<BeanProxy>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(f));
			String t;
			while ((t = br.readLine()) != null) {
				BeanProxy bp = new BeanProxy();
				bp.fromCsv(t);
				result.add(bp);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new FileIoException();
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return result;
	}
}
