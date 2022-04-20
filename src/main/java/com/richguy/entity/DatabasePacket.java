package com.richguy.entity;

import com.zfoo.net.packet.common.TripleLLS;
import com.zfoo.protocol.IPacket;
import com.zfoo.protocol.collection.CollectionUtils;

import java.util.*;

/**
 * @author jaysunxiao
 * @version 3.0
 */
public class DatabasePacket implements IPacket {

    public static final transient short PROTOCOL_ID = 1;

    public static final int LIST_SIZE_LIMIT = 1000;

    // 已经推送过的电报ID
    private List<Long> pushTelegraphIds;


    // 热点板块变化
    private Map<Integer, Integer> oldTopIndustryMap;
    private Map<Integer, Integer> topIndustryMap;

    // 热点词语
    private Map<String, Integer> oldTopWordMap;
    private Map<String, Integer> topWordMap;

    // 热点新闻
    private List<Long> topNewIds;


    private List<TripleLLS> newHotGns;

    public static DatabasePacket valueOf() {
        var packet = new DatabasePacket();
        packet.pushTelegraphIds = new ArrayList<>();
        packet.topNewIds = new ArrayList<>();
        packet.oldTopIndustryMap = new HashMap<>();
        packet.topIndustryMap = new HashMap<>();
        packet.oldTopWordMap = new HashMap<>();
        packet.topWordMap = new HashMap<>();
        packet.newHotGns = new ArrayList<>();
        return packet;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }


    public void addPushTelegraphId(long id) {
        if (CollectionUtils.isEmpty(pushTelegraphIds)) {
            pushTelegraphIds = new ArrayList<>();
        }

        if (pushTelegraphIds.size() >= LIST_SIZE_LIMIT) {
            pushTelegraphIds.remove(0);
        }

        pushTelegraphIds.add(id);
    }

    public void addTopNewsId(long id) {
        if (CollectionUtils.isEmpty(topNewIds)) {
            topNewIds = new ArrayList<>();
        }

        if (topNewIds.size() >= LIST_SIZE_LIMIT) {
            topNewIds.remove(0);
        }

        topNewIds.add(id);
    }


    public void addTopIndustry(int code) {
        if (CollectionUtils.isEmpty(topIndustryMap)) {
            topIndustryMap = new HashMap<>();
        }
        var count = topIndustryMap.computeIfAbsent(code, key -> 0);
        topIndustryMap.put(code, count + 1);
    }


    public void addTopWordMap(String word) {
        if (CollectionUtils.isEmpty(topWordMap)) {
            topWordMap = new HashMap<>();
        }
        var count = topWordMap.computeIfAbsent(word, key -> 0);
        topWordMap.put(word, count + 1);
    }

    public void clearTopIndustryMap() {
        oldTopIndustryMap = topIndustryMap;
        topIndustryMap = Collections.emptyMap();
    }

    public void clearTopWordMap() {
        oldTopWordMap = topWordMap;
        topWordMap = Collections.emptyMap();
    }

    public void addNewGn(long industryId, String name) {
        if (CollectionUtils.isEmpty(newHotGns)) {
            newHotGns = new ArrayList<>();
        }
        newHotGns.add(TripleLLS.valueOf(industryId, 0, name));
    }

    public List<Long> getPushTelegraphIds() {
        return pushTelegraphIds;
    }

    public void setPushTelegraphIds(List<Long> pushTelegraphIds) {
        this.pushTelegraphIds = pushTelegraphIds;
    }

    public List<Long> getTopNewIds() {
        return topNewIds;
    }

    public void setTopNewIds(List<Long> topNewIds) {
        this.topNewIds = topNewIds;
    }

    public Map<Integer, Integer> getTopIndustryMap() {
        return topIndustryMap;
    }

    public void setTopIndustryMap(Map<Integer, Integer> topIndustryMap) {
        this.topIndustryMap = topIndustryMap;
    }

    public Map<String, Integer> getTopWordMap() {
        return topWordMap;
    }

    public void setTopWordMap(Map<String, Integer> topWordMap) {
        this.topWordMap = topWordMap;
    }

    public List<TripleLLS> getNewHotGns() {
        return newHotGns;
    }

    public void setNewHotGns(List<TripleLLS> newHotGns) {
        this.newHotGns = newHotGns;
    }

    public Map<Integer, Integer> getOldTopIndustryMap() {
        return oldTopIndustryMap;
    }

    public void setOldTopIndustryMap(Map<Integer, Integer> oldTopIndustryMap) {
        this.oldTopIndustryMap = oldTopIndustryMap;
    }

    public Map<String, Integer> getOldTopWordMap() {
        return oldTopWordMap;
    }

    public void setOldTopWordMap(Map<String, Integer> oldTopWordMap) {
        this.oldTopWordMap = oldTopWordMap;
    }

}
