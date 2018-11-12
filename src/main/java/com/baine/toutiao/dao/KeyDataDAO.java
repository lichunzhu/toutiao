package com.baine.toutiao.dao;

import com.baine.toutiao.model.KeyData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface KeyDataDAO {
    String TABLE_NAME = "key_data";
    String SELECT_FIELDS = " mail_username, mail_password, qiniu_accessKey, qiniu_secretKey ";

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " limit 1"})
    KeyData getKeyData();
}
