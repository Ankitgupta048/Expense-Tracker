package com.expensetracker.config;

import com.expensetracker.entity.Category;
import com.expensetracker.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final String[] DEFAULT_CATEGORIES = {
            "Food", "Transport", "Entertainment", "Bills", "Shopping", "Health", "Education", "Other"
    };

    private final CategoryRepository categoryRepository;

    public DataInitializer(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        for (String name : DEFAULT_CATEGORIES) {
            if (categoryRepository.findByNameIgnoreCase(name).isEmpty()) {
                Category c = new Category();
                c.setName(name);
                categoryRepository.save(c);
            }
        }
    }
}
