package com.blogProject.common.member.converter;

import com.blogProject.common.member.entity.Role;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * 유저의 권한을 컨버팅해주는 컨버터입니다. Role 타입으로 입력받은 값을 String으로 변환 및 String으로 입력 받은 값을 Role 타입으로 변환해줍니다.
 */
@Converter
public class RoleTypeConverter implements AttributeConverter<Role, String> {

  @Override
  public String convertToDatabaseColumn(Role attribute) {
    if (attribute == null) {
      return null;
    }
    return attribute.getKey();
  }

  @Override
  public Role convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }
    return Role.fromKey(dbData);
  }
}
