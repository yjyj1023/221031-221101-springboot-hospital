package com.example.hello.dao;


import com.example.hello.domain.user.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@Repository
public class UserDao {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public UserDao(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    RowMapper<User> rowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User(rs.getString("id"), rs.getString("name"),
                    rs.getString("password"));
            return user;
        }
    };

    public void add(final User user){
        //jdbcTemplate.update()의 경우 두번째부터 파라메터 개수 만큼 ?자리에 값을 넘길 수 있다.
        this.jdbcTemplate.update("insert into users(id, name, password) values (?, ?, ?);",
                user.getId(), user.getName(), user.getPassword());
    }

    public User get(String id){
        String sql = "select * from users where id = ?";
        return this.jdbcTemplate.queryForObject(sql, rowMapper, id);
    }

    public void deleteAll(){
        this.jdbcTemplate.update("delete from users");
    }

    public int getCount(){
        //queryForObject에 두번째 파라메터로 Integer.class를 넘겨줌으로써 int형의 데이터를 받아온다.
        return this.jdbcTemplate.queryForObject("select count(*) from users;", Integer.class);
    }

    public List<User> getAll() {
        String sql = "select * from users order by id";
        return this.jdbcTemplate.query(sql, rowMapper);
    }

}