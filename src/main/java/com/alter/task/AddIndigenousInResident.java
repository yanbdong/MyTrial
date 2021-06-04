package com.alter.task;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.alter.dao.rec.entity.Resident;
import com.alter.dao.rec.mapper.ResidentMapper;
import com.alter.dao.rec.service.IResidentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;

/**
 * 为resident表增加原著民字段
 *
 * @author yanbodong
 * @date 2021/05/12 11:09
 **/
@Slf4j
@Component
public class AddIndigenousInResident implements ApplicationListener<ApplicationReadyEvent>,
    Runnable {

    @Autowired
    private IResidentService residentService;

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
//        run();
    }

    @Override
    @Transactional
    public void run() {
        ((ResidentMapper) this.residentService.getBaseMapper()).addIndigenous();
        Long limit = 20L;
        // Query the territory whose resident count greater than 256
        QueryWrapper<Resident> queryCount = Wrappers.<Resident>query().select(
            "territory_id AS territoryId", "count( uid ) as co")
            .groupBy("territoryId")
            .having("co > {0}", limit);
        List<Map<String, Object>> territoryResidentCount =
            this.residentService.listMaps(queryCount);
        // Query all the normal residents from given territory
        List<String> normalResidentId = territoryResidentCount.stream().flatMap(it -> {
            String territoryId = (String) it.get("territoryId");
            Long co = (Long) it.get("co");
            LambdaQueryWrapper<Resident> queryNormalResident = Wrappers.<Resident>lambdaQuery()
                .select(Resident::getId)
                .eq(Resident::getTerritoryId, territoryId)
                .orderByAsc(Resident::getJoinTime)
                .last(String.format("limit %d, %d", limit, co - limit));
            List<String> ids = this.residentService
                .listObjs(queryNormalResident, Objects::toString);
            return ids.stream();
        }).collect(Collectors.toList());
        log.info("{}", normalResidentId);
        normalResidentId.forEach(
            it -> {
                LambdaUpdateWrapper<Resident> updateIndigenous = Wrappers.<Resident>lambdaUpdate()
                    .set(Resident::getIdentityCategory, 2)
                    .eq(Resident::getId, it);
                this.residentService.update(updateIndigenous);
            }
        );
    }
}
