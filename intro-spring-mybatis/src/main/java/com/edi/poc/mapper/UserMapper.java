package com.edi.poc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.edi.poc.domain.User;

public interface UserMapper {

	@Insert("insert into tb_user(name,password,sex,age) values(#{name},#{password},#{sex},#{age})")
	public void saveUser(User user);
	
	@Update("update tb_user set name=#{name},password=#{password},sex=#{sex},age=#{age} where name=#{name}")
	public void updateUser(User user);
	
	@Delete("delete from tb_user where name=#{name}")
	public void deleteUser(User user);
	
	@Select("select * from tb_user order by name asc")
	public List<User> getAllUsers();
}
