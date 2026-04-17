package com.wq.service.impl;

import com.wq.entity.Monster;
import com.wq.service.MonsterService;
import com.wq.wqspringmvc.annotation.Service;

import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

@Service
public class MonsterServiceImpl implements MonsterService {
    @Override
    public List<Monster> listMonster() {
        List<Monster> monsters
                = new ArrayList<>();
        monsters.add(new Monster(100,"JLU","9",60));
        monsters.add(new Monster(200,"JLAU","2",70));
        return monsters;
    }

    @Override
    public List<Monster> findMonsterByName(String name) {
        List<Monster> monsters
                = new ArrayList<>();
        monsters.add(new Monster(100,"JLU","9",60));
        monsters.add(new Monster(200,"JLAU1","1",71));
        monsters.add(new Monster(300,"JLAU2","2",72));
        monsters.add(new Monster(400,"JLAU3","3",73));
        monsters.add(new Monster(500,"JLAU4","4",74));
        List<Monster> findMonsters
                = new ArrayList<>();
        //遍历monsters集合 返回满足条件的

        for (Monster monster : monsters) {
            if (monster.getName().contains(name)) {
                findMonsters.add(monster);
            }
        }

        return  findMonsters;
    }

    @Override
    public boolean login(String name) {
        if ("JLAU1".equals(name)) {
            return true;
        }else {

            return false;
        }
    }
}
