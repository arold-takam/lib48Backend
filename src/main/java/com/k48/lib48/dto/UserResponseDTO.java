package com.k48.lib48.dto;

import com.k48.lib48.myEnum.Role;

public record UserResponseDTO(String name, String mail, String password, Role roleName) {

}
