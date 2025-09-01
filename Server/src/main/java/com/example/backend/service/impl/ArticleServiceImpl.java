package com.example.backend.service.impl;

import com.example.backend.dto.response.PageResponse;
import com.example.backend.entity.Article;
import com.example.backend.mapper.ArticleMapper;
import com.example.backend.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl implements ArticleService {

    private static final Logger log = LoggerFactory.getLogger(ArticleServiceImpl.class);

    private ArticleMapper articleMapper;

    @Autowired
    public ArticleServiceImpl(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    @Override
    public PageResponse<Article> list(int page, int size, String search) {
        if (page < 1) {
            page = 1;
        }
        if (size < 1) {
            size = 10;
        }
        int offset = (page - 1) * size;
        if (search != null && !search.isEmpty()) {
            String codePoints = search.chars().mapToObj(c -> String.format("U+%04X", c)).collect(Collectors.joining(","));
            log.debug("Article search param raw: [{}], length={}, codePoints=[{}]", search, search.length(), codePoints);
            
            // 测试简化的搜索方法
            try {
                List<Article> testResults = articleMapper.testSearch(search);
                log.debug("Test search results count: {}", testResults.size());
                if (!testResults.isEmpty()) {
                    log.debug("First test result: id={}, title={}", testResults.get(0).getId(), testResults.get(0).getTitle());
                }
            } catch (Exception e) {
                log.error("Test search failed", e);
            }
        } else {
            log.debug("Article list without search, page={}, size={}, offset={}", page, size, offset);
        }
        List<Article> data = articleMapper.list(offset, size, search);
        long total = articleMapper.count(search);
        return new PageResponse<>(data, total, page, size);
    }

    @Override
    public Article getById(Long id, boolean increaseViewCount) {
        if (increaseViewCount) {
            articleMapper.incrementViewCount(id);
        }
        return articleMapper.findById(id);
    }
}


