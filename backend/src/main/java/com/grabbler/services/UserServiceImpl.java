package com.grabbler.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.grabbler.exceptions.APIException;
import com.grabbler.exceptions.ResourceNotFoundException;
import com.grabbler.models.Address;
import com.grabbler.models.Cart;
import com.grabbler.models.CartItem;
import com.grabbler.models.Role;
import com.grabbler.models.User;
import com.grabbler.payloads.AddressDTO;
import com.grabbler.payloads.CartDTO;
import com.grabbler.payloads.ProductDTO;
import com.grabbler.payloads.UserDTO;
import com.grabbler.payloads.UserResponse;
import com.grabbler.repositories.AddressRepository;
import com.grabbler.repositories.RoleRepository;
import com.grabbler.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ModelMapper modelMapper;

    // @Autowired
    // private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDTO registerUser(UserDTO userDTO) {
        try {
            User user = modelMapper.map(userDTO, User.class);

            Cart cart = new Cart();
            user.setCart(cart);

            Role role = roleRepository.findById(102L).get();
            user.getRoles().add(role);

            String country = userDTO.getAddress().getCountry();
            String city = userDTO.getAddress().getCity();
            String plz = userDTO.getAddress().getPlz();
            String addressLineOne = userDTO.getAddress().getAddressLineOne();

            Address address = addressRepository.findByCountryAndCityAndPlzAndAddressLineOne(country, city, plz,
                    addressLineOne);

            if (address == null) {
                address = new Address();
                address.setCountry(country);
                address.setCity(city);
                address.setPlz(plz);
                address.setAddressLineOne(addressLineOne);

                address = addressRepository.save(address);
            }

            user.setAddresses(List.of(address));

            User registeredUser = userRepository.save(user);
            cart.setUser(registeredUser);

            userDTO = modelMapper.map(registeredUser, UserDTO.class);
            userDTO.setAddress(
                    modelMapper.map(user.getAddresses().stream().findFirst().get(), AddressDTO.class));

            return userDTO;
        } catch (DataIntegrityViolationException e) {
            throw new APIException("User already exists");
        }
    }

    @Override
    public UserResponse getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageDetails = PageRequest.of(pageNumber, pageSize, sort);

        Page<User> pageUsers = userRepository.findAll(pageDetails);

        List<User> users = pageUsers.getContent();

        if (users.isEmpty()) {
            throw new APIException("No users found");
        }

        List<UserDTO> userDTOs = users.stream().map(user -> {
            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            if (!user.getAddresses().isEmpty()) {
                userDTO.setAddress(modelMapper.map(user.getAddresses().stream().findFirst().get(), AddressDTO.class));
            }
            CartDTO cartDTO = modelMapper.map(user.getCart(), CartDTO.class);
            List<ProductDTO> productDTOs = user.getCart().getCartItems().stream().map(cartItem -> {
                ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                return productDTO;
            }).collect(Collectors.toList());

            userDTO.setCart(cartDTO);
            userDTO.getCart().setProducts(productDTOs);

            return userDTO;
        }).collect(Collectors.toList());

        UserResponse userResponse = new UserResponse();
        userResponse.setContent(userDTOs);
        userResponse.setTotalPages(pageUsers.getTotalPages());
        userResponse.setTotalElements(pageUsers.getTotalElements());
        userResponse.setPageNumber(pageUsers.getNumber());
        userResponse.setPageSize(pageUsers.getSize());
        userResponse.setLastPage(pageUsers.isLast());

        return userResponse;
    }

    @Override
    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        userDTO.setAddress(modelMapper.map(user.getAddresses().stream().findFirst().get(), AddressDTO.class));

        CartDTO cartDTO = modelMapper.map(user.getCart(), CartDTO.class);

        List<ProductDTO> productDTOs = user.getCart().getCartItems().stream().map(cartItem -> {
            ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
            return productDTO;
        }).collect(Collectors.toList());

        userDTO.setCart(cartDTO);
        userDTO.getCart().setProducts(productDTOs);
        return userDTO;
    }

    @Override
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        // String encodedPassword = user.getPassword();

        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setMobileNumber(user.getMobileNumber());
        // user.setPassword(encodedPassword);

        if (userDTO.getAddress() != null) {
            String country = userDTO.getAddress().getCountry();
            String city = userDTO.getAddress().getCity();
            String plz = userDTO.getAddress().getPlz();
            String addressLineOne = userDTO.getAddress().getAddressLineOne();

            Address address = addressRepository.findByCountryAndCityAndPlzAndAddressLineOne(country, city, plz,
                    addressLineOne);

            if (address == null) {
                address = new Address();
                address.setCountry(country);
                address.setCity(city);
                address.setPlz(plz);
                address.setAddressLineOne(addressLineOne);

                address = addressRepository.save(address);
            }

            user.setAddresses(List.of(address));
        }

        userDTO = modelMapper.map(user, UserDTO.class);
        userDTO.setAddress(modelMapper.map(user.getAddresses().stream().findFirst().get(), AddressDTO.class));

        CartDTO cart = modelMapper.map(user.getCart(), CartDTO.class);

        List<ProductDTO> productDTOs = user.getCart().getCartItems().stream().map(cartItem -> {
            ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
            return productDTO;
        }).collect(Collectors.toList());

        userDTO.setCart(cart);
        userDTO.getCart().setProducts(productDTOs);
        return userDTO;

    }

    @Override
    @Transactional
    public String deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        List<CartItem> cartItems = user.getCart().getCartItems();
        Long cartId = user.getCart().getCartId();

        cartItems.forEach(cartItem -> {
            Long productId = cartItem.getProduct().getProductId();
            cartService.deleteProductFromCart(cartId, productId);
        });
        userRepository.delete(user);

        return "User deleted successfully";
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findUserById(Long userId) {
        return userRepository.findById(userId);
    }

}
