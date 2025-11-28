package com.grabbler.payloads.user;

import com.grabbler.models.Role;
import com.grabbler.payloads.address.AddressDTO;
import com.grabbler.payloads.cart.CartDTO;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
  private Long userId;
  private String firstName;
  private String lastName;
  private String mobileNumber;
  private String email;
  private Set<Role> roles = new HashSet<>();
  private AddressDTO address;
  private CartDTO cart;
}
