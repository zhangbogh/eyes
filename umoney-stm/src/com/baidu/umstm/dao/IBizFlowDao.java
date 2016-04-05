package com.baidu.umstm.dao;

import com.baidu.umstm.model.BizFlow;

import org.apache.ibatis.annotations.Update;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface IBizFlowDao {
    @Select("select * from BizFlow where id=#{id}")
    public BizFlow queryBizFlowById(long id);

    @Select("select * from BizFlow where bid=#{bid} and flowid=#{flowid}")
    public BizFlow queryBizFlowByBidFlowId(@Param("bid") int bid, @Param("flowid") int flowid);

    @Insert("INSERT INTO BizFlow(bid,type,flowid,curstatus,curstep,finalstatus) VALUES (#{bid},#{type},#{flowid},#{curstatus},#{curstep},#{finalstatus})")
    public void insertBizFlow(BizFlow bf);

    @Update("update BizFlow set bid=#{bid},type=#{type},flowid=#{flowid},curstatus=#{curstatus},curstep=#{curstep},finalstatus=#{finalstatus} where id=#{id}")
    public void updateBizFlow(BizFlow bf);
}
