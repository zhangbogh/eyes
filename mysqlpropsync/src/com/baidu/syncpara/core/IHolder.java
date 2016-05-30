package com.baidu.syncpara.core;

import com.baidu.syncpara.cfg.SyncParaRow;

public interface IHolder {
    public void addRow(SyncParaRow row);

    public int getIntValue(int kid);

    public String getStringValue(int kid);

    public long getLongValue(int kid);

    public String getTemplateValue(int kid, Object... objs);
}
