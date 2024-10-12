package com.grabbler.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.grabbler.models.Role;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long userId;
    private String firstName;
	private String lastName;
	private String mobileNumber;
	private String email;
	private String password;
	private Set<Role> roles = new HashSet<>();
	private AddressDTO address;
	private CartDTO cart;
}
