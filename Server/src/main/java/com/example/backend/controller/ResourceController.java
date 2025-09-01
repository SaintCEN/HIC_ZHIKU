package com.example.backend.controller;

import com.example.backend.entity.Resource;
import com.example.backend.service.ResourceService;
import com.example.backend.mapper.ResourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/knowledge")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;
    
    @Autowired
    private ResourceMapper resourceMapper;

    @GetMapping("/resources")
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String search) {
        
        // 处理"全部"分类
        if ("全部".equals(category) || (category != null && category.trim().isEmpty())) {
            category = null;
        }
        if ("全部".equals(type) || (type != null && type.trim().isEmpty())) {
            type = null;
        }
        if (search != null && search.trim().isEmpty()) {
            search = null;
        }
        
        try {
            List<Resource> resources = resourceService.listAll(page, size, category, type, search);
            long total = resourceService.count(category, type, search);
            int totalPages = (int) Math.ceil((double) total / size);

            Map<String, Object> data = new HashMap<>();
            data.put("data", resources);
            data.put("total", total);
            data.put("totalPages", totalPages);
            data.put("currentPage", page);
            data.put("pageSize", size);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 0);
            response.put("message", "OK");
            response.put("data", data);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取资源列表失败");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/test-software")
    public ResponseEntity<Map<String, Object>> testSoftware() {
        try {
            List<Resource> resources = resourceService.listAll(1, 10, "software", null, null);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 0);
            response.put("message", "OK");
            response.put("data", resources);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "测试查询失败");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/test-software-mapper")
    public ResponseEntity<Map<String, Object>> testSoftwareMapper() {
        try {
            List<Resource> resources = resourceMapper.testSoftwareQuery();
            Map<String, Object> response = new HashMap<>();
            response.put("code", 0);
            response.put("message", "OK");
            response.put("data", resources);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "服务器内部错误");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/test-category-param")
    public ResponseEntity<Map<String, Object>> testCategoryParam(@RequestParam String category) {
        try {
            List<Resource> resources = resourceMapper.testCategoryQuery(category);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 0);
            response.put("message", "OK");
            response.put("data", resources);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "服务器内部错误");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/test-count-param")
    public ResponseEntity<Map<String, Object>> testCountParam(@RequestParam String category) {
        try {
            long count = resourceMapper.testCountQuery(category);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 0);
            response.put("data", count);
            response.put("message", "OK");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/test-dynamic-fixed")
    public ResponseEntity<Map<String, Object>> testDynamicFixed(@RequestParam String category) {
        try {
            long count = resourceMapper.testDynamicCountFixed(category);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 0);
            response.put("data", count);
            response.put("message", "OK");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/test-list-simple")
    public ResponseEntity<Map<String, Object>> testListSimple() {
        try {
            List<Resource> resources = resourceMapper.list(0, 5, null, null, null);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 0);
            response.put("data", resources);
            response.put("message", "OK");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/test-db-data")
    public ResponseEntity<Map<String, Object>> testDbData() {
        try {
            List<Map<String, Object>> data = resourceMapper.getDbData();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 0);
            response.put("data", data);
            response.put("message", "OK");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查看数据库数据失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/test-dynamic-count")
    public ResponseEntity<Map<String, Object>> testDynamicCount(@RequestParam String category) {
        try {
            long count = resourceMapper.count(category, null, null);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 0);
            response.put("data", count);
            response.put("message", "OK");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }





    @GetMapping("/resources/{id}")
    public ResponseEntity<Map<String, Object>> getResourceDetail(@PathVariable Long id) {
        try {
            Resource resource = resourceService.getById(id, true);
            if (resource == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 404);
                response.put("message", "资源不存在");
                return ResponseEntity.status(404).body(response);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("code", 0);
            response.put("message", "OK");
            response.put("data", resource);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取资源详情失败");
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/resources/{id}/view")
    public ResponseEntity<Map<String, Object>> incrementView(@PathVariable Long id) {
        try {
            resourceService.getById(id, true);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 0);
            response.put("message", "OK");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "增加浏览次数失败");
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/debug-categories")
    public ResponseEntity<Map<String, Object>> debugCategories() {
        try {
            List<String> categories = resourceMapper.getAllCategories();
            List<Resource> allResources = resourceMapper.getAllResourcesInfo();
            
            System.out.println("=== 所有category值 ===");
            for (String category : categories) {
                System.out.println("Category: '" + category + "', 长度: " + category.length() + ", 字节: " + java.util.Arrays.toString(category.getBytes()));
            }
            
            System.out.println("=== 所有resources信息 ===");
            for (Resource resource : allResources) {
                System.out.println("ID: " + resource.getId() + ", Title: " + resource.getTitle() + ", Category: '" + resource.getCategory() + "', Type: '" + resource.getType() + "'");
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 0);
            response.put("categories", categories);
            response.put("resources", allResources);
            response.put("message", "OK");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}