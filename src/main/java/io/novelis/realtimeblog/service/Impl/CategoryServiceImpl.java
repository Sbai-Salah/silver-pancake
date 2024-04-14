package io.novelis.realtimeblog.service.Impl;
import io.novelis.realtimeblog.domain.Category;
import io.novelis.realtimeblog.exception.ResourceNotFoundException;
import io.novelis.realtimeblog.payload.CategoryDto;
import io.novelis.realtimeblog.repository.CategoryDao;
import io.novelis.realtimeblog.repository.CategoryRepository;
import io.novelis.realtimeblog.service.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

//    private CategoryRepository categoryRepository;
//    private ModelMapper modelMapper;
//
//    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
//        this.categoryRepository = categoryRepository;
//        this.modelMapper = modelMapper;
//    }
//
//    @Override
//    public CategoryDto addCategory(CategoryDto categoryDto) {
//        Category category = modelMapper.map(categoryDto, Category.class);
//        Category savedCategory = categoryRepository.save(category);
//        return modelMapper.map(savedCategory, CategoryDto.class);
//    }
//
//    @Override
//    public CategoryDto getCategory(Long categoryId) {
//
//        Category category = categoryRepository.findById(categoryId)
//                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
//
//        return modelMapper.map(category, CategoryDto.class);
//    }
//
//    @Override
//    public List<CategoryDto> getAllCategories() {
//
//        List<Category> categories = categoryRepository.findAll();
//
//        return categories.stream().map((category) -> modelMapper.map(category, CategoryDto.class))
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId) {
//
//        Category category = categoryRepository.findById(categoryId)
//                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
//
//        category.setName(categoryDto.getName());
//        category.setDescription(categoryDto.getDescription());
//        category.setId(categoryId);
//
//        Category updatedCategory = categoryRepository.save(category);
//
//        return modelMapper.map(updatedCategory, CategoryDto.class);
//    }
//
//    @Override
//    public void deleteCategory(Long categoryId) {
//
//        Category category = categoryRepository.findById(categoryId)
//                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
//
//        categoryRepository.delete(category);
//    }


    private final CategoryDao categoryDao;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryDao categoryDao, ModelMapper modelMapper) {
        this.categoryDao = categoryDao;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public CategoryDto addCategory(CategoryDto categoryDto) {
        Category category = modelMapper.map(categoryDto, Category.class);
        Category savedCategory = categoryDao.save(category);
        return modelMapper.map(savedCategory, CategoryDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategory(Long categoryId) {
        Category category = categoryDao.getById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryDao.findAll();
        return categories.stream().map((category) -> modelMapper.map(category, CategoryDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId) {
        Category category = categoryDao.getById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        category.setId(categoryId);
        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryDao.getById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        categoryDao.delete(category);
    }
}