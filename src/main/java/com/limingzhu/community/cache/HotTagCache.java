package com.limingzhu.community.cache;

import com.limingzhu.community.dto.HotTagDTO;
import com.limingzhu.community.dto.HotTagNum_QueNum_ComNum;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class HotTagCache {
    private List<HotTagDTO> hots = new ArrayList<>();

    public List<HotTagDTO> getHots() {
        return hots;
    }

    public void setHots(List<HotTagDTO> hots) {
        this.hots = hots;
    }

    public void updateTags(Map<String, HotTagNum_QueNum_ComNum> tags){
        int max = 5;
        //Java自带的小顶堆集合类，可以用来算topN
        PriorityQueue<HotTagDTO> priorityQueue = new PriorityQueue<HotTagDTO>(max);

        tags.forEach((name,priority) ->{
            HotTagDTO hotTagDTO = new HotTagDTO();
            hotTagDTO.setName(name);
            hotTagDTO.setPriority(priority.getHotTagNum());
            hotTagDTO.setCommentNum(priority.getCommentNum());
            hotTagDTO.setQuestionNum(priority.getQuestionNum());
            //因为是要求top3，所以维护一个size为3的小顶堆
            //当size小于3时，直接将对象加入进行维护
            if (priorityQueue.size() < max){
                priorityQueue.add(hotTagDTO);
            }else{
                //当size大于3时，若新加入的值大于小顶堆的堆顶，就将新值和小顶堆替换，然后重新维护这个小顶堆
                //若新加入的值小于小顶堆的堆顶就舍弃到
                HotTagDTO minHot = priorityQueue.peek(); //获取堆顶的值
                if (hotTagDTO.compareTo(minHot) > 0){
                    //弹出堆顶值
                    priorityQueue.poll();
                    priorityQueue.add(hotTagDTO); //将新的hotTagDTO加入集合，并重新维护该小顶堆
                }
            }
        });

        List<HotTagDTO> sortedTags = new ArrayList<>();

        HotTagDTO pollTag = priorityQueue.poll();
        while(pollTag != null){
            sortedTags.add(0,pollTag);
            pollTag = priorityQueue.poll();
        }
        hots = sortedTags;
    }
}
