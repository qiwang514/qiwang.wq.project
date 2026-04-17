package com.wq.service;

import com.wq.entity.Monster;

import java.util.List;

public interface MonsterService {
    public List<Monster> listMonster();

    public List<Monster> findMonsterByName(String name);


    //增加方法 处理登录
    public boolean login(String name);
}
