package com.grabbler.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.grabbler.models.Address;
import java.util.Optional;
// Import annotation
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
// @EnableJpaRepositories(basePackageClasses = AddressRepository.class)
@EntityScan(basePackages = "com.grabbler.models")
public class AddressRepositoryTest {
  @Autowired private TestEntityManager entityManager;

  @Autowired private AddressRepository addressRepository;

  @Test
  public void whenFindByCountryAndCityAndPlzAndStreet_thenReturnAddress() {
    // given
    Address address = new Address();
    address.setCountry("USA");
    address.setCity("New York");
    address.setPostalCode("10001");
    address.setStreet("123 Main St");
    entityManager.persistAndFlush(address);

    Optional<Address> found =
        addressRepository.findByCountryAndCityAndPostalCodeAndStreet(
            "USA", "New York", "10001", "123 Main St");

    // then
    assertThat(found.get()).isNotNull();
    assertThat(found.get().getCity()).isEqualTo(address.getCity());
    assertThat(found.get().getCountry()).isEqualTo(address.getCountry());
  }

  @Test
  public void
      whenFindByCountryAndCityAndPlzAndAddressLineOne_withNonExistentAddress_thenReturnNull() {
    // when
    Optional<Address> found =
        addressRepository.findByCountryAndCityAndPostalCodeAndStreet(
            "Nowhere", "NoCity", "00000", "No Street");

    // then
    assertThat(found).isEmpty();
  }

  @Test
  public void findAddressesByCity() {
    // given
    Address address1 = new Address();
    address1.setCountry("USA");
    address1.setCity("Los Angeles");
    address1.setPostalCode("90001");
    address1.setStreet("456 Sunset Blvd");
    entityManager.persist(address1);

    Address address2 = new Address();
    address2.setCountry("USA");
    address2.setCity("Los Angeles");
    address2.setPostalCode("90002");
    address2.setStreet("789 Hollywood Blvd");
    entityManager.persist(address2);

    entityManager.flush();

    // when
    var addresses = addressRepository.findByCity("Los Angeles");

    // then
    assertThat(addresses).isNotNull();
    assertThat(addresses.size()).isEqualTo(2);
  }
}
