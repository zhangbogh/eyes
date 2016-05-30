package com.union.check.obj;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.union.check.gen.DbPort;

public class DataBase {
	DataSource source;

	public DataBase(DbPort port) {
		final ComboPooledDataSource cpds = new ComboPooledDataSource();
		try {
			cpds.setDriverClass("com.mysql.jdbc.Driver");
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		cpds.setJdbcUrl(port.getUrl());
		cpds.setUser(port.getName());
		cpds.setPassword(port.getPassword());
		cpds.setMinPoolSize(3);
		cpds.setMaxPoolSize(3);
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				cpds.close();
			}
		}));
		this.source = cpds;
	}

	public List<BeanProxy> query(String sql) {
		List<BeanProxy> result = new ArrayList<BeanProxy>();
		Connection conn = null;
		try {
			conn = source.getConnection();
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			ResultSetMetaData meta = rs.getMetaData();
			int count = meta.getColumnCount();
			String[] colNames = new String[count];
			for(int i =0;i<count;i++){
				colNames[i]=meta.getColumnLabel(i+1);
			}
			while (rs.next()) {
				BeanProxy bp = new BeanProxy();
				for (int i = 0; i < colNames.length; i++) {
					bp.setValue(colNames[i], rs.getObject(i+1));
				}
				result.add(bp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return result;
	}
}
