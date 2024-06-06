package com.example.findPartnerbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.findPartnerbackend.mapper.TagMapper;
import com.example.findPartnerbackend.model.domain.Tag;
import com.example.findPartnerbackend.service.TagService;
import org.springframework.stereotype.Service;

/**
* @author 28044
* @description 针对表【tag(标签)】的数据库操作Service实现
* @createDate 2024-06-06 16:42:03
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService {

}




