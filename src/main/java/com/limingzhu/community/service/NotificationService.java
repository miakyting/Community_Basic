package com.limingzhu.community.service;

import com.limingzhu.community.dto.NotificationDTO;
import com.limingzhu.community.dto.PaginationDTO;
import com.limingzhu.community.enums.NotificationStatusEnum;
import com.limingzhu.community.enums.NotificationTypeEnum;
import com.limingzhu.community.exception.CustomizeErrorCode;
import com.limingzhu.community.exception.CustomizeException;
import com.limingzhu.community.mapper.NotificationMapper;
import com.limingzhu.community.model.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    /**
     * 查询当前user的所有通知内容
     * @param userId
     * @param page
     * @param size
     * @return
     */
    public PaginationDTO list(Integer userId, Integer page, Integer size) {

        //查询需要通知的内容的totalCount
        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andReceiverEqualTo(userId);
        notificationExample.setOrderByClause("gmt_create desc");
        Integer totalCount = notificationMapper.countByExample(notificationExample);
        paginationDTO.setPagination(totalCount, page, size);

        page = paginationDTO.getPage();
        Integer offset = size * (page - 1);

        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(notificationExample, new RowBounds(offset, size));

        if (notifications.size() == 0) {
            return paginationDTO;
        }

        List<NotificationDTO> notificationDTOSList = new ArrayList<>();
        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification, notificationDTO);
            notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
            notificationDTOSList.add(notificationDTO);
        }

        paginationDTO.setData(notificationDTOSList);
        return paginationDTO;
    }

    /**
     * 查询当前用户的未读通知数
     * @param id
     * @return
     */
    public int unreadCount(Integer id) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andReceiverEqualTo(id).andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        int counts = notificationMapper.countByExample(notificationExample);
        return counts;
    }

    /**
     *在profile读取消息内容，并将此消息的状态置为已读
     * @param id
     * @param user
     * @return
     */
    public NotificationDTO read(Integer id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if (notification == null) {
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if (!Objects.equals(notification.getReceiver(), user.getId())) {
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }

        //当点击了信息，就会将这条notification置为已读，并返回notificationDTO返回到页面
        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);

        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification,notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));

        return notificationDTO;
    }
}
