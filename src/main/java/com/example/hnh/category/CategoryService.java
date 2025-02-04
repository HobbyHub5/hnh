package com.example.hnh.category;

import com.example.hnh.category.dto.CategoryResponseDto;
import com.example.hnh.category.dto.CreateCategoryRequestDto;
import com.example.hnh.global.error.errorcode.ErrorCode;
import com.example.hnh.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 카테고리 생성 로직
     *
     * @param createCategoryRequestDto 카테고리 생성 정보
     * @return
     */
    @Transactional
    public CategoryResponseDto createCategory(CreateCategoryRequestDto createCategoryRequestDto) {

        //카테고리 중복 검증
        Optional<Category> findCategory = categoryRepository.findByName(createCategoryRequestDto.getName());

        if (findCategory.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
        }

        //카테고리 생성, 저장
        Category savedCategory = categoryRepository.save(new Category(createCategoryRequestDto.getName()));

        return new CategoryResponseDto(savedCategory);
    }
}
