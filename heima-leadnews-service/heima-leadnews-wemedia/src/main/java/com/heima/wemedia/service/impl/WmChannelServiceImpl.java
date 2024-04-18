package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.admin.beans.AdChannel;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.service.WmChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
@Slf4j
public class WmChannelServiceImpl extends ServiceImpl<WmChannelMapper, WmChannel> implements WmChannelService {
    @Autowired
    private WmChannelMapper channelMapper;


    /**
     * 查询所有频道
     *
     * @return
     */
    @Override
    public ResponseResult findAll() {
        List<WmChannel> channelList = channelMapper.selectList(null);
        return ResponseResult.okResult(channelList);
    }

    /**
     * 新增频道
     *
     * @param adChannel
     * @return
     */
    @Override
    public ResponseResult saveChannel(AdChannel adChannel) {
        //1. 校验参数
        if (adChannel == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2. 保存频道
        WmChannel wmChannel = new WmChannel();
        BeanUtils.copyProperties(adChannel, wmChannel);
        if (wmChannel.getIsDefault() == null) {
            wmChannel.setIsDefault(true);
        }
        if (wmChannel.getOrd() == null) {
            wmChannel.setOrd(1);
        }
        if (wmChannel.getCreatedTime() == null) {
            wmChannel.setCreatedTime(new Date());
        }
        List<WmChannel> channelList = channelMapper.selectList(null);
        //3. 判断频道是否重复
        if (channelList != null && !channelList.isEmpty()) {
            for (WmChannel channel : channelList) {
                if (channel.getName().equals(wmChannel.getName())) {
                    return ResponseResult.errorResult(AppHttpCodeEnum.DATA_EXIST, "频道已存在");
                }
            }
        }
        //4. 保存频道
        channelMapper.insert(wmChannel);
        return ResponseResult.okResult(wmChannel);
    }
}