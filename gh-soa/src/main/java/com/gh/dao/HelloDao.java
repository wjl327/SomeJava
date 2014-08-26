package com.gh.dao;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class HelloDao {
	
	@Autowired
	protected NamedParameterJdbcTemplate jdbcTemplate;
	
	public boolean register(HelloDTO hello){
		String sql = "insert into hello(id) values(:id)";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", getId());
		int rowNum = jdbcTemplate.update(sql, paramMap);
		return rowNum > 0;
	}
	
	public int getId(){
		String sql = "select seq_leaguer.nextval from dual";
		return jdbcTemplate.queryForInt(sql, new HashMap<String, Object>());
	}

}
