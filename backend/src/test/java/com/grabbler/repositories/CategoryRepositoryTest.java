package com.grabbler.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.grabbler.models.Category;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@EntityScan(basePackages = "com.grabbler.models")
public class CategoryRepositoryTest {

  @Autowired private TestEntityManager entityManager;

  @Autowired private CategoryRepository categoryRepository;

  @Test
  public void whenFindByCategoryName_thenReturnCategory() {
    // given
    Category category = new Category();
    category.setCategoryName("Electronics");
    entityManager.persistAndFlush(category);

    // when
    Optional<Category> found = categoryRepository.findByCategoryName("Electronics");

    Category foundCategory = found.orElse(null);
    // then
    assertThat(foundCategory).isNotNull();
    assertThat(foundCategory.getCategoryName()).isEqualTo(category.getCategoryName());
  }

  @Test
  public void whenFindByCategoryName_withNonExistentName_thenReturnNull() {
    // when
    Optional<Category> found = categoryRepository.findByCategoryName("NonExistent");

    // then
    assertThat(found).isEmpty();
  }
}
