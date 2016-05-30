package com.baidu.binlog.model;

import java.util.List;

public class Configure {
    // dump部分
    DataBase src;

    DumpCtrl dumpctrl;

    List<TableDump> tabledumps;

    // recv部分
    DataBase dest;
    RecvCtrl recvctrl;
    List<TableRecv> tablerecvs;

    public List<TableRecv> getTablerecvs() {
        return tablerecvs;
    }

    public void setTablerecvs(List<TableRecv> tablerecvs) {
        this.tablerecvs = tablerecvs;
    }

    public DataBase getDest() {
        return dest;
    }

    public void setDest(DataBase dest) {
        this.dest = dest;
    }

    public RecvCtrl getRecvctrl() {
        return recvctrl;
    }

    public void setRecvctrl(RecvCtrl recvctrl) {
        this.recvctrl = recvctrl;
    }

    public DumpCtrl getDumpctrl() {
        return dumpctrl;
    }

    public void setDumpctrl(DumpCtrl dumpctrl) {
        this.dumpctrl = dumpctrl;
    }

    public List<TableDump> getTabledumps() {
        return tabledumps;
    }

    public void setTabledumps(List<TableDump> tabledumps) {
        this.tabledumps = tabledumps;
    }

    public DataBase getSrc() {
        return src;
    }

    public void setSrc(DataBase src) {
        this.src = src;
    }
}
