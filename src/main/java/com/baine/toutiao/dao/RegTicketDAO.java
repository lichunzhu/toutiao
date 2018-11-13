package com.baine.toutiao.dao;

import com.baine.toutiao.model.RegTicket;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface RegTicketDAO {
    String TABLE_NAME = "reg_ticket";
    String INSERT_FIELDS = " username, password, salt, ticket, expired, status";
    String SELECT_FIELDS = INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{username},#{password},#{salt},#{ticket},#{expired},#{status})"})
    int addRegTicket(RegTicket regTicket);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where ticket=#{ticket}"})
    RegTicket selectByTicket(String ticket);

    @Update({"update ", TABLE_NAME, " set status=#{status} where ticket=#{ticket}"})
    void updateStatus(@Param("ticket") String ticket, @Param("status") int status);
}
